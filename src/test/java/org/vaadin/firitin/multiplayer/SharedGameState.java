package org.vaadin.firitin.multiplayer;

import com.vaadin.flow.component.UI;
import in.virit.color.Color;
import in.virit.color.NamedColor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Application-scoped bean that maintains the shared game state for multiplayer puck game.
 * Manages puck reservations and broadcasts updates to all connected players.
 */
@Service
public class SharedGameState {

    private static final int MAX_PUCKS = 4;
    private static final Color[] PUCK_COLORS = {
            NamedColor.MISTYROSE,
            NamedColor.TURQUOISE,
            NamedColor.YELLOWGREEN,
            NamedColor.MINTCREAM
    };

    // Map of puck ID to PuckState
    private final Map<String, PuckState> pucks = new ConcurrentHashMap<>();

    // Map of UI to reserved puck ID
    private final Map<UI, String> uiToPuck = new ConcurrentHashMap<>();

    // List of listeners to notify on state changes
    private final List<GameStateListener> listeners = new CopyOnWriteArrayList<>();

    public SharedGameState() {
        // Initialize all pucks
        for (int i = 0; i < MAX_PUCKS; i++) {
            String puckId = "puck-" + i;
            pucks.put(puckId, new PuckState(puckId, PUCK_COLORS[i]));
        }
    }

    /**
     * Reserve a puck for a UI. Returns the reserved puck or null if none available.
     */
    public synchronized PuckState reservePuck(UI ui) {
        // Check if UI already has a puck
        if (uiToPuck.containsKey(ui)) {
            String existingPuckId = uiToPuck.get(ui);
            return pucks.get(existingPuckId);
        }

        // Find first available puck
        for (PuckState puck : pucks.values()) {
            if (!puck.isReserved()) {
                puck.setReserved(true);
                puck.setOwnerUI(ui);
                uiToPuck.put(ui, puck.getId());
                notifyStateChange();
                return puck;
            }
        }
        return null; // No pucks available
    }

    /**
     * Release a puck owned by a UI.
     */
    public synchronized void releasePuck(UI ui) {
        String puckId = uiToPuck.remove(ui);
        if (puckId != null) {
            PuckState puck = pucks.get(puckId);
            if (puck != null) {
                puck.setReserved(false);
                puck.setOwnerUI(null);
                puck.reset();
                notifyStateChange();
            }
        }
    }

    /**
     * Update a puck's position and velocity.
     */
    public synchronized void updatePuck(String puckId, double x, double y, double vx, double vy) {
        PuckState puck = pucks.get(puckId);
        if (puck != null) {
            puck.setX(x);
            puck.setY(y);
            puck.setVx(vx);
            puck.setVy(vy);
            notifyStateChange();
        }
    }

    /**
     * Get all pucks (for rendering).
     */
    public Collection<PuckState> getAllPucks() {
        return new ArrayList<>(pucks.values());
    }

    /**
     * Get a specific puck by ID.
     */
    public PuckState getPuck(String puckId) {
        return pucks.get(puckId);
    }

    /**
     * Register a listener for state changes.
     */
    public void addListener(GameStateListener listener) {
        listeners.add(listener);
    }

    /**
     * Unregister a listener.
     */
    public void removeListener(GameStateListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notify all listeners of state changes.
     */
    private void notifyStateChange() {
        for (GameStateListener listener : listeners) {
            try {
                listener.onStateChanged(this);
            } catch (Exception e) {
                // Ignore errors in listeners
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the puck ID reserved by a UI.
     */
    public String getPuckIdForUI(UI ui) {
        return uiToPuck.get(ui);
    }

    /**
     * Listener interface for game state changes.
     */
    public interface GameStateListener {
        void onStateChanged(SharedGameState state);
    }

    /**
     * Represents the state of a single puck.
     */
    public static class PuckState {
        private final String id;
        private final Color color;
        private boolean reserved;
        private UI ownerUI;

        // Physics state (0-100 percentage-based)
        private double x = 50;
        private double y = 50;
        private double vx = 0;
        private double vy = 0;

        public PuckState(String id, Color color) {
            this.id = id;
            this.color = color;
            this.reserved = false;
        }

        public void reset() {
            x = 50;
            y = 50;
            vx = 0;
            vy = 0;
        }

        // Getters and setters
        public String getId() { return id; }

        public Color getColor() {
            return color;
        }

        public boolean isReserved() { return reserved; }
        public void setReserved(boolean reserved) { this.reserved = reserved; }
        public UI getOwnerUI() { return ownerUI; }
        public void setOwnerUI(UI ownerUI) { this.ownerUI = ownerUI; }
        public double getX() { return x; }
        public void setX(double x) { this.x = x; }
        public double getY() { return y; }
        public void setY(double y) { this.y = y; }
        public double getVx() { return vx; }
        public void setVx(double vx) { this.vx = vx; }
        public double getVy() { return vy; }
        public void setVy(double vy) { this.vy = vy; }
    }
}
