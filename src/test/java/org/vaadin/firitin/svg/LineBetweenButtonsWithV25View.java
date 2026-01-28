package org.vaadin.firitin.svg;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;

/**
 * A Vaadin 25 version of "drawing a line between components example", benefitting
 * of both SVG namespace support in Elementn API and Jackson goodies in client-server comms.
 */
@Route
public class LineBetweenButtonsWithV25View extends HorizontalLayout {

    public LineBetweenButtonsWithV25View() {
        setPadding(true);
        setWidthFull();

        var button1 = new Button("Button 1");
        var button2 = new Button("Button 2");
        // override default (translucent color) to hide line below components
        button1.getStyle().setBackgroundColor("lightgreen");
        button2.getStyle().setBackgroundColor("lightblue");
        // Move second buttond down a bit so that line is not horizontal
        button2.getStyle().setMarginTop("300px");

        // Line will be absolutely positioned, added first -> below others
        getStyle().setPosition(Style.Position.RELATIVE);
        ConnectingLine line = new ConnectingLine();
        // Position the SVG absolutely, so it doesn't affect laying out other components
        Style style = line.getStyle();
        style.setPosition(Style.Position.ABSOLUTE);
        style.setTop("0");
        style.setLeft("0");
        style.setWidth("100%");
        style.setHeight("100%");

        add(line);

        add(button1);
        addAndExpand(new Span());
        add(button2);

        add(new Button("Draw line", e -> {
            // Define DTOs to get values from the client
            record BoundingClientRect(double x, double y, double width, double height) {
            }
            record Bounds(BoundingClientRect el1, BoundingClientRect el2) {
            }

            // Doing ugly JS executions here in click listener as the purpose is to highlight Element support for SVG
            getElement().executeJs("""
                    return {el1 : $0.getBoundingClientRect(), el2 : $1.getBoundingClientRect()};
                    """, button1.getElement(), button2.getElement()).toCompletableFuture(Bounds.class).thenAccept(b -> {
                double x1 = b.el1.x + b.el1.width / 2;
                double y1 = b.el1.y + b.el1.height / 2;
                double x2 = b.el2.x + b.el2.width / 2;
                double y2 = b.el2.y + b.el2.height / 2;
                line.drawLine(x1, y1, x2, y2);
            });

        }));

    }

    /**
     * A simple SVG based component drawing a line
     */
    static class ConnectingLine extends Component {
        private final Element line;

        public ConnectingLine() {
            // The root Element of a component can now be svg, even e.g. line or circle, but then the parent
            // component also needs to be of an SVG namespace (like in html, e.g. circle can't get directly into div)
            super(new Element("svg"));
            // within this svg component we only have a single red line
            line = new Element("line");
            line.setAttribute("stroke", "red");
            line.setAttribute("stroke-width", "2");
            getElement().appendChild(line);
        }

        // You should not expose elements directly from your component, but provide a typed Java API
        public void drawLine(double x1, double y1, double x2, double y2) {
            // Now only  the attributes of the line element in the browser gets dynamically adjusted
            line.setAttribute("x1", "" + x1);
            line.setAttribute("y1", "" + y1);
            line.setAttribute("x2", "" + x2);
            line.setAttribute("y2", "" + y2);
        }
    }
}
