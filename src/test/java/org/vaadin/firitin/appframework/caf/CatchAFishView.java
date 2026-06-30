package org.vaadin.firitin.appframework.caf;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route(value = "", layout = CafLayout.class)
@MenuItem(order = MenuItem.BEGINNING, icon = VaadinIcon.HOME, title = "Catch-a-Fish")
public class CatchAFishView extends VVerticalLayout {
    public CatchAFishView() {
        add(new Paragraph("Catch-a-Fish home"));
    }
}
