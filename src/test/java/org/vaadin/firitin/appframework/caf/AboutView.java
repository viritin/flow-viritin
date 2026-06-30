package org.vaadin.firitin.appframework.caf;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route(value = "about", layout = CafLayout.class)
@MenuItem(order = 40, icon = VaadinIcon.INFO_CIRCLE, title = "About")
public class AboutView extends VVerticalLayout {
    public AboutView() {
        add(new Paragraph("About Catch-a-Fish"));
    }
}
