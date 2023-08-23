package org.vaadin.firitin.util;

import com.vaadin.flow.component.UI;

import java.util.concurrent.CompletableFuture;

/**
 * Helper class to use browser prompts.
 */
public class BrowserPrompt {

    /**
     * Shows a browser prompt.
     *
     * @param message the message to show
     * @return the value entered by the user
     */
    public static CompletableFuture<String> showPrompt(String message) {
        return showPrompt(message, "");
    }

    /**
     * Shows a browser prompt.
     *
     * @param message      the message to show
     * @param defaultValue the default value to show
     * @return the value entered by the user
     */
    public static CompletableFuture<String> showPrompt(String message, String defaultValue) {
        UI ui = UI.getCurrent();
        return showPrompt(ui, message, defaultValue);
    }

    /**
     * Shows a browser prompt.
     *
     * @param ui           the UI to use
     * @param message      the message to show
     * @param defaultValue the default value to show
     * @return the value entered by the user
     */
    public static CompletableFuture<String> showPrompt(UI ui, String message, String defaultValue) {
        CompletableFuture<String> stringCompletableFuture = new CompletableFuture<>();
        ui.getPage().executeJs("return prompt($0, $1)",
                message, defaultValue).then(String.class, s -> {
            if (s != null) {
                stringCompletableFuture.complete(s);
            } else {
                stringCompletableFuture.completeExceptionally(new RuntimeException("Prompt cancelled"));
            }
        });
        return stringCompletableFuture;
    }

}
