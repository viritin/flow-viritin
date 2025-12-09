package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.html.VDiv;
import org.vaadin.firitin.devicemotion.DeviceMotion;
import org.vaadin.firitin.devicemotion.ScreenOrientation;
import org.vaadin.firitin.devicemotion.ScreenOrientationInfo;
import org.vaadin.firitin.util.VStyle;

@Route("puck-game")
public class PuckGameWithMotionAndOrientation extends Div {

    private DeviceMotion deviceMotion;
    private ScreenOrientation screenOrientation;
    private GameBoard gameBoard;
    private boolean isPlaying = false;

    public PuckGameWithMotionAndOrientation() {
        setSizeFull();

        add(new Paragraph("Puck Game - Tilt your device to move the puck!"));
        add(new Paragraph("Requires HTTPS. Click 'Start Game' to begin."));

        // Create game board
        gameBoard = new GameBoard();

        // Initialize game with client-side script
        initializeGame();

        add(gameBoard);

        // Controls
        Button startButton = new Button("Start Game");
        Button resetButton = new Button("Reset Puck", e -> resetPuck());

        // Set up screen orientation listener
        screenOrientation = ScreenOrientation.listen(orientation -> {
            System.out.println("Orientation changed: " + orientation);
            ScreenOrientationInfo.OrientationType orientationType = orientation.getOrientationType();
            // In iOS, DeviceMotion is "normal" when in portrait-primary, degrees depend on device (iphone 0, ipad 90)
            // Rotate the GameBoard based on orientation for better UX
            // TODO test in some android devices, probably broken there...
            switch (orientationType) {
                case PORTRAIT_PRIMARY:
                    gameBoard.rotate(0);
                    break;
                case LANDSCAPE_PRIMARY:
                    gameBoard.rotate(-90);
                    break;
                case PORTRAIT_SECONDARY:
                    gameBoard.rotate(180);
                    break;
                case LANDSCAPE_SECONDARY:
                    gameBoard.rotate(90);
                    break;
            }
        });

        // Use requestPermissionAndListen to handle permissions automatically
        DeviceMotion.requestPermissionAndListen(startButton,
            event -> {
                if (event.getAccelerationIncludingGravity() != null && isPlaying) {
                    Double x = event.getAccelerationIncludingGravity().getX();
                    Double y = event.getAccelerationIncludingGravity().getY();

                    if (x != null && y != null) {
                        // Transform acceleration based on screen orientation
                        gameBoard.applyAcceleration(x,-y);
                    }
                }
            },
            deviceMotion -> {
                // Configure throttling for smooth movement (20 updates per second)
                deviceMotion.throttleEvents(300);
            },
            () -> {
                Notification.show("Motion permission denied!");
                if (isPlaying) {
                    stopGame(startButton);
                }
            }
        );

        // Override click to handle start/stop
        startButton.getElement().addEventListener("click", e -> {
            if (!isPlaying) {
                startGame(startButton);
            } else {
                stopGame(startButton);
            }
        }).addEventData("event.preventDefault()");

        add(new HorizontalLayout(startButton, resetButton));

        add(new Paragraph("Tilt your device to move the red puck around the board."));
    }

    private void initializeGame() {
        // Game is now initialized in GameBoard constructor
        gameBoard.reset();
    }

    private void startGame(Button startButton) {
        isPlaying = true;
        startButton.setText("Stop Game");
        Notification.show("Game started! Tilt your device to move the puck.");
    }

    private void stopGame(Button startButton) {
        isPlaying = false;
        startButton.setText("Start Game");
        Notification.show("Game stopped");
    }

    private void resetPuck() {
        gameBoard.reset();
    }

    private static class GameBoard extends Div {

        private static final double FRICTION = 0.96;
        private static final double BOUNCE = 0.5;
        private static final double ACCELERATION_SCALE = 0.5;
        private static final int PUCK_SIZE_PERCENT = 8; // Puck is 8% of board size

        // Physics state (0-100 for percentage-based positioning)
        private double x = 50.0;
        private double y = 50.0;
        private double vx = 0;
        private double vy = 0;

        private final Div puck = new VDiv(){{
            new VStyle() {{
                setPosition(Position.ABSOLUTE);
                setWidth(PUCK_SIZE_PERCENT + "%");
                setHeight(PUCK_SIZE_PERCENT + "%");
                setBorderRadius("50%");
                setBackground("radial-gradient(circle at 30% 30%, #ff6b6b, #c92a2a)");
                setBoxShadow("0 4px 10px rgba(0,0,0,0.4), inset -2px -2px 5px rgba(0,0,0,0.3)");
                setBorder("2px solid #fff");
                setTransition("1s all ease-out");
            }}.apply(this);
        }};

        {
            setWidth("min(100vw, 100vh, 600px)");
            setHeight("min(100vw, 100vh, 600px)");
            getStyle()
                    .setPosition(Style.Position.RELATIVE)
                    .setBorder("3px solid #333")
                    .setBackground("linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                    .setBorderRadius("10px")
                    .setBoxShadow("0 10px 30px rgba(0,0,0,0.3)")
                    .setTransition("transform 1.0s ease-out")
                    .setMargin("20px auto")
                    .set("aspect-ratio", "1");

            add(puck);
            updatePuckPosition();
        }

        public void applyAcceleration(double ax, double ay) {

            // Apply acceleration (scaled for better control)
            vx += ax * ACCELERATION_SCALE;
            vy += ay * ACCELERATION_SCALE;

            // Apply friction
            vx *= FRICTION;
            vy *= FRICTION;

            // Update position
            x += vx;
            y += vy;

            // Boundary collision with bounce (puck size is PUCK_SIZE_PERCENT)
            double halfPuck = PUCK_SIZE_PERCENT / 2.0;
            if (x - halfPuck < 0) {
                x = halfPuck;
                vx = Math.abs(vx) * BOUNCE;
            } else if (x + halfPuck > 100) {
                x = 100 - halfPuck;
                vx = -Math.abs(vx) * BOUNCE;
            }

            if (y - halfPuck < 0) {
                y = halfPuck;
                vy = Math.abs(vy) * BOUNCE;
            } else if (y + halfPuck > 100) {
                y = 100 - halfPuck;
                vy = -Math.abs(vy) * BOUNCE;
            }

            updatePuckPosition();
        }

        public void reset() {
            x = 50.0;
            y = 50.0;
            vx = 0;
            vy = 0;
            updatePuckPosition();
        }

        private void updatePuckPosition() {
            double halfPuck = PUCK_SIZE_PERCENT / 2.0;
            puck.getStyle()
                    .setLeft((x - halfPuck) + "%")
                    .setTop((y - halfPuck) + "%");
        }

        public void rotate(int deg) {
            getStyle().setTransform("rotate(" + deg + "deg)");
        }
    }
}
