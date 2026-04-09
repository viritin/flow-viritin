package org.vaadin.firitin.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A helper to detect when components become visible in a scrollable area or the viewport.
 * Provides a Java API around the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/API/IntersectionObserver">IntersectionObserver</a> JS API.
 * Useful for lazy loading, infinite scroll, visibility tracking, and triggering actions
 * when elements scroll into view.
 * <p>
 *     There is one IntersectionObserver instance per UI (for viewport observation) or per root component.
 *     Fetch a viewport-level observer with {@link #of(UI)} or {@link #get()}.
 *     For observing intersection with a specific scrollable container, use {@link #of(UI, Component)}.
 * </p>
 */
public class IntersectionObserver {

    /**
     * Information about a component's intersection with the root.
     *
     * @param isIntersecting whether the target is currently intersecting the root
     * @param intersectionRatio how much of the target is visible (0.0 to 1.0)
     */
    public record IntersectionEntry(boolean isIntersecting, double intersectionRatio) {}

    /**
     * A listener notified when a component's intersection state changes.
     */
    public interface IntersectionListener {
        void onChange(IntersectionEntry entry);
    }

    private record ComponentMapping(int id, Component component, ArrayList<IntersectionListener> listeners) {
        private ComponentMapping(int id, Component component) {
            this(id, component, new ArrayList<>());
        }
    }

    private static final int DEFAULT_DEBOUNCE_TIMEOUT = 100;

    private final Map<Component, Integer> componentToId = new HashMap<>();
    private final Map<Integer, ComponentMapping> idToComponentMapping = new HashMap<>();
    private int nextId = 0;

    private final UI ui;
    private final Element uiElement;
    private final Component root;
    private double[] thresholds = {0};
    private String rootMargin = "0px";
    private int debounceTimeout = DEFAULT_DEBOUNCE_TIMEOUT;

    /**
     * Returns a viewport-level IntersectionObserver for the given UI.
     *
     * @param ui the UI
     * @return the IntersectionObserver for the given UI
     */
    public static IntersectionObserver of(UI ui) {
        IntersectionObserver observer = ComponentUtil.getData(ui, IntersectionObserver.class);
        if (observer == null) {
            observer = new IntersectionObserver(ui, null);
            ComponentUtil.setData(ui, IntersectionObserver.class, observer);
        }
        return observer;
    }

    /**
     * Returns a viewport-level IntersectionObserver for the current UI.
     *
     * @return the IntersectionObserver for the current UI
     */
    public static IntersectionObserver get() {
        return of(UI.getCurrent());
    }

    /**
     * Creates an IntersectionObserver with a specific root component as the intersection root.
     * This is useful for detecting visibility within a scrollable container.
     *
     * @param ui the UI
     * @param root the root component (scrollable container)
     * @return a new IntersectionObserver with the given root
     */
    public static IntersectionObserver of(UI ui, Component root) {
        return new IntersectionObserver(ui, root);
    }

    private IntersectionObserver(UI ui, Component root) {
        this.ui = ui;
        this.uiElement = ui.getElement();
        this.root = root;
        initJs();
    }

    private void initJs() {
        String thresholdArray = buildThresholdArray();
        // The JS callback accumulates entries in a pending map and flushes
        // them to the server after a debounce timeout. This prevents excessive
        // server roundtrips when many elements cross thresholds during scrolling.
        // Only the latest state per element is kept, so rapid intersecting/not-intersecting
        // toggles collapse into the final state.
        String observerCallback = """
                (entries) => {
                  if (!el._ioPending) el._ioPending = {};
                  for (const entry of entries) {
                    if (entry.target._intersectionObserverId !== undefined) {
                      const id = entry.target._intersectionObserverId;
                      el._ioPending[id] = JSON.stringify({isIntersecting: entry.isIntersecting, intersectionRatio: entry.intersectionRatio});
                    }
                  }
                  clearTimeout(el._ioFlushTimer);
                  el._ioFlushTimer = setTimeout(() => {
                    const event = new Event("element-intersection");
                    event.intersections = el._ioPending;
                    el._ioPending = {};
                    el.dispatchEvent(event);
                  }, el._ioDebounce || %d);
                }
                """.formatted(debounceTimeout);

        if (root != null) {
            Runnable init = () -> uiElement.executeJs("""
                    var el = this;
                    var rootEl = $0;
                    var observerKey = '_intersectionObserver_' + (rootEl._ioId || (rootEl._ioId = Math.random().toString(36).slice(2)));
                    el._ioDebounce = $3;
                    el[observerKey] = new IntersectionObserver(""" + observerCallback + """
                    , {root: rootEl, rootMargin: $1, threshold: JSON.parse($2)});
                    el._intersectionObserverKey = observerKey;
                    if (!el._intersectionObserverElements) el._intersectionObserverElements = {};
                    """, root.getElement(), rootMargin, thresholdArray, debounceTimeout);
            if (root.isAttached()) {
                init.run();
            } else {
                root.addAttachListener(e -> {
                    init.run();
                    e.unregisterListener();
                });
            }
        } else {
            uiElement.executeJs("""
                    var el = this;
                    var observerKey = '_intersectionObserver_viewport';
                    el._ioDebounce = $2;
                    el[observerKey] = new IntersectionObserver(""" + observerCallback + """
                    , {rootMargin: $0, threshold: JSON.parse($1)});
                    el._intersectionObserverKey = observerKey;
                    if (!el._intersectionObserverElements) el._intersectionObserverElements = {};
                    """, rootMargin, thresholdArray, debounceTimeout);
        }

        uiElement.addEventListener("element-intersection", event -> {
            var intersections = event.getEventData().get("event.intersections");
            if (intersections == null) return;
            var object = (tools.jackson.databind.node.ObjectNode) intersections;
            for (String idx : object.propertyNames()) {
                String json = object.get(idx).asString();
                try {
                    var node = new tools.jackson.databind.ObjectMapper().readTree(json);
                    boolean isIntersecting = node.get("isIntersecting").asBoolean();
                    double ratio = node.get("intersectionRatio").asDouble();
                    IntersectionEntry entry = new IntersectionEntry(isIntersecting, ratio);
                    ComponentMapping mapping = idToComponentMapping.get(Integer.valueOf(idx));
                    if (mapping != null) {
                        new ArrayList<>(mapping.listeners()).forEach(l -> l.onChange(entry));
                    }
                } catch (Exception e) {
                    Logger.getLogger(IntersectionObserver.class.getName())
                            .warning("Failed to parse intersection entry: " + e.getMessage());
                }
            }
        }).addEventData("event.intersections");
    }

    private String buildThresholdArray() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < thresholds.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(thresholds[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    private ComponentMapping getComponentMapping(Component component) {
        return idToComponentMapping.computeIfAbsent(
                componentToId.computeIfAbsent(component, c -> nextId++),
                id -> mapComponent(component, id));
    }

    private ComponentMapping mapComponent(Component c, Integer id) {
        Runnable register = () -> {
            var componentElement = c.getElement();
            uiElement.executeJs("""
                    const el = $0;
                    const id = $1;
                    const observerKey = this._intersectionObserverKey;
                    if (el instanceof HTMLElement && this[observerKey]) {
                        el._intersectionObserverId = id;
                        this[observerKey].observe(el);
                        this._intersectionObserverElements[id] = el;
                    }
                """, componentElement, id);
        };
        if (c.isAttached()) {
            register.run();
        } else {
            c.addAttachListener(e -> {
                register.run();
                e.unregisterListener();
            });
        }
        c.addDetachListener(e -> {
            if (e.getUI().isClosing()) {
                return;
            }
            idToComponentMapping.remove(id);
            componentToId.remove(c);
            uiElement.executeJs("""
                    const el = this._intersectionObserverElements[$0];
                    const observerKey = this._intersectionObserverKey;
                    if (el && this[observerKey]) {
                        this[observerKey].unobserve(el);
                        delete this._intersectionObserverElements[$0];
                    }
                """, id);
            e.unregisterListener();
        });

        return new ComponentMapping(id, c);
    }

    /**
     * Observe the intersection state of a component. The listener will be called when
     * the component's visibility relative to the root changes.
     *
     * @param component the component to observe
     * @param listener the listener to be notified
     * @return this for chaining
     */
    public IntersectionObserver observe(Component component, IntersectionListener listener) {
        getComponentMapping(component).listeners().add(listener);
        return this;
    }

    /**
     * Stop observing a specific listener for a component.
     *
     * @param component the component
     * @param listener the listener to remove
     * @return this for chaining
     */
    public IntersectionObserver unobserve(Component component, IntersectionListener listener) {
        ComponentMapping mapping = idToComponentMapping.get(componentToId.get(component));
        if (mapping != null) {
            mapping.listeners().remove(listener);
            if (mapping.listeners().isEmpty()) {
                unobserve(component);
            }
        }
        return this;
    }

    /**
     * Stop observing a component entirely.
     *
     * @param component the component to stop observing
     * @return this for chaining
     */
    public IntersectionObserver unobserve(Component component) {
        Integer id = componentToId.remove(component);
        if (id != null) {
            idToComponentMapping.remove(id);
            uiElement.executeJs("""
                    const el = this._intersectionObserverElements[$0];
                    const observerKey = this._intersectionObserverKey;
                    if (el && this[observerKey]) {
                        this[observerKey].unobserve(el);
                        delete this._intersectionObserverElements[$0];
                    }
                """, id);
        }
        return this;
    }

    /**
     * Set the root margin for the intersection observer. This can expand or shrink
     * the root's bounding box before computing intersections.
     * Must be called before {@link #observe(Component, IntersectionListener)}.
     *
     * @param rootMargin the root margin (e.g. "10px", "10px 20px", "10% 20%")
     * @return this for chaining
     */
    public IntersectionObserver withRootMargin(String rootMargin) {
        this.rootMargin = rootMargin;
        return this;
    }

    /**
     * Set the thresholds at which the observer callback fires.
     * Must be called before {@link #observe(Component, IntersectionListener)}.
     *
     * @param thresholds visibility ratio thresholds (0.0 to 1.0)
     * @return this for chaining
     */
    public IntersectionObserver withThresholds(double... thresholds) {
        this.thresholds = thresholds;
        return this;
    }

    /**
     * Set the debounce timeout for intersection events sent to the server.
     * Intersection changes are accumulated on the client side and flushed
     * to the server after this timeout, reducing server roundtrips during
     * rapid scrolling. Only the latest state per element is sent.
     * The default is 100ms.
     *
     * @param timeout the timeout in milliseconds
     * @return this for chaining
     */
    public IntersectionObserver withDebounceTimeout(int timeout) {
        this.debounceTimeout = timeout;
        uiElement.executeJs("this._ioDebounce = $0;", timeout);
        return this;
    }
}
