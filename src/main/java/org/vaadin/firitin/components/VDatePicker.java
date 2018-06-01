package org.vaadin.firitin.components;

import com.vaadin.flow.component.datepicker.DatePicker;
import org.vaadin.firitin.fluency.ui.*;

import java.time.LocalDate;
import java.util.Locale;

public class VDatePicker extends DatePicker implements FluentHasSize<VDatePicker>, FluentHasValidation<VDatePicker>,
        FluentHasStyle<VDatePicker>, FluentFocusable<DatePicker, VDatePicker>, FluentComponent<VDatePicker>, FluentHasElement<VDatePicker>
{
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

    public VDatePicker(LocalDate initialDate, ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
        super(initialDate, listener);
    }

    public VDatePicker(String label, LocalDate initialDate, ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
        super(label, initialDate, listener);
    }

    public VDatePicker(LocalDate initialDate, Locale locale) {
        super(initialDate, locale);
    }

    @Override
    public VDatePicker withId(String id) {
        setId(id);
        return this;
    }
}
