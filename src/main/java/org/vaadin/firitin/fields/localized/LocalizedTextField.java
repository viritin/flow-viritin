package org.vaadin.firitin.fields.localized;

import com.vaadin.flow.component.textfield.TextField;

import java.util.List;
import java.util.Locale;

/**
 * A {@link LocalizedField} that edits each language version with a single-line
 * {@link TextField}.
 */
public class LocalizedTextField extends LocalizedField<TextField> {

    /** Creates a field for the given languages. */
    public LocalizedTextField(Locale... locales) {
        this(List.of(locales));
    }

    /** Creates a field for the given languages. */
    public LocalizedTextField(List<Locale> locales) {
        super(locales);
    }

    /**
     * Creates a field for the given languages, switching to a combo box selector
     * when there are more than {@code comboBoxThreshold} of them.
     */
    public LocalizedTextField(List<Locale> locales, int comboBoxThreshold) {
        super(locales, comboBoxThreshold);
    }

    @Override
    protected TextField createField(Locale locale) {
        return new TextField();
    }
}
