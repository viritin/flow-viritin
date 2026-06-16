package org.vaadin.firitin.fields.localized;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextFieldBase;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;
import org.vaadin.firitin.components.button.ActionButton;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.customfield.VCustomField;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Base class for a {@link com.vaadin.flow.component.customfield.CustomField}
 * that edits the language versions of a single piece of text. The field value
 * is a map from locale to the entered text, and the user edits one language at
 * a time in a single text editor (a
 * {@link com.vaadin.flow.component.textfield.TextField} or
 * {@link com.vaadin.flow.component.textfield.TextArea}).
 * <p>
 * The language to edit is picked with a selector at the top of a bordered box:
 * a {@link Tabs} tab bar by default, or a {@link ComboBox} once there are more
 * languages than {@link #getDefaultComboBoxThreshold()} (configurable), since a
 * tab bar becomes unwieldy with very many languages. Languages are always shown
 * in alphabetical order of their localized name.
 * <p>
 * The {@code localized-field} class on this component activates the styling in
 * {@code localized-field.css} (loaded via the {@link StyleSheet} annotation
 * below): a plain bordered box with the selector as a shaded header above a
 * straight divider, the editor filling the box with no chrome of its own, and
 * (in tab mode) the active language marked with an underline. The styling is
 * theme-agnostic and works in both the Aura and Lumo themes; the two selector
 * modes look the same in spirit.
 * <p>
 * A {@link Translator} can fill the other languages from the currently edited
 * one via a small "magic" action in the corner. It is discovered automatically
 * from the {@link com.vaadin.flow.di.Instantiator} (so registering a Spring
 * bean is enough for the action to appear), or set explicitly with
 * {@link #setTranslator(Translator)}.
 *
 * @param <F> the type of text editor used for each language
 */
@StyleSheet("context://frontend/org/vaadin/firitin/components/localized-field.css")
public abstract class LocalizedField<F extends TextFieldBase<F, String>>
        extends VCustomField<Map<Locale, String>> {

    private static int defaultComboBoxThreshold = 20;

    private final Map<Locale, F> fields = new LinkedHashMap<>();
    private final Div box = new Div();
    private final Div content = new Div();
    private final ActionButton<Map<Locale, String>> translateButton = new ActionButton<>();
    // Source language and text captured on the UI thread when the translate
    // action is clicked, so the background task does not read live UI state.
    private Locale pendingSource;
    private String pendingText;

    // Exactly one of these is used, depending on the number of languages.
    private Tabs tabs;
    private final Map<Locale, Tab> localeToTab = new HashMap<>();
    private final Map<Tab, Locale> tabToLocale = new HashMap<>();
    private ComboBox<Locale> localeSelect;

    private Locale selectedLocale;
    private boolean explicitSelection;
    private boolean updatingSelector;

    // Translator resolution: an explicitly set one (via setTranslator, even
    // null) always wins; otherwise one looked up from the Instantiator on attach
    // (e.g. a Spring bean) is used.
    private Translator translator;
    private boolean translatorExplicitlySet;
    private Translator resolvedTranslator;
    private boolean translatorResolved;

    protected LocalizedField(List<Locale> locales) {
        this(locales, defaultComboBoxThreshold);
    }

    protected LocalizedField(List<Locale> locales, int comboBoxThreshold) {
        addClassName("localized-field");

        // Always show the languages in alphabetical order of their (localized)
        // name, regardless of the order they were given in. This keeps the
        // selector predictable and easy to scan.
        List<Locale> ordered = new ArrayList<>(locales);
        ordered.sort(Comparator.comparing(this::languageName, Collator.getInstance()));
        ordered.forEach(locale -> {
            F field = createField(locale);
            // The selector provides the language visually; give screen reader
            // users the same context by naming the editor after its language.
            field.setAriaLabel(languageName(locale));
            field.setValueChangeMode(ValueChangeMode.LAZY);
            field.addValueChangeListener(e -> updateValue());
            field.setWidthFull();
            fields.put(locale, field);
        });

        content.addClassName("localized-field-content");

        Component selector = ordered.size() > comboBoxThreshold
                ? buildComboBoxSelector(ordered)
                : buildTabsSelector(ordered);

        configureTranslateButton();

        box.addClassName("localized-field-box");
        box.add(selector, content, translateButton);
        add(box);

        if (!ordered.isEmpty()) {
            showLocale(ordered.get(0), false);
        }
    }

    private Component buildTabsSelector(List<Locale> ordered) {
        tabs = new Tabs();
        tabs.setWidthFull();
        ordered.forEach(locale -> {
            Tab tab = new Tab(tabLabel(locale));
            localeToTab.put(locale, tab);
            tabToLocale.put(tab, locale);
            tabs.add(tab);
        });
        // Swap the editor on any selection change (including programmatic ones);
        // only move focus into the editor on a real tab click.
        tabs.addSelectedChangeListener(event ->
                onLanguagePicked(tabToLocale.get(tabs.getSelectedTab()),
                        event.isFromClient()));
        return tabs;
    }

    private Component buildComboBoxSelector(List<Locale> ordered) {
        localeSelect = new ComboBox<>();
        localeSelect.addClassName("localized-field-select");
        localeSelect.setItems(ordered);
        localeSelect.setItemLabelGenerator(this::tabLabel);
        localeSelect.setAllowCustomValue(false);
        localeSelect.setClearButtonVisible(false);
        localeSelect.setWidthFull();
        localeSelect.setAriaLabel("Language");
        localeSelect.addValueChangeListener(event ->
                onLanguagePicked(event.getValue(), event.isFromClient()));
        return localeSelect;
    }

    private void configureTranslateButton() {
        // A discreet icon-only action overlaid in the bottom-right corner of the
        // field box that fills the other languages from the currently selected
        // one. The "magic" icon hints at the (typically AI-based) translation;
        // the explanatory text is exposed as a tooltip and as the accessible
        // name. Only shown once a Translator is available.
        //
        // It is an ActionButton: the (possibly slow) translation runs off the UI
        // thread and the button is disabled while it runs, so repeated clicks
        // cannot pile up several calls.
        translateButton.addClassName("localized-field-translate");
        translateButton.getStyle().setPosition(Style.Position.ABSOLUTE);
        translateButton.setShowProgressBar(false);
        translateButton.setVisible(false);

        VButton button = translateButton.getButton();
        button.setIcon(VaadinIcon.MAGIC.create());
        button.setAriaLabel(translateButtonText());
        button.setTooltipText(translateButtonText());
        button.addThemeVariants(ButtonVariant.LUMO_SMALL,
                ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ICON);

        // Capture the source on the UI thread, translate in the background,
        // apply the results back on the UI thread.
        translateButton.setPreUiAction(() -> {
            pendingSource = selectedLocale;
            pendingText = pendingSource == null ? "" : fields.get(pendingSource).getValue();
        });
        translateButton.setAction(this::translateInBackground);
        translateButton.setPostUiAction(this::applyTranslations);
    }

    /**
     * Runs off the UI thread: translates the captured source text into every
     * other language. Translators may block (network/LLM I/O).
     */
    private Map<Locale, String> translateInBackground() {
        Translator translator = getTranslator();
        if (translator == null || pendingSource == null) {
            return Map.of();
        }
        Map<Locale, String> translations = new LinkedHashMap<>();
        fields.keySet().stream()
                .filter(locale -> !locale.equals(pendingSource))
                .forEach(target -> translations.put(target,
                        translator.translate(pendingText, pendingSource, target)));
        return translations;
    }

    /** Runs on the UI thread after the background translation completes. */
    private void applyTranslations(Map<Locale, String> translations) {
        translations.forEach((locale, value) ->
                fields.get(locale).setValue(value == null ? "" : value));
        updateValue();
    }

    /**
     * Reacts to a language being picked from the client (tab click or combo box
     * selection): shows that language's editor and, optionally, focuses it.
     */
    private void onLanguagePicked(Locale locale, boolean focus) {
        if (updatingSelector || locale == null) {
            return;
        }
        showLocale(locale, focus);
    }

    /**
     * Shows the editor for the given locale and keeps the selector in sync. Only
     * the selected language's editor is attached at a time.
     */
    private void showLocale(Locale locale, boolean focus) {
        selectedLocale = locale;
        F editor = fields.get(locale);
        content.removeAll();
        content.add(editor);
        syncSelector(locale);
        if (focus) {
            editor.focus();
        }
    }

    private void syncSelector(Locale locale) {
        updatingSelector = true;
        try {
            if (tabs != null) {
                tabs.setSelectedTab(localeToTab.get(locale));
            }
            if (localeSelect != null) {
                localeSelect.setValue(locale);
            }
        } finally {
            updatingSelector = false;
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        // Default the open language to the user's own, if it is one of the
        // editable languages and the application has not chosen one explicitly.
        if (!explicitSelection) {
            matchingLocale(attachEvent.getUI().getLocale())
                    .ifPresent(locale -> showLocale(locale, false));
        }
        // Unless a translator was set explicitly, look one up from the
        // Instantiator (a registered Spring bean, say). If found, the translate
        // action appears with no extra wiring.
        if (!translatorExplicitlySet && !translatorResolved) {
            translatorResolved = true;
            resolvedTranslator = lookupTranslator(attachEvent);
            updateTranslateButtonVisibility();
        }
    }

    private Translator lookupTranslator(AttachEvent attachEvent) {
        try {
            return attachEvent.getUI().getSession().getService()
                    .getInstantiator().getOrCreate(Translator.class);
        } catch (RuntimeException noTranslatorRegistered) {
            // The Instantiator cannot supply a Translator (e.g. no Spring bean,
            // or plain Vaadin trying to instantiate an interface). That just
            // means the translate action stays hidden.
            return null;
        }
    }

    /**
     * Finds the editable locale that best matches the given one: an exact match
     * if present, otherwise the first locale with the same language.
     */
    private Optional<Locale> matchingLocale(Locale locale) {
        if (locale == null) {
            return Optional.empty();
        }
        if (fields.containsKey(locale)) {
            return Optional.of(locale);
        }
        return fields.keySet().stream()
                .filter(l -> l.getLanguage().equals(locale.getLanguage()))
                .findFirst();
    }

    /**
     * Selects the language whose editor is shown (the "open" one). By default
     * this is the user's own language if editable, otherwise the alphabetically
     * first; calling this overrides that default.
     *
     * @throws IllegalArgumentException if the locale is not one of the field's
     *                                  editable languages
     */
    public void setSelectedLocale(Locale locale) {
        if (!fields.containsKey(locale)) {
            throw new IllegalArgumentException(
                    "Not an editable language of this field: " + locale);
        }
        explicitSelection = true;
        showLocale(locale, false);
    }

    /** The language whose editor is currently shown. */
    public Locale getSelectedLocale() {
        return selectedLocale;
    }

    /**
     * The default number of languages above which new instances use a
     * {@link ComboBox} instead of a {@link Tabs} bar to select the language.
     */
    public static int getDefaultComboBoxThreshold() {
        return defaultComboBoxThreshold;
    }

    public static void setDefaultComboBoxThreshold(int threshold) {
        defaultComboBoxThreshold = threshold;
    }

    /**
     * Sets the {@link Translator} used by the "translate to other languages"
     * action. When set to a non-{@code null} value the action becomes visible;
     * when set to {@code null} it is hidden.
     * <p>
     * Calling this <em>explicitly overrides</em> any translator the component
     * would otherwise discover from the {@link com.vaadin.flow.di.Instantiator}
     * on attach (e.g. a registered Spring bean). In particular, calling it with
     * {@code null} turns the action off even when such a translator exists.
     */
    public void setTranslator(Translator translator) {
        this.translator = translator;
        this.translatorExplicitlySet = true;
        updateTranslateButtonVisibility();
    }

    /**
     * The effective translator: the explicitly set one if {@link #setTranslator}
     * has been called, otherwise the one discovered from the Instantiator (or
     * {@code null} if none).
     */
    public Translator getTranslator() {
        return translatorExplicitlySet ? translator : resolvedTranslator;
    }

    private void updateTranslateButtonVisibility() {
        translateButton.setVisible(getTranslator() != null);
    }

    /**
     * Returns the {@link ActionButton} that triggers the "translate to other
     * languages" action, so its presentation can be customized: get its inner
     * button via {@link ActionButton#getButton()} to change the icon, text,
     * theme variants or tooltip, set a busy text, show its progress bar, etc.
     * The component manages the button's visibility (tied to the available
     * {@link Translator}) and its action, so avoid overriding those.
     */
    public ActionButton<Map<Locale, String>> getTranslateButton() {
        return translateButton;
    }

    /**
     * The label of the translate action. Override to localize it.
     */
    protected String translateButtonText() {
        return "Translate to other languages…";
    }

    /**
     * Creates the editor used to edit a single language. The aria label, value
     * change mode, change listener and width are configured by the base class.
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
     * The text shown for a locale in the selector: the flag (if any) from
     * {@link #flagFor(Locale)} followed by the {@link #languageName(Locale)}.
     * Override to fully customize the label.
     */
    protected String tabLabel(Locale locale) {
        String flag = flagFor(locale);
        String name = languageName(locale);
        return flag.isEmpty() ? name : flag + " " + name;
    }

    /**
     * The capitalized, localized language name, used both as the selector label
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
