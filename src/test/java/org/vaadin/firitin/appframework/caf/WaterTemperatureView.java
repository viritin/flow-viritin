package org.vaadin.firitin.appframework.caf;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route(value = "weather/water-temperature", layout = CafLayout.class)
@MenuItem(parent = WeatherHistory.class, order = 2, icon = VaadinIcon.DROP, title = "Water Temperature")
public class WaterTemperatureView extends VVerticalLayout {
    public WaterTemperatureView() {
        add(new Paragraph("Water temperature history"));
    }
}
