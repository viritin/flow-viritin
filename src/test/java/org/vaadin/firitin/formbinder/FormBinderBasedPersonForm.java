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
import org.vaadin.firitin.PersonForm;
import org.vaadin.firitin.components.datetimepicker.VDateTimePicker;
import org.vaadin.firitin.components.textfield.VIntegerField;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.fields.ElementCollectionField;
import org.vaadin.firitin.fields.EnumSelect;
import org.vaadin.firitin.form.BeanValidationForm;
import org.vaadin.firitin.testdomain.Address;
import org.vaadin.firitin.testdomain.Person;
import org.vaadin.firitin.util.style.Padding;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Route
public class FormBinderBasedPersonForm extends BeanValidationForm<Person> {

    private TextField firstName = new VTextField();
    private TextField lastName = new VTextField().withTooltip("You should type last name here");
    private VDateTimePicker joinTime = new VDateTimePicker();
    private VerticalLayout formContents;
    private ElementCollectionField<Address> addresses = new ElementCollectionField<Address>(Address.class, PersonForm.AddressEditor.class)
            .withEditorInstantiator(() -> {
                PersonForm.AddressEditor e = new PersonForm.AddressEditor();
                // This is optional but sometimes handy in more complex cases
                // to configure fields. Do whatever configuraiton for the fields you need.
                return e;
            });

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

    @Override
    public HorizontalLayout getToolbar() {
        return super.getToolbar();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getBinder().setClassLevelViolationDisplay(formContents);

    }

    public static class AddressEditor {
        EnumSelect<Address.AddressType> type = new EnumSelect<>(Address.AddressType.class);
        TextField street = new VTextField();
        TextField city = new VTextField();
        IntegerField zipCode = new VIntegerField();
    }
}