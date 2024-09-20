package org.vaadin.firitin.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;

public class HorizontalFloatLayout extends VHorizontalLayout {
    public HorizontalFloatLayout() {
        setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        addClassName(LumoUtility.FlexWrap.WRAP);
    }

    public HorizontalFloatLayout(Component... components) {
        this();
        add(components);
    }
}
