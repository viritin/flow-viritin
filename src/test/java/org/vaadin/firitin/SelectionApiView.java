package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.datepicker.VDatePicker;
import org.vaadin.firitin.components.formlayout.VFormLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.textfield.VNumberField;
import org.vaadin.firitin.components.textfield.VTextArea;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.fields.CommaSeparatedStringField;
import org.vaadin.firitin.fields.StringToDoubleMapField;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by mstahv
 */
@Route
public class SelectionApiView extends VVerticalLayout {

    public SelectionApiView() {

        var textField = new VTextField();
        //var textField = new VTextArea();
        //var textField = new VNumberField();
        //textField.setValue(123456789.0);
        textField.setValue("0123456789");

        var b = new Button("Select 3-5");
        b.addClickListener(e -> {
            textField.setSelectionRange(3, 5);
        });

        var b2 = new Button("Select all");
        b2.addClickListener(e -> {
            textField.selectAll();
        });

        var b3 = new Button("Set cursor position 4");
        b3.addClickListener(e -> {
            textField.setCursorPosition(4);
        });

        var b4 = new Button("Get selection");
        b4.addClickListener(e -> {
            textField.getSelectionRange((start, end, content) -> {
                Notification.show("Selection: " + start + "-" + end
                        + ": " + content);
            });
        });

        var b5 = new Button("Get cursor position");
        b5.addClickListener(e -> {
            textField.getCursorPosition(cursorPosition -> {
                Notification.show("Cursor position: " + cursorPosition);
            });
        });

        add(textField, b, b2, b3, b4, b5);

        VDatePicker datePicker = new VDatePicker();
        datePicker.setValue(LocalDate.now());
        add(datePicker,
                new Button("Select all in date picker", e -> datePicker.selectAll()),
                new Button("Select 0-2", e -> datePicker.setSelectionRange(0, 2))
        );


    }

}
