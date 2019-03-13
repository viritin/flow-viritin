package org.vaadin.firitin.components.orderedlayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import org.vaadin.firitin.fluency.ui.FluentComponent;

public class VFlexLayout extends FlexLayout implements FluentComponent<VFlexLayout> {

    public VFlexLayout() {
        super();
    }

    public VFlexLayout(Component... children) {
        super(children);
    }

    public VFlexLayout withJustifyContentMode(final JustifyContentMode justifyContentMode) {
        setJustifyContentMode(justifyContentMode);
        return this;
    }
}
