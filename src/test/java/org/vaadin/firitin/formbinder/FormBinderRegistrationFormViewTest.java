package org.vaadin.firitin.formbinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Consumer;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;

import org.junit.jupiter.api.Test;

/**
 * Cross-field validation written by hand, without the Bean Validation API: the
 * view compares the two password fields itself and hands the binder a message for
 * the field it belongs to.
 *
 * <p>The point of the example is that the comparison is written against the DTO
 * rather than against the fields, so these tests drive the fields and check what
 * the DTO's verdict looks like on screen.
 */
class FormBinderRegistrationFormViewTest {

    private static final String VIEW_PACKAGE = "org.vaadin.firitin.formbinder";

    private static void inView(Consumer<BrowserlessUIContext> body) {
        try (BrowserlessApplicationContext app =
                     BrowserlessApplicationContext.create(VIEW_PACKAGE)) {
            BrowserlessUIContext ui = app.newUser().newWindow();
            ui.navigate(FormBinderRegistrationFormView.class);
            body.accept(ui);
        }
    }

    @Test
    void passwordsThatDoNotMatchAreReportedOnTheSecondField() {
        inView(ui -> {
            ui.findTextField().withLabel("Username").setValue("jorma");
            ui.findPasswordField().withLabel("Password").setValue("salasana");
            ui.findPasswordField().withLabel("Verify password").setValue("salsana");

            ui.findButton().withText("Register").click();

            var verification = ui.findPasswordField().withLabel("Verify password").component();
            assertTrue(verification.isInvalid());
            assertEquals("Passwords do not match!", verification.getErrorMessage());
            assertFalse(ui.findNotification().exists(), "and nothing was registered");
        });
    }

    @Test
    void matchingPasswordsGoThrough() {
        inView(ui -> {
            ui.findTextField().withLabel("Username").setValue("jorma");
            ui.findPasswordField().withLabel("Password").setValue("salasana");
            ui.findPasswordField().withLabel("Verify password").setValue("salasana");

            ui.findButton().withText("Register").click();

            assertTrue(ui.findNotification().getText().contains("All fine"));
            assertFalse(ui.findPasswordField().withLabel("Verify password").component().isInvalid());
        });
    }

    /** The message is cleared when the reason for it is: the next attempt starts clean. */
    @Test
    void correctingThePasswordClearsTheMessage() {
        inView(ui -> {
            ui.findPasswordField().withLabel("Password").setValue("salasana");
            ui.findPasswordField().withLabel("Verify password").setValue("vaara");
            ui.findButton().withText("Register").click();

            ui.findPasswordField().withLabel("Verify password").setValue("salasana");
            ui.findButton().withText("Register").click();

            assertFalse(ui.findPasswordField().withLabel("Verify password").component().isInvalid());
        });
    }

    /** @NotEmpty on the username reaches the field as the required indicator. */
    @Test
    void theUsernameIsMarkedRequired() {
        inView(ui -> assertTrue(ui.findTextField().withLabel("Username").component()
                .isRequiredIndicatorVisible()));
    }
}
