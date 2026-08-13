package org.vaadin.firitin.formbinder;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.validation.ConstraintViolation;
import org.vaadin.firitin.PersonForm;
import org.vaadin.firitin.components.RichText;
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
    private TextField lastName = new VTextField("Lastname").withTooltip("You should type last name here");
//    private IntegerField age = new VIntegerField("Age");

    private VDateTimePicker joinTime = new VDateTimePicker("Join date");

    private ElementCollectionField<Address> addresses = new ElementCollectionField<Address>(Address.class);

    public FormBinderBasedPersonForm() {
        super(Person.class);
        // The default group requires "age", for which we don't have
        // a field and we are not interested in this form, define a different
        // validation group to use
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
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getContent().addComponentAsFirst(new RichText().withMarkDown("""
                # Validation groups, and a form that could not be saved without them

                `Person` requires an **age** — `@NotNull`, in the default validation
                group. This form has no field for one, so with the default group it
                could never be valid, and nothing on the screen would say why. It
                asks for a group of its own instead:

                ```java
                setValidationGroups(Person.FirstNameOnly.class);
                ```

                In that group the first name is required and has to be 3 to 15
                characters. Try it: empty the first name, or type two letters, and
                watch Save go. The age is not asked about at all.

                The toolbar has all three buttons because all three handlers are
                set. The delete one is a trash icon, and it asks before it does
                anything.
                """));
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
//                age,
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