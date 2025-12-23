package org.vaadin.firitin.element.svg;

import in.virit.color.Color;

/**
 * A typed Java API for the SVG {@code <stop>} element.
 * <p>
 * The {@code <stop>} element defines a color and its position in a gradient.
 * It is used inside {@code <linearGradient>} or {@code <radialGradient>} elements.
 * </p>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/stop">MDN: stop element</a>
 */
public class StopElement extends SvgElement {

    public StopElement() {
        super("stop");
    }

    /**
     * Creates a stop element with offset and color.
     *
     * @param offset the offset (0.0 to 1.0 or percentage string like "50%")
     * @param color  the stop color
     */
    public StopElement(double offset, Color color) {
        super("stop");
        offset(offset);
        stopColor(color);
    }

    /**
     * Creates a stop element with offset and color string.
     *
     * @param offset the offset (0.0 to 1.0)
     * @param color  the stop color as string
     */
    public StopElement(double offset, String color) {
        super("stop");
        offset(offset);
        stopColor(color);
    }

    /**
     * Sets the offset where this gradient stop is placed.
     *
     * @param offset the offset from 0.0 (start) to 1.0 (end)
     * @return this element for method chaining
     */
    public StopElement offset(double offset) {
        setAttribute("offset", String.valueOf(offset));
        return this;
    }

    /**
     * Sets the offset where this gradient stop is placed.
     *
     * @param offset the offset as percentage string (e.g., "50%")
     * @return this element for method chaining
     */
    public StopElement offset(String offset) {
        setAttribute("offset", offset);
        return this;
    }

    /**
     * Sets the color at this gradient stop.
     *
     * @param color the stop color
     * @return this element for method chaining
     */
    public StopElement stopColor(Color color) {
        setAttribute("stop-color", color.toString());
        return this;
    }

    /**
     * Sets the color at this gradient stop.
     *
     * @param color the stop color (e.g., "red", "#ff0000", "rgb(255,0,0)")
     * @return this element for method chaining
     */
    public StopElement stopColor(String color) {
        setAttribute("stop-color", color);
        return this;
    }

    /**
     * Sets the opacity at this gradient stop.
     *
     * @param opacity the opacity from 0.0 (transparent) to 1.0 (opaque)
     * @return this element for method chaining
     */
    public StopElement stopOpacity(double opacity) {
        setAttribute("stop-opacity", String.valueOf(opacity));
        return this;
    }
}
