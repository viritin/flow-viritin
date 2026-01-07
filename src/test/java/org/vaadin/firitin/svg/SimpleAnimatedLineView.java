package org.vaadin.firitin.svg;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import in.virit.color.HexColor;
import org.vaadin.firitin.components.VSvg;
import org.vaadin.firitin.element.svg.DefsElement;
import org.vaadin.firitin.element.svg.LineElement;
import org.vaadin.firitin.element.svg.MarkerElement;
import org.vaadin.firitin.element.svg.PathElement;
import org.vaadin.firitin.util.ResizeObserver;

import java.time.Duration;

/**
 * Simple demo showing a line with arrowhead between two buttons.
 * <p>
 * Uses {@link ResizeObserver} to track button positions and update the line automatically.
 * </p>
 */
@Route
public class SimpleAnimatedLineView extends VHorizontalLayout {

    public SimpleAnimatedLineView() {
        setSizeFull();
        getStyle().setPosition(Style.Position.RELATIVE);

        var button1 = new Button("Button 1");
        var button2 = new Button("Button 2") {{
            // move bit down for big interesting setup
            getStyle().setTop("30%");
            getStyle().setPosition(Style.Position.RELATIVE);
        }};

        // Create SVG at view construction time for Safari compatibility
        ConnectingLine line = new ConnectingLine();

        add(line, button1);
        space();
        add(button2);

        add(new Button("Draw line", e -> {
            line.drawLine(button1, button2);
            e.getSource().setEnabled(false);
        }));
    }

    private static class ConnectingLine extends VSvg {
        private int x1, y1, x2, y2;
        private final LineElement lineElement;

        public ConnectingLine() {
            // Optional: Create arrowhead marker
            MarkerElement arrowhead = new MarkerElement("arrowhead")
                    .viewBox(0, 0, 10, 10)
                    .ref(10, 5)
                    .markerSize(6, 6)
                    .orientAuto()
                    .add(new PathElement(p -> p.moveTo(0, 0).lineTo(10, 5).lineTo(0, 10).closePath())
                            .fill(HexColor.of("#ff0000")));

            getElement().appendChild(new DefsElement(arrowhead));

            // Create line with arrowhead
            lineElement = new LineElement()
                    .stroke(HexColor.of("#ff0000"))
                    .strokeWidth(2)
                    .markerEnd(arrowhead);
            getElement().appendChild(lineElement);

            // Position absolutely so it doesn't affect layout of other components
            getStyle().setPosition(Style.Position.ABSOLUTE);
            setSizeFull();
        }

        void drawLine(Component component1, Component component2) {
            ResizeObserver.get().observe(dimensions -> {
                var dim1 = dimensions.get(component1);
                var dim2 = dimensions.get(component2);
                x1 = dim1.offsetLeft() + dim1.offsetWidth(); // right edge
                y1 = dim1.offsetTop() + dim1.offsetHeight() / 2;
                x2 = dim2.offsetLeft(); // left edge
                y2 = dim2.offsetTop() + dim2.offsetHeight() / 2;

                lineElement.points(x1, y1, x2, y2);

                // Optional: animate endpoint from start to end
                if(false) {
                    Duration dur = Duration.ofMillis(500);
                    lineElement.animateX2(x1, x2, dur);
                    lineElement.animateY2(y1, y2, dur);
                }
            }, component1, component2, component1.getParent().orElseThrow());
        }
    }
}
