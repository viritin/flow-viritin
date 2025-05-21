package org.vaadin.firitin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Svg;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

@Route
public class LineBetweenButtonsView extends HorizontalLayout {

    public LineBetweenButtonsView() {
        setWidthFull();

        var button1 = new Button("Button 1");
        button1.getStyle().setBackgroundColor("lightblue");
        var button2 = new Button("Button 2");
        button2.getStyle().setBackgroundColor("lightblue");
        ConnectingLine line = new ConnectingLine();
        add(line);
        add(button1, line, button2);

        VerticalLayout verticalLayout1 = new VerticalLayout();
        verticalLayout1.add(button1);

        VerticalLayout verticalLayout2 = new VerticalLayout();
        verticalLayout2.add(button2);

        add(line);
        add(verticalLayout1, verticalLayout2);

        add(new Button("Draw line", e-> {
            line.drawLine(button1, button2);
        }));

        Random random = new Random();
        add(new Button("Add random buttons", e -> {

            verticalLayout1.removeAll();
            verticalLayout2.removeAll();

            ArrayList<Component> components = new ArrayList<>();
            components.add(button1);
            components.add(button2);
            for(int i = 0; i < 20; i++) {
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


    }

    static class ConnectingLine extends Svg {
        public ConnectingLine() {
            super("<svg ><line style=\"stroke:red;stroke-width:2\" /></svg>");
            Style style = getStyle();
            // Position the SVG absolutely, so it doesn't disturb actual components
            style.setPosition(Style.Position.ABSOLUTE);
            style.setTop("0");
            style.setLeft("0");
            style.setBottom("0");
            style.setRight("0");
        }

        public void drawLine(Component button1, Component button2) {
            getElement().executeJs("""
                        const w = document.body.clientWidth;
                        const h = this.clientHeight;
                    
                        this.firstChild.setAttribute("viewBox", "0 0 " + w + " " + h );
                    
                        const b1 = $0;
                        const b2 = $1;
                        const line = this.querySelector("line");
                        setTimeout(() => {
                            // some math to determine the center of the buttons and draw the line
                            const rect2 = b2.getBoundingClientRect();
                            line.setAttribute("x2", rect2.x + rect2.width / 2);
                            line.setAttribute("y2", rect2.y + rect2.height / 2);
                    
                            const rect1 = b1.getBoundingClientRect();
                            line.setAttribute("x1", rect1.x + rect1.width / 2);
                            line.setAttribute("y1", rect1.y + rect1.height / 2);
                        }, 0);
                    
                    """, button1.getElement(), button2.getElement());
        }
    }
}
