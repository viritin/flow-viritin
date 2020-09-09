package org.vaadin.firitin.components.tabs;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasComponents;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

public class VTabs extends Tabs implements FluentComponent<VTab>, FluentHasStyle<VTab>, FluentHasComponents<VTab> {

    public VTabs() {
        super();
    }

    public VTabs(Tab... tabs) {
        super(tabs);
    }

    public VTabs withTab(Tab tab) {
        add(tab);
        return this;
    }

    public VTabs withSelectedTab(Tab selectedTab) {
        setSelectedTab(selectedTab);
        return this;
    }

    public VTabs withOrientation(Orientation orientation) {
        setOrientation(orientation);
        return this;
    }

    public VTabs withFlexGrowForEnclosedTabs(double flexGrow) {
        setFlexGrowForEnclosedTabs(flexGrow);
        return this;
    }

    public VTabs withThemeVariants(TabsVariant... variants) {
        addThemeVariants(variants);
        return this;
    }

    public VTabs withSelectedIndex(int selectedIndex) {
        setSelectedIndex(selectedIndex);
        return this;
    }

    public VTabs withAutoselect(boolean autoselect) {
        setAutoselect(autoselect);
        return this;
    }

    public VTabs withSelectedChangeListener(ComponentEventListener<SelectedChangeEvent> listener) {
        addSelectedChangeListener(listener);
        return this;
    }

}
