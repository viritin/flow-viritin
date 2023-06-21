/*
 * Copyright 2019 Viritin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.firitin.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.function.SerializableFunction;

/**
 * A Tree component to display hierarchical data sets.
 *
 * @author mstahv
 * @param <T> the type of items listed as nodes of Tree. Use Object if nothing
 *            else.
 */
@StyleSheet("./org/vaadin/firitin/components/tree.css")
public class Tree<T> extends Composite<VerticalLayout> {

    private static final long serialVersionUID = -927074586817131378L;

    /**
     * This can be used to further configure the created TreeItem instances. For
     * example to add additional click listeners or context menus.
     *
     * @author mstahv
     *
     * @param <T> the type of items in the Tree
     */
    public interface ItemDecorator<T> extends BiConsumer<T, TreeItem>, Serializable {

    }

    /**
     * A listener to track when the selected node is changed.
     *
     * @author mstahv
     *
     * @param <T> the type of the selected domain object
     */
    @FunctionalInterface
    public interface SelectionListener<T> extends Serializable {
        public void selected(T selected, TreeItem item);
    }

    /**
     * {@link ItemIconGenerator} can be used to customize the icon shown before the
     * label of an item.
     *
     * @param <T> item type
     * @author Vaadin Ltd
     * @since 1.0
     */
    @FunctionalInterface
    public interface ItemIconGenerator<T> extends SerializableFunction<T, Component> {

        /**
         * Gets a icon for the {@code item}.
         *
         * @param item the item to get icon for
         * @return the icon of the item, not {@code null}
         */
        @Override
        Component apply(T item);
    }

    /**
     * {@link ItemGenerator} can be used to customize how to item is shown. This
     * overrides everything. If for example {@link ItemLabelGenerator} or
     * {@link ItemIconGenerator} are defined, they are ignored.
     *
     * @param <T> item type
     * @author Vaadin Ltd
     * @since 1.0
     */
    @FunctionalInterface
    public interface ItemGenerator<T> extends SerializableFunction<T, Component> {

        /**
         * Gets a component for the {@code item}.
         *
         * @param item the item
         * @return the component for the item, not {@code null}
         */
        @Override
        Component apply(T item);
    }

    private ItemLabelGenerator<T> itemLabelGenerator = o -> o.toString();
    private ItemIconGenerator<T> itemIconGenerator;
    private ItemGenerator<T> itemGenerator = item -> {
        if (itemIconGenerator != null) {
            Component icon = itemIconGenerator.apply(item);

            Span textSpan = new Span(new Text(itemToString(item)));
            textSpan.getElement().getClassList().add("item-text");

            return new Span(icon, textSpan);
        } else {
            return new Span(itemToString(item));
        }
    };

    private List<ItemDecorator<T>> itemDecorators = new ArrayList<>();
    private Set<SelectionListener<T>> selectionListeners = new LinkedHashSet<>();
    private HashMap<T, TreeItem> domainObjectToTreeItem = new HashMap<>();

    private TreeItem selectedItem;

    public Tree() {
        getElement().getClassList().add("viritin-tree");
        getContent().setMargin(false);
        getContent().setSpacing(false);
    }

    private String itemToString(T item) {
        return itemLabelGenerator.apply(item);
    }

    @FunctionalInterface
    public interface ChildrenProvider<T> {

        /**
         * @param parent the item whose children are to be provided
         * @return list of children or null if parent is is a leaf node.
         */
        List<T> getChildren(T parent);
    }

    public void setItems(List<T> rootNodes, ChildrenProvider<T> childrenProvider) {
        for (T item : rootNodes) {
            final TreeItem treeItem = createTreeItem(item);
            getContent().add(treeItem);
            fillTree(childrenProvider, item, treeItem);
        }
    }

    public void setItems(TreeData<T> treeData, ChildrenProvider<T> childrenProvider) {
        getContent().removeAll();

        setItems(treeData.getRootItems(), childrenProvider);
    }

    protected TreeItem createTreeItem(T item) {
        final TreeItem treeItem = new TreeItem(itemGenerator.apply(item));
        domainObjectToTreeItem.put(item, treeItem);
        treeItem.addClickListener(e -> {
            if (selectedItem != null) {
                selectedItem.setSelected(false);
            }
            selectedItem = treeItem;
            selectedItem.setSelected(true);
            selectionListeners.forEach(l -> l.selected(item, treeItem));
        });
        itemDecorators.forEach(d -> d.accept(item, treeItem));
        return treeItem;
    }

    protected void fillTree(ChildrenProvider<T> childrenProvider, T item, final TreeItem treeItem) {
        List<T> children = childrenProvider.getChildren(item);
        if (children != null && !children.isEmpty()) {
            treeItem.setPopulateSubreeHandler(() -> {
                for (T t : children) {
                    final TreeItem child = createTreeItem(t);
                    treeItem.addChild(child);
                    fillTree(childrenProvider, t, child);
                }
            });

        }
    }

    /**
     * Adds an {@link ItemDecorator} to further configure {@link TreeItem}s
     * generated automatically when {@link #setItems(List, ChildrenProvider)} method
     * is called.
     *
     * @param decorator the {@link ItemDecorator}
     */
    public void addItemDecorator(ItemDecorator<T> decorator) {
        itemDecorators.add(decorator);
    }

    public void removeItemDecorator(ItemDecorator<T> decorator) {
        itemDecorators.remove(decorator);
    }

    /**
     * Sets the strategy to generate label texts for the items.
     *
     * @param itemLabelGenerator the {@link ItemLabelGenerator}
     */
    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
        this.itemLabelGenerator = itemLabelGenerator;
    }

    /**
     * Sets the strategy to generate icons for the items.
     *
     * @param itemIconGenerator the {@link ItemIconGenerator}
     */
    public void setItemIconGenerator(ItemIconGenerator<T> itemIconGenerator) {
        this.itemIconGenerator = itemIconGenerator;
    }

    /**
     * Sets the strategy to generate component for for the items.
     * <p>
     * Note that this overrides possibly configured {@link ItemIconGenerator} and
     * {@link ItemLabelGenerator}.
     *
     * @param itemGenerator the {@link ItemGenerator}
     */
    public void setItemGenerator(ItemGenerator<T> itemGenerator) {
        this.itemGenerator = itemGenerator;
    }

    /**
     * Adds selection listener to nodes.
     *
     * @param listener the listener to be called when selected item changes
     */
    public void addSelectionListener(SelectionListener<T> listener) {
        selectionListeners.add(listener);
    }

    public void removeSelectionListener(SelectionListener<T> listener) {
        selectionListeners.remove(listener);
    }

    /**
     * Shows children of the node in UI. Same as user would click on the caret in
     * the UI.
     *
     * @param item the item whose children should be visible in the UI
     */
    public void showChildren(T item) {
        domainObjectToTreeItem.get(item).showChildren();
    }

    /**
     * Shows children of the node in UI recursively. Same as user would click on the caret in
     * the UI.
     *
     * @param item the item whose children should be visible in the UI
     */
    public void showChildrenRecursively(T item) {
        domainObjectToTreeItem.get(item).showChildrenRecursively();
    }

    /**
     * Hides children of the node in UI. Same as user would click on the caret in
     * the UI when children are visible.
     *
     * @param item the item whose children should be hidden in the UI
     */
    public void hideChildren(T item) {
        domainObjectToTreeItem.get(item).closeChildren();
    }

    /**
     * Moves child of the node in UI.
     *
     * @param parent the parent whose child should be moved in the UI, may be null
     * @param items the items within which one is to be moved
     * @param index the index of the item to be moved
     * @param up True then move up else move down
     */
    public void moveChild(T parent, List<T> items, int index, boolean up) {
        int newIndex = up ? index - 1 : index + 1;

        if (newIndex >= 0 && newIndex < items.size()) {
            List<TreeItem> treeItems = items.stream().map(i -> domainObjectToTreeItem.get(i)).collect(Collectors.toList());

            if (parent != null) {
                TreeItem parentTreeItem = domainObjectToTreeItem.get(parent);

                treeItems.forEach(ti -> parentTreeItem.removeChild(ti));
                Collections.swap(treeItems, index, newIndex);
                treeItems.forEach(ti -> parentTreeItem.addChild(ti));
            } else {
                treeItems.forEach(ti -> getContent().remove(ti));
                Collections.swap(treeItems, index, newIndex);
                treeItems.forEach(ti -> getContent().add(ti));
            }
        }
    }

    /**
     * Adds child of the node in UI.
     *
     * @param parent the parent whose child should be added in the UI, may be null
     * @param item the item to be added in the UI
     */
    public void addChild(T parent, T item) {
        if (parent == null) {
            final TreeItem treeItem = createTreeItem(item);
            getContent().add(treeItem);
        } else {
            TreeItem parentTreeItem = domainObjectToTreeItem.get(parent);
            TreeItem treeItem = createTreeItem(item);

            parentTreeItem.addChild(treeItem);
        }
    }

    /**
     * Removes child of the node in UI.
     *
     * @param parent the parent whose child should be removed in the UI, may be null
     * @param item the item to be removed in the UI
     */
    public void removeChild(T parent, T item) {
        TreeItem treeItem = domainObjectToTreeItem.get(item);

        if (parent == null) {
            getContent().remove(treeItem);
        } else {
            TreeItem parentTreeItem = domainObjectToTreeItem.get(parent);
            parentTreeItem.removeChild(treeItem);
        }
    }

    /**
     * Edits child in UI.
     *
     * @param item the item to be edited in the UI
     */
    public void editChild(T item) {
        TreeItem treeItem = domainObjectToTreeItem.get(item);

        Span nodeContent = (Span) treeItem.getNodeContent();

        Optional<Component> optIcon = treeItem.getNodeContent().getChildren().filter(c -> c instanceof Icon).findFirst();
        Optional<Component> optSpan = treeItem.getNodeContent().getChildren().filter(c -> c instanceof Span).findFirst();

        if (optIcon.isPresent() && optSpan.isPresent()) {
            Icon icon = (Icon) optIcon.get();
            Span span = (Span) optSpan.get();

            nodeContent.remove(icon);
            nodeContent.remove(span);

            span.getChildren().filter(cc -> cc instanceof Text).findFirst().ifPresent(cc -> {
                ((Text) cc).setText(itemLabelGenerator.apply(item));
            });

            nodeContent.add(itemIconGenerator.apply(item));
            nodeContent.add(span);
        }
    }

    /**
     * @param item the item to be styled in the UI
     * @param styleName the style property name as camelCase, not null
     * @param styleValue the style property value (if null, the property will be removed)
     */
    public void styleChild(T item, String styleName, String styleValue) {
        TreeItem treeItem = domainObjectToTreeItem.get(item);

        treeItem.getNodeContent().getChildren().filter(c -> c instanceof Span).findFirst().ifPresent(c -> {
            ((Span) c).getStyle().set(styleName, styleValue);
        });
    }

    /**
     * Scrolls the tree item representing given item to be visible in the UI.
     * <p>
     * Note, the item needs to be visible for the method to work.
     *
     * @param item the item to be shown in the UI
     * @return The TreeItem for the scroll
     */
    public TreeItem scrollItemToView(T item) {
        TreeItem treeItem = domainObjectToTreeItem.get(item);
        treeItem.getElement().executeJs("this.scrollIntoView()");
        return treeItem;
    }

    /**
     * Selects (or deselects) the tree item representing given item in the UI.
     *
     * @param item item the item to be selected in the UI
     * @param selected if True then select else deselect
     */
    public void selectItem(T item, boolean selected) {
        TreeItem treeItem = domainObjectToTreeItem.get(item);

        if (selected) {
            if (selectedItem != null) {
                selectedItem.setSelected(false);
            }
            selectedItem = treeItem;
            selectedItem.setSelected(true);
            selectionListeners.forEach(l -> l.selected(item, treeItem));
        } else {
            if (selectedItem == treeItem) {
                selectedItem.setSelected(false);
                selectedItem = null;
            }
        }
    }

    /**
     * Deselects all tree items in the UI.
     */
    public void deselectAllItems() {
        selectedItem = null;
        domainObjectToTreeItem.values().forEach(treeItem -> treeItem.setSelected(false));
    }

}
