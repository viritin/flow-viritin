package org.vaadin.firitin.components.gridlayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Style;

import java.util.Arrays;

/**
 * A Css Grid based layout with a better typed Java API.
 */
public class CssGridLayout extends Div {

    public CssGridLayout(int columns) {
        this();
        setTemplateColumns("repeat(%s, 1fr)".formatted(columns));
    }

    public CssGridLayout() {
        getStyle().setDisplay(Style.Display.GRID);
    }

    public void setTemplateColumns(String... templateColumns) {
        getStyle().set("grid-template-columns", String.join(" ", templateColumns));
    }

    public void setTemplateRows(String... templateRows) {
        getStyle().set("grid-template-rows", String.join(" ", templateRows));
    }

    public GridCellHandle add(Component component) {
        super.add(component);
        return new GridCellHandle(component);
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
     * added to the {@link CssGridLayout}.
     */
    public static class GridCellHandle {

        private final Component component;

        // TODO figure out if this could be handy as such
        private GridCellHandle(Component component) {
            this.component = component;
        }

        public GridCellHandle withColumn(int column) {

            return this;
        }

        public GridCellHandle withColumns(int startColumn, int endColumn) {
            component.getStyle().set("grid-column", "%s / %s".formatted(startColumn, endColumn));
            return this;
        }

        public GridCellHandle withRows(int startRow, int endRow) {
            component.getStyle().set("grid-row", "%s / %s".formatted(startRow, endRow));
            return this;
        }

        public GridCellHandle withRow(int row) {
            component.getStyle().set("grid-row", row +"");
            return this;
        }

        public GridCellHandle withAlign(Style.AlignSelf align) {
            component.getStyle().setAlignSelf(align);
            return this;
        }
        public GridCellHandle withJustifySelf(Style.JustifyContent align) {
            component.getStyle().set("justify-self", align.name());
            return this;
        }

        public GridCellHandle withRowSpan(int numberRows) {
            component.getStyle().set("grid-row-start", "span " + numberRows);
            return this;
        }
        public GridCellHandle withColumnSpan(int numberColumns) {
            component.getStyle().set("grid-column-start", "span " + numberColumns);
            return this;
        }

        public GridCellHandle withArea(String areaName) {
            component.getStyle().set("grid-area", areaName);
            return this;
        }

        public GridCellHandle withArea(Area area) {
            component.getStyle().set("grid-area", area.name());
            return this;
        }
    }

}
