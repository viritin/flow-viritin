package org.vaadin.firitin;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import org.vaadin.firitin.layouts.Column;

/**
 * {@link Column} against the idiom it replaces. Both stacks below hold the
 * same components; the difference is that the first is a
 * {@code VerticalLayout} with its born-with padding — meant for a view that
 * has the page to itself — and the second is a Column: the same stack and the
 * same theme gap, flush with whatever it is part of.
 */
@Route
public class ColumnExample extends VerticalLayout {

    public ColumnExample() {
        add(new H1("Column"));

        add(new H3("VerticalLayout inside a box"));
        add(framed(new VerticalLayout(
                new Span("The padding assumes this is a whole view,"),
                new TextField("so inside a box"),
                new Span("it stands away from its own edges."))));

        add(new H3("Column inside a box"));
        add(framed(new Column(
                new Span("A Column is part of something:"),
                new TextField("same stack, same gap"),
                new Span("flush with its surroundings."))));

        add(new Paragraph("Every place that writes VerticalLayout followed by "
                + "setPadding(false) means this class."));
    }

    private static Div framed(com.vaadin.flow.component.Component content) {
        Div frame = new Div(content);
        frame.getStyle().setBorder("1px dashed gray");
        frame.setMaxWidth("30rem");
        return frame;
    }
}
