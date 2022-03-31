package org.vaadin.firitin.fields;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.shared.Registration;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.datetimepicker.VDateTimePicker;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MultiDateTimeField extends Composite<VerticalLayout>
        implements HasValue<HasValue.ValueChangeEvent<List<LocalDateTime>>, List<LocalDateTime>>, HasSize {

    private List<LocalDateTime> value;

    private enum RepeatMode {
        daily, weekly
    }

    DateTimePicker next = new VDateTimePicker();
    Button addEntry = new Button(VaadinIcon.PLUS.create());
    Checkbox repeat = new Checkbox("Repeat");
    Select<RepeatMode> repeatMode = new Select<>();
    IntegerField repeatTimes = new IntegerField();
    Span repeatTimesText = new Span("times");

    VerticalLayout existingValues = new VVerticalLayout()
            .withMargin(false)
            .withPadding(false)
            .withSpacing(false);

    public MultiDateTimeField() {
        value = new ArrayList<>();

        next.setWidth("260px");
        next.setLabel("Add new date (+)");
        addEntry.getElement().setAttribute("title", "Click to add new date(s) to selection");
        HorizontalLayout newRowForm = new VHorizontalLayout(
                next, repeat,
                repeatTimes, repeatTimesText,
                repeatMode, addEntry).withDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        //newRowForm.getStyle().set("border-top", "1px solid #ddd");
        getContent().setPadding(false);
        getContent().add(
                existingValues,
                newRowForm);

        next.setValue(LocalDateTime.now().withMinute(0).withNano(0));
        repeatMode.setItems(RepeatMode.values());
        repeatMode.setValue(RepeatMode.weekly);
        repeatTimes.setMin(1);
        repeatTimes.setMax(52);
        repeatTimes.setValue(1);
        //repeatTimes.setWidth("3em");
        repeat.addValueChangeListener(e -> {
            Stream.of(repeatTimes, repeatTimesText, repeatMode)
                    .forEach(c -> c.setVisible(repeat.getValue()));
        });
        repeat.setValue(true);
        repeat.setValue(false);
        addEntry.addClickListener(e -> {
            LocalDateTime newValue = next.getValue();
            value.add(newValue);
            addRow(newValue);
            if (repeat.getValue()) {
                for (int i = 0; i < repeatTimes.getValue(); i++) {
                    newValue = newValue.plusDays(repeatMode.getValue() == RepeatMode.daily ? 1 : 7);
                    value.add(newValue);
                    addRow(newValue);
                }
                next.setValue(newValue);
            }
            fireValueChange();
        });

    }

    private void addRow(LocalDateTime newValue) {
        VHorizontalLayout row = new VHorizontalLayout();
        DateTimePicker dateTimePicker = new VDateTimePicker(newValue, event -> {
            LocalDateTime oldValue = event.getOldValue();
            value.set(value.indexOf(oldValue), event.getValue());
            fireValueChange();
        });
        dateTimePicker.setWidth("260px");
        VButton deleteButton = new VButton(VaadinIcon.TRASH.create(), e -> {
            value.remove(dateTimePicker.getValue());
            existingValues.remove(row);
            fireValueChange();
        });
        row.add(dateTimePicker, deleteButton);
        existingValues.add(row);
    }

    private void fireValueChange() {
        fireEvent(new AbstractField.ComponentValueChangeEvent<MultiDateTimeField, List<LocalDateTime>>(this, this, null, true));
    }

    @Override
    public void setValue(List<LocalDateTime> value) {
        this.value = value;
        existingValues.removeAll();
        value.forEach(this::addRow);
    }

    @Override
    public List<LocalDateTime> getValue() {
        return value;
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<? super ValueChangeEvent<List<LocalDateTime>>> listener) {
        @SuppressWarnings("rawtypes")
        ComponentEventListener componentListener = event -> {
            AbstractField.ComponentValueChangeEvent<MultiDateTimeField, List<LocalDateTime>> valueChangeEvent = (AbstractField.ComponentValueChangeEvent<MultiDateTimeField, List<LocalDateTime>>) event;
            listener.valueChanged(valueChangeEvent);
        };
        return addListener(AbstractField.ComponentValueChangeEvent.class,
                componentListener);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        getContent().setEnabled(false);
    }

    @Override
    public boolean isReadOnly() {
        return getContent().isEnabled();
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {

    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return false;
    }
}
