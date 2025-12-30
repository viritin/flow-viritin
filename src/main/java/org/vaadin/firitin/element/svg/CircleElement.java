package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <circle>} element.
 * <p>
 * The {@code <circle>} element is a basic SVG shape that draws circles,
 * defined by a center point and a radius.
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
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/circle">MDN: circle element</a>
 */
public class CircleElement extends SvgGraphicsElement {

    public CircleElement() {
        super("circle");
    }

    // ========== cx attribute ==========

    /**
     * Sets the x-axis coordinate of the center of the circle.
     * <p>
     * Uses write-only optimization. Use {@link #cxRW(double)} if you need to read the value back.
     * </p>
     *
     * @param cx the x coordinate of the center in user units
     * @return this element for method chaining
     */
    public CircleElement cx(double cx) {
        setWriteOnlyAttribute("cx", String.valueOf(cx));
        return this;
    }

    /**
     * Sets the x-axis coordinate of the center of the circle with a unit or percentage.
     * <p>
     * Uses write-only optimization. Use {@link #cxRW(String)} if you need to read the value back.
     * </p>
     *
     * @param cx the x coordinate of the center (e.g., "50", "50%", "10px")
     * @return this element for method chaining
     */
    public CircleElement cx(String cx) {
        setWriteOnlyAttribute("cx", cx);
        return this;
    }

    /**
     * Sets the x-axis coordinate of the center of the circle (read-write).
     *
     * @param cx the x coordinate of the center in user units
     * @return this element for method chaining
     */
    public CircleElement cxRW(double cx) {
        setAttribute("cx", String.valueOf(cx));
        return this;
    }

    /**
     * Sets the x-axis coordinate of the center of the circle with a unit or percentage (read-write).
     *
     * @param cx the x coordinate of the center (e.g., "50", "50%", "10px")
     * @return this element for method chaining
     */
    public CircleElement cxRW(String cx) {
        setAttribute("cx", cx);
        return this;
    }

    // ========== cy attribute ==========

    /**
     * Sets the y-axis coordinate of the center of the circle.
     * <p>
     * Uses write-only optimization. Use {@link #cyRW(double)} if you need to read the value back.
     * </p>
     *
     * @param cy the y coordinate of the center in user units
     * @return this element for method chaining
     */
    public CircleElement cy(double cy) {
        setWriteOnlyAttribute("cy", String.valueOf(cy));
        return this;
    }

    /**
     * Sets the y-axis coordinate of the center of the circle with a unit or percentage.
     * <p>
     * Uses write-only optimization. Use {@link #cyRW(String)} if you need to read the value back.
     * </p>
     *
     * @param cy the y coordinate of the center (e.g., "50", "50%", "10px")
     * @return this element for method chaining
     */
    public CircleElement cy(String cy) {
        setWriteOnlyAttribute("cy", cy);
        return this;
    }

    /**
     * Sets the y-axis coordinate of the center of the circle (read-write).
     *
     * @param cy the y coordinate of the center in user units
     * @return this element for method chaining
     */
    public CircleElement cyRW(double cy) {
        setAttribute("cy", String.valueOf(cy));
        return this;
    }

    /**
     * Sets the y-axis coordinate of the center of the circle with a unit or percentage (read-write).
     *
     * @param cy the y coordinate of the center (e.g., "50", "50%", "10px")
     * @return this element for method chaining
     */
    public CircleElement cyRW(String cy) {
        setAttribute("cy", cy);
        return this;
    }

    // ========== r attribute ==========

    /**
     * Sets the radius of the circle.
     * <p>
     * A value less than or equal to zero disables rendering of the circle.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #rRW(double)} if you need to read the value back.
     * </p>
     *
     * @param r the radius in user units
     * @return this element for method chaining
     */
    public CircleElement r(double r) {
        setWriteOnlyAttribute("r", String.valueOf(r));
        return this;
    }

    /**
     * Sets the radius of the circle with a unit or percentage.
     * <p>
     * A value less than or equal to zero disables rendering of the circle.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #rRW(String)} if you need to read the value back.
     * </p>
     *
     * @param r the radius (e.g., "25", "50%", "10px")
     * @return this element for method chaining
     */
    public CircleElement r(String r) {
        setWriteOnlyAttribute("r", r);
        return this;
    }

    /**
     * Sets the radius of the circle (read-write).
     *
     * @param r the radius in user units
     * @return this element for method chaining
     */
    public CircleElement rRW(double r) {
        setAttribute("r", String.valueOf(r));
        return this;
    }

    /**
     * Sets the radius of the circle with a unit or percentage (read-write).
     *
     * @param r the radius (e.g., "25", "50%", "10px")
     * @return this element for method chaining
     */
    public CircleElement rRW(String r) {
        setAttribute("r", r);
        return this;
    }

    // ========== pathLength attribute ==========

    /**
     * Sets the total length for the circle's circumference in user units.
     * <p>
     * This value is used to calibrate the browser's distance calculations
     * with those of the author, by scaling all distance computations using
     * the ratio pathLength / (computed value of circumference).
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #pathLengthRW(double)} if you need to read the value back.
     * </p>
     *
     * @param pathLength the total path length in user units
     * @return this element for method chaining
     */
    public CircleElement pathLength(double pathLength) {
        setWriteOnlyAttribute("pathLength", String.valueOf(pathLength));
        return this;
    }

    /**
     * Sets the total length for the circle's circumference in user units (read-write).
     *
     * @param pathLength the total path length in user units
     * @return this element for method chaining
     */
    public CircleElement pathLengthRW(double pathLength) {
        setAttribute("pathLength", String.valueOf(pathLength));
        return this;
    }

    // ========== Convenience methods ==========

    /**
     * Convenience method to set the center position (cx, cy) at once.
     * <p>
     * Uses write-only optimization. Use {@link #centerRW(double, double)} if you need to read the values back.
     * </p>
     *
     * @param cx the x coordinate of the center in user units
     * @param cy the y coordinate of the center in user units
     * @return this element for method chaining
     */
    public CircleElement center(double cx, double cy) {
        return cx(cx).cy(cy);
    }

    /**
     * Convenience method to set the center position with strings (supports units/percentages).
     * <p>
     * Uses write-only optimization. Use {@link #centerRW(String, String)} if you need to read the values back.
     * </p>
     *
     * @param cx the x coordinate of the center (e.g., "50%")
     * @param cy the y coordinate of the center (e.g., "50%")
     * @return this element for method chaining
     */
    public CircleElement center(String cx, String cy) {
        return cx(cx).cy(cy);
    }

    /**
     * Convenience method to set the center position (cx, cy) at once (read-write).
     *
     * @param cx the x coordinate of the center in user units
     * @param cy the y coordinate of the center in user units
     * @return this element for method chaining
     */
    public CircleElement centerRW(double cx, double cy) {
        return cxRW(cx).cyRW(cy);
    }

    /**
     * Convenience method to set the center position with strings (read-write).
     *
     * @param cx the x coordinate of the center (e.g., "50%")
     * @param cy the y coordinate of the center (e.g., "50%")
     * @return this element for method chaining
     */
    public CircleElement centerRW(String cx, String cy) {
        return cxRW(cx).cyRW(cy);
    }
}