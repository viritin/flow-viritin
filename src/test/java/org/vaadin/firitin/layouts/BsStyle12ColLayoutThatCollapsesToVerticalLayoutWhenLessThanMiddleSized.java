package org.vaadin.firitin.layouts;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.cssgrid.CssGrid;
import org.vaadin.firitin.util.ResizeObserver;
import org.vaadin.firitin.util.VStyleUtil;

@Route
public class BsStyle12ColLayoutThatCollapsesToVerticalLayoutWhenLessThanMiddleSized extends VerticalLayout {
    public BsStyle12ColLayoutThatCollapsesToVerticalLayoutWhenLessThanMiddleSized() {
        VStyleUtil.inject("""
                #cssgrid div {
                    background: cyan;
                    margin:0.5em;
                }
            """);
        CssGrid gridLayout = new CssGrid(12);
        gridLayout.setId("cssgrid");
        gridLayout.setWidthFull();
        gridLayout.add(new Div("span 2")).withColumnSpan(2);
        gridLayout.add(new Div("span 10")).withColumnSpan(10);
        gridLayout.add(new Div("span 6")).withColumnSpan(6);
        gridLayout.add(new Div("span 6")).withColumnSpan(6);
        gridLayout.add(new Div("span 3")).withColumnSpan(3);
        gridLayout.add(new Div("span 3")).withColumnSpan(3);
        gridLayout.add(new Div("span 6")).withColumnSpan(6);

        for(int i = 0; i < 24;i++) {
            gridLayout.add(new Div("span 1"));
        }

        add(gridLayout);

        ResizeObserver.get().observe(this, size -> {
            // "sm" in BS
            if(size.width() < 768) {
                gridLayout.getStyle().setDisplay(Style.Display.BLOCK);
            } else {
                gridLayout.getStyle().setDisplay(Style.Display.GRID);
            }
        });

    }
}
