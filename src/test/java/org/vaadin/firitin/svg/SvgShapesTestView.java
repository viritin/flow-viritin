package org.vaadin.firitin.svg;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import in.virit.color.NamedColor;
import org.vaadin.firitin.components.VSvg;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.element.svg.CircleElement;
import org.vaadin.firitin.element.svg.DefsElement;
import org.vaadin.firitin.element.svg.EllipseElement;
import org.vaadin.firitin.element.svg.GElement;
import org.vaadin.firitin.element.svg.LineElement;
import org.vaadin.firitin.element.svg.PathElement;
import org.vaadin.firitin.element.svg.PolygonElement;
import org.vaadin.firitin.element.svg.PolylineElement;
import org.vaadin.firitin.element.svg.RectElement;
import org.vaadin.firitin.element.svg.SvgGraphicsElement;
import org.vaadin.firitin.element.svg.SymbolElement;
import org.vaadin.firitin.element.svg.UseElement;

/**
 * Test view demonstrating the typed SVG element APIs.
 */
@Route
public class SvgShapesTestView extends VVerticalLayout {

    public SvgShapesTestView() {
        add(new Paragraph("Demonstration of typed SVG element APIs"));

        add(new H3("Basic Shapes"));
        add(new VHorizontalLayout(
                new RectDemo(),
                new CircleDemo(),
                new EllipseDemo(),
                new LineDemo(),
                new PolylineDemo()
        ));

        add(new H3("Path and Polygon Shapes"));
        add(new VHorizontalLayout(
                new HeartPathDemo(),
                new PolygonDemo(),
                new StarDemo()
        ));

        add(new H3("Stroke Styles Demo"));
        add(new VHorizontalLayout(
                new StrokeStylesDemo(),
                new OpacityDemo()
        ));

        add(new H3("Transforms Demo"));
        add(new VHorizontalLayout(
                new TransformDemo(),
                new RotateDemo()
        ));

        add(new H3("Structural Elements (Group, Defs, Use)"));
        add(new VHorizontalLayout(
                new GroupDemo(),
                new SymbolUseDemo()
        ));

        add(new H3("Combined Example: Simple Diagram"));
        add(new FlowDiagram());
    }

    static class RectDemo extends VSvg {
        RectDemo() {
            super(0, 0, 100, 100);
            setWidth("150px");
            setHeight("150px");
            getStyle().setBorder("1px solid #ccc");

            var rect = new RectElement()
                    .bounds(10, 10, 80, 80)
                    .cornerRadius(8)
                    .fill(NamedColor.STEELBLUE);

            var label = new RectElement()
                    .x(10).y(85)
                    .width(80).height(12)
                    .fill(NamedColor.WHITE);

            getElement().appendChild(rect, label);
        }
    }

    static class CircleDemo extends VSvg {
        CircleDemo() {
            super(0, 0, 100, 100);
            setWidth("150px");
            setHeight("150px");
            getStyle().setBorder("1px solid #ccc");

            var circle = new CircleElement()
                    .center(50, 50)
                    .r(40)
                    .fill(NamedColor.CORAL);

            var innerCircle = new CircleElement()
                    .center(50, 50)
                    .r(20)
                    .fill(NamedColor.WHITE);

            getElement().appendChild(circle, innerCircle);
        }
    }

    static class EllipseDemo extends VSvg {
        EllipseDemo() {
            super(0, 0, 100, 100);
            setWidth("150px");
            setHeight("150px");
            getStyle().setBorder("1px solid #ccc");

            var ellipse = new EllipseElement()
                    .center(50, 50)
                    .radii(45, 25)
                    .fill(NamedColor.MEDIUMSEAGREEN);

            var verticalEllipse = new EllipseElement()
                    .center(50, 50)
                    .radii(15, 35)
                    .fill(NamedColor.PALEGREEN);

            getElement().appendChild(ellipse, verticalEllipse);
        }
    }

    static class LineDemo extends VSvg {
        LineDemo() {
            super(0, 0, 100, 100);
            setWidth("150px");
            setHeight("150px");
            getStyle().setBorder("1px solid #ccc");

            var line1 = new LineElement()
                    .from(10, 10).to(90, 90)
                    .stroke(NamedColor.DARKVIOLET)
                    .strokeWidth(3);

            var line2 = new LineElement()
                    .from(90, 10).to(10, 90)
                    .stroke(NamedColor.ORANGE)
                    .strokeWidth(3);

            var line3 = new LineElement()
                    .from(50, 5).to(50, 95)
                    .stroke(NamedColor.TEAL)
                    .strokeWidth(2);

            var line4 = new LineElement()
                    .from(5, 50).to(95, 50)
                    .stroke(NamedColor.TEAL)
                    .strokeWidth(2);

            getElement().appendChild(line1, line2, line3, line4);
        }
    }

    static class PolylineDemo extends VSvg {
        PolylineDemo() {
            super(0, 0, 100, 100);
            setWidth("150px");
            setHeight("150px");
            getStyle().setBorder("1px solid #ccc");

            // Stair-step pattern (open shape - not closed like polygon)
            var stairs = new PolylineElement()
                    .points(10, 80, 10, 60, 30, 60, 30, 40, 50, 40, 50, 20, 70, 20, 70, 10, 90, 10)
                    .stroke(NamedColor.STEELBLUE)
                    .strokeWidth(3)
                    .noFill();

            // Zigzag pattern
            var zigzag = new PolylineElement()
                    .addPoint(10, 95)
                    .addPoint(25, 85)
                    .addPoint(40, 95)
                    .addPoint(55, 85)
                    .addPoint(70, 95)
                    .addPoint(85, 85)
                    .stroke(NamedColor.CORAL)
                    .strokeWidth(2)
                    .noFill();

            getElement().appendChild(stairs, zigzag);
        }
    }

    static class HeartPathDemo extends VSvg {
        HeartPathDemo() {
            super(0, 0, 100, 100);
            setWidth("150px");
            setHeight("150px");
            getStyle().setBorder("1px solid #ccc");

            var heart = new PathElement()
                    .moveTo(50, 30)
                    .cubicBezierTo(50, 25, 45, 15, 30, 15)
                    .cubicBezierTo(10, 15, 10, 40, 10, 40)
                    .cubicBezierTo(10, 55, 25, 70, 50, 85)
                    .cubicBezierTo(75, 70, 90, 55, 90, 40)
                    .cubicBezierTo(90, 40, 90, 15, 70, 15)
                    .cubicBezierTo(55, 15, 50, 25, 50, 30)
                    .closePath()
                    .fill(NamedColor.CRIMSON);

            getElement().appendChild(heart);
        }
    }

    static class PolygonDemo extends VSvg {
        PolygonDemo() {
            super(0, 0, 100, 100);
            setWidth("150px");
            setHeight("150px");
            getStyle().setBorder("1px solid #ccc");

            var triangle = new PolygonElement()
                    .triangle(50, 10, 10, 60, 90, 60)
                    .fill(NamedColor.DODGERBLUE)
                    .stroke(NamedColor.DARKBLUE)
                    .strokeWidth(2);

            var hexagon = new PolygonElement()
                    .regularPolygon(50, 75, 20, 6)
                    .fill(NamedColor.GOLD)
                    .stroke(NamedColor.DARKORANGE)
                    .strokeWidth(2);

            getElement().appendChild(triangle, hexagon);
        }
    }

    static class StarDemo extends VSvg {
        StarDemo() {
            super(0, 0, 100, 100);
            setWidth("150px");
            setHeight("150px");
            getStyle().setBorder("1px solid #ccc");

            var star = new PolygonElement()
                    .star(50, 50, 45, 20, 5)
                    .fill(NamedColor.GOLD)
                    .stroke(NamedColor.DARKORANGE)
                    .strokeWidth(2);

            getElement().appendChild(star);
        }
    }

    static class StrokeStylesDemo extends VSvg {
        StrokeStylesDemo() {
            super(0, 0, 150, 100);
            setWidth("225px");
            setHeight("150px");
            getStyle().setBorder("1px solid #ccc");

            // Solid line
            var solid = new LineElement()
                    .from(10, 15).to(140, 15)
                    .stroke(NamedColor.DARKBLUE)
                    .strokeWidth(3);

            // Dashed line
            var dashed = new LineElement()
                    .from(10, 35).to(140, 35)
                    .stroke(NamedColor.DARKGREEN)
                    .strokeWidth(3)
                    .strokeDasharray(10, 5);

            // Dotted line
            var dotted = new LineElement()
                    .from(10, 55).to(140, 55)
                    .stroke(NamedColor.DARKRED)
                    .strokeWidth(3)
                    .strokeDasharray(3, 3);

            // Line with round cap
            var roundCap = new LineElement()
                    .from(10, 75).to(70, 75)
                    .stroke(NamedColor.PURPLE)
                    .strokeWidth(8)
                    .strokeLinecap(SvgGraphicsElement.LineCap.ROUND);

            // Line with square cap
            var squareCap = new LineElement()
                    .from(80, 75).to(140, 75)
                    .stroke(NamedColor.ORANGE)
                    .strokeWidth(8)
                    .strokeLinecap(SvgGraphicsElement.LineCap.SQUARE);

            // Path with different line joins
            var miterJoin = new PathElement()
                    .moveTo(10, 95).lineTo(30, 85).lineTo(50, 95)
                    .stroke(NamedColor.TEAL)
                    .strokeWidth(4)
                    .noFill()
                    .strokeLinejoin(SvgGraphicsElement.LineJoin.MITER);

            var roundJoin = new PathElement()
                    .moveTo(60, 95).lineTo(80, 85).lineTo(100, 95)
                    .stroke(NamedColor.TEAL)
                    .strokeWidth(4)
                    .noFill()
                    .strokeLinejoin(SvgGraphicsElement.LineJoin.ROUND);

            var bevelJoin = new PathElement()
                    .moveTo(110, 95).lineTo(130, 85).lineTo(150, 95)
                    .stroke(NamedColor.TEAL)
                    .strokeWidth(4)
                    .noFill()
                    .strokeLinejoin(SvgGraphicsElement.LineJoin.BEVEL);

            getElement().appendChild(solid, dashed, dotted, roundCap, squareCap,
                    miterJoin, roundJoin, bevelJoin);
        }
    }

    static class OpacityDemo extends VSvg {
        OpacityDemo() {
            super(0, 0, 100, 100);
            setWidth("150px");
            setHeight("150px");
            getStyle().setBorder("1px solid #ccc");

            // Overlapping circles with different opacities
            var circle1 = new CircleElement()
                    .center(35, 40)
                    .r(30)
                    .fill(NamedColor.RED)
                    .fillOpacity(0.6);

            var circle2 = new CircleElement()
                    .center(65, 40)
                    .r(30)
                    .fill(NamedColor.BLUE)
                    .fillOpacity(0.6);

            var circle3 = new CircleElement()
                    .center(50, 65)
                    .r(30)
                    .fill(NamedColor.GREEN)
                    .fillOpacity(0.6);

            // Rect with stroke opacity
            var rect = new RectElement()
                    .bounds(20, 80, 60, 15)
                    .fill(NamedColor.GOLD)
                    .stroke(NamedColor.BLACK)
                    .strokeWidth(3)
                    .strokeOpacity(0.3);

            getElement().appendChild(circle1, circle2, circle3, rect);
        }
    }

    static class TransformDemo extends VSvg {
        TransformDemo() {
            super(0, 0, 150, 100);
            setWidth("225px");
            setHeight("150px");
            getStyle().setBorder("1px solid #ccc");

            // Original rect (reference)
            var original = new RectElement()
                    .bounds(10, 10, 30, 20)
                    .fill(NamedColor.LIGHTGRAY)
                    .stroke(NamedColor.GRAY)
                    .strokeWidth(1);

            // Translated rect
            var translated = new RectElement()
                    .bounds(10, 10, 30, 20)
                    .fill(NamedColor.STEELBLUE)
                    .translate(50, 0);

            // Scaled rect
            var scaled = new RectElement()
                    .bounds(10, 10, 30, 20)
                    .fill(NamedColor.CORAL)
                    .translate(0, 40)
                    .scale(1.5);

            // Skewed rect
            var skewed = new RectElement()
                    .bounds(10, 10, 30, 20)
                    .fill(NamedColor.MEDIUMSEAGREEN)
                    .translate(80, 40)
                    .skewX(20);

            getElement().appendChild(original, translated, scaled, skewed);
        }
    }

    static class RotateDemo extends VSvg {
        RotateDemo() {
            super(0, 0, 100, 100);
            setWidth("150px");
            setHeight("150px");
            getStyle().setBorder("1px solid #ccc");

            // Center point marker
            var center = new CircleElement()
                    .center(50, 50)
                    .r(3)
                    .fill(NamedColor.RED);

            // Multiple rotated rectangles around center
            for (int i = 0; i < 8; i++) {
                var rect = new RectElement()
                        .bounds(45, 20, 10, 25)
                        .fill(NamedColor.STEELBLUE)
                        .fillOpacity(0.7)
                        .stroke(NamedColor.DARKBLUE)
                        .strokeWidth(1)
                        .rotate(i * 45, 50, 50);
                getElement().appendChild(rect);
            }

            getElement().appendChild(center);
        }
    }

    static class FlowDiagram extends VSvg {
        FlowDiagram() {
            super(0, 0, 300, 150);
            setWidth("450px");
            setHeight("225px");
            getStyle().setBorder("1px solid #ccc");

            var box1 = new RectElement()
                    .bounds(20, 50, 60, 40)
                    .cornerRadius(5)
                    .fill(NamedColor.LIGHTBLUE);

            var box2 = new RectElement()
                    .bounds(120, 50, 60, 40)
                    .cornerRadius(5)
                    .fill(NamedColor.LIGHTGREEN);

            var box3 = new RectElement()
                    .bounds(220, 50, 60, 40)
                    .cornerRadius(5)
                    .fill(NamedColor.LIGHTSALMON);

            var line1 = new LineElement()
                    .from(80, 70).to(120, 70)
                    .stroke(NamedColor.GRAY)
                    .strokeWidth(2);

            var line2 = new LineElement()
                    .from(180, 70).to(220, 70)
                    .stroke(NamedColor.GRAY)
                    .strokeWidth(2);

            var arrow1a = new LineElement()
                    .from(115, 65).to(120, 70)
                    .stroke(NamedColor.GRAY)
                    .strokeWidth(2);
            var arrow1b = new LineElement()
                    .from(115, 75).to(120, 70)
                    .stroke(NamedColor.GRAY)
                    .strokeWidth(2);

            var arrow2a = new LineElement()
                    .from(215, 65).to(220, 70)
                    .stroke(NamedColor.GRAY)
                    .strokeWidth(2);
            var arrow2b = new LineElement()
                    .from(215, 75).to(220, 70)
                    .stroke(NamedColor.GRAY)
                    .strokeWidth(2);

            var status1 = new CircleElement()
                    .center(50, 45)
                    .r(5)
                    .fill(NamedColor.GREEN);

            var status2 = new CircleElement()
                    .center(150, 45)
                    .r(5)
                    .fill(NamedColor.YELLOW);

            var status3 = new CircleElement()
                    .center(250, 45)
                    .r(5)
                    .fill(NamedColor.RED);

            getElement().appendChild(
                    box1, box2, box3,
                    line1, line2,
                    arrow1a, arrow1b, arrow2a, arrow2b,
                    status1, status2, status3
            );
        }
    }

    static class GroupDemo extends VSvg {
        GroupDemo() {
            super(0, 0, 100, 100);
            setWidth("150px");
            setHeight("150px");
            getStyle().setBorder("1px solid #ccc");

            // Create a group with common styling
            var group1 = new GElement()
                    .add(
                            new RectElement().bounds(5, 5, 20, 20),
                            new RectElement().bounds(10, 10, 20, 20),
                            new RectElement().bounds(15, 15, 20, 20)
                    );
            group1.fill(NamedColor.STEELBLUE)
                    .stroke(NamedColor.DARKBLUE)
                    .strokeWidth(1);

            // Create another group with transform
            var group2 = new GElement()
                    .add(
                            new CircleElement().center(0, 0).r(15),
                            new CircleElement().center(15, 0).r(10),
                            new CircleElement().center(-15, 0).r(10)
                    );
            group2.fill(NamedColor.CORAL)
                    .stroke(NamedColor.DARKRED)
                    .strokeWidth(1)
                    .translate(50, 50)
                    .rotate(30);

            getElement().appendChild(group1, group2);
        }
    }

    static class SymbolUseDemo extends VSvg {
        SymbolUseDemo() {
            super(0, 0, 150, 100);
            setWidth("225px");
            setHeight("150px");
            getStyle().setBorder("1px solid #ccc");

            // Define a reusable star symbol
            var starSymbol = new SymbolElement("myStar")
                    .viewBox(0, 0, 100, 100)
                    .add(new PolygonElement()
                            .star(50, 50, 45, 20, 5)
                            .fill(NamedColor.GOLD)
                            .stroke(NamedColor.DARKORANGE)
                            .strokeWidth(2));

            // Define a reusable circle symbol
            var circleSymbol = new SymbolElement("myCircle")
                    .viewBox(0, 0, 100, 100)
                    .add(new CircleElement()
                            .center(50, 50)
                            .r(40)
                            .fill(NamedColor.LIGHTBLUE)
                            .stroke(NamedColor.STEELBLUE)
                            .strokeWidth(3));

            // Put symbols in defs
            var defs = new DefsElement().add(starSymbol, circleSymbol);

            // Use the symbols multiple times
            var star1 = new UseElement("myStar").position(5, 5).size(40, 40);
            var star2 = new UseElement("myStar").position(50, 30).size(30, 30);
            var star3 = new UseElement("myStar").position(95, 5).size(50, 50);

            var circle1 = new UseElement("myCircle").position(5, 55).size(40, 40);
            var circle2 = new UseElement("myCircle").position(55, 55).size(40, 40);
            var circle3 = new UseElement("myCircle").position(105, 55).size(40, 40);

            getElement().appendChild(defs, star1, star2, star3, circle1, circle2, circle3);
        }
    }
}
