package org.vaadin.firitin.util.fullscreen;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
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
     * This method will request full screen mode for the element of the provided component.
     * Due to a limitation in current Vaadin version, certain "floating" components (or pieces
     * of components) may not work correctly in full screen mode. Examples: Notification, popup
     * part of ComboBox, etc. See
     *
     * @param component the component for which to request full screen mode
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
                return document.fullscreenEnabled;
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
