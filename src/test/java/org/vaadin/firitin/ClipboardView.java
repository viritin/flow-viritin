package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.textfield.VTextArea;
import org.vaadin.firitin.util.Clipboard;

import java.util.concurrent.CompletableFuture;

@Route
public class ClipboardView extends VerticalLayout {

    public ClipboardView() {

        VTextArea area = new VTextArea("Area for testing clipboard");
        area.setValue("Eräjorma");

        add(new Button("Copy text to clipboard", e -> {
            Clipboard.copyToClipboard("This is a test text");
        }));
        add(area);

        add(new Button("Copy textarea value to clipboard", e -> {
            Clipboard.copyToClipboard(area.getValue());
        }));


        add(new Button("Replace selection with Clipboard value", e -> {

            CompletableFuture<String> clipboardContent = Clipboard.readFromClipboard();

            clipboardContent.thenAccept(clipboardValue -> {
                area.getSelectionRange((start, end, content) -> {
                    area.setValue(content.substring(0, start) + clipboardValue + content.substring(end));
                });
            });
        }));


    }
}
