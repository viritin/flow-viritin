package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.router.Menu;
import org.vaadin.firitin.util.style.VaadinCssProps;

@StyleSheet("context://assets/org/vaadin/firitin/components/app-side-nav.css")
public class SubMenu extends AdvancedSideNav implements NavigationItem {

    private final Class<?> navigationTarget;
    private NavigationItem parentItem;

    public SubMenu(Class<?> navigationTarget) {
        this.navigationTarget = navigationTarget;
        var text = NavigationItem.getMenuTextFromClass(navigationTarget);
        setLabel(text);
        MenuItem me = navigationTarget.getAnnotation(MenuItem.class);
        if (me != null) {
            if(me.icon() != null) {
                // TODO support icons !?
            }
            if(!me.enabled()) {
                throw new IllegalArgumentException("SubMenu don't yet support disabling");
            }
            setCollapsible(me.collapsible());
            setExpanded(me.openByDefault());
        } else if(navigationTarget.isAnnotationPresent(Menu.class)) {
            Menu menu = navigationTarget.getAnnotation(Menu.class);
            if(menu.icon() != null) {
                throw new IllegalArgumentException("SubMenu don't yet support an icon");
            }
        }
    }

    @Override
    public String getText() {
        return getLabel();
    }

    @Override
    public Class<?> getNavigationTarget() {
        return navigationTarget;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setActive(boolean active) {
        // These can't really be active, TODO refactor
    }

    @Override
    public void addSubItem(NavigationItem item) {
        if(item instanceof SubMenu) {
            addSubMenu((SubMenu) item);
        } else {
            addItem((BasicNavigationItem) item);
            item.getStyle().setPaddingLeft(VaadinCssProps.GAP_L.var());
        }
    }

    @Override
    public void setParentItem(NavigationItem parent) {
        parentItem = parent;

    }

    @Override
    public NavigationItem getParentItem() {
        return parentItem;
    }
}
