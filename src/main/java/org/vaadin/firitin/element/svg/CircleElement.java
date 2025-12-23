package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <circle>} element.
 * <p>
 * The {@code <circle>} element is a basic SVG shape that draws circles,
 * defined by a center point and a radius.
 * </p>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/circle">MDN: circle element</a>
 */
public class CircleElement extends SvgElement {

    public CircleElement() {
        super("circle");
    }

    /**
     * Sets the x-axis coordinate of the center of the circle.
     *
     * @param cx the x coordinate of the center in user units
     * @return this element for method chaining
     */
    public CircleElement cx(double cx) {
        setAttribute("cx", String.valueOf(cx));
        return this;
    }

    /**
     * Sets the x-axis coordinate of the center of the circle with a unit or percentage.
     *
     * @param cx the x coordinate of the center (e.g., "50", "50%", "10px")
     * @return this element for method chaining
     */
    public CircleElement cx(String cx) {
        setAttribute("cx", cx);
        return this;
    }

    /**
     * Sets the y-axis coordinate of the center of the circle.
     *
     * @param cy the y coordinate of the center in user units
     * @return this element for method chaining
     */
    public CircleElement cy(double cy) {
        setAttribute("cy", String.valueOf(cy));
        return this;
    }

    /**
     * Sets the y-axis coordinate of the center of the circle with a unit or percentage.
     *
     * @param cy the y coordinate of the center (e.g., "50", "50%", "10px")
     * @return this element for method chaining
     */
    public CircleElement cy(String cy) {
        setAttribute("cy", cy);
        return this;
    }

    /**
     * Sets the radius of the circle.
     * <p>
     * A value less than or equal to zero disables rendering of the circle.
     * </p>
     *
     * @param r the radius in user units
     * @return this element for method chaining
     */
    public CircleElement r(double r) {
        setAttribute("r", String.valueOf(r));
        return this;
    }

    /**
     * Sets the radius of the circle with a unit or percentage.
     * <p>
     * A value less than or equal to zero disables rendering of the circle.
     * </p>
     *
     * @param r the radius (e.g., "25", "50%", "10px")
     * @return this element for method chaining
     */
    public CircleElement r(String r) {
        setAttribute("r", r);
        return this;
    }

    /**
     * Sets the total length for the circle's circumference in user units.
     * <p>
     * This value is used to calibrate the browser's distance calculations
     * with those of the author, by scaling all distance computations using
     * the ratio pathLength / (computed value of circumference).
     * </p>
     *
     * @param pathLength the total path length in user units
     * @return this element for method chaining
     */
    public CircleElement pathLength(double pathLength) {
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
    public CircleElement center(double cx, double cy) {
        return cx(cx).cy(cy);
    }

    /**
     * Convenience method to set the center position with strings (supports units/percentages).
     *
     * @param cx the x coordinate of the center (e.g., "50%")
     * @param cy the y coordinate of the center (e.g., "50%")
     * @return this element for method chaining
     */
    public CircleElement center(String cx, String cy) {
        return cx(cx).cy(cy);
    }
}
