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
package org.vaadin.firitin;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.vaadin.firitin.components.Tree;
import org.vaadin.firitin.components.TreeItem;
import org.vaadin.firitin.testdomain.Dude;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 *
 * @author mstahv
 */
@Route
public class TreeExampleView extends VerticalLayout {
    
    public TreeExampleView() {
        
        add(new H2("Low level tree item usage..."));
        TreeItem root = new TreeItem("Moro");
        TreeItem poro = root.addChild(new Span("Poro!"));
        
        poro.addChild(new Span("Vasa"));
        
        add(root);
        
        
        Random random = new Random(0);

        add(new H2("Tree:"));
        
        Tree<Dude> dudeTree = new Tree<>();

        // Configure how items labels are generated
        dudeTree.setItemLabelGenerator(item -> item.getFirstName());
        // Configure an icon for items
        dudeTree.setItemIconGenerator(item ->  {
        	VaadinIcon[] values = VaadinIcon.values();
        	return values[random.nextInt(values.length)].create();
        });
        
        // following would completely customise what is used as "node representation"
        // This also overrides itemLabelGenerator and itemIconGenerator
        // dudeTree.setItemGenerator(item -> new Div());
        
        // we can also use ItemDecorator to customize the generated tree nodes
        // here, we add context menu to the content part (icon + caption)
        dudeTree.addItemDecorator((dude, treeItem) -> {
			Component nodeContent = treeItem.getNodeContent();
			
		    ContextMenu contextMenu = new ContextMenu();
		    
		    contextMenu.addItem("First menu item for " + dude.getFirstName(),
		            e1 -> Notification.show("Clicked on the first item"));

		    contextMenu.addItem("Second menu item",
		            e2 -> Notification.show("Clicked on the second item"));

		    // The created MenuItem component can be saved for later use
		    MenuItem item = contextMenu.addItem("Disabled menu item",
		            e3 -> Notification.show("This cannot happen"));
		    item.setEnabled(false);
		    
		    contextMenu.setTarget(nodeContent);
			
		});
        
        // Now actually populate the tree, assign a list of root nodes and 
        // a strategy to get children
        dudeTree.setItems(getRootNodes(), Dude::getSubordinates);
        
        add(dudeTree);
        
    }
    
    private List<Dude> getRootNodes() {
        Dude ceo = new Dude("Joonas");
        
        Dude cfo = new Dude("Jurka");
        ceo.getSubordinates().add(cfo);
        
        Dude vpom = new Dude("Niko");
        ceo.getSubordinates().add(vpom);

        Dude community = new Dude("Marcus");
        vpom.getSubordinates().add(community);

        return Arrays.asList(ceo);
    }
    
}
