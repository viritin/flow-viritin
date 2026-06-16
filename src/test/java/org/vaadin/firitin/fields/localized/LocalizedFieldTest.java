package org.vaadin.firitin.fields.localized;

import com.vaadin.browserless.BrowserlessUIContext;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Browserless tests for {@link LocalizedField} (via {@link LocalizedTextField}).
 * The component is a composition over a language selector ({@code Tabs} or
 * {@code ComboBox}) and per-language editors, so these tests focus on the
 * component's own behavior — value presentation/collection, language switching,
 * the selector mode and the translate action — rather than the (theme-only)
 * rendering, which is best verified visually.
 * <p>
 * Uses the Playwright-like browserless locator API: a route-free single
 * component is attached with {@link BrowserlessUIContext#forComponent}, and the
 * inner editors/selector/button are driven through {@code findTextField()},
 * {@code findTabs()}, {@code findComboBox()} and {@code findButton()}. The open
 * language is set explicitly where it matters, since by default it follows the
 * user's locale.
 */
class LocalizedFieldTest {

    static final Locale EN = Locale.ENGLISH;
    static final Locale FI = Locale.of("fi");
    static final Locale SV = Locale.of("sv");

    /** Accessible name (= language name) of the currently shown editor. */
    private static String shownLanguage(BrowserlessUIContext ui) {
        return ui.findTextField().component().getAriaLabel().orElseThrow();
    }

    @Test
    void typingIntoTheShownEditorCollectsTheValuePerLanguage() {
        LocalizedTextField field = new LocalizedTextField(EN, FI, SV);
        try (var ui = BrowserlessUIContext.forComponent(field)) {
            field.setSelectedLocale(EN);

            ui.findTextField().withAriaLabel("English").setValue("Hello");

            assertEquals("Hello", field.getValue().get(EN));
            // The other languages remain empty.
            assertEquals("", field.getValue().get(FI));
        }
    }

    @Test
    void setValuePresentsEachLanguageOnItsOwnTab() {
        LocalizedTextField field = new LocalizedTextField(EN, FI, SV);
        try (var ui = BrowserlessUIContext.forComponent(field)) {
            Map<Locale, String> value = new LinkedHashMap<>();
            value.put(EN, "Hi");
            value.put(FI, "Moi");
            value.put(SV, "Hej");
            field.setValue(value);

            // Selecting a language tab shows that language's text.
            ui.findTabs().select(0);
            assertEquals("Hi", ui.findTextField().component().getValue());

            ui.findTabs().select(1);
            assertEquals("Moi", ui.findTextField().component().getValue());
        }
    }

    @Test
    void languagesAreOrderedAlphabeticallyRegardlessOfInputOrder() {
        // Given in a deliberately unsorted order; expected order by display
        // name is English < Suomi < Svenska.
        LocalizedTextField field = new LocalizedTextField(SV, EN, FI);
        try (var ui = BrowserlessUIContext.forComponent(field)) {
            ui.findTabs().select(0);
            assertEquals("English", shownLanguage(ui));
            ui.findTabs().select(1);
            assertEquals("Suomi", shownLanguage(ui));
            ui.findTabs().select(2);
            assertEquals("Svenska", shownLanguage(ui));
        }
    }

    @Test
    void usesAComboBoxSelectorBeyondTheThreshold() {
        // Three languages with a threshold of two -> combo box mode.
        LocalizedTextField field = new LocalizedTextField(List.of(SV, EN, FI), 2);
        try (var ui = BrowserlessUIContext.forComponent(field)) {
            assertFalse(ui.findTabs().exists(),
                    "Should not use a tab bar beyond the threshold");
            assertTrue(ui.findComboBox(Locale.class).exists(),
                    "Should use a combo box beyond the threshold");

            // The selection API chooses which language's editor is shown.
            field.setSelectedLocale(FI);
            assertEquals(FI, field.getSelectedLocale());
            assertEquals("Suomi", shownLanguage(ui));
        }
    }

    @Test
    void translateActionIsHiddenUntilATranslatorIsSet() {
        LocalizedTextField field = new LocalizedTextField(EN, FI);
        try (var ui = BrowserlessUIContext.forComponent(field)) {
            assertFalse(field.getTranslateButton().isVisible(),
                    "Translate button must be hidden without a Translator");

            field.setTranslator((text, from, to) -> text);
            assertTrue(field.getTranslateButton().isVisible(),
                    "Translate button must appear once a Translator is set");
        }
    }

    @Test
    void translateFillsTheOtherLanguagesFromTheSelectedOne() {
        // Synchronous, deterministic stand-in for a real (AI) translator.
        Translator translator = (text, from, to) -> text + "-" + to.getLanguage();

        LocalizedTextField field = new LocalizedTextField(EN, FI, SV);
        field.setTranslator(translator);
        try (var ui = BrowserlessUIContext.forComponent(field)) {
            field.setSelectedLocale(EN);
            ui.findTextField().withAriaLabel("English").setValue("Hello");

            ui.findButton().withAriaLabelContaining("Translate to other languages").click();

            // Translation runs off the UI thread and applies via UI.access, so
            // pump the UI until the result is in.
            waitUntil(ui, () -> !field.getValue().getOrDefault(FI, "").isEmpty());

            assertEquals("Hello-fi", field.getValue().get(FI));
            assertEquals("Hello-sv", field.getValue().get(SV));
            // The source language is left untouched.
            assertEquals("Hello", field.getValue().get(EN));
        }
    }

    private static void waitUntil(BrowserlessUIContext ui, BooleanSupplier condition) {
        long deadline = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < deadline) {
            // Temporarily releases the session lock, which both lets the
            // translation worker thread enqueue its UI.access command and runs
            // the pending access tasks already in the queue.
            ui.runPendingSignalsTasks();
            ui.roundTrip();
            if (condition.getAsBoolean()) {
                return;
            }
        }
        if (!condition.getAsBoolean()) {
            throw new AssertionError("Condition was not met within the timeout");
        }
    }
}
