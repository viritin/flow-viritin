package org.vaadin.firitin.components;

import static org.claspina.confirmdialog.ButtonOption.caption;
import static org.claspina.confirmdialog.ButtonOption.focus;
import static org.vaadin.firitin.components.VButton.ButtonSize.SMALL;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import org.claspina.confirmdialog.ButtonType;
import org.claspina.confirmdialog.ConfirmDialog;
import org.vaadin.firitin.util.VStyleUtil;

/**
 * @author Panos Bariamis (pbaris)
 */
public class DeleteButton extends Composite<VButton> {

    private String headerText = "";
    private String promptText = "Are you sure?";
    private String confirmText = "OK";
    private String cancelText = "Cancel";

    private ConfirmDialog dialog;

    private Runnable confirmHandler;
    private Runnable cancelHandler;

    public DeleteButton() {
        getContent().withColor(VButton.ButtonColor.ERROR);
        getContent().addClickListener(e -> this.confirm());
    }

    private void confirm() {
        if (dialog == null) {
            dialog = ConfirmDialog.create()
                .withCaption(headerText)
                .withMessage(new Span(promptText))
                .withOkButton(confirmHandler, focus(), caption(confirmText))
                .withCancelButton(cancelHandler, caption(cancelText));

            adjustDialogButton(dialog.getButton(ButtonType.OK));
            adjustDialogButton(dialog.getButton(ButtonType.CANCEL));
        }
        dialog.open();
    }

    private void adjustDialogButton(Button button) {
        button.setIcon(null);
        VStyleUtil.applyOrElse(SMALL, SMALL, button);
    }

    public void setConfirmHandler(final Runnable confirmHandler) {
        this.confirmHandler = confirmHandler;
    }

    public DeleteButton withConfirmHandler(final Runnable confirmHandler) {
        setConfirmHandler(confirmHandler);
        return this;
    }

    public void setCancelHandler(final Runnable cancelHandler) {
        this.cancelHandler = cancelHandler;
    }

    public DeleteButton withCancelHandler(final Runnable cancelHandler) {
        setCancelHandler(cancelHandler);
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
