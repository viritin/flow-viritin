package org.vaadin.firitin.formbinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Consumer;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.html.Pre;

import org.junit.jupiter.api.Test;

/**
 * The binder without the Bean Validation API: violations arrive as plain strings,
 * keyed by property name, and anything whose key matches no field is shown at form
 * level.
 */
class FormBinderWithoutBeanValidationAPIViewTest {

    private static final String VIEW_PACKAGE = "org.vaadin.firitin.formbinder";

    private static final String FIELD_MESSAGE = "Name must be filled, now String.isBlank()";
    private static final String FORM_MESSAGE = "This is a bean level error message";

    private static void inView(Consumer<BrowserlessUIContext> body) {
        try (BrowserlessApplicationContext app =
                     BrowserlessApplicationContext.create(VIEW_PACKAGE)) {
            BrowserlessUIContext ui = app.newUser().newWindow();
            ui.navigate(FormBinderWithoutBeanValidationAPIView.class);
            body.accept(ui);
        }
    }

    @Test
    void theFieldStartsWithTheValueTheDtoHad() {
        inView(ui -> assertEquals("Initial name value",
                ui.findTextField().withLabel("Name!").component().getValue()));
    }

    /**
     * A key that names a property lands on its field; a key that names none —
     * the view uses a deliberately silly one — lands at form level.
     */
    @Test
    void aMessageGoesToItsFieldAndAnUnmatchedOneToTheForm() {
        inView(ui -> {
            ui.findTextField().withLabel("Name!").setValue("   ");
            ui.findButton().withText("Validate and show dto value").click();

            var name = ui.findTextField().withLabel("Name!").component();
            assertTrue(name.isInvalid());
            assertEquals(FIELD_MESSAGE, name.getErrorMessage());
            assertTrue(ui.findParagraph().withTextContaining(FORM_MESSAGE).exists(),
                    "the message that belongs to no field is shown all the same");
        });
    }

    /** Non-buffered: what was typed is in the DTO before anyone asks it to be. */
    @Test
    void theDtoShownIsTheOneTheFieldWroteInto() {
        inView(ui -> {
            ui.findTextField().withLabel("Name!").setValue("Jorma");
            ui.findButton().withText("Validate and show dto value").click();

            assertTrue(ui.find(Pre.class).first().getText().contains("\"name\" : \"Jorma\""),
                    ui.find(Pre.class).first().getText());
        });
    }

    @Test
    void aValidValueClearsWhatTheInvalidOneLeft() {
        inView(ui -> {
            ui.findTextField().withLabel("Name!").setValue(" ");
            ui.findButton().withText("Validate and show dto value").click();

            ui.findTextField().withLabel("Name!").setValue("Jorma");
            ui.findButton().withText("Validate and show dto value").click();

            assertFalse(ui.findTextField().withLabel("Name!").component().isInvalid());
            assertFalse(ui.findParagraph().withTextContaining(FORM_MESSAGE).exists());
        });
    }
}
