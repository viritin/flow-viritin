package org.vaadin.firitin.components.textfield;

import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class VIntegerField extends IntegerField implements FluentVaadinTextField<VIntegerField, IntegerField, Integer> {

    public VIntegerField() {
        super();
    }

    public VIntegerField(String label) {
        super(label);
    }

    public VIntegerField(String label, String placeholder) {
        super(label, placeholder);
    }

    public VIntegerField(ValueChangeListener<? super ComponentValueChangeEvent<IntegerField, Integer>> listener) {
        super(listener);
    }

    public VIntegerField(String label, ValueChangeListener<? super ComponentValueChangeEvent<IntegerField, Integer>> listener) {
        super(label, listener);
    }

    public VIntegerField(String label, Integer initialValue, ValueChangeListener<? super ComponentValueChangeEvent<IntegerField, Integer>> listener) {
        super(label, initialValue, listener);
    }

    public VIntegerField withValueChangeMode(ValueChangeMode valueChangeMode) {
        setValueChangeMode(valueChangeMode);
        return this;
    }

    public VIntegerField withHasControls(boolean hasControls) {
        setHasControls(hasControls);
        return this;
    }

    public VIntegerField withValueChangeTimeout(int valueChangeTimeout) {
        setValueChangeTimeout(valueChangeTimeout);
        return this;
    }

    public VIntegerField withPlaceholder(String placeholder) {
        setPlaceholder(placeholder);
        return this;
    }

    public VIntegerField withAutoselect(boolean autoselect) {
        setAutoselect(autoselect);
        return this;
    }

    public VIntegerField withPreventInvalidInput(boolean preventInvalidInput) {
        setPreventInvalidInput(preventInvalidInput);
        return this;
    }

    public VIntegerField withPattern(String pattern) {
        setPattern(pattern);
        return this;
    }

    public VIntegerField withTitle(String title) {
        setTitle(title);
        return this;
    }

    public VIntegerField withRequired(boolean required) {
        setRequired(required);
        return this;
    }

    public VIntegerField withClearButtonVisible(boolean clearButtonVisible) {
        setClearButtonVisible(clearButtonVisible);
        return this;
    }
}
