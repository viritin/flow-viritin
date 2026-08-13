package org.vaadin.firitin.formbinder.hibernatecrossfieldexample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.BiConsumer;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;

import org.junit.jupiter.api.Test;

/**
 * The cross-field example, driven the way a reader drives it.
 *
 * <p>This view is the one place in the project where a class level constraint is
 * demonstrated, and until now nothing but a pair of eyes checked that it works. It
 * is worth a test of its own because it is exactly what the recent binder changes
 * touch: the form is given a car that does not hold together — three passengers in
 * two seats — so what it does before anyone touches a field is the whole question.
 *
 * <p>Browserless, in the style of {@code LocalizedFieldTest}: a real view in a real
 * routing context, no browser and no frontend build.
 */
class FormBinderCarFormTest {

    private static final String VIEW_PACKAGE =
            "org.vaadin.firitin.formbinder.hibernatecrossfieldexample";

    private static final String VIOLATION = "There must be not more passengers than seats.";

    private static void inView(BiConsumer<BrowserlessUIContext, FormBinderCarForm> body) {
        try (BrowserlessApplicationContext app =
                     BrowserlessApplicationContext.create(VIEW_PACKAGE)) {
            BrowserlessUIContext ui = app.newUser().newWindow();
            body.accept(ui, ui.navigate(FormBinderCarForm.class));
        }
    }

    /**
     * The entity the form is opened with is invalid. It used to say nothing about
     * that until the first keystroke, because validation ran only from the value
     * change listener — so the reader met a form that looked fine and a Save button
     * that was merely waiting for a change to disable itself.
     */
    @Test
    void aCarThatDoesNotHoldTogetherSaysSoOnArrival() {
        inView((ui, form) -> {
            assertTrue(containsText(form, VIOLATION),
                    "the violation belongs on screen before anyone touches a field");
            assertFalse(form.getSaveButton().isEnabled(),
                    "and saving it should not be on offer");
        });
    }

    /**
     * What the view's own text tells the reader to do: "Increase the car size or
     * remove persons to make save button enabled."
     */
    @Test
    void enoughSeatsClearsTheViolationAndOffersSave() {
        inView((ui, form) -> {
            ui.findIntegerField().setValue(3);

            assertFalse(containsText(form, VIOLATION), "three seats fit three passengers");
            assertTrue(form.getSaveButton().isEnabled());
        });
    }

    /** And back again: the form does not keep an answer it has outgrown. */
    @Test
    void takingTheSeatsAwayBringsTheViolationBack() {
        inView((ui, form) -> {
            ui.findIntegerField().setValue(3);
            ui.findIntegerField().setValue(1);

            assertTrue(containsText(form, VIOLATION));
            assertFalse(form.getSaveButton().isEnabled());
            assertEquals(1, form.getBinder().getValue().getSeatCount(),
                    "and the value follows the field, this being a non-buffered binder");
        });
    }

    /**
     * The whole of it, in both directions and through both fields.
     *
     * <p>Here because the question "did this regress?" was worth a second, and
     * because the answer is not obvious from the screen: adding a passenger makes
     * the car <em>less</em> valid, so Save staying disabled is the validator being
     * right rather than the form being stuck. The view now says so as well.
     */
    @Test
    void theFormKeepsUpWithBothFieldsInBothDirections() {
        inView((ui, form) -> {
            ui.findIntegerField().setValue(3);
            assertTrue(form.getSaveButton().isEnabled(), "three seats, three passengers");

            ui.findIntegerField().setValue(4);
            assertTrue(form.getSaveButton().isEnabled(),
                    "and again through the same field, which is not a one-off");

            ui.findIntegerField().setValue(2);
            assertFalse(form.getSaveButton().isEnabled());

            ui.findTextField().setValue("Jorma");
            assertTrue(form.getSaveButton().isEnabled(),
                    "one passenger fits two seats — the other way to fix it");

            ui.findTextField().setValue("Jorma,Ville,Kalle,Uusi");
            assertFalse(form.getSaveButton().isEnabled(),
                    "and adding passengers is not a way to fix anything");
        });
    }

    /** Anywhere below the form, since a class level violation belongs to no field. */
    private static boolean containsText(Component root, String text) {
        String own = root.getElement().getText();
        if (own != null && own.contains(text)) {
            return true;
        }
        return ComponentUtil.getAllChildren(root).anyMatch(child -> containsText(child, text));
    }
}
