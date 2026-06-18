package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HasComponents;

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
     * @return all child components, including ones nested in wrappers that don't
     * implement {@link HasComponents} and virtual children (slotted components,
     * overlays). Backed by {@link ComponentUtil#getAllChildren(Component)}.
     */
    default Stream<Component> children() {
        return ComponentUtil.getAllChildren((Component) this);
    }

    /**
     * @return all descendant components (not including this one) in pre-order,
     * including virtual children. Backed by
     * {@link ComponentUtil#streamDescendants(Component)}.
     */
    default Stream<Component> descendants() {
        return ComponentUtil.streamDescendants((Component) this);
    }

}
