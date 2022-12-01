package org.vaadin.firitin.components.textfield;

import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.math.BigDecimal;
import java.util.Locale;

public class VBigDecimalField extends BigDecimalField implements FluentVaadinTextField<VBigDecimalField, BigDecimalField, BigDecimal> {

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

    public VBigDecimalField withValueChangeTimeout(int valueChangeTimeout) {
        setValueChangeTimeout(valueChangeTimeout);
        return this;
    }

    public VBigDecimalField withPlaceholder(String placeholder) {
        setPlaceholder(placeholder);
        return this;
    }

    public VBigDecimalField withAutoselect(boolean autoselect) {
        setAutoselect(autoselect);
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

    public VBigDecimalField withLocale(Locale locale) {
        setLocale(locale);
        return this;
    }

    public VBigDecimalField withRequired(boolean required) {
        setRequired(required);
        return this;
    }
}
