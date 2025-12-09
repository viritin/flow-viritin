package org.vaadin.firitin.devicemotion;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.dom.Element;

import java.util.concurrent.CompletableFuture;

/**
 * A helper class to detect device orientation events.
 *
 * <p>
 * This class uses the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Device_orientation_events">Device Orientation API</a>
 * in the browser to detect the orientation of the device. The API mimics the JS counterpart.
 * </p>
 * <p>
 * Note that this API is primarily useful on mobile devices with accelerometers and gyroscopes.
 * Desktop browsers may not support this API or may not have the necessary hardware.
 * The API requires a secure context (HTTPS).
 * </p>
 * <p>
 * On iOS 13+, you must request permission before accessing device orientation data.
 * Use {@link #requestPermission(Runnable, Runnable)} to request permission.
 * </p>
 *
 * @author mstahv
 */
public class DeviceOrientation {

    private DomListenerRegistration orientationListener;
    private UI ui;

    public interface OrientationListener {
        void deviceOrientationUpdate(DeviceOrientationEvent event);
    }

    public enum Permission {
        GRANTED, DENIED, DEFAULT, NOT_REQUIRED
    }

    /**
     * Starts listening to device orientation events and notifies the listener with orientation data.
     * Note: On iOS 13+, you must request permission first using
     * {@link DeviceSensorPermissions#configurePermissionRequest} or call
     * {@link #requestPermissionAndListen} instead.
     *
     * @param listener the listener called when device orientation changes
     * @return a DeviceOrientation instance that can be used to stop listening
     */
    public static DeviceOrientation listen(OrientationListener listener) {
        return listen(listener, false);
    }

    /**
     * Requests permission (if needed) and then starts listening to device orientation events.
     * This is a convenience method that combines permission request with listening.
     * The button click will trigger the permission request on iOS 13+.
     *
     * @param button the button that will trigger the permission request
     * @param listener the listener called when device orientation changes
     * @param onError callback if permission is denied
     * @return the configured button
     */
    public static Button requestPermissionAndListen(Button button, OrientationListener listener, Runnable onError) {
        return requestPermissionAndListen(button, listener, false, onError);
    }

    /**
     * Requests permission (if needed) and then starts listening to device orientation events.
     * This is a convenience method that combines permission request with listening.
     * The button click will trigger the permission request on iOS 13+.
     *
     * @param button the button that will trigger the permission request
     * @param listener the listener called when device orientation changes
     * @param absolute if true, listens to deviceorientationabsolute events
     * @param onError callback if permission is denied
     * @return the configured button
     */
    public static Button requestPermissionAndListen(Button button, OrientationListener listener,
                                                     boolean absolute, Runnable onError) {
        final DeviceOrientation[] holder = new DeviceOrientation[1];

        DeviceSensorPermissions.configurePermissionRequest(button,
            DeviceSensorPermissions.SensorType.ORIENTATION,
            () -> {
                holder[0] = DeviceOrientation.listen(listener, absolute);
                button.setText("Stop Listening");
            },
            onError
        );

        // Update button to toggle between start/stop
        button.addClickListener(e -> {
            if (holder[0] != null) {
                holder[0].cancel();
                holder[0] = null;
                button.setText("Start Listening");
            }
        });

        return button;
    }

    /**
     * Starts listening to device orientation events and notifies the listener with orientation data.
     *
     * @param listener the listener called when device orientation changes
     * @param absolute if true, listens to deviceorientationabsolute events (provides absolute orientation relative to Earth's coordinate frame)
     * @return a DeviceOrientation instance that can be used to stop listening
     */
    public static DeviceOrientation listen(OrientationListener listener, boolean absolute) {
        UI ui = UI.getCurrent();
        return listen(ui, listener, absolute);
    }

    /**
     * Starts listening to device orientation events and notifies the listener with orientation data.
     *
     * @param ui the UI in which context the orientation listener is to be registered
     * @param listener the listener called when device orientation changes
     * @param absolute if true, listens to deviceorientationabsolute events (provides absolute orientation relative to Earth's coordinate frame)
     * @return a DeviceOrientation instance that can be used to stop listening
     */
    public static DeviceOrientation listen(UI ui, OrientationListener listener, boolean absolute) {
        DeviceOrientation deviceOrientation = new DeviceOrientation();
        deviceOrientation.ui = ui;

        Element eventSourceElement = ui.getElement();
        Component activeModalComponent = ui.getInternals().getActiveModalComponent();
        if (activeModalComponent != null) {
            eventSourceElement = activeModalComponent.getElement();
        }

        deviceOrientation.orientationListener = eventSourceElement.addEventListener("deviceorientationevent", e -> {
            try {
                DeviceOrientationEvent orientationEvent = e.getEventDetail(DeviceOrientationEvent.class);
                listener.deviceOrientationUpdate(orientationEvent);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        })
                .addEventDetail()
                .throttle(DeviceMotion.DEFAULT_THROTTLE_TIMEOUT); // limit the event rate to 1 per second by default
        deviceOrientation.orientationListener.addEventData("event.detail");

        String eventName = absolute ? "deviceorientationabsolute" : "deviceorientation";

        // Register the deviceorientation event listener
        eventSourceElement.executeJs("""
                var el = $0;
                window.addEventListener($1, function(e) {
                    const event = new CustomEvent('deviceorientationevent', {
                        detail: JSON.stringify({
                            alpha: e.alpha,
                            beta: e.beta,
                            gamma: e.gamma,
                            absolute: e.absolute,
                            webkitCompassHeading: e.webkitCompassHeading,
                            webkitCompassAccuracy: e.webkitCompassAccuracy
                        })
                    });
                    el.dispatchEvent(event);
                });
                """, eventSourceElement, eventName);

        return deviceOrientation;
    }

    /**
     * Stops listening to device orientation events.
     */
    public void cancel() {
        clearListener();
    }

    private void clearListener() {
        if (orientationListener != null) {
            orientationListener.remove();
            orientationListener = null;
        }
    }

    /**
     * Sets a throttle timeout for device orientation events.
     * Events will be sent at most once per the specified milliseconds.
     * Default is 1000 ms (1 second).
     *
     * @param milliseconds the minimum interval between events in milliseconds
     */
    public void throttleEvents(int milliseconds) {
        orientationListener.throttle(milliseconds);
    }

    /**
     * Requests permission to access device orientation data.
     * On iOS 13+, this must be called before listening to device orientation events.
     * The request must be triggered by a user action (e.g., button click).
     *
     * @param onSuccess callback to run if permission is granted
     * @param onError callback to run if permission is denied or an error occurs
     */
    public static void requestPermission(Runnable onSuccess, Runnable onError) {
        UI ui = UI.getCurrent();
        requestPermission(ui, onSuccess, onError);
    }

    /**
     * Requests permission to access device orientation data.
     * On iOS 13+, this must be called before listening to device orientation events.
     * The request must be triggered by a user action (e.g., button click).
     *
     * @param ui the UI in which context to request permission
     * @param onSuccess callback to run if permission is granted
     * @param onError callback to run if permission is denied or an error occurs
     */
    public static void requestPermission(UI ui, Runnable onSuccess, Runnable onError) {
        ui.getPage().executeJs("""
            debugger;
            // Check if permission API exists (iOS 13+)
            if (typeof DeviceOrientationEvent !== 'undefined' &&
                typeof DeviceOrientationEvent.requestPermission === 'function') {
                return DeviceOrientationEvent.requestPermission()
                    .then(permissionState => {
                        return permissionState;
                    })
                    .catch(error => {
                        return 'denied';
                    });
            } else {
                // Permission not required on this platform
                return 'not_required';
            }
            """).then(String.class, result -> {
            if ("granted".equals(result) || "not_required".equals(result)) {
                onSuccess.run();
            } else {
                onError.run();
            }
        }, error -> {
            onError.run();
        });
    }

    /**
     * Checks if permission is required for device orientation events.
     * Returns true on iOS 13+ Safari, false otherwise.
     *
     * @return CompletableFuture that resolves to true if permission API exists
     */
    public static CompletableFuture<Boolean> isPermissionRequired() {
        return isPermissionRequired(UI.getCurrent());
    }

    /**
     * Checks if permission is required for device orientation events.
     * Returns true on iOS 13+ Safari, false otherwise.
     *
     * @param ui the UI in which context to check
     * @return CompletableFuture that resolves to true if permission API exists
     */
    public static CompletableFuture<Boolean> isPermissionRequired(UI ui) {
        return ui.getPage().executeJs("""
            return typeof DeviceOrientationEvent !== 'undefined' &&
                   typeof DeviceOrientationEvent.requestPermission === 'function';
            """).toCompletableFuture(Boolean.class);
    }

    /**
     * Checks the current permission state for device orientation events.
     *
     * @return CompletableFuture that resolves to the permission state
     */
    public static CompletableFuture<Permission> checkPermission() {
        return checkPermission(UI.getCurrent());
    }

    /**
     * Checks the current permission state for device orientation events.
     *
     * @param ui the UI in which context to check
     * @return CompletableFuture that resolves to the permission state
     */
    public static CompletableFuture<Permission> checkPermission(UI ui) {
        return ui.getPage().executeJs("""
            if (typeof DeviceOrientationEvent !== 'undefined' &&
                typeof DeviceOrientationEvent.requestPermission === 'function') {
                // iOS 13+ - check permission state (note: can't actually check state before requesting)
                return 'default';
            } else {
                return 'not_required';
            }
            """).toCompletableFuture(String.class)
                .thenApply(str -> Permission.valueOf(str.toUpperCase()));
    }
}
