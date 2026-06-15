package org.vaadin.firitin.fields.localized;

import com.vaadin.browserless.BrowserlessUIContext;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Browserless tests for {@link LocalizedField} (via {@link LocalizedTextField}).
 * The component is a composition over a {@link com.vaadin.flow.component.tabs.TabSheet}
 * and per-language {@code TextField}s, so these tests focus on the component's
 * own behavior — value presentation/collection, tab switching and the
 * translate action — rather than the (theme-only) rendering, which is best
 * verified visually.
 * <p>
 * Uses the Playwright-like browserless locator API: a route-free single
 * component is attached with {@link BrowserlessUIContext#forComponent}, and the
 * inner fields/button are driven through {@code findTextField()},
 * {@code findTabSheet()} and {@code findButton()}.
 */
class LocalizedFieldTest {

    static final Locale EN = Locale.ENGLISH;
    static final Locale FI = Locale.of("fi");
    static final Locale SV = Locale.of("sv");

    @Test
    void typingIntoTheVisibleTabCollectsTheValuePerLanguage() {
        LocalizedTextField field = new LocalizedTextField(EN, FI, SV);
        try (var ui = BrowserlessUIContext.forComponent(field)) {

            // The first (English) tab is selected, so its editor is the visible
            // one. The editor's accessible name is the language name.
            ui.findTextField().withAriaLabel("English").setValue("Hello");

            assertEquals("Hello", field.getValue().get(EN));
            // The other languages have been visited but not typed into.
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

            // English tab is showing.
            assertEquals("Hi",
                    ui.findTextField().withAriaLabel("English").component().getValue());

            // Switch to the Finnish tab (index 1; the tab label carries a flag
            // emoji prefix, so select by index); its editor now shows the
            // Finnish text.
            ui.findTabSheet().select(1);
            assertEquals("Moi",
                    ui.findTextField().withAriaLabel("Suomi").component().getValue());
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
