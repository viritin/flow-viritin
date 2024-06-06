package org.vaadin.firitin.layouts;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class HorizontalLayoutThatWraps extends FlexLayout {
    public HorizontalLayoutThatWraps() {
        setFlexDirection(FlexDirection.ROW);
        setFlexWrap(FlexWrap.WRAP);
        addClassName(LumoUtility.Gap.MEDIUM); // spacing, same as getStyle().set("gap", "1em");
    }
}
