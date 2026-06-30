package org.vaadin.firitin.appframework.caf;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route(value = "weather/wind", layout = CafLayout.class)
@MenuItem(parent = WeatherHistory.class, order = 5, icon = VaadinIcon.AIRPLANE, title = "Wind")
public class WindView extends VVerticalLayout {
    public WindView() {
        add(new Paragraph("Wind history"));
    }
}
