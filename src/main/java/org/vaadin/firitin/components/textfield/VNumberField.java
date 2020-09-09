package org.vaadin.firitin.components.textfield;

import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class VNumberField extends NumberField implements FluentVaadinTextField<VNumberField, NumberField, Double> {

    public VNumberField() {
        super();
    }

    public VNumberField(String label) {
        super(label);
    }

    public VNumberField(String label, String placeholder) {
        super(label, placeholder);
    }

    public VNumberField(ValueChangeListener<? super ComponentValueChangeEvent<NumberField, Double>> listener) {
        super(listener);
    }

    public VNumberField(String label, ValueChangeListener<? super ComponentValueChangeEvent<NumberField, Double>> listener) {
        super(label, listener);
    }

    public VNumberField(String label, Double initialValue, ValueChangeListener<? super ComponentValueChangeEvent<NumberField, Double>> listener) {
        super(label, initialValue, listener);
    }

    public VNumberField withValueChangeMode(ValueChangeMode valueChangeMode) {
        setValueChangeMode(valueChangeMode);
        return this;
    }

    public VNumberField withMax(double max) {
        setMax(max);
        return this;
    }

    public VNumberField withMin(double min) {
        setMin(min);
        return this;
    }

    public VNumberField withStep(double step) {
        setStep(step);
        return this;
    }

    public VNumberField withPlaceholder(String placeholder) {
        setPlaceholder(placeholder);
        return this;
    }

    public VNumberField withHasControls(boolean hasControls) {
        setHasControls(hasControls);
        return this;
    }

    public VNumberField withAutofocus(boolean autofocus) {
        setAutofocus(autofocus);
        return this;
    }

    public VNumberField withRequired(boolean required) {
        setRequired(required);
        return this;
    }

    @Deprecated
    public VNumberField withMaxLength(int maxLength) {
        setMaxLength(maxLength);
        return this;
    }


    @Deprecated
    public VNumberField withMinLength(int minLength) {
        setMinLength(minLength);
        return this;
    }

    @Deprecated
    public VNumberField withPreventInvalidInput(boolean preventInvalidInput) {
        setPreventInvalidInput(preventInvalidInput);
        return this;
    }

    @Deprecated
    public VNumberField withPattern(String pattern) {
        setPattern(pattern);
        return this;
    }

    public VNumberField withTitle(String title) {
        setTitle(title);
        return this;
    }

    public VNumberField withClearButtonVisible(boolean clearButtonVisible) {
        setClearButtonVisible(clearButtonVisible);
        return this;
    }
}
