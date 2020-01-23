package org.vaadin.firitin.components.timepicker;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.timepicker.TimePicker;
import org.vaadin.firitin.fluency.ui.*;
import org.vaadin.firitin.fluency.ui.internal.FluentHasLabel;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Locale;


public class VTimePicker extends TimePicker implements FluentHasSize<VTimePicker>, FluentHasValidation<VTimePicker>, FluentHasEnabled<VTimePicker>, FluentComponent<VTimePicker>,
        FluentHasValueAndElement<VTimePicker, ComponentValueChangeEvent<TimePicker, LocalTime>, LocalTime>, FluentHasLabel<VTimePicker>, FluentFocusable<TimePicker, VTimePicker> {


    public VTimePicker() {
        super();
    }

    public VTimePicker(LocalTime time) {
        super(time);
    }

    public VTimePicker(String label) {
        super(label);
    }

    public VTimePicker(String label, LocalTime time) {
        super(label, time);
    }

    public VTimePicker(ValueChangeListener<ComponentValueChangeEvent<TimePicker, LocalTime>> listener) {
        super(listener);
    }

    public VTimePicker withErrorMessage(String errorMessage) {
        setErrorMessage(errorMessage);
        return this;
    }


    public VTimePicker withInvalid(boolean invalid) {
        setInvalid(invalid);
        return this;
    }


    public VTimePicker withPlaceholder(String placeholder) {
        setPlaceholder(placeholder);
        return this;
    }


    public VTimePicker withRequired(boolean required) {
        setRequired(required);
        return this;
    }


    public VTimePicker withStep(Duration step) {
        setStep(step);
        return this;
    }

    public VTimePicker withInvalidChangeListener(ComponentEventListener<InvalidChangeEvent<TimePicker>> listener) {
        addInvalidChangeListener(listener);
        return this;
    }


    public VTimePicker withLocale(Locale locale) {
        setLocale(locale);
        return this;
    }

    public VTimePicker withMin(String min) {
        setMin(min);
        return this;
    }

    public VTimePicker withMax(String max) {
        setMax(max);
        return this;
    }

}
