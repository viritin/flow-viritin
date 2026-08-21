package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import org.vaadin.firitin.layouts.NavigationView;

/**
 * A {@link NavigationView} in its natural habitat: a mobile-first application
 * whose front page is the menu, and whose every other screen is a titled view
 * with a way up in its top corner. Open the front page route, tap through, and
 * scroll — the header floats over the content on a veil of blur, and the back
 * circle is glass on the Aura theme.
 */
@Route
public class NavigationViewExample extends NavigationView {

    public NavigationViewExample() {
        super("Cabin thermometer", FrontPage.class, "Devices");

        // The view's own controls mirror the way back at the other edge.
        Button edit = new Button(VaadinIcon.PENCIL.create(),
                e -> Notification.show("This is where its settings would open."));
        edit.getElement().setAttribute("aria-label", "Settings");
        addAction(edit);

        add(new Paragraph("This view extends NavigationView: the floating header "
                + "above came with the base class, along with a min-height that "
                + "makes long content scroll correctly on phones."));
        for (int i = 1; i <= 15; i++) {
            add(new Paragraph("Reading " + i + " — filler so there is something "
                    + "to scroll. Watch the header stay put and the content "
                    + "dissolve under it."));
        }
    }

    /** The level above: any ordinary view. The arrow in the header leads here. */
    @Route("navigation-view-example-front")
    public static class FrontPage extends VerticalLayout {

        public FrontPage() {
            add(new H1("Devices"),
                    new Paragraph("A front page acting as the menu. The link below "
                            + "opens a sub-view whose header knows the way back here."),
                    new RouterLink("Cabin thermometer", NavigationViewExample.class));
        }
    }
}
