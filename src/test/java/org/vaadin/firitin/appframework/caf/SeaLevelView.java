package org.vaadin.firitin.appframework.caf;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route(value = "weather/sea-level", layout = CafLayout.class)
@MenuItem(parent = WeatherHistory.class, order = 6, icon = VaadinIcon.ANCHOR, title = "Sea Level")
public class SeaLevelView extends VVerticalLayout {
    public SeaLevelView() {
        add(new Paragraph("Sea level history"));
    }
}
