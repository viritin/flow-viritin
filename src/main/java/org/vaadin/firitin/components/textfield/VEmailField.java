package org.vaadin.firitin.components.textfield;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.vaadin.firitin.fluency.ui.*;
import org.vaadin.firitin.fluency.ui.internal.FluentHasAutofocus;
import org.vaadin.firitin.fluency.ui.internal.FluentHasLabel;

public class VEmailField extends EmailField implements FluentHasSize<VEmailField>, FluentHasValidation<VEmailField>, FluentFocusable<EmailField, VEmailField>,
        FluentHasValue<VEmailField, ComponentValueChangeEvent<EmailField, String>, String>, FluentComponent<VEmailField>,
        FluentHasLabel<VEmailField>, FluentHasAutofocus<VEmailField>, FluentHasStyle<VEmailField> {


    public VEmailField() {
        super();
    }

    public VEmailField(String label) {
        super(label);
    }

    public VEmailField(String label, String placeholder) {
        super(label, placeholder);
    }

    public VEmailField(ValueChangeListener<? super ComponentValueChangeEvent<EmailField, String>> listener) {
        super(listener);
    }

    public VEmailField(String label, ValueChangeListener<? super ComponentValueChangeEvent<EmailField, String>> listener) {
        super(label, listener);
    }

    public VEmailField(String label, String initialValue, ValueChangeListener<? super ComponentValueChangeEvent<EmailField, String>> listener) {
        super(label, initialValue, listener);
    }


    public VEmailField withValueChangeMode(ValueChangeMode valueChangeMode) {
        setValueChangeMode(valueChangeMode);
        return this;
    }


    public VEmailField withErrorMessage(String errorMessage) {
        setErrorMessage(errorMessage);
        return this;
    }


    public VEmailField withInvalid(boolean invalid) {
        setInvalid(invalid);
        return this;
    }


    public VEmailField withPlaceholder(String placeholder) {
        setPlaceholder(placeholder);
        return this;
    }


    public VEmailField withMaxLength(int maxLength) {
        setMaxLength(maxLength);
        return this;
    }


    public VEmailField withMinLength(int minLength) {
        setMinLength(minLength);
        return this;
    }

    public VEmailField withPreventInvalidInput(boolean preventInvalidInput) {
        setPreventInvalidInput(preventInvalidInput);
        return this;
    }

    public VEmailField withPattern(String pattern) {
        setPattern(pattern);
        return this;
    }


    public VEmailField withTitle(String title) {
        setTitle(title);
        return this;
    }


    public VEmailField withAutoselect(boolean autoselect) {
        setAutoselect(autoselect);
        return this;
    }


    public VEmailField withClearButtonVisible(boolean clearButtonVisible) {
        setClearButtonVisible(clearButtonVisible);
        return this;
    }
}
