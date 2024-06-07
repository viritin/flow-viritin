package org.vaadin.firitin.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * 
 *
 * A trivial grid layout that mimics Bootstrap grid system with following features
 *
 *  * supports colspan (1-12)
 *  * collapses to a vertical layout below 768 window width.
 *
 *  Not really meant for anything actual usage, but aiming to create a helper
 *  to migrate a project currently using limited set of Bootstrap grid system
 *  for layouts. This version is built with LumoUtilities shipped with Vaadin.
 *
 *  Current implementation only allows setting col span once for the Cell (calling again
 *  can cause unexpected results).
 */
public class BullShitLayout extends Div {

    public BullShitLayout() {
        addClassNames(
                LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                "mdgrid", // note, a typo in utility classes, this will be broken soon: https://github.com/vaadin/web-components/pull/7416
                LumoUtility.Grid.Column.COLUMNS_12
        );
        setWidthFull();
    }

    public CellConfig add(Component component) {
        super.add(component);
        return new CellConfig(component);
    }

    public class CellConfig {
        private final Component component;

        public CellConfig(Component component) {
            this.component = component;
        }

        public CellConfig withColSpan(int span) {
            if(span > 0 && span < 13) {
                component.addClassName("col-span-" + span);
                return this;
            } else {
                throw new IllegalArgumentException("Colspan must be between 1 and 12");
            }
        }
    }

}
