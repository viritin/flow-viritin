package org.vaadin.firitin.components.orderedlayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.firitin.fluency.ui.*;

public class VVerticalLayout extends VerticalLayout implements FluentThemableLayout<VVerticalLayout>,
        FluentComponent<VVerticalLayout>, FluentHasStyle<VVerticalLayout>, FluentHasSize<VVerticalLayout>, FluentHasComponents<VVerticalLayout>, FluentClickNotifier<VerticalLayout, VVerticalLayout>, FluentFlexComponent<VerticalLayout, VVerticalLayout> {
    public VVerticalLayout() {
        super();
    }

    public VVerticalLayout(Component... children) {
        super(children);
    }

    public VVerticalLayout withPadding(boolean padding) {
        setPadding(padding);
        return this;
    }

    public VVerticalLayout alignAll(Alignment alignment) {
        return withDefaultHorizontalComponentAlignment(alignment);
    }

    public VVerticalLayout withDefaultHorizontalComponentAlignment(Alignment alignment) {
        setDefaultHorizontalComponentAlignment(alignment);
        return this;
    }

    public VVerticalLayout withJustifyContentMode(final JustifyContentMode justifyContentMode) {
        setJustifyContentMode(justifyContentMode);
        return this;
    }

    public VVerticalLayout withComponent(Component component) {
        add(component);
        return this;
    }

    public VVerticalLayout withComponent(Component component, Alignment alignment) {
        add(component);
        setHorizontalComponentAlignment(alignment, component);
        return this;
    }

    @Deprecated
    public VVerticalLayout addExpanded(Component... components) {
        return withExpanded(components);
    }

    public VVerticalLayout withExpanded(Component... components) {
        add(components);
        setFlexGrow(1, components);
        if (getHeight() == null) {
            setHeight("100%");
        }
        return this;
    }

}
