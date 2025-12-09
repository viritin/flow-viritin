package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;

/**
 * A helper class for {@link VAppLayout} and {@link MainLayout}, not mean to designed to be used separately.
 */
@Deprecated()
public class AdvancedSideNav extends SideNav {

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
