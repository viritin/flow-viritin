package org.vaadin.firitin.util;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.DomListenerRegistration;

import java.util.LinkedList;
import java.util.List;

public class WindowScroller {

    private final UI ui;
    private List<ScrollListener> scrollListener = new LinkedList<>();
    private List<ScrollListener> scrollToEndListener = new LinkedList<>();

    public WindowScroller(UI ui) {
        this.ui = ui;
    }

    public void setScrollTop(int pixels) {
        ui.getElement().executeJs("window.scroll(window.scrollX, $0)", pixels);
    }

    public void setScrollLeft(int pixels) {
        ui.getElement().executeJs("window.scroll($0, window.scrollX)", pixels);
    }

    private void sinkScrollEvents() {

        ui.getElement().executeJs(""
                + "var s = this;\n"
                + "window.addEventListener(\"scroll\", "
                + "function() {\n"
                + " var atScrollEnd = (window.innerHeight + window.scrollY) >= document.body.scrollHeight;\n"
                + " const event = new CustomEvent('windowscroll', { detail : atScrollEnd +','+ window.scrollX+','+window.scrollY});\n"
                + " s.dispatchEvent(event);\n"
                + "}\n"
                + ");\n"
        );

        DomListenerRegistration domlistener = ui.getElement().addEventListener("windowscroll", e -> {
            String[] split = e.getEventData().getString("event.detail").split(",");
            boolean isEnd = Boolean.parseBoolean(split[0]);
            int x = Integer.parseInt(split[1]);
            int y = Integer.parseInt(split[2]);
            if (isEnd) {
                scrollToEndListener.forEach(l -> l.onScroll(y, x));
            }
            scrollListener.forEach(l -> l.onScroll(y, x));
        });
        domlistener.debounce(100);
        domlistener.addEventData("event.detail");
    }

    public void addScrollListener(ScrollListener listener) {
        if (scrollListener.isEmpty() && scrollToEndListener.isEmpty()) {
            sinkScrollEvents();
        }
        this.scrollListener.add(listener);
    }

    ;

    public void addScrollToEndListener(ScrollListener listener) {
        if (scrollListener == null && scrollToEndListener == null) {
            sinkScrollEvents();
        }
        this.scrollToEndListener.add(listener);
    }

    public interface ScrollListener {
        void onScroll(int scrollTop, int scrollLeft);
    }
}
