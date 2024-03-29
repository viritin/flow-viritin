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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.orderedlayout.VScroller;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

/**
 * @author mstahv
 */
@Route
public class ScrollerExample extends VVerticalLayout {

    Component row5 = null;

    public ScrollerExample() {

        VerticalLayout content = new VerticalLayout(new Span("first loooooooooooooooooooooooooooooooooooong line"));
        content.setWidth("100vw");

        for (int i = 0; i < 100; i++) {
            final Span span = new Span("Row " + i);
            content.add(span);
            if (i == 5) {
                row5 = span;
            }
        }
        VScroller scroller = new VScroller(content);

        scroller.addScrollToEndListener(e -> {

            Notification.show("Scrolled to the end, adding more content");

            int cc = content.getComponentCount();
            for (int i = cc; i < cc + 10; i++) {
                content.add(new Span("Row (new) " + i));
            }

        });

        scroller.addScrollListener(e -> {
            Notification.show("Scroll position: " + e.getScrollTop() + ", " + e.getScrollLeft());
        });

        scroller.setHeight("300px");
        scroller.setWidth("300px");
        add(scroller);

        Button scrollToTop = new Button("scrollToTop()", e -> scroller.scrollToTop());
        Button scrollToTop2 = new Button("scrollTop(69)", e -> scroller.setScrollTop(69));
        Button scrollLeft = new Button("setScrollLeft(40)", e -> scroller.setScrollLeft(40));
        Button toView = new Button("scrollIntoView(row5)", e -> scroller.scrollIntoView(row5));
        add(scrollToTop, scrollToTop2, scrollLeft, toView);

    }

}
