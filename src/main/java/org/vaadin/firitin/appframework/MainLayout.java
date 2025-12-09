package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.RouteBaseData;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.RouteRegistry;
import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main view is a top-level placeholder for other views. This version is
 * based on a one produced by start.vaadin.com service, but add a bit structure
 * to it and populates main views automatically to it.
 * <p>
 * Suites as such for small apps that has no special needs for the main layout.
 * Menu items can be configured (override caption, order, icon) using
 * {@link MenuItem} annotation</p>
 * <p>
 * Check usage example from the text package org.vaadin.firitin.appframework</p>
 */
public abstract class MainLayout extends AppLayout implements AfterNavigationObserver {

    public static class AdvancedSideNav extends SideNav {

        public void addSubMenu(AdvancedSideNav subMenu) {
            // This seems to work, although probably not supported really
            getElement().appendChild(subMenu.getElement());
        }

        public void addNavigationItem(NavigationItem item) {
            if (item instanceof SubMenu) {
                addSubMenu((SubMenu) item);
            } else {
                addItem((SideNavItem) item);
            }
        }
    }



    private H2 viewTitle;
    private AdvancedSideNav menu;
    //private List<NavigationItem> navigationItems = new ArrayList<>();
    private Map<Class<?>, NavigationItem> targetToItem = new HashMap<>();
    private Stack<Component> viewStack = new Stack<>();
    private Map<Component, String> explicitViewTitles = new WeakHashMap<>();

    public MainLayout() {
//		getElement().getClassList().add("v-applayout");
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1(getDrawerHeader());
        appName.getStyle().setMargin("var(--lumo-space-m)");
        appName.getStyle().set("font-size", "var(--lumo-font-size-l)");
        Header header = new Header(appName);

        Scroller scroller = new Scroller(prepareNav());
        scroller.addClassNames(LumoUtility.Padding.SMALL);

        addToDrawer(header, scroller, prepareFooter());
    }

    protected AdvancedSideNav prepareNav() {
        // SideNav is a production-ready official component under a feature flag.
        // However, it has accessibility issues and is missing some features.
        // Both will be addressed in an upcoming minor version.
        // These changes are likely to cause some breaking change to the custom css
        // applied to the component.
        AdvancedSideNav nav = new AdvancedSideNav();
        this.menu = nav;
        return nav;
    }

    public SideNav getMenu() {
        return menu;
    }

    protected Footer prepareFooter() {
        Footer layout = new Footer();
        return layout;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (targetToItem.isEmpty()) {
            init();
        }
        super.onAttach(attachEvent);
    }

    protected void init() {
        RouteConfiguration routeConfiguration = RouteConfiguration.forSessionScope();
        routeConfiguration.getAvailableRoutes().stream().filter(routeData -> {
            Class<? extends RouterLayout> parentLayout = routeData.getParentLayout();
            if(parentLayout == null) {
                // Try to find from route registry (the @Layout annotation way)
                parentLayout = routeConfiguration.getHandledRegistry().getLayout(routeConfiguration.getUrl(routeData.getNavigationTarget()));
            }

            if (parentLayout != null) {
                boolean assignableFrom = MainLayout.class.isAssignableFrom(parentLayout);
                return assignableFrom;
            }
            return false;
        }).forEach(rd -> {
            Class<? extends Component> routeClass = rd.getNavigationTarget();
            if (!Modifier.isAbstract(routeClass.getModifiers())) {
                addNavigationItem(new BasicNavigationItem(routeClass));
            }
        });

        // Add and remove dynamically added routes
        RouteConfiguration.forApplicationScope().addRoutesChangeListener(event -> {
            try {

                event.getRemovedRoutes().forEach(route -> {
                    targetToItem.remove(route.getNavigationTarget());
                });
                // UI access used to support reload by JRebel etc
                MainLayout.this.getUI().ifPresent(ui -> {
                    if (ui.isClosing()) {
                        // Route reload caused most likely by JRebel reload
                        // and might be on a closing UI (because Vaadin dev
                        // mode reloads automatically these days). Ignore
                        return;
                    }
                    ui.access(() -> {
                        List<RouteBaseData<?>> addedRoutes = event.getAddedRoutes();
                        addedRoutes.stream().filter(routeData -> {
                            Class<? extends RouterLayout> parentLayout = routeData.getParentLayout();
                            if (parentLayout != null) {
                                boolean assignableFrom = MainLayout.class.isAssignableFrom(parentLayout);
                                return assignableFrom;
                            }
                            return false;
                        }).forEach(rd -> {
                            Class<? extends Component> routeClass = rd.getNavigationTarget();
                            if (!Modifier.isAbstract(routeClass.getModifiers()) && routeClass != null) {
                                addNavigationItem(new BasicNavigationItem(routeClass));
                            }
                        });
                        buildMenu();
                    });
                });
            } catch (Exception e) {
                // caching and logging, with new dev mode, happens autoreload & jrebel
                // if letting forwared -> all routes dissappear...
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error updating routes, happens with latest Vaadin versions & JRebel sometimes", e);
            }

        });

        buildMenu();
    }

    private void addNavigationItem(NavigationItem item) {
        MenuItem annotation = item.getNavigationTarget().getAnnotation(MenuItem.class);
        if(annotation != null && annotation.parent() != MenuItem.NO_PARENT) {
            NavigationItem parentItem = ensureParentItem(annotation.parent());
            item.setParentItem(parentItem);
        }
        targetToItem.put(item.getNavigationTarget(), item);
    }

    private NavigationItem ensureParentItem(Class<?> parent) {
        return targetToItem.computeIfAbsent(parent, p -> {
            if(Component.class.isAssignableFrom(parent)) {
                return new BasicNavigationItem((Class<? extends Component>) parent);
            } else {
                // This is a group item, not a view
                return new SubMenu(parent);
            }
        });
    }

    protected void sortMenuItems(List<NavigationItem> navigationItems) {
        Collections.sort(navigationItems, new Comparator<NavigationItem>() {

            @Override
            public int compare(NavigationItem o1, NavigationItem o2) {
                Double order1 = getOrder1(o1);
                Double order2 = getOrder1(o2);
                double d = order1 - order2;
                if (d == 0) {
                    return o1.getText().compareTo(o2.getText());
                } else {
                    // who on earth got the idea to use double for ordering in the @Menu annotation!?
                    if (d < 0) {
                        return -1;
                    } else if (d > 0) {
                        return 1;
                    } else {
                        return o1.getText().compareTo(o2.getText());
                    }
                }
            }

            private static Double getOrder1(NavigationItem o1) {
                MenuItem a1 = o1.getNavigationTarget().
                        getAnnotation(MenuItem.class);
                Double order1;
                if (a1 != null) {
                    order1 = (double) a1.order();
                } else {
                    Menu av1 = o1.getNavigationTarget().
                            getAnnotation(Menu.class);
                    if (av1 != null) {
                        order1 = av1.order();
                    } else {
                        order1 = (double) MenuItem.DEFAULT;
                    }
                }
                return order1;
            }
        });
    }

    /**
     * @return A List of {@link BasicNavigationItem} objects to be shown in the menu.
     * After modifying these manually, call {@link #buildMenu()} to update the
     * screen.
     */
    public List<NavigationItem> getNavigationItems() {
        if (targetToItem.isEmpty()) {
            init();
        }
        List<NavigationItem> navigationItems = new ArrayList<>(targetToItem.values());
        return navigationItems;
    }

    /**
     * This method can be called to re-build the menu, if e.g. views has been
     * added, removed or otherwise changed.
     * <p>
     * If you have dynamically added/removed views from another thread, wrap the
     * behavior in UI.access method.
     */
    public void buildMenu() {

        List<NavigationItem> navigationItems = new ArrayList<>(targetToItem.values().stream()
                .filter(ni -> ni.getParentItem() == null).toList());

        sortMenuItems(navigationItems);

        menu.removeAll();
        navigationItems.stream().filter(this::checkAccess).forEach(item -> {
            menu.addNavigationItem(item);
            // possible sub-items
            List<NavigationItem> subItems = new ArrayList<>(targetToItem.values().stream().filter(ni -> ni.getParentItem() == item).toList());
            sortMenuItems(subItems);
            subItems.forEach(item::addSubItem);
        });
    }

    /**
     * Application that has access control can limit the appearance of the
     * navigation item in the menu by returning false here.
     *
     * @param navigationItem the navigation item
     * @return true if item should be visible or not
     */
    protected boolean checkAccess(NavigationItem navigationItem) {
        return true;
    }

    protected abstract String getDrawerHeader();

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        updateViewTitle();
        updateSelectedNavigationItem();
    }

    private void updateSelectedNavigationItem() {
        getNavigationItems().forEach(i -> {
            // TODO check if this is still needed, there was some bugs fixed in Vaadin at some point
            i.setActive(i.getNavigationTarget() == getContent().getClass());
        });
    }

    private void updateViewTitle() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < viewStack.size(); i++) {
            if (i > 0) {
                sb.append(" / ");
            }
            Component component = viewStack.get(i);
            if (explicitViewTitles.containsKey(component)) {
                sb.append(explicitViewTitles.get(component));
            } else {
                sb.append(NavigationItem.getMenuTextFromClass(component.getClass()));
            }
        }
        viewTitle.setText(sb.toString());
    }

    @Override
    public void setContent(Component content) {
        while (viewStack.size() > 1) {
            closeSubView();
        }
        super.setContent(content);
        viewStack.clear();
        viewStack.push(content);
    }

    public void openSubView(Component component, String viewTitle) {
        viewStack.push(component);
        if (viewTitle != null) {
            explicitViewTitles.put(component, viewTitle);
        }
        super.setContent(component);
        updateViewTitle();
    }

    public void openSubView(Component component) {
        openSubView(component, null);
    }

    public void closeSubView(Component component) {
        Component pop = viewStack.pop();
        if (pop != component) {
            throw new IllegalStateException();
        }
        if (pop == null) {
            throw new IllegalStateException();
        }
        explicitViewTitles.remove(pop);
        super.setContent(viewStack.peek());
        updateViewTitle();
    }

    public void closeSubView() {
        Component pop = viewStack.pop();
        if (pop == null) {
            throw new IllegalStateException();
        }
        explicitViewTitles.remove(pop);
        super.setContent(viewStack.peek());
        updateViewTitle();
    }

    /**
     * Manually override the current view title.
     *
     * @param title the title to set
     */
    public void setViewTitle(String title) {
        viewTitle.setText(title);
    }

}
