package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;

@SuppressWarnings("unchecked")
public interface FluentHasComponents<S extends FluentHasComponents<S>> extends HasComponents {

    default S withComponents(Component... components) {
        add(components);
        return (S) this;
    }

    default S withComponentAsFirst(Component component) {
        addComponentAsFirst(component);
        return (S) this;
    }

    default S withComponentAtIndex(int index, Component component) {
        addComponentAtIndex(index, component);
        return (S) this;
    }
}
