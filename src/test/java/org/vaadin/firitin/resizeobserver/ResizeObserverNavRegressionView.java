package org.vaadin.firitin.resizeobserver;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.appframework.MobileMainLayout;
import org.vaadin.firitin.appframework.NavigationItem;
import org.vaadin.firitin.util.ResizeObserver;

/**
 * Reproduction harness for the regression isolated in the {@code repro-attach-sync}
 * project: under Viritin's {@link MobileMainLayout}, components that build their
 * content lazily (in a {@link ResizeObserver} callback, or in {@code onAttach})
 * render correctly on a full page load but come up <strong>empty after a
 * client-side (SPA) navigation</strong> between sibling routes. A plain
 * {@link RouterLayout} does not trigger it.
 * <p>
 * Mirror of the repro, in the library's own test sources so it can be driven by
 * the test server + Playwright. Each pair has a Home and an Other view with
 * cross-links; Home hosts three probes:
 * <ul>
 *   <li><b>#probe-resize</b> — Viritin {@link ResizeObserver}: observes itself in
 *       the constructor and builds its content in the resize callback.</li>
 *   <li><b>#probe-onattach</b> — pure Vaadin: builds children in {@code onAttach}.</li>
 *   <li><b>#probe-js</b> — pure Vaadin: {@code Element.executeJs} in {@code onAttach}.</li>
 * </ul>
 * Suspected case under {@link RoMobileLayout} ({@code /ro-mob-home}); control case
 * under {@link RoPlainLayout} ({@code /ro-plain-home}). To reproduce: full-load
 * Home, click to Other, click Back home, then inspect the probes.
 */
public class ResizeObserverNavRegressionView {

    // ---- Probes ---------------------------------------------------------------

    /** Viritin ResizeObserver: observes itself in the constructor, builds in the callback. */
    public static class ResizeBuiltProbe extends Div {
        public ResizeBuiltProbe() {
            setId("probe-resize");
            getStyle().setBorder("2px solid orange").setPadding("8px");
            setText("(resize callback did NOT run)");
            ResizeObserver.get().observe(this, dim -> {
                removeAll();
                add(new Span("[resize callback ran, width=" + dim.width() + "]"));
            });
        }
    }

    /** Pure Vaadin: builds children only in onAttach. */
    public static class OnAttachChildrenProbe extends Div {
        public OnAttachChildrenProbe() {
            setId("probe-onattach");
            getStyle().setBorder("2px solid green").setPadding("8px");
            setText("(onAttach did NOT build)");
        }

        @Override
        protected void onAttach(AttachEvent e) {
            super.onAttach(e);
            removeAll();
            add(new Span("[children built in onAttach] "));
            Element raw = new Element("b");
            raw.setText("[raw element child]");
            getElement().appendChild(raw);
        }
    }

    /** Pure Vaadin: executeJs in onAttach. */
    public static class OnAttachJsProbe extends Div {
        public OnAttachJsProbe() {
            setId("probe-js");
            getStyle().setBorder("2px solid blue").setPadding("8px");
            setText("(executeJs did NOT run)");
        }

        @Override
        protected void onAttach(AttachEvent e) {
            super.onAttach(e);
            getElement().executeJs("this.textContent = '[executeJs ran in onAttach]';");
        }
    }

    /** Shared Home content: cross-link + the three probes. */
    public static abstract class HomeBase extends VerticalLayout {
        protected abstract Class<? extends Component> otherView();

        public HomeBase() {
            add(new H2("Home"));
            add(new RouterLink("Go to other view (SPA nav)", otherView()));
            add(new H3("1) Viritin ResizeObserver"));
            add(new ResizeBuiltProbe());
            add(new H3("2) Pure Vaadin children in onAttach"));
            add(new OnAttachChildrenProbe());
            add(new H3("3) Pure Vaadin executeJs in onAttach"));
            add(new OnAttachJsProbe());
        }
    }

    public static abstract class OtherBase extends VerticalLayout {
        protected abstract Class<? extends Component> homeView();

        public OtherBase() {
            add(new H2("Other view"));
            add(new RouterLink("Back home (SPA nav)", homeView()));
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
            return t == MobileHome.class || t == MobileOther.class;
        }
    }

    @Route(value = "ro-mob-home", layout = RoMobileLayout.class)
    @MenuItem(title = "Home", icon = VaadinIcon.HOME)
    public static class MobileHome extends HomeBase {
        @Override protected Class<? extends Component> otherView() { return MobileOther.class; }
    }

    @Route(value = "ro-mob-other", layout = RoMobileLayout.class)
    @MenuItem(title = "Other", icon = VaadinIcon.STAR)
    public static class MobileOther extends OtherBase {
        @Override protected Class<? extends Component> homeView() { return MobileHome.class; }
    }

    // ---- Control case: plain RouterLayout ------------------------------------

    public static class RoPlainLayout extends VerticalLayout implements RouterLayout {
    }

    @Route(value = "ro-plain-home", layout = RoPlainLayout.class)
    public static class PlainHome extends HomeBase {
        @Override protected Class<? extends Component> otherView() { return PlainOther.class; }
    }

    @Route(value = "ro-plain-other", layout = RoPlainLayout.class)
    public static class PlainOther extends OtherBase {
        @Override protected Class<? extends Component> homeView() { return PlainHome.class; }
    }
}
