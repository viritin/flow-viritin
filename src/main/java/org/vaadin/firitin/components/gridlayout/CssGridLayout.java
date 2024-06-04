package org.vaadin.firitin.components.gridlayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Style;

import java.util.Random;

/**
 * A Css Grid based layout with better typed Java API.
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

    public GridCell add(Component component) {
        super.add(component);
        return new GridCell(component);
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

    public void setTemplateAreas(String... areas) {
        getStyle().set("grid-template-areas",
        "\"" + String.join("\" \"", areas) + "\""
        );
    }

    public class GridCell {

        private final Component component;

        GridCell(Component component) {
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
    }

}
