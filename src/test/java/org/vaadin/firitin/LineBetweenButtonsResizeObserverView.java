package org.vaadin.firitin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Svg;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.util.ResizeObserver;
import org.vaadin.firitin.util.VStyleUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

@Route
public class LineBetweenButtonsResizeObserverView extends HorizontalLayout {

    public LineBetweenButtonsResizeObserverView() {
        setWidthFull();

        VStyleUtil.injectAsFirst("""
            vaadin-button {
                background-color: lightblue;
            }
        """);

        var button1 = new Button("Button 1");
        var button2 = new Button("Button 2");
        button2.setWidthFull();
        button2.setHeight("200px");

        ConnectingLine line = new ConnectingLine(button1, button2);
        add(line);

        add(button1, button2);

        VerticalLayout verticalLayout1 = new VerticalLayout();
        verticalLayout1.add(button1);

        VerticalLayout verticalLayout2 = new VerticalLayout();
        verticalLayout2.add(button2);

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

    private static class ConnectingLine extends Svg {
        private int x1;
        private int y1;
        private int x2;
        private int y2;

        public ConnectingLine(Component component1, Component component2) {
            super("");
            Style style = getStyle();
            style.setPosition(Style.Position.ABSOLUTE);
            style.setTop("0");
            style.setLeft("0");
            style.setBottom("0");
            style.setRight("0");
        }

        private void drawLine(int x1, int y1, int x2, int y2) {
            int width = Math.max(x1, x2);
            int height = Math.max(y1, y2);
            setSvg("""
                    <svg viewBox="0 0 %d %d">
                        <line style="stroke:red;stroke-width:2" x1="%d" y1="%d" x2="%d" y2="%d" />
                    </svg>
                    """.formatted(width, height, x1, y1, x2, y2));
        }

        public void drawLine(Component component1, Component component2) {
            ResizeObserver.get().observe(component1, dimensions -> {
                this.x1 = dimensions.offsetLeft() + dimensions.offsetWidth() / 2;
                this.y1 = dimensions.offsetTop() + dimensions.offsetHeight() / 2;
            });
            ResizeObserver.get().observe(component2, dimensions -> {
                this.x2 = dimensions.offsetLeft() + dimensions.offsetWidth() / 2;
                this.y2 = dimensions.offsetTop() + dimensions.offsetHeight() / 2;
                drawLine(x1, y1, x2, y2);
            });

        }
    }

}
