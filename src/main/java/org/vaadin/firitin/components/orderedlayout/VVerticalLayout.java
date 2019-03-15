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
}
