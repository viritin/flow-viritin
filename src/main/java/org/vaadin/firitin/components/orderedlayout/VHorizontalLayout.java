package org.vaadin.firitin.components.orderedlayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentFlexComponent;
import org.vaadin.firitin.fluency.ui.FluentHasComponents;
import org.vaadin.firitin.fluency.ui.FluentThemableLayout;

public class VHorizontalLayout extends HorizontalLayout implements FluentThemableLayout<VHorizontalLayout>,
        FluentComponent<VHorizontalLayout>, FluentFlexComponent<VHorizontalLayout>, FluentHasComponents<VHorizontalLayout> {
    public VHorizontalLayout() {
        super();
    }

    public VHorizontalLayout(Component... children) {
        super(children);
    }

    public VHorizontalLayout withDefaultVerticalComponentAlignment(Alignment alignment) {
        setDefaultVerticalComponentAlignment(alignment);
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
}
