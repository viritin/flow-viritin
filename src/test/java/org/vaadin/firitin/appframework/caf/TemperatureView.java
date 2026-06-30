package org.vaadin.firitin.appframework.caf;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route(value = "weather/temperature", layout = CafLayout.class)
@MenuItem(parent = WeatherHistory.class, order = 1, icon = VaadinIcon.CHART, title = "Temperature")
public class TemperatureView extends VVerticalLayout {
    public TemperatureView() {
        add(new Paragraph("Temperature history"));
    }
}
