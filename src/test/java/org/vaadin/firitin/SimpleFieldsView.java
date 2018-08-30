package org.vaadin.firitin;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.VFormLayout;
import org.vaadin.firitin.fields.CommaSeparatedStringField;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by mstahv
 */
@Route
public class SimpleFieldsView extends VFormLayout {

    public SimpleFieldsView() {

        CommaSeparatedStringField commaSeparatedStringField = new CommaSeparatedStringField();
        commaSeparatedStringField.setValue(new HashSet<>(Arrays.asList("foo","bar")));
        commaSeparatedStringField.addValueChangeListener(e-> {
            Notification.show(e.getValue().toString());
        });

        addFormItem(commaSeparatedStringField, "Comma separated field for a set of strings");



    }
}
