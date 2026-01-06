package org.vaadin.firitin.svg;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import in.virit.color.NamedColor;
import org.vaadin.firitin.components.VSvg;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.element.svg.AnimateTransformElement;
import org.vaadin.firitin.element.svg.CircleElement;
import org.vaadin.firitin.element.svg.GElement;
import org.vaadin.firitin.element.svg.LineElement;
import org.vaadin.firitin.element.svg.PathElement;
import org.vaadin.firitin.element.svg.TextElement;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A test view demonstrating SVG animations with a wind direction indicator.
 * <p>
 * This view displays a compass-like visualization where an arrow shows the
 * current wind direction. A background thread simulates wind direction readings
 * every 5 seconds, and the arrow animates smoothly to the new direction using
 * SVG SMIL animations.
 * </p>
 */
@Route
public class WindDirectionView extends VVerticalLayout {

    private final WindCompass compass;
    private final Span directionLabel;
    private final Span degreesLabel;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> updateTask;
    private final Random random = new Random();

    public WindDirectionView() {
        add(new H2("Wind Direction Indicator"));
        add(new Paragraph("This demonstrates SVG SMIL animations. The arrow animates smoothly " +
                "when the wind direction changes. Simulated readings occur every 5 seconds."));

        compass = new WindCompass();
        compass.setWidth("300px");
        compass.setHeight("300px");

        directionLabel = new Span("--");
        directionLabel.getStyle()
                .setFontSize("24px")
                .setFontWeight("bold");

        degreesLabel = new Span("--°");
        degreesLabel.getStyle()
                .setFontSize("18px")
                .setColor("gray");

        VVerticalLayout infoPanel = new VVerticalLayout() {{
            setSpacing(false);
            setPadding(false);
            add(new Span("Wind from:"));
            add(directionLabel);
            add(degreesLabel);
        }};
        infoPanel.getStyle()
                .setPadding("20px")
                .setBackground("#f5f5f5")
                .setBorderRadius("8px");

        add(new VHorizontalLayout(compass, infoPanel));
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        // Set initial direction
        updateWindDirection(0);

        // Start background thread to simulate wind readings
        UI ui = attachEvent.getUI();
        updateTask = scheduler.scheduleAtFixedRate(() -> {
            // Generate random wind direction (0-359 degrees)
            int newDirection = random.nextInt(360);

            // Update UI from background thread using UI.access()
            ui.access(() -> updateWindDirection(newDirection));

        }, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);

        // Cancel the background task when view is detached
        if (updateTask != null) {
            updateTask.cancel(false);
        }
    }

    private void updateWindDirection(int degrees) {
        compass.setDirection(degrees);
        degreesLabel.setText(degrees + "°");
        directionLabel.setText(getDirectionName(degrees));
    }

    private String getDirectionName(int degrees) {
        // Normalize to 0-360
        degrees = ((degrees % 360) + 360) % 360;

        if (degrees >= 337.5 || degrees < 22.5) return "N";
        if (degrees < 67.5) return "NE";
        if (degrees < 112.5) return "E";
        if (degrees < 157.5) return "SE";
        if (degrees < 202.5) return "S";
        if (degrees < 247.5) return "SW";
        if (degrees < 292.5) return "W";
        return "NW";
    }

    /**
     * A compass visualization component showing wind direction.
     */
    static class WindCompass extends VSvg {

        private static final int SIZE = 100;
        private static final int CENTER = SIZE / 2;
        private static final int RADIUS = 40;

        private final GElement arrowGroup;
        private AnimateTransformElement currentAnimation;
        private int currentDirection = 0;
        private int currentRotation = 0;  // Actual rotation angle (not normalized)

        WindCompass() {
            super(0, 0, SIZE, SIZE);

            // Background circle
            CircleElement background = new CircleElement()
                    .center(CENTER, CENTER)
                    .r(RADIUS + 5)
                    .fill("#e8e8e8")
                    .stroke("#cccccc")
                    .strokeWidth(2);

            // Compass rose (direction markers)
            GElement compassRose = new GElement();

            // Cardinal direction lines
            for (int i = 0; i < 8; i++) {
                double angle = i * 45 * Math.PI / 180;
                double innerR = (i % 2 == 0) ? RADIUS - 8 : RADIUS - 5;
                double outerR = RADIUS;

                LineElement tick = new LineElement()
                        .x1(CENTER + innerR * Math.sin(angle))
                        .y1(CENTER - innerR * Math.cos(angle))
                        .x2(CENTER + outerR * Math.sin(angle))
                        .y2(CENTER - outerR * Math.cos(angle))
                        .stroke("#666666")
                        .strokeWidth(i % 2 == 0 ? 2 : 1);
                compassRose.add(tick);
            }

            // Direction labels
            addDirectionLabel(compassRose, "N", 0);
            addDirectionLabel(compassRose, "E", 90);
            addDirectionLabel(compassRose, "S", 180);
            addDirectionLabel(compassRose, "W", 270);

            // Arrow group - contains the arrow and the animation
            arrowGroup = new GElement();

            // Arrow pointing up (north) - will be rotated
            PathElement arrow = new PathElement()
                    .moveTo(CENTER, CENTER - RADIUS + 12)  // Arrow tip
                    .lineTo(CENTER + 6, CENTER + 5)        // Bottom right
                    .lineTo(CENTER + 2, CENTER + 5)        // Inner right
                    .lineTo(CENTER + 2, CENTER + 15)       // Tail right
                    .lineTo(CENTER - 2, CENTER + 15)       // Tail left
                    .lineTo(CENTER - 2, CENTER + 5)        // Inner left
                    .lineTo(CENTER - 6, CENTER + 5)        // Bottom left
                    .closePath()
                    .fill(NamedColor.CRIMSON)
                    .stroke(NamedColor.DARKRED)
                    .strokeWidth(0.5);

            // Center dot
            CircleElement centerDot = new CircleElement()
                    .center(CENTER, CENTER)
                    .r(4)
                    .fill(NamedColor.DARKRED)
                    .stroke(NamedColor.WHITE)
                    .strokeWidth(1);

            arrowGroup.add(arrow);

            getElement().appendChild(background, compassRose, arrowGroup, centerDot);
        }

        private void addDirectionLabel(GElement parent, String label, int degrees) {
            double angle = degrees * Math.PI / 180;
            double labelR = RADIUS - 15;

            TextElement text = new TextElement(label)
                    .position(CENTER + labelR * Math.sin(angle), CENTER - labelR * Math.cos(angle) + 3)
                    .textAnchor(TextElement.TextAnchor.MIDDLE)
                    .fontSize("8px")
                    .fontWeight(TextElement.FontWeight.BOLD)
                    .fill("#333333");

            parent.add(text);
        }

        /**
         * Sets the wind direction with smooth animation.
         *
         * @param degrees the wind direction in degrees (0 = North, 90 = East, etc.)
         */
        void setDirection(int degrees) {
            // Remove previous animation if exists
            if (currentAnimation != null && currentAnimation.getParent() != null) {
                currentAnimation.getParent().removeChild(currentAnimation);
            }

            // Calculate the shortest rotation path from current position
            int diff = degrees - currentDirection;
            if (diff > 180) diff -= 360;
            if (diff < -180) diff += 360;
            int targetRotation = currentRotation + diff;

            // Create new animation from current rotation to target using typed API
            // Note: Safari may flash briefly when animation changes due to SMIL limitations
            currentAnimation = new AnimateTransformElement()
                    .rotateFromTo(currentRotation, targetRotation, CENTER, CENTER)
                    .dur(Duration.ofSeconds(1))
                    .freeze()
                    .easeOut();

            arrowGroup.appendChild(currentAnimation);

            // Dynamically added SMIL animations need to be explicitly started
            currentAnimation.beginElement();

            // Safari workaround: set base transform after animation completes
            // so Safari has correct position when next animation starts
            arrowGroup.executeJs("setTimeout(() => this.setAttribute('transform', $0), 1100)",
                    "rotate(%d %d %d)".formatted(targetRotation, CENTER, CENTER));

            // Update state: currentRotation tracks actual angle, currentDirection is normalized
            currentRotation = targetRotation;
            currentDirection = ((degrees % 360) + 360) % 360;
        }
    }
}