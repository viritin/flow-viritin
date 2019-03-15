package org.vaadin.firitin.components.orderedlayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentFlexComponent;
import org.vaadin.firitin.fluency.ui.FluentHasComponents;
import org.vaadin.firitin.fluency.ui.FluentThemableLayout;

public class VVerticalLayout extends VerticalLayout implements FluentThemableLayout<VVerticalLayout>,
        FluentComponent<VVerticalLayout>, FluentFlexComponent<VVerticalLayout>, FluentHasComponents<VVerticalLayout> {
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

    public VVerticalLayout withDefaultHorizontalComponentAlignment(Alignment alignment) {
        setDefaultHorizontalComponentAlignment(alignment);
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

}
