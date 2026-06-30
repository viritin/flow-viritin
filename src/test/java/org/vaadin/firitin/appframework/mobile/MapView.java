package org.vaadin.firitin.appframework.mobile;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.aura.Aura;
import org.vaadin.firitin.TestTheme;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.appframework.MobileMainLayout;

/**
 * Demonstrates a full-bleed, edge-to-edge view — e.g. a map. {@link
 * #setSizeFull()} makes it fill the available content area, and {@link
 * MobileMainLayout#EDGE_TO_EDGE} opts out of the bottom-bar clearance so the
 * content runs all the way under the floating bar (which just overlays it). No
 * {@code 100lvh} or fixed/absolute hacks needed.
 */
@Route(value = "map", layout = MobileDemoLayout.class)
@MenuItem(icon = VaadinIcon.MAP_MARKER)
@TestTheme(Aura.class)
public class MapView extends Div {

    public MapView() {
        setText("A view that fills the available space (here: a coloured \"map\").");
        Style s = getStyle();
        s.setBackground("#2a9d8f url('/bg.jpg') center / cover no-repeat"); // a clear, map-like teal
        s.setDisplay(Style.Display.FLEX);
        s.setAlignItems(Style.AlignItems.CENTER);
        s.setJustifyContent(Style.JustifyContent.CENTER);
        s.setTextAlign(Style.TextAlign.CENTER);

        // Fill the content area and run edge-to-edge under the floating bar.
        setSizeFull();
        addClassName(MobileMainLayout.EDGE_TO_EDGE);
    }

}
