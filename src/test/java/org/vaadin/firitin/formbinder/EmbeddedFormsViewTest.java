package org.vaadin.firitin.formbinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;

import org.junit.jupiter.api.Test;
import org.vaadin.firitin.components.button.DefaultButton;

/**
 * The embedded-form additions, asserted where they can be without a browser.
 *
 * <p>What a keyboard actually does with the shortcuts is a browser's business;
 * these tests pin down what the server registers and how the forms size
 * themselves, which is the part that used to go wrong silently.
 */
class EmbeddedFormsViewTest {

    private static void inView(Consumer<BrowserlessUIContext> body) {
        try (BrowserlessApplicationContext app =
                     BrowserlessApplicationContext.create("org.vaadin.firitin.formbinder")) {
            BrowserlessUIContext ui = app.newUser().newWindow();
            ui.navigate(EmbeddedFormsView.class);
            body.accept(ui);
        }
    }

    /**
     * asSection() means full width and content-driven height. The Composite's
     * element is the Div it wraps, so the style says what the content box does.
     */
    @Test
    void aSectionFormTakesItsWidthAndOnlyTheHeightItNeeds() {
        inView(ui -> {
            var form = ui.find(EmbeddedFormsView.SettingsForm.class).first();
            assertEquals("100%", form.getElement().getStyle().get("width"));
            assertNull(form.getElement().getStyle().get("height"),
                    "the full-size default would make this 100% — a form with no "
                    + "height at all inside a popover, and one that shoves its "
                    + "siblings off a page");
        });
    }

    /** Both forms lay themselves out; neither declares an empty component list. */
    @Test
    void aCustomContentFormNoLongerAnswersForTheDefaultLayout() {
        inView(ui -> {
            // Compiles without the override — that is most of the assertion. The
            // rest: the default answer really is empty, so nothing sneaks into a
            // layout nobody built.
            assertEquals(List.of(),
                    new EmbeddedFormsView.StartCounterForm(ignored -> { }) {
                        List<com.vaadin.flow.component.Component> expose() {
                            return getFormComponents();
                        }
                    }.expose());
        });
    }

    /**
     * One page, two save buttons, one ENTER. The main form keeps the shortcut,
     * the second form said setSaveOnEnter(false) — and the state is readable on
     * the buttons themselves.
     */
    @Test
    void onlyThePagesMainFormListensToEnter() {
        inView(ui -> {
            List<DefaultButton> saveButtons = ui.find(DefaultButton.class).all();
            assertEquals(2, saveButtons.size(), "both forms use the default button");

            List<DefaultButton> listening = saveButtons.stream()
                    .filter(DefaultButton::isEnterShortcutEnabled)
                    .toList();
            assertEquals(1, listening.size(),
                    "two ENTER listeners would mean one keypress saving twice");
            assertEquals("Save", listening.getFirst().getText(),
                    "and the one that listens is the page's verdict, not the row's action");
        });
    }

    /** The forms still save — embedding changed their size, not their job. */
    @Test
    void bothFormsSave() {
        inView(ui -> {
            ui.findTextField().withLabel("Name").setValue("Cold room");
            ui.findButton().withText("Save").click();
            assertTrue(ui.find(com.vaadin.flow.component.html.Pre.class).first()
                    .getText().contains("Cold room"));

            ui.findTextField().withPlaceholder("What is hanging").setValue("hirvi");
            ui.findButton().withText("Start").click();
            assertTrue(ui.find(com.vaadin.flow.component.html.Pre.class).first()
                    .getText().contains("hirvi"));

            assertFalse(ui.findTextField().withPlaceholder("What is hanging")
                    .component().getValue().length() > 0,
                    "the starter resets for the next one");
        });
    }
}
