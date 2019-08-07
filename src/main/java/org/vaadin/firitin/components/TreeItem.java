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

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * A very simple component composition to implement a trivial tree component.
 *
 * @author mstahv
 */
public class TreeItem extends Composite<HorizontalLayout> implements ComponentEventListener<ClickEvent<Span>> {

	private Span expander;
	private boolean expanded = false;
	private VerticalLayout children;

	public TreeItem(Component c) {
		super();
		expander = new Span();
		expander.add(VaadinIcon.ANGLE_RIGHT.create());
		expander.addClickListener(this);
		expander.setVisible(false);

		children = new VerticalLayout();
		children.setPadding(false);
		children.setVisible(false);

		VerticalLayout verticalLayout = new VerticalLayout(c, children);
		verticalLayout.setPadding(false);
		getContent().add(expander, verticalLayout);
	}

	public TreeItem(String stringContent) {
		this(new Span(stringContent));
	}

	public TreeItem addChild(Component childComponent) {
		TreeItem i = new TreeItem(childComponent);
		this.expander.setVisible(true);
		this.children.add(i);
		return i;
	}

	public TreeItem addChild(String stringContent) {
		TreeItem i = new TreeItem(new Span(stringContent));
		this.expander.setVisible(true);
		this.children.add(i);
		return i;
	}

	public void removeChild(TreeItem c) {
		this.children.remove(c);
	}

	@Override
	public void onComponentEvent(ClickEvent<Span> e) {
		expanded = !expanded;
		expander.removeAll();
		expander.add(expanded ? VaadinIcon.ANGLE_DOWN.create() : VaadinIcon.ANGLE_RIGHT.create());
		children.setVisible(expanded);
	}

}
