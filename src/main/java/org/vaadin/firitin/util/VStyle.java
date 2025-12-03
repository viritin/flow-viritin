package org.vaadin.firitin.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.dom.Style;
import com.vaadin.signals.Signal;
import in.virit.color.Color;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VStyle implements Style {

    private final Style wrapped;

    public VStyle(Style wrapped) {
        this.wrapped = wrapped;
    }

    public VStyle() {
        this(new TreeMapStyle());
    }

    public static VStyle wrap(Style original) {
        return new VStyle(original);
    }

    public Color getColor() {
        String color = wrapped.get("color");
        if (color == null) {
            return null;
        }
        return Color.parseCssColor(color);
    }

    public VStyle setColor(Color color) {
        return this.set("color", color.toString());
    }

    public Color getBackgroundColor() {
        String color = wrapped.get("background-color");
        if (color == null) {
            return null;
        }
        return Color.parseCssColor(color);
    }

    // Piggyback for the original Style

    public VStyle setBackgroundColor(Color color) {
        return this.set("background-color", color.toString());
    }

    @Override
    public String get(String name) {
        return wrapped.get(name);
    }

    @Override
    public VStyle set(String name, String value) {
        wrapped.set(name, value);
        return this;
    }

    @Override
    public VStyle remove(String name) {
        wrapped.remove(name);
        return this;
    }

    @Override
    public VStyle clear() {
        wrapped.clear();
        return this;
    }

    @Override
    public boolean has(String name) {
        return wrapped.has(name);
    }

    @Override
    public Stream<String> getNames() {
        return wrapped.getNames();
    }

    @Override
    public Style bind(String name, Signal<String> signal) {
        return null;
    }

    /**
     * Applies the styles defined in this VStyle to the given component.
     *
     * @param component the component to which the styles will be applied
     */
    public void apply(Component component) {
        for (String name : getNames().toList()) {
            String value = get(name);
            if (value != null) {
                component.getElement().getStyle().set(name, value);
            }
        }
    }

    void apply(Component scope, String cssSelector, boolean applyToShadowRoot) {
        Runnable task = () -> {
            String style = "[" + getNames().map(name -> String.format("[\"%s\",\"%s\"]", name, get(name)))
                    .collect(Collectors.joining(",\n")) + "]";
            final String querySelectorRoot = applyToShadowRoot ? "this.shadowRoot" : "this";

            // Note, timeout is needed to ensure that the style is applied after the component is rendered
            // and the shadow DOM is available, e.g. with Grid.
            scope.getElement().executeJs("""
                    const style = %s;
                    setTimeout(() => {
                        const qs = %s.querySelectorAll($0);
                        qs.forEach(el => {
                            style.forEach(r => {
                                el.style.setProperty(r[0], r[1]);
                            });
                        });
                    }, 0);
                    """.formatted(style.toString(), querySelectorRoot), cssSelector);
        };
        if(scope.isAttached()) {
            task.run();
        } else {
            scope.addAttachListener(event -> {
                task.run();
                event.unregisterListener();
            });
        }
    }

    /**
     * Applies the styles defined in this VStyle to an element specified by the context component and
     * css selector.
     * <p>
     * Note that the styles are applied to the element after it has been rendered using JS. If you happen
     * to use "preserve on refresh" feature of Vaadin, you might need to call this method again after the
     * refresh, as the styles are not preserved automatically.
     * </p>
     *
     * @param component the component to which the styles will be applied
     * @param cssSelector the CSS selector to target the specific element within the component
     */
    public void apply(Component component, String cssSelector) {
        apply(component, cssSelector, false);
    }

    /**
     * Injects the styles defined in this object to the host page as CSS, for the given CSS selector.
     *
     * @param cssSelectors the CSS selector to target the specific element within the host page
     */
    public void injectWithSelectors(String... cssSelectors) {
        VStyleUtil.inject(toCss(cssSelectors));
    }

    public String toCss(String... cssSelectors) {
        StringBuilder styleBuilder = new StringBuilder();
        styleBuilder.append(Arrays.stream(cssSelectors).collect(Collectors.joining(", ")));
        styleBuilder.append(" {\n");
        getNames().forEach(name -> {
            String value = get(name);
            if (value != null) {
                styleBuilder.append(name).append(": ").append(value).append(";\n");
            }
        });
        styleBuilder.append("}\n");
        return styleBuilder.toString();
    }

    /**
     * Applies the styles defined in this VStyle to an element specified by the context component and
     * css selector, but specifically to the shadow root of the component.
     * <p>
     * Note that the styles are applied to the element after it has been rendered using JS. If you happen
     * to use "preserve on refresh" feature of Vaadin, you might need to call this method again after the
     * refresh, as the styles are not preserved automatically.
     * </p>
     *
     * @param component the component to which the styles will be applie
     * @param cssSelector the CSS selector to target the specific element within the component's shadow root
     */
    public void applyToShadowRoot(Component component, String cssSelector) {
        apply(component, cssSelector, true);
    }
}
