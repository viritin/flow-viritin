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
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.util.WindowScroller;

/**
 *
 * @author mstahv
 */
@Route
public class WindowInfiniteScrollingExample extends VerticalLayout {

    public WindowInfiniteScrollingExample() {
        add(new Html("<pre>LOOOOOOOOOOOODSFDOSOFDSOFOSDFOSOFOSDOFSDFSDKFNLKJSDF SJKLFSJK JKLFSJLKFJKLSDJLKDFJSLKDFJLKSDFJLFDJLKSFDJLKFSDJLK FDSJL KFDSJL KFDSJL KFSDJL KDFSJL KFSDJLK FDSJKL FSDJLK FDSJLK SDFJLK FDSJLK FDSJLK FSDJLSFDJL DSFJL K</pre>"));

        UI ui = UI.getCurrent();

        WindowScroller ws = new WindowScroller(ui);

        Button b = new Button("Scroll top to 1000", e -> {
            ws.setScrollTop(1000);
        });
        add(b);
        b = new Button("Scroll left to 1000", e -> {
            ws.setScrollLeft(1000);
        });
        add(b);

        for (int i = 0; i < 100; i++) {
            add(new Span("Row " + i));
        }

        ws.addScrollListener((int scrollTop, int scrollLeft) -> {
            Notification.show("Scroll position: " + scrollTop + "," + scrollLeft);
        });

        ws.addScrollToEndListener((top, left) -> {
            Notification.show("Scrolled to end position: " + top + "," + left);
            int currentCount = getComponentCount();
            for (int i = currentCount; i < (currentCount + 100); i++) {
                add(new Span("Row (new)" + i));
            }
        });

    }

}
