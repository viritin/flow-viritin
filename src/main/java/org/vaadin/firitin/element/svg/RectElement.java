package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <rect>} element.
 * <p>
 * The {@code <rect>} element is a basic SVG shape that draws rectangles,
 * defined by their position, width, and height. The rectangles may have
 * their corners rounded.
 * </p>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/rect">MDN: rect element</a>
 */
public class RectElement extends SvgGraphicsElement {

    public RectElement() {
        super("rect");
    }

    /**
     * Sets the x coordinate of the rectangle.
     *
     * @param x the x coordinate in user units
     * @return this element for method chaining
     */
    public RectElement x(double x) {
        setAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the x coordinate of the rectangle with a unit or percentage.
     *
     * @param x the x coordinate (e.g., "10", "50%", "10px")
     * @return this element for method chaining
     */
    public RectElement x(String x) {
        setAttribute("x", x);
        return this;
    }

    /**
     * Sets the y coordinate of the rectangle.
     *
     * @param y the y coordinate in user units
     * @return this element for method chaining
     */
    public RectElement y(double y) {
        setAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the y coordinate of the rectangle with a unit or percentage.
     *
     * @param y the y coordinate (e.g., "10", "50%", "10px")
     * @return this element for method chaining
     */
    public RectElement y(String y) {
        setAttribute("y", y);
        return this;
    }

    /**
     * Sets the width of the rectangle.
     *
     * @param width the width in user units
     * @return this element for method chaining
     */
    public RectElement width(double width) {
        setAttribute("width", String.valueOf(width));
        return this;
    }

    /**
     * Sets the width of the rectangle with a unit or percentage.
     *
     * @param width the width (e.g., "100", "50%", "100px")
     * @return this element for method chaining
     */
    public RectElement width(String width) {
        setAttribute("width", width);
        return this;
    }

    /**
     * Sets the height of the rectangle.
     *
     * @param height the height in user units
     * @return this element for method chaining
     */
    public RectElement height(double height) {
        setAttribute("height", String.valueOf(height));
        return this;
    }

    /**
     * Sets the height of the rectangle with a unit or percentage.
     *
     * @param height the height (e.g., "100", "50%", "100px")
     * @return this element for method chaining
     */
    public RectElement height(String height) {
        setAttribute("height", height);
        return this;
    }

    /**
     * Sets the horizontal corner radius of the rectangle.
     * <p>
     * If {@code ry} is not specified, it defaults to the value of {@code rx}.
     * </p>
     *
     * @param rx the horizontal corner radius in user units
     * @return this element for method chaining
     */
    public RectElement rx(double rx) {
        setAttribute("rx", String.valueOf(rx));
        return this;
    }

    /**
     * Sets the horizontal corner radius of the rectangle with a unit or percentage.
     * <p>
     * If {@code ry} is not specified, it defaults to the value of {@code rx}.
     * </p>
     *
     * @param rx the horizontal corner radius (e.g., "10", "50%", "10px")
     * @return this element for method chaining
     */
    public RectElement rx(String rx) {
        setAttribute("rx", rx);
        return this;
    }

    /**
     * Sets the vertical corner radius of the rectangle.
     * <p>
     * If {@code rx} is not specified, it defaults to the value of {@code ry}.
     * </p>
     *
     * @param ry the vertical corner radius in user units
     * @return this element for method chaining
     */
    public RectElement ry(double ry) {
        setAttribute("ry", String.valueOf(ry));
        return this;
    }

    /**
     * Sets the vertical corner radius of the rectangle with a unit or percentage.
     * <p>
     * If {@code rx} is not specified, it defaults to the value of {@code ry}.
     * </p>
     *
     * @param ry the vertical corner radius (e.g., "10", "50%", "10px")
     * @return this element for method chaining
     */
    public RectElement ry(String ry) {
        setAttribute("ry", ry);
        return this;
    }

    /**
     * Sets the total length of the rectangle's perimeter in user units.
     * <p>
     * This value is used to calibrate the browser's distance calculations
     * with those of the author, by scaling all distance computations using
     * the ratio pathLength / (computed value of perimeter).
     * </p>
     *
     * @param pathLength the total path length in user units
     * @return this element for method chaining
     */
    public RectElement pathLength(double pathLength) {
        setAttribute("pathLength", String.valueOf(pathLength));
        return this;
    }

    /**
     * Convenience method to set position (x, y) at once.
     *
     * @param x the x coordinate in user units
     * @param y the y coordinate in user units
     * @return this element for method chaining
     */
    public RectElement position(double x, double y) {
        return x(x).y(y);
    }

    /**
     * Convenience method to set size (width, height) at once.
     *
     * @param width the width in user units
     * @param height the height in user units
     * @return this element for method chaining
     */
    public RectElement size(double width, double height) {
        return width(width).height(height);
    }

    /**
     * Convenience method to set bounds (x, y, width, height) at once.
     *
     * @param x the x coordinate in user units
     * @param y the y coordinate in user units
     * @param width the width in user units
     * @param height the height in user units
     * @return this element for method chaining
     */
    public RectElement bounds(double x, double y, double width, double height) {
        return x(x).y(y).width(width).height(height);
    }

    /**
     * Convenience method to set equal corner radius for both axes.
     *
     * @param radius the corner radius in user units
     * @return this element for method chaining
     */
    public RectElement cornerRadius(double radius) {
        return rx(radius).ry(radius);
    }

    /**
     * Convenience method to set different corner radii for horizontal and vertical axes.
     *
     * @param rx the horizontal corner radius in user units
     * @param ry the vertical corner radius in user units
     * @return this element for method chaining
     */
    public RectElement cornerRadius(double rx, double ry) {
        return rx(rx).ry(ry);
    }
}