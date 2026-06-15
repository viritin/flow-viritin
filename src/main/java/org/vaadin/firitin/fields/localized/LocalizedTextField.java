package org.vaadin.firitin.fields.localized;

import com.vaadin.flow.component.textfield.TextField;

import java.util.List;
import java.util.Locale;

/**
 * A {@link LocalizedField} that edits each language version with a single-line
 * {@link TextField}.
 */
public class LocalizedTextField extends LocalizedField<TextField> {

    public LocalizedTextField(Locale... locales) {
        this(List.of(locales));
    }

    public LocalizedTextField(List<Locale> locales) {
        super(locales);
    }

    @Override
    protected TextField createField(Locale locale) {
        return new TextField();
    }
}
