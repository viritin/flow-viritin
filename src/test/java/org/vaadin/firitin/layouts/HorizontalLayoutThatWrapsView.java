package org.vaadin.firitin.layouts;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class HorizontalLayoutThatWrapsView extends VerticalLayout {
    public HorizontalLayoutThatWrapsView() {
        var layout = new HorizontalLayoutThatWraps();
        for (int i = 0; i < 15; i++) {
            layout.add(new Button("Hello there " + i));
        }
        add(layout);
    }

}
