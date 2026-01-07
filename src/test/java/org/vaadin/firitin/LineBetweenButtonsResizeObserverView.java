package org.vaadin.firitin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import in.virit.color.HexColor;
import org.vaadin.firitin.components.VSvg;
import org.vaadin.firitin.element.svg.DefsElement;
import org.vaadin.firitin.element.svg.LineElement;
import org.vaadin.firitin.element.svg.MarkerElement;
import org.vaadin.firitin.element.svg.PathElement;

import java.time.Duration;
import org.vaadin.firitin.util.ResizeObserver;
import org.vaadin.firitin.util.VStyleUtil;

import java.util.ArrayList;
import java.util.Collections;

@Route
public class LineBetweenButtonsResizeObserverView extends HorizontalLayout {

    public LineBetweenButtonsResizeObserverView() {
        setWidthFull();

        getStyle().setPosition(Style.Position.RELATIVE);

        VStyleUtil.injectAsFirst("""
            vaadin-button {
                background-color: lightblue;
            }
        """);

        var button1 = new Button("Button 1");
        var button2 = new Button("Button 2");
        button2.setWidthFull();
        button2.setHeight("200px");

        ConnectingLine line = new ConnectingLine();
        add(line);

        add(button1, button2);

        VerticalLayout verticalLayout1 = new VerticalLayout();
        verticalLayout1.add(button1);

        VerticalLayout verticalLayout2 = new VerticalLayout();
        verticalLayout2.add(button2);

        add(verticalLayout1, verticalLayout2);

        // Controls
        VerticalLayout controls = new VerticalLayout();
        controls.setSpacing(false);
        controls.setPadding(false);

        controls.add(new Button("Draw line", e -> line.drawLine(button1, button2)));

        Checkbox edgeToEdge = new Checkbox("Edge-to-edge", true);
        edgeToEdge.setEnabled(false); // Disabled when arrowhead is shown
        edgeToEdge.addValueChangeListener(e -> {
            line.setEdgeToEdge(e.getValue());
            line.redraw();
        });

        Checkbox showArrowhead = new Checkbox("Show arrowhead", true);
        showArrowhead.addValueChangeListener(e -> {
            line.setShowArrowhead(e.getValue());
            if (e.getValue()) {
                // Enforce edge-to-edge when arrowhead is shown
                edgeToEdge.setValue(true);
                edgeToEdge.setEnabled(false);
            } else {
                edgeToEdge.setEnabled(true);
            }
            line.setEdgeToEdge(edgeToEdge.getValue());
            line.redraw();
        });
        controls.add(showArrowhead);
        controls.add(edgeToEdge);

        Checkbox animateDrawing = new Checkbox("Animate drawing", false);
        animateDrawing.addValueChangeListener(e -> {
            line.setAnimateDrawing(e.getValue());
        });
        controls.add(animateDrawing);

        controls.add(new Button("Add random buttons", e -> {
            verticalLayout1.removeAll();
            verticalLayout2.removeAll();

            ArrayList<Component> components = new ArrayList<>();
            components.add(button1);
            components.add(button2);
            for (int i = 0; i < 20; i++) {
                components.add(new Button("Button " + i));
            }
            Collections.shuffle(components);
            for (int i = 0; i < components.size(); i++) {
                var button = components.get(i);
                if (i % 2 == 0) {
                    verticalLayout1.add(button);
                } else {
                    verticalLayout2.add(button);
                }
            }
            line.drawLine(button1, button2);
        }));

        add(controls);
    }

    private static class ConnectingLine extends VSvg {
        private int x1, y1, x2, y2;
        private int width1, height1, width2, height2;
        private final LineElement lineElement;
        private final MarkerElement arrowhead;
        private boolean showArrowhead = true;
        private boolean edgeToEdge = true;
        private boolean animateDrawing = false;

        public ConnectingLine() {
            // Create arrowhead marker
            arrowhead = new MarkerElement("arrowhead")
                    .viewBox(0, 0, 10, 10)
                    .ref(10, 5)
                    .markerSize(6, 6)
                    .orientAuto();

            // Arrow shape pointing right
            PathElement arrowPath = new PathElement()
                    .d(p -> p
                            .moveTo(0, 0)
                            .lineTo(10, 5)
                            .lineTo(0, 10)
                            .closePath())
                    .fill(HexColor.of("#ff0000"));
            arrowhead.add(arrowPath);

            // Add marker to defs
            DefsElement defs = new DefsElement();
            defs.appendChild(arrowhead);
            getElement().appendChild(defs);

            // Create line
            lineElement = new LineElement()
                    .stroke(HexColor.of("#ff0000"))
                    .strokeWidth(2)
                    .markerEnd(arrowhead);
            getElement().appendChild(lineElement);

            Style style = getStyle();
            style.setPosition(Style.Position.ABSOLUTE);
            style.setTop("0");
            style.setLeft("0");
        }

        public void setShowArrowhead(boolean show) {
            this.showArrowhead = show;
        }

        public void setEdgeToEdge(boolean edgeToEdge) {
            this.edgeToEdge = edgeToEdge;
        }

        public void setAnimateDrawing(boolean animateDrawing) {
            this.animateDrawing = animateDrawing;
        }

        public void redraw() {
            drawLineInternal();
        }

        private void drawLineInternal() {
            int lineX1, lineY1, lineX2, lineY2;

            if (edgeToEdge) {
                // Calculate edge-to-edge points
                int centerX1 = x1;
                int centerY1 = y1;
                int centerX2 = x2;
                int centerY2 = y2;

                // Calculate direction vector
                double dx = centerX2 - centerX1;
                double dy = centerY2 - centerY1;
                double length = Math.sqrt(dx * dx + dy * dy);

                if (length > 0) {
                    // Normalize direction
                    double nx = dx / length;
                    double ny = dy / length;

                    // Find intersection with component 1 edge
                    // Use half dimensions to find edge point
                    double hw1 = width1 / 2.0;
                    double hh1 = height1 / 2.0;
                    double t1 = Math.min(
                            nx != 0 ? Math.abs(hw1 / nx) : Double.MAX_VALUE,
                            ny != 0 ? Math.abs(hh1 / ny) : Double.MAX_VALUE
                    );
                    lineX1 = (int) (centerX1 + nx * t1);
                    lineY1 = (int) (centerY1 + ny * t1);

                    // Find intersection with component 2 edge (from center toward component 1)
                    double hw2 = width2 / 2.0;
                    double hh2 = height2 / 2.0;
                    double t2 = Math.min(
                            nx != 0 ? Math.abs(hw2 / nx) : Double.MAX_VALUE,
                            ny != 0 ? Math.abs(hh2 / ny) : Double.MAX_VALUE
                    );
                    lineX2 = (int) (centerX2 - nx * t2);
                    lineY2 = (int) (centerY2 - ny * t2);
                } else {
                    lineX1 = centerX1;
                    lineY1 = centerY1;
                    lineX2 = centerX2;
                    lineY2 = centerY2;
                }
            } else {
                // Center-to-center
                lineX1 = x1;
                lineY1 = y1;
                lineX2 = x2;
                lineY2 = y2;
            }

            int width = Math.max(lineX1, lineX2) + 10;
            int height = Math.max(lineY1, lineY2) + 10;

            //getElement().viewBox(0, 0, width, height);
            getElement().size(width + "px", height + "px");

            // Toggle arrowhead
            if (showArrowhead) {
                lineElement.markerEnd(arrowhead);
            } else {
                lineElement.setAttribute("marker-end", "none");
            }

            if (animateDrawing) {
                lineElement.x1(lineX1);
                lineElement.y1(lineY1);
                Duration duration = Duration.ofMillis(500);
                lineElement.animateY2(lineY1, lineY2, duration);
                lineElement.animateX2(lineX1, lineX2, duration);
            } else {
                lineElement.points(lineX1, lineY1, lineX2, lineY2);
            }
        }

        public void drawLine(Component component1, Component component2) {
            ResizeObserver.get().observe(component1, dimensions -> {
                this.x1 = dimensions.offsetLeft() + dimensions.offsetWidth() / 2;
                this.y1 = dimensions.offsetTop() + dimensions.offsetHeight() / 2;
                this.width1 = dimensions.offsetWidth();
                this.height1 = dimensions.offsetHeight();
            });
            ResizeObserver.get().observe(component2, dimensions -> {
                this.x2 = dimensions.offsetLeft() + dimensions.offsetWidth() / 2;
                this.y2 = dimensions.offsetTop() + dimensions.offsetHeight() / 2;
                this.width2 = dimensions.offsetWidth();
                this.height2 = dimensions.offsetHeight();
                drawLineInternal();
            });
        }
    }

}