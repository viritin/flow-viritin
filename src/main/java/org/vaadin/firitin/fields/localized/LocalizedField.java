package org.vaadin.firitin.fields.localized;

import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextFieldBase;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.vaadin.firitin.components.customfield.VCustomField;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Base class for a {@link com.vaadin.flow.component.customfield.CustomField}
 * that edits the language versions of a single piece of text. Each locale gets
 * its own tab and the tab content is a single text editor (a
 * {@link com.vaadin.flow.component.textfield.TextField} or
 * {@link com.vaadin.flow.component.textfield.TextArea}); the field value is a
 * map from locale to the entered text.
 * <p>
 * The {@link TabSheet} is used purely as a layout. The {@code localized-field}
 * class on this component activates the styling in {@code localized-field.css}
 * (loaded via the {@link StyleSheet} annotation below), which reworks the
 * default styling so the tabsheet is a plain bordered box with a straight
 * divider under the tabs, the active tab is marked with an underline instead of
 * looking like a button, and the editor fills the box without a second border
 * ("box in a box"). The styling is theme-agnostic and works in both the Aura
 * and Lumo themes.
 *
 * @param <F> the type of text editor used for each language tab
 */
@StyleSheet("context://frontend/org/vaadin/firitin/components/localized-field.css")
public abstract class LocalizedField<F extends TextFieldBase<F, String>>
        extends VCustomField<Map<Locale, String>> {

    private final Map<Locale, F> fields = new LinkedHashMap<>();
    private final TabSheet tabSheet = new TabSheet();
    private final Button translateButton = new Button();
    private Translator translator;

    protected LocalizedField(List<Locale> locales) {
        addClassName("localized-field");
        tabSheet.setWidthFull();
        locales.forEach(locale -> {
            F field = createField(locale);
            // The tab provides the language visually; give screen reader users
            // the same context by naming the editor after its language.
            field.setAriaLabel(languageName(locale));
            field.setValueChangeMode(ValueChangeMode.LAZY);
            field.addValueChangeListener(e -> updateValue());
            fields.put(locale, field);
            tabSheet.add(tabLabel(locale), field);
        });
        // Selecting a language tab moves focus straight into its editor.
        tabSheet.addSelectedChangeListener(event -> {
            if (event.isFromClient()) {
                int index = tabSheet.getSelectedIndex();
                fields.values().stream().skip(index).findFirst()
                        .ifPresent(Focusable::focus);
            }
        });
        add(tabSheet);

        // A discreet icon-only action below the box (outside its border) that
        // fills the other languages from the currently selected one. The "magic"
        // icon hints at the (typically AI-based) translation; the explanatory
        // text is exposed as a tooltip and as the accessible name. Only shown
        // once a Translator has been set.
        translateButton.setIcon(VaadinIcon.MAGIC.create());
        translateButton.setAriaLabel(translateButtonText());
        translateButton.setTooltipText(translateButtonText());
        translateButton.addThemeVariants(ButtonVariant.LUMO_SMALL,
                ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ICON);
        translateButton.setVisible(false);
        translateButton.getStyle().setMarginTop("0.2em");
        translateButton.addClickListener(e -> translateToOtherLanguages());
        add(translateButton);
    }

    /**
     * Sets the {@link Translator} used by the "translate to other languages"
     * action shown below the field. When set to a non-{@code null} value the
     * action becomes visible; setting it to {@code null} hides it again.
     */
    public void setTranslator(Translator translator) {
        this.translator = translator;
        translateButton.setVisible(translator != null);
    }

    public Translator getTranslator() {
        return translator;
    }

    /**
     * Returns the button that triggers the "translate to other languages"
     * action, so its presentation can be customized: change the icon or text,
     * tweak the theme variants, replace the tooltip, etc. The component manages
     * its visibility (tied to {@link #setTranslator(Translator)}), its click
     * handler and its disabled/tooltip state while translating, so avoid
     * overriding those.
     */
    public Button getTranslateButton() {
        return translateButton;
    }

    /**
     * The label of the translate action. Override to localize it.
     */
    protected String translateButtonText() {
        return "Translate to other languages…";
    }

    /**
     * Takes the text of the currently selected language tab and translates it
     * into all the other languages, replacing their current content. The
     * translation runs off the UI thread (translators may do network I/O), so
     * the application needs server push enabled for the result to appear
     * without a further round trip.
     */
    protected void translateToOtherLanguages() {
        if (translator == null) {
            return;
        }
        Locale source = selectedLocale();
        if (source == null) {
            return;
        }
        String text = fields.get(source).getValue();
        UI ui = UI.getCurrent();
        setTranslating(true);
        Thread worker = new Thread(() -> {
            try {
                Map<Locale, String> translations = new LinkedHashMap<>();
                fields.keySet().stream()
                        .filter(locale -> !locale.equals(source))
                        .forEach(target -> translations.put(target,
                                translator.translate(text, source, target)));
                ui.access(() -> {
                    translations.forEach((locale, value) ->
                            fields.get(locale).setValue(value == null ? "" : value));
                    updateValue();
                    setTranslating(false);
                });
            } catch (RuntimeException ex) {
                ui.access(() -> {
                    setTranslating(false);
                    Notification.show("Translation failed: " + ex.getMessage());
                });
            }
        }, "localized-field-translator");
        worker.setDaemon(true);
        worker.start();
    }

    private Locale selectedLocale() {
        int index = tabSheet.getSelectedIndex();
        return fields.keySet().stream().skip(index).findFirst().orElse(null);
    }

    private void setTranslating(boolean translating) {
        translateButton.setEnabled(!translating);
        translateButton.setTooltipText(translating ? "Translating…" : translateButtonText());
    }

    /**
     * Creates the editor used as the content of a single language tab. The
     * placeholder, value change mode and change listener are configured by the
     * base class.
     */
    protected abstract F createField(Locale locale);

    /**
     * Representative country (ISO 3166 alpha-2) for the most common languages,
     * used to pick a flag when a {@link Locale} carries no country of its own.
     * Override {@link #flagFor(Locale)} to extend or change this.
     */
    private static final Map<String, String> LANGUAGE_COUNTRIES = Map.ofEntries(
            Map.entry("en", "GB"), Map.entry("fi", "FI"), Map.entry("sv", "SE"),
            Map.entry("de", "DE"), Map.entry("fr", "FR"), Map.entry("es", "ES"),
            Map.entry("it", "IT"), Map.entry("pt", "PT"), Map.entry("nl", "NL"),
            Map.entry("da", "DK"), Map.entry("nb", "NO"), Map.entry("nn", "NO"),
            Map.entry("no", "NO"), Map.entry("pl", "PL"), Map.entry("cs", "CZ"),
            Map.entry("sk", "SK"), Map.entry("et", "EE"), Map.entry("lv", "LV"),
            Map.entry("lt", "LT"), Map.entry("hu", "HU"), Map.entry("ro", "RO"),
            Map.entry("el", "GR"), Map.entry("tr", "TR"), Map.entry("uk", "UA"),
            Map.entry("ru", "RU"), Map.entry("ar", "SA"), Map.entry("he", "IL"),
            Map.entry("ja", "JP"), Map.entry("ko", "KR"), Map.entry("zh", "CN"),
            Map.entry("hi", "IN"), Map.entry("th", "TH"), Map.entry("vi", "VN"));

    /**
     * The text shown on the tab for a locale: the flag (if any) from
     * {@link #flagFor(Locale)} followed by the {@link #languageName(Locale)}.
     * Override to fully customize the label.
     */
    protected String tabLabel(Locale locale) {
        String flag = flagFor(locale);
        String name = languageName(locale);
        return flag.isEmpty() ? name : flag + " " + name;
    }

    /**
     * The capitalized, localized language name, used both as the tab label text
     * and as the accessible name of the language's editor.
     */
    protected String languageName(Locale locale) {
        String name = locale.getDisplayLanguage(locale);
        if (name.isEmpty()) {
            return locale.toLanguageTag();
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * Returns a flag emoji visualizing the language of the given locale, or an
     * empty string if none is known. The country is taken from the locale
     * itself when present, otherwise derived from the language for the most
     * common languages. Override to support more languages or to pick a
     * different country for a language (e.g. US instead of GB for English).
     */
    protected String flagFor(Locale locale) {
        String country = locale.getCountry();
        if (country.isEmpty()) {
            country = LANGUAGE_COUNTRIES.getOrDefault(locale.getLanguage(), "");
        }
        return country.length() == 2 ? flagEmoji(country) : "";
    }

    /** Builds a flag emoji from a two-letter ISO country code. */
    private static String flagEmoji(String country) {
        int offset = 0x1F1E6 - 'A';
        country = country.toUpperCase();
        return new StringBuilder()
                .appendCodePoint(offset + country.charAt(0))
                .appendCodePoint(offset + country.charAt(1))
                .toString();
    }

    @Override
    protected Map<Locale, String> generateModelValue() {
        Map<Locale, String> value = new LinkedHashMap<>();
        fields.forEach((locale, field) -> value.put(locale, field.getValue()));
        return value;
    }

    @Override
    protected void setPresentationValue(Map<Locale, String> value) {
        fields.forEach((locale, field) ->
                field.setValue(value == null ? "" : value.getOrDefault(locale, "")));
    }
}
