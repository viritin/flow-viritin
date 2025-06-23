package org.vaadin.firitin.util;

import com.vaadin.flow.component.UI;

import java.util.concurrent.CompletableFuture;

/**
 * Utility class to check the visibility state of the current page in a Vaadin application.
 * It provides methods to determine if the page is visible, focused, or hidden.
 * <p>
 * This class uses JavaScript to interact with the browser's visibility API and adds some
 * helpful extra data via `document.hasFocus()` method.
 * <p>
 * TODO: add a listener to detect visibility changes
 */
public class PageVisibility {

    public enum Visibility {
        /**
         * The page is visible and focused.
         * In the browser, this is indicated by the `document.hasFocus()` method returning true.
         */
        VISIBLE,

        /**
         * The page is visible but not focused. E.g. behind another window.
         * In the browser, this is indicated by the `document.hasFocus()` method returning false,
         */
        VISIBLE_NON_FOCUSED,

        /**
         * The page is not visible, e.g. the browser tab is not active or the window is minimized.
         * In the browser, this is indicated by the `document.hidden` property being true.
         */
        HIDDEN
    }

    private final UI ui;

    public static PageVisibility get() {
        return new PageVisibility(UI.getCurrent());
    }

    public PageVisibility(UI ui) {
        this.ui = ui;
    }

    /**
     * Checks the visibility state of the current page.
     * <p>
     * This method returns a CompletableFuture that resolves to one of the {@link Visibility} enum values,
     * indicating whether the page is visible, focused, or hidden.
     *
     * @return a CompletableFuture containing the visibility state of the page
     */
    public CompletableFuture<Visibility> isVisible() {
        return ui.getPage().executeJs("""
            if(document.hidden) {
                return 'HIDDEN';
            } else {
                if(document.hasFocus()) {
                    return 'VISIBLE';
                } else {
                    return 'VISIBLE_NON_FOCUSED';
                }
            }
        """).toCompletableFuture(String.class).thenApply(str -> Visibility.valueOf(str.toUpperCase()));
    }

}
