package org.vaadin.firitin.util;

import com.vaadin.flow.component.UI;

import java.util.concurrent.CompletableFuture;

public class Clipboard {

    /**
     * Copies the given text to the current user's clipboard using the Clipboard API.
     *
     * @param thisIsATestText the text to be copied to the clipboard
     */
    public static void copyToClipboard(String thisIsATestText) {
        UI.getCurrent().getPage().executeJs("""
            navigator.clipboard.writeText($0)
        """, thisIsATestText);
    }

    /**
     * Reads text from the clipboard using the Clipboard API.
     * This method returns a CompletableFuture that resolves to the text read from the clipboard,
     * once the required browser round-trip is done.
     *
     * @return a CompletableFuture containing the text from the clipboard
     */
    public static CompletableFuture<String> readFromClipboard() {
        return UI.getCurrent().getPage().executeJs("""
            return navigator.clipboard.readText();
        """).toCompletableFuture(String.class);
    }
}
