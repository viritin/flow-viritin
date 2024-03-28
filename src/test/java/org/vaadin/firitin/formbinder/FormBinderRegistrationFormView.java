package org.vaadin.firitin.formbinder;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.form.FormBinder;

import java.util.Map;

/**
 * Example/playground for cross-field-validation without Java Bean Validation API
 */
@Route
public class FormBinderRegistrationFormView extends VerticalLayout {

    // DTO, most likely provided by backend, but could be for UI only as well
    record Account(String username, String password, String passwordVerification) {}

    TextField username = new TextField("Username");
    PasswordField password = new PasswordField("Password");
    PasswordField passwordVerification = new PasswordField("Verify password");
    FormBinder<Account> binder = new FormBinder<>(Account.class,this);

    public FormBinderRegistrationFormView() {
        add(new H1("Register as new user"));
        add(username, password, passwordVerification);
        add(new Button("Register", this::register));
    }

    private void register(ClickEvent<Button> buttonClickEvent) {
        var account = binder.getValue();
        // Now we can write validation logic against the DTO, instead of
        // UI fields -> cleaner code and would be easier move the logic
        // separate place for re-use
        if(!account.password().equals(account.passwordVerification())) {
            binder.setRawConstraintViolations(
                    Map.of("passwordVerification", "Passwords do not match!")
            );
        } else {
            Notification.show("All fine, do stuff with things");
        }
    }
}
