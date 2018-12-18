package org.vaadin.firitin.layouts;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.vaadin.firitin.util.VStyleUtil;
import org.vaadin.firitin.util.VStyleUtil.FlexDirection;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tabs.Orientation;
import com.vaadin.flow.component.tabs.Tabs.SelectedChangeEvent;
import com.vaadin.flow.shared.Registration;

/**
 * 
 * A TabSheet component that behaves in the same way as the familiar Vaadin 7,8.
 * 
 * It provides a layer on top of {@link Tabs} which handles component change on {@link Tab} clicks automatically.
 * 
 * @author mmerruko
 *
 */
public class VTabSheet extends Composite<FlexLayout> {
    private Tabs tabs = new Tabs();

    private Map<Tab, Component> components = new LinkedHashMap<>();

    private FlexLayout content = new FlexLayout();

    public VTabSheet() {
        getContent().add(tabs);
        getContent().add(content);
        getContent().setWidth("100%");

        VStyleUtil.setFlexDirection(getContent(), FlexDirection.COLUMN);
        VStyleUtil.setFlexShrink(0, tabs);
        getContent().setFlexGrow(1, content);

        tabs.addSelectedChangeListener(this::onTabChanged);
    }

    public Registration addSelectedChangeListener(ComponentEventListener<SelectedChangeEvent> listener) {
        return tabs.addSelectedChangeListener(listener);
    }

    public int getSelectedIndex() {
        return tabs.getSelectedIndex();
    }

    public void setSelectedIndex(int selectedIndex) {
        tabs.setSelectedIndex(selectedIndex);
    }

    public Tab getSelectedTab() {
        return tabs.getSelectedTab();
    }

    public void setSelectedTab(Tab selectedTab) {
        tabs.setSelectedTab(selectedTab);
    }

    public Tab addTab(String caption, Component component) {
        Tab tab = new Tab(caption);
        tabs.add(tab);

        if (components.isEmpty()) {
            content.add(component);
        }
        components.put(tab, component);

        return tab;
    }

    public void removeTab(Tab tab) {
        if (tabs.getSelectedTab() == tab) {
            int index = tabs.getSelectedIndex();
            if (index - 1 >= 0) {
                tabs.setSelectedIndex(index - 1);
            } else {
                tabs.setSelectedIndex(0);
            }
        }
        tabs.remove(tab);
        Optional.ofNullable(components.get(tab)).ifPresent(content::remove);
    }

    public Orientation getOrientation() {
        return tabs.getOrientation();
    }

    public void setOrientation(Orientation orientation) {
        tabs.setOrientation(orientation);
        if (orientation == Orientation.HORIZONTAL) {
            VStyleUtil.setFlexDirection(getContent(), FlexDirection.COLUMN);
        } else {
            VStyleUtil.setFlexDirection(getContent(), FlexDirection.ROW);
        }
    }

    public void setFlexGrowForEnclosedTabs(double flexGrow) {
        tabs.setFlexGrowForEnclosedTabs(flexGrow);
    }

    private void onTabChanged(SelectedChangeEvent e) {
        Tab selectedTab = e.getSource().getSelectedTab();
        content.removeAll();

        Optional.ofNullable(components.get(selectedTab)).ifPresent(content::add);
    }
}
