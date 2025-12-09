package org.vaadin.firitin.devicemotion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.DomListenerRegistration;

import java.util.concurrent.CompletableFuture;

/**
 * Helper class to detect screen orientation.
 *
 * <p>
 * This class uses the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Screen/orientation">Screen Orientation API</a>
 * in the browser to detect the current orientation of the screen.
 * </p>
 * <p>
 * Useful for adjusting game physics when device is in landscape vs portrait mode.
 * </p>
 *
 * @author mstahv
 */
public class ScreenOrientation {

    private static ObjectMapper om = new ObjectMapper();
    private DomListenerRegistration orientationListener;
    private UI ui;

    public interface OrientationChangeListener {
        void onOrientationChanged(ScreenOrientationInfo orientation);
    }

    /**
     * Get the current screen orientation.
     *
     * @return CompletableFuture with the current orientation info
     */
    public static CompletableFuture<ScreenOrientationInfo> getCurrentOrientation() {
        return getCurrentOrientation(UI.getCurrent());
    }

    /**
     * Get the current screen orientation.
     *
     * @param ui the UI context
     * @return CompletableFuture with the current orientation info
     */
    public static CompletableFuture<ScreenOrientationInfo> getCurrentOrientation(UI ui) {
        return ui.getPage().executeJs("""
            if (screen.orientation) {
                return JSON.stringify({
                    type: screen.orientation.type,
                    angle: screen.orientation.angle
                });
            } else {
                // Fallback for browsers without screen.orientation
                const angle = window.orientation || 0;
                let type = 'portrait-primary';
                if (angle === 90 || angle === -90) {
                    type = angle === 90 ? 'landscape-primary' : 'landscape-secondary';
                }
                return JSON.stringify({ type: type, angle: Math.abs(angle) });
            }
            """).toCompletableFuture(String.class)
            .thenApply(json -> {
                try {
                    return om.readValue(json, ScreenOrientationInfo.class);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse screen orientation", e);
                }
            });
    }

    /**
     * Listen to screen orientation changes.
     *
     * @param listener the listener to be notified of orientation changes
     * @return ScreenOrientation instance that can be used to stop listening
     */
    public static ScreenOrientation listen(OrientationChangeListener listener) {
        return listen(UI.getCurrent(), listener);
    }

    /**
     * Listen to screen orientation changes.
     *
     * @param ui the UI context
     * @param listener the listener to be notified of orientation changes
     * @return ScreenOrientation instance that can be used to stop listening
     */
    public static ScreenOrientation listen(UI ui, OrientationChangeListener listener) {
        ScreenOrientation screenOrientation = new ScreenOrientation();
        screenOrientation.ui = ui;

        // Register listener on the UI element
        screenOrientation.orientationListener = ui.getElement()
            .addEventListener("screen-orientation-change", e -> {
                String detail = e.getEventData().getString("event.detail");
                try {
                    ScreenOrientationInfo info = om.readValue(detail, ScreenOrientationInfo.class);
                    listener.onOrientationChanged(info);
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to parse orientation event", ex);
                }
            })
                .debounce(100); // iphone...
        screenOrientation.orientationListener.addEventData("event.detail");

        // Set up the orientation change listener in JavaScript
        ui.getElement().executeJs("""
            const element = this;

            function notifyOrientationChange() {
                let type, angle;
                if (screen.orientation) {
                    type = screen.orientation.type;
                    angle = screen.orientation.angle;
                } else {
                    window.alert("No screen.orientation support");
                    // Fallback
                    angle = Math.abs(window.orientation || 0);
                    type = (angle === 90 || angle === 270) ? 'landscape-primary' : 'portrait-primary';
                }

                const event = new CustomEvent('screen-orientation-change', {
                    detail: JSON.stringify({ type: type, angle: angle })
                });
                element.dispatchEvent(event);
            }

            // Listen to orientation changes
            if (screen.orientation) {
                screen.orientation.addEventListener('change', notifyOrientationChange);
            } else if (window.orientation !== undefined) {
                window.addEventListener('orientationchange', notifyOrientationChange);
            }

            // Notify current orientation immediately
            notifyOrientationChange();
            """);

        return screenOrientation;
    }

    /**
     * Stop listening to orientation changes.
     */
    public void cancel() {
        if (orientationListener != null) {
            orientationListener.remove();
            orientationListener = null;
        }
    }
}
