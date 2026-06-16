package org.vaadin.firitin.fields.localized;

import com.vaadin.flow.component.textfield.TextArea;

import java.util.List;
import java.util.Locale;

/**
 * A {@link LocalizedField} that edits each language version with a multi-line
 * {@link TextArea}.
 */
public class LocalizedTextArea extends LocalizedField<TextArea> {

    /** Creates a field for the given languages. */
    public LocalizedTextArea(Locale... locales) {
        this(List.of(locales));
    }

    /** Creates a field for the given languages. */
    public LocalizedTextArea(List<Locale> locales) {
        super(locales);
    }

    /**
     * Creates a field for the given languages, switching to a combo box selector
     * when there are more than {@code comboBoxThreshold} of them.
     */
    public LocalizedTextArea(List<Locale> locales, int comboBoxThreshold) {
        super(locales, comboBoxThreshold);
    }

    @Override
    protected TextArea createField(Locale locale) {
        return new TextArea();
    }
}
