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
 * <h2>Write-Only vs Read-Write Methods</h2>
 * <p>
 * This class provides two variants for each attribute setter:
 * </p>
 * <ul>
 *   <li><strong>Default methods</strong> (e.g., {@code cx()}, {@code cy()}) - Use an optimized
 *       write-only approach. Attribute values are NOT stored on the server and cannot be
 *       retrieved via {@code getAttribute()}.</li>
 *   <li><strong>RW methods</strong> (e.g., {@code cxRW()}, {@code cyRW()}) - Use traditional
 *       {@code setAttribute()} which stores values on the server for later retrieval.</li>
 * </ul>
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

    // ========== cx attribute ==========

    /**
     * Sets the x coordinate of the gradient circle center.
     * <p>
     * Uses write-only optimization. Use {@link #cxRW(double)} if you need to read the value back.
     * </p>
     *
     * @param cx the x coordinate (default is 0.5 or 50%)
     * @return this element for method chaining
     */
    public RadialGradientElement cx(double cx) {
        setWriteOnlyAttribute("cx", String.valueOf(cx));
        return this;
    }

    /**
     * Sets the x coordinate of the gradient circle center with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #cxRW(String)} if you need to read the value back.
     * </p>
     *
     * @param cx the x coordinate (e.g., "50%")
     * @return this element for method chaining
     */
    public RadialGradientElement cx(String cx) {
        setWriteOnlyAttribute("cx", cx);
        return this;
    }

    /**
     * Sets the x coordinate of the gradient circle center (read-write).
     *
     * @param cx the x coordinate (default is 0.5 or 50%)
     * @return this element for method chaining
     */
    public RadialGradientElement cxRW(double cx) {
        setAttribute("cx", String.valueOf(cx));
        return this;
    }

    /**
     * Sets the x coordinate of the gradient circle center with a unit (read-write).
     *
     * @param cx the x coordinate (e.g., "50%")
     * @return this element for method chaining
     */
    public RadialGradientElement cxRW(String cx) {
        setAttribute("cx", cx);
        return this;
    }

    // ========== cy attribute ==========

    /**
     * Sets the y coordinate of the gradient circle center.
     * <p>
     * Uses write-only optimization. Use {@link #cyRW(double)} if you need to read the value back.
     * </p>
     *
     * @param cy the y coordinate (default is 0.5 or 50%)
     * @return this element for method chaining
     */
    public RadialGradientElement cy(double cy) {
        setWriteOnlyAttribute("cy", String.valueOf(cy));
        return this;
    }

    /**
     * Sets the y coordinate of the gradient circle center with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #cyRW(String)} if you need to read the value back.
     * </p>
     *
     * @param cy the y coordinate (e.g., "50%")
     * @return this element for method chaining
     */
    public RadialGradientElement cy(String cy) {
        setWriteOnlyAttribute("cy", cy);
        return this;
    }

    /**
     * Sets the y coordinate of the gradient circle center (read-write).
     *
     * @param cy the y coordinate (default is 0.5 or 50%)
     * @return this element for method chaining
     */
    public RadialGradientElement cyRW(double cy) {
        setAttribute("cy", String.valueOf(cy));
        return this;
    }

    /**
     * Sets the y coordinate of the gradient circle center with a unit (read-write).
     *
     * @param cy the y coordinate (e.g., "50%")
     * @return this element for method chaining
     */
    public RadialGradientElement cyRW(String cy) {
        setAttribute("cy", cy);
        return this;
    }

    // ========== r attribute ==========

    /**
     * Sets the radius of the gradient circle.
     * <p>
     * Uses write-only optimization. Use {@link #rRW(double)} if you need to read the value back.
     * </p>
     *
     * @param r the radius (default is 0.5 or 50%)
     * @return this element for method chaining
     */
    public RadialGradientElement r(double r) {
        setWriteOnlyAttribute("r", String.valueOf(r));
        return this;
    }

    /**
     * Sets the radius of the gradient circle with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #rRW(String)} if you need to read the value back.
     * </p>
     *
     * @param r the radius (e.g., "50%")
     * @return this element for method chaining
     */
    public RadialGradientElement r(String r) {
        setWriteOnlyAttribute("r", r);
        return this;
    }

    /**
     * Sets the radius of the gradient circle (read-write).
     *
     * @param r the radius (default is 0.5 or 50%)
     * @return this element for method chaining
     */
    public RadialGradientElement rRW(double r) {
        setAttribute("r", String.valueOf(r));
        return this;
    }

    /**
     * Sets the radius of the gradient circle with a unit (read-write).
     *
     * @param r the radius (e.g., "50%")
     * @return this element for method chaining
     */
    public RadialGradientElement rRW(String r) {
        setAttribute("r", r);
        return this;
    }

    // ========== fx attribute ==========

    /**
     * Sets the x coordinate of the focal point.
     * <p>
     * The focal point is where the gradient starts (0% stop).
     * If not specified, defaults to the center (cx).
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #fxRW(double)} if you need to read the value back.
     * </p>
     *
     * @param fx the x coordinate
     * @return this element for method chaining
     */
    public RadialGradientElement fx(double fx) {
        setWriteOnlyAttribute("fx", String.valueOf(fx));
        return this;
    }

    /**
     * Sets the x coordinate of the focal point with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #fxRW(String)} if you need to read the value back.
     * </p>
     *
     * @param fx the x coordinate (e.g., "25%")
     * @return this element for method chaining
     */
    public RadialGradientElement fx(String fx) {
        setWriteOnlyAttribute("fx", fx);
        return this;
    }

    /**
     * Sets the x coordinate of the focal point (read-write).
     *
     * @param fx the x coordinate
     * @return this element for method chaining
     */
    public RadialGradientElement fxRW(double fx) {
        setAttribute("fx", String.valueOf(fx));
        return this;
    }

    /**
     * Sets the x coordinate of the focal point with a unit (read-write).
     *
     * @param fx the x coordinate (e.g., "25%")
     * @return this element for method chaining
     */
    public RadialGradientElement fxRW(String fx) {
        setAttribute("fx", fx);
        return this;
    }

    // ========== fy attribute ==========

    /**
     * Sets the y coordinate of the focal point.
     * <p>
     * Uses write-only optimization. Use {@link #fyRW(double)} if you need to read the value back.
     * </p>
     *
     * @param fy the y coordinate
     * @return this element for method chaining
     */
    public RadialGradientElement fy(double fy) {
        setWriteOnlyAttribute("fy", String.valueOf(fy));
        return this;
    }

    /**
     * Sets the y coordinate of the focal point with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #fyRW(String)} if you need to read the value back.
     * </p>
     *
     * @param fy the y coordinate (e.g., "25%")
     * @return this element for method chaining
     */
    public RadialGradientElement fy(String fy) {
        setWriteOnlyAttribute("fy", fy);
        return this;
    }

    /**
     * Sets the y coordinate of the focal point (read-write).
     *
     * @param fy the y coordinate
     * @return this element for method chaining
     */
    public RadialGradientElement fyRW(double fy) {
        setAttribute("fy", String.valueOf(fy));
        return this;
    }

    /**
     * Sets the y coordinate of the focal point with a unit (read-write).
     *
     * @param fy the y coordinate (e.g., "25%")
     * @return this element for method chaining
     */
    public RadialGradientElement fyRW(String fy) {
        setAttribute("fy", fy);
        return this;
    }

    // ========== fr attribute ==========

    /**
     * Sets the radius of the focal point circle.
     * <p>
     * This creates a focal ring instead of a focal point.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #frRW(double)} if you need to read the value back.
     * </p>
     *
     * @param fr the focal radius (default is 0)
     * @return this element for method chaining
     */
    public RadialGradientElement fr(double fr) {
        setWriteOnlyAttribute("fr", String.valueOf(fr));
        return this;
    }

    /**
     * Sets the radius of the focal point circle (read-write).
     *
     * @param fr the focal radius (default is 0)
     * @return this element for method chaining
     */
    public RadialGradientElement frRW(double fr) {
        setAttribute("fr", String.valueOf(fr));
        return this;
    }

    // ========== Convenience methods ==========

    /**
     * Sets the center of the gradient circle.
     * <p>
     * Uses write-only optimization. Use {@link #centerRW(double, double)} if you need to read the values back.
     * </p>
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
     * Sets the center of the gradient circle (read-write).
     *
     * @param cx the x coordinate
     * @param cy the y coordinate
     * @return this element for method chaining
     */
    public RadialGradientElement centerRW(double cx, double cy) {
        cxRW(cx);
        cyRW(cy);
        return this;
    }

    /**
     * Sets the focal point of the gradient.
     * <p>
     * Uses write-only optimization. Use {@link #focalPointRW(double, double)} if you need to read the values back.
     * </p>
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
     * Sets the focal point of the gradient (read-write).
     *
     * @param fx the x coordinate
     * @param fy the y coordinate
     * @return this element for method chaining
     */
    public RadialGradientElement focalPointRW(double fx, double fy) {
        fxRW(fx);
        fyRW(fy);
        return this;
    }

    // ========== gradientUnits attribute ==========

    /**
     * Sets the coordinate system for the gradient.
     * <p>
     * Uses write-only optimization. Use {@link #gradientUnitsRW(LinearGradientElement.GradientUnits)} if you need to read the value back.
     * </p>
     *
     * @param units the gradient units
     * @return this element for method chaining
     */
    public RadialGradientElement gradientUnits(LinearGradientElement.GradientUnits units) {
        setWriteOnlyAttribute("gradientUnits", units.toString());
        return this;
    }

    /**
     * Sets the coordinate system for the gradient (read-write).
     *
     * @param units the gradient units
     * @return this element for method chaining
     */
    public RadialGradientElement gradientUnitsRW(LinearGradientElement.GradientUnits units) {
        setAttribute("gradientUnits", units.toString());
        return this;
    }

    // ========== spreadMethod attribute ==========

    /**
     * Sets how the gradient behaves outside its bounds.
     * <p>
     * Uses write-only optimization. Use {@link #spreadMethodRW(LinearGradientElement.SpreadMethod)} if you need to read the value back.
     * </p>
     *
     * @param method the spread method
     * @return this element for method chaining
     */
    public RadialGradientElement spreadMethod(LinearGradientElement.SpreadMethod method) {
        setWriteOnlyAttribute("spreadMethod", method.toString());
        return this;
    }

    /**
     * Sets how the gradient behaves outside its bounds (read-write).
     *
     * @param method the spread method
     * @return this element for method chaining
     */
    public RadialGradientElement spreadMethodRW(LinearGradientElement.SpreadMethod method) {
        setAttribute("spreadMethod", method.toString());
        return this;
    }

    // ========== gradientTransform attribute ==========

    /**
     * Sets a transform on the gradient.
     * <p>
     * Uses write-only optimization. Use {@link #gradientTransformRW(String)} if you need to read the value back.
     * </p>
     *
     * @param transform the transform string
     * @return this element for method chaining
     */
    public RadialGradientElement gradientTransform(String transform) {
        setWriteOnlyAttribute("gradientTransform", transform);
        return this;
    }

    /**
     * Sets a transform on the gradient (read-write).
     *
     * @param transform the transform string
     * @return this element for method chaining
     */
    public RadialGradientElement gradientTransformRW(String transform) {
        setAttribute("gradientTransform", transform);
        return this;
    }

    // ========== href attribute ==========

    /**
     * References another gradient to inherit stops and attributes from.
     * <p>
     * Uses write-only optimization. Use {@link #hrefRW(String)} if you need to read the value back.
     * </p>
     *
     * @param href the reference (e.g., "#otherGradient")
     * @return this element for method chaining
     */
    public RadialGradientElement href(String href) {
        setWriteOnlyAttribute("href", href);
        return this;
    }

    /**
     * References another gradient to inherit stops and attributes from (read-write).
     *
     * @param href the reference (e.g., "#otherGradient")
     * @return this element for method chaining
     */
    public RadialGradientElement hrefRW(String href) {
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
