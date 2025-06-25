package org.vaadin.firitin.util.clipboard;

import com.vaadin.flow.component.UI;

import java.util.concurrent.CompletableFuture;

/**
 * Utility class for clipboard operations in a Vaadin application.
 * This class provides methods to copy text to the clipboard and read text from the clipboard
 * using the Clipboard API.
 * <p>
 *     Note that this is helper is early experiment and may not work in all browsers, most especially
 *     in Safari, due to its incomplete handling of transient activation regarding clipboard. See
 *     test class for more details and workarounds.
 * </p>
 */
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
