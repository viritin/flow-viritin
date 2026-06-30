package org.vaadin.firitin.appframework.caf;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route(value = "diary", layout = CafLayout.class)
@MenuItem(order = 10, icon = VaadinIcon.BOOK, title = "Fishing diary")
public class FishingDiaryView extends VVerticalLayout {
    public FishingDiaryView() {
        add(new Paragraph("Fishing diary (diary + session merged)"));
    }
}
