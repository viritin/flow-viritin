package org.vaadin.firitin.fields;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.shared.Registration;
import org.vaadin.firitin.components.button.DefaultButton;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.textfield.VIntegerField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MultiDateTimeField extends Composite<VerticalLayout>
        implements HasValue<HasValue.ValueChangeEvent<List<LocalDateTime>>, List<LocalDateTime>>, HasSize {

    public class CustomDateTimePicker extends CustomField<LocalDateTime> {

        private DatePicker dp = new DatePicker();
        private TimePicker tp = new TimePicker();
        private Checkbox wholeDay = new Checkbox("All day");

        {
            dp.setWidth("140px");
            tp.setWidth("100px");

            wholeDay.setValue(true);
            tp.setEnabled(false);

            wholeDay.addValueChangeListener(e -> {
                tp.setEnabled(!wholeDay.getValue());
                if(wholeDay.getValue()) {
                    tp.setValue(null);
                } else if (tp.getValue() == null) {
                    tp.setValue(LocalTime.now().withMinute(0).withSecond(0));
                }
            });
            dp.addValueChangeListener(e-> {
                if(e.isFromClient()) {
                    updateValue();
                }
            });
            tp.addValueChangeListener(e-> {
                if(e.isFromClient()) {
                    updateValue();
                }
            });
        }

        @Override
        protected LocalDateTime generateModelValue() {
            LocalDate localDate = dp.getValue();
            LocalTime localTime = tp.getValue();
            if(localDate == null) {
                return null;
            }
            if(wholeDay.getValue() || localTime == null) {
                localTime = LocalTime.of(0,0,0,0);
            }
            return LocalDateTime.of(localDate,localTime);
        }

        @Override
        protected void setPresentationValue(LocalDateTime localDateTime) {
            if(localDateTime == null) {
                dp.setValue(null);
                tp.setValue(null);
            } else {
                dp.setValue(localDateTime.toLocalDate());
                tp.setValue(localDateTime.toLocalTime());
            }
            boolean atMidnight = localDateTime == null || (localDateTime.getHour() == 0 && localDateTime.getMinute() == 0);
            wholeDay.setValue(atMidnight);
        }

        @Override
        public void clear() {
            dp.setValue(null);
        }

        public CustomDateTimePicker() {
            add(new VHorizontalLayout(dp,tp,wholeDay)
                    .withMargin(false)
                    .withPadding(false)
                    .alignAll(FlexComponent.Alignment.CENTER)
            );
        }

        private void fireValueChange() {
            fireEvent(new AbstractField.ComponentValueChangeEvent<CustomDateTimePicker, LocalDateTime>(this, this, null, true));
        }

    }


    private List<LocalDateTime> value;

    private enum RepeatMode {
        daily, weekly
    }

    CustomDateTimePicker next = new CustomDateTimePicker();
    Select<RepeatMode> repeatMode = new Select<>();
    VIntegerField repeatTimes = new VIntegerField();
    Span repeatTimesText = new Span("times");

    VerticalLayout existingValues = new VVerticalLayout()
            .withMargin(false)
            .withPadding(false)
            .withSpacing(false);

    public MultiDateTimeField() {
        value = new ArrayList<>();

        next.setLabel("Add new date:");
        HorizontalLayout newRowForm = new VHorizontalLayout(
                next).withDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        //newRowForm.getStyle().set("border-top", "1px solid #ddd");
        getContent().setPadding(false);
        getContent().add(
                existingValues,
                newRowForm);

        next.addValueChangeListener(e -> {
            addNewEntry();
        });

        repeatMode.setItems(RepeatMode.values());
        repeatMode.setValue(RepeatMode.weekly);
        repeatTimes.setMin(1);
        repeatTimes.setMax(52);
        repeatTimes.setValue(1);
        repeatTimes.setWidth("3em");

    }

    private void addNewEntry() {
        LocalDateTime newValue = next.getValue();
        if(newValue == null) {
            return;
        }
        value.add(newValue);
        addRow(newValue);
        next.clear();

        fireValueChange();
    }

    private void addRow(LocalDateTime newValue) {
        VHorizontalLayout row = new VHorizontalLayout();
        CustomDateTimePicker dateTimePicker = new CustomDateTimePicker();
        dateTimePicker.setValue(newValue);
        dateTimePicker.addValueChangeListener(event -> {
            LocalDateTime oldValue = event.getOldValue();
            value.set(value.indexOf(oldValue), event.getValue());
            fireValueChange();
        });
        VButton deleteButton = new VButton(VaadinIcon.TRASH.create(), e -> {
            value.remove(dateTimePicker.getValue());
            existingValues.remove(row);
            fireValueChange();
        });

        VButton r = new VButton(VaadinIcon.RECYCLE.create(), e -> {

            Dialog dialog = new Dialog();
            dialog.setHeaderTitle("Repeat the date...");

            dialog.add(
                    new VHorizontalLayout(repeatTimes, repeatTimesText, repeatMode)
                            .withAlignItems(FlexComponent.Alignment.CENTER)
            );

            VButton doRepeat = new DefaultButton("Repeat", ()->{
                var cur = dateTimePicker.getValue();
                for (int i = 0; i < repeatTimes.getValue(); i++) {
                    cur = cur.plusDays(repeatMode.getValue() == RepeatMode.daily ? 1 : 7);
                    value.add(cur);
                    addRow(cur);
                }
                dialog.close();
            });
            Button cancelButton = new VButton("Cancel", () -> dialog.close());
            dialog.getFooter().add(cancelButton, doRepeat);
            dialog.open();
            repeatTimes.selectAll();
            repeatTimes.focus();

        }).withTooltip("Repeat this time daily/weekly/monthly...");

        row.add(dateTimePicker, r, deleteButton);
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
