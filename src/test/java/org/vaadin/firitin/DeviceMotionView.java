package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.devicemotion.*;

@Route
public class DeviceMotionView extends VVerticalLayout {

    private Paragraph accelerationInfo = new Paragraph("Acceleration: waiting for data...");
    private Paragraph accelerationWithGravityInfo = new Paragraph("Acceleration (with gravity): waiting for data...");
    private Paragraph rotationInfo = new Paragraph("Rotation rate: waiting for data...");
    private Paragraph intervalInfo = new Paragraph("Interval: waiting for data...");

    private Paragraph orientationInfo = new Paragraph("Orientation: waiting for data...");
    private Paragraph orientationAbsoluteInfo = new Paragraph("Absolute: waiting for data...");
    private Paragraph compassHeadingInfo = new Paragraph("Compass heading (iOS): waiting for data...");
    private Paragraph compassAccuracyInfo = new Paragraph("Compass accuracy (iOS): waiting for data...");

    public DeviceMotionView() {
        add(new Paragraph("Device Motion & Orientation API Test"));
        add(new Paragraph("This API works on mobile devices with accelerometers and gyroscopes. " +
                "On iOS 13+, permission will be automatically requested when you click the buttons. Requires HTTPS (even on localhost 🤷‍♂️)."));

        add(new H3("Device Motion"));
        add(accelerationInfo);
        add(accelerationWithGravityInfo);
        add(rotationInfo);
        add(intervalInfo);

        add(new Hr());
        add(new H3("Device Orientation"));
        add(orientationInfo);
        add(orientationAbsoluteInfo);
        add(compassHeadingInfo);
        add(compassAccuracyInfo);

        // Create motion button with automatic permission handling
        Button motionButton = new Button("Start Motion Listening");
        DeviceMotion.requestPermissionAndListen(motionButton,
            event -> {
                System.out.println("Device motion event: " + event);

                DeviceMotionAcceleration accel = event.getAcceleration();
                if (accel != null && accel.getX() != null) {
                    accelerationInfo.setText(String.format("Acceleration: x=%.2f, y=%.2f, z=%.2f m/s²",
                            accel.getX(), accel.getY(), accel.getZ()));
                } else {
                    accelerationInfo.setText("Acceleration: not available");
                }

                DeviceMotionAcceleration accelWithGravity = event.getAccelerationIncludingGravity();
                if (accelWithGravity != null && accelWithGravity.getX() != null) {
                    accelerationWithGravityInfo.setText(String.format("Acceleration (with gravity): x=%.2f, y=%.2f, z=%.2f m/s²",
                            accelWithGravity.getX(), accelWithGravity.getY(), accelWithGravity.getZ()));
                } else {
                    accelerationWithGravityInfo.setText("Acceleration (with gravity): not available");
                }

                DeviceMotionRotationRate rotation = event.getRotationRate();
                if (rotation != null && rotation.getAlpha() != null) {
                    rotationInfo.setText(String.format("Rotation rate: alpha=%.2f, beta=%.2f, gamma=%.2f °/s",
                            rotation.getAlpha(), rotation.getBeta(), rotation.getGamma()));
                } else {
                    rotationInfo.setText("Rotation rate: not available");
                }

                if (event.getInterval() != null) {
                    intervalInfo.setText(String.format("Device interval (Vaadin throttles to 1/s): %.2f ms", event.getInterval()));
                } else {
                    intervalInfo.setText("Interval: not available");
                }
            },
            () -> {
                Notification.show("Motion permission denied!");
                resetMotionDisplay();
            }
        );

        // Create orientation button with automatic permission handling
        Button orientationButton = new Button("Start Orientation Listening");
        DeviceOrientation.requestPermissionAndListen(orientationButton,
            event -> {
                System.out.println("Device orientation event: " + event);

                if (event.getAlpha() != null && event.getBeta() != null && event.getGamma() != null) {
                    orientationInfo.setText(String.format("Orientation: alpha=%.2f°, beta=%.2f°, gamma=%.2f°",
                            event.getAlpha(), event.getBeta(), event.getGamma()));
                } else {
                    orientationInfo.setText("Orientation: not available");
                }

                if (event.getAbsolute() != null) {
                    orientationAbsoluteInfo.setText("Absolute: " + event.getAbsolute());
                } else {
                    orientationAbsoluteInfo.setText("Absolute: not available");
                }

                if (event.getWebkitCompassHeading() != null) {
                    compassHeadingInfo.setText(String.format("Compass heading (iOS): %.2f°", event.getWebkitCompassHeading()));
                } else {
                    compassHeadingInfo.setText("Compass heading (iOS): not available");
                }

                if (event.getWebkitCompassAccuracy() != null) {
                    String accuracyText = event.getWebkitCompassAccuracy() == -1
                        ? "unreliable"
                        : String.format("%.2f°", event.getWebkitCompassAccuracy());
                    compassAccuracyInfo.setText("Compass accuracy (iOS): " + accuracyText);
                } else {
                    compassAccuracyInfo.setText("Compass accuracy (iOS): not available");
                }
            },
            () -> {
                Notification.show("Orientation permission denied!");
                resetOrientationDisplay();
            }
        );

        add(new HorizontalLayout(motionButton, orientationButton));
    }

    private void resetMotionDisplay() {
        accelerationInfo.setText("Acceleration: stopped");
        accelerationWithGravityInfo.setText("Acceleration (with gravity): stopped");
        rotationInfo.setText("Rotation rate: stopped");
        intervalInfo.setText("Interval: stopped");
    }

    private void resetOrientationDisplay() {
        orientationInfo.setText("Orientation: stopped");
        orientationAbsoluteInfo.setText("Absolute: stopped");
        compassHeadingInfo.setText("Compass heading (iOS): stopped");
        compassAccuracyInfo.setText("Compass accuracy (iOS): stopped");
    }
}
