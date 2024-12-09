package org.vaadin.firitin.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.shared.Registration;
import elemental.json.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;

/**
 * A helper to detect and observe size changes of components. Provides a Java API around the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/API/ResizeObserver">ResizeObserver</a> JS API.
 * Allows you for example to easily configure Grid columns for different
 * devices or swap component implementations based on the screen size/orientation.
 * <p>
 *     When you start to observe a component size, the initial size is reported immediately.
 *     So unlike with the Page#addBrowserWindowResizeListener, you don't need to wait for the first resize event
 *     or to combine it with the Page#retrieveExtendedClientDetails to get the initial size.
 * </p>
 * <p>
 *     There is one ResizeObserver instance per UI, but your listeners are attached to a specific component.
 *     As the current version of Vaadin does not support extending UI, but this API is designed to be
 *     UI specific, fetch a ResizeObserver instance with {@link #of(UI)} or {@link #get()} (this uses
 *     {@link UI#getCurrent()} ).
 * </p>
 * <p>
 *     There are two ways to listen to size changes:
 * </p>
 * <ul>
 *     <li>Using the {@link #addResizeListener(Component, ComponentEventListener)} method, which is a Vaadin core
 *     style API, the listener gets {@link SizeChangeEvent}. and the return value is a {@link Registration} you can
 *     use to stop listening.</li>
 *     <li>Using the {@link #observe(Component, SizeChangeListener)} method, your listener simply receives the
 *     {@link Dimensions} of the listened component.</li>
 * </ul>
 */
public class ResizeObserver {

    /**
     * Event fired when the size of a component changes.
     */
    public static class SizeChangeEvent extends ComponentEvent<UI> {
        private final Component component;
        private final Dimensions dimensions;

        public SizeChangeEvent(UI ui, Component component, Dimensions dimensions) {
            super(ui, true);
            this.component = component;
            this.dimensions = dimensions;
        }

        /**
         * @return the component that was resized
         */
        public Component getComponent() {
            return component;
        }

        /**
         * @return the new width of the component in pixels
         */
        public int getWidht() {
            return dimensions.width();
        }

        /**
         * @return the new height of the component in pixels
         */
        public int getHeight() {
            return dimensions.height();
        }

        /**
         * @return the new dimensions of the component
         */
        public Dimensions getDimensions() {
            return dimensions;
        }
    }

    /**
     * A simple listener notified when the size of a component changes.
     */
    public interface SizeChangeListener {
        void onChange(Dimensions observation);
    }

    /**
     * A record that describes the size and position of a component. Serialized from the browsers
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/DOMRectReadOnly">DOMRectReadOnly</a>
     *
     * @param x the x coordinate of the DOMRectReadOnly's origin.
     * @param y the y coordinate of the DOMRectReadOnly's origin.
     * @param width the width of the DOMRectReadOnly.
     * @param height the height of the DOMRectReadOnly.
     * @param top the top coordinate value of the DOMRectReadOnly (usually the same as y).
     * @param right the right coordinate value of the DOMRectReadOnly (usually the same as x + width).
     * @param bottom the bottom coordinate value of the DOMRectReadOnly (usually the same as y + height).
     * @param left the left coordinate value of the DOMRectReadOnly (usually the same as x).
     */
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

    private record ComponentMapping(int id, Component component, ArrayList<SizeChangeListener> listeners) {
        private ComponentMapping(int id, Component component) {
            this(id, component, new ArrayList<>());
        }
    }

    private Map<Component,Integer> componentToId = new HashMap<>();
    private Map<Integer,ComponentMapping> idToComponentMapping = new HashMap<>();
    private int nextId = 0;

    private static ObjectMapper om = new ObjectMapper();

    private final UI ui;
    private final Element uiElement;
    private final DomListenerRegistration reg;

    /**
     * @param ui the UI whose ResizeObserver you want to use
     * @return the ResizeObserver for the given UI
     */
    public static ResizeObserver of(UI ui) {
        ResizeObserver resizeObserver = ComponentUtil.getData(ui, ResizeObserver.class);
        if(resizeObserver == null) {
            resizeObserver = new ResizeObserver(ui);
            ComponentUtil.setData(ui, ResizeObserver.class, resizeObserver);
        }
        return resizeObserver;
    }

    /**
     * @return the ResizeObserver for the current UI
     */
    public static ResizeObserver get() {
        return ResizeObserver.of(UI.getCurrent());
    }

    private ResizeObserver(UI ui) {
        this.ui = ui;
        this.uiElement = ui.getElement();
        uiElement.executeJs("""
                var el = this;
                el._resizeObserver = new ResizeObserver((entries) => {
                  const sizes = {};
                  for (const entry of entries) {
                    if (entry.target.isConnected && entry.contentBoxSize) {
                      const id = entry.target._resizeObserverId;
                      const contentBoxSize = entry.contentBoxSize[0];
                      sizes[id] = JSON.stringify(entry.contentRect);
                    } else {
                      console.log("Ignoring resize event for detached element " + entry.target._resizeObserverId +  ", TODO: cleanup??");
                    }
                  }
                  const event = new Event("element-resize");
                  event.dimensions = sizes;
                  el.dispatchEvent(event);
                });
                el._resizeObserverElements = {};
                """);
        reg = uiElement.addEventListener("element-resize", event -> {
                    JsonObject object = event.getEventData().getObject("event.dimensions");
                    for(String idx : object.keys()) {
                        String json = object.getString(idx);
                        try {
                            Dimensions dimensions = om.readValue(json, Dimensions.class);
                            ComponentMapping componentMapping = idToComponentMapping.get(Integer.valueOf(idx));
                            if(componentMapping != null) {
                                // Old deprecated API
                                new ArrayList<>(componentMapping.listeners()).forEach(l -> l.onChange(dimensions));
                                // Vaadin core style API
                                SizeChangeEvent sizeChangeEvent = new SizeChangeEvent(ui, componentMapping.component, dimensions);
                                // Ugly but I guess there is no other way to fire an event from UI
                                ComponentUtil.fireEvent(ui, sizeChangeEvent);
                            } else {
                                // Timing issue in Flow navigation can make this happen, simply ignore
                                Logger.getLogger(ResizeObserver.class.getName()).fine("Resize listener called for component that is already de-registered, id:" + idx);
                            }
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .addEventData("event.dimensions")
                .debounce(100); // Wait a tiny bit for a pause while resizing, otherwise it will choke the connection for no reason...
    }

    private ComponentMapping getComponentMapping(Component component) {
        return idToComponentMapping.computeIfAbsent(
                componentToId.computeIfAbsent(component, c -> nextId++), id -> mapComponent(component, id));
    }

    private ComponentMapping mapComponent(Component c, Integer id) {
        Runnable register = () -> {
            var componentElement = c.getElement();
            uiElement.executeJs("""
                    const el = $0;
                    const id = $1;
                    if(el instanceof HTMLElement) {
                        el._resizeObserverId = id;
                        this._resizeObserver.observe(el);
                        this._resizeObserverElements[id] = el;
                    } else {
                        throw new Error("el not Element, Flow bug?");
                    }
                """, componentElement, id).then(jsonvalue -> {
            });
        };
        if(c.isAttached()) {
            register.run();
        } else {
            c.addAttachListener(e -> {
                register.run();
                e.unregisterListener();
            });
        }
        c.addDetachListener(e -> {
            if(e.getUI().isClosing()) {
                // UI itself is being closed, no need to do clean up
                return;
            }
            idToComponentMapping.remove(id);
            componentToId.remove(c);
            // Note sure how browsers have implemented ResizeObserver, but manually cleaning
            // up elements registered to the observer to avoid memory leaks
            uiElement.executeJs("""
                    const el = this._resizeObserverElements[$0];
                    if(el) {
                        delete this._resizeObserverElements[$0];
                        this._resizeObserver.unobserve(el);
                    }
                """, id);
            e.unregisterListener();
        });

        return new ComponentMapping(id, c);
    }

    /**
     * Adds a listener to be notified when the size of the given component changes. Also the initial
     * size is reported immediately.
     *
     * @param component the component to observe
     * @param listener the listener to be notified
     * @return a registration that can be used to stop listening
     */
    public Registration addResizeListener(Component component, ComponentEventListener<SizeChangeEvent> listener) {
        SizeChangeListener sizeChangeListener = d -> listener.onComponentEvent(new SizeChangeEvent(ui, component, d));
        getComponentMapping(component).listeners().add(sizeChangeListener);
        return () -> {
            ComponentMapping componentMapping = getComponentMapping(component);
            componentMapping.listeners().remove(sizeChangeListener);
            if(componentMapping.listeners().isEmpty()) {
                // Do cleanup if no listeners left
                unobserve(component);
            }
        };
    }


    /**
     * Observe the size of a component. The listener will be notified when the size of the component changes.
     * @param component the component to observe
     * @param listener the listener to be notified
     * @return this for chaining
     */
    public ResizeObserver observe(Component component, SizeChangeListener listener) {
        getComponentMapping(component).listeners().add(listener);
        return this;
    }

    /**
     * Stop observing the size of a component.
     *
     * @param component the component to stop observing
     * @param listener the listener to remove
     * @return this for chaining
     */
    public ResizeObserver unobserve(Component component, SizeChangeListener listener) {
        ComponentMapping componentMapping = getComponentMapping(component);
        componentMapping.listeners().remove(listener);
        if(componentMapping.listeners().isEmpty()) {
            unobserve(component);
        }
        return this;
    }

    /**
     * Stop observing the size of a component.
     *
     * @param component the component to stop observing
     * @return this for chaining
     */
    private ResizeObserver unobserve(Component component) {
        ComponentMapping componentMapping = getComponentMapping(component);
        idToComponentMapping.remove(componentMapping.id());
        componentToId.remove(component);
        uiElement.executeJs("""
                const el = this._resizeObserverElements[$0];
                if(el) {
                    delete this._resizeObserverElements[$0];
                    this._resizeObserver.unobserve(el);
                }
            """, componentMapping.id());
        return this;
    }

    /**
     * Set a debounce timeout for the resize events. This can be useful if you want to avoid
     * doing heavy operations on every resize event. The default is 100ms
     *
     * @param timeout the timeout in milliseconds
     * @return this for chaining
     */
    public ResizeObserver withDebounceTimeout(int timeout) {
        reg.debounce(timeout);
        return this;
    }

}
