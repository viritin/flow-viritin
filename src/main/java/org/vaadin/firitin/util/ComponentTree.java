package org.vaadin.firitin.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.internal.StateNode;
import com.vaadin.flow.internal.nodefeature.ComponentMapping;
import com.vaadin.flow.internal.nodefeature.ElementChildrenList;
import com.vaadin.flow.internal.nodefeature.VirtualChildrenList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Helpers for traversing the Vaadin component tree.
 * <p>
 * Unlike {@link com.vaadin.flow.component.HasComponents#getChildren()}, these
 * methods walk the underlying element tree (both regular and virtual children),
 * so they also find components nested inside wrappers that don't implement
 * {@code HasComponents}, as well as virtual children such as opened dialogs
 * and popovers.
 */
public final class ComponentTree {

    private ComponentTree() {
    }

    /**
     * Returns the direct child components of the given root.
     */
    public static Stream<Component> children(Component root) {
        List<Component> children = new ArrayList<>();
        collectChildren(root.getElement().getNode(), children);
        return children.stream();
    }

    /**
     * Returns the given root and all of its descendants in pre-order
     * (root first, then each child's sub-tree in order).
     */
    public static Stream<Component> descendants(Component root) {
        return Stream.concat(Stream.of(root),
                children(root).flatMap(ComponentTree::descendants));
    }

    private static void collectChildren(StateNode node, List<Component> children) {
        if (node.hasFeature(ElementChildrenList.class)) {
            node.getFeatureIfInitialized(ElementChildrenList.class)
                    .ifPresent(list -> list.forEachChild(child -> visit(child, children)));
        }
        if (node.hasFeature(VirtualChildrenList.class)) {
            node.getFeatureIfInitialized(VirtualChildrenList.class)
                    .ifPresent(list -> list.forEachChild(child -> visit(child, children)));
        }
    }

    private static void visit(StateNode node, List<Component> children) {
        node.getFeatureIfInitialized(ComponentMapping.class).ifPresentOrElse(
                cm -> cm.getComponent().ifPresent(children::add),
                () -> collectChildren(node, children));
    }
}
