package org.vaadin.firitin.svg;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import in.virit.color.NamedColor;
import org.vaadin.firitin.components.VSvg;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.element.svg.CircleElement;
import org.vaadin.firitin.element.svg.EllipseElement;
import org.vaadin.firitin.element.svg.LineElement;
import org.vaadin.firitin.element.svg.PathElement;
import org.vaadin.firitin.element.svg.PolygonElement;
import org.vaadin.firitin.element.svg.RectElement;

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
                new LineDemo()
        ));

        add(new H3("Path and Polygon Shapes"));
        add(new VHorizontalLayout(
                new HeartPathDemo(),
                new PolygonDemo(),
                new StarDemo()
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
                    .cornerRadius(8);
            rect.setFill(NamedColor.STEELBLUE);

            var label = new RectElement()
                    .x(10).y(85)
                    .width(80).height(12);
            label.setFill(NamedColor.WHITE);

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
                    .r(40);
            circle.setFill(NamedColor.CORAL);

            var innerCircle = new CircleElement()
                    .center(50, 50)
                    .r(20);
            innerCircle.setFill(NamedColor.WHITE);

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
                    .radii(45, 25);
            ellipse.setFill(NamedColor.MEDIUMSEAGREEN);

            var verticalEllipse = new EllipseElement()
                    .center(50, 50)
                    .radii(15, 35);
            verticalEllipse.setFill(NamedColor.PALEGREEN);

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

    static class FlowDiagram extends VSvg {
        FlowDiagram() {
            super(0, 0, 300, 150);
            setWidth("450px");
            setHeight("225px");
            getStyle().setBorder("1px solid #ccc");

            var box1 = new RectElement()
                    .bounds(20, 50, 60, 40)
                    .cornerRadius(5);
            box1.setFill(NamedColor.LIGHTBLUE);

            var box2 = new RectElement()
                    .bounds(120, 50, 60, 40)
                    .cornerRadius(5);
            box2.setFill(NamedColor.LIGHTGREEN);

            var box3 = new RectElement()
                    .bounds(220, 50, 60, 40)
                    .cornerRadius(5);
            box3.setFill(NamedColor.LIGHTSALMON);

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
                    .r(5);
            status1.setFill(NamedColor.GREEN);

            var status2 = new CircleElement()
                    .center(150, 45)
                    .r(5);
            status2.setFill(NamedColor.YELLOW);

            var status3 = new CircleElement()
                    .center(250, 45)
                    .r(5);
            status3.setFill(NamedColor.RED);

            getElement().appendChild(
                    box1, box2, box3,
                    line1, line2,
                    arrow1a, arrow1b, arrow2a, arrow2b,
                    status1, status2, status3
            );
        }
    }
}
