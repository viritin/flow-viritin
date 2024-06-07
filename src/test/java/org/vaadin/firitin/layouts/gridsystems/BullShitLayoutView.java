package org.vaadin.firitin.layouts.gridsystems;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class BullShitLayoutView extends VerticalLayout {

    public BullShitLayoutView() {
        add("BullShitLayout provides a tiny subset of the features of Bootstrap grid system.");

        BullShitLayout bullShitLayout = new BullShitLayout();

        bullShitLayout.add(content("Foo")).withColSpan(6);
        bullShitLayout.add(content("Bar")).withColSpan(6);

        // 12 cols consume, new row starting from here...
        bullShitLayout.add(content("Foo")).withColSpan(4);
        bullShitLayout.add(content("Bar")).withColSpan(8);

        // six withing a six wide col is essentially 3
        BullShitLayout bs2 = new BullShitLayout();
        bs2.add(content("Foo 2 level")).withColSpan(6);
        bs2.add(content("Bar 2 level")).withColSpan(6);
        bullShitLayout.add(bs2).withColSpan(6);
        bullShitLayout.add(content("Baz")).withColSpan(6);

        // Same as above blown to top level
        bullShitLayout.add(content("Foo")).withColSpan(3);
        bullShitLayout.add(content("Bar")).withColSpan(3);
        bullShitLayout.add(content("Baz")).withColSpan(6);

        add(bullShitLayout);
    }

    private Div content(String textContent) {
        Div div = new Div();
        div.setText(textContent);
        div.getStyle().setBackground("cyan");
        div.getStyle().setMargin("1em");
        return div;
    }
}
