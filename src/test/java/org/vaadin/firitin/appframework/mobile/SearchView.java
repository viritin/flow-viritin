package org.vaadin.firitin.appframework.mobile;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.aura.Aura;
import org.vaadin.firitin.TestTheme;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route(value = "search", layout = MobileDemoLayout.class)
@MenuItem(icon = VaadinIcon.SEARCH)
@TestTheme(Aura.class)
public class SearchView extends VVerticalLayout {
    public SearchView() {
        add(new Paragraph("Search content"));
    }
}
