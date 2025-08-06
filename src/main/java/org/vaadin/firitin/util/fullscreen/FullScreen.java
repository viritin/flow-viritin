package org.vaadin.firitin.util.fullscreen;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.shared.Registration;

import java.util.concurrent.CompletableFuture;

public class FullScreen {

    public static void requestFullscreen() {
        // Vaadin sets background color to html element, so we need to request full screen on it
        // not to make "theme explode" when entering full screen mode.
        UI.getCurrent().getPage().executeJs("""
                    document.documentElement.requestFullscreen();
                """);
    }

    /**
     * Requests full screen mode for the given component.
     * <p>
     * Note that on the client side, fullscreen mode is technically requested for the html element
     * and the rest of the view is hidden during the full screen mode. This is to work around
     * current limitation in "overlay components" (like Notification, ComboBox popup, etc.)
     * and theme. Thus, if you have some elements in the "index.html" that are not part of the
     * Vaadin Flow view, they may not be visible in full screen mode. Same if you are using embedding.
     * In these cases you might try using {@link #requestFullscreenRaw(Component)} method instead.
     *
     * @param component the component for which to request full screen mode
     * @see <a href="https://github.com/vaadin/flow/issues/21902">Related Vaadin Flow issue</a>
     */
    public static void requestFullscreen(Component component) {
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }
        if (!component.isAttached()) {
            throw new IllegalStateException("Component must be attached to the UI to request full screen");
        }
        /*
         * Not making the component itself fullscreen, due to limitations in current Vaadin version.
         * See https://github.com/vaadin/flow/issues/21902, but also theme background is set to
         * html element, so we need to request full screen on it to make things look good.
         *
         * Also, as certain components, like Markdown, breaks if it is re-attached to the DOM, we'll
         * do a hack on the client side that will move the fullscreen component to a wrapper element
         * root and hide the actual view during the full screen mode. The component will be
         * restored to its original parent when exiting full screen mode.
         */
        UI.getCurrent().getPage().executeJs("""
                    const wrapper = $1;
                    const element = $0;
                
                    const placeholder = document.createComment("placeholder");
                    const originalParent = element.parentNode;
                    element.parentNode.insertBefore(placeholder, element);
                
                    wrapper.appendChild(element);
                    wrapper.firstChild.style.display = "none";
                    document.documentElement.requestFullscreen();
                
                    const restoreOriginalParent = evt => {
                        if(!document.fullscreenElement) {
                            originalParent.appendChild(element);
                            placeholder.remove();
                            wrapper.firstChild.style.display = "";
                            document.documentElement.removeEventListener("fullscreenchange", restoreOriginalParent);
                        }
                    };
                    document.documentElement.addEventListener("fullscreenchange", restoreOriginalParent);
                """, component.getElement(), UI.getCurrent().wrapperElement);
    }

    /**
     * Requests full screen mode for the given element.
     *
     * @param el the element for which to request full screen mode
     * @deprecated Requesting full screen for a specific element may have limitations
     * in the current Vaadin version. Consider using {@link #requestFullscreen(Component)}
     * instead.
     */
    @Deprecated(forRemoval = false)
    public static void requestFullscreenRaw(Element el) {
        UI.getCurrent().getPage().executeJs("""
                    const element = $0;
                    element.requestFullscreen();
                """, el);
    }

    /**
     * Requests full screen mode for the given component's element.
     *
     * @param component the component for which to request full screen mode
     * @deprecated Consider using {@link #requestFullscreen(Component)} instead.
     * See implementation notes in {@link #requestFullscreen(Component)} for more information.
     */
    @Deprecated(forRemoval = false)
    public static void requestFullscreenRaw(Component component) {
        requestFullscreenRaw(component.getElement());
    }

    public static void exitFullscreen() {
        UI.getCurrent().getPage().executeJs("""
                document.exitFullscreen();
                """);
    }

    /**
     * Checks if the current UI is in full screen mode.
     *
     * @return a CompletableFuture that resolves to true if the UI is in full screen mode, false otherwise
     */
    public static CompletableFuture<Boolean> isFullscreen() {
        return UI.getCurrent().getPage().executeJs("""
                return document.fullscreenElement !== null;
                """).toCompletableFuture(Boolean.class);
    }

    /**
     * Checks if the full screen mode is currently available.
     *
     * @return a CompletableFuture that resolves to true if full screen mode is available, false otherwise
     */
    public static CompletableFuture<Boolean> fullScreenAvailable() {
        return UI.getCurrent().getPage().executeJs("""
                return document.fullscreenEnabled === true;
                """).toCompletableFuture(Boolean.class);
    }

    public static Registration addFullscreenChangeListener(ComponentEventListener<FullscreenChangeEvent> listener) {
        // conditionally add a listener to document.documentElement that re-fires events with a different name
        // on document.body (~ UI)
        UI ui = UI.getCurrent();
        ui.getPage().executeJs("""
                    if(!document.__viritin_fullscreen_change_listener) {
                        document.addEventListener('fullscreenchange', () => {
                            const isFullscreen = document.fullscreenElement !== null;
                            document.body.dispatchEvent(new CustomEvent('viritin-fullscreenchange', {
                                detail: isFullscreen
                            }));
                        });
                    }
                    document.__viritin_fullscreen_change_listener = true;
                """);

        return ui.getElement().addEventListener("viritin-fullscreenchange", event -> {
            boolean isFullscreen = event.getEventData().getBoolean("event.detail");
            listener.onComponentEvent(new FullscreenChangeEvent(ui, isFullscreen));
        }).addEventData("event.detail");
    }

}
