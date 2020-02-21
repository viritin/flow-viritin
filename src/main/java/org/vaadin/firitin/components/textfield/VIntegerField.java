package org.vaadin.firitin.components.textfield;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.vaadin.firitin.fluency.ui.*;
import org.vaadin.firitin.fluency.ui.internal.FluentHasAutofocus;
import org.vaadin.firitin.fluency.ui.internal.FluentHasLabel;

public class VIntegerField extends IntegerField implements FluentHasSize<VIntegerField>, FluentHasValidation<VIntegerField>, FluentFocusable<IntegerField, VIntegerField>,
        FluentHasValue<VIntegerField, AbstractField.ComponentValueChangeEvent<IntegerField, Integer>, Integer>, FluentComponent<VIntegerField>,
        FluentHasLabel<VIntegerField>, FluentHasAutofocus<VIntegerField>, FluentHasStyle<VIntegerField>, FluentHasPrefixAndSuffix<VIntegerField> {

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

    public VIntegerField withErrorMessage(String errorMessage) {
        setErrorMessage(errorMessage);
        return this;
    }

    public VIntegerField withInvalid(boolean invalid) {
        setInvalid(invalid);
        return this;
    }

    public VIntegerField withMax(double max) {
        setMax(max);
        return this;
    }

    public VIntegerField withMin(double min) {
        setMin(min);
        return this;
    }

    public VIntegerField withStep(double step) {
        setStep(step);
        return this;
    }

    public VIntegerField withPlaceholder(String placeholder) {
        setPlaceholder(placeholder);
        return this;
    }

    public VIntegerField withHasControls(boolean hasControls) {
        setHasControls(hasControls);
        return this;
    }

    public VIntegerField withAutofocus(boolean autofocus) {
        setAutofocus(autofocus);
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

    public VIntegerField withAutoselect(boolean autoselect) {
        setAutoselect(autoselect);
        return this;
    }

    public VIntegerField withClearButtonVisible(boolean clearButtonVisible) {
        setClearButtonVisible(clearButtonVisible);
        return this;
    }
}
