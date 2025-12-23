package org.vaadin.firitin.svg;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import in.virit.color.Color;
import in.virit.color.NamedColor;
import org.vaadin.addons.parttio.colorful.RgbaColorPicker;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.VSvg;
import org.vaadin.firitin.element.svg.CircleElement;
import org.vaadin.firitin.element.svg.RectElement;

@Route
public class SvgTestView extends VVerticalLayout {

    public SvgTestView() {

        add(new Paragraph("This is a test for SVG rendering in Vaadin."));

        RectAndCircle rectAndCircle = new RectAndCircle();
        rectAndCircle.setWidth("100px");
        rectAndCircle.setHeight("100px");
        add(rectAndCircle);

        add(new H3("Adjust color directly to element (rect)"));
        add(new RgbaColorPicker(){{
            addValueChangeListener(event -> {
                rectAndCircle.setRectColor(event.getValue());
            });
        }});

        add(new H3("Adjust color directly to element (circle)"));
        add(new RgbaColorPicker(){{
            addValueChangeListener(event -> {
                rectAndCircle.setCirleColor(event.getValue());
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
        private final RectElement rect;
        private final CircleElement circle;

        public RectAndCircle() {
            super(0,0, 10, 10);

            rect = new RectElement()
                    .x(0).y(0)
                    .width("100%").height("100%");
            circle = new CircleElement()
                    .center("50%", "50%")
                    .r(4)
                    .fill(NamedColor.WHITE);
            getElement().appendChild(rect, circle);


            circle.addEventListener("mouseover", event -> {
                circle.fill(NamedColor.PINK);
                Notification.show("Mouse over the circle -> circle fill to PINK");
            });

            circle.addEventListener("mouseout", event -> {
                circle.fill(NamedColor.WHEAT);
                Notification.show("Mouse over the circle -> circle fill to WHEAT");
            });
        }

        public void setRectColor(Color value) {
            rect.fill(value);
        }

        public void setCirleColor(Color value) {
            circle.fill(value);
        }
    }

}
