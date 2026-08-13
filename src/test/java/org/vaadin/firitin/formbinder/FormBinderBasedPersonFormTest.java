package org.vaadin.firitin.formbinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Consumer;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;

import org.junit.jupiter.api.Test;
import org.vaadin.firitin.components.button.DeleteButton;

/**
 * The validation group demo. {@code Person} requires an age in the default group,
 * this form has no field for one, and it says so by activating a group of its own:
 * {@code Person.FirstNameOnly}.
 *
 * <p>So the interesting part is which constraints apply here and which do not —
 * the form would be unsaveable for a reason it has no field to fix.
 */
class FormBinderBasedPersonFormTest {

    private static final String VIEW_PACKAGE = "org.vaadin.firitin.formbinder";

    private static final String FIRST_NAME = "First name, remove this to see error";

    private static void inView(Consumer<BrowserlessUIContext> body) {
        try (BrowserlessApplicationContext app =
                     BrowserlessApplicationContext.create(VIEW_PACKAGE)) {
            BrowserlessUIContext ui = app.newUser().newWindow();
            ui.navigate(FormBinderBasedPersonForm.class);
            body.accept(ui);
        }
    }

    /**
     * The age is required in the default group and there is no field for it. If the
     * group had not been changed, nothing typed here could ever make the form
     * saveable.
     */
    @Test
    void theFormIsSaveableWithoutTheAgeTheDefaultGroupRequires() {
        inView(ui -> {
            ui.findTextField().withLabel(FIRST_NAME).setValue("Kalle");

            assertTrue(ui.findButton().withText("Save").component().isEnabled());
        });
    }

    /** The group that is active is enforced, all of it. */
    @Test
    void aFirstNameTooShortForItsGroupIsRefused() {
        inView(ui -> {
            // @Size(min = 3, groups = FirstNameOnly.class)
            ui.findTextField().withLabel(FIRST_NAME).setValue("ab");

            assertTrue(ui.findTextField().withLabel(FIRST_NAME).component().isInvalid());
            assertFalse(ui.findButton().withText("Save").component().isEnabled());
        });
    }

    /**
     * That @Size does not reach the field as a maxlength, though, and deliberately:
     * a constraint belonging to a group cannot be un-set from a widget when the
     * group stops being active. It is checked in validation instead, which the test
     * above shows.
     */
    @Test
    void aGroupedSizeIsNotHandedToTheField() {
        inView(ui -> assertEquals(0,
                ui.findTextField().withLabel(FIRST_NAME).component().getMaxLength(),
                "@Size(max = 15, groups = ...) stays on the server side"));
    }

    /** Its @NotNull does reach it, because setValidationGroups re-reads them. */
    @Test
    void theFirstNameIsMarkedRequiredForTheActiveGroup() {
        inView(ui -> assertTrue(ui.findTextField().withLabel(FIRST_NAME).component()
                .isRequiredIndicatorVisible()));
    }

    /**
     * All three handlers are set, so all three buttons are offered. The delete one
     * is found by type rather than by caption: it is a {@link DeleteButton}, which
     * is a trash icon and no text — {@code setDeleteCaption} only reaches a button
     * that has already been created, and the default never does.
     */
    @Test
    void theToolbarShowsWhatTheFormCanDo() {
        inView(ui -> {
            assertTrue(ui.findButton().withText("Save").exists());
            assertTrue(ui.findButton().withText("Cancel").exists());
            assertTrue(ui.find(DeleteButton.class).first().isVisible());
        });
    }
}
