/*
 * Copyright 2019 Viritin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.firitin.fields;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

/**
 * Simple LocalDateTimeField.
 *
 * Currently allows only 24h hh:mm presentation for time part.
 *
 * @author mstahv
 */
public class LocalDateTimeField extends CustomField<LocalDateTime> {

    private DatePicker datePicker = new DatePicker();
    private TextField hour = new TextField();
    private TextField minute = new TextField();
    private LocalDateTime value;

    public LocalDateTimeField() {
        add(new HorizontalLayout(datePicker, hour, minute));

//        hour.setMin(0);
//        hour.setMax(23);
//        hour.setStep(1);
        hour.setPattern("[0-2][0-9]");
//        hour.setPreventInvalidInput(true);
        hour.setWidth("3em");
        hour.setAutoselect(true);
//        minute.setMin(0);
//        minute.setMax(59);
//        minute.setStep(1);
        minute.setPattern("[0-5][0-9]");
//        minute.setPreventInvalidInput(true);
        minute.setWidth("3em");
        minute.setAutoselect(true);

        ValueChangeListener listener = e -> {
            if (e.isFromClient()) {
                updateFromFields();
            }
        };
        hour.addValueChangeListener(listener);
        minute.addValueChangeListener(listener);
        datePicker.addValueChangeListener(listener);
    }

    public LocalDateTimeField(String label) {
        this();
        setLabel(label);
    }

    @Override
    protected LocalDateTime generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(LocalDateTime newValue) {
        datePicker.setValue(newValue.toLocalDate());
        hour.setValue("" + newValue.getHour());
        minute.setValue("" + newValue.getMinute());
        this.value = newValue;
    }

    private void updateFromFields() {
        try {
            value = LocalDateTime.of(datePicker.getValue(), LocalTime.of(Integer.valueOf(hour.getValue()), Integer.valueOf(minute.getValue())));
        } catch (Exception e) {
            // No way to disallow invalid input :-(
        }
    }

}
