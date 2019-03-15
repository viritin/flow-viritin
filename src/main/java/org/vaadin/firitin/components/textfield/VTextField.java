package org.vaadin.firitin.components.textfield;

import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentFocusable;
import org.vaadin.firitin.fluency.ui.FluentHasSize;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;
import org.vaadin.firitin.fluency.ui.FluentHasValidation;
import org.vaadin.firitin.fluency.ui.FluentHasValue;
import org.vaadin.firitin.fluency.ui.internal.FluentHasAutofocus;
import org.vaadin.firitin.fluency.ui.internal.FluentHasLabel;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.textfield.TextField;

public class VTextField extends TextField
        implements FluentHasSize<VTextField>, FluentHasValidation<VTextField>, FluentFocusable<TextField, VTextField>,
        FluentHasValue<VTextField, ComponentValueChangeEvent<TextField, String>, String>, FluentComponent<VTextField>,
        FluentHasLabel<VTextField>, FluentHasAutofocus<VTextField>, FluentHasStyle<VTextField> {

    public VTextField() {
        super();
    }

    public VTextField(String label) {
        super(label);
    }

    public VTextField(String label, String placeholder) {
        super(label, placeholder);
    }

    public VTextField(String label, String initialValue, String placeholder) {
        super(label, initialValue, placeholder);
    }

    public VTextField(ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener) {
        super(listener);
    }

    public VTextField(String label,
            ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener) {
        super(label, listener);
    }

    public VTextField(String label, String initialValue,
            ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener) {
        super(label, initialValue, listener);
    }

    public VTextField withRequired(boolean required) {
        setRequired(required);
        return this;
    }

    public VTextField withPlaceholder(String placeholder) {
        setPlaceholder(placeholder);
        return this;
    }

    public VTextField withPattern(String pattern) {
        setPattern(pattern);
        return this;
    }

    @Override
    public void setValue(String value) {
        // avoid NPE's with null values and just show empty fields
        // like all users would expect
        if (value == null) {
            value = "";
        }
        super.setValue(value);
    }

}
