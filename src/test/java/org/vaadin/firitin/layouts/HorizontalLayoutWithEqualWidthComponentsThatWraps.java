package org.vaadin.firitin.layouts;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.gridlayout.CssGridLayout;

@Route
public class HorizontalLayoutWithEqualWidthComponentsThatWraps extends CssGridLayout {
    public HorizontalLayoutWithEqualWidthComponentsThatWraps() {
        setTemplateColumns("repeat(auto-fill, minmax(200px, 1fr))");
        for (int i = 0; i < 10; i++) {
            add(new Button("Hello there " + i));
        }
    }
}
