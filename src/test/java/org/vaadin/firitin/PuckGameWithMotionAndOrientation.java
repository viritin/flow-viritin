package org.vaadin.firitin;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.html.VDiv;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.devicemotion.DeviceMotion;
import org.vaadin.firitin.devicemotion.DeviceOrientation;
import org.vaadin.firitin.util.VStyle;

@Route("puck-game")
public class PuckGameWithMotionAndOrientation extends VVerticalLayout {

    private DeviceMotion deviceMotion;
    private DeviceOrientation deviceOrientation;
    private GameBoard gameBoard;
    private boolean isPlaying = false;
    private Checkbox rotateWithDevice;
    private double currentRotation = 0;

    public PuckGameWithMotionAndOrientation() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(new Paragraph("Puck Game - Tilt your device to move the puck!"));
        add(new Paragraph("Requires HTTPS. Click 'Start Game' to begin."));

        // Create game board
        gameBoard = new GameBoard();

        // Initialize game with client-side script
        initializeGame();

        add(gameBoard);

        // Controls
        rotateWithDevice = new Checkbox("Rotate board with device orientation");
        rotateWithDevice.setValue(false);

        Button startButton = new Button("Start Game");
        startButton.addClickListener(e -> {
            if (!isPlaying) {
                startGame(startButton);
            } else {
                stopGame(startButton);
            }
        });

        Button resetButton = new Button("Reset Puck", e -> resetPuck());

        add(new HorizontalLayout(startButton, resetButton, rotateWithDevice));

        // Info panel
        Paragraph info = new Paragraph();
        info.getStyle().set("font-size", "14px").set("color", "#666");
        info.setText("Tilt your device to move the red puck around the board. " +
                "Enable rotation to match the board orientation with your device.");
        add(info);
    }

    private void initializeGame() {
        // Game is now initialized in GameBoard constructor
        gameBoard.reset();
    }

    private void startGame(Button startButton) {
        isPlaying = true;
        startButton.setText("Stop Game");

        // Start device motion listening
        deviceMotion = DeviceMotion.listen(event -> {
            // Use accelerationIncludingGravity as it's more widely supported
            if (event.getAccelerationIncludingGravity() != null) {
                Double x = event.getAccelerationIncludingGravity().getX();
                Double y = event.getAccelerationIncludingGravity().getY();

                if (x != null && y != null) {
                    // Apply acceleration to puck (inverted for natural tilt control)
                    // Pass current board rotation so physics compensates
                    gameBoard.applyAcceleration(x, -y, currentRotation);
                }
            }
        });
        deviceMotion.throttleEvents(50); // Update 20 times per second for smooth movement

        // Start orientation listening if rotation is enabled
        if (rotateWithDevice.getValue()) {
            startOrientationListening();
        }

        // Watch checkbox changes
        rotateWithDevice.addValueChangeListener(e -> {
            if (e.getValue() && isPlaying) {
                startOrientationListening();
            } else if (!e.getValue() && deviceOrientation != null) {
                stopOrientationListening();
                gameBoard.rotateBoard(0);
            }
        });

        Notification.show("Game started! Tilt your device to move the puck.");
    }

    private void startOrientationListening() {
        if (deviceOrientation == null) {
            deviceOrientation = DeviceOrientation.listen(event -> {
                if (rotateWithDevice.getValue() && event.getAlpha() != null) {
                    // Alpha represents device rotation around Z axis (compass heading)
                    // Store rotation for physics calculation
                    currentRotation = event.getAlpha();
                    gameBoard.rotateBoard(currentRotation);
                }
            });
            deviceOrientation.throttleEvents(1000);
        }
    }

    private void stopOrientationListening() {
        if (deviceOrientation != null) {
            deviceOrientation.cancel();
            deviceOrientation = null;
            currentRotation = 0;
        }
    }

    private void stopGame(Button startButton) {
        isPlaying = false;
        startButton.setText("Start Game");

        if (deviceMotion != null) {
            deviceMotion.cancel();
            deviceMotion = null;
        }

        stopOrientationListening();
        gameBoard.rotateBoard(0);

        Notification.show("Game stopped");
    }

    private void resetPuck() {
        gameBoard.reset();
    }

    private static class GameBoard extends Div {

        private static final int BOARD_SIZE = 400;
        private static final int PUCK_RADIUS = 40;
        private static final double FRICTION = 0.95;
        private static final double BOUNCE = 0.7;
        private static final double ACCELERATION_SCALE = 2.0;

        // Physics state
        private double x = BOARD_SIZE / 2.0;
        private double y = BOARD_SIZE / 2.0;
        private double vx = 0;
        private double vy = 0;

        private final Div puck = new VDiv(){{
            new VStyle() {{
                setPosition(Position.ABSOLUTE);
                setWidth((PUCK_RADIUS*2) + "px");
                setHeight((PUCK_RADIUS*2) + "px");
                setBorderRadius("50%");
                setBackground("radial-gradient(circle at 30% 30%, #ff6b6b, #c92a2a)");
                setBoxShadow("0 4px 10px rgba(0,0,0,0.4), inset -2px -2px 5px rgba(0,0,0,0.3)");
                setBorder("2px solid #fff");
                setTransition("transform 1.0s ease-out");
            }}.apply(this);
        }};
        private double previousRotation;

        {
            setWidth(BOARD_SIZE + "px");
            setHeight(BOARD_SIZE + "px");
            getStyle()
                    .setBorder("3px solid #333")
                    .setBackground("linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                    .setPosition(Style.Position.RELATIVE)
                    .setMargin("20px auto")
                    .setBorderRadius("10px")
                    .setBoxShadow("0 10px 30px rgba(0,0,0,0.3)")
                    .setOverflow(Style.Overflow.HIDDEN)
                    .setTransition("transform 1.0s ease-out");

            add(puck);
            updatePuckPosition();
        }

        public void applyAcceleration(double ax, double ay, double rotation) {
            // Apply inverse rotation to acceleration vectors so the puck moves correctly
            // relative to the rotated board. When the board rotates, the acceleration
            // vectors need to be rotated in the opposite direction to maintain natural physics.
            if (rotation != 0) {
                // Negative rotation because we want the inverse transformation
                double rad = Math.toRadians(-rotation);
                double cos = Math.cos(rad);
                double sin = Math.sin(rad);
                double rotatedAx = ax * cos - ay * sin;
                double rotatedAy = ax * sin + ay * cos;
                ax = rotatedAx;
                ay = rotatedAy;
            }

            // Apply acceleration (scaled for better control)
            vx += ax * ACCELERATION_SCALE;
            vy += ay * ACCELERATION_SCALE;

            // Apply friction
            vx *= FRICTION;
            vy *= FRICTION;

            // Update position
            x += vx;
            y += vy;

            // Boundary collision with bounce
            if (x - PUCK_RADIUS < 0) {
                x = PUCK_RADIUS;
                vx = Math.abs(vx) * BOUNCE;
            } else if (x + PUCK_RADIUS > BOARD_SIZE) {
                x = BOARD_SIZE - PUCK_RADIUS;
                vx = -Math.abs(vx) * BOUNCE;
            }

            if (y - PUCK_RADIUS < 0) {
                y = PUCK_RADIUS;
                vy = Math.abs(vy) * BOUNCE;
            } else if (y + PUCK_RADIUS > BOARD_SIZE) {
                y = BOARD_SIZE - PUCK_RADIUS;
                vy = -Math.abs(vy) * BOUNCE;
            }

            updatePuckPosition();
        }

        public void reset() {
            x = BOARD_SIZE / 2.0;
            y = BOARD_SIZE / 2.0;
            vx = 0;
            vy = 0;
            updatePuckPosition();
        }

        public void rotateBoard(double degrees) {
            if(degrees - previousRotation > 180) {
                degrees -= 360;
            } else if(degrees - previousRotation < -180) {
                degrees += 360;
            }
            getStyle().set("transform", "rotate(" + degrees + "deg)");
            previousRotation = degrees;
        }

        private void updatePuckPosition() {
            puck.getStyle()
                    .setLeft((x - PUCK_RADIUS) + "px")
                    .setTop((y - PUCK_RADIUS) + "px");
        }
    }
}
