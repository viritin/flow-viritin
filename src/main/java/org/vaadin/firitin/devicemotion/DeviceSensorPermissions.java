package org.vaadin.firitin.devicemotion;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;

/**
 * Helper class for handling device sensor permissions (motion and orientation).
 * On iOS 13+, these permissions must be requested from a user-initiated action (click event).
 * This class provides utilities to handle permissions seamlessly.
 *
 * @author mstahv
 */
public class DeviceSensorPermissions {

    public enum SensorType {
        MOTION, ORIENTATION, BOTH
    }

    /**
     * Creates a button that requests the specified permissions on click,
     * then executes the onSuccess callback if permissions are granted.
     *
     * @param buttonText the text for the button
     * @param sensorType which sensor permissions to request
     * @param onSuccess callback to execute after permissions are granted (on server side)
     * @param onError callback to execute if permissions are denied (on server side)
     * @return a configured button
     */
    public static Button createRequestPermissionButton(String buttonText, SensorType sensorType,
                                                       Runnable onSuccess, Runnable onError) {
        Button button = new Button(buttonText);
        configurePermissionRequest(button, sensorType, onSuccess, onError);
        return button;
    }

    /**
     * Configures an existing button to request permissions on click,
     * then execute the action callback on the server side.
     * This allows seamless integration - clicking the button requests permissions
     * if needed, then executes the intended action.
     *
     * @param button the button to configure
     * @param sensorType which sensor permissions to request
     * @param onSuccess callback to execute after permissions are granted (on server side)
     * @param onError callback to execute if permissions are denied (on server side)
     */
    public static void configurePermissionRequest(Button button, SensorType sensorType,
                                                   Runnable onSuccess, Runnable onError) {
        UI ui = UI.getCurrent();

        // Add client-side click listener that handles permissions
        String jsCode = generatePermissionRequestJS(sensorType);

        button.getElement().executeJs("""
            const btn = this;
            const originalClick = btn.onclick;

            // Store callbacks on the element
            btn._permissionSuccess = () => {
                $0.$server.onPermissionGranted();
            };
            btn._permissionError = () => {
                $1.$server.onPermissionDenied();
            };

            // Override click to handle permissions first
            btn.addEventListener('click', async function(e) {
                // Check if permissions are needed
                const needsMotion = typeof DeviceMotionEvent !== 'undefined' &&
                                   typeof DeviceMotionEvent.requestPermission === 'function';
                const needsOrientation = typeof DeviceOrientationEvent !== 'undefined' &&
                                        typeof DeviceOrientationEvent.requestPermission === 'function';

                const sensorType = $2;

                if (!needsMotion && !needsOrientation) {
                    // No permissions needed, proceed directly
                    btn._permissionSuccess();
                    return;
                }

                try {
                    // Request permissions based on sensor type
                    if (sensorType === 'BOTH' || sensorType === 'MOTION') {
                        if (needsMotion) {
                            const motionState = await DeviceMotionEvent.requestPermission();
                            if (motionState !== 'granted') {
                                btn._permissionError();
                                return;
                            }
                        }
                    }

                    if (sensorType === 'BOTH' || sensorType === 'ORIENTATION') {
                        if (needsOrientation) {
                            const orientationState = await DeviceOrientationEvent.requestPermission();
                            if (orientationState !== 'granted') {
                                btn._permissionError();
                                return;
                            }
                        }
                    }

                    // All permissions granted
                    btn._permissionSuccess();
                } catch (error) {
                    console.error('Permission request failed:', error);
                    btn._permissionError();
                }
            });
            """,
            button.getElement(),
            button.getElement(),
            sensorType.name()
        );

        // Add server-side listeners
        button.getElement().addAttachListener(event -> {
            ui.getPage().executeJs("""
                const btn = $0;
                btn.$server = {
                    onPermissionGranted: () => {
                        btn.dispatchEvent(new CustomEvent('permission-granted'));
                    },
                    onPermissionDenied: () => {
                        btn.dispatchEvent(new CustomEvent('permission-denied'));
                    }
                };
                """, button.getElement());
        });

        button.getElement().addEventListener("permission-granted", e -> {
            if (onSuccess != null) {
                onSuccess.run();
            }
        });

        button.getElement().addEventListener("permission-denied", e -> {
            if (onError != null) {
                onError.run();
            }
        });
    }

    /**
     * Checks if device sensor permissions are required on this platform.
     * Returns true on iOS 13+ Safari.
     *
     * @param ui the UI context
     * @param sensorType which sensor to check
     * @return true if permission request is needed
     */
    public static boolean isPermissionRequired(UI ui, SensorType sensorType) {
        String jsCheck = switch (sensorType) {
            case MOTION -> """
                return typeof DeviceMotionEvent !== 'undefined' &&
                       typeof DeviceMotionEvent.requestPermission === 'function';
                """;
            case ORIENTATION -> """
                return typeof DeviceOrientationEvent !== 'undefined' &&
                       typeof DeviceOrientationEvent.requestPermission === 'function';
                """;
            case BOTH -> """
                return (typeof DeviceMotionEvent !== 'undefined' &&
                        typeof DeviceMotionEvent.requestPermission === 'function') ||
                       (typeof DeviceOrientationEvent !== 'undefined' &&
                        typeof DeviceOrientationEvent.requestPermission === 'function');
                """;
        };

        return ui.getPage().executeJs(jsCheck).toCompletableFuture(Boolean.class).join();
    }

    private static String generatePermissionRequestJS(SensorType sensorType) {
        return switch (sensorType) {
            case MOTION -> "DeviceMotionEvent.requestPermission()";
            case ORIENTATION -> "DeviceOrientationEvent.requestPermission()";
            case BOTH -> """
                Promise.all([
                    DeviceMotionEvent.requestPermission(),
                    DeviceOrientationEvent.requestPermission()
                ])
                """;
        };
    }
}
