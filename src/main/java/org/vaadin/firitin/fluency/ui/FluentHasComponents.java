package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.dom.Element;

import java.util.List;
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
     * Adds the given components right before the reference component, in the
     * given order. Components that are already children of this container are
     * moved.
     *
     * @param reference an existing child of this container
     * @param components the components to add
     * @return this for chaining
     * @throws IllegalArgumentException if reference is not a child of this
     * container, or is one of the components to add
     */
    default S addBefore(Component reference, Component... components) {
        insertRelativeTo(reference, 0, components);
        return (S) this;
    }

    /**
     * Adds the given components right after the reference component, in the
     * given order. Components that are already children of this container are
     * moved.
     *
     * @param reference an existing child of this container
     * @param components the components to add
     * @return this for chaining
     * @throws IllegalArgumentException if reference is not a child of this
     * container, or is one of the components to add
     */
    default S addAfter(Component reference, Component... components) {
        insertRelativeTo(reference, 1, components);
        return (S) this;
    }

    private void insertRelativeTo(Component reference, int offset, Component... components) {
        Element element = ((Component) this).getElement();
        if (!element.equals(reference.getElement().getParent())) {
            throw new IllegalArgumentException("Reference component is not a child of this container");
        }
        if (List.of(components).contains(reference)) {
            throw new IllegalArgumentException("Can't add the reference component relative to itself");
        }
        for (Component c : components) {
            // detach existing children first, so they don't skew the index
            if (element.equals(c.getElement().getParent())) {
                element.removeChild(c.getElement());
            }
        }
        int index = element.indexOfChild(reference.getElement()) + offset;
        for (Component c : components) {
            addComponentAtIndex(index++, c);
        }
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
