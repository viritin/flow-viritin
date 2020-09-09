package org.vaadin.firitin.components.orderedlayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.firitin.fluency.ui.*;

public class VHorizontalLayout extends HorizontalLayout implements FluentThemableLayout<VHorizontalLayout>,
        FluentComponent<VHorizontalLayout>, FluentHasStyle<VHorizontalLayout>, FluentHasSize<VHorizontalLayout>, FluentHasComponents<VHorizontalLayout>, FluentClickNotifier<HorizontalLayout, VHorizontalLayout>,
        FluentFlexComponent<HorizontalLayout, VHorizontalLayout> {
    public VHorizontalLayout() {
        super();
    }

    public VHorizontalLayout(Component... children) {
        super(children);
    }

    public VHorizontalLayout alignAll(Alignment alignment) {
        return withDefaultVerticalComponentAlignment(alignment);
    }

    public VHorizontalLayout withDefaultVerticalComponentAlignment(Alignment alignment) {
        setDefaultVerticalComponentAlignment(alignment);
        return this;
    }

    public VHorizontalLayout withJustifyContentMode(final JustifyContentMode justifyContentMode) {
        setJustifyContentMode(justifyContentMode);
        return this;
    }

    public VHorizontalLayout withSpacing(boolean spacing) {
        setSpacing(spacing);
        return this;
    }

    public VHorizontalLayout withComponent(Component component) {
        add(component);
        return this;
    }

    public VHorizontalLayout withComponent(Component component, Alignment alignment) {
        add(component);
        setVerticalComponentAlignment(alignment, component);
        return this;
    }

    public VHorizontalLayout withExpanded(Component... components) {
        add(components);
        setFlexGrow(1, components);
        if (getWidth() == null) {
            setWidth("100%");
        }
        return this;
    }
}
