package org.vaadin.firitin;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.vaadin.firitin.components.html.VDiv;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.devicemotion.DeviceMotion;
import org.vaadin.firitin.devicemotion.ScreenOrientation;
import org.vaadin.firitin.devicemotion.ScreenOrientationInfo;
import org.vaadin.firitin.multiplayer.SharedGameState;
import org.vaadin.firitin.multiplayer.SharedGameState.PuckState;
import org.vaadin.firitin.util.VStyle;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Route("multiplayer-puck")
@SpringComponent
@UIScope
public class MultiplayerPuckGame extends VVerticalLayout implements SharedGameState.GameStateListener {

    private final SharedGameState gameState;
    private DeviceMotion deviceMotion;
    private ScreenOrientation screenOrientation;
    private MultiplayerGameBoard gameBoard;
    private PuckState myPuck;
    private Paragraph statusText;
    private UI ui;

    @Autowired
    public MultiplayerPuckGame(SharedGameState gameState) {
        this.gameState = gameState;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(new H2("Multiplayer Puck Game"));
        add(new Paragraph("Up to 4 players can join! Tilt your device to move your puck."));
        add(new Paragraph("Requires HTTPS. Each player gets a different colored puck."));

        statusText = new Paragraph();
        statusText.getStyle().setFontWeight(Style.FontWeight.BOLD);
        add(statusText);

        // Create game board
        gameBoard = new MultiplayerGameBoard();
        add(gameBoard);

        // Controls
        Button startButton = new Button("Join Game");
        Button resetButton = new Button("Reset My Puck", e -> resetMyPuck());
        resetButton.setEnabled(false);

        String ua = VaadinRequest.getCurrent().getHeader("User-Agent");
        if(ua.contains("iPhone") || ua.contains("iPad")) {
            // Basic Screen Lock not available (although relevant), rotate
            // Set up screen orientation listener
            screenOrientation = ScreenOrientation.listen(orientation -> {
                ScreenOrientationInfo.OrientationType orientationType = orientation.getOrientationType();
                // In iOS, DeviceMotion is "normal" when in portrait-primary, degrees depend on device (iphone 0, ipad 90)
                // Rotate the GameBoard based on orientation for better UX
                // TODO test in some android devices, probably broken there...
                System.out.println(LocalTime.now() + "Orientation changed: " + orientationType);
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
        }

        // Configure permission request on start button
        DeviceMotion.requestPermissionAndListen(startButton,
            event -> {
                if (event.getAccelerationIncludingGravity() != null) {
                    Double x = event.getAccelerationIncludingGravity().getX();
                    Double y = event.getAccelerationIncludingGravity().getY();

                    if (isPlaying()) {
                        gameBoard.applyAccelerationToMyPuck(x, -y);
                        gameBoard.renderAllPucks();
                    }
                }
            },
            deviceMotion -> {
                // Configure throttling for smooth movement (~3 updates per second)
                deviceMotion.throttleEvents(300);
            },
            () -> {
                Notification notification = Notification.show("Motion permission denied!");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                if (isPlaying()) {
                    leaveGame(startButton, resetButton);
                }
            }
        );

        // Override click behavior to handle join/leave
        startButton.addClickListener(e -> {
            if (!isPlaying()) {
                joinGame(startButton, resetButton);
            } else {
                leaveGame(startButton, resetButton);
            }
        });

        add(new HorizontalLayout(startButton, resetButton));

        add(new Paragraph("Join the game to reserve a puck. Tilt your device to move it. " +
                "Other players' pucks will appear on your board in real-time!"));

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        this.ui = attachEvent.getUI();
        gameState.addListener(this);
        updateStatus();
        // Initial render of all pucks
        gameBoard.renderAllPucks();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        gameState.removeListener(this);
        if (myPuck != null) {
            gameState.releasePuck(UI.getCurrent());
            myPuck = null;
        }
    }

    public boolean isPlaying() {
        return myPuck != null;
    }

    private void joinGame(Button startButton, Button resetButton) {
        myPuck = gameState.reservePuck(UI.getCurrent());

        if (myPuck == null) {
            Notification notification = Notification.show("Game is full! Maximum 4 players.");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        startButton.setText("Leave Game");
        resetButton.setEnabled(true);
        updateStatus();

        Notification notification = Notification.show("Joined as " + myPuck.getColor().toString() + " puck! Tilt to move.");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void leaveGame(Button startButton, Button resetButton) {
        startButton.setText("Join Game");
        resetButton.setEnabled(false);

        if (myPuck != null) {
            gameState.releasePuck(UI.getCurrent());
            myPuck = null;
        }

        if (screenOrientation != null) {
            screenOrientation.cancel();
            screenOrientation = null;
        }

        updateStatus();
        Notification.show("Left the game");
    }

    private void resetMyPuck() {
        if (myPuck != null) {
            gameBoard.resetPuck(myPuck.getId());
        }
    }

    private void updateStatus() {
        long activePlayers = gameState.getAllPucks().stream()
                .filter(PuckState::isReserved)
                .count();
        statusText.setText("Active players: " + activePlayers + "/4" +
                (myPuck != null ? " | You are: " + myPuck.getColor() : ""));
    }

    Instant lastUpdate = Instant.now();

    @Override
    public void onStateChanged(SharedGameState state) {
        if(isPlaying()) {
            return; // No need, events will redraw on movement
        }
        // for observers...
        if(Instant.now().minusMillis(300).isBefore(lastUpdate)) { // Throttle updates to max 1 per second
            System.out.println(LocalTime.now() + "Skipping update, too frequent");
            return;
        }
        // Update UI on state changes from other players
        ui.access(() -> {
            System.out.println(LocalTime.now() + "State changed, updating pucks");
            gameBoard.renderAllPucks();
            updateStatus();
        });
    }

    private class MultiplayerGameBoard extends Div {

        private static final double FRICTION = 0.97;
        private static final double BOUNCE = 0.5;
        private static final double ACCELERATION_SCALE = .5;
        private static final int PUCK_SIZE_PERCENT = 8; // Puck is 8% of board size

        private final Map<String, Puck> puckElements = new HashMap<>();

        {
            setWidth("min(80vw, 80vh)");
            getStyle()
                    .setPosition(Style.Position.RELATIVE)
                    .setBorder("3px solid #333")
                    .setBackground("linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                    .setBorderRadius("10px")
                    .setBoxShadow("0 10px 30px rgba(0,0,0,0.3)")
                    .setTransition("transform 1.0s ease-out")
                    .setMargin("20px auto")
                    .set("aspect-ratio", "1");
        }

        public void renderAllPucks() {
            // Remove pucks that no longer exist
            puckElements.keySet().removeIf(puckId -> {
                PuckState puck = gameState.getPuck(puckId);
                if (puck == null || !puck.isReserved()) {
                    Div element = puckElements.get(puckId);
                    if (element != null) {
                        remove(element);
                    }
                    return true;
                }
                return false;
            });

            // Add or update all active pucks
            for (PuckState state : gameState.getAllPucks()) {
                if (state.isReserved()) {
                    Puck puck  = puckElements.computeIfAbsent(state.getId(), id -> createPuckElement(state));
                    puck.updatePosition(state.getX(), state.getY());
                }
            }
            lastUpdate = Instant.now();
        }

        private Puck createPuckElement(PuckState state) {
            Puck puck = new Puck(state);
            add(puck);
            return puck;
        }

        public void applyAccelerationToMyPuck(double ax, double ay) {
            if (myPuck == null) return;

            // Get current state
            double x = myPuck.getX();
            double y = myPuck.getY();
            double vx = myPuck.getVx();
            double vy = myPuck.getVy();

            // Apply acceleration
            vx += ax * ACCELERATION_SCALE;
            vy += ay * ACCELERATION_SCALE;

            // Apply friction
            vx *= FRICTION;
            vy *= FRICTION;

            // Update position
            x += vx;
            y += vy;

            // Boundary collision with bounce (percentage-based)
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

            // Update shared state
            gameState.updatePuck(myPuck.getId(), x, y, vx, vy);
        }

        public void resetPuck(String puckId) {
            PuckState puck = gameState.getPuck(puckId);
            if (puck != null) {
                gameState.updatePuck(puckId, 50.0, 50.0, 0, 0);
            }
        }

        public void rotate(int deg) {
            getStyle().setTransform("rotate(" + deg + "deg)");
        }

        private class Puck extends VDiv {
            public Puck(PuckState puck) {
                new VStyle() {{
                    setPosition(Position.ABSOLUTE);
                    setWidth(PUCK_SIZE_PERCENT + "%");
                    setHeight(PUCK_SIZE_PERCENT + "%");
                    setBorderRadius("50%");
                    setBackground("radial-gradient(circle at 30% 30%, " + puck.getColor() + ", " +
                            puck.getColor().toRgbColor().toHslColor().darken(0.3) + ")");
                    setBoxShadow("0 4px 10px rgba(0,0,0,0.4), inset -2px -2px 5px rgba(0,0,0,0.3)");
                    setBorder("2px solid #fff");
                    setTransition("1s all ease-out");
                }}.apply(this);
            }

            public void updatePosition(double x, double y) {
                double halfPuck = PUCK_SIZE_PERCENT / 2.0;
                getStyle()
                        .setLeft((x - halfPuck) + "%")
                        .setTop((y - halfPuck) + "%");
            }
        }
    }
}
