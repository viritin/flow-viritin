package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.button.ConfirmButton;
import org.vaadin.firitin.components.button.DeleteButton;
import org.vaadin.firitin.components.dialog.ConfirmationDialog;

@Route
public class ConfirmButtonUsage extends VerticalLayout {

    public ConfirmButtonUsage() {
        ConfirmationDialog cd = new ConfirmationDialog("Are you sure you want to do this action?", () -> {
            Notification.show("Action!");
        });
        Button b = new Button("Show standalone confirmation dialog", e-> {
            cd.open();
        });
        add(b);

        var button = new ConfirmButton("Do scary thing", () -> Notification.show("Done!"))
                // optionals
                .withConfirmationPrompt("Are you sure you want to do the scary thing?")
                .withConfirmationDescription("Proceeding might make your screen glass break, YES, even LCDs!")
                .withOKText("Break glass")
                .withCancelText("Stay safe");

        add(button);

        add(new Paragraph("DeleteButton is like ConfirmButton, but with scary red colors and different default texts"));

        DeleteButton deleteButton = new DeleteButton("Delete", () -> Notification.show("Deleted!"));
        add(deleteButton);

        DeleteButton iconOnly = new DeleteButton(() -> Notification.show("Deleted!"));
        add(iconOnly);

        DeleteButton both = new DeleteButton(() -> Notification.show("Deleted!"));
        both.setButtonCaption("Delete");
        add(both);
    }
}
