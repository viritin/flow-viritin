package org.vaadin.firitin.components.datetimepicker;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import org.vaadin.firitin.fluency.ui.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;

public class VDateTimePicker extends DateTimePicker implements FluentHasSize<VDateTimePicker>, FluentHasValidation<VDateTimePicker>,
        FluentHasStyle<VDateTimePicker>, FluentFocusable<DateTimePicker, VDateTimePicker>, FluentComponent<VDateTimePicker>,
        FluentThemableLayout<VDateTimePicker>, FluentHasValueAndElement<VDateTimePicker, AbstractField.ComponentValueChangeEvent<DateTimePicker, LocalDateTime>, LocalDateTime> {

    public VDateTimePicker(String label) {
        super(label);
    }

    public VDateTimePicker(String label, LocalDateTime initialDateTime) {
        super(label, initialDateTime);
    }

    public VDateTimePicker(LocalDateTime initialDateTime) {
        super(initialDateTime);
    }

    public VDateTimePicker(ValueChangeListener<ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> listener) {
        super(listener);
    }

    public VDateTimePicker(String label, ValueChangeListener<ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> listener) {
        super(label, listener);
    }

    public VDateTimePicker(LocalDateTime initialDateTime, ValueChangeListener<ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> listener) {
        super(initialDateTime, listener);
    }

    public VDateTimePicker(String label, LocalDateTime initialDateTime, ValueChangeListener<ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> listener) {
        super(label, initialDateTime, listener);
    }

    public VDateTimePicker(LocalDateTime initialDateTime, Locale locale) {
        super(initialDateTime, locale);
    }

    public VDateTimePicker withDatePlaceholder(String placeholder) {
        setDatePlaceholder(placeholder);
        return this;
    }

    public VDateTimePicker withTimePlaceholder(String placeholder) {
        setTimePlaceholder(placeholder);
        return this;
    }

    public VDateTimePicker withStep(Duration step) {
        setStep(step);
        return this;
    }

    public VDateTimePicker withWeekNumbersVisible(boolean weekNumbersVisible) {
        setWeekNumbersVisible(weekNumbersVisible);
        return this;
    }

    public VDateTimePicker withLocale(Locale locale) {
        setLocale(locale);
        return this;
    }

    public VDateTimePicker withMin(LocalDateTime min) {
        setMin(min);
        return this;
    }

    public VDateTimePicker withMax(LocalDateTime max) {
        setMax(max);
        return this;
    }

    public VDateTimePicker withDatePickerI18n(DatePicker.DatePickerI18n i18n) {
        setDatePickerI18n(i18n);
        return this;
    }

    public VDateTimePicker withRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        setRequiredIndicatorVisible(requiredIndicatorVisible);
        return this;
    }

}
