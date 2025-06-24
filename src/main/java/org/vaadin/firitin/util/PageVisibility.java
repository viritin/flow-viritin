package org.vaadin.firitin.util;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Utility class to check the visibility state of the current page in a Vaadin application.
 * It provides methods to determine if the page is visible, focused, or hidden.
 * <p>
 * This class uses JavaScript to interact with the browser's visibility API and adds some
 * helpful extra data via `document.hasFocus()` method. This allows it to determine not only
 * hidden tabs, but also tabs that are visible but not focused (and thus often behind
 * another browser window or application).
 */
public class PageVisibility {

    private final UI ui;
    private List<PageVisibilityListener> listeners = new ArrayList<>();

    private DomListenerRegistration domListenerRegistration;

    private PageVisibility(UI ui) {
        this.ui = ui;
    }

    public static PageVisibility get() {
        return get(UI.getCurrent());
    }

    public static PageVisibility get(UI ui) {
        // PageVisibility is "UI scoped" utility, re-use if already created
        PageVisibility pageVisibility = ComponentUtil.getData(ui, PageVisibility.class);
        if (pageVisibility == null) {
            pageVisibility = new PageVisibility(ui);
            ComponentUtil.setData(ui, PageVisibility.class, pageVisibility);
        }
        return pageVisibility;
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

    /**
     * Adds a listener for visibility change events on the page. In the browser, this is
     * uses both the `visibilitychange` event and the `blur` and `focus` events to determine
     * the visibility and focusing state of the page.
     *
     * @param listener the listener to be notified when the visibility state changes.
     * @return a DomListenerRegistration that can be used to remove the listener later.
     */
    public Registration addVisibilityChangeListener(PageVisibilityListener listener) {
        if (domListenerRegistration == null) {
            /*
             * Browser differences makes the implementation a bit more complex. Some notes:
             *
             *   - firefox defers visibilitychange events
             *   - sofari fires some duplicate focus events
             */

            ui.getPage().executeJs("""
                    document.addEventListener('visibilitychange', function() {
                         if(document.hidden) {
                           document.body.dispatchEvent(new CustomEvent('viritin-visibilitychange', {detail: 'HIDDEN'}));
                         } else {
                            // when becoming visible, always also has focus
                            document.body.dispatchEvent(new CustomEvent('viritin-visibilitychange', {detail: 'VISIBLE'}));
                         }
                         if(document.viritinBlurTimer) {
                             clearTimeout(document.viritinBlurTimer);
                             unset(document.viritinBlurTimer);
                         }
                    });
                    window.addEventListener('blur', function(event) {
                         var timeout = 10;
                         // Firefox has a long delay before it fires the visibilitychange event
                         // when the page is blurred, so we use a timeout to ensure we catch it.
                         const isFirefox = navigator.userAgent.indexOf("Firefox") > -1;
                         if(isFirefox) {
                             // Timeout for the visibilitychange event in Firefox is actaully more than 500ms,
                             // but at 500ms the document.hidden is already true, so we can use that to ignore the
                             // obsolete state change
                             timeout = 500;
                         }
                         document.viritinBlurTimer = setTimeout(() => {
                             if(document.hidden) {
                                 // detected by visibilitychange
                                 console.error("Blur event detected, but page is hidden, not dispatching visibility change.");
                             } else {
                                 document.body.dispatchEvent(new CustomEvent('viritin-visibilitychange', {detail: 'VISIBLE_NON_FOCUSED'}));
                             }
                             unset(document.viritinBlurTimer);
                         }, timeout);
                    });
                    window.addEventListener('focus', function(event) {
                         if(!document.hidden) {
                             document.body.dispatchEvent(new CustomEvent('viritin-visibilitychange', {detail: 'VISIBLE'}));
                         }
                    });
                    """);
            domListenerRegistration = ui.getElement().addEventListener("viritin-visibilitychange", event -> {
                        String detail = event.getEventData().getString("event.detail");
                        Visibility visibility = Visibility.valueOf(detail.toUpperCase());
                        // shallow copy the listeners to avoid concurrent modification issues
                        listeners.stream().toList().forEach(l -> l.accept(visibility));
                    }).addEventData("event.detail")
                    .debounce(100); // this helps to avoid some duplicates in Safari

        }
        listeners.add((PageVisibilityListener) listener);
        return () -> {
            listeners.remove(listener);
        };
    }

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

    public interface PageVisibilityListener extends SerializableConsumer<PageVisibility.Visibility> {
    }

}
