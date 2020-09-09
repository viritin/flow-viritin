package org.vaadin.firitin.components.datepicker;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.datepicker.DatePicker;
import org.vaadin.firitin.components.datetimepicker.VDateTimePicker;
import org.vaadin.firitin.fluency.ui.*;

import java.time.LocalDate;
import java.util.Locale;

public class VDatePicker extends DatePicker implements FluentHasSize<VDatePicker>, FluentHasValidation<VDatePicker>,
        FluentHasStyle<VDatePicker>, FluentFocusable<DatePicker, VDatePicker>, FluentComponent<VDatePicker>,
        FluentThemableLayout<VDateTimePicker>, FluentHasValueAndElement<VDatePicker, AbstractField.ComponentValueChangeEvent<DatePicker, LocalDate>, LocalDate> {
    public VDatePicker() {
        super();
    }

    public VDatePicker(LocalDate initialDate) {
        super(initialDate);
    }

    public VDatePicker(String label) {
        super(label);
    }

    public VDatePicker(String label, LocalDate initialDate) {
        super(label, initialDate);
    }

    public VDatePicker(ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
        super(listener);
    }

    public VDatePicker(String label, ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
        super(label, listener);
    }

    public VDatePicker(LocalDate initialDate,
                       ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
        super(initialDate, listener);
    }

    public VDatePicker(String label, LocalDate initialDate,
                       ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
        super(label, initialDate, listener);
    }

    public VDatePicker(LocalDate initialDate, Locale locale) {
        super(initialDate, locale);
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
}
