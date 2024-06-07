package org.vaadin.firitin.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.dom.Element;
import elemental.json.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A helper to detect and observe changes for the size of the given component.
 * Allows you for example to easily configure Grid columns for different
 * devices or swap component implementations based on the screen size/orientation.
 *
 */
public class ResizeObserver {

    private static ObjectMapper om = new ObjectMapper();

    private final Element el;
    private final Map<Component,List<SizeHandler>> listeners = new HashMap<>();
    private final List<Component> components = new ArrayList<>();
    private final DomListenerRegistration reg;

    public interface SizeHandler {
        void onSizeObservation(Dimensions observation);
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

    private static Map<Component, ResizeObserver> uiToObserver = Collections.synchronizedMap(new WeakHashMap<>());

    public static ResizeObserver of(UI ui) {
        return uiToObserver.computeIfAbsent(ui, c -> new ResizeObserver(ui));
    }

    public static ResizeObserver get() {
        return ResizeObserver.of(UI.getCurrent());
    }

    private ResizeObserver(UI ui) {
        components.add(ui);
        this.el = ui.getElement();
        el.executeJs("""
                var el = this;
                el._resizeObserverElements = [];
                const resizeObserver = new ResizeObserver((entries) => {
                  const sizes = {};
                  for (const entry of entries) {
                    if (entry.contentBoxSize) {
                      const idx = el._resizeObserverElements.indexOf(entry.target);
                      const contentBoxSize = entry.contentBoxSize[0];
                      sizes[idx] = JSON.stringify(entry.contentRect);
                    }
                  }
                  const event = new Event("element-resize");
                  event.dimensions = sizes;
                  el.dispatchEvent(event);
                });
                el._resizeObserver = resizeObserver;
                // Always observe UI element !?!
                el._resizeObserverElements.push(el);
                resizeObserver.observe(el);
                """);
        reg = el.addEventListener("element-resize", event -> {
                    JsonObject object = event.getEventData().getObject("event.dimensions");
                    for(String idx : object.keys()) {
                        String json = object.getString(idx);
                        try {
                            Dimensions dimensions = om.readValue(json, Dimensions.class);
                            Component component = components.get(Integer.valueOf(idx));
                            new ArrayList<>(listeners.getOrDefault(component, Collections.emptyList()))
                                    .forEach(l -> l.onSizeObservation(dimensions));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .addEventData("event.dimensions")
                .debounce(100); // Wait a tiny bit for a pause while resizing, otherwise it will choke the connection for no reason...
    }

    public ResizeObserver observe(Component component, SizeHandler listener) {
        observeComponentChanges(component);
        listeners.computeIfAbsent(component, c -> new ArrayList<>()).add(listener);
        return this;
    }

    private ResizeObserver observeComponentChanges(Component... additionalComponentsToObserve) {
        for(Component c : additionalComponentsToObserve) {
            if(!components.contains(c)) {
                components.add(c);
                el.executeJs("""
                    this._resizeObserverElements.push($0);
                    this._resizeObserver.observe($0);
                """, c.getElement()).then(jsonvalue -> {}, s -> {
                    throw new RuntimeException("Error adding size observer, component not attached!?");
                });
            }
        }
        return this;
    }

    public ResizeObserver withDebounceTimeout(int timeout) {
        reg.debounce(timeout);
        return this;
    }

}
