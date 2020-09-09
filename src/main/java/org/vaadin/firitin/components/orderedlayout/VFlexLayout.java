package org.vaadin.firitin.components.orderedlayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import org.vaadin.firitin.fluency.ui.*;

public class VFlexLayout extends FlexLayout implements FluentComponent<VFlexLayout>,
        FluentHasStyle<VFlexLayout>, FluentHasSize<VFlexLayout>, FluentHasComponents<VFlexLayout>,
        FluentClickNotifier<FlexLayout, VFlexLayout> {

    public VFlexLayout() {
        super();
    }

    public VFlexLayout(Component... children) {
        super(children);
    }

    public VFlexLayout withWrapMode(FlexLayout.WrapMode wrapMode) {
        setWrapMode(wrapMode);
        return this;
    }

    public VFlexLayout withAlignItems(FlexComponent.Alignment alignment) {
        setAlignItems(alignment);
        return this;
    }

    public VFlexLayout withAlignSelf(FlexComponent.Alignment alignment, HasElement... elementContainers) {
        setAlignSelf(alignment, elementContainers);
        return this;
    }

    public VFlexLayout withFlexGrow(double flexGrow, HasElement... elementContainers) {
        setFlexGrow(flexGrow, elementContainers);
        return this;
    }

    public VFlexLayout withJustifyContentMode(FlexComponent.JustifyContentMode justifyContentMode) {
        setJustifyContentMode(justifyContentMode);
        return this;
    }

    public VFlexLayout withExpand(Component... componentsToExpand) {
        expand(componentsToExpand);
        return this;
    }
}
