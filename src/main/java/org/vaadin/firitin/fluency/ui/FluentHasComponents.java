package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import org.vaadin.firitin.util.ComponentTree;

import java.util.stream.Stream;

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

    /**
     * @return children via backing "StateNode" structure, so that all components are listed,
     * regardless of the component implementation.
     */
    default Stream<Component> children() {
        return ComponentTree.children((Component) this);
    }

    /**
     * @return All descendants (via backing StateNode structure, so that all components are truly listed) in
     * pre-order.
     */
    default Stream<Component> descendants() {
        return ComponentTree.descendants((Component) this);
    }

}
