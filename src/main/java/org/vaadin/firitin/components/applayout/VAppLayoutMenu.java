package org.vaadin.firitin.components.applayout;

import com.vaadin.flow.component.applayout.AppLayoutMenu;
import com.vaadin.flow.component.applayout.AppLayoutMenuItem;
import org.vaadin.firitin.fluency.ui.FluentAttachNotifier;

public class VAppLayoutMenu extends AppLayoutMenu implements FluentAttachNotifier<VAppLayoutMenu> {

    public VAppLayoutMenu withMenuItems(AppLayoutMenuItem... menuItems) {
        addMenuItems(menuItems);
        return this;
    }

    public VAppLayoutMenu withSelectMenuItem(AppLayoutMenuItem menuItem) {
        setMenuItems(menuItem);
        return this;
    }

}
