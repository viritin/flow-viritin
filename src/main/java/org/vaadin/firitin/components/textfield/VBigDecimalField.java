package org.vaadin.firitin.components.textfield;

import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.vaadin.firitin.fluency.ui.*;
import org.vaadin.firitin.fluency.ui.internal.FluentHasAutofocus;
import org.vaadin.firitin.fluency.ui.internal.FluentHasLabel;

import java.math.BigDecimal;

public class VBigDecimalField extends BigDecimalField implements FluentHasSize<VBigDecimalField>, FluentHasValidation<VBigDecimalField>, FluentFocusable<BigDecimalField, VBigDecimalField>,
        FluentHasLabel<VBigDecimalField>, FluentHasAutofocus<VBigDecimalField>, FluentHasStyle<VBigDecimalField>, FluentHasPrefixAndSuffix<VBigDecimalField> {

    public VBigDecimalField() {
        super();
    }

    public VBigDecimalField(String label) {
        super(label);
    }

    public VBigDecimalField(String label, String placeholder) {
        super(label, placeholder);
    }

    public VBigDecimalField(ValueChangeListener<? super ComponentValueChangeEvent<BigDecimalField, BigDecimal>> listener) {
        super(listener);
    }

    public VBigDecimalField(String label, ValueChangeListener<? super ComponentValueChangeEvent<BigDecimalField, BigDecimal>> listener) {
        super(label, listener);
    }

    public VBigDecimalField(String label, BigDecimal initialValue, ValueChangeListener<? super ComponentValueChangeEvent<BigDecimalField, BigDecimal>> listener) {
        super(label, initialValue, listener);
    }

    public VBigDecimalField withValueChangeMode(ValueChangeMode valueChangeMode) {
        setValueChangeMode(valueChangeMode);
        return this;
    }

    public VBigDecimalField withErrorMessage(String errorMessage) {
        setErrorMessage(errorMessage);
        return this;
    }

    public VBigDecimalField withInvalid(boolean invalid) {
        setInvalid(invalid);
        return this;
    }

    public VBigDecimalField withPlaceholder(String placeholder) {
        setPlaceholder(placeholder);
        return this;
    }

    public VBigDecimalField withAutofocus(boolean autofocus) {
        setAutofocus(autofocus);
        return this;
    }

    public VBigDecimalField withPreventInvalidInput(boolean preventInvalidInput) {
        setPreventInvalidInput(preventInvalidInput);
        return this;
    }

    public VBigDecimalField withPattern(String pattern) {
        setPattern(pattern);
        return this;
    }

    public VBigDecimalField withTitle(String title) {
        setTitle(title);
        return this;
    }

    public VBigDecimalField withClearButtonVisible(boolean clearButtonVisible) {
        setClearButtonVisible(clearButtonVisible);
        return this;
    }

    public VBigDecimalField withReadOnly(boolean readOnly) {
        setReadOnly(readOnly);
        return this;
    }

    public VBigDecimalField withRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        setRequiredIndicatorVisible(requiredIndicatorVisible);
        return this;
    }

    public VBigDecimalField withValue(BigDecimal value) {
        setValue(value);
        return this;
    }

    public VBigDecimalField withValueChangeListener(ValueChangeListener<? super ValueChangeEvent<BigDecimal>> listener) {
        addValueChangeListener(listener);
        return this;
    }
}
