package org.vaadin.firitin.appframework.caf;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route(value = "weather/air-pressure", layout = CafLayout.class)
@MenuItem(parent = WeatherHistory.class, order = 4, icon = VaadinIcon.DASHBOARD, title = "Air Pressure")
public class AirPressureView extends VVerticalLayout {
    public AirPressureView() {
        add(new Paragraph("Air pressure history"));
    }
}
