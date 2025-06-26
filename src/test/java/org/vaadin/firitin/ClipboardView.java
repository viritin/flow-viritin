package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.textfield.VTextArea;
import org.vaadin.firitin.util.clipboard.Clipboard;
import org.vaadin.firitin.util.clipboard.CopyToClipboardButton;
import org.vaadin.firitin.util.clipboard.ReadFromClipboardButton;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Route
public class ClipboardView extends VerticalLayout {

    public ClipboardView() {

        add(new Paragraph("This view is for testing clipboard functionality. Topmost buttons/actions only work in Firefox and Chrome."));

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
                    area.setValue(area.getValue().substring(0, start) + clipboardValue + area.getValue().substring(end));
                });
            });
        }));

        add(new Button("Show clipboard value", e -> {
            CompletableFuture<String> clipboardContent = Clipboard.readFromClipboard();
            clipboardContent.thenAccept(clipboardValue -> {
                Notification.show("Clipboard value: " + clipboardValue);
            });
        }));

        add(new Paragraph("Safari is such a bitch with clipboard compared to Firefox/Chrome. Activation don't properly last like it should and errors occur. Components with workarounds below  (that are bound to click initiated actions)."));

        add(new ReadFromClipboardButton(string -> {
            area.setValue(string);
            if(string.contains(";") && string.contains("\n")) {
                // treat as CSV, show as table;

                List<List<String>> cells = new ArrayList<>();
                String[] lines = string.split("\n");
                for (var l : lines) {
                    String[] split = l.split(";");
                    cells.add(Arrays.asList(split));
                }
                var grid = new Grid<List<String>>(){{
                    int cols = cells.get(0).size();
                    for (int i = 0; i < cols; i++) {
                        int finalI = i;
                        addColumn(line -> line.get(finalI)).setHeader("Column " + (i + 1));
                    }
                    setItems(cells);
                }};
                add(grid);
            }
        }) {{
            setText("Handle clipbooard value");
            addClickListener(event -> {
                Notification.show("Your clipboard value was requested and copied to the text area above. Browser" +
                        "might have requested a permission or showed a native menu with 'Paste' option.");
            });
        }});
        add(new Paragraph("The button above reads clipboard value as text and copy it to the text area above (and show as table if it looks like CSV)."));

        add(new CopyToClipboardButton(() -> "Text content generated at " + LocalTime.now()){{
            addClickListener(e -> Notification.show("Copied text to your clipboard, try pasting it somewhere"));
        }});
        // TODO add other datatypes

    }
}
