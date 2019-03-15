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
    
    public VHorizontalLayout alignAll(Alignment alignment) {
        setDefaultVerticalComponentAlignment(alignment);
        return this;
    }
}
