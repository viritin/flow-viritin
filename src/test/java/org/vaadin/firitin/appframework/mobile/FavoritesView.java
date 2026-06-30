package org.vaadin.firitin.appframework.mobile;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.aura.Aura;
import org.vaadin.firitin.TestTheme;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route(value = "favorites", layout = MobileDemoLayout.class)
@MenuItem(icon = VaadinIcon.HEART)
@TestTheme(Aura.class)
public class FavoritesView extends VVerticalLayout {
    public FavoritesView() {
        // A full-width image to see how media behaves in the mobile layout.
        Image image = new Image("https://picsum.photos/1200/600", "Sample image");
        image.setWidthFull();

        add(image, new Paragraph("Favorites content"));
    }
}
