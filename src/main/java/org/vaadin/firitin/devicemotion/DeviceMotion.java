package org.vaadin.firitin.devicemotion;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.dom.Element;

import java.util.concurrent.CompletableFuture;

/**
 * A helper class to detect device motion events including acceleration and rotation.
 *
 * <p>
 * This class uses the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Device_orientation_events">Device Motion API</a>
 * in the browser to detect motion of the device. The API mimics the JS counterpart.
 * </p>
 * <p>
 * Note that this API is primarily useful on mobile devices with accelerometers and gyroscopes.
 * Desktop browsers may not support this API or may not have the necessary hardware.
 * The API requires a secure context (HTTPS).
 * </p>
 * <p>
 * On iOS 13+, you must request permission before accessing device motion data.
 * Use {@link #requestPermission(Runnable, Runnable)} to request permission.
 * </p>
 *
 * @author mstahv
 */
public class DeviceMotion {

    static final int DEFAULT_THROTTLE_TIMEOUT = 1000;
    private DomListenerRegistration motionListener;
    private UI ui;

    public interface MotionListener {
        void deviceMotionUpdate(DeviceMotionEvent event);
    }

    public enum Permission {
        GRANTED, DENIED, DEFAULT, NOT_REQUIRED
    }

    /**
     * Starts listening to device motion events and notifies the listener with motion data.
     * Note: On iOS 13+, you must request permission first using
     * {@link DeviceSensorPermissions#configurePermissionRequest} or call
     * {@link #requestPermissionAndListen} instead.
     *
     * @param listener the listener called when device motion is detected
     * @return a DeviceMotion instance that can be used to stop listening
     */
    public static DeviceMotion listen(MotionListener listener) {
        UI ui = UI.getCurrent();
        return listen(ui, listener);
    }

    /**
     * Functional interface for handling DeviceMotion instance after permission is granted.
     */
    @FunctionalInterface
    public interface DeviceMotionHandler {
        void onDeviceMotionCreated(DeviceMotion deviceMotion);
    }

    /**
     * Requests permission (if needed) and then starts listening to device motion events.
     * This is a convenience method that combines permission request with listening.
     * The button click will trigger the permission request on iOS 13+.
     * The handler is called with the DeviceMotion instance so you can configure it (e.g., throttling).
     *
     * @param button the button that will trigger the permission request
     * @param listener the listener called when device motion is detected
     * @param handler callback with DeviceMotion instance for configuration (can be null)
     * @param onError callback if permission is denied
     * @return the configured button
     */
    public static Button requestPermissionAndListen(Button button, MotionListener listener,
                                                     DeviceMotionHandler handler, Runnable onError) {
        final DeviceMotion[] holder = new DeviceMotion[1];

        DeviceSensorPermissions.configurePermissionRequest(button,
            DeviceSensorPermissions.SensorType.MOTION,
            () -> {
                holder[0] = DeviceMotion.listen(listener);
                if (handler != null) {
                    handler.onDeviceMotionCreated(holder[0]);
                }
            },
            onError
        );

        // Update button to toggle between start/stop
        button.addClickListener(e -> {
            if (holder[0] != null) {
                holder[0].cancel();
                holder[0] = null;
            }
        });

        return button;
    }

    /**
     * Requests permission (if needed) and then starts listening to device motion events.
     * This is a convenience method that combines permission request with listening.
     * The button click will trigger the permission request on iOS 13+.
     *
     * @param button the button that will trigger the permission request
     * @param listener the listener called when device motion is detected
     * @param onError callback if permission is denied
     * @return the configured button
     */
    public static Button requestPermissionAndListen(Button button, MotionListener listener, Runnable onError) {
        return requestPermissionAndListen(button, listener, null, onError);
    }

    /**
     * Starts listening to device motion events and notifies the listener with motion data.
     *
     * @param ui the UI in which context the motion listener is to be registered
     * @param listener the listener called when device motion is detected
     * @return a DeviceMotion instance that can be used to stop listening
     */
    public static DeviceMotion listen(UI ui, MotionListener listener) {
        DeviceMotion deviceMotion = new DeviceMotion();
        deviceMotion.ui = ui;

        Element eventSourceElement = ui.getElement();
        Component activeModalComponent = ui.getInternals().getActiveModalComponent();
        if (activeModalComponent != null) {
            eventSourceElement = activeModalComponent.getElement();
        }
        deviceMotion.motionListener = eventSourceElement.addEventListener("devicemotionevent", e -> {
            try {
                DeviceMotionEvent motionEvent = e.getEventDetail(DeviceMotionEvent.class);
                listener.deviceMotionUpdate(motionEvent);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        })
                .addEventDetail()
                .throttle(DEFAULT_THROTTLE_TIMEOUT); // limit the event rate to 1 per second by default
        deviceMotion.motionListener.addEventData("event.detail");

        // Register the devicemotion event listener
        eventSourceElement.executeJs("""
                var el = $0;
                window.addEventListener('devicemotion', function(e) {
                    const event = new CustomEvent('devicemotionevent', {
                        detail: JSON.stringify({
                            acceleration: e.acceleration ? {
                                x: e.acceleration.x,
                                y: e.acceleration.y,
                                z: e.acceleration.z
                            } : null,
                            accelerationIncludingGravity: e.accelerationIncludingGravity ? {
                                x: e.accelerationIncludingGravity.x,
                                y: e.accelerationIncludingGravity.y,
                                z: e.accelerationIncludingGravity.z
                            } : null,
                            rotationRate: e.rotationRate ? {
                                alpha: e.rotationRate.alpha,
                                beta: e.rotationRate.beta,
                                gamma: e.rotationRate.gamma
                            } : null,
                            interval: e.interval
                        })
                    });
                    el.dispatchEvent(event);
                });
                """, eventSourceElement);

        return deviceMotion;
    }

    /**
     * Stops listening to device motion events.
     */
    public void cancel() {
        clearListener();
    }

    private void clearListener() {
        if (motionListener != null) {
            motionListener.remove();
            motionListener = null;
        }
    }

    /**
     * Sets a throttle timeout for device motion events.
     * Events will be sent at most once per the specified milliseconds.
     * Default is 1000 ms (1 second).
     *
     * @param milliseconds the minimum interval between events in milliseconds
     */
    public void throttleEvents(int milliseconds) {
        motionListener.throttle(milliseconds);
    }

    /**
     * Requests permission to access device motion data.
     * On iOS 13+, this must be called before listening to device motion events.
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
     * Requests permission to access device motion data.
     * On iOS 13+, this must be called before listening to device motion events.
     * The request must be triggered by a user action (e.g., button click).
     *
     * @param ui the UI in which context to request permission
     * @param onSuccess callback to run if permission is granted
     * @param onError callback to run if permission is denied or an error occurs
     */
    public static void requestPermission(UI ui, Runnable onSuccess, Runnable onError) {
        ui.getPage().executeJs("""
            // Check if permission API exists (iOS 13+)
            if (typeof DeviceMotionEvent !== 'undefined' &&
                typeof DeviceMotionEvent.requestPermission === 'function') {
                return DeviceMotionEvent.requestPermission()
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
     * Checks if permission is required for device motion events.
     * Returns true on iOS 13+ Safari, false otherwise.
     *
     * @return CompletableFuture that resolves to true if permission API exists
     */
    public static CompletableFuture<Boolean> isPermissionRequired() {
        return isPermissionRequired(UI.getCurrent());
    }

    /**
     * Checks if permission is required for device motion events.
     * Returns true on iOS 13+ Safari, false otherwise.
     *
     * @param ui the UI in which context to check
     * @return CompletableFuture that resolves to true if permission API exists
     */
    public static CompletableFuture<Boolean> isPermissionRequired(UI ui) {
        return ui.getPage().executeJs("""
            return typeof DeviceMotionEvent !== 'undefined' &&
                   typeof DeviceMotionEvent.requestPermission === 'function';
            """).toCompletableFuture(Boolean.class);
    }

    /**
     * Checks the current permission state for device motion events.
     *
     * @return CompletableFuture that resolves to the permission state
     */
    public static CompletableFuture<Permission> checkPermission() {
        return checkPermission(UI.getCurrent());
    }

    /**
     * Checks the current permission state for device motion events.
     *
     * @param ui the UI in which context to check
     * @return CompletableFuture that resolves to the permission state
     */
    public static CompletableFuture<Permission> checkPermission(UI ui) {
        return ui.getPage().executeJs("""
            if (typeof DeviceMotionEvent !== 'undefined' &&
                typeof DeviceMotionEvent.requestPermission === 'function') {
                // iOS 13+ - check permission state (note: can't actually check state before requesting)
                return 'default';
            } else {
                return 'not_required';
            }
            """).toCompletableFuture(String.class)
                .thenApply(str -> Permission.valueOf(str.toUpperCase()));
    }
}
