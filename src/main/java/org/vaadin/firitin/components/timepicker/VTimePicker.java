package org.vaadin.firitin.components.timepicker;

import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.vaadin.firitin.components.internal.PickerValueChangeModeSupport;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.timepicker.TimePicker;
import org.vaadin.firitin.components.datetimepicker.VDateTimePicker;
import org.vaadin.firitin.fluency.ui.*;
import org.vaadin.firitin.fluency.ui.internal.FluentHasLabel;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Locale;


public class VTimePicker extends TimePicker implements FluentHasSize<VTimePicker>, FluentHasValidation<VTimePicker>, FluentHasEnabled<VTimePicker>, FluentComponent<VTimePicker>,
        FluentThemableLayout<VDateTimePicker>, FluentHasValueAndElement<VTimePicker, ComponentValueChangeEvent<TimePicker, LocalTime>, LocalTime>, FluentHasLabel<VTimePicker>, FluentFocusable<TimePicker, VTimePicker>,
        FluentHasHelper<VTimePicker>, HasValueChangeMode {

    private final PickerValueChangeModeSupport valueChangeModeSupport = new PickerValueChangeModeSupport(this);



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

    public VTimePicker withInvalidChangeListener(ComponentEventListener<InvalidChangeEvent> listener) {
        addInvalidChangeListener(listener);
        return this;
    }


    public VTimePicker withLocale(Locale locale) {
        setLocale(locale);
        return this;
    }

    public VTimePicker withMinTime(LocalTime min) {
        setMin(min);
        return this;
    }

    public VTimePicker withMaxTime(LocalTime max) {
        setMax(max);
        return this;
    }

    public VTimePicker withClearButtonVisible(boolean clearButtonVisible) {
        setClearButtonVisible(clearButtonVisible);
        return this;
    }

    public VTimePicker withRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
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

    public VTimePicker withValueChangeMode(ValueChangeMode valueChangeMode) {
        setValueChangeMode(valueChangeMode);
        return this;
    }

    public VTimePicker withValueChangeTimeout(int valueChangeTimeout) {
        setValueChangeTimeout(valueChangeTimeout);
        return this;
    }

}
