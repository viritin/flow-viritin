package org.vaadin.firitin.formbinder;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.validation.constraints.NotEmpty;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.form.FormBinder;

/**
 * Example/playground for cross-field-validation without Java Bean Validation API
 */
@Route
public class FormBinderRegistrationFormView extends VerticalLayout {

    // DTO, most likely provided by backend, but could be for UI only as well
    record Account(@NotEmpty String username, String password, String passwordVerification) {}

    TextField username = new VTextField("Username");
    PasswordField password = new PasswordField("Password");
    PasswordField passwordVerification = new PasswordField("Verify password");
    FormBinder<Account> binder = new FormBinder<>(Account.class,this);

    public FormBinderRegistrationFormView() {
        add(new RichText().withMarkDown("""
        A rule that spans two fields, without the Bean Validation API: the two
        passwords have to match. The check is written against the DTO the binder
        hands over, not against the fields — so it reads like a rule about accounts
        and could be moved somewhere it is reusable.

        Type two different passwords and press Register. The message appears on the
        second field, because `Account::passwordVerification` is the property it was
        given — a method reference rather than a string, so a typo would not compile.
        Make them match, and the form goes through.

        """));
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
            // The record's accessor names the field, so a typo here would not
            // compile and renaming the component renames this too
            binder.setRawConstraintViolation(Account::passwordVerification,
                    "Passwords do not match!");
        } else {
            Notification.show("All fine, do stuff with things");
        }
    }
}
