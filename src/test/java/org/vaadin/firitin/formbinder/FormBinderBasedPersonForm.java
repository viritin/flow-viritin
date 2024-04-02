package org.vaadin.firitin.formbinder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.validation.ConstraintViolation;
import org.vaadin.firitin.PersonForm;
import org.vaadin.firitin.components.datetimepicker.VDateTimePicker;
import org.vaadin.firitin.components.textfield.VIntegerField;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.fields.ElementCollectionField;
import org.vaadin.firitin.fields.EnumSelect;
import org.vaadin.firitin.form.BeanValidationForm;
import org.vaadin.firitin.testdomain.Address;
import org.vaadin.firitin.testdomain.Person;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Route
public class FormBinderBasedPersonForm extends BeanValidationForm<Person> {

    private TextField firstName = new VTextField("First name, remove this to see error");
    private TextField lastName = new VTextField().withTooltip("You should type last name here");

    private VDateTimePicker joinTime = new VDateTimePicker();

    private ElementCollectionField<Address> addresses = new ElementCollectionField<Address>(Address.class);

    public FormBinderBasedPersonForm() {
        super(Person.class);
        // The default group requires "age", for which we don't have
        // a field and we are not interested in this form, define a different
        // validation group to use
        // TODO Make validation groups affect how required indicator works
        // Now if you add age, it is required, even though not for this group
        setValidationGroups(Person.FirstNameOnly.class);

        setDeleteHandler(this::handleDelete);
        setSavedHandler(this::handleSave);
        setResetHandler(this::handleReset);
        final Person person = new Person();
        person.setFirstName("Jorma");
        person.setJoinTime(LocalDateTime.now());
        setEntity(person);
        joinTime.setLocale(new Locale("fi", "FI"));

    }

    @Override
    public HorizontalLayout getToolbar() {
        HorizontalLayout toolbar = super.getToolbar();
        toolbar.add(new Button("Show validation errors", e-> {
            Person person = getBinder().getValue();
            Set<ConstraintViolation<Person>> constraintViolations = doBeanValidation(person);

            Notification.show(constraintViolations.size() + " violations, person: " + person.toString());
            getBinder().setConstraintViolations(constraintViolations);
        }));
        return toolbar;
    }

    private void handleSave(Person person) {
        // TODO why would it not be!?
        if (isValid()) {
            Notification.show("Handle Save. Person: " + person.toString());
            closePopup();
        }
    }

    private void handleDelete(Person person) {
        Notification.show("Handle Delete");
        closePopup();
    }

    private void handleReset(Person person) {
        Notification.show("Handle Reset");
    }

    @Override
    protected List<Component> getFormComponents() {
        return Arrays.asList(
                firstName,
                lastName,
                joinTime,
                addresses
        );
    }

    public static class AddressEditor {
        EnumSelect<Address.AddressType> type = new EnumSelect<>(Address.AddressType.class);
        TextField street = new VTextField();
        TextField city = new VTextField();
        IntegerField zipCode = new VIntegerField();
    }
}