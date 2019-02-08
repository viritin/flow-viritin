package org.vaadin.firitin.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog.CancelEvent;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog.ConfirmEvent;

/**
 * @author Panos Bariamis (pbaris)
 */
public class DeleteButton extends Composite<VButton> {

    private String headerText = "";
    private String promptText = "Are you sure?";
    private String confirmText = "OK";
    private String cancelText = "Cancel";

    private ComponentEventListener<ConfirmEvent> confirmListener = e -> {};
    private ComponentEventListener<CancelEvent> cancelListener = e -> {};

    public DeleteButton() {
        getContent().withColor(VButton.ButtonColor.ERROR);
        getContent().addClickListener(e -> this.confirm());
    }

    private void confirm() {
        new ConfirmDialog(headerText, promptText,
            confirmText, confirmListener, cancelText, cancelListener).open();
    }

    public void setConfirmListener(final ComponentEventListener<ConfirmEvent> confirmListener) {
        if (confirmListener != null) {
            this.confirmListener = confirmListener;
        }
    }

    public DeleteButton withConfirmListener(final ComponentEventListener<ConfirmEvent> confirmListener) {
        setConfirmListener(confirmListener);
        return this;
    }

    public void setCancelListener(final ComponentEventListener<CancelEvent> cancelListener) {
        if (cancelListener != null) {
            this.cancelListener = cancelListener;
        }
    }

    public DeleteButton withCancelListener(final ComponentEventListener<CancelEvent> cancelListener) {
        setCancelListener(cancelListener);
        return this;
    }

    public void setText(final String text) {
        getContent().setText(text);
    }

    public DeleteButton withText(final String text) {
        setText(text);
        return this;
    }

    public DeleteButton withType(final VButton.ButtonType type) {
        getContent().withType(type);
        return this;
    }

    public DeleteButton withSize(final VButton.ButtonSize size) {
        getContent().withSize(size);
        return this;
    }

    public DeleteButton withIcon(final Component icon) {
        getContent().withIcon(icon);
        return this;
    }

    public void setHeaderText(final String headerText) {
        this.headerText = headerText;
    }

    public DeleteButton withHeaderText(final String headerText) {
        setHeaderText(headerText);
        return this;
    }

    public void setPromptText(final String promptText) {
        this.promptText = promptText;
    }

    public DeleteButton withPromptText(final String promptText) {
        setPromptText(promptText);
        return this;
    }

    public void setConfirmText(final String confirmText) {
        this.confirmText = confirmText;
    }

    public DeleteButton withConfirmText(final String confirmText) {
        setConfirmText(confirmText);
        return this;
    }

    public void setCancelText(final String cancelText) {
        this.cancelText = cancelText;
    }

    public DeleteButton withRejectText(final String rejectText) {
        setCancelText(rejectText);
        return this;
    }

    public void setEnabled(final boolean enabled) {
        getContent().setEnabled(enabled);
    }

    public DeleteButton withEnabled(final boolean enabled) {
        setEnabled(enabled);
        return this;
    }
}
