package org.vaadin.firitin.components.tabs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasComponents;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;
import org.vaadin.firitin.fluency.ui.FluentHasTheme;

public class VTab extends Tab implements FluentComponent<VTab>, FluentHasStyle<VTab>, FluentHasComponents<VTab>, FluentHasTheme<VTab> {

    public VTab() {
        super();
    }

    public VTab(String label) {
        super(label);
    }

    public VTab(Component... components) {
        super(components);
    }

    public VTab withLabel(String label) {
        setLabel(label);
        return this;
    }

    public VTab withFlexGrow(double flexGrow) {
        setFlexGrow(flexGrow);
        return this;
    }

    public VTab withThemeVariants(TabVariant... variants) {
        addThemeVariants(variants);
        return this;
    }

    public VTab withSelected(boolean selected) {
        setSelected(selected);
        return this;
    }
}
