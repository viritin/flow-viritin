package org.vaadin.firitin.components.dialog;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.server.Command;
import org.vaadin.firitin.components.button.DefaultButton;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;

/**
 * Simple confirmation dialog. Designed for ConfirmButton and DeleteButton,
 * but can be used as such as well.
 */
public class ConfirmationDialog extends VDialog {

    private final Command action;

    DefaultButton ok = new DefaultButton("OK");
    H3 prompt = new H3();
    Paragraph details = new Paragraph("\u00A0");
    VButton cancel = new VButton("Cancel").withThemeVariants(ButtonVariant.LUMO_TERTIARY);
    VHorizontalLayout footer = new VHorizontalLayout(cancel).space().withComponents(ok);

    public ConfirmationDialog(String confirmationPrompt, Command action) {
        super();
        ok.addClickListener(e -> {
            action.execute();
            close();
        });
        cancel.addClickListener(e -> close());
        this.action = action;
        setConfirmationPrompt(confirmationPrompt);
        add(prompt, details, footer);
    }

    public void setConfirmationPrompt(String confirmationPrompt) {
        prompt.setText(confirmationPrompt);
    }

    public void setConfirmationDescription(String confirmationDescription) {
        details.setText(confirmationDescription);
    }

    public void setOkText(String okText) {
        ok.setText(okText);
    }

    public void setCancelText(String cancelText) {
        cancel.setText(cancelText);
    }

    public DefaultButton getOkButton() {
        return ok;
    }
}
