package org.vaadin.firitin.fields.localized;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsTester;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldTester;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Browserless tests for {@link LocalizedField} (via {@link LocalizedTextField}).
 * The component is a composition over a language selector ({@code Tabs} or
 * {@code ComboBox}) and per-language editors, so these tests focus on the
 * component's own behavior — value collection/presentation, language switching,
 * the selector mode, language-code binding and the translate action — rather
 * than the (theme-only) rendering, which is best verified visually.
 * <p>
 * They navigate to {@link LocalizedFieldTestView} and drive it through the
 * browserless API: {@code find(...)}/{@code test(...)} for the inner
 * editor/selector/button, and the field's own API for value and configuration.
 */
class LocalizedFieldTest {

    static final Locale EN = Locale.ENGLISH;
    static final Locale FI = Locale.of("fi");
    static final Locale SV = Locale.of("sv");

    private static final String VIEW_PACKAGE = "org.vaadin.firitin.fields.localized";

    /** Navigates to the test view in a fresh context and runs the test body. */
    private static void inView(BiConsumer<BrowserlessUIContext, LocalizedFieldTestView> body) {
        try (BrowserlessApplicationContext app =
                     BrowserlessApplicationContext.create(VIEW_PACKAGE)) {
            BrowserlessUIContext ui = app.newUser().newWindow();
            LocalizedFieldTestView view = ui.navigate(LocalizedFieldTestView.class);
            body.accept(ui, view);
        }
    }

    /** The single editor currently shown for the given field. */
    private static TextField shownEditor(BrowserlessUIContext ui, LocalizedTextField field) {
        return ui.find(TextField.class).from(field).first();
    }

    @Test
    void typingIntoTheShownEditorCollectsTheValuePerLanguage() {
        inView((ui, view) -> {
            view.text.setSelectedLocale(EN);
            ui.test(TextFieldTester.class, shownEditor(ui, view.text)).setValue("Hello");

            assertEquals("Hello", view.text.getValue().get(EN));
            assertEquals("", view.text.getValue().get(FI));
        });
    }

    @Test
    void setValuePresentsEachLanguageOnItsOwnTab() {
        inView((ui, view) -> {
            Map<Locale, String> value = new LinkedHashMap<>();
            value.put(EN, "Hi");
            value.put(FI, "Moi");
            value.put(SV, "Hej");
            view.text.setValue(value);

            Tabs tabs = ui.find(Tabs.class).from(view.text).first();
            ui.test(TabsTester.class, tabs).select(0);
            assertEquals("Hi", shownEditor(ui, view.text).getValue());
            ui.test(TabsTester.class, tabs).select(1);
            assertEquals("Moi", shownEditor(ui, view.text).getValue());
        });
    }

    @Test
    void languagesAreOrderedAlphabeticallyRegardlessOfInputOrder() {
        // The view gives the languages as SV, EN, FI; expected display order is
        // English < Suomi < Svenska.
        inView((ui, view) -> {
            Tabs tabs = ui.find(Tabs.class).from(view.text).first();
            ui.test(TabsTester.class, tabs).select(0);
            assertEquals("English", shownLanguage(ui, view.text));
            ui.test(TabsTester.class, tabs).select(1);
            assertEquals("Suomi", shownLanguage(ui, view.text));
            ui.test(TabsTester.class, tabs).select(2);
            assertEquals("Svenska", shownLanguage(ui, view.text));
        });
    }

    @Test
    void usesAComboBoxSelectorBeyondTheThreshold() {
        inView((ui, view) -> {
            assertFalse(ui.find(Tabs.class).from(view.combo).exists(),
                    "Should not use a tab bar beyond the threshold");
            assertTrue(ui.find(ComboBox.class).from(view.combo).exists(),
                    "Should use a combo box beyond the threshold");

            view.combo.setSelectedLocale(FI);
            assertEquals(FI, view.combo.getSelectedLocale());
            assertEquals("Suomi", shownLanguage(ui, view.combo));
        });
    }

    @Test
    void labelConstructorSetsTheLabel() {
        inView((ui, view) -> assertEquals("Texts", view.text.getLabel()));
    }

    @Test
    void languageCodeMapConverterMapsByLanguageCode() {
        inView((ui, view) -> {
            Converter<Map<Locale, String>, Map<String, String>> converter =
                    view.text.languageCodeMapConverter();
            ValueContext ctx = new ValueContext();

            Map<Locale, String> all = new LinkedHashMap<>();
            all.put(EN, "Hi");
            all.put(FI, "Moi");
            all.put(SV, "Hej");
            view.text.setValue(all);

            Map<String, String> codes = converter.convertToModel(view.text.getValue(), ctx)
                    .getOrThrow(RuntimeException::new);
            assertEquals("Hi", codes.get("en"));
            assertEquals("Moi", codes.get("fi"));

            // Back from a code-keyed map; a missing code becomes empty text.
            Map<Locale, String> presentation =
                    converter.convertToPresentation(Map.of("en", "Hello", "fi", "Hei"), ctx);
            view.text.setValue(presentation);
            assertEquals("Hello", view.text.getValue().get(EN));
            assertEquals("Hei", view.text.getValue().get(FI));
            assertEquals("", view.text.getValue().get(SV));
        });
    }

    @Test
    void presentsValueByLanguageCodeWhenCountryVariantDiffers() {
        // The field has plain "en"; a value keyed by "en_US" still shows there.
        inView((ui, view) -> {
            view.text.setValue(Map.of(Locale.US, "Hello"));
            assertEquals("Hello", view.text.getValue().get(EN));
        });
    }

    @Test
    void translateActionIsHiddenUntilATranslatorIsSet() {
        inView((ui, view) -> {
            assertFalse(view.text.getTranslateButton().isVisible(),
                    "Translate button must be hidden without a Translator");

            view.text.setTranslator((text, from, to) -> text);
            assertTrue(view.text.getTranslateButton().isVisible(),
                    "Translate button must appear once a Translator is set");
        });
    }

    @Test
    void translateFillsTheOtherLanguagesFromTheSelectedOne() {
        inView((ui, view) -> {
            // Synchronous, deterministic stand-in for a real (AI) translator.
            view.text.setTranslator((text, from, to) -> text + "-" + to.getLanguage());
            view.text.setSelectedLocale(EN);
            view.text.setValue(Map.of(EN, "Hello"));

            Button button = view.text.getTranslateButton().getButton();
            ui.test(button).click();

            // Translation runs off the UI thread and applies via UI.access, so
            // pump the UI until the result is in.
            waitUntil(ui, () -> !view.text.getValue().getOrDefault(FI, "").isEmpty());

            assertEquals("Hello-fi", view.text.getValue().get(FI));
            assertEquals("Hello-sv", view.text.getValue().get(SV));
            assertEquals("Hello", view.text.getValue().get(EN));
        });
    }

    private static String shownLanguage(BrowserlessUIContext ui, LocalizedTextField field) {
        return shownEditor(ui, field).getAriaLabel().orElseThrow();
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
