package org.vaadin.firitin.components.cssgrid;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Style;

import java.util.Arrays;

/**
 * An EXPERIMENTAL css Grid based layout with a better typed Java API. Aim of this
 * class is to provide a helpful Java API for the raw css grid layout. Read more
 * about Css Grid from <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_grid_layout/Basic_concepts_of_grid_layout">MDN</a>.
 * <p>
 *     The components added to this layout are the "cells" and how they are
 *     laid out can be further configure with the {@link GridCell} returned by
 *     the {@link #add(Component)} method.
 * </p>
 * @deprecated Early version of this class, there might still be some backwards incompatible
 * changes. Please provide your feedback, even if this "just works".
 */
@Deprecated(forRemoval = false)
public class CssGrid extends Div {

    /**
     * Constructs a css grid layout with given number of equally
     * sized columns.
     *
     * @param columns the number of columns to create.
     */
    public CssGrid(int columns) {
        this();
        setTemplateColumns("repeat(%s, 1fr)".formatted(columns));
    }

    public CssGrid() {
        getStyle().setDisplay(Style.Display.GRID);
    }

    public void setTemplateColumns(String... templateColumns) {
        getStyle().set("grid-template-columns", String.join(" ", templateColumns));
    }

    public void setTemplateRows(String... templateRows) {
        getStyle().set("grid-template-rows", String.join(" ", templateRows));
    }

    public GridCell add(Component component) {
        super.add(component);
        return new GridCell(component);
    }

    public void add(Object... texts) {
        for(Object o : texts) {
            if (o instanceof Component c) {
                add(c);
            } else {
                add(o.toString());
            }
        }
    }

    @Override
    public void add(String text) {
        super.add(new Div(text));
    }

    public void setGap(String gap) {
        getStyle().set("gap", gap);
    }

    public void setAutoRows(String autoRows) {
        getStyle().set("grid-auto-rows", autoRows);
    }

    public void setColumnGap(String columnGap) {
        getStyle().set("grid-column-gap", columnGap);
    }

    public void setRowGap(String rowGap) {
        getStyle().set("grid-row-gap", rowGap);
    }

    /**
     * 
     * @param rows the template areas defined as raw string
     * @deprecated consider using the better typed version {@link #setTemplateAreas(Row...)}
     */
    @Deprecated(forRemoval = false)
    public void setTemplateAreas(String... rows) {
        getStyle().set("grid-template-areas",
        "\"" + String.join("\" \"", rows) + "\""
        );
    }

    public void setTemplateAreas(Row... rows) {
        getStyle().set("grid-template-areas",
                String.join(" ", Arrays.stream(rows).map(Row::toCss).toList())
        );
    }

    /**
     * A helper class to customize Css grid specific features of a component
     * added to the {@link CssGrid}.
     *
     * This class essentially proxies inline styles for the wrapped component.
     */
    public static class GridCell {

        private final Component component;

        // TODO figure out if this could be handy as such
        private GridCell(Component component) {
            this.component = component;
        }

        public GridCell withColumn(int column) {

            return this;
        }

        public GridCell withColumns(int startColumn, int endColumn) {
            component.getStyle().set("grid-column", "%s / %s".formatted(startColumn, endColumn));
            return this;
        }

        public GridCell withRows(int startRow, int endRow) {
            component.getStyle().set("grid-row", "%s / %s".formatted(startRow, endRow));
            return this;
        }

        public GridCell withRow(int row) {
            component.getStyle().set("grid-row", row +"");
            return this;
        }

        public GridCell withAlign(Style.AlignSelf align) {
            component.getStyle().setAlignSelf(align);
            return this;
        }
        public GridCell withJustifySelf(Style.JustifyContent align) {
            component.getStyle().set("justify-self", align.name());
            return this;
        }

        public GridCell withRowSpan(int numberRows) {
            component.getStyle().set("grid-row-start", "span " + numberRows);
            return this;
        }
        public GridCell withColumnSpan(int numberColumns) {
            component.getStyle().set("grid-column-start", "span " + numberColumns);
            return this;
        }

        public GridCell withArea(String areaName) {
            component.getStyle().set("grid-area", areaName);
            return this;
        }

        public GridCell withArea(Area area) {
            component.getStyle().set("grid-area", area.name());
            return this;
        }
    }

}
