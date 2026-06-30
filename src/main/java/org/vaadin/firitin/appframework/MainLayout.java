package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.RouteBaseData;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RoutesChangedEvent;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public abstract class MainLayout extends VAppLayout {
    private Map<Class<?>, NavigationItem> targetToItem = new HashMap<>();

    public MainLayout() {
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
    }

    protected void init() {
        RouteConfiguration routeConfiguration = RouteConfiguration.forSessionScope();
        routeConfiguration.getAvailableRoutes().stream().filter(routeData -> {
            Class<? extends RouterLayout> parentLayout = routeData.getParentLayout();
            if(parentLayout == null) {
                // Try to find from route registry (the @Layout annotation way)
                try {
                    parentLayout = routeConfiguration.getHandledRegistry().getLayout(routeConfiguration.getUrl(routeData.getNavigationTarget()));
                } catch (Exception e) {
                    // TODO log maybe? Navigation target 'foo.bar.View' requires a parameter.
                }
            }

            if (parentLayout != null) {
                boolean assignableFrom = MainLayout.class.isAssignableFrom(parentLayout);
                return assignableFrom;
            }
            return false;
        }).forEach(rd -> {
            Class<? extends Component> routeClass = rd.getNavigationTarget();
            if (!Modifier.isAbstract(routeClass.getModifiers())) {
                try {
                    addNavigationItem(new BasicNavigationItem(routeClass));
                } catch (Exception e) {
                    // TODO handle, HasUrlParameterFormat.checkMandatoryParameter
                }
            }
        });

        // Add and remove dynamically added routes. Listen to both scopes:
        // application scope for class-reload (JRebel/dev mode) and session
        // scope for routes registered at runtime via RouteConfiguration.forSessionScope().
        RouteConfiguration.forApplicationScope().addRoutesChangeListener(this::handleRoutesChange);
        RouteConfiguration.forSessionScope().addRoutesChangeListener(this::handleRoutesChange);

        buildMenu();
    }

    private void handleRoutesChange(RoutesChangedEvent event) {
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
                            try {
                                addNavigationItem(new BasicNavigationItem(routeClass));
                            } catch (Exception e) {
                                // TODO report, can happen e.g. if url parameters not configured
                            }
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

        List<NavigationItem> visibleItems = navigationItems.stream()
                .filter(this::checkAccess).toList();

        getMenu().removeAll();
        visibleItems.forEach(item -> {
            translateItem(item);
            getMenu().addNavigationItem(item);
            // possible sub-items
            List<NavigationItem> subItems = new ArrayList<>(targetToItem.values().stream().filter(ni -> ni.getParentItem() == item).toList());
            sortMenuItems(subItems);
            subItems.forEach(subItem -> {
                translateItem(subItem);
                item.addSubItem(subItem);
            });
        });

        onMenuBuilt(visibleItems);
    }

    /**
     * Called at the end of {@link #buildMenu()} with the visible, access-filtered
     * and ordered top-level navigation items (the same ones just rendered into
     * the drawer {@link #getMenu() SideNav}). The default implementation does
     * nothing.
     * <p>
     * Subclasses can use this to build an <em>additional</em> navigation
     * presentation from the same model, e.g. a mobile bottom navigation bar (see
     * {@link MobileMainLayout}). The items carry their navigation target, icon
     * ({@link MenuItem}/{@link Menu}) and translated label, so the alternative
     * presentation stays in sync with access control, ordering and i18n for
     * free. It is called again whenever the menu is rebuilt (routes added or
     * removed), so rebuild the alternative presentation from scratch here.
     *
     * @param topLevelItems the visible top-level items, in menu order
     */
    protected void onMenuBuilt(List<NavigationItem> topLevelItems) {
    }

    /**
     * The child navigation items of the given (parent/group) item, in menu order.
     * Empty for a leaf item. Useful for an alternative presentation that wants to
     * render two-level hierarchies itself (e.g. {@link MobileMainLayout} opening a
     * group's children in a popover instead of a drawer sub-menu).
     *
     * @param parent the parent item
     * @return its direct children, sorted as in the menu
     */
    protected List<NavigationItem> getChildItems(NavigationItem parent) {
        List<NavigationItem> children = new ArrayList<>(targetToItem.values().stream()
                .filter(ni -> ni.getParentItem() == parent).toList());
        sortMenuItems(children);
        return children;
    }

    private void translateItem(NavigationItem item) {
        String translated = getMenuText(item.getNavigationTarget(), item.getText());
        if (!translated.equals(item.getText())) {
            item.setLabel(translated);
        }
    }

    /**
     * Application that has e.g. role based access control can limit the appearance of the
     * navigation item in the menu by returning false here.
     *
     * @param navigationItem the navigation item
     * @return true if item should be visible or not
     */
    protected boolean checkAccess(NavigationItem navigationItem) {
        return true;
    }


    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        updateSelectedNavigationItem();
        super.afterNavigation(event);
        if (targetToItem.isEmpty()) {
            init();
        }
    }

    private void updateSelectedNavigationItem() {
        getNavigationItems().forEach(i -> {
            // TODO check if this is still needed, there was some bugs fixed in Vaadin at some point
            i.setActive(i.getNavigationTarget() == getContent().getClass());
        });
    }

    /**
     * Assuming an attached component is within o view utilizing MainLayout, this method returns the current MainLayout.
     * In a bigger projects it is most often better to inject the parent layout if needed and use a more specific type.
     *
     * @return the current main layout.
     */
    public static MainLayout getCurrent() {
        UI ui = UI.getCurrent();
        return (MainLayout) ui.getChildren().filter(c -> MainLayout.class.isAssignableFrom(c.getClass())).findFirst().get();
    }

}
