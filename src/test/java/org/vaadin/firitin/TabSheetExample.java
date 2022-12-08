/*
 * Copyright 2022 Viritin.
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

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.layouts.VTabSheet;


/**
 *
 * @author mstahv
 */
@Route
public class TabSheetExample extends VerticalLayout{

    public TabSheetExample() {
        VTabSheet tabsheet = new VTabSheet();
        tabsheet.setHeight("200px");
        tabsheet.add("foo", new Html("<div>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/>bar<br/></div>"));

        tabsheet.addScrollToEndListener(e -> {
            Notification.show("Scrolled to the end");
        });

        tabsheet.addScrollListener( e -> {
            Notification.show("Scroll pos: " + e.getScrollTop());
        });
        
        add(tabsheet);
        
    }
    
}
