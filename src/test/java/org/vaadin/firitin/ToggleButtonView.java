package org.vaadin.firitin;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.checkbox.ToggleButton;

@Route
public class ToggleButtonView extends VerticalLayout {

    public ToggleButtonView() {
        add(new ToggleButton("Toggle me"));
    }
}
