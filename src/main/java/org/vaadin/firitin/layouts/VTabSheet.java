package org.vaadin.firitin.layouts;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.dom.DomListenerRegistration;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.shared.Registration;

/**
 * 
 * A TabSheet component that behaves in the same way as the familiar Vaadin 7,8.
 * 
 * It provides a layer on top of {@link Tabs} which handles component change on {@link Tab} clicks automatically.
 * 
 * @author mmerruko
 *
 */
public class VTabSheet extends TabSheet {

    private DomListenerRegistration scrollreg;

    public static class ScrollToEndEvent extends ComponentEvent<VTabSheet> {

        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source the source component
         */
        ScrollToEndEvent(VTabSheet source) {
            super(source, true);
        }
    }

    public static class ScrollEvent extends ComponentEvent<VTabSheet> {

        private final int scrollTop;
        private final int scrollLeft;

        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source the source component
         */
        ScrollEvent(VTabSheet source, String details) {
            super(source, true);
            String[] split = details.split(",");
            this.scrollTop = Integer.parseInt(split[0]);
            this.scrollLeft = Integer.parseInt(split[1]);
        }

        public int getScrollLeft() {
            return scrollLeft;
        }

        public int getScrollTop() {
            return scrollTop;
        }

    }

    public VTabSheet() {
    }

    public Tab addTab(String caption, Component component) {
        return add(caption, component);
    }

    public void removeTab(Tab tab) {
        remove(tab);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getElement().executeJs(
                "var self = this;var el = this.shadowRoot.querySelector(\"vaadin-tabsheet-scroller\");\n"
                        + "    el.addEventListener(\"scroll\", function(e) {\n"
                        + "        if(el.scrollTop + el.clientHeight == el.scrollHeight) {\n"
                        + "            self.$server.onScrollToEnd();\n"
                        + "        }\n"
                        + "    });\n"
        );
    }

    @ClientCallable
    private void onScrollToEnd() {
        getEventBus().fireEvent(new ScrollToEndEvent(this));
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
            getElement().executeJs("" +
                    "var el = this;" +
                    "this.shadowRoot.querySelector(\"vaadin-tabsheet-scroller\")" +
                    ".addEventListener('scroll', e => {" +
                    "const event = new CustomEvent('myscroll', { detail: e.target.scrollTop + ',' + e.target.scrollLeft });\n" +
                    "el.dispatchEvent(event);" +
                    "})");
            scrollreg = getElement().addEventListener("myscroll", (DomEvent de) -> {
                getEventBus().fireEvent(new ScrollEvent(
                        this,
                        (String) de.getEventData().getString("event.detail")
                ));
            });
            scrollreg.debounce(100); // use reasonable debouncing
            scrollreg.addEventData("event.detail");
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
