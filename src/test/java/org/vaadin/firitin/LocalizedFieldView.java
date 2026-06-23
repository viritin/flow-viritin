package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.fields.localized.LocalizedField;
import org.vaadin.firitin.fields.localized.LocalizedTextArea;
import org.vaadin.firitin.fields.localized.LocalizedTextField;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Route
public class LocalizedFieldView extends VVerticalLayout {

    static final Locale FI = Locale.of("fi");
    static final Locale SV = Locale.of("sv");

    static final List<Locale> LOCALES = List.of(
            Locale.ENGLISH, FI, SV, Locale.GERMAN);

    /** A dozen-plus languages to see how the tab bar copes with many tabs. */
    static final List<Locale> MANY_LOCALES = List.of(
            Locale.ENGLISH, FI, SV, Locale.GERMAN,
            Locale.FRENCH, Locale.of("es"), Locale.ITALIAN, Locale.of("pt"),
            Locale.of("nl"), Locale.of("da"), Locale.of("no"), Locale.of("pl"),
            Locale.of("cs"), Locale.of("et"));

    /** Over two dozen languages — past the threshold, so a ComboBox is used. */
    static final List<Locale> LOTS_OF_LOCALES = List.of(
            Locale.ENGLISH, FI, SV, Locale.GERMAN,
            Locale.FRENCH, Locale.of("es"), Locale.ITALIAN, Locale.of("pt"),
            Locale.of("nl"), Locale.of("da"), Locale.of("no"), Locale.of("pl"),
            Locale.of("cs"), Locale.of("et"), Locale.of("hu"), Locale.of("ro"),
            Locale.of("el"), Locale.of("tr"), Locale.of("uk"), Locale.of("ru"),
            Locale.JAPANESE, Locale.KOREAN, Locale.CHINESE, Locale.of("ar"));

    private static final Map<Locale, String> DEMO_VALUES = Map.of(
            Locale.ENGLISH, "Hello", FI, "Hei", SV, "Hej", Locale.GERMAN, "Hallo");

    private final List<LocalizedField<?>> allFields = new ArrayList<>();

    public LocalizedFieldView() {
        // No translator is wired here on purpose: the fields discover the
        // registered Translator bean via the Instantiator on their own (see
        // TranslatorConfig), so the translate action just appears. One field
        // below opts out explicitly with setTranslator(null).

        // Toolbar at the top, separated by a line, acting on every field below.
        Button setDemo = new Button("Set demo values",
                e -> allFields.forEach(f -> f.setValue(DEMO_VALUES)));
        Button clear = new Button("Clear",
                e -> allFields.forEach(f -> f.setValue(Map.of())));

        // Live switch for how languages are shown in the selectors. The setter
        // refreshes the tabs / combo box immediately, no re-creation needed.
        RadioButtonGroup<LocalizedField.LanguageDisplay> display = new RadioButtonGroup<>();
        display.setLabel("Language display");
        display.setItems(LocalizedField.LanguageDisplay.values());
        display.setValue(LocalizedField.LanguageDisplay.FLAG_AND_NAME);
        display.addValueChangeListener(e ->
                allFields.forEach(f -> f.setLanguageDisplay(e.getValue())));

        add(new HorizontalLayout(setDemo, clear), display);
        add(new Hr());

        // A plain text field as a reference, to compare how the box matches the
        // theme's own field styling (Lumo: fill, no border; Aura: border + white).
        com.vaadin.flow.component.textfield.TextField plainRef =
                new com.vaadin.flow.component.textfield.TextField("Plain TextField (reference)");
        plainRef.setWidth("480px");
        add(plainRef);

        LocalizedTextField title = new LocalizedTextField("Title", LOCALES);
        title.setHelperText("Enter the title in each language. Each language has its own tab.");
        title.setWidth("480px");
        addSection("LocalizedTextField", title);

        LocalizedTextArea description = new LocalizedTextArea("Description", LOCALES);
        description.setHelperText("Multi-line description per language.");
        // Verifies the field is sizable straight through its HasSize API: the
        // height propagates down to the editor inside the box.
        description.setWidth("480px");
        description.setHeight("220px");
        addSection("LocalizedTextArea", description);

        LocalizedTextField manyLanguages = new LocalizedTextField("Slogan", MANY_LOCALES);
        manyLanguages.setHelperText(MANY_LOCALES.size()
                + " languages — translation explicitly turned off here (no magic icon).");
        manyLanguages.setWidth("480px");
        // Explicitly override the auto-discovered translator: no action here.
        manyLanguages.setTranslator(null);
        addSection("Many languages", manyLanguages);

        LocalizedTextField lots = new LocalizedTextField("Keyword", LOTS_OF_LOCALES);
        lots.setHelperText(LOTS_OF_LOCALES.size()
                + " languages — past the threshold, so a ComboBox picks the language.");
        lots.setWidth("480px");
        addSection("Lots of languages (ComboBox mode)", lots);
    }

    private void addSection(String heading, LocalizedField<?> field) {
        allFields.add(field);
        add(new H3(heading));
        add(field);
        Paragraph value = new Paragraph();
        add(value);
        field.addValueChangeListener(e -> value.setText(format(e.getValue())));
    }

    private static String format(Map<Locale, String> value) {
        if (value == null) {
            return "null";
        }
        return value.entrySet().stream()
                .map(en -> en.getKey().getLanguage() + "=\"" + en.getValue() + "\"")
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
