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
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

/**
 * A very simple component composition to implement a trivial tree component.
 *
 * @author mstahv
 */
public class TreeItem extends Composite<HorizontalLayout> implements ComponentEventListener<ClickEvent<Div>> {

	private Div expander;
	private boolean expanded = false;
	private VerticalLayout children;
	private Registration clickListenerReg;

	public TreeItem(Component c) {
		super();
		expander = new Div();
		expander.setWidth("3em");

		children = new VerticalLayout();
		children.setPadding(false);
		children.setVisible(false);

		VerticalLayout verticalLayout = new VerticalLayout(c, children);
		
		verticalLayout.setPadding(false);
		verticalLayout.setSpacing(false);
		getContent().add(expander, verticalLayout);
		
		getContent().setPadding(false);
		getContent().setSpacing(false);
		getContent().setFlexGrow(0, expander);
		
	}

	public TreeItem(String stringContent) {
		this(new Span(stringContent));
	}

	public TreeItem addChild(Component childComponent) {
		TreeItem i = new TreeItem(childComponent);
		this.expander.setVisible(true);
		this.children.add(i);
		if(children.getComponentCount() == 1) {
			expander.add(VaadinIcon.CARET_RIGHT.create());
			clickListenerReg = expander.addClickListener(this);
		}
		return i;
	}

	public TreeItem addChild(String stringContent) {
		return addChild(new Span(stringContent));
	}

	public void removeChild(TreeItem c) {
		this.children.remove(c);
		if(children.getComponentCount() == 0) {
			expander.removeAll();
			clickListenerReg.remove();
		}
	}

	@Override
	public void onComponentEvent(ClickEvent<Div> e) {
		expanded = !expanded;
		expander.removeAll();
		expander.add(expanded ? VaadinIcon.CARET_DOWN.create() : VaadinIcon.CARET_RIGHT.create());
		children.setVisible(expanded);
	}

}
