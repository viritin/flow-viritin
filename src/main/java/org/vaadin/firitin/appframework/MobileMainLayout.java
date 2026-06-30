package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.RouterLink;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A {@link MainLayout} tuned for phones, in the spirit of native iOS/Android
 * apps: the primary navigation is an icon-only <strong>bottom bar</strong>
 * (tabs at the bottom of the screen) instead of a side drawer, and the view
 * title is a large heading at the top of the content that scrolls away as the
 * user scrolls down.
 * <p>
 * It is <strong>adaptive</strong>, not phone-only. The same instance keeps the
 * familiar side drawer on wide (desktop) viewports — Vaadin's {@link
 * com.vaadin.flow.component.applayout.AppLayout AppLayout} renders the
 * bottom bar (a "touch-optimized" navbar) only on small/touch screens
 * (≈{@code max-width: 800px}) and the persistent drawer on wider ones. So an
 * application whose primary device is an iPhone still looks right on a laptop.
 * <p>
 * The bottom bar is built from the very same navigation model as the drawer
 * (see {@link #onMenuBuilt(List)}), so {@link MenuItem}/{@link Menu} icons,
 * ordering, {@link #checkAccess(NavigationItem) access control} and i18n all
 * apply unchanged. Each entry is a {@link RouterLink} that highlights itself
 * when its route is active. Because a bottom bar fits only a handful of icons,
 * at most {@link #getMaxBottomNavItems() five} entries are shown; if there are
 * more top-level views (or any grouped {@link SubMenu} items, which a flat bar
 * cannot show), the last slot becomes a <em>"More"</em> action that opens the
 * full drawer — overridable via {@link #openOverflow()}.
 * <p>
 * Subclasses only need to implement {@link #getDrawerHeader()} like with
 * {@link MainLayout}.
 *
 * @deprecated New experimental component; its API is likely to evolve even in
 *             patch releases.
 */
@Deprecated
@StyleSheet("context://assets/org/vaadin/firitin/components/mobile-main-layout.css")
public abstract class MobileMainLayout extends MainLayout {

    /** Maximum number of icons in the bottom bar before a "More" item is used. */
    public static final int DEFAULT_MAX_BOTTOM_NAV_ITEMS = 5;

    /**
     * Style class name to add to a view to make it run <em>edge to edge</em>: its
     * content fills under the floating bottom bar, with no reserved clearance
     * (the bar just floats on top). Use it for full-bleed, size-full views such
     * as a map; ordinary views keep the clearance so bottom content stays visible.
     * <pre>
     * mapView.addClassName(MobileMainLayout.EDGE_TO_EDGE);
     * </pre>
     */
    public static final String EDGE_TO_EDGE = "mobile-edge-to-edge";

    private final Div bottomNav = new Div();
    private final Div header = new Div();
    private final DrawerToggle drawerToggle = new DrawerToggle();
    private final H1 pageTitle = new H1();
    private final Div headerHelpers = new Div();
    private final Div contentWrapper = new Div();
    private final Map<Class<?>, RouterLink> bottomNavLinks = new HashMap<>();
    // Group (popover) bottom-bar items mapped to the route classes of their
    // children, so a group can be marked active when one of its children shows.
    private final Map<Component, Set<Class<?>>> bottomNavGroups = new LinkedHashMap<>();
    private Class<?> currentViewClass;
    private int maxBottomNavItems = DEFAULT_MAX_BOTTOM_NAV_ITEMS;
    private boolean bottomNavLabelsVisible;
    private boolean bottomNavHideOnScroll = true;
    private boolean bodyScrolling = true;
    private boolean viewTitleVisible = false;
    private boolean bottomNavInitialized;

    // Turns on body/page scrolling by tagging the document root; our stylesheet
    // hooks that class to undo Flow's html/body/#outlet height:100% so the mobile
    // browser chrome can collapse and content scrolls edge-to-edge. The viewport
    // (incl. viewport-fit) is left to the application's own config (Vaadin's
    // defaults / AppShell), so we do not touch the viewport meta here.
    private static final String BODY_SCROLL_ON_JS =
            "document.documentElement.classList.add('viritin-body-scroll');";
    private static final String BODY_SCROLL_OFF_JS =
            "document.documentElement.classList.remove('viritin-body-scroll');";

    // Measures the bottom bar's height into the --mobile-bottom-nav-space custom
    // property, kept up to date with a ResizeObserver (it changes with the label
    // mode, theme, etc.). The stylesheet uses it to reserve exactly that much
    // bottom space in the content, so content (e.g. a save button at the bottom)
    // clears the floating bar without each view hard-coding a padding.
    private static final String NAV_SPACE_JS = """
        const layout = this;
        if (layout.__mblNavSpace) return;
        layout.__mblNavSpace = true;
        const apply = () => {
          const bar = layout.querySelector('.mobile-bottom-nav');
          if (bar) layout.style.setProperty('--mobile-bottom-nav-space', bar.offsetHeight + 'px');
        };
        const ro = new ResizeObserver(apply);
        const wire = (tries) => {
          const bar = layout.querySelector('.mobile-bottom-nav');
          if (bar) { ro.observe(bar); apply(); }
          else if (tries > 0) requestAnimationFrame(() => wire(tries - 1));
        };
        wire(30);
        """;

    // Client-side scroll watcher: hides the bottom bar when the content is
    // scrolled down and brings it back on scroll up (iOS-style). It toggles the
    // "mobile-bottom-nav--hidden" class with no server round-trip, reads the live
    // "nav-autohide" attribute so it can be turned off at runtime, and guards the
    // listener so re-running on re-attach does not stack duplicates. The scroll
    // source depends on the mode: the AppLayout content area normally, or the
    // window when body scrolling is on (the "body-scroll" attribute).
    private static final String AUTO_HIDE_JS = """
        const layout = this;
        const wire = (tries) => {
          const useWindow = layout.hasAttribute('body-scroll');
          const scroller = useWindow
              ? document.scrollingElement
              : (layout.shadowRoot && layout.shadowRoot.querySelector('[part~="content"]'));
          if (!scroller) { if (tries > 0) requestAnimationFrame(() => wire(tries - 1)); return; }
          const target = useWindow ? window : scroller;
          if (target.__mblAutoHide) return;
          target.__mblAutoHide = true;
          let last = scroller.scrollTop, ticking = false;
          target.addEventListener('scroll', () => {
            if (ticking) return;
            ticking = true;
            requestAnimationFrame(() => {
              const y = scroller.scrollTop;
              const bar = layout.querySelector('.mobile-bottom-nav');
              if (bar) {
                if (!layout.hasAttribute('nav-autohide')) {
                  bar.classList.remove('mobile-bottom-nav--hidden');
                } else if (y > last + 4 && y > 48) {
                  bar.classList.add('mobile-bottom-nav--hidden');
                } else if (y < last - 4) {
                  bar.classList.remove('mobile-bottom-nav--hidden');
                }
              }
              last = y;
              ticking = false;
            });
          }, { passive: true });
        };
        wire(20);
        """;

    public MobileMainLayout() {
        bottomNav.addClassName("mobile-bottom-nav");
        pageTitle.addClassName("mobile-view-title");
        contentWrapper.addClassName("mobile-content");

        // The view's header lives in the content (not in a dedicated navbar):
        // a single row with the drawer toggle, the large scroll-away title, and
        // any per-view helper components on the right.
        drawerToggle.addClassName("mobile-drawer-toggle");
        drawerToggle.getElement().setAttribute("aria-label", "Menu toggle");
        headerHelpers.addClassName("mobile-content-header-helpers");
        header.addClassName("mobile-content-header");
        header.add(drawerToggle, pageTitle, headerHelpers);

        // Apply the initial title visibility (off by default). The title element
        // stays rendered (its text keeps syncing) and visibility is driven purely
        // by the "no-view-title" class: in phone-like mode (bottom bar) that
        // collapses the header, while a wide desktop always shows the title — see
        // the CSS. (Using setVisible(false) here would stop the text from ever
        // reaching the client, so CSS could not reveal it on desktop.)
        header.getClassNames().set("no-view-title", !viewTitleVisible);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getElement().setAttribute("nav-autohide", bottomNavHideOnScroll);
        getElement().setAttribute("body-scroll", bodyScrolling);
        if (bodyScrolling) {
            getElement().executeJs(BODY_SCROLL_ON_JS);
        }
        // Re-runs on each attach; an internal guard keeps it from stacking
        // duplicate scroll listeners.
        getElement().executeJs(AUTO_HIDE_JS);
        getElement().executeJs(NAV_SPACE_JS);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // The body-scroll class lives on the document root, so drop it when this
        // layout goes away (e.g. navigating to a view under a different layout),
        // otherwise the page-scroll behaviour would linger for the next view.
        if (bodyScrolling) {
            getElement().executeJs(BODY_SCROLL_OFF_JS);
        }
        super.onDetach(detachEvent);
    }

    /**
     * Leaves the top navbar empty on purpose: the drawer toggle and view title
     * are rendered into the content header row instead (see
     * {@link #showContent(Component)}), so no full-width bar is reserved just for
     * them. The toggle is the universal way to open the drawer on wide and on
     * narrow non-touch screens; on a touch phone, where the bottom bar's "More"
     * already reaches the drawer, it is hidden by CSS.
     */
    @Override
    protected void addHeaderContent() {
    }

    /**
     * Places per-view helper components into the content header row (right side)
     * rather than the navbar. They are cleared automatically on navigation.
     */
    @Override
    public void addNavbarHelper(Component component) {
        headerHelpers.add(component);
    }

    /**
     * (Re)builds the bottom navigation bar from the visible top-level items
     * whenever the menu changes, mirroring the drawer the base class just built.
     */
    @Override
    protected void onMenuBuilt(List<NavigationItem> topLevelItems) {
        if (!bottomNavInitialized) {
            // The "true" makes this a touch-optimized navbar: AppLayout renders
            // it as a bottom bar on small/touch screens and hides it on desktop.
            addToNavbar(true, bottomNav);
            bottomNavInitialized = true;
        }
        bottomNav.removeAll();
        bottomNavLinks.clear();
        bottomNavGroups.clear();
        // Lets the stylesheet give labelled items more height than icon-only ones.
        bottomNav.getClassNames().set("mobile-bottom-nav--labeled", bottomNavLabelsVisible);

        // Each top-level item becomes a bottom-bar item: a routable leaf is a link,
        // a group (has children) opens its children in a popover (two-level), so
        // sub-views are reachable without the drawer. Disabled/childless non-routable
        // items are skipped.
        List<NavigationItem> shown = topLevelItems.stream()
                .filter(item -> item.isEnabled())
                .filter(item -> item instanceof BasicNavigationItem || !getChildItems(item).isEmpty())
                .toList();
        boolean moreNeeded = shown.size() > maxBottomNavItems;
        int directCount = Math.min(
                moreNeeded ? maxBottomNavItems - 1 : shown.size(),
                shown.size());

        shown.stream().limit(directCount).forEach(item -> {
            List<NavigationItem> children = getChildItems(item);
            bottomNav.add(children.isEmpty()
                    ? createNavLink(item)
                    : createGroupItem(item, children));
        });
        if (moreNeeded) {
            bottomNav.add(createMoreItem());
        }
        updateBottomNavHighlight();
    }

    private RouterLink createNavLink(NavigationItem item) {
        @SuppressWarnings("unchecked")
        Class<? extends Component> target =
                (Class<? extends Component>) item.getNavigationTarget();
        RouterLink link = new RouterLink();
        link.addClassName("mobile-bottom-nav-item");
        link.setRoute(target);
        // Highlight the link when its exact route is the current location.
        // RouterLink does this itself on each client navigation (setting AND
        // clearing the [highlight] attribute), but only for links already
        // attached when the navigation happened — so on a direct page load,
        // where the links are (re)built during that very navigation, it does not
        // fire. updateBottomNavHighlight() covers that initial case; the two
        // agree, so they never fight. (sameLocation, not the default
        // locationPrefix, so the root view is not marked active everywhere.)
        link.setHighlightCondition(HighlightConditions.sameLocation());
        fillItem(link, iconFor(item), getMenuText(target, item.getText()));
        bottomNavLinks.put(target, link);
        return link;
    }

    /**
     * A bottom-bar item for a group (a navigation item with children): tapping it
     * opens a {@link Popover} above the bar listing the children, so a two-level
     * hierarchy is reachable without the drawer. The group item is marked active
     * while any of its children is the current view.
     */
    private Component createGroupItem(NavigationItem group, List<NavigationItem> children) {
        Div item = new Div();
        item.addClassName("mobile-bottom-nav-item");
        item.getElement().setAttribute("role", "button");
        item.getElement().setAttribute("tabindex", "0");
        fillItem(item, iconFor(group), getMenuText(group.getNavigationTarget(), group.getText()));

        Popover popover = new Popover();
        popover.setTarget(item);
        popover.setOpenOnClick(true);
        popover.setPosition(PopoverPosition.TOP); // open upward from the bottom bar
        popover.addThemeVariants(PopoverVariant.ARROW); // point back at the tapped item
        popover.addClassName("mobile-bottom-nav-popover");

        Set<Class<?>> childTargets = new HashSet<>();
        Div list = new Div();
        list.addClassName("mobile-bottom-nav-popover-list");
        children.stream()
                .filter(child -> child instanceof BasicNavigationItem && child.isEnabled())
                .forEach(child -> {
                    @SuppressWarnings("unchecked")
                    Class<? extends Component> target =
                            (Class<? extends Component>) child.getNavigationTarget();
                    childTargets.add(target);
                    RouterLink link = new RouterLink();
                    link.addClassName("mobile-bottom-nav-popover-item");
                    link.setRoute(target);
                    link.setHighlightCondition(HighlightConditions.sameLocation());
                    link.add(iconFor(child));
                    link.add(new Span(getMenuText(target, child.getText())));
                    // Close the popover once a child is chosen.
                    link.getElement().addEventListener("click", e -> popover.close());
                    list.add(link);
                });
        popover.add(list);
        item.add(popover);

        bottomNavGroups.put(item, childTargets);
        return item;
    }

    private Component createMoreItem() {
        // A clickable div rather than a Button, so it shares the icon-over-label
        // layout of the navigation links exactly.
        Div more = new Div();
        more.addClassName("mobile-bottom-nav-item");
        more.getElement().setAttribute("role", "button");
        more.getElement().setAttribute("tabindex", "0");
        more.addClickListener(e -> openOverflow());
        more.getElement().addEventListener("keydown", e -> openOverflow())
                .setFilter("event.key === 'Enter' || event.key === ' '");
        fillItem(more, VaadinIcon.ELLIPSIS_DOTS_H.create(), getOverflowText());
        return more;
    }

    /**
     * Fills a bottom-bar item with its icon and, when
     * {@link #isBottomNavLabelsVisible() labels are enabled}, a small caption
     * below it. The label is always exposed as tooltip and accessible name so
     * the icon-only mode stays usable too.
     */
    private void fillItem(HasComponents item, Component icon, String label) {
        item.add(icon);
        if (bottomNavLabelsVisible) {
            Span caption = new Span(label);
            caption.addClassName("mobile-bottom-nav-label");
            item.add(caption);
        }
        Component itemComponent = (Component) item;
        itemComponent.getElement().setAttribute("title", label);
        itemComponent.getElement().setAttribute("aria-label", label);
    }

    /** A fresh icon component for an item, from its {@link MenuItem}/{@link Menu}. */
    private Component iconFor(NavigationItem item) {
        Class<?> target = item.getNavigationTarget();
        MenuItem menuItem = NavigationItem.getAnnotationFromType(target, MenuItem.class);
        if (menuItem != null) {
            return BasicNavigationItem.createIcon(menuItem);
        }
        Menu menu = NavigationItem.getAnnotationFromType(target, Menu.class);
        if (menu != null && menu.icon() != null && !menu.icon().isEmpty()) {
            String icon = menu.icon();
            return icon.endsWith(".svg") ? new SvgIcon(icon) : new Icon(icon);
        }
        return VaadinIcon.FILE.create();
    }

    /**
     * Wraps the view with the large, scroll-away page title above it. The title
     * lives inside the scrollable content area, so it slides out of view as the
     * user scrolls — the way native mobile apps show a screen title.
     */
    @Override
    protected void showContent(Component content) {
        // Track the real (unwrapped) view so the bottom bar can mark it active;
        // getContent() would now return the wrapper.
        currentViewClass = content == null ? null : content.getClass();
        headerHelpers.removeAll();
        contentWrapper.removeAll();
        contentWrapper.add(header, content);
        // If the view asked to fill (setSizeFull / 100% height), give the content
        // column a definite height and let the view take the space left by the
        // header — otherwise a "100% height" view would collapse in the
        // (auto-height) body-scrolling layout. Other views keep normal scrolling.
        boolean fill = content != null
                && "100%".equals(content.getElement().getStyle().get("height"));
        contentWrapper.getClassNames().set("mobile-content--fill", fill);
        super.showContent(contentWrapper);
        // Highlight here, where currentViewClass was just set, so it is correct
        // regardless of whether showContent runs before or after afterNavigation
        // (their order is not guaranteed). Doing it only from afterNavigation made
        // the highlight depend on that ordering: if afterNavigation ran first, it
        // saw a stale currentViewClass and cleared the just-set highlight, which
        // showed up as the active item losing its background when navigating back
        // to a view (e.g. profile) on some browsers.
        updateBottomNavHighlight();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        super.afterNavigation(event);
        // Belt-and-braces: re-assert with the now-final navigation state. By this
        // point showContent has run, so currentViewClass is fresh and this agrees.
        updateBottomNavHighlight();
    }

    /** Marks the bottom-bar item of the currently shown view as active. */
    private void updateBottomNavHighlight() {
        bottomNavLinks.forEach((target, link) ->
                link.getElement().setAttribute("highlight", target == currentViewClass));
        bottomNavGroups.forEach((item, childTargets) ->
                item.getElement().setAttribute("highlight", childTargets.contains(currentViewClass)));
    }

    @Override
    public void setViewTitle(String title) {
        // Keep the (now hidden) navbar title in sync for any code reading it,
        // and drive the visible in-content title.
        super.setViewTitle(title);
        pageTitle.setText(title);
    }

    /**
     * Opens the navigation that does not fit in the bottom bar. The default
     * opens the full drawer (shown as an overlay on a phone). Override to use a
     * different presentation, e.g. a popover.
     */
    protected void openOverflow() {
        setDrawerOpened(true);
    }

    /** The label/tooltip of the "More" action. Override to localize. */
    protected String getOverflowText() {
        return "More";
    }

    /**
     * The maximum number of icons shown in the bottom bar. When more top-level
     * views exist, the last slot is used for the {@link #openOverflow() "More"}
     * action. Defaults to {@link #DEFAULT_MAX_BOTTOM_NAV_ITEMS}.
     */
    public int getMaxBottomNavItems() {
        return maxBottomNavItems;
    }

    public void setMaxBottomNavItems(int maxBottomNavItems) {
        if (maxBottomNavItems < 1) {
            throw new IllegalArgumentException("Need room for at least one item");
        }
        this.maxBottomNavItems = maxBottomNavItems;
        if (bottomNavInitialized) {
            buildMenu();
        }
    }

    /**
     * Whether the bottom bar shows a small text caption under each icon.
     * Defaults to {@code false} (icon-only; the label is still available as a
     * tooltip and accessible name).
     */
    public boolean isBottomNavLabelsVisible() {
        return bottomNavLabelsVisible;
    }

    /**
     * Sets whether a small text caption is shown under each bottom-bar icon
     * (as in many native mobile tab bars) and rebuilds the bar.
     */
    public void setBottomNavLabelsVisible(boolean bottomNavLabelsVisible) {
        this.bottomNavLabelsVisible = bottomNavLabelsVisible;
        if (bottomNavInitialized) {
            buildMenu();
        }
    }

    /**
     * Whether the bottom bar hides itself when the content is scrolled down and
     * reappears on scroll up (iOS-style). Defaults to {@code true}.
     */
    public boolean isBottomNavHideOnScroll() {
        return bottomNavHideOnScroll;
    }

    public void setBottomNavHideOnScroll(boolean bottomNavHideOnScroll) {
        this.bottomNavHideOnScroll = bottomNavHideOnScroll;
        getElement().setAttribute("nav-autohide", bottomNavHideOnScroll);
        if (!bottomNavHideOnScroll) {
            // Make sure a currently-hidden bar comes back when the feature is off.
            getElement().executeJs(
                    "const b = this.querySelector('.mobile-bottom-nav');"
                            + "if (b) b.classList.remove('mobile-bottom-nav--hidden');");
        }
    }

    /**
     * Whether the large in-content view title is shown. Defaults to
     * {@code false}.
     */
    public boolean isViewTitleVisible() {
        return viewTitleVisible;
    }

    /**
     * Sets whether the large, scroll-away view title is rendered into the content
     * area in phone-like mode (when the bottom bar is shown). Turn it off for
     * applications whose views already render their own heading, so the title is
     * not duplicated: the whole content header then collapses, leaving no extra
     * band. A wide desktop window always shows the title (next to the drawer
     * toggle) regardless of this setting — there the header is the only place a
     * view title appears, so hiding it would leave a bare, lonely toggle.
     */
    public void setViewTitleVisible(boolean viewTitleVisible) {
        this.viewTitleVisible = viewTitleVisible;
        header.getClassNames().set("no-view-title", !viewTitleVisible);
    }

    /**
     * Whether the whole page scrolls (instead of just the content area).
     * Defaults to {@code true}.
     */
    public boolean isBodyScrolling() {
        return bodyScrolling;
    }

    /**
     * Switches to body/page scrolling: the entire page scrolls rather than the
     * content area alone. This lets the mobile browser's address bar collapse on
     * scroll and, when the application's viewport uses {@code viewport-fit=cover},
     * lets the content scroll edge-to-edge <em>under</em> the status bar / dynamic
     * island (the layout already insets its header and bottom bar by the safe
     * areas). The viewport itself is left to the application's configuration.
     * <p>
     * It works by undoing Flow's default {@code height: 100%} on
     * {@code html, body, #outlet} (Flow's index.html sets it, which forces
     * content-area scrolling). This is an app-wide change, so it is opt-in:
     * notably, components that rely on {@code height: 100%} (e.g. a full-height
     * {@link com.vaadin.flow.component.grid.Grid}) then need an explicit height
     * of their own. Best set once, before the layout is attached.
     */
    public void setBodyScrolling(boolean bodyScrolling) {
        this.bodyScrolling = bodyScrolling;
        getElement().setAttribute("body-scroll", bodyScrolling);
        getElement().executeJs(bodyScrolling ? BODY_SCROLL_ON_JS : BODY_SCROLL_OFF_JS);
        // Re-point the scroll watcher at the new scroll source.
        getElement().executeJs(AUTO_HIDE_JS);
    }
}
