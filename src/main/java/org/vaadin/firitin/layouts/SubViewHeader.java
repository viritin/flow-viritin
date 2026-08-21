package org.vaadin.firitin.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.RouterLink;

/**
 * A floating header line for a sub-view: the way back at the left edge, the
 * view's name in the middle, and room for the view's actions at the right edge.
 *
 * <p>A small mobile-first application often has no navigation shell: the front
 * page is the menu, and every other view sits one level below it. What such a
 * view needs at the top is a way up and its own name, and iOS settled the form
 * years ago — a chevron in a circle of glass, the title beside it, floating
 * over the content as it scrolls.
 *
 * <p>Only the arrow gets the glass. A first draft put the arrow and the title
 * in one shared pill, and it read as a single button whose label was the name
 * of the view you were already on — a back button to the place you are at. The
 * title is bare text, as in iOS's current form language: the circle says
 * "control", the undecorated word says "name", and the difference in dress is
 * what keeps them apart.
 *
 * <p>The back control is an arrow alone. The word next to it ("Devices",
 * "Back") is dead weight once the gesture is learned, which on a phone is the
 * first time it is used — but a control with no text still has to say where it
 * goes, so the destination's name travels as the link's {@code aria-label}
 * instead of as pixels.
 *
 * <p>The bar is {@code position: sticky}: it scrolls away with the page until
 * it would leave, then stays — its offset counts the safe area, so an installed
 * PWA does not park the arrow under a phone's camera cutout — and the content
 * slides underneath, dissolving into a veil of blur rather than hitting an
 * edge. The bar itself takes no clicks; between the pieces, the page underneath
 * does. On the Aura theme the glass is built from the theme's own surface
 * tokens; on any other theme the fallbacks make a plain translucent circle and
 * nothing breaks.
 *
 * <p>The title is an {@code <h1>}: on a view like this it is the page's main
 * heading, and the document outline should say so.
 *
 * <p>{@link NavigationView} is the view around this bar, for the classes that
 * would rather inherit the whole arrangement than compose it.
 */
@StyleSheet("context://assets/org/vaadin/firitin/components/sub-view-header.css")
public class SubViewHeader extends Header {

    private final H1 title = new H1();
    private final Div actions = new Div();
    private final RouterLink back = new RouterLink();

    /**
     * @param backTarget the view one level up
     * @param backLabel  where the arrow goes, for whoever cannot see that it is
     *                   an arrow — read out as the link's accessible name
     */
    public SubViewHeader(Class<? extends Component> backTarget, String backLabel) {
        addClassName("sub-view-header");

        back.setRoute(backTarget);
        back.add(VaadinIcon.CHEVRON_LEFT.create());
        back.getElement().setAttribute("aria-label", backLabel);
        /*
           aura-surface asks Aura to recompute its surface colour on the link
           itself, which is what lets the stylesheet raise the circle's level.
           Without it the surface tokens arrive already resolved from the page,
           and the level the stylesheet sets would change nothing. On other
           themes the class is inert.
        */
        back.addClassName("aura-surface");

        actions.addClassName("sub-view-header-actions");

        // Three zones of a grid; the stylesheet is what places them.
        add(back, title, actions);
    }

    public SubViewHeader(String title, Class<? extends Component> backTarget, String backLabel) {
        this(backTarget, backLabel);
        setTitle(title);
    }

    /** Separate from construction because a title from a URL parameter arrives later. */
    public void setTitle(String text) {
        title.setText(text);
    }

    /**
     * The way back for a view more than one level down, where "up" is itself a
     * parameterised route — a detail view's sub-view goes back to
     * {@code /thing/42}, not to {@code /thing}. Separate from construction for
     * the same reason as the title: the parameter comes from the URL, which the
     * view only sees later.
     */
    public <T, C extends Component & HasUrlParameter<T>> void setBackTarget(
            Class<? extends C> backTarget, T parameter) {
        back.setRoute(backTarget, parameter);
    }

    /**
     * Puts the view's own controls at the right edge of the bar, mirroring the
     * way back. Icon-only buttons arrive dressed by the stylesheet as the
     * arrow's twin; ones with text become pills.
     */
    public void addAction(Component... components) {
        actions.add(components);
    }
}
