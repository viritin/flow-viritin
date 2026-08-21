package org.vaadin.firitin.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasUrlParameter;

/**
 * A view one level down from the front page: the floating header — the way
 * back, the title, the view's actions — and a content area that scrolls
 * correctly. Extend it, set a title, {@code add} the content; the header is
 * already there.
 *
 * <p>The name is Vaadin TouchKit's, which took it from the iOS of its day, and
 * the shape is the same one both were describing: an application whose front
 * page is the menu, and whose every other screen is a titled view with a way up
 * in its top corner. {@link SubViewHeader} is the bar; this is the view around
 * it, for the classes that would otherwise all compose the same three lines.
 *
 * <p>What extending buys, besides the header:
 *
 * <ul>
 * <li><b>A height that scrolls right.</b> A floor rather than
 * {@code setSizeFull()}: a view pinned to the viewport's height ends there and
 * takes its bottom padding with it, so anything past one screenful finishes
 * flush against the end of the document — on a phone that reads as a page cut
 * off rather than one that has ended. A minimum keeps what full height was for
 * (a short page still fills the screen) and lets the box grow.</li>
 * <li><b>The title in one place.</b> {@link #setTitle(String)} names the view
 * in the bar; the browser tab is still the view's own business
 * ({@code HasDynamicTitle}), because only the view knows what several of its
 * tabs need to be told apart by.</li>
 * <li><b>The way back, re-aimable.</b> The target given at construction is the
 * static case; {@link #setBackTarget(Class, Object)} is for a view whose "up"
 * is itself a parameterised route, known only once the URL arrives.</li>
 * </ul>
 */
public class NavigationView extends VerticalLayout {

    private final SubViewHeader header;

    /**
     * @param backTarget the view one level up
     * @param backLabel  where the arrow goes, for whoever cannot see that it
     *                   is an arrow — the link's accessible name
     */
    protected NavigationView(Class<? extends Component> backTarget, String backLabel) {
        header = new SubViewHeader(backTarget, backLabel);
        setMinHeight("100%");
        add(header);
    }

    protected NavigationView(String title, Class<? extends Component> backTarget,
                             String backLabel) {
        this(backTarget, backLabel);
        setTitle(title);
    }

    /** Names the view in the bar. Callable late: titles often come from a URL. */
    public void setTitle(String title) {
        header.setTitle(title);
    }

    /**
     * Re-aims the way back at a parameterised route — a detail view's sub-view
     * goes back to {@code /thing/42}, not to {@code /thing} — with the
     * parameter the URL handed over.
     */
    public <T, C extends Component & HasUrlParameter<T>> void setBackTarget(
            Class<? extends C> backTarget, T parameter) {
        header.setBackTarget(backTarget, parameter);
    }

    /**
     * Puts the view's own controls at the right edge of the bar, mirroring the
     * way back. Icon-only buttons arrive dressed as the arrow's twin.
     */
    public void addAction(Component... actions) {
        header.addAction(actions);
    }
}
