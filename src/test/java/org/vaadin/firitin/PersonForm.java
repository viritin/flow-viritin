package org.vaadin.firitin;

import org.vaadin.firitin.form.AbstractForm;
import org.vaadin.firitin.testdomain.Person;
import org.vaadin.firitin.util.VStyleUtil;
import org.vaadin.firitin.util.style.Padding;
import org.vaadin.firitin.util.style.Padding.Side;
import org.vaadin.firitin.util.style.Padding.Size;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.validator.BeanValidator;

public class PersonForm extends AbstractForm<Person> {

    private TextField firstName = new TextField();
    private TextField lastName = new TextField();

    public PersonForm() {
        super(Person.class);
        setDeleteHandler(this::handleDelete);
        setSavedHandler(this::handleSave);
        setResetHandler(this::handleReset);
        bindFields();
    }

    private void bindFields() {
        getBinder().forField(firstName).withValidator(new BeanValidator(Person.class, "firstName"))
                .bind(Person::getFirstName, Person::setFirstName);
    }

    private void handleSave(Person person) {
        Notification.show("Handle Save");
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

        content.add(formContents);

        HorizontalLayout toolbar = getToolbar();
        Padding.of(Side.HORIZONTAL, Size.SMALL).apply(toolbar);
        
        content.add(toolbar);
        return content;
    }

}
