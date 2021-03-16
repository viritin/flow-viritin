package org.vaadin.firitin.components.textfield;

import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class VPasswordField extends PasswordField implements FluentVaadinTextField<VPasswordField, PasswordField, String> {

    public VPasswordField() {
        super();
    }

    public VPasswordField(String label) {
        super(label);
    }

    public VPasswordField(String label, String placeholder) {
        super(label, placeholder);
    }

    public VPasswordField(ValueChangeListener<? super ComponentValueChangeEvent<PasswordField, String>> listener) {
        super(listener);
    }

    public VPasswordField(String label, ValueChangeListener<? super ComponentValueChangeEvent<PasswordField, String>> listener) {
        super(label, listener);
    }

    public VPasswordField(String label, String initialValue, ValueChangeListener<? super ComponentValueChangeEvent<PasswordField, String>> listener) {
        super(label, listener);
    }

    public VPasswordField withValueChangeMode(ValueChangeMode valueChangeMode) {
        setValueChangeMode(valueChangeMode);
        return this;
    }


    public VPasswordField withPlaceholder(String placeholder) {
        setPlaceholder(placeholder);
        return this;
    }

    public VPasswordField withAutofocus(boolean autofocus) {
        setAutofocus(autofocus);
        return this;
    }

    public VPasswordField withMaxLength(int maxLength) {
        setMaxLength(maxLength);
        return this;
    }

    public VPasswordField withMinLength(int minLength) {
        setMinLength(minLength);
        return this;
    }

    public VPasswordField withRequired(boolean required) {
        setRequired(required);
        return this;
    }


    public VPasswordField withPreventInvalidInput(boolean preventInvalidInput) {
        setPreventInvalidInput(preventInvalidInput);
        return this;
    }

    public VPasswordField withPattern(String pattern) {
        setPattern(pattern);
        return this;
    }

    public VPasswordField withTitle(String title) {
        setTitle(title);
        return this;
    }

    public VPasswordField withRevealButtonVisible(boolean revealButtonVisible) {
        setRevealButtonVisible(revealButtonVisible);
        return this;
    }

    public VPasswordField withAutoselect(boolean autoselect) {
        setAutoselect(autoselect);
        return this;
    }

    public VPasswordField withClearButtonVisible(boolean clearButtonVisible) {
        setClearButtonVisible(clearButtonVisible);
        return this;
    }
}
