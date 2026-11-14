package org.vaadin.firitin.components.datepicker;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.datetimepicker.VDateTimePicker;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.components.timepicker.VTimePicker;

import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Value change events of the date/time pickers should fire while the user is
 * typing (like with VTextField), not only on blur/enter. Otherwise e.g. a save
 * button in a form doesn't get enabled until the user leaves the field.
 */
@Route
public class LazyDateValueChangeView extends VVerticalLayout {

    public LazyDateValueChangeView() {
        VButton save = new VButton("Save").withEnabled(false).withId("save");
        add(
                new FieldWithValue<>(new VTextField("Text"), "text", save),
                new FieldWithValue<>(new VDatePicker("Date").withLocale(Locale.forLanguageTag("fi")), "date", save),
                new FieldWithValue<>(new VDatePicker("Date (ON_CHANGE)").withLocale(Locale.forLanguageTag("fi"))
                        .withValueChangeMode(ValueChangeMode.ON_CHANGE), "date-onchange", save),
                new FieldWithValue<>(new VTimePicker("Time").withLocale(Locale.UK), "time", save),
                new FieldWithValue<>(new VDateTimePicker("Date time")
                        .withLocale(Locale.forLanguageTag("fi")), "datetime", save),
                save
        );
    }

    static class FieldWithValue<F extends AbstractField<?, ?>> extends VVerticalLayout {
        FieldWithValue(F field, String id, VButton save) {
            field.setId(id);
            Paragraph value = new Paragraph("-");
            value.setId(id + "-value");
            Paragraph count = new Paragraph("0");
            count.setId(id + "-count");
            AtomicInteger events = new AtomicInteger();
            field.addValueChangeListener((HasValue.ValueChangeListener) e -> {
                value.setText(String.valueOf(e.getValue()));
                count.setText(String.valueOf(events.incrementAndGet()));
                save.setEnabled(true);
            });
            add(field, value, count);
            setPadding(false);
        }
    }
}
