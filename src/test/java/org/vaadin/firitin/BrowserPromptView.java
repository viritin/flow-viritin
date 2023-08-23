package org.vaadin.firitin;


import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.util.BrowserPrompt;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@Route
public class BrowserPromptView extends Div {

    public BrowserPromptView() {
        VButton promptBtn = new VButton().withText("Prompt").withClickListener(e -> {
            BrowserPrompt.showPrompt("Enter something").thenAccept(s -> {
                        add(new Paragraph("You entered: " + s));
                    }
            );
        });
        VButton promptBtn2 = new VButton().withText("Prompt with default").withClickListener(e -> {
                    BrowserPrompt.showPrompt("Enter something", "Jorma").thenAccept(s -> {
                                add(new Paragraph("You entered: " + s));
                            }
                    ).exceptionally(throwable -> {
                        add(new Paragraph("Prompt cancelled"));
                        return null;
                    });
                });
        add(promptBtn, promptBtn2);
    }
}
