package org.vaadin.firitin.appframework.mobile;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.aura.Aura;
import org.vaadin.firitin.TestTheme;
import org.vaadin.firitin.appframework.MainLayout;
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

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        // A per-view "options" action contributed to the navbar, the same
        // addNavbarHelper pattern apps use on the wide layout. On a phone (bottom
        // bar) it shows as a "…" button at the top-right of the content header.
        Button options = new Button(VaadinIcon.ELLIPSIS_DOTS_H.create());
        options.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        options.setAriaLabel("Options");

        Popover popover = new Popover();
        popover.setTarget(options);
        popover.setOpenOnClick(true);
        Select<String> resolution = new Select<>();
        resolution.setLabel("Resolution");
        resolution.setItems("1 hour", "24 hours", "7 days");
        resolution.setValue("24 hours");
        popover.add(resolution, new Checkbox("Bezier curve"));
        add(popover);

        MainLayout.getCurrent().addNavbarHelper(options);
    }
}
