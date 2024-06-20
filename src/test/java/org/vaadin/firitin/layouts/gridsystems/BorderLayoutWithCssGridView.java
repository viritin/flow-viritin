package org.vaadin.firitin.layouts.gridsystems;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.cssgrid.CssGrid;

import java.util.Random;

@Route
public class BorderLayoutWithCssGridView extends BorderLayoutWithCssGrid {

    public BorderLayoutWithCssGridView() {
        setSizeFull();
        setGap("1em");
        setChildAt(Region.NORTH, content("NORTH"));
        setChildAt(Region.SOUTH, content("SOUTH"));
        setChildAt(Region.EAST, content("EAST"));
        setChildAt(Region.WEST, content("WEST"));
        setChildAt(Region.CENTER, content("CENTER. In Borderlayout, the center part takes all the space" +
                "that is not needed by the other regions."));
    }


    private Random random = new Random(0);

    private Div content(String textContent) {
        Div div = new Div(textContent);
        //pseudorandom color to visualize the output
        int nextInt = random.nextInt(0xffffff + 1);
        String colorCode = String.format("#%06x", nextInt);
        div.getStyle().setBackground(colorCode);
        div.getStyle().setPadding("1em");
        div.setMinWidth("100px");
        div.setMinHeight("100px");
        return div;
    }
}
