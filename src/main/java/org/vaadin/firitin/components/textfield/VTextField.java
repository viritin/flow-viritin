package org.vaadin.firitin.components.textfield;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;

public class VTextField extends TextField implements FluentVaadinTextField<VTextField, TextField, String> {

    public VTextField() {
        super();
        initializeSettings();
    }

    public VTextField(String label) {
        super(label);
        // make validations work properly
        initializeSettings();
    }

    public VTextField(String label, String placeholder) {
        super(label, placeholder);
        // make validations work properly
        initializeSettings();
    }

    public VTextField(String label, String initialValue, String placeholder) {
        super(label, initialValue, placeholder);
        // make validations work properly
        initializeSettings();
    }

    public VTextField(ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener) {
        super(listener);
        // make validations work properly
        initializeSettings();
    }

    public VTextField(String label,
                      ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener) {
        super(label, listener);
        // make validations work properly
        initializeSettings();
    }

    public VTextField(String label, String initialValue,
                      ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener) {
        super(label, initialValue, listener);
        // make validations work properly
        initializeSettings();
    }

    protected void initializeSettings() {
        // make validations work properly
        setValueChangeMode(ValueChangeMode.LAZY);
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

    public VTextField withThemeVariants(TextFieldVariant... variants) {
        addThemeVariants(variants);
        return this;
    }


    public VTextField withValueChangeMode(ValueChangeMode valueChangeMode) {
        setValueChangeMode(valueChangeMode);
        return this;
    }

    public VTextField withValueChangeTimeout(int valueChangeTimeout) {
        setValueChangeTimeout(valueChangeTimeout);
        return this;
    }

    public VTextField withAutoselect(boolean autoselect) {
        setAutoselect(autoselect);
        return this;
    }

    public VTextField withTitle(String title) {
        setTitle(title);
        return this;
    }

    public VTextField withClearButtonVisible(boolean clearButtonVisible) {
        setClearButtonVisible(clearButtonVisible);
        return this;
    }

    public VTextField withReadOnly(boolean readOnly) {
        setReadOnly(readOnly);
        return this;
    }

    public VTextField withMaxLength(int maxLength) {
        setMaxLength(maxLength);
        return this;
    }

    public VTextField withMinLength(int minLength) {
        setMinLength(minLength);
        return this;
    }

}
