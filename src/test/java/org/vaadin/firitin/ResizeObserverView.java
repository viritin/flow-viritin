package org.vaadin.firitin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.util.ComponentSizeReporter;

@Route
public class ResizeObserverView extends VVerticalLayout {

    public ResizeObserverView() {
        Paragraph uiElementSize = new Paragraph();
        Paragraph paragraph = new Paragraph();
        paragraph.setWidthFull();
        add(uiElementSize, paragraph);

        SplitLayout splitLayout = new SplitLayout();
        // there is an interesting effect without this, split layout trying
        // to do some interesting logic to figure out good default position...
        splitLayout.setSplitterPosition(30);
        var left = new Div("L");
        var grid = new VGrid<>(ComponentSizeReporter.Dimensions.class)
                .withSizeFull();
        splitLayout.addToPrimary(left);
        splitLayout.addToSecondary(grid);
        splitLayout.setSizeFull(); // WTF, why is this needed?
        addAndExpand(splitLayout);

        ComponentSizeReporter.get()
                .observe(paragraph, left, grid)
                .addResizeListener(e -> {
                    if (e.component() == UI.getCurrent()) {
                        uiElementSize.setText("Hello. UI element (~ browser content area) size is " + e.dimensions());
                    } else if (e.component() == paragraph) {
                        paragraph.setText("Full width paragraph size (~ excluding marginals) is " + e.dimensions());
                    } else if (e.component() == left) {
                        left.setText("Left component size is " + e.dimensions());
                    } else if (e.component() == grid) {
                        // Added grid sizes to the grid itself and adjust
                        // columns if narrow
                        grid.getListDataView().addItem(e.dimensions());
                        if(e.dimensions().width() > 450) {
                            grid.setColumns("x","y","width","height", "top", "right", "bottom");
                        } else {
                            grid.setColumns("width","height");
                        }
                    }
                });
    }

}
