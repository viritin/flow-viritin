package org.vaadin.firitin.components.applayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.applayout.AppLayoutMenuItem;
import com.vaadin.flow.component.applayout.MenuItemClickEvent;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasComponents;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

public class VAppLayoutMenuItem extends AppLayoutMenuItem implements FluentComponent<VAppLayoutMenuItem>, FluentHasStyle<VAppLayoutMenuItem>, FluentHasComponents<VAppLayoutMenuItem> {

    public VAppLayoutMenuItem(String title) {
        super(title);
    }

    public VAppLayoutMenuItem(Component icon) {
        super(icon);
    }

    public VAppLayoutMenuItem(Component icon, String title) {
        super(icon, title);
    }

    public VAppLayoutMenuItem(String title, String route) {
        super(title, route);
    }

    public VAppLayoutMenuItem(Component icon, String title, String route) {
        super(icon, title, route);
    }

    public VAppLayoutMenuItem(Component icon, ComponentEventListener<MenuItemClickEvent> listener) {
        super(icon, listener);
    }

    public VAppLayoutMenuItem(String title, ComponentEventListener<MenuItemClickEvent> listener) {
        super(title, listener);
    }

    public VAppLayoutMenuItem(Component icon, String title, ComponentEventListener<MenuItemClickEvent> listener) {
        super(icon, title, listener);
    }

    public VAppLayoutMenuItem withIcon(Component icon) {
        setIcon(icon);
        return this;
    }

    public VAppLayoutMenuItem withTitle(String title) {
        setTitle(title);
        return this;
    }

    public VAppLayoutMenuItem withRoute(String route) {
        setRoute(route);
        return this;
    }

    public VAppLayoutMenuItem withMenuItemClickListener(ComponentEventListener<MenuItemClickEvent> listener) {
        addMenuItemClickListener(listener);
        return this;
    }

}
