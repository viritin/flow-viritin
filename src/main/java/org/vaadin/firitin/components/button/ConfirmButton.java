package org.vaadin.firitin.components.button;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.shared.Registration;
import org.vaadin.firitin.components.dialog.ConfirmationDialog;

/**
 * A button whose action is executed ofter showing a confirmation dialog.
 */
public class ConfirmButton extends VButton {

    private Command action;
    private String confirmationPrompt = "Are you sure?";
    private String confirmationDescription;
    private String okText;
    private String cancelText;

    public ConfirmButton(String buttonCaption, Command action) {
        this.action = action;
        setText(buttonCaption);
        super.addClickListener(e->this.prompt());
    }

    @Override
    public Registration addClickListener(ComponentEventListener<ClickEvent<Button>> listener) {
        action = () -> listener.onComponentEvent(null);
        return () -> action = () -> {};
    }

    @Override
    public Registration addClickListener(BasicClickListener clickListener) {
        action = () -> clickListener.onClick();
        return () -> action = () -> {};
    }

    public ConfirmButton withConfirmHandler(Command handler) {
        action = handler;
        return this;
    }

    protected ConfirmationDialog prompt() {
        ConfirmationDialog dialog = new ConfirmationDialog(getConfirmationPrompt(), action);
        if(confirmationDescription != null) {
            dialog.setConfirmationDescription(getConfirmationDescription());
        }
        if(okText != null) {
            dialog.setOkText(okText);
        }
        if(cancelText != null) {
            dialog.setCancelText(cancelText);
        }
        dialog.open();
        return dialog;
    }

    public String getConfirmationPrompt() {
        return confirmationPrompt;
    }

    public void setConfirmationPrompt(String confirmationPrompt) {
        this.confirmationPrompt = confirmationPrompt;
    }

    public ConfirmButton withConfirmationPrompt(String confirmationPrompt) {
        setConfirmationPrompt(confirmationPrompt);
        return this;
    }

    public ConfirmButton withConfirmationDescription(String description) {
        this.setConfirmationDescription(description);
        return this;
    }

    public void setConfirmationDescription(String description) {
        this.confirmationDescription = description;
    }

    public String getConfirmationDescription() {
        return confirmationDescription;
    }

    public ConfirmButton withOKText(String okText) {
        setOkText(okText);
        return this;
    }

    public void setOkText(String okText) {
        this.okText = okText;
    }

    public String getOkText() {
        return okText;
    }

    public ConfirmButton withCancelText(String cancelText) {
        setCancelText(cancelText);
        return this;
    }

    public String getCancelText() {
        return cancelText;
    }

    public void setCancelText(String cancelText) {
        this.cancelText = cancelText;
    }
}
