package org.vaadin.firitin.fields.localized;

import com.vaadin.flow.component.textfield.TextArea;

import java.util.List;
import java.util.Locale;

/**
 * A {@link LocalizedField} that edits each language version with a multi-line
 * {@link TextArea}.
 */
public class LocalizedTextArea extends LocalizedField<TextArea> {

    public LocalizedTextArea(Locale... locales) {
        this(List.of(locales));
    }

    public LocalizedTextArea(List<Locale> locales) {
        super(locales);
    }

    @Override
    protected TextArea createField(Locale locale) {
        return new TextArea();
    }
}
