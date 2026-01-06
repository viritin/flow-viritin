package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <ellipse>} element.
 * <p>
 * The {@code <ellipse>} element is a basic SVG shape that draws ellipses,
 * defined by a center point and two radii (horizontal and vertical).
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
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/ellipse">MDN: ellipse element</a>
 */
public class EllipseElement extends SvgGraphicsElement {

    public EllipseElement() {
        super("ellipse");
    }

    // ========== cx attribute ==========

    /**
     * Sets the x-axis coordinate of the center of the ellipse.
     * <p>
     * Uses write-only optimization. Use {@link #cxRW(double)} if you need to read the value back.
     * </p>
     *
     * @param cx the x coordinate of the center in user units
     * @return this element for method chaining
     */
    public EllipseElement cx(double cx) {
        setWriteOnlyAttribute("cx", String.valueOf(cx));
        return this;
    }

    /**
     * Sets the x-axis coordinate of the center of the ellipse with a unit or percentage.
     * <p>
     * Uses write-only optimization. Use {@link #cxRW(String)} if you need to read the value back.
     * </p>
     *
     * @param cx the x coordinate of the center (e.g., "50", "50%", "10px")
     * @return this element for method chaining
     */
    public EllipseElement cx(String cx) {
        setWriteOnlyAttribute("cx", cx);
        return this;
    }

    /**
     * Sets the x-axis coordinate of the center of the ellipse (read-write).
     *
     * @param cx the x coordinate of the center in user units
     * @return this element for method chaining
     */
    public EllipseElement cxRW(double cx) {
        setAttribute("cx", String.valueOf(cx));
        return this;
    }

    /**
     * Sets the x-axis coordinate of the center of the ellipse with a unit or percentage (read-write).
     *
     * @param cx the x coordinate of the center (e.g., "50", "50%", "10px")
     * @return this element for method chaining
     */
    public EllipseElement cxRW(String cx) {
        setAttribute("cx", cx);
        return this;
    }

    // ========== cy attribute ==========

    /**
     * Sets the y-axis coordinate of the center of the ellipse.
     * <p>
     * Uses write-only optimization. Use {@link #cyRW(double)} if you need to read the value back.
     * </p>
     *
     * @param cy the y coordinate of the center in user units
     * @return this element for method chaining
     */
    public EllipseElement cy(double cy) {
        setWriteOnlyAttribute("cy", String.valueOf(cy));
        return this;
    }

    /**
     * Sets the y-axis coordinate of the center of the ellipse with a unit or percentage.
     * <p>
     * Uses write-only optimization. Use {@link #cyRW(String)} if you need to read the value back.
     * </p>
     *
     * @param cy the y coordinate of the center (e.g., "50", "50%", "10px")
     * @return this element for method chaining
     */
    public EllipseElement cy(String cy) {
        setWriteOnlyAttribute("cy", cy);
        return this;
    }

    /**
     * Sets the y-axis coordinate of the center of the ellipse (read-write).
     *
     * @param cy the y coordinate of the center in user units
     * @return this element for method chaining
     */
    public EllipseElement cyRW(double cy) {
        setAttribute("cy", String.valueOf(cy));
        return this;
    }

    /**
     * Sets the y-axis coordinate of the center of the ellipse with a unit or percentage (read-write).
     *
     * @param cy the y coordinate of the center (e.g., "50", "50%", "10px")
     * @return this element for method chaining
     */
    public EllipseElement cyRW(String cy) {
        setAttribute("cy", cy);
        return this;
    }

    // ========== rx attribute ==========

    /**
     * Sets the radius of the ellipse on the x axis.
     * <p>
     * Uses write-only optimization. Use {@link #rxRW(double)} if you need to read the value back.
     * </p>
     *
     * @param rx the horizontal radius in user units
     * @return this element for method chaining
     */
    public EllipseElement rx(double rx) {
        setWriteOnlyAttribute("rx", String.valueOf(rx));
        return this;
    }

    /**
     * Sets the radius of the ellipse on the x axis with a unit or percentage.
     * <p>
     * Uses write-only optimization. Use {@link #rxRW(String)} if you need to read the value back.
     * </p>
     *
     * @param rx the horizontal radius (e.g., "50", "25%", "10px")
     * @return this element for method chaining
     */
    public EllipseElement rx(String rx) {
        setWriteOnlyAttribute("rx", rx);
        return this;
    }

    /**
     * Sets the radius of the ellipse on the x axis (read-write).
     *
     * @param rx the horizontal radius in user units
     * @return this element for method chaining
     */
    public EllipseElement rxRW(double rx) {
        setAttribute("rx", String.valueOf(rx));
        return this;
    }

    /**
     * Sets the radius of the ellipse on the x axis with a unit or percentage (read-write).
     *
     * @param rx the horizontal radius (e.g., "50", "25%", "10px")
     * @return this element for method chaining
     */
    public EllipseElement rxRW(String rx) {
        setAttribute("rx", rx);
        return this;
    }

    // ========== ry attribute ==========

    /**
     * Sets the radius of the ellipse on the y axis.
     * <p>
     * Uses write-only optimization. Use {@link #ryRW(double)} if you need to read the value back.
     * </p>
     *
     * @param ry the vertical radius in user units
     * @return this element for method chaining
     */
    public EllipseElement ry(double ry) {
        setWriteOnlyAttribute("ry", String.valueOf(ry));
        return this;
    }

    /**
     * Sets the radius of the ellipse on the y axis with a unit or percentage.
     * <p>
     * Uses write-only optimization. Use {@link #ryRW(String)} if you need to read the value back.
     * </p>
     *
     * @param ry the vertical radius (e.g., "50", "25%", "10px")
     * @return this element for method chaining
     */
    public EllipseElement ry(String ry) {
        setWriteOnlyAttribute("ry", ry);
        return this;
    }

    /**
     * Sets the radius of the ellipse on the y axis (read-write).
     *
     * @param ry the vertical radius in user units
     * @return this element for method chaining
     */
    public EllipseElement ryRW(double ry) {
        setAttribute("ry", String.valueOf(ry));
        return this;
    }

    /**
     * Sets the radius of the ellipse on the y axis with a unit or percentage (read-write).
     *
     * @param ry the vertical radius (e.g., "50", "25%", "10px")
     * @return this element for method chaining
     */
    public EllipseElement ryRW(String ry) {
        setAttribute("ry", ry);
        return this;
    }

    // ========== pathLength attribute ==========

    /**
     * Sets the total length for the ellipse's path in user units.
     * <p>
     * This value is used to calibrate the browser's distance calculations
     * with those of the author, by scaling all distance computations using
     * the ratio pathLength / (computed value of path length).
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #pathLengthRW(double)} if you need to read the value back.
     * </p>
     *
     * @param pathLength the total path length in user units
     * @return this element for method chaining
     */
    public EllipseElement pathLength(double pathLength) {
        setWriteOnlyAttribute("pathLength", String.valueOf(pathLength));
        return this;
    }

    /**
     * Sets the total length for the ellipse's path in user units (read-write).
     *
     * @param pathLength the total path length in user units
     * @return this element for method chaining
     */
    public EllipseElement pathLengthRW(double pathLength) {
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
    public EllipseElement center(double cx, double cy) {
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
    public EllipseElement center(String cx, String cy) {
        return cx(cx).cy(cy);
    }

    /**
     * Convenience method to set the center position (cx, cy) at once (read-write).
     *
     * @param cx the x coordinate of the center in user units
     * @param cy the y coordinate of the center in user units
     * @return this element for method chaining
     */
    public EllipseElement centerRW(double cx, double cy) {
        return cxRW(cx).cyRW(cy);
    }

    /**
     * Convenience method to set the center position with strings (read-write).
     *
     * @param cx the x coordinate of the center (e.g., "50%")
     * @param cy the y coordinate of the center (e.g., "50%")
     * @return this element for method chaining
     */
    public EllipseElement centerRW(String cx, String cy) {
        return cxRW(cx).cyRW(cy);
    }

    /**
     * Convenience method to set both radii at once.
     * <p>
     * Uses write-only optimization. Use {@link #radiiRW(double, double)} if you need to read the values back.
     * </p>
     *
     * @param rx the horizontal radius in user units
     * @param ry the vertical radius in user units
     * @return this element for method chaining
     */
    public EllipseElement radii(double rx, double ry) {
        return rx(rx).ry(ry);
    }

    /**
     * Convenience method to set both radii with strings (supports units/percentages).
     * <p>
     * Uses write-only optimization. Use {@link #radiiRW(String, String)} if you need to read the values back.
     * </p>
     *
     * @param rx the horizontal radius (e.g., "50%")
     * @param ry the vertical radius (e.g., "25%")
     * @return this element for method chaining
     */
    public EllipseElement radii(String rx, String ry) {
        return rx(rx).ry(ry);
    }

    /**
     * Convenience method to set both radii at once (read-write).
     *
     * @param rx the horizontal radius in user units
     * @param ry the vertical radius in user units
     * @return this element for method chaining
     */
    public EllipseElement radiiRW(double rx, double ry) {
        return rxRW(rx).ryRW(ry);
    }

    /**
     * Convenience method to set both radii with strings (read-write).
     *
     * @param rx the horizontal radius (e.g., "50%")
     * @param ry the vertical radius (e.g., "25%")
     * @return this element for method chaining
     */
    public EllipseElement radiiRW(String rx, String ry) {
        return rxRW(rx).ryRW(ry);
    }

    // ========== Animation methods ==========

    /**
     * Creates an animation for the cx (center x) attribute and appends it to this element.
     * <p>
     * Example usage:
     * <pre>{@code
     * ellipse.animateCx()
     *     .from(0).to(100)
     *     .dur(Duration.ofSeconds(2))
     *     .beginElement();
     * }</pre>
     *
     * @return the animation element for further configuration
     */
    public AnimateElement animateCx() {
        AnimateElement animate = new AnimateElement().attributeName("cx");
        appendChild(animate);
        return animate;
    }

    /**
     * Creates an animation for the cy (center y) attribute and appends it to this element.
     * <p>
     * Example usage:
     * <pre>{@code
     * ellipse.animateCy()
     *     .from(0).to(100)
     *     .dur(Duration.ofSeconds(2))
     *     .beginElement();
     * }</pre>
     *
     * @return the animation element for further configuration
     */
    public AnimateElement animateCy() {
        AnimateElement animate = new AnimateElement().attributeName("cy");
        appendChild(animate);
        return animate;
    }

    /**
     * Creates an animation for the rx (horizontal radius) attribute and appends it to this element.
     * <p>
     * Example usage:
     * <pre>{@code
     * ellipse.animateRx()
     *     .from(10).to(50)
     *     .dur(Duration.ofSeconds(1))
     *     .beginElement();
     * }</pre>
     *
     * @return the animation element for further configuration
     */
    public AnimateElement animateRx() {
        AnimateElement animate = new AnimateElement().attributeName("rx");
        appendChild(animate);
        return animate;
    }

    /**
     * Creates an animation for the ry (vertical radius) attribute and appends it to this element.
     * <p>
     * Example usage:
     * <pre>{@code
     * ellipse.animateRy()
     *     .from(10).to(30)
     *     .dur(Duration.ofSeconds(1))
     *     .beginElement();
     * }</pre>
     *
     * @return the animation element for further configuration
     */
    public AnimateElement animateRy() {
        AnimateElement animate = new AnimateElement().attributeName("ry");
        appendChild(animate);
        return animate;
    }
}
