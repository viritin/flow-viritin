package org.vaadin.firitin;

import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.button.VButton.ButtonColor;
import org.vaadin.firitin.components.button.VButton.ButtonSize;
import org.vaadin.firitin.components.button.VButton.ButtonType;
import org.vaadin.firitin.components.checkbox.VCheckBox;
import org.vaadin.firitin.components.combobox.VComboBox;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.testdomain.Person;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import org.vaadin.firitin.testdomain.Service;

@Route
public class DemoView extends VerticalLayout {

    public DemoView() {
        VButton button = new VButton().withAutofocus(true).withColor(ButtonColor.SUCCESS).withType(ButtonType.PRIMARY)
                .withSize(ButtonSize.LARGE).withText("Click me!").withClickListener(e -> Notification.show("Clicked!"));

        add(button);

        VCheckBox checkbox = new VCheckBox().withValue(true)
                .withValueChangeListener(v -> Notification.show("New Value : " + v.getValue()))
                .withLabel("Checkbox Label");

        add(checkbox);
        
        VComboBox<Person> comboBox = new VComboBox<Person>()
                .withAllowCustomValue(false).withLabel("Person").withItemLabelGenerator(Person::getLastName)
                .withValueChangeListener(v -> Notification.show("Selected " + v.getValue().getLastName()));
        
        comboBox.setItems(Service.getListOfPersons(100));
        add(comboBox);

        VTextField textField = new VTextField().withPlaceholder("This field don't give you NPE with null value");
        textField.setValue(null);
        add(textField);
    }
}
