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

import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.SerializableFunction;

/**
 *
 * @author mstahv
 * @param <T> the type of items listed as nodes of Tree. Use Object if nothing
 *            else.
 */
@StyleSheet("org/vaadin/firitin/components/tree.css")
public class Tree<T> extends Composite<VerticalLayout> {

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
		 * @param component the item 
		 * @return the component for the item, not {@code null}
		 */
		@Override
		Component apply(T item);
	}

	private ItemLabelGenerator<T> itemLabelGenerator = o -> o.toString();
	private ItemIconGenerator<T> itemIconGenerator;
	private ItemGenerator<T> itemGenerator = item -> {
		if(itemIconGenerator != null) {
			return new Span(itemIconGenerator.apply(item), new Text(itemToString(item)));
		} else {
			return new Span(itemToString(item));
		}
	};
	
	public Tree() {
		getElement().getClassList().add("viritin-tree");
//		getContent().setWidth("0");
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

	public void setItems(List<T> items, ChildrenProvider<T> childrenProvider) {
		for (T item : items) {
			final TreeItem treeItem = new TreeItem(itemGenerator.apply(item));
			getContent().add(treeItem);
			fillTree(childrenProvider, item, treeItem);
		}
	}

	protected void fillTree(ChildrenProvider<T> childrenProvider, T item, final TreeItem treeItem) {
		List<T> children = childrenProvider.getChildren(item);
		for (T t : children) {
			TreeItem child = treeItem.addChild(itemGenerator.apply(t));
			fillTree(childrenProvider, t, child);
		}
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
	 * Note that this overrides possibly configured {@link ItemIconGenerator} and {@link ItemLabelGenerator}.
	 * 
	 * @param itemGenerator the {@link ItemGenerator}
	 */
	public void setItemGenerator(ItemGenerator<T> itemGenerator) {
		this.itemGenerator = itemGenerator;
	}

}
