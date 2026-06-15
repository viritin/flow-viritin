package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.fields.localized.LocalizedTextArea;
import org.vaadin.firitin.fields.localized.LocalizedTextField;
import org.vaadin.firitin.fields.localized.Translator;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Route
public class LocalizedFieldView extends VVerticalLayout {

    static final List<Locale> LOCALES = List.of(
            Locale.ENGLISH,
            Locale.of("fi"),
            Locale.of("sv"),
            Locale.GERMAN);

    public LocalizedFieldView(Translator translator) {

        add(new H3("LocalizedTextField"));

        LocalizedTextField title = new LocalizedTextField(LOCALES);
        title.setLabel("Title");
        title.setHelperText("Enter the title in each language. Each language has its own tab.");
        title.setTranslator(translator);
        add(title);

        Paragraph titleValue = new Paragraph();
        add(titleValue);
        title.addValueChangeListener(e -> titleValue.setText(format(e.getValue())));

        add(new Button("Set demo values", e -> title.setValue(Map.of(
                Locale.ENGLISH, "Hello",
                Locale.of("fi"), "Hei",
                Locale.of("sv"), "Hej",
                Locale.GERMAN, "Hallo"))));
        add(new Button("Clear", e -> title.clear()));

        add(new H3("LocalizedTextArea"));

        LocalizedTextArea description = new LocalizedTextArea(LOCALES);
        description.setLabel("Description");
        description.setHelperText("Multi-line description per language.");
        description.setTranslator(translator);
        add(description);

        Paragraph descriptionValue = new Paragraph();
        add(descriptionValue);
        description.addValueChangeListener(e -> descriptionValue.setText(format(e.getValue())));
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
