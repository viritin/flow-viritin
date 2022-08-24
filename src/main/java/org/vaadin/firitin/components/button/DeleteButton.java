package org.vaadin.firitin.components.button;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.Command;
import org.vaadin.firitin.components.dialog.ConfirmationDialog;

public class DeleteButton extends ConfirmButton {
    public DeleteButton(String buttonCaption, Command action) {
        super(buttonCaption, action);
        withThemeVariants(ButtonVariant.LUMO_ERROR);
        setConfirmationPrompt("Are you sure you want to delete this item?");
        setOkText("Delete");
    }

    public DeleteButton(Command action) {
        this(null, action);
        setIcon(VaadinIcon.TRASH.create());
    }

    public DeleteButton() {
        this(null);
    }

    @Override
    protected ConfirmationDialog prompt() {
        ConfirmationDialog prompt = super.prompt();
        prompt.getOkButton().withThemeVariants(ButtonVariant.LUMO_ERROR);
        return prompt;
    }

    @Override
    public DeleteButton withConfirmationPrompt(String confirmationPrompt) {
        return (DeleteButton) super.withConfirmationPrompt(confirmationPrompt);
    }

    @Override
    public DeleteButton withConfirmationDescription(String description) {
        return (DeleteButton) super.withConfirmationDescription(description);
    }

    @Override
    public DeleteButton withConfirmHandler(Command handler) {
        return (DeleteButton) super.withConfirmHandler(handler);
    }

    public DeleteButton withButttonCaption(String text) {
        setButtonCaption(text);
        return this;
    }

    public void setButtonCaption(String text) {
        setText(text);
    }

    public String getButtonCaption() {
        return getText();
    }

    @Override
    public DeleteButton onClick(BasicClickListener clickListener) {
        return (DeleteButton) super.onClick(clickListener);
    }
    
    
    
}
