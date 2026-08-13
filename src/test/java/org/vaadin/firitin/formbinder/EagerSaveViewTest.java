package org.vaadin.firitin.formbinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.BiConsumer;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.html.Pre;

import org.junit.jupiter.api.Test;

/**
 * {@link EagerSaveView} driven the way a reader drives it, which for this view is
 * the whole feature: there is nothing to press.
 *
 * <p>Browserless, in the style of {@code LocalizedFieldTest}: a real view in a real
 * routing context, no browser and no frontend build.
 */
class EagerSaveViewTest {

    private static final String VIEW_PACKAGE = "org.vaadin.firitin.formbinder";

    private static void inView(BiConsumer<BrowserlessUIContext, EagerSaveView> body) {
        try (BrowserlessApplicationContext app =
                     BrowserlessApplicationContext.create(VIEW_PACKAGE)) {
            BrowserlessUIContext ui = app.newUser().newWindow();
            body.accept(ui, ui.navigate(EagerSaveView.class));
        }
    }

    @Test
    void typingSavesWithoutAnythingBeingPressed() {
        inView((ui, view) -> {
            ui.findTextField().atIndex(1).setValue("peura");

            assertTrue(stored(ui).contains("comment=peura"), stored(ui));
        });
    }

    /**
     * The part that is easy to get wrong by hand: a change that leaves the row
     * unusable is shown and not stored. The store keeps what it had.
     */
    @Test
    void aChangeThatDoesNotValidateIsNotSaved() {
        inView((ui, view) -> {
            ui.findTextField().atIndex(1).setValue("aivan liian pitka kommentti");

            assertTrue(ui.findTextField().atIndex(1).component().isInvalid(),
                    "the reader is told");
            assertTrue(stored(ui).contains("comment=hirvi"), "and the store is untouched: " + stored(ui));
        });
    }

    /** The identifier has no field, and comes back with the value all the same. */
    @Test
    void theComponentNoFieldEditsComesBackWithIt() {
        inView((ui, view) -> {
            ui.findTextField().atIndex(2).setValue("poro");

            assertTrue(stored(ui).contains("Counter[id=2, comment=poro"),
                    "the second row saved itself, not the first: " + stored(ui));
            assertTrue(stored(ui).contains("Counter[id=1, comment=hirvi"), stored(ui));
        });
    }

    /** Nothing to press, and nothing offering to be pressed. */
    @Test
    void thereIsNoSaveButton() {
        inView((ui, view) -> assertFalse(ui.findButton().exists(),
                "a form saving as you type should not also show a button"));
    }

    /** Each row keeps its own target, so a change goes to the row it was made in. */
    @Test
    void eachRowSavesItself() {
        inView((ui, view) -> {
            ui.findNumberField().atIndex(1).setValue(60.0);

            assertTrue(stored(ui).contains("id=1, comment=hirvi, target=60.0"), stored(ui));
            assertTrue(stored(ui).contains("id=2, comment=kauris, target=30.0"), stored(ui));
            assertEquals(2, stored(ui).lines().count());
        });
    }

    private static String stored(BrowserlessUIContext ui) {
        return ui.find(Pre.class).first().getText();
    }
}
