package org.vaadin.firitin.components.button;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import org.claspina.confirmdialog.ButtonType;
import org.claspina.confirmdialog.ConfirmDialog;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasEnabled;

import static org.claspina.confirmdialog.ButtonOption.caption;

/**
 * @author Panos Bariamis (pbaris)
 */
public class DeleteButton extends Button implements FluentHasEnabled<DeleteButton>, FluentComponent<DeleteButton> {

    private String headerText = "";
    private String promptText = "Are you sure?";
    private String confirmText = "OK";
    private String cancelText = "Cancel";

    private ConfirmDialog dialog;

    private Runnable confirmHandler;
    private Runnable cancelHandler;

    public DeleteButton() {
        VButton.ButtonColor.ERROR.applyTheme(this);
        addClickListener(e -> this.confirm());
    }

    private void confirm() {
        if (dialog == null) {
            dialog = ConfirmDialog.create()
                .withCaption(headerText)
                .withMessage(new Span(promptText))
                .withOkButton(confirmHandler, caption(confirmText))
                .withCancelButton(cancelHandler, caption(cancelText));

            adjustDialogButton(dialog.getButton(ButtonType.OK));
            adjustDialogButton(dialog.getButton(ButtonType.CANCEL));
        }
        dialog.open();
    }

    private void adjustDialogButton(Button button) {
        button.setIcon(null);
        VButton.ButtonSize.SMALL.applyTheme(button);
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

    public DeleteButton withText(final String text) {
        setText(text);
        return this;
    }

    public DeleteButton withType(final VButton.ButtonType type) {
        type.applyTheme(this);
        return this;
    }

    public DeleteButton withSize(final VButton.ButtonSize size) {
        size.applyTheme(this);
        return this;
    }

    public DeleteButton withIcon(final Component icon) {
        setIcon(icon);
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
}
