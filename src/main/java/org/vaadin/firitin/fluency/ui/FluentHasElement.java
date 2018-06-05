package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.HasElement;

@SuppressWarnings("unchecked")
public interface FluentHasElement<S extends FluentHasElement<S>>
        extends HasElement {

    default S withDescription(String description) {
        getElement().setAttribute("title", description); // TODO find the Vaadin implementation of a tooltip
        return (S) this;
    }

    ;
}
