package org.vaadin.firitin.components.datepicker;

import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.vaadin.firitin.components.internal.PickerValueChangeModeSupport;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.datepicker.DatePicker;
import org.vaadin.firitin.components.datetimepicker.VDateTimePicker;
import org.vaadin.firitin.components.textfield.SelectionApi;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentFocusable;
import org.vaadin.firitin.fluency.ui.FluentHasHelper;
import org.vaadin.firitin.fluency.ui.FluentHasSize;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;
import org.vaadin.firitin.fluency.ui.FluentHasTooltip;
import org.vaadin.firitin.fluency.ui.FluentHasValidation;
import org.vaadin.firitin.fluency.ui.FluentHasValueAndElement;
import org.vaadin.firitin.fluency.ui.FluentThemableLayout;

import java.time.LocalDate;
import java.util.Locale;

public class VDatePicker extends DatePicker implements FluentHasSize<VDatePicker>, FluentHasValidation<VDatePicker>,
        FluentHasStyle<VDatePicker>, FluentFocusable<DatePicker, VDatePicker>, FluentComponent<VDatePicker>,
        FluentThemableLayout<VDateTimePicker>, FluentHasValueAndElement<VDatePicker, AbstractField.ComponentValueChangeEvent<DatePicker, LocalDate>, LocalDate>,
        FluentHasHelper<VDatePicker>, FluentHasTooltip<VDatePicker>, SelectionApi, HasValueChangeMode {

    private final PickerValueChangeModeSupport valueChangeModeSupport = new PickerValueChangeModeSupport(this);


    public VDatePicker() {
        super();
        initI18n();
    }

    public VDatePicker(LocalDate initialDate) {
        super(initialDate);
        initI18n();
    }

    public VDatePicker(String label) {
        super(label);
        initI18n();
    }

    public VDatePicker(String label, LocalDate initialDate) {
        super(label, initialDate);
        initI18n();
    }

    public VDatePicker(ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
        super(listener);
        initI18n();
    }

    public VDatePicker(String label, ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
        super(label, listener);
        initI18n();
    }

    public VDatePicker(LocalDate initialDate,
                       ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
        super(initialDate, listener);
        initI18n();
    }

    public VDatePicker(String label, LocalDate initialDate,
                       ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
        super(label, initialDate, listener);
        initI18n();
    }

    public VDatePicker(LocalDate initialDate, Locale locale) {
        super(initialDate, locale);
        initI18n();
    }

    @Override
    public void setLocale(Locale locale) {
        super.setLocale(locale);
        initI18n();
    }

    /**
     * Makes a "proper" initialization based on set locale.
     */
    protected void initI18n() {
        Locale l = getLocale();
        if(l != null) {
            setI18n(VDateTimePicker.createI18nFromLocale(l));
        }

    }

    public VDatePicker withMin(LocalDate min) {
        setMin(min);
        return this;
    }

    public VDatePicker withMax(LocalDate max) {
        setMax(max);
        return this;
    }

    public VDatePicker withRange(LocalDate min, LocalDate max) {
        setMin(min);
        setMax(max);
        return this;
    }

    public VDatePicker withLocale(Locale locale) {
        setLocale(locale);
        return this;
    }

    public VDatePicker withI18n(DatePicker.DatePickerI18n i18n) {
        setI18n(i18n);
        return this;
    }

    public VDatePicker withClearButtonVisible(boolean clearButtonVisible) {
        setClearButtonVisible(clearButtonVisible);
        return this;
    }

    public VDatePicker withWeekNumbersVisible(boolean weekNumbersVisible) {
        setWeekNumbersVisible(weekNumbersVisible);
        return this;
    }

    public VDatePicker withOpened(boolean opened) {
        setOpened(opened);
        return this;
    }

    public VDatePicker withName(String name) {
        setName(name);
        return this;
    }

    public VDatePicker withRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        setRequiredIndicatorVisible(requiredIndicatorVisible);
        return this;
    }

    /**
     * Returns the value change mode. Defaults to {@link ValueChangeMode#LAZY}
     * in Viritin, so that value change events are fired while the user types
     * (when the typed text is a complete, valid value), like with VTextField.
     *
     * @return the value change mode
     */
    @Override
    public ValueChangeMode getValueChangeMode() {
        return valueChangeModeSupport.getValueChangeMode();
    }

    /**
     * Sets the value change mode. {@link ValueChangeMode#ON_CHANGE} and
     * {@link ValueChangeMode#ON_BLUR} restore the default behaviour of the
     * web component (value committed on enter or blur). With
     * {@link ValueChangeMode#LAZY}, {@link ValueChangeMode#TIMEOUT} and
     * {@link ValueChangeMode#EAGER} the value is committed also while
     * typing, if the typed text is a complete value (it parses and formats back
     * to the exact same text).
     *
     * @param valueChangeMode the new value change mode
     */
    @Override
    public void setValueChangeMode(ValueChangeMode valueChangeMode) {
        valueChangeModeSupport.setValueChangeMode(valueChangeMode);
    }

    @Override
    public void setValueChangeTimeout(int valueChangeTimeout) {
        valueChangeModeSupport.setValueChangeTimeout(valueChangeTimeout);
    }

    @Override
    public int getValueChangeTimeout() {
        return valueChangeModeSupport.getValueChangeTimeout();
    }

    public VDatePicker withValueChangeMode(ValueChangeMode valueChangeMode) {
        setValueChangeMode(valueChangeMode);
        return this;
    }

    public VDatePicker withValueChangeTimeout(int valueChangeTimeout) {
        setValueChangeTimeout(valueChangeTimeout);
        return this;
    }

}
