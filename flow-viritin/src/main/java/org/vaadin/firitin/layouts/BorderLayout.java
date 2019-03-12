package org.vaadin.firitin.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple container that can position children in 5 regions, namely:
 * NORTH, SOUTH, EAST, WEST, CENTER.
 * Similar to https://docs.oracle.com/javase/7/docs/api/java/awt/BorderLayout.html
 */
@Tag("border-layout")
@HtmlImport("org/vaadin/firitin/layouts/border-layout.html")
public class BorderLayout extends Component implements HasComponents {

    public enum Region {

        NORTH,
        SOUTH,
        EAST,
        WEST,
        CENTER
    }

    private final Map<Region, Component> children = new HashMap<>(Region.values().length);

    public BorderLayout() {
        super();
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

    private static void applyStyle(Component component, Region region) {
        stripStyle(component);
        component.getElement().getClassList().add(region.name().toLowerCase());
    }

    private static void stripStyle(Component component) {
        for (Region region : Region.values()) {
            component.getElement().getClassList().remove(region.name().toLowerCase());
        }
    }
}
