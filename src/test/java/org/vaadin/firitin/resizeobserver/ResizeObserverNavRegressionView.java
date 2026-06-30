package org.vaadin.firitin.resizeobserver;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.appframework.MobileMainLayout;
import org.vaadin.firitin.appframework.NavigationItem;
import org.vaadin.firitin.util.ResizeObserver;

/**
 * Reproduction harness for a ResizeObserver regression observed on Vaadin 25.2,
 * which appears to surface specifically when navigating between views that share
 * a {@link MobileMainLayout}.
 * <p>
 * The UI-scoped {@link ResizeObserver} normally reports the <em>initial</em>
 * size of a component as soon as you start observing it (the browser fires the
 * ResizeObserver callback once on observe). That works for the first view that
 * creates the observer, but the regression is: once the observer already exists
 * on the UI, a view navigated to afterwards — that starts observing a component
 * in its {@code onAttach} phase — never receives that initial size callback.
 * (One plausible cause: under MobileMainLayout's content wrapping the observed
 * element has no laid-out size at the moment {@code observe()} runs, so the
 * browser's single initial callback is for a 0-sized / disconnected element and
 * the helper ignores it — and nothing resizes it afterwards.)
 * <p>
 * The harness offers two pairs of cross-linked views that build in
 * {@code onAttach} and observe a box, surfacing the size in a {@code #ro-status}
 * element:
 * <ul>
 *   <li><b>{@code /ro-mob-a} ↔ {@code /ro-mob-b}</b> — share a MobileMainLayout
 *       (the suspected case).</li>
 *   <li><b>{@code /ro-plain-a} ↔ {@code /ro-plain-b}</b> — share a plain
 *       AppLayout (the control; expected to work).</li>
 * </ul>
 * To reproduce: open the first view of a pair (status flips to "first size
 * #1: W x H"), then click "Go to the other view". A working pair shows a first
 * size on the second view too; the regressing pair stays on "WAITING…".
 */
public class ResizeObserverNavRegressionView {

    // ---- Shared building block ------------------------------------------------

    /** Builds itself in onAttach and observes a box, reporting the first size. */
    public static abstract class Base extends VerticalLayout {

        protected abstract String name();

        protected abstract Class<? extends Component> otherView();

        @Override
        protected void onAttach(AttachEvent attachEvent) {
            super.onAttach(attachEvent);
            removeAll();

            add(new H3("ResizeObserver nav regression — " + name()));
            add(new RouterLink("Go to the other view", otherView()));

            Span status = new Span("WAITING for first size…");
            status.setId("ro-status");
            status.getElement().getThemeList().add("badge error");
            add(status);

            Div observed = new Div();
            observed.setText("observed box (" + name() + ")");
            observed.setWidthFull();
            observed.setHeight("160px");
            observed.getStyle().setBackground("#cde3f5").setPadding("1em");
            add(observed);

            int[] callbacks = {0};
            ResizeObserver.get().observe(observed, dim -> {
                callbacks[0]++;
                status.setText("first size #" + callbacks[0] + ": "
                        + dim.width() + " x " + dim.height());
                status.getElement().getThemeList().clear();
                status.getElement().getThemeList().add("badge success");
            });
        }
    }

    // ---- Suspected case: MobileMainLayout ------------------------------------

    public static class RoMobileLayout extends MobileMainLayout {
        @Override
        protected Object getDrawerHeader() {
            return "RO regression (mobile)";
        }

        @Override
        protected boolean checkAccess(NavigationItem item) {
            Class<?> t = item.getNavigationTarget();
            return t == MobileA.class || t == MobileB.class;
        }
    }

    @Route(value = "ro-mob-a", layout = RoMobileLayout.class)
    @MenuItem(title = "A", icon = VaadinIcon.CIRCLE)
    public static class MobileA extends Base {
        @Override protected String name() { return "MobileMainLayout A"; }
        @Override protected Class<? extends Component> otherView() { return MobileB.class; }
    }

    @Route(value = "ro-mob-b", layout = RoMobileLayout.class)
    @MenuItem(title = "B", icon = VaadinIcon.STAR)
    public static class MobileB extends Base {
        @Override protected String name() { return "MobileMainLayout B"; }
        @Override protected Class<? extends Component> otherView() { return MobileA.class; }
    }

    // ---- Control case: plain AppLayout ---------------------------------------

    public static class RoPlainLayout extends AppLayout {
        public RoPlainLayout() {
            addToNavbar(new HorizontalLayout(
                    new RouterLink("Plain A", PlainA.class),
                    new RouterLink("Plain B", PlainB.class)));
        }
    }

    @Route(value = "ro-plain-a", layout = RoPlainLayout.class)
    public static class PlainA extends Base {
        @Override protected String name() { return "Plain AppLayout A"; }
        @Override protected Class<? extends Component> otherView() { return PlainB.class; }
    }

    @Route(value = "ro-plain-b", layout = RoPlainLayout.class)
    public static class PlainB extends Base {
        @Override protected String name() { return "Plain AppLayout B"; }
        @Override protected Class<? extends Component> otherView() { return PlainA.class; }
    }
}
