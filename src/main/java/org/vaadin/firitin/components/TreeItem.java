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
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.shared.Registration;

/**
 * A very simple component composition to implement a trivial tree component.
 *
 * @author mstahv
 */
public class TreeItem extends Component implements ClickNotifier<TreeItem> {

	private Element expander;
	private boolean expanded = false;
	private Div children;
	private Component nodeContent;
	private Element contentTD;
	private Div contentDiv;

	public TreeItem(Component nodeContent) {
		super(new Element("table"));
		Element tr = new Element("tr");
		
		expander = new Element("td");
		expander.getClassList().add("expander");
		expander.addEventListener("click", e -> toggleNode());

		children = new Div();
		children.setVisible(false);
		
		contentTD = new Element("td");
		contentDiv = new Div();
		contentDiv.addClassName("node-content");
		contentDiv.getElement().appendChild(nodeContent.getElement());
		
		contentTD.appendChild(contentDiv.getElement(), children.getElement());
		
		tr.appendChild(expander, contentTD);
		
		getElement().appendChild(tr);
		this.nodeContent = nodeContent;
	}

	public TreeItem(String stringContent) {
		this(new Div(new Text(stringContent)));
	}

	public TreeItem addChild(Component childComponent) {
		TreeItem i = new TreeItem(childComponent);
		addChild(i);
		return i;
	}
	
	public Component getNodeContent() {
		return nodeContent;
	}
	
	public void addChild(TreeItem treeItem) {
		this.expander.setVisible(true);
		this.children.add(treeItem);
		if(children.getChildren().count() == 1) {
			Icon icon = VaadinIcon.CARET_RIGHT.create();
			expander.appendChild(icon.getElement());
		}
	}

	public TreeItem addChild(String stringContent) {
		return addChild(new Span(stringContent));
	}

	public void removeChild(TreeItem c) {
		this.children.remove(c);
		if(children.getChildren().count() == 0) {
			expander.removeAllChildren();
		}
	}
	
	public void toggleNode() {
		if(children.getChildren().findFirst().isPresent()) {
			expanded = !expanded;
			expander.removeAllChildren();
			expander.appendChild(expanded ? VaadinIcon.CARET_DOWN.create().getElement() : VaadinIcon.CARET_RIGHT.create().getElement());
			children.setVisible(expanded);
		}
	}
	
	public void setSelected(boolean selected) {
		nodeContent.getElement().getClassList().set("selected", selected);
	}
	
	@Override
	public Registration addClickListener(ComponentEventListener<ClickEvent<TreeItem>> listener) {
        return ComponentUtil.addListener(nodeContent, ClickEvent.class,
                (ComponentEventListener) listener);	}
}
