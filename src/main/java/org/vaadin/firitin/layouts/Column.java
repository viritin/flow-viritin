package org.vaadin.firitin.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * A stack of components with the theme's gap between them, flush with its
 * surroundings.
 *
 * <p>This is what most vertical layouts inside a view actually are, and the
 * name replaces the idiom that keeps saying it: {@code VerticalLayout} followed
 * by {@code setPadding(false)}, over and over, each one meaning "a stack inside
 * something, not a page". The padding a VerticalLayout is born with assumes it
 * has a view to itself; a column is part of one.
 *
 * <p>What separates a Column from a {@code Div} is the gap: a Div is for pieces
 * that are lines of one thing and stack tight, a Column for pieces that are
 * things of their own and need the theme's space between them. And what a
 * Column deliberately does not carry is distance from its <em>siblings</em> —
 * that is a margin, and it belongs to the component that needs the elbow room,
 * not to the stack.
 */
public class Column extends VerticalLayout {

    public Column(Component... components) {
        super(components);
        setPadding(false);
    }
}
