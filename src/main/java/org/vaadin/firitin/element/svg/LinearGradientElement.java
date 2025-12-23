package org.vaadin.firitin.element.svg;

import in.virit.color.Color;

/**
 * A typed Java API for the SVG {@code <linearGradient>} element.
 * <p>
 * The {@code <linearGradient>} element defines a linear gradient to be used
 * as a fill or stroke for other SVG elements. The gradient is defined along
 * a line specified by x1, y1, x2, y2 coordinates.
 * </p>
 * <p>
 * Linear gradients must be placed inside a {@code <defs>} element and
 * referenced by ID (e.g., fill="url(#myGradient)").
 * </p>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/linearGradient">MDN: linearGradient element</a>
 */
public class LinearGradientElement extends SvgElement {

    public LinearGradientElement() {
        super("linearGradient");
    }

    /**
     * Creates a linear gradient with the given ID.
     *
     * @param id the ID for referencing this gradient
     */
    public LinearGradientElement(String id) {
        super("linearGradient");
        id(id);
    }

    /**
     * Sets the x coordinate of the gradient start point.
     *
     * @param x1 the x coordinate (default is 0)
     * @return this element for method chaining
     */
    public LinearGradientElement x1(double x1) {
        setAttribute("x1", String.valueOf(x1));
        return this;
    }

    /**
     * Sets the x coordinate of the gradient start point with a unit.
     *
     * @param x1 the x coordinate (e.g., "0%", "50%")
     * @return this element for method chaining
     */
    public LinearGradientElement x1(String x1) {
        setAttribute("x1", x1);
        return this;
    }

    /**
     * Sets the y coordinate of the gradient start point.
     *
     * @param y1 the y coordinate (default is 0)
     * @return this element for method chaining
     */
    public LinearGradientElement y1(double y1) {
        setAttribute("y1", String.valueOf(y1));
        return this;
    }

    /**
     * Sets the y coordinate of the gradient start point with a unit.
     *
     * @param y1 the y coordinate (e.g., "0%", "50%")
     * @return this element for method chaining
     */
    public LinearGradientElement y1(String y1) {
        setAttribute("y1", y1);
        return this;
    }

    /**
     * Sets the x coordinate of the gradient end point.
     *
     * @param x2 the x coordinate (default is 1 or 100%)
     * @return this element for method chaining
     */
    public LinearGradientElement x2(double x2) {
        setAttribute("x2", String.valueOf(x2));
        return this;
    }

    /**
     * Sets the x coordinate of the gradient end point with a unit.
     *
     * @param x2 the x coordinate (e.g., "100%", "50%")
     * @return this element for method chaining
     */
    public LinearGradientElement x2(String x2) {
        setAttribute("x2", x2);
        return this;
    }

    /**
     * Sets the y coordinate of the gradient end point.
     *
     * @param y2 the y coordinate (default is 0)
     * @return this element for method chaining
     */
    public LinearGradientElement y2(double y2) {
        setAttribute("y2", String.valueOf(y2));
        return this;
    }

    /**
     * Sets the y coordinate of the gradient end point with a unit.
     *
     * @param y2 the y coordinate (e.g., "100%", "50%")
     * @return this element for method chaining
     */
    public LinearGradientElement y2(String y2) {
        setAttribute("y2", y2);
        return this;
    }

    /**
     * Sets the gradient vector from start to end point.
     * <p>
     * Default is horizontal (0,0 to 1,0).
     * </p>
     *
     * @param x1 start x coordinate
     * @param y1 start y coordinate
     * @param x2 end x coordinate
     * @param y2 end y coordinate
     * @return this element for method chaining
     */
    public LinearGradientElement vector(double x1, double y1, double x2, double y2) {
        x1(x1);
        y1(y1);
        x2(x2);
        y2(y2);
        return this;
    }

    /**
     * Creates a horizontal gradient (left to right).
     *
     * @return this element for method chaining
     */
    public LinearGradientElement horizontal() {
        return vector(0, 0, 1, 0);
    }

    /**
     * Creates a vertical gradient (top to bottom).
     *
     * @return this element for method chaining
     */
    public LinearGradientElement vertical() {
        return vector(0, 0, 0, 1);
    }

    /**
     * Creates a diagonal gradient (top-left to bottom-right).
     *
     * @return this element for method chaining
     */
    public LinearGradientElement diagonal() {
        return vector(0, 0, 1, 1);
    }

    /**
     * Gradient unit options.
     */
    public enum GradientUnits {
        /** Coordinates are relative to the bounding box of the element (default) */
        OBJECT_BOUNDING_BOX("objectBoundingBox"),
        /** Coordinates are in user space units */
        USER_SPACE_ON_USE("userSpaceOnUse");

        private final String value;

        GradientUnits(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Sets the coordinate system for the gradient vector.
     *
     * @param units the gradient units
     * @return this element for method chaining
     */
    public LinearGradientElement gradientUnits(GradientUnits units) {
        setAttribute("gradientUnits", units.toString());
        return this;
    }

    /**
     * Spread method options for gradients.
     */
    public enum SpreadMethod {
        /** Gradient stops at the edges (default) */
        PAD("pad"),
        /** Gradient reflects at the edges */
        REFLECT("reflect"),
        /** Gradient repeats at the edges */
        REPEAT("repeat");

        private final String value;

        SpreadMethod(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Sets how the gradient behaves outside its bounds.
     *
     * @param method the spread method
     * @return this element for method chaining
     */
    public LinearGradientElement spreadMethod(SpreadMethod method) {
        setAttribute("spreadMethod", method.toString());
        return this;
    }

    /**
     * Sets a transform on the gradient.
     *
     * @param transform the transform string
     * @return this element for method chaining
     */
    public LinearGradientElement gradientTransform(String transform) {
        setAttribute("gradientTransform", transform);
        return this;
    }

    /**
     * References another gradient to inherit stops and attributes from.
     *
     * @param href the reference (e.g., "#otherGradient")
     * @return this element for method chaining
     */
    public LinearGradientElement href(String href) {
        setAttribute("href", href);
        return this;
    }

    /**
     * Adds stop elements to this gradient.
     *
     * @param stops the stop elements
     * @return this element for method chaining
     */
    public LinearGradientElement addStops(StopElement... stops) {
        appendChild(stops);
        return this;
    }

    /**
     * Adds a stop at the specified offset with the given color.
     *
     * @param offset the offset (0.0 to 1.0)
     * @param color  the color
     * @return this element for method chaining
     */
    public LinearGradientElement addStop(double offset, Color color) {
        appendChild(new StopElement(offset, color));
        return this;
    }

    /**
     * Adds a stop at the specified offset with the given color.
     *
     * @param offset the offset (0.0 to 1.0)
     * @param color  the color string
     * @return this element for method chaining
     */
    public LinearGradientElement addStop(double offset, String color) {
        appendChild(new StopElement(offset, color));
        return this;
    }
}
