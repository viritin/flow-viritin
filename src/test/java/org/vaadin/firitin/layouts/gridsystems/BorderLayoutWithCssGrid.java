package org.vaadin.firitin.layouts.gridsystems;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import org.vaadin.firitin.components.cssgrid.CssGrid;

/**
 * An example how to re-create a Swing like BorderLayout with the API provided by {@link CssGrid}.
 *
 * <p>Uses {@link Composite} to hide complexity of CssGrid and only exposes minimal API</p>
 *
 */
public class BorderLayoutWithCssGrid extends Composite<CssGrid>
    implements HasSize {

    public BorderLayoutWithCssGrid() {
        // Configure CssGrid so that middle col takes all left from others
        getContent().setTemplateColumns("0fr 1fr 0fr");
        // middle row takes all left from others
        getContent().setTemplateRows("0fr 1fr 0fr");
    }

    /**
     * Sets the component in given region.
     *
     * @param region the region
     * @param component the component to show in given region
     */
    public void setChildAt(Region region, Component component) {
        getContent().add(component)
                .withColumns(region.colStart,region.colEnd)
                .withRow(region.row);
    }

    public void setGap(String gapCssLenth) {
        // exposing the setGap method from CssGrid
        getContent().setGap(gapCssLenth);
    }

    public enum Region {

        NORTH(1,4,1),
        SOUTH(1,4,3),
        EAST(1,1,2),
        WEST(3,4,2),
        CENTER(2,2,2);

        int colStart;
        int colEnd;
        int row;

        Region(int colStart, int colEnd, int row) {
            this.colStart = colStart;
            this.colEnd = colEnd;
            this.row = row;
        }
    }

}
