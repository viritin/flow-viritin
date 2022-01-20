package org.vaadin.firitin.components.button;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.Command;
import org.vaadin.firitin.components.dialog.ConfirmationDialog;

public class DeleteButton extends ConfirmButton {
    public DeleteButton(String buttonCaption, Command action) {
        super(buttonCaption, action);
        getContent().withThemeVariants(ButtonVariant.LUMO_ERROR);
        setConfirmationPrompt("Are you sure you want to delete this item?");
        setOkText("Delete");
    }

    public DeleteButton(Command action) {
        this(null, action);
        getContent().setIcon(VaadinIcon.TRASH.create());
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

    public DeleteButton withButttonCaption(String text) {
        setButtonCaption(text);
        return this;
    }

    public void setButtonCaption(String text) {
        getContent().setText(text);
    }

    public String getButtonCaption() {
        return getContent().getText();
    }
}
