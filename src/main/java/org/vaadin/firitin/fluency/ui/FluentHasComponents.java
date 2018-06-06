package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;

public interface FluentHasComponents<S extends FluentHasComponents<S>> extends HasComponents {
    @SuppressWarnings("unchecked")
    default S withComponents(Component... components) {
        add(components);
        return (S) this;
    }
}
