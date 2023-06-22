package org.vaadin.firitin.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.dom.Style;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple container that can position children in 5 regions, namely:
 * NORTH, SOUTH, EAST, WEST, CENTER.
 * Similar to https://docs.oracle.com/javase/7/docs/api/java/awt/BorderLayout.html
 */
@Tag("border-layout")
public class BorderLayout extends Component implements HasComponents {

    private final Map<Region, Component> children = new HashMap<>(Region.values().length);

    public BorderLayout() {
        super();
        getElement().getStyle().setDisplay(Style.Display.GRID);
        getElement().getStyle().set("grid-template-columns", "repeat(3, 1fr)");
    }

    private static void applyStyle(Component component, Region region) {
        component.getElement().getStyle().set("grid-column", region.gridColumn);
        component.getElement().getStyle().set("grid-row", region.gridRow);

        stripStyle(component);
        component.getElement().getClassList().add(region.name().toLowerCase());
    }

    private static void stripStyle(Component component) {
        for (Region region : Region.values()) {
            component.getElement().getClassList().remove(region.name().toLowerCase());
        }
    }

    public void setChildAt(Region region, Component component) {
        Component existingChild = children.get(region);
        if (existingChild != null) {
            remove(existingChild);
        }

        applyStyle(component, region);
        add(component);
        children.put(region, component);
    }

    public void removeChildAt(Region region) {
        Component existingChild = children.get(region);
        if (existingChild == null) {
            return;
        }

        stripStyle(existingChild);
        remove(existingChild);
        children.remove(region);
    }

    public enum Region {

        NORTH("1", "2"),
        SOUTH("3", "2"),
        EAST("2", "3"),
        WEST("2", "1"),
        CENTER("2", "2");

        private String gridRow;
        private String gridColumn;

        private Region(String r, String c) {
            this.gridRow = r;
            this.gridColumn = c;
        }
    }
}
