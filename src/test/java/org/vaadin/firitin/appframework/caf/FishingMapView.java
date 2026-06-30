package org.vaadin.firitin.appframework.caf;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route(value = "map", layout = CafLayout.class)
@MenuItem(order = 30, icon = VaadinIcon.GLOBE, title = "Fishing Map")
public class FishingMapView extends VVerticalLayout {
    public FishingMapView() {
        add(new Paragraph("Fishing Map"));
    }
}
