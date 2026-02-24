package org.vaadin.firitin.layouts;

import com.vaadin.flow.component.Component;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;

public class HorizontalFloatLayout extends VHorizontalLayout {
    public HorizontalFloatLayout() {
        setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        setWrap(true);
    }

    public HorizontalFloatLayout(Component... components) {
        this();
        add(components);
    }
}
