package org.vaadin.firitin.appframework.caf;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route(value = "weather/raw-data", layout = CafLayout.class)
@MenuItem(parent = WeatherHistory.class, order = 7, icon = VaadinIcon.GRID, title = "Raw data")
public class RawDataView extends VVerticalLayout {
    public RawDataView() {
        add(new Paragraph("Raw weather data"));
    }
}
