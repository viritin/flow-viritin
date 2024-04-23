package org.vaadin.firitin.formbinder;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.form.BeanValidationForm;
import org.vaadin.firitin.form.FormBinder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Route
public class BeanValidationBinderWithRecordView extends BeanValidationForm<BeanValidationBinderWithRecordView.Account> {

    record Account(String username, String password, String passwordVerification) {}

    TextField username = new TextField("Username");
    PasswordField password = new PasswordField("Password");
    PasswordField passwordVerification = new PasswordField("Verify password");

    public BeanValidationBinderWithRecordView() {
        super(Account.class);
        setSavedHandler(account -> {
            Notification.show("All fine, do stuff with things:" + account);
        });
    }

    @Override
    protected List<Component> getFormComponents() {
        return Arrays.asList(username, password, passwordVerification);
    }

}
