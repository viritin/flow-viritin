package org.vaadin.firitin.svg;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import in.virit.color.HexColor;
import org.vaadin.firitin.components.VSvg;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.element.svg.AnimateElement;
import org.vaadin.firitin.element.svg.AnimateMotionElement;
import org.vaadin.firitin.element.svg.AnimateTransformElement;
import org.vaadin.firitin.element.svg.CircleElement;
import org.vaadin.firitin.element.svg.RectElement;

import java.time.Duration;

/**
 * Test view demonstrating SMIL animation examples from MDN.
 * <p>
 * Examples based on:
 * <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Guides/SVG_animation_with_SMIL">
 * MDN SVG Animation with SMIL</a>
 * </p>
 */
@Route
public class SmilAnimationExamplesView extends VVerticalLayout {

    public SmilAnimationExamplesView() {
        add(new H2("SMIL Animation Examples"));
        add(new Paragraph("Examples from MDN SVG Animation with SMIL guide."));

        add(createAttributeAnimationExample());
        add(createTransformAnimationExample());
        add(createLinearMotionExample());
        add(createCurvedMotionExample());
        add(createColorAnimationExample());
        add(createMultipleAnimationsExample());
    }

    /**
     * Example 1: Animating attributes of an element.
     * Animates the cx attribute of a circle.
     */
    private VVerticalLayout createAttributeAnimationExample() {
        VVerticalLayout section = new VVerticalLayout();
        section.add(new H3("1. Attribute Animation"));
        section.add(new Paragraph("Animates the cx attribute of a circle, moving it horizontally."));

        VSvg svg = new VSvg(0, 0, 300, 100);
        svg.getElement().getStyle().set("border", "1px solid black");

        // Background
        RectElement bg = new RectElement()
                .position(0, 0)
                .size(300, 100)
                .fill(HexColor.of("#f0f0f0"))
                .noStroke();

        // Animated circle
        CircleElement circle = new CircleElement()
                .center(0, 50)
                .r(15)
                .fill(HexColor.of("#3366cc"))
                .stroke(HexColor.of("#000000"))
                .strokeWidth(1);

        // Animation for cx attribute
        AnimateElement anim = new AnimateElement()
                .attributeName("cx")
                .from(15)
                .to(285)
                .dur(Duration.ofSeconds(3))
                .repeatIndefinitely();

        circle.appendChild(anim);
        svg.getElement().appendChild(bg, circle);

        section.add(svg);
        return section;
    }

    /**
     * Example 2: Animating transform attributes.
     * Rotates a rectangle with a moving center point.
     */
    private VVerticalLayout createTransformAnimationExample() {
        VVerticalLayout section = new VVerticalLayout();
        section.add(new H3("2. Transform Animation"));
        section.add(new Paragraph("Animates rotation with a moving center point."));

        VSvg svg = new VSvg(0, 0, 300, 100);
        svg.getElement().getStyle().set("border", "1px solid black");

        // Background
        RectElement bg = new RectElement()
                .position(0, 0)
                .size(300, 100)
                .fill(HexColor.of("#f0f0f0"))
                .noStroke();

        // Animated rectangle
        RectElement rect = new RectElement()
                .position(0, 40)
                .size(15, 30)
                .fill(HexColor.of("#cc3366"))
                .stroke(HexColor.of("#000000"))
                .strokeWidth(1);

        // Rotation animation - rotates while moving the center point
        AnimateTransformElement rotateAnim = new AnimateTransformElement()
                .rotateFromTo(0, 360, 60, 55)
                .dur(Duration.ofSeconds(4))
                .repeatIndefinitely();

        rect.appendChild(rotateAnim);
        svg.getElement().appendChild(bg, rect);

        section.add(svg);
        return section;
    }

    /**
     * Example 3: Linear motion along a path.
     * Circle bounces horizontally.
     */
    private VVerticalLayout createLinearMotionExample() {
        VVerticalLayout section = new VVerticalLayout();
        section.add(new H3("3. Linear Motion Along Path"));
        section.add(new Paragraph("Circle moves horizontally along a straight path."));

        VSvg svg = new VSvg(0, 0, 300, 100);
        svg.getElement().getStyle().set("border", "1px solid black");

        // Background
        RectElement bg = new RectElement()
                .position(0, 0)
                .size(300, 100)
                .fill(HexColor.of("#f0f0f0"))
                .noStroke();

        // Animated circle
        CircleElement circle = new CircleElement()
                .center(0, 50)
                .r(15)
                .fill(HexColor.of("#66cc33"))
                .stroke(HexColor.of("#000000"))
                .strokeWidth(1);

        // Motion along path using lambda-based PathBuilder API
        AnimateMotionElement motion = new AnimateMotionElement()
                .path(p -> p
                        .moveTo(15, 0)
                        .horizontalLineTo(270)
                        .closePath())
                .dur(Duration.ofSeconds(3))
                .repeatIndefinitely();

        circle.appendChild(motion);
        svg.getElement().appendChild(bg, circle);

        section.add(svg);
        return section;
    }

    /**
     * Example 4: Curved motion along a path with auto-rotation.
     * Rectangle follows a curved path and rotates to follow tangent.
     */
    private VVerticalLayout createCurvedMotionExample() {
        VVerticalLayout section = new VVerticalLayout();
        section.add(new H3("4. Curved Motion with Auto-Rotation"));
        section.add(new Paragraph("Rectangle follows a curved path and rotates to follow the direction."));

        VSvg svg = new VSvg(0, 0, 300, 120);
        svg.getElement().getStyle().set("border", "1px solid black");

        // Background
        RectElement bg = new RectElement()
                .position(0, 0)
                .size(300, 120)
                .fill(HexColor.of("#f0f0f0"))
                .noStroke();

        // Animated rectangle (arrow-like)
        RectElement rect = new RectElement()
                .position(-10, -5)
                .size(20, 10)
                .fill(HexColor.of("#cc6633"))
                .stroke(HexColor.of("#000000"))
                .strokeWidth(1);

        // Curved motion path using PathBuilder API - rounded rectangle track
        AnimateMotionElement motion = new AnimateMotionElement()
                .path(p -> p
                        .moveTo(250, 80)
                        .horizontalLineTo(50)
                        .quadraticBezierTo(20, 80, 20, 60)
                        .quadraticBezierTo(20, 40, 50, 40)
                        .horizontalLineTo(250)
                        .quadraticBezierTo(280, 40, 280, 60)
                        .quadraticBezierTo(280, 80, 250, 80)
                        .closePath())
                .dur(Duration.ofSeconds(4))
                .rotateAuto()
                .repeatIndefinitely();

        rect.appendChild(motion);
        svg.getElement().appendChild(bg, rect);

        section.add(svg);
        return section;
    }

    /**
     * Example 5: Color animation.
     * Animates fill color of a circle.
     */
    private VVerticalLayout createColorAnimationExample() {
        VVerticalLayout section = new VVerticalLayout();
        section.add(new H3("5. Color Animation"));
        section.add(new Paragraph("Animates the fill color through multiple values."));

        VSvg svg = new VSvg(0, 0, 300, 100);
        svg.getElement().getStyle().set("border", "1px solid black");

        // Background
        RectElement bg = new RectElement()
                .position(0, 0)
                .size(300, 100)
                .fill(HexColor.of("#f0f0f0"))
                .noStroke();

        // Circle with color animation
        CircleElement circle = new CircleElement()
                .center(150, 50)
                .r(30)
                .fill(HexColor.of("#ff0000"))
                .stroke(HexColor.of("#000000"))
                .strokeWidth(2);

        // Color animation through multiple values
        AnimateElement colorAnim = new AnimateElement()
                .attributeName("fill")
                .values("#ff0000;#00ff00;#0000ff;#ff0000")
                .dur(Duration.ofSeconds(4))
                .repeatIndefinitely();

        circle.appendChild(colorAnim);
        svg.getElement().appendChild(bg, circle);

        section.add(svg);
        return section;
    }

    /**
     * Example 6: Multiple simultaneous animations.
     * Circle with multiple animations: position, radius, and color.
     */
    private VVerticalLayout createMultipleAnimationsExample() {
        VVerticalLayout section = new VVerticalLayout();
        section.add(new H3("6. Multiple Simultaneous Animations"));
        section.add(new Paragraph("Circle animates position, size, and color simultaneously."));

        VSvg svg = new VSvg(0, 0, 300, 100);
        svg.getElement().getStyle().set("border", "1px solid black");

        // Background
        RectElement bg = new RectElement()
                .position(0, 0)
                .size(300, 100)
                .fill(HexColor.of("#f0f0f0"))
                .noStroke();

        // Circle with multiple animations
        CircleElement circle = new CircleElement()
                .center(50, 50)
                .r(15)
                .fill(HexColor.of("#9933cc"))
                .noStroke();

        // Position animation (cx)
        AnimateElement cxAnim = new AnimateElement()
                .attributeName("cx")
                .values("50;250;50")
                .dur(Duration.ofSeconds(4))
                .repeatIndefinitely();

        // Radius animation (pulsing)
        AnimateElement rAnim = new AnimateElement()
                .attributeName("r")
                .values("15;25;15")
                .dur(Duration.ofSeconds(2))
                .repeatIndefinitely();

        // Color animation
        AnimateElement fillAnim = new AnimateElement()
                .attributeName("fill")
                .values("#9933cc;#33cc99;#cc9933;#9933cc")
                .dur(Duration.ofSeconds(6))
                .repeatIndefinitely();

        // Opacity animation
        AnimateElement opacityAnim = new AnimateElement()
                .attributeName("opacity")
                .values("1;0.5;1")
                .dur(Duration.ofSeconds(3))
                .repeatIndefinitely();

        circle.appendChild(cxAnim);
        circle.appendChild(rAnim);
        circle.appendChild(fillAnim);
        circle.appendChild(opacityAnim);

        svg.getElement().appendChild(bg, circle);

        section.add(svg);
        return section;
    }
}