package org.vaadin.firitin.appframework.mobile;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.aura.Aura;
import org.vaadin.firitin.TestTheme;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

import java.util.stream.IntStream;

@Route(value = "", layout = MobileDemoLayout.class)
@MenuItem(order = MenuItem.BEGINNING, icon = VaadinIcon.HOME)
@TestTheme(Aura.class)
public class HomeView extends VVerticalLayout {
    public HomeView() {
        // Plenty of content so the large title scrolls away on a phone.
        IntStream.rangeClosed(1, 40).forEach(i ->
                add(new Paragraph("Home content line " + i)));
    }
}
