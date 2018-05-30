package org.vaadin.firitin.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.firitin.fluency.ui.*;

public class VHorizontalLayout extends HorizontalLayout
        implements FluentThemableLayout<VHorizontalLayout>, FluentFlexComponent<VHorizontalLayout>
{
    public VHorizontalLayout() {
        super();
    }

    public VHorizontalLayout(Component... children) {
        super(children);
    }
}
