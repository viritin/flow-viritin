package org.vaadin.firitin.layouts;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route
public class HorizontalLayoutThatWrapsView extends VerticalLayout {
    public HorizontalLayoutThatWrapsView() {
        var layout = new HorizontalLayoutThatWraps();
        for (int i = 0; i < 15; i++) {
            layout.add(new Button("Hello there " + i));
        }
        add(layout);

        add(new H2("This wraps as well and aligns baseline"));

        var row = new HorizontalFloatLayout();
        row.add(new Button("Hello there"));
        row.add(new TextField("Text here"));
        row.add(new Button("Hello there"));
        row.add(new TextField("Text here"));
        row.add(new Button("Hello there"));
        row.add(new TextField("Text here"));
        row.add(new Button("Hello there"));
        row.add(new TextField("Text here"));
        add(row);


    }

}
