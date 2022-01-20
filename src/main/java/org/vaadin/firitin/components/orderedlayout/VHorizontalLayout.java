package org.vaadin.firitin.components.orderedlayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.firitin.components.select.VSelect;
import org.vaadin.firitin.fluency.ui.*;

public class VHorizontalLayout extends HorizontalLayout implements FluentThemableLayout<VHorizontalLayout>,
        FluentComponent<VHorizontalLayout>, FluentHasStyle<VHorizontalLayout>, FluentHasSize<VHorizontalLayout>, FluentHasComponents<VHorizontalLayout>, FluentClickNotifier<HorizontalLayout, VHorizontalLayout> {
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


    public VHorizontalLayout withAlignItems(FlexComponent.Alignment alignment) {
        setAlignItems(alignment);
        return this;
    }

    public VHorizontalLayout withAlignSelf(FlexComponent.Alignment alignment, HasElement... elementContainers) {
        setAlignSelf(alignment, elementContainers);
        return this;
    }

    public VHorizontalLayout withFlexGrow(double flexGrow, HasElement... elementContainers) {
        setFlexGrow(flexGrow, elementContainers);
        return this;
    }

    public VHorizontalLayout withExpand(Component... componentsToExpand) {
        expand(componentsToExpand);
        return this;
    }

    /**
     * Adds a spacer component that consumes all available space. Handy for example to add
     * components to both ends of a row, but leave space between them.
     * @return self with the spacer
     */
    public VHorizontalLayout space() {
        addAndExpand(new Div());
        return this;
    }
}
