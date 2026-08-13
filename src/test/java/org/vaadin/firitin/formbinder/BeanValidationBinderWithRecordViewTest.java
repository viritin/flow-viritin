package org.vaadin.firitin.formbinder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Consumer;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;

import org.junit.jupiter.api.Test;

/**
 * A {@link org.vaadin.firitin.form.BeanValidationForm} over a record, opened with
 * no entity at all: the form is for creating one, and the binder builds it from the
 * empty fields.
 *
 * <p>What the reader sees is the save button: off until there is something to save
 * and it holds together.
 */
class BeanValidationBinderWithRecordViewTest {

    private static final String VIEW_PACKAGE = "org.vaadin.firitin.formbinder";

    private static void inView(Consumer<BrowserlessUIContext> body) {
        try (BrowserlessApplicationContext app =
                     BrowserlessApplicationContext.create(VIEW_PACKAGE)) {
            BrowserlessUIContext ui = app.newUser().newWindow();
            ui.navigate(BeanValidationBinderWithRecordView.class);
            body.accept(ui);
        }
    }

    @Test
    void anEmptyFormOffersNothingToSave() {
        inView(ui -> assertFalse(ui.findButton().withText("Save").component().isEnabled()));
    }

    /**
     * The username is the only thing the record requires, so filling it is enough.
     * The password fields are free to stay empty — this form does not compare them.
     */
    @Test
    void aUsernameIsEnoughToMakeItSaveable() {
        inView(ui -> {
            ui.findTextField().withLabel("Username").setValue("jorma");

            assertTrue(ui.findButton().withText("Save").component().isEnabled());
        });
    }

    @Test
    void savingHandsOverWhatWasTyped() {
        inView(ui -> {
            ui.findTextField().withLabel("Username").setValue("jorma");
            ui.findPasswordField().withLabel("Password").setValue("salasana");
            ui.findButton().withText("Save").click();

            String shown = ui.findNotification().getText();
            assertTrue(shown.contains("jorma") && shown.contains("salasana"), shown);
        });
    }

    /** Emptying it again takes the offer back. */
    @Test
    void takingTheUsernameAwayDisablesSaveAgain() {
        inView(ui -> {
            ui.findTextField().withLabel("Username").setValue("jorma");
            ui.findTextField().withLabel("Username").setValue("");

            assertFalse(ui.findButton().withText("Save").component().isEnabled());
        });
    }

    /** @NotEmpty reaches the field, as it does everywhere else. */
    @Test
    void theUsernameIsMarkedRequired() {
        inView(ui -> assertTrue(ui.findTextField().withLabel("Username").component()
                .isRequiredIndicatorVisible()));
    }
}
