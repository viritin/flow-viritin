package org.vaadin.firitin;

import org.vaadin.firitin.components.VButton;
import org.vaadin.firitin.components.VButton.ButtonColor;
import org.vaadin.firitin.components.VButton.ButtonSize;
import org.vaadin.firitin.components.VButton.ButtonType;
import org.vaadin.firitin.components.VCheckBox;
import org.vaadin.firitin.components.VComboBox;
import org.vaadin.firitin.components.VTextField;
import org.vaadin.firitin.testdomain.Person;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.Route;

import io.github.benas.randombeans.api.EnhancedRandom;

@Route("")
public class DemoView extends VerticalLayout {

    public DemoView() {
        VButton button = new VButton().withAutofocus(true).withColor(ButtonColor.SUCCESS).withType(ButtonType.PRIMARY)
                .withSize(ButtonSize.LARGE).withText("Click me!").withClickListener(e -> Notification.show("Clicked!"));

        add(button);

        VCheckBox checkbox = new VCheckBox().withValue(true)
                .withValueChangeListener(v -> Notification.show("New Value : " + v.getValue()))
                .withLabel("Checkbox Label");

        add(checkbox);

        VComboBox<Person> comboBox = new VComboBox<Person>().withItems(EnhancedRandom.randomListOf(100, Person.class))
                .withAllowCustomValue(false).withLabel("Person").withItemLabelGenerator(Person::getLastName)
                .withRenderer(TemplateRenderer.<Person>of("<span style=\"background:yellow\">[[item.name]]</span>")
                        .withProperty("name", Person::getLastName))
                .withValueChangeListener(v -> Notification.show("Selected " + v.getValue().getLastName()));
        add(comboBox);

        VTextField textField = new VTextField().withPlaceholder("This field don't give you NPE with null value");
        textField.setValue(null);
        add(textField);
    }
}
