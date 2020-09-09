package org.vaadin.firitin.components.textfield;

import com.vaadin.flow.component.textfield.TextArea;

public class VTextArea extends TextArea implements FluentVaadinTextField<VTextArea, TextArea, String> {

    public VTextArea() {
        super();
    }

    public VTextArea(String label) {
        super(label);
    }

    public VTextArea(String label, String placeholder) {
        super(label, placeholder);
    }

    public VTextArea(String label, String initialValue, String placeholder) {
        super(label, initialValue, placeholder);
    }

    public VTextArea(ValueChangeListener<? super ComponentValueChangeEvent<TextArea, String>> listener) {
        super(listener);
    }

    public VTextArea(String label,
                     ValueChangeListener<? super ComponentValueChangeEvent<TextArea, String>> listener) {
        super(label, listener);
    }

    public VTextArea(String label, String initialValue,
                     ValueChangeListener<? super ComponentValueChangeEvent<TextArea, String>> listener) {
        super(label, initialValue, listener);
    }

    public VTextArea withRequired(boolean required) {
        setRequired(required);
        return this;
    }

    public VTextArea withPlaceholder(String placeholder) {
        setPlaceholder(placeholder);
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

    public VTextArea withMaxLength(int maxLength) {
        setMaxLength(maxLength);
        return this;
    }

    public VTextArea withMinLength(int minLength) {
        setMinLength(minLength);
        return this;
    }
}
