package org.vaadin.firitin.element.svg;

import in.virit.color.Color;

/**
 * A typed Java API for the SVG {@code <radialGradient>} element.
 * <p>
 * The {@code <radialGradient>} element defines a radial gradient to be used
 * as a fill or stroke for other SVG elements. The gradient radiates from
 * a focal point to the edge of a circle.
 * </p>
 * <p>
 * Radial gradients must be placed inside a {@code <defs>} element and
 * referenced by ID (e.g., fill="url(#myGradient)").
 * </p>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/radialGradient">MDN: radialGradient element</a>
 */
public class RadialGradientElement extends SvgElement {

    public RadialGradientElement() {
        super("radialGradient");
    }

    /**
     * Creates a radial gradient with the given ID.
     *
     * @param id the ID for referencing this gradient
     */
    public RadialGradientElement(String id) {
        super("radialGradient");
        id(id);
    }

    /**
     * Sets the x coordinate of the gradient circle center.
     *
     * @param cx the x coordinate (default is 0.5 or 50%)
     * @return this element for method chaining
     */
    public RadialGradientElement cx(double cx) {
        setAttribute("cx", String.valueOf(cx));
        return this;
    }

    /**
     * Sets the x coordinate of the gradient circle center with a unit.
     *
     * @param cx the x coordinate (e.g., "50%")
     * @return this element for method chaining
     */
    public RadialGradientElement cx(String cx) {
        setAttribute("cx", cx);
        return this;
    }

    /**
     * Sets the y coordinate of the gradient circle center.
     *
     * @param cy the y coordinate (default is 0.5 or 50%)
     * @return this element for method chaining
     */
    public RadialGradientElement cy(double cy) {
        setAttribute("cy", String.valueOf(cy));
        return this;
    }

    /**
     * Sets the y coordinate of the gradient circle center with a unit.
     *
     * @param cy the y coordinate (e.g., "50%")
     * @return this element for method chaining
     */
    public RadialGradientElement cy(String cy) {
        setAttribute("cy", cy);
        return this;
    }

    /**
     * Sets the radius of the gradient circle.
     *
     * @param r the radius (default is 0.5 or 50%)
     * @return this element for method chaining
     */
    public RadialGradientElement r(double r) {
        setAttribute("r", String.valueOf(r));
        return this;
    }

    /**
     * Sets the radius of the gradient circle with a unit.
     *
     * @param r the radius (e.g., "50%")
     * @return this element for method chaining
     */
    public RadialGradientElement r(String r) {
        setAttribute("r", r);
        return this;
    }

    /**
     * Sets the x coordinate of the focal point.
     * <p>
     * The focal point is where the gradient starts (0% stop).
     * If not specified, defaults to the center (cx).
     * </p>
     *
     * @param fx the x coordinate
     * @return this element for method chaining
     */
    public RadialGradientElement fx(double fx) {
        setAttribute("fx", String.valueOf(fx));
        return this;
    }

    /**
     * Sets the x coordinate of the focal point with a unit.
     *
     * @param fx the x coordinate (e.g., "25%")
     * @return this element for method chaining
     */
    public RadialGradientElement fx(String fx) {
        setAttribute("fx", fx);
        return this;
    }

    /**
     * Sets the y coordinate of the focal point.
     *
     * @param fy the y coordinate
     * @return this element for method chaining
     */
    public RadialGradientElement fy(double fy) {
        setAttribute("fy", String.valueOf(fy));
        return this;
    }

    /**
     * Sets the y coordinate of the focal point with a unit.
     *
     * @param fy the y coordinate (e.g., "25%")
     * @return this element for method chaining
     */
    public RadialGradientElement fy(String fy) {
        setAttribute("fy", fy);
        return this;
    }

    /**
     * Sets the radius of the focal point circle.
     * <p>
     * This creates a focal ring instead of a focal point.
     * </p>
     *
     * @param fr the focal radius (default is 0)
     * @return this element for method chaining
     */
    public RadialGradientElement fr(double fr) {
        setAttribute("fr", String.valueOf(fr));
        return this;
    }

    /**
     * Sets the center of the gradient circle.
     *
     * @param cx the x coordinate
     * @param cy the y coordinate
     * @return this element for method chaining
     */
    public RadialGradientElement center(double cx, double cy) {
        cx(cx);
        cy(cy);
        return this;
    }

    /**
     * Sets the focal point of the gradient.
     *
     * @param fx the x coordinate
     * @param fy the y coordinate
     * @return this element for method chaining
     */
    public RadialGradientElement focalPoint(double fx, double fy) {
        fx(fx);
        fy(fy);
        return this;
    }

    /**
     * Sets the coordinate system for the gradient.
     *
     * @param units the gradient units
     * @return this element for method chaining
     */
    public RadialGradientElement gradientUnits(LinearGradientElement.GradientUnits units) {
        setAttribute("gradientUnits", units.toString());
        return this;
    }

    /**
     * Sets how the gradient behaves outside its bounds.
     *
     * @param method the spread method
     * @return this element for method chaining
     */
    public RadialGradientElement spreadMethod(LinearGradientElement.SpreadMethod method) {
        setAttribute("spreadMethod", method.toString());
        return this;
    }

    /**
     * Sets a transform on the gradient.
     *
     * @param transform the transform string
     * @return this element for method chaining
     */
    public RadialGradientElement gradientTransform(String transform) {
        setAttribute("gradientTransform", transform);
        return this;
    }

    /**
     * References another gradient to inherit stops and attributes from.
     *
     * @param href the reference (e.g., "#otherGradient")
     * @return this element for method chaining
     */
    public RadialGradientElement href(String href) {
        setAttribute("href", href);
        return this;
    }

    /**
     * Adds stop elements to this gradient.
     *
     * @param stops the stop elements
     * @return this element for method chaining
     */
    public RadialGradientElement addStops(StopElement... stops) {
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
    public RadialGradientElement addStop(double offset, Color color) {
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
    public RadialGradientElement addStop(double offset, String color) {
        appendChild(new StopElement(offset, color));
        return this;
    }
}
