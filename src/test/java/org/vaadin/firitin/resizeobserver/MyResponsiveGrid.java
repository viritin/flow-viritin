package org.vaadin.firitin.resizeobserver;

import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.util.ResizeObserver;

@Route
public class MyResponsiveGrid extends VGrid<ResizeObserver.Dimensions> {
    private Mode mode;

    public MyResponsiveGrid() {
        super(ResizeObserver.Dimensions.class);
        addResizeListener(event -> {
            configureColumns(event.getWidht() < 800 ? Mode.MOBILE : Mode.DESKTOP);
            // Also add new rows to the grid, this is demo specific...
            getListDataView().addItem(event.getDimensions());
        });
    }

    private void configureColumns(Mode mode) {
        if (this.mode != mode) {
            this.mode = mode;
            if (mode == Mode.MOBILE) {
                setColumns("width", "height");
            } else {
                setColumns("width", "height", "left", "top");
            }
        }
    }

    enum Mode {
        MOBILE, DESKTOP
    }

}
