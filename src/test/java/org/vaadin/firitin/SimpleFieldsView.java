package org.vaadin.firitin;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.formlayout.VFormLayout;
import org.vaadin.firitin.fields.CommaSeparatedStringField;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.vaadin.firitin.fields.StringToDoubleMapField;

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

        addFormItem(commaSeparatedStringField, "Comma separated field for a set of strings", 2);
        commaSeparatedStringField.setWidth("100%");
        
        
        StringToDoubleMapField strToDouble = new StringToDoubleMapField();
        Map<String,Double> map = new HashMap<>();
        map.put("foo", 1.0);
        map.put("bar", 2.1);
        strToDouble.setValue(map);
        strToDouble.addValueChangeListener(e-> {
            Notification.show(e.getValue().toString());
        });

        addFormItem(strToDouble, "String to Double map field", 2);
        commaSeparatedStringField.setWidth("100%");


    }

}
