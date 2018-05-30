package org.vaadin.firitin.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.firitin.fluency.ui.FluentFlexComponent;
import org.vaadin.firitin.fluency.ui.FluentHasComponents;
import org.vaadin.firitin.fluency.ui.FluentThemableLayout;

public class VVerticalLayout extends VerticalLayout
        implements FluentThemableLayout<VVerticalLayout>, FluentFlexComponent<VVerticalLayout>,
        FluentHasComponents<VVerticalLayout>
{
    public VVerticalLayout() {
        super();
    }

    public VVerticalLayout(Component... children) {
        super(children);
    }
}
