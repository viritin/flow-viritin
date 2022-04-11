package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.formlayout.VFormLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.textfield.VNumberField;
import org.vaadin.firitin.components.textfield.VTextArea;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.fields.CommaSeparatedStringField;
import org.vaadin.firitin.fields.StringToDoubleMapField;

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

        add(textField, b, b2);


    }

}
