package org.vaadin.firitin.style;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import in.virit.color.HslColor;
import in.virit.color.NamedColor;
import org.vaadin.firitin.util.VStyle;

@Route
public class VStyleUnreferencedElements extends VerticalLayout {
    public VStyleUnreferencedElements() {
        add("VStyle supports changing inline style for an inner element without Flow counterpart, even within shadow root");

        Paragraph paragraph = new Paragraph("This is a paragraph (for which VStyle is applied to");
        add(paragraph);

        Html html = new Html("""
                    <div>First div<div> second div<div>ThirdDiv</div></div></div>
                """);
        add(html);

        Grid<String> grid = new Grid<>() {{
            addColumn(item -> item).setHeader("column");
            addColumn(item -> item +"-c2").setHeader("column2");
            setItems("Item 1", "Item 2", "Item 3");
        }};
        add(grid);

        // Non-bound style, not attached to any component or element
        VStyle style = new VStyle() {{
            setBackgroundColor(NamedColor.AQUAMARINE);
            setColor(new HslColor(0, 100, 70));
            setFontWeight(FontWeight.BOLD);
        }};

        // Now you can apply this style to any component or element
        style.apply(paragraph);

        // In the power user mode, you can apply the style to an element that does not have a Flow counterpart.
        style.apply(html, "div > div > div");

        style.applyToShadowRoot(grid, "th");



    }
}
