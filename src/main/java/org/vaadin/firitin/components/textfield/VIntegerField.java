package org.vaadin.firitin.components.textfield;

import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class VIntegerField extends IntegerField implements FluentVaadinTextField<VIntegerField, IntegerField, Integer> {

    public VIntegerField() {
        super();
        initializeSettings();
    }

    public VIntegerField(String label) {
        super(label);
        initializeSettings();
    }

    public VIntegerField(String label, String placeholder) {
        super(label, placeholder);
        initializeSettings();
    }

    public VIntegerField(ValueChangeListener<? super ComponentValueChangeEvent<IntegerField, Integer>> listener) {
        super(listener);
        initializeSettings();
    }

    public VIntegerField(String label, ValueChangeListener<? super ComponentValueChangeEvent<IntegerField, Integer>> listener) {
        super(label, listener);
        initializeSettings();
    }

    public VIntegerField(String label, Integer initialValue, ValueChangeListener<? super ComponentValueChangeEvent<IntegerField, Integer>> listener) {
        super(label, initialValue, listener);
        initializeSettings();
    }

    protected void initializeSettings() {
        // make validations work properly
        setValueChangeMode(ValueChangeMode.LAZY);
    }

    public VIntegerField withValueChangeMode(ValueChangeMode valueChangeMode) {
        setValueChangeMode(valueChangeMode);
        return this;
    }

    public VIntegerField withHasControls(boolean hasControls) {
        setStepButtonsVisible(hasControls);
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
