package org.vaadin.firitin;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.checkbox.IndeterminateCheckbox;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.form.FormBinder;

@Route
public class IndeterminateCheckboxView extends VVerticalLayout {

    IndeterminateCheckbox value = new IndeterminateCheckbox("Value from a record");

    public record DataWithBoolean(Boolean value) {
        public Boolean getValue() {
            return value;
        }
    }

    public IndeterminateCheckboxView() {

        add(new H2("Plain IndeterminateCheckbox"));

        IndeterminateCheckbox checkbox = new IndeterminateCheckbox();
        checkbox.setLabel("Indeterminate Checkbox");
        checkbox.addValueChangeListener(event -> {
            add(new Paragraph("IndeterminateCheckbox value changed: " + event.getValue() + " (null means indeterminate)"));
        });
        add(checkbox);


        add(new H2("IndeterminateCheckbox with Binder"));
        add(value);
        FormBinder<DataWithBoolean> dataWithBooleanFormBinder = new FormBinder<>(DataWithBoolean.class, this);
        dataWithBooleanFormBinder.addValueChangeListener(event -> {
            add(new Paragraph("FormBinder value changed: " + event.getValue() + ""));
        });


    }
}
