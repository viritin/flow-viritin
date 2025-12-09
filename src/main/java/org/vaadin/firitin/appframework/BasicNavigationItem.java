package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Menu;

/**
 * A component to represent a main view in the navigation menu
 */
public class BasicNavigationItem extends SideNavItem implements NavigationItem {
    private final Class<? extends Component> navigationTarget;
    private final String text;
    private String path;
    private boolean disabled = false;
    private NavigationItem parentItem;

    public BasicNavigationItem(Class<? extends Component> navigationTarget) {
        super(null, navigationTarget);
        getStyle().setDisplay(Style.Display.BLOCK); // TODO WTF?
        text = NavigationItem.getMenuTextFromClass(navigationTarget);
        setLabel(text);
        MenuItem me = navigationTarget.getAnnotation(MenuItem.class);
        if (me != null) {
            setPrefixComponent(createIcon(me));

            if(me.hidden()) {
                setVisible(false);
            }
            if(!me.enabled()) {
                setEnabled(false);
            }
            if(me.openByDefault()) {
                setExpanded(true);
            }
        } else if(navigationTarget.isAnnotationPresent(Menu.class)) {
            Menu menu = navigationTarget.getAnnotation(Menu.class);
            if(menu.icon() != null) {
                String icon = menu.icon();
                if(icon.endsWith(".svg")) {
                    setPrefixComponent(new SvgIcon(icon));
                } else {
                    setPrefixComponent(new Icon(icon));
                }
            }
        }
        this.navigationTarget = navigationTarget;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Class<? extends Component> getNavigationTarget() {
        return navigationTarget;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
        if(!disabled) {
            super.setPath(path);
        }
    }

    public void setEnabled(boolean enabled) {
        this.disabled = !enabled;
        if(disabled) {
            super.setPath((String) null);
        } else if (path != null) {
            super.setPath(path);
        }
        String color = enabled ? "" : "gray";
        getStyle().setColor(color);
    }

    @Override
    public boolean isEnabled() {
        return !disabled;
    }

    @Override
    public void setActive(boolean active) {
        if (active) {
            getElement().setAttribute("active", true);
        } else {
            getElement().removeAttribute("active");
        }
    }

    @Override
    public void addSubItem(NavigationItem item) {
        if(item instanceof BasicNavigationItem bi) {
            addItem(bi);
        } else {
            addSubMenu((SubMenu) item);
        }
    }

    private void addSubMenu(SubMenu item) {
        getElement().appendChild(item.getElement());
    }

    @Override
    public void setParentItem(NavigationItem parent) {
        this.parentItem = parent;
    }

    @Override
    public NavigationItem getParentItem() {
        return parentItem;
    }

    public static Component createIcon(MenuItem menuItem) {
        String iconUrl = menuItem.iconUrl();
        if(iconUrl.endsWith(".svg")) {
            return new SvgIcon(iconUrl);
        } else if(!iconUrl.isEmpty()) {
            return new Icon(iconUrl);
        } else {
            return menuItem.icon().create();
        }
    }

}
