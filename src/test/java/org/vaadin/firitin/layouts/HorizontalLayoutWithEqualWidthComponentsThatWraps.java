package org.vaadin.firitin.layouts;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.cssgrid.CssGrid;

@Route
public class HorizontalLayoutWithEqualWidthComponentsThatWraps extends CssGrid {
    public HorizontalLayoutWithEqualWidthComponentsThatWraps() {
        setTemplateColumns("repeat(auto-fill, minmax(200px, 1fr))");
        for (int i = 0; i < 10; i++) {
            add(new Button("Hello there " + i));
        }
    }
}
