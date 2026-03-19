package org.vaadin.firitin.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.dom.Style;
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
        return this.set("color", color == null ? null : color.toString());
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
        return this.set("background-color", color == null ? null : color.toString());
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


    // ========== Typed Pixel Helpers ==========

    // Position properties

    /**
     * Sets the CSS {@code left} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setLeftPx(Number pixels) {
        if (pixels != null) {
            setLeft(pixels + "px");
        } else {
            setLeft(null);
        }
    }

    /**
     * Sets the CSS {@code top} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setTopPx(Number pixels) {
        if (pixels != null) {
            setTop(pixels + "px");
        } else {
            setTop(null);
        }
    }

    /**
     * Sets the CSS {@code right} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setRightPx(Number pixels) {
        if (pixels != null) {
            setRight(pixels + "px");
        } else {
            setRight(null);
        }
    }

    /**
     * Sets the CSS {@code bottom} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setBottomPx(Number pixels) {
        if (pixels != null) {
            setBottom(pixels + "px");
        } else {
            setBottom(null);
        }
    }

    // Dimension properties

    /**
     * Sets the CSS {@code width} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setWidthPx(Number pixels) {
        if (pixels != null) {
            setWidth(pixels + "px");
        } else {
            setWidth(null);
        }
    }

    /**
     * Sets the CSS {@code height} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setHeightPx(Number pixels) {
        if (pixels != null) {
            setHeight(pixels + "px");
        } else {
            setHeight(null);
        }
    }

    /**
     * Sets the CSS {@code min-width} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setMinWidthPx(Number pixels) {
        if (pixels != null) {
            setMinWidth(pixels + "px");
        } else {
            setMinWidth(null);
        }
    }

    /**
     * Sets the CSS {@code max-width} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setMaxWidthPx(Number pixels) {
        if (pixels != null) {
            setMaxWidth(pixels + "px");
        } else {
            setMaxWidth(null);
        }
    }

    /**
     * Sets the CSS {@code min-height} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setMinHeightPx(Number pixels) {
        if (pixels != null) {
            setMinHeight(pixels + "px");
        } else {
            setMinHeight(null);
        }
    }

    /**
     * Sets the CSS {@code max-height} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setMaxHeightPx(Number pixels) {
        if (pixels != null) {
            setMaxHeight(pixels + "px");
        } else {
            setMaxHeight(null);
        }
    }

    // Margin properties

    /**
     * Sets the CSS {@code margin} property in pixels (all sides).
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setMarginPx(Number pixels) {
        if (pixels != null) {
            setMargin(pixels + "px");
        } else {
            setMargin(null);
        }
    }

    /**
     * Sets the CSS {@code margin-top} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setMarginTopPx(Number pixels) {
        if (pixels != null) {
            setMarginTop(pixels + "px");
        } else {
            setMarginTop(null);
        }
    }

    /**
     * Sets the CSS {@code margin-right} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setMarginRightPx(Number pixels) {
        if (pixels != null) {
            setMarginRight(pixels + "px");
        } else {
            setMarginRight(null);
        }
    }

    /**
     * Sets the CSS {@code margin-bottom} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setMarginBottomPx(Number pixels) {
        if (pixels != null) {
            setMarginBottom(pixels + "px");
        } else {
            setMarginBottom(null);
        }
    }

    /**
     * Sets the CSS {@code margin-left} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setMarginLeftPx(Number pixels) {
        if (pixels != null) {
            setMarginLeft(pixels + "px");
        } else {
            setMarginLeft(null);
        }
    }

    // Padding properties

    /**
     * Sets the CSS {@code padding} property in pixels (all sides).
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setPaddingPx(Number pixels) {
        if (pixels != null) {
            setPadding(pixels + "px");
        } else {
            setPadding(null);
        }
    }

    /**
     * Sets the CSS {@code padding-top} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setPaddingTopPx(Number pixels) {
        if (pixels != null) {
            setPaddingTop(pixels + "px");
        } else {
            setPaddingTop(null);
        }
    }

    /**
     * Sets the CSS {@code padding-right} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setPaddingRightPx(Number pixels) {
        if (pixels != null) {
            setPaddingRight(pixels + "px");
        } else {
            setPaddingRight(null);
        }
    }

    /**
     * Sets the CSS {@code padding-bottom} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setPaddingBottomPx(Number pixels) {
        if (pixels != null) {
            setPaddingBottom(pixels + "px");
        } else {
            setPaddingBottom(null);
        }
    }

    /**
     * Sets the CSS {@code padding-left} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setPaddingLeftPx(Number pixels) {
        if (pixels != null) {
            setPaddingLeft(pixels + "px");
        } else {
            setPaddingLeft(null);
        }
    }

    // Gap properties (flexbox/grid)

    /**
     * Sets the CSS {@code gap} property in pixels.
     * <p>
     * This property is used with flexbox and grid layouts to set the spacing
     * between items.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setGapPx(Number pixels) {
        if (pixels != null) {
            set("gap", pixels + "px");
        } else {
            remove("gap");
        }
    }

    /**
     * Sets the CSS {@code row-gap} property in pixels.
     * <p>
     * This property is used with flexbox and grid layouts to set the spacing
     * between rows.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setRowGapPx(Number pixels) {
        if (pixels != null) {
            set("row-gap", pixels + "px");
        } else {
            remove("row-gap");
        }
    }

    /**
     * Sets the CSS {@code column-gap} property in pixels.
     * <p>
     * This property is used with flexbox and grid layouts to set the spacing
     * between columns.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setColumnGapPx(Number pixels) {
        if (pixels != null) {
            set("column-gap", pixels + "px");
        } else {
            remove("column-gap");
        }
    }

    // Border properties

    /**
     * Sets the CSS {@code border-width} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setBorderWidthPx(Number pixels) {
        if (pixels != null) {
            set("border-width", pixels + "px");
        } else {
            remove("border-width");
        }
    }

    /**
     * Sets the CSS {@code border-radius} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setBorderRadiusPx(Number pixels) {
        if (pixels != null) {
            set("border-radius", pixels + "px");
        } else {
            remove("border-radius");
        }
    }

    // Font/text properties

    /**
     * Sets the CSS {@code font-size} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setFontSizePx(Number pixels) {
        if (pixels != null) {
            setFontSize(pixels + "px");
        } else {
            setFontSize(null);
        }
    }

    /**
     * Sets the CSS {@code line-height} property in pixels.
     *
     * @param pixels the value in pixels, or {@code null} to clear the property
     */
    public void setLineHeightPx(Number pixels) {
        if (pixels != null) {
            setLineHeight(pixels + "px");
        } else {
            setLineHeight(null);
        }
    }
}
