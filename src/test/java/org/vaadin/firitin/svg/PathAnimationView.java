package org.vaadin.firitin.svg;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import in.virit.color.HexColor;
import org.vaadin.firitin.components.VSvg;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.element.svg.PathElement;
import org.vaadin.firitin.element.svg.RectElement;

import java.time.Duration;

/**
 * Test view demonstrating the PathElement.animateD() API.
 * <p>
 * This view shows various examples of animating path data using the
 * Consumer&lt;PathBuilder&gt; lambda-based API.
 * </p>
 */
@Route
public class PathAnimationView extends VVerticalLayout {

    public PathAnimationView() {
        add(new H2("Path Animation (animateD) Examples"));
        add(new Paragraph("Demonstrates the PathElement.animateD() API for animating path data."));

        add(createBasicAnimationExample());
        add(createFromToAnimationExample());
        add(createMorphingShapesExample());
        add(createEasingExample());
    }

    /**
     * Example 1: Basic animation from current value.
     * Uses animateD(to, duration) - animates from current path to target.
     */
    private VVerticalLayout createBasicAnimationExample() {
        VVerticalLayout section = new VVerticalLayout();
        section.add(new H3("1. Basic Animation (from current value)"));
        section.add(new Paragraph("Click buttons to animate the triangle to different positions."));

        VSvg svg = new VSvg(0, 0, 300, 150);
        svg.getElement().getStyle().setBorder("1px solid black");

        RectElement bg = new RectElement()
                .position(0, 0)
                .size(300, 150)
                .fill(HexColor.of("#f5f5f5"))
                .noStroke();

        // Initial triangle shape
        PathElement path = new PathElement()
                .d(p -> p.moveTo(50, 120).lineTo(100, 30).lineTo(150, 120).closePath())
                .fill(HexColor.of("#3366cc"))
                .stroke(HexColor.of("#000000"))
                .strokeWidth(2);

        svg.getElement().appendChild(bg, path);

        Button moveRight = new Button("Move Right", e ->
                path.animateD(
                        to -> to.moveTo(150, 120).lineTo(200, 30).lineTo(250, 120).closePath(),
                        Duration.ofMillis(400)
                )
        );

        Button moveLeft = new Button("Move Left", e ->
                path.animateD(
                        to -> to.moveTo(50, 120).lineTo(100, 30).lineTo(150, 120).closePath(),
                        Duration.ofMillis(400)
                )
        );

        Button grow = new Button("Grow", e ->
                path.animateD(
                        to -> to.moveTo(30, 140).lineTo(150, 10).lineTo(270, 140).closePath(),
                        Duration.ofMillis(400)
                )
        );

        section.add(svg);
        section.add(new VHorizontalLayout(moveLeft, moveRight, grow));
        return section;
    }

    /**
     * Example 2: Explicit from/to animation.
     * Uses animateD(from, to, duration) - explicit start and end paths.
     */
    private VVerticalLayout createFromToAnimationExample() {
        VVerticalLayout section = new VVerticalLayout();
        section.add(new H3("2. Explicit From/To Animation"));
        section.add(new Paragraph("Uses explicit from and to paths for predictable animation."));

        VSvg svg = new VSvg(0, 0, 300, 150);
        svg.getElement().getStyle().setBorder("1px solid black");

        RectElement bg = new RectElement()
                .position(0, 0)
                .size(300, 150)
                .fill(HexColor.of("#f5f5f5"))
                .noStroke();

        PathElement path = new PathElement()
                .d(p -> p.moveTo(50, 75).lineTo(150, 25).lineTo(250, 75).lineTo(150, 125).closePath())
                .fill(HexColor.of("#cc3366"))
                .stroke(HexColor.of("#000000"))
                .strokeWidth(2);

        svg.getElement().appendChild(bg, path);

        Button animate = new Button("Animate Diamond", e ->
                path.animateD(
                        from -> from.moveTo(50, 75).lineTo(150, 25).lineTo(250, 75).lineTo(150, 125).closePath(),
                        to -> to.moveTo(100, 75).lineTo(150, 50).lineTo(200, 75).lineTo(150, 100).closePath(),
                        Duration.ofMillis(500)
                )
        );

        Button reset = new Button("Reset", e ->
                path.animateD(
                        to -> to.moveTo(50, 75).lineTo(150, 25).lineTo(250, 75).lineTo(150, 125).closePath(),
                        Duration.ofMillis(300)
                )
        );

        section.add(svg);
        section.add(new VHorizontalLayout(animate, reset));
        return section;
    }

    /**
     * Example 3: Morphing between different shapes.
     * Both paths must have the same number and type of commands for smooth morphing.
     */
    private VVerticalLayout createMorphingShapesExample() {
        VVerticalLayout section = new VVerticalLayout();
        section.add(new H3("3. Shape Morphing"));
        section.add(new Paragraph("Morphs between different polygon shapes (same command count)."));

        VSvg svg = new VSvg(0, 0, 300, 150);
        svg.getElement().getStyle().setBorder("1px solid black");

        RectElement bg = new RectElement()
                .position(0, 0)
                .size(300, 150)
                .fill(HexColor.of("#f5f5f5"))
                .noStroke();

        // Pentagon shape (5 points)
        PathElement path = new PathElement()
                .d(p -> p
                        .moveTo(150, 20)
                        .lineTo(220, 55)
                        .lineTo(195, 130)
                        .lineTo(105, 130)
                        .lineTo(80, 55)
                        .closePath())
                .fill(HexColor.of("#66cc33"))
                .stroke(HexColor.of("#000000"))
                .strokeWidth(2);

        svg.getElement().appendChild(bg, path);

        // Star shape (5 points, same command structure)
        Button toStar = new Button("To Star", e ->
                path.animateD(
                        to -> to
                                .moveTo(150, 10)
                                .lineTo(170, 60)
                                .lineTo(230, 60)
                                .lineTo(180, 95)
                                .lineTo(200, 145)
                                .lineTo(150, 115)
                                .lineTo(100, 145)
                                .lineTo(120, 95)
                                .lineTo(70, 60)
                                .lineTo(130, 60)
                                .closePath(),
                        Duration.ofMillis(600)
                )
        );

        // Back to pentagon
        Button toPentagon = new Button("To Pentagon", e ->
                path.animateD(
                        to -> to
                                .moveTo(150, 20)
                                .lineTo(220, 55)
                                .lineTo(195, 130)
                                .lineTo(105, 130)
                                .lineTo(80, 55)
                                .lineTo(150, 20)
                                .lineTo(220, 55)
                                .lineTo(195, 130)
                                .lineTo(105, 130)
                                .lineTo(80, 55)
                                .closePath(),
                        Duration.ofMillis(600)
                )
        );

        section.add(svg);
        section.add(new VHorizontalLayout(toStar, toPentagon));
        section.add(new Paragraph("Note: Star has more points, so pentagon is repeated to match command count."));
        return section;
    }

    /**
     * Example 4: Animation with easing.
     * Demonstrates chaining .easeInOut() on the returned AnimateElement.
     */
    private VVerticalLayout createEasingExample() {
        VVerticalLayout section = new VVerticalLayout();
        section.add(new H3("4. Animation with Easing"));
        section.add(new Paragraph("Demonstrates easing functions on path animations."));

        VSvg svg = new VSvg(0, 0, 300, 150);
        svg.getElement().getStyle().setBorder("1px solid black");

        RectElement bg = new RectElement()
                .position(0, 0)
                .size(300, 150)
                .fill(HexColor.of("#f5f5f5"))
                .noStroke();

        PathElement path = new PathElement()
                .d(p -> p.moveTo(30, 130).lineTo(80, 20).lineTo(130, 130).closePath())
                .fill(HexColor.of("#9933cc"))
                .stroke(HexColor.of("#000000"))
                .strokeWidth(2);

        svg.getElement().appendChild(bg, path);

        Button bounceRight = new Button("Ease In-Out Right", e ->
                path.animateD(
                        to -> to.moveTo(170, 130).lineTo(220, 20).lineTo(270, 130).closePath(),
                        Duration.ofMillis(800)
                ).easeInOut()
        );

        Button bounceLeft = new Button("Ease In-Out Left", e ->
                path.animateD(
                        to -> to.moveTo(30, 130).lineTo(80, 20).lineTo(130, 130).closePath(),
                        Duration.ofMillis(800)
                ).easeInOut()
        );

        Button linear = new Button("Linear (no easing)", e ->
                path.animateD(
                        to -> to.moveTo(100, 130).lineTo(150, 20).lineTo(200, 130).closePath(),
                        Duration.ofMillis(800)
                ) // No easing - linear by default
        );

        section.add(svg);
        section.add(new VHorizontalLayout(bounceLeft, linear, bounceRight));
        return section;
    }
}
