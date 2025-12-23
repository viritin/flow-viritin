package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <ellipse>} element.
 * <p>
 * The {@code <ellipse>} element is a basic SVG shape that draws ellipses,
 * defined by a center point and two radii (horizontal and vertical).
 * </p>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/ellipse">MDN: ellipse element</a>
 */
public class EllipseElement extends SvgElement {

    public EllipseElement() {
        super("ellipse");
    }

    /**
     * Sets the x-axis coordinate of the center of the ellipse.
     *
     * @param cx the x coordinate of the center in user units
     * @return this element for method chaining
     */
    public EllipseElement cx(double cx) {
        setAttribute("cx", String.valueOf(cx));
        return this;
    }

    /**
     * Sets the x-axis coordinate of the center of the ellipse with a unit or percentage.
     *
     * @param cx the x coordinate of the center (e.g., "50", "50%", "10px")
     * @return this element for method chaining
     */
    public EllipseElement cx(String cx) {
        setAttribute("cx", cx);
        return this;
    }

    /**
     * Sets the y-axis coordinate of the center of the ellipse.
     *
     * @param cy the y coordinate of the center in user units
     * @return this element for method chaining
     */
    public EllipseElement cy(double cy) {
        setAttribute("cy", String.valueOf(cy));
        return this;
    }

    /**
     * Sets the y-axis coordinate of the center of the ellipse with a unit or percentage.
     *
     * @param cy the y coordinate of the center (e.g., "50", "50%", "10px")
     * @return this element for method chaining
     */
    public EllipseElement cy(String cy) {
        setAttribute("cy", cy);
        return this;
    }

    /**
     * Sets the radius of the ellipse on the x axis.
     *
     * @param rx the horizontal radius in user units
     * @return this element for method chaining
     */
    public EllipseElement rx(double rx) {
        setAttribute("rx", String.valueOf(rx));
        return this;
    }

    /**
     * Sets the radius of the ellipse on the x axis with a unit or percentage.
     *
     * @param rx the horizontal radius (e.g., "50", "25%", "10px")
     * @return this element for method chaining
     */
    public EllipseElement rx(String rx) {
        setAttribute("rx", rx);
        return this;
    }

    /**
     * Sets the radius of the ellipse on the y axis.
     *
     * @param ry the vertical radius in user units
     * @return this element for method chaining
     */
    public EllipseElement ry(double ry) {
        setAttribute("ry", String.valueOf(ry));
        return this;
    }

    /**
     * Sets the radius of the ellipse on the y axis with a unit or percentage.
     *
     * @param ry the vertical radius (e.g., "50", "25%", "10px")
     * @return this element for method chaining
     */
    public EllipseElement ry(String ry) {
        setAttribute("ry", ry);
        return this;
    }

    /**
     * Sets the total length for the ellipse's path in user units.
     * <p>
     * This value is used to calibrate the browser's distance calculations
     * with those of the author, by scaling all distance computations using
     * the ratio pathLength / (computed value of path length).
     * </p>
     *
     * @param pathLength the total path length in user units
     * @return this element for method chaining
     */
    public EllipseElement pathLength(double pathLength) {
        setAttribute("pathLength", String.valueOf(pathLength));
        return this;
    }

    /**
     * Convenience method to set the center position (cx, cy) at once.
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
     *
     * @param cx the x coordinate of the center (e.g., "50%")
     * @param cy the y coordinate of the center (e.g., "50%")
     * @return this element for method chaining
     */
    public EllipseElement center(String cx, String cy) {
        return cx(cx).cy(cy);
    }

    /**
     * Convenience method to set both radii at once.
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
     *
     * @param rx the horizontal radius (e.g., "50%")
     * @param ry the vertical radius (e.g., "25%")
     * @return this element for method chaining
     */
    public EllipseElement radii(String rx, String ry) {
        return rx(rx).ry(ry);
    }
}
