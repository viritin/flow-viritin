package org.vaadin.firitin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.vaadin.firitin.components.SessionExpirationWarningDialog;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.button.VButton.ButtonColor;
import org.vaadin.firitin.components.button.VButton.ButtonSize;
import org.vaadin.firitin.components.button.VButton.ButtonType;
import org.vaadin.firitin.components.checkbox.VCheckBox;
import org.vaadin.firitin.components.combobox.VComboBox;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.testdomain.Person;

@Route
public class SessionExpirationDialogView extends VerticalLayout {

    public SessionExpirationDialogView() {

        // Demo, super short session timeout
        UI.getCurrent().getSession().getSession().setMaxInactiveInterval(60);
        SessionExpirationWarningDialog sessionExpirationWarningDialog = new SessionExpirationWarningDialog();
        // demo, make it appear already quickly for demo purposes
        sessionExpirationWarningDialog.setWarningThreshold(40);
        sessionExpirationWarningDialog.setSessionDestroyAction(() -> {
            System.err.println("Session was destroyed");
        });
        add(sessionExpirationWarningDialog);

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
        comboBox.setItems(EnhancedRandom.randomListOf(100, Person.class)); // TODO add back fluent setItems methods via new interfaces: .withItems(EnhancedRandom.randomListOf(100, Person.class))
        add(comboBox);

        VTextField textField = new VTextField().withPlaceholder("This field don't give you NPE with null value");
        textField.setValue(null);
        add(textField);
    }
}
