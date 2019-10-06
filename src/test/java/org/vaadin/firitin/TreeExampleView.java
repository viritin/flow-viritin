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

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.hibernate.validator.internal.engine.ValueContext.ValueState;
import org.vaadin.firitin.components.Tree;

import org.vaadin.firitin.components.TreeItem;
import org.vaadin.firitin.testdomain.Dude;

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
        
        
        add(new H2("Tree:"));
        
        Random random = new Random(0);
        
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
