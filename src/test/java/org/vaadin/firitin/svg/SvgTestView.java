package org.vaadin.firitin.svg;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import in.virit.color.Color;
import in.virit.color.NamedColor;
import org.vaadin.addons.parttio.colorful.RgbaColorPicker;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.util.SvgElement;
import org.vaadin.firitin.util.VSvg;

@Route
public class SvgTestView extends VVerticalLayout {

    public SvgTestView() {

        add(new Paragraph("This is a test for SVG rendering in Vaadin."));

        RectAndCircle rectAndCircle = new RectAndCircle();
        rectAndCircle.setWidth("100px");
        rectAndCircle.setHeight("100px");
        add(rectAndCircle);

        add(new RgbaColorPicker(){{
            addValueChangeListener(event -> {
                rectAndCircle.setRectColor(event.getValue());
            });
        }});


    }

    /**
     *
     * <svg viewBox="0 0 10 10" xmlns="http://www.w3.org/2000/svg">
     *   <rect x="0" y="0" width="100%" height="100%" />
     *   <circle cx="50%" cy="50%" r="4" fill="white" />
     * </svg>
     */
    public static class RectAndCircle extends VSvg {
        private final SvgElement rect;
        private final SvgElement circle;

        public RectAndCircle() {
            super(0,0, 10, 10);

            rect = (SvgElement) new SvgElement("rect")
                    .setAttribute("x", "0")
                    .setAttribute("y", "0")
                    .setAttribute("width", "100%")
                    .setAttribute("height", "100%");
            circle = (SvgElement) new SvgElement("circle")
                    .setAttribute("cx", "50%")
                    .setAttribute("cy", "50%")
                    .setAttribute("r", "4")
                    .setAttribute("fill", "white");
            getElement().appendChild(rect, circle);


            circle.addEventListener("mouseover", event -> {
                circle.setFill(NamedColor.PINK);
                Notification.show("Mouse over the circle!");
            });

            circle.addEventListener("mouseout", event -> {
                circle.setFill(NamedColor.WHEAT);
            });
        }

        public void setRectColor(Color value) {
            rect.setFill(value);
        }
    }

}
