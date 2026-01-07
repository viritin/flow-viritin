package org.vaadin.firitin.svg;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import in.virit.color.Color;
import in.virit.color.HexColor;
import in.virit.color.HslColor;
import in.virit.color.RgbColor;
import org.vaadin.firitin.components.VSvg;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.element.svg.AnimateElement;
import org.vaadin.firitin.element.svg.AnimateTransformElement;
import org.vaadin.firitin.element.svg.CircleElement;
import org.vaadin.firitin.element.svg.DefsElement;
import org.vaadin.firitin.element.svg.EllipseElement;
import org.vaadin.firitin.element.svg.GElement;
import org.vaadin.firitin.element.svg.RadialGradientElement;
import org.vaadin.firitin.element.svg.RectElement;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A test view demonstrating SVG animations with 3D-looking pucks moving on a board.
 * <p>
 * Click on any puck to see its color reported via notification.
 * </p>
 */
@Route
public class AnimatedPucksView extends VVerticalLayout {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> animationTask;
    private final Board board;

    public AnimatedPucksView() {
        add(new H2("Animated Pucks"));
        add(new Paragraph("Three 3D-looking pucks move around the board. Click on any puck to see its color."));

        board = new Board(400, 300);

        // Create pucks with click handlers - highlight and shadow colors are calculated automatically
        Puck redPuck = new Puck("Red", HexColor.of("#cc0000"), 80, 80);
        Puck bluePuck = new Puck("Blue", HexColor.of("#0000cc"), 200, 150);
        Puck greenPuck = new Puck("Green", HexColor.of("#00cc00"), 320, 220);

        redPuck.addClickListener(e -> Notification.show("You clicked the Red puck!"));
        bluePuck.addClickListener(e -> Notification.show("You clicked the Blue puck!"));
        greenPuck.addClickListener(e -> Notification.show("You clicked the Green puck!"));

        board.addPuck(redPuck);
        board.addPuck(bluePuck);
        board.addPuck(greenPuck);

        add(board);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        UI ui = attachEvent.getUI();
        animationTask = scheduler.scheduleAtFixedRate(() -> {
            ui.access(() -> {
                for (Puck puck : board.getPucks()) {
                    puck.animateTo(board.randomX(), board.randomY());
                }
            });
        }, 1, 6, TimeUnit.SECONDS);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        if (animationTask != null) {
            animationTask.cancel(false);
        }
    }

    /**
     * A game board component for displaying and animating pucks.
     */
    static class Board extends VSvg {

        private static final int PUCK_RADIUS = 25;
        private static final int MARGIN = PUCK_RADIUS + 5;

        private final int width;
        private final int height;
        private final DefsElement defs;
        private final java.util.List<Puck> pucks = new java.util.ArrayList<>();
        private final Random random = new Random();

        Board(int width, int height) {
            // Board extends VSvg (which has SvgElement as root)
            super(0, 0, width, height);
            this.width = width;
            this.height = height;

            getElement().getStyle().set("width", width + "px");
            getElement().getStyle().set("height", height + "px");

            // Definitions for gradients
            defs = new DefsElement();

            // Board background
            RectElement boardRect = new RectElement()
                    .position(0, 0)
                    .size(width, height)
                    .fill(HexColor.of("#2d5016"))
                    .stroke(HexColor.of("#1a3009"))
                    .strokeWidth(4);

            // Board surface pattern (simple lines)
            GElement boardPattern = new GElement();
            for (int i = 1; i < 4; i++) {
                RectElement line = new RectElement()
                        .position(i * width / 4 - 1, 10)
                        .size(2, height - 20)
                        .fill(HexColor.of("#3d6020"))
                        .noStroke();
                boardPattern.add(line);
            }

            getElement().appendChild(defs, boardRect, boardPattern);
        }

        void addPuck(Puck puck) {
            pucks.add(puck);
            // Add gradient to defs
            defs.add(puck.getGradient());
            // Add puck element to board
            getElement().appendChild(puck.getElement());
        }

        java.util.List<Puck> getPucks() {
            return pucks;
        }

        double randomX() {
            return MARGIN + random.nextDouble() * (width - 2 * MARGIN);
        }

        double randomY() {
            return MARGIN + random.nextDouble() * (height - 2 * MARGIN);
        }
    }

    /**
     * A 3D-looking puck component with shadow and highlight that respond to
     * a simulated light source at the top-left corner of the board.
     */
    static class Puck extends Component {

        private static final int RADIUS = 25;

        // Board dimensions for light calculations
        private static final double BOARD_WIDTH = 400;
        private static final double BOARD_HEIGHT = 300;

        // Shadow offset range (min at top-left, max at bottom-right)
        private static final double SHADOW_BASE_X = 2;
        private static final double SHADOW_BASE_Y = 3;
        private static final double SHADOW_RANGE_X = 10;
        private static final double SHADOW_RANGE_Y = 12;

        // Highlight offset range (moves toward light source)
        private static final double HIGHLIGHT_BASE_X = -5;
        private static final double HIGHLIGHT_BASE_Y = -5;
        private static final double HIGHLIGHT_RANGE_X = 6;
        private static final double HIGHLIGHT_RANGE_Y = 6;

        private final String colorName;
        private final RadialGradientElement gradient;
        private final GElement group;
        private final CircleElement mainCircle;
        private final EllipseElement shadow;
        private final CircleElement highlight;

        private double currentX;
        private double currentY;
        private AnimateTransformElement groupAnimation;
        private AnimateElement shadowCxAnim;
        private AnimateElement shadowCyAnim;
        private AnimateElement highlightCxAnim;
        private AnimateElement highlightCyAnim;

        Puck(String colorName, Color color, double x, double y) {
            super(new GElement());
            this.colorName = colorName;
            this.currentX = x;
            this.currentY = y;
            this.group = (GElement) getElement();

            // Calculate highlight and shadow colors using HSL manipulation
            HslColor hsl = color.toRgbColor().toHslColor();
            Color highlightColor = hsl.lighten(0.3);
            Color shadowColor = hsl.darken(0.5);

            // Create gradient for this puck
            gradient = new RadialGradientElement(colorName.toLowerCase() + "Gradient")
                    .cx("30%").cy("30%")
                    .r("70%")
                    .addStop(0, highlightColor)
                    .addStop(0.5, color)
                    .addStop(1, shadowColor);

            // Calculate initial shadow/highlight positions based on light
            double[] shadowOffset = calculateShadowOffset(x, y);
            double[] highlightOffset = calculateHighlightOffset(x, y);

            // Shadow (ellipse below the puck) - position based on light direction
            shadow = new EllipseElement()
                    .center(shadowOffset[0], shadowOffset[1])
                    .rx(RADIUS * 0.9)
                    .ry(RADIUS * 0.4)
                    .fill(new RgbColor(0, 0, 0, 0.3))
                    .noStroke();

            // Main puck circle with gradient
            mainCircle = new CircleElement()
                    .center(0, 0)
                    .r(RADIUS)
                    .fill(gradient)
                    .stroke(HexColor.of("#333333"))
                    .strokeWidth(1);

            // Highlight reflection - position based on light direction
            highlight = new CircleElement()
                    .center(highlightOffset[0], highlightOffset[1])
                    .r(6)
                    .fill(new RgbColor(255, 255, 255, 0.4))
                    .noStroke();

            group.add(shadow, mainCircle, highlight);

            // Position the group at initial location
            group.translate(x, y);

            // Make it look clickable
            mainCircle.getStyle().setCursor("pointer");
        }

        /**
         * Calculates shadow offset based on position.
         * Light comes from top-left, so shadow extends toward bottom-right
         * as the puck moves away from the light source.
         */
        private double[] calculateShadowOffset(double x, double y) {
            double normalizedX = x / BOARD_WIDTH;  // 0 at left, 1 at right
            double normalizedY = y / BOARD_HEIGHT; // 0 at top, 1 at bottom
            return new double[] {
                SHADOW_BASE_X + normalizedX * SHADOW_RANGE_X,
                SHADOW_BASE_Y + normalizedY * SHADOW_RANGE_Y
            };
        }

        /**
         * Calculates highlight offset based on position.
         * Highlight faces the light source (top-left), so it moves
         * more toward top-left as puck moves away from light.
         */
        private double[] calculateHighlightOffset(double x, double y) {
            double normalizedX = x / BOARD_WIDTH;
            double normalizedY = y / BOARD_HEIGHT;
            return new double[] {
                HIGHLIGHT_BASE_X - normalizedX * HIGHLIGHT_RANGE_X,
                HIGHLIGHT_BASE_Y - normalizedY * HIGHLIGHT_RANGE_Y
            };
        }

        RadialGradientElement getGradient() {
            return gradient;
        }

        String getColorName() {
            return colorName;
        }

        Registration addClickListener(ComponentEventListener<ClickEvent<Puck>> listener) {
            return mainCircle.addEventListener("click", e ->
                    listener.onComponentEvent(new ClickEvent<>(this)));
        }

        void animateTo(double newX, double newY) {
            removeOldAnimations();

            Duration duration = Duration.ofSeconds(5);

            // Calculate current and new shadow/highlight offsets
            double[] currentShadow = calculateShadowOffset(currentX, currentY);
            double[] newShadow = calculateShadowOffset(newX, newY);
            double[] currentHighlight = calculateHighlightOffset(currentX, currentY);
            double[] newHighlight = calculateHighlightOffset(newX, newY);

            // Animate group position
            groupAnimation = group.animateTranslate()
                    .translateFromTo(currentX, currentY, newX, newY)
                    .dur(duration)
                    .freeze()
                    .easeInOut();
            groupAnimation.beginElement();

            // Animate shadow offset (light effect)
            shadowCxAnim = shadow.animateCx()
                    .from(currentShadow[0])
                    .to(newShadow[0])
                    .dur(duration)
                    .freeze()
                    .easeInOut();
            shadowCxAnim.beginElement();

            shadowCyAnim = shadow.animateCy()
                    .from(currentShadow[1])
                    .to(newShadow[1])
                    .dur(duration)
                    .freeze()
                    .easeInOut();
            shadowCyAnim.beginElement();

            // Animate highlight offset (light effect)
            highlightCxAnim = highlight.animateCx()
                    .from(currentHighlight[0])
                    .to(newHighlight[0])
                    .dur(duration)
                    .freeze()
                    .easeInOut();
            highlightCxAnim.beginElement();

            highlightCyAnim = highlight.animateCy()
                    .from(currentHighlight[1])
                    .to(newHighlight[1])
                    .dur(duration)
                    .freeze()
                    .easeInOut();
            highlightCyAnim.beginElement();

            currentX = newX;
            currentY = newY;
        }

        private void removeOldAnimations() {
            removeAnimation(groupAnimation);
            removeAnimation(shadowCxAnim);
            removeAnimation(shadowCyAnim);
            removeAnimation(highlightCxAnim);
            removeAnimation(highlightCyAnim);
        }

        private void removeAnimation(AnimateElement anim) {
            if (anim != null && anim.getParent() != null) {
                anim.getParent().removeChild(anim);
            }
        }
    }
}