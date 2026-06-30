package org.vaadin.firitin.appframework.caf;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route(value = "weather/humidity", layout = CafLayout.class)
@MenuItem(parent = WeatherHistory.class, order = 3, icon = VaadinIcon.DROP, title = "Humidity")
public class HumidityView extends VVerticalLayout {
    public HumidityView() {
        add(new Paragraph("Humidity history"));
    }
}
