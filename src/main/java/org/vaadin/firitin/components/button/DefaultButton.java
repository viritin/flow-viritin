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

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        hookEnterListener();
    }

    private void hookEnterListener() {
        addClickShortcut(Key.ENTER);
    }

}
