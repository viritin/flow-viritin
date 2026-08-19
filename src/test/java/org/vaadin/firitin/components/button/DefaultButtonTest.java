package org.vaadin.firitin.components.button;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The ENTER shortcut's lifecycle, which used to be wrong in a way nobody saw:
 * the registration was made in onAttach, on every attach, and each one is a new
 * registration — so a button detached and reattached (a form reopened in a
 * popup, a row rebuilt) was clicked several times by a single press of ENTER.
 */
class DefaultButtonTest {

    private final VerticalLayout view = new VerticalLayout();
    private final DefaultButton button = new DefaultButton("Save");

    private BrowserlessUIContext ui;

    @BeforeEach
    void setUp() {
        ui = BrowserlessUIContext.forComponent(view);
    }

    @AfterEach
    void tearDown() {
        ui.close();
    }

    @Test
    void reattachingDoesNotStackRegistrations() {
        view.add(button);
        ShortcutRegistration first = enterRegistration(button);
        assertNotNull(first, "attaching is what registers the shortcut");

        view.remove(button);
        view.add(button);

        assertEquals(first, enterRegistration(button),
                "a reattached button must keep its one registration, not add another —"
                + " each extra one is an extra click per keypress");
    }

    @Test
    void theShortcutCanBeSwitchedOff() {
        view.add(button);
        assertTrue(button.isEnterShortcutEnabled());

        button.setEnterShortcutEnabled(false);

        assertFalse(button.isEnterShortcutEnabled());
        assertNull(enterRegistration(button), "off means removed, not merely flagged");
    }

    /** And on again, also after the fact: the state is a switch, not a one-way door. */
    @Test
    void theShortcutCanBeSwitchedBackOn() {
        view.add(button);
        button.setEnterShortcutEnabled(false);

        button.setEnterShortcutEnabled(true);

        assertNotNull(enterRegistration(button));
    }

    /** Disabled before attach stays disabled through attach. */
    @Test
    void switchingOffBeforeAttachSticks() {
        button.setEnterShortcutEnabled(false);

        view.add(button);

        assertNull(enterRegistration(button));
    }

    /*
       Reflection, because the registration is deliberately not API: what this
       test pins down is exactly the thing a caller cannot observe — how many
       registrations exist behind one button.
    */
    private static ShortcutRegistration enterRegistration(DefaultButton button) {
        try {
            Field field = DefaultButton.class.getDeclaredField("enterShortcut");
            field.setAccessible(true);
            return (ShortcutRegistration) field.get(button);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("The field moved; move the test with it", e);
        }
    }
}
