package org.vaadin.firitin.components.button;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

/**
 * A Button to use for primary actions, like saving. Styled accordingly and contains
 * a keyboard shortcut with ENTER.
 * 
 * @author mstahv
 */
public class DefaultButton extends VButton {

    /**
     * Creates an empty button.
     */
    public DefaultButton() {
        configureDefaultStyling();
    }

    /**
     * Creates a button with given icon and listener.
     *
     * @param icon the icon
     * @param clickListener the listener to handle click
     */
    public DefaultButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(icon, clickListener);
        configureDefaultStyling();
    }


    /**
     * Creates a button with given icon and listener. The listener is of
     * type BasicClickListener, which doesn't receive the click event,
     * making it easier to use with method references.
     *
     * @param icon the icon
     * @param clickListener the listener to handle click
     */
    public DefaultButton(Component icon, BasicClickListener clickListener) {
        super(icon, clickListener);
        configureDefaultStyling();
    }

    /**
     * Creates a button with given icon.
     *
     * @param icon the icon
     */
    public DefaultButton(Component icon) {
        super(icon);
        configureDefaultStyling();
    }

    /**
     * Creates a button with given text, icon and listener.
     *
     * @param text the text
     * @param icon the icon
     * @param clickListener the listener to handle click
     */
    public DefaultButton(String text, Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, icon, clickListener);
        configureDefaultStyling();
    }

    /**
     * Creates a button with given text, icon and listener.
     *
     * @param text the text
     * @param icon the icon
     * @param clickListener the listener to handle click
     */
    public DefaultButton(Component icon, String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(icon, text, clickListener);
        configureDefaultStyling();
    }

    /**
     * Creates a button with given text, icon and listener. The listener is of
     * type BasicClickListener, which doesn't receive the click event,
     * making it easier to use with method references.
     *
     * @param text the text
     * @param icon the icon
     * @param clickListener the listener to handle click
     */
    public DefaultButton(String text, Component icon, BasicClickListener clickListener) {
        super(text, icon, clickListener);
        configureDefaultStyling();
    }

    /**
     * Creates a button with given text and icon.
     *
     * @param text the text
     * @param icon the icon
     */
    public DefaultButton(String text, Component icon) {
        super(text, icon);
        configureDefaultStyling();
    }

    /**
     * Creates a button with given icon and text.
     * @param icon the icon
     * @param text the text
     */
    public DefaultButton(Component icon, String text) {
        super(icon, text);
        configureDefaultStyling();
    }

    /**
     * Creates a button with given text and listener.
     * @param text the text
     * @param clickListener the listener to handle click
     */
    public DefaultButton(String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, clickListener);
        configureDefaultStyling();
    }

    /**
     * Creates a button with given text and listener. The listener is of
     * type BasicClickListener, which doesn't receive the click event,
     * making it easier to use with method references.
     *
     * @param text the text
     * @param clickListener the listener to handle click
     */
    public DefaultButton(String text, BasicClickListener clickListener) {
        super(text, clickListener);
        configureDefaultStyling();
    }

    /**
     * Creates a button with given text.
     * @param text the text
     */
    public DefaultButton(String text) {
        super(text);
        configureDefaultStyling();
    }

    /**
     * configures the default styling for the default button
     */
    protected void configureDefaultStyling() {
        withThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    /**
     * Controls whether this button is clicked by the ENTER key. On by default,
     * which is the point of the class — but two DefaultButtons in one view means
     * one keypress clicking both, so a form that shares its view (or its popover)
     * with another can switch its own shortcut off.
     *
     * @param enterShortcutEnabled true to click this button on ENTER
     */
    public void setEnterShortcutEnabled(boolean enterShortcutEnabled) {
        this.enterShortcutEnabled = enterShortcutEnabled;
        if (!enterShortcutEnabled && enterShortcut != null) {
            enterShortcut.remove();
            enterShortcut = null;
        } else if (enterShortcutEnabled) {
            hookEnterListener();
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (enterShortcutEnabled) {
            hookEnterListener();
        }
    }

    /*
       At most one registration. This used to call addClickShortcut on every
       attach, and each call registers a new shortcut — so a button that had been
       detached and reattached (a form reopened in a popup, a row rebuilt) was
       clicked several times by one press of ENTER. The registration survives
       detach on its own; it only ever needs to be made once.
    */
    private void hookEnterListener() {
        if (enterShortcut == null) {
            enterShortcut = addClickShortcut(Key.ENTER);
        }
    }

    private ShortcutRegistration enterShortcut;
    private boolean enterShortcutEnabled = true;

}
