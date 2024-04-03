package org.vaadin.firitin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

import java.util.ArrayList;
import java.util.List;

@Route
public class ResizeObserverView extends VVerticalLayout {

    public ResizeObserverView() {
        add("TODO description why you should not use Page resize listener");

        ResizeObserver.of(UI.getCurrent()).addResizeListener(sizeDetails -> {
            Notification.show("Hello. New Size is " + sizeDetails);
        });

    }

    public static class ResizeObserver {

        private final Element el;
        private final List<ResizeListener> listeners = new ArrayList<>();
        private final DomListenerRegistration reg;

        public interface ResizeListener {
            void onResize(Dimensions sizeDetails);
        }

        public record Dimensions(
                int x,
                int y,
                int width,
                int height,
                int top,
                int right,
                int bottom,
                int left
        ) {}

        public static ResizeObserver of(Component component) {
            return new ResizeObserver(component.getElement());
        }

        public static ResizeObserver observe(Component component, ResizeListener listener) {
            return of(component).addResizeListener(listener);
        }

        private ResizeObserver(Element element) {
            this.el = element;
            el.executeJs("""
                var el = this;
                const resizeObserver = new ResizeObserver((entries) => {
                  for (const entry of entries) {
                    if (entry.contentBoxSize) {
                      const contentBoxSize = entry.contentBoxSize[0];
                      const event = new Event("element-resize");
                      event.dimensions = JSON.stringify(entry.contentRect);
                      el.dispatchEvent(event);
                    }
                  }
                });
                resizeObserver.observe(el);
                """);
            reg = el.addEventListener("element-resize", event -> {
                String json = event.getEventData().getString("event.dimensions");
                    try {
                        Dimensions dimensions = new ObjectMapper().readValue(json, Dimensions.class);
                        new ArrayList<>(listeners).forEach(l -> l.onResize(dimensions));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
            })
                    .addEventData("event.dimensions")
                    .debounce(100); // Wait a tiny bit for a pause while resizing, otherwise it will choke the connection for no reason...

        }

        public ResizeObserver addResizeListener(ResizeListener listener) {
            listeners.add(listener);
            return this;
        }

        public void removeListener(ResizeListener listener) {
            listeners.remove(listener);
        }

        public ResizeObserver withDebounceTimeout(int timeout) {
            reg.debounce(timeout);
            return this;
        }

        public void clear() {
            listeners.clear();
            reg.remove();
        }

    }
}
