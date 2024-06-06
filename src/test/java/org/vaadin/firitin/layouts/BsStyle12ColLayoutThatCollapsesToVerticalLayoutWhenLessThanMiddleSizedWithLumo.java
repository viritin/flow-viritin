package org.vaadin.firitin.layouts;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.firitin.components.gridlayout.CssGridLayout;
import org.vaadin.firitin.util.ResizeObserver;
import org.vaadin.firitin.util.VStyleUtil;

@Route
public class BsStyle12ColLayoutThatCollapsesToVerticalLayoutWhenLessThanMiddleSizedWithLumo extends VerticalLayout {
    public BsStyle12ColLayoutThatCollapsesToVerticalLayoutWhenLessThanMiddleSizedWithLumo() {
        VStyleUtil.inject("""
                #cssgrid div {
                    background: cyan;
                    margin:0.5em;
                }
            """);

        Div gridLayout = new Div();
        gridLayout.addClassName(LumoUtility.Display.GRID);
        gridLayout.addClassName(LumoUtility.Grid.Column.COLUMNS_12);
        gridLayout.setId("cssgrid");
        gridLayout.setWidthFull();
        Div div = new Div("span 2");
        div.addClassName(LumoUtility.Grid.Column.COLUMN_SPAN_2);
        gridLayout.add(div);
        div = new Div("span 10");
        div.addClassName(LumoUtility.Grid.Column.COLUMN_SPAN_10);
        gridLayout.add(div);
        div = new Div("span 6");
        div.addClassName(LumoUtility.Grid.Column.COLUMN_SPAN_6);
        gridLayout.add(div);
        div = new Div("span 6");
        div.addClassName(LumoUtility.Grid.Column.COLUMN_SPAN_6);
        gridLayout.add(div);
        div = new Div("span 3");
        div.addClassName(LumoUtility.Grid.Column.COLUMN_SPAN_3);
        gridLayout.add(div);
        div = new Div("span 3");
        div.addClassName(LumoUtility.Grid.Column.COLUMN_SPAN_3);
        gridLayout.add(div);
        div = new Div("span 6");
        div.addClassName(LumoUtility.Grid.Column.COLUMN_SPAN_6);
        gridLayout.add(div);

        for(int i = 0; i < 24;i++) {
            gridLayout.add(new Div("span 1"));
        }

        add(gridLayout);

        ResizeObserver.get().observe(this, size -> {
            // "sm" in BS
            if(size.width() < 768) {
                gridLayout.removeClassName(LumoUtility.Display.GRID);
            } else {
                gridLayout.addClassName(LumoUtility.Display.GRID);
            }
        });

    }
}
