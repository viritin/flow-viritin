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
package org.vaadin.firitin.components.orderedlayout;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DebounceSettings;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.dom.DebouncePhase;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.shared.Registration;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasSize;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

/**
 * Extended version of the official Scroller component, with some actual
 * scrolling related methods.
 *
 * @author mstahv
 */
public class VScroller extends Scroller implements
        FluentComponent<VScroller>, FluentHasStyle<VScroller>, FluentHasSize<VScroller> {

    private DomListenerRegistration scrollreg;

    @DomEvent(value = "scroll-to-end", debounce = @DebounceSettings(
            timeout = 250,
            phases = DebouncePhase.TRAILING))
    public static class ScrollToEndEvent extends ComponentEvent<VScroller> {

        public ScrollToEndEvent(VScroller source, boolean fromClient) {
            super(source, fromClient);
        }
    }

    public static class ScrollEvent extends ComponentEvent<VScroller> {

        private final int scrollTop;
        private final int scrollLeft;

        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source the source component
         * @param fromClient <code>true</code> if the event originated from the
         * client
         */
        ScrollEvent(VScroller source, int scrollTop, int scrollLeft) {
            super(source, true);
            this.scrollTop = scrollTop;
            this.scrollLeft = scrollLeft;
        }

        public int getScrollLeft() {
            return scrollLeft;
        }

        public int getScrollTop() {
            return scrollTop;
        }

    }

    public VScroller() {
    }

    public VScroller(Component content) {
        super(content);
    }

    public VScroller(Component content, ScrollDirection scrollDirection) {
        super(content, scrollDirection);
    }

    public VScroller(ScrollDirection scrollDirection) {
        super(scrollDirection);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getElement().executeJs("""
            var self = this;
            this.addEventListener("scroll", function(e) {
                // if rather close the the end, fire it!
                if(self.scrollTop + self.clientHeight > (self.scrollHeight - self.clientHeight/4)) {
                    var e = new Event("scroll-to-end");
                    self.dispatchEvent(e);
                }
            });
        """);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    /**
     * Adds a listener that is called when a users scrolls the component to the
     * end of its scrollable area.
     *
     * @param listener the listener
     * @return the {@link Registration} you can use to remove this listener.
     */
    public Registration addScrollToEndListener(ComponentEventListener<ScrollToEndEvent> listener) {
        return addListener(ScrollToEndEvent.class, listener);
    }

    public Registration addScrollListener(ComponentEventListener<ScrollEvent> listener) {
        if (scrollreg == null) {
            scrollreg = getElement().addEventListener("scroll", de -> {
                getEventBus().fireEvent(new ScrollEvent(
                        this,
                        (int) de.getEventData().getNumber("event.target.scrollTop"),
                        (int) de.getEventData().getNumber("event.target.scrollLeft")
                ));
            });
            scrollreg.debounce(100); // use reasonable debouncing
            scrollreg.addEventData("event.target.scrollTop");
            scrollreg.addEventData("event.target.scrollLeft");
        }
        return addListener(ScrollEvent.class, listener);
    }

    public void scrollToTop() {
        setScrollTop(0);
    }

    public void scrollToBottom() {
        getElement().executeJs("this.scrollTop = this.scrollHeight");
    }

    public void setScrollTop(int pixelsFromTop) {
        getElement().executeJs("this.scrollTop = $0", pixelsFromTop);
    }

    public void setScrollLeft(int pixelsFromLeft) {
        getElement().executeJs("this.scrollLeft = $0", pixelsFromLeft);
    }
    
    public void scrollIntoView(Component c) {
        c.scrollIntoView();
    }

}
