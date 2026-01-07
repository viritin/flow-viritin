package org.vaadin.firitin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import in.virit.color.NamedColor;
import org.vaadin.firitin.components.VSvg;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.element.svg.DefsElement;
import org.vaadin.firitin.element.svg.LineElement;
import org.vaadin.firitin.element.svg.MarkerElement;
import org.vaadin.firitin.element.svg.PathElement;
import org.vaadin.firitin.util.ResizeObserver;

import java.time.Duration;

/**
 * Simple demo showing an animated line with arrowhead between two buttons.
 */
@Route
public class SimpleAnimatedLineView extends VHorizontalLayout {

    public SimpleAnimatedLineView() {
        setSizeFull();
        // Using relative positioning to move the second button to a more interesting place
        getStyle().setPosition(Style.Position.RELATIVE);

        var button1 = new Button("Button 1");
        var button2 = new Button("Button 2") {{
            getStyle().setTop("30%");
            getStyle().setPosition(Style.Position.RELATIVE);
        }};

        add(button1);
        space();
        add(button2);

        add(new Button("Draw line", e -> {
            add(new ConnectingLine(button1, button2));
            // this simple demo only supports drawing once
            e.getSource().setEnabled(false);
        }));
    }

    private static class ConnectingLine extends VSvg {
        private int x1, y1, x2, y2;
        private final LineElement lineElement;

        public ConnectingLine(Button button1, Button button2) {
            // Optional, create arrowhead marker
            MarkerElement arrowhead = new MarkerElement("arrowhead")
                    .viewBox(0, 0, 10, 10)
                    .ref(10, 5)
                    .markerSize(6, 6)
                    .orientAuto()
                    .add(new PathElement(p -> p.moveTo(0, 0).lineTo(10, 5).lineTo(0, 10).closePath())
                            .fill(NamedColor.RED));

            getElement().appendChild(new DefsElement(arrowhead));

            // Create line with arrowhead
            lineElement = new LineElement()
                    .stroke(NamedColor.RED)
                    .strokeWidth(2)
                    .markerEnd(arrowhead);
            getElement().appendChild(lineElement);

            // position the line absolutely, so that it doesn't affect positioning other components
            // in the layout
            getStyle().setPosition(Style.Position.ABSOLUTE);
            getStyle().setTop("0");
            getStyle().setLeft("0");
            drawLine(button1, button2);
        }

        void drawLine(Component component1, Component component2) {
            // TODO simplify resize observer to support listening multiple components at once
            ResizeObserver.get().observe(component1, dim -> {
                x1 = dim.offsetLeft() + dim.offsetWidth(); // right edge
                y1 = dim.offsetTop() + dim.offsetHeight() / 2;
            });
            ResizeObserver.get().observe(component2, dim -> {
                x2 = dim.offsetLeft(); // left edge
                y2 = dim.offsetTop() + dim.offsetHeight() / 2;
                drawLineInternal();
            });
        }

        private void drawLineInternal() {
            setSizeFull();
            lineElement.points(x1,y1, x2, y2);
            // Optional: animate end point from start to end with SMIL animations
            var dur = Duration.ofMillis(500);
            lineElement.animateX2(x1, x2, dur);
            lineElement.animateY2(y1, y2, dur);
        }
    }
}