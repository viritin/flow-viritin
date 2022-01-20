package org.vaadin.firitin.components.button;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

public class DefaultButton extends VButton {

    public DefaultButton() {
        configureDefaultStyling();
    }

    public DefaultButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(icon, clickListener);
        configureDefaultStyling();
    }

    public DefaultButton(Component icon, BasicClickListener clickListener) {
        super(icon, clickListener);
        configureDefaultStyling();
    }

    public DefaultButton(Component icon) {
        super(icon);
        configureDefaultStyling();
    }

    public DefaultButton(String text, Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, icon, clickListener);
        configureDefaultStyling();
    }

    public DefaultButton(Component icon, String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(icon, text, clickListener);
        configureDefaultStyling();
    }

    public DefaultButton(String text, Component icon, BasicClickListener clickListener) {
        super(text, icon, clickListener);
        configureDefaultStyling();
    }

    public DefaultButton(String text, Component icon) {
        super(text, icon);
        configureDefaultStyling();
    }

    public DefaultButton(Component icon, String text) {
        super(icon, text);
        configureDefaultStyling();
    }

    public DefaultButton(String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, clickListener);
        configureDefaultStyling();
    }

    public DefaultButton(String text, BasicClickListener clickListener) {
        super(text, clickListener);
        configureDefaultStyling();
    }

    public DefaultButton(String text) {
        super(text);
        configureDefaultStyling();
    }

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
