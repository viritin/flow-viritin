package org.vaadin.firitin;

import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import org.vaadin.firitin.components.textfield.VIntegerField;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.fields.ElementCollectionField;
import org.vaadin.firitin.fields.EnumSelect;
import org.vaadin.firitin.form.AbstractForm;
import org.vaadin.firitin.testdomain.Address;
import org.vaadin.firitin.testdomain.Person;
import org.vaadin.firitin.util.style.Padding;
import org.vaadin.firitin.util.style.Padding.Side;
import org.vaadin.firitin.util.style.Padding.Size;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.validator.BeanValidator;
import com.vaadin.flow.router.Route;
import java.time.LocalDateTime;
import org.vaadin.firitin.fields.LocalDateTimeField;

@Route
public class PersonForm extends AbstractForm<Person> {

    private TextField firstName = new VTextField();
    private TextField lastName = new VTextField();
    private LocalDateTimeField joinTime = new LocalDateTimeField();

    public static class AddressEditor {
        EnumSelect<Address.AddressType> type = new EnumSelect<>(Address.AddressType.class);
        TextField street = new VTextField();
        TextField city = new VTextField();
        IntegerField zipCode = new VIntegerField();
    }

    private ElementCollectionField<Address> addresses = new ElementCollectionField<Address>(Address.class, AddressEditor.class)
            .withEditorInstantiator(() -> {
                AddressEditor e = new AddressEditor();
                // This is optional but sometimes handy in more complex cases
                // to configure fields. Do whatever configuraiton for the fields you need.
                return e;
            });

    public PersonForm() {
        super(Person.class);
        setDeleteHandler(this::handleDelete);
        setSavedHandler(this::handleSave);
        setResetHandler(this::handleReset);
        bindFields();
        final Person person = new Person();
        person.setFirstName("Jorma");
        person.setJoinTime(LocalDateTime.now());
        setEntity(person);
    }

    private void bindFields() {
        getBinder().forField(firstName).withValidator(new BeanValidator(Person.class, "firstName"))
                .bind(Person::getFirstName, Person::setFirstName);
    }

    private void handleSave(Person person) {
        Notification.show("Handle Save. Person: " + person.toString());
        BinderValidationStatus<Person> validate = getBinder().validate();
        if (validate.isOk()) {
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
    protected Component createContent() {
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);

        VerticalLayout formContents = new VerticalLayout();
        
        formContents.add(firstName);
        formContents.add(lastName);
        formContents.add(joinTime);
        formContents.add(addresses);

        content.add(formContents);

        HorizontalLayout toolbar = getToolbar();
        Padding.of(Side.HORIZONTAL, Size.SMALL).apply(toolbar);
        
        toolbar.add(new Button("Open another in a modal popup", e->{
            new PersonForm().openInModalPopup().setCloseOnOutsideClick(true);
        }));
        
        content.add(toolbar);
        return content;
    }

}
