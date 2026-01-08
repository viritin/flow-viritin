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
        add(line);

        add(button1);
        addAndExpand(new Span());
        add(button2);

        add(new Button("Draw line", e -> {
            line.drawLine(button1, button2);
        }));

    }

    /**
     * A simple SVG based component drawing a line between buttons.
     */
    static class ConnectingLine extends Component {
        private final Element line;

        public ConnectingLine() {
            // The root Element of the component can now be svg, even e.g. line or circle, but then the parent
            // component also needs to be of an SVG namespace (like in html, e.g. circle can't get directly into div)
            super(new Element("svg"));
            // within this svg component we only have a single red line
            line = new Element("line");
            line.getStyle()
                    .set("stroke", "red")
                    .set("stroke-width", "2");
            getElement().appendChild(line);
            Style style = getStyle();
            // Position the SVG absolutely, so it doesn't affect laying out other components
            style.setPosition(Style.Position.ABSOLUTE);
            style.setTop("0");
            style.setLeft("0");
            style.setWidth("100%");
            style.setHeight("100%");
        }

        /**
         * Reads the component positions form the browser and assigns them to the line.
         *
         * @param button1 the component whose center point is set to x1/y1
         * @param button2 the component whose center point is set to x2/y2
         */
        public void drawLine(Component button1, Component button2) {
            // Define DTOs to get values from the client
            record BoundingClientRect(double x, double y, double width, double height) {}
            record Bounds(BoundingClientRect el1, BoundingClientRect el2) {}

            getElement().executeJs("""
                    return {el1 : $0.getBoundingClientRect(), el2 : $1.getBoundingClientRect()};
                    """, button1.getElement(), button2.getElement()).toCompletableFuture(Bounds.class).thenAccept(b -> {
                line.setAttribute("x1", "" + (b.el1.x + b.el1.width / 2));
                line.setAttribute("y1", "" + (b.el1.y + b.el1.height / 2));
                line.setAttribute("x2", "" + (b.el2.x + b.el2.width / 2));
                line.setAttribute("y2", "" + (b.el2.y + b.el2.height / 2));
            });

        }
    }
}
