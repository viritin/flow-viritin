package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <rect>} element.
 * <p>
 * The {@code <rect>} element is a basic SVG shape that draws rectangles,
 * defined by their position, width, and height. The rectangles may have
 * their corners rounded.
 * </p>
 * <h2>Write-Only vs Read-Write Methods</h2>
 * <p>
 * This class provides two variants for each attribute setter:
 * </p>
 * <ul>
 *   <li><strong>Default methods</strong> (e.g., {@code x()}, {@code y()}) - Use an optimized
 *       write-only approach. Attribute values are NOT stored on the server and cannot be
 *       retrieved via {@code getAttribute()}.</li>
 *   <li><strong>RW methods</strong> (e.g., {@code xRW()}, {@code yRW()}) - Use traditional
 *       {@code setAttribute()} which stores values on the server for later retrieval.</li>
 * </ul>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/rect">MDN: rect element</a>
 */
public class RectElement extends SvgGraphicsElement {

    public RectElement() {
        super("rect");
    }

    // ========== x attribute ==========

    /**
     * Sets the x coordinate of the rectangle.
     * <p>
     * Uses write-only optimization. Use {@link #xRW(double)} if you need to read the value back.
     * </p>
     *
     * @param x the x coordinate in user units
     * @return this element for method chaining
     */
    public RectElement x(double x) {
        setWriteOnlyAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the x coordinate of the rectangle with a unit or percentage.
     * <p>
     * Uses write-only optimization. Use {@link #xRW(String)} if you need to read the value back.
     * </p>
     *
     * @param x the x coordinate (e.g., "10", "50%", "10px")
     * @return this element for method chaining
     */
    public RectElement x(String x) {
        setWriteOnlyAttribute("x", x);
        return this;
    }

    /**
     * Sets the x coordinate of the rectangle (read-write).
     *
     * @param x the x coordinate in user units
     * @return this element for method chaining
     */
    public RectElement xRW(double x) {
        setAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the x coordinate of the rectangle with a unit or percentage (read-write).
     *
     * @param x the x coordinate (e.g., "10", "50%", "10px")
     * @return this element for method chaining
     */
    public RectElement xRW(String x) {
        setAttribute("x", x);
        return this;
    }

    // ========== y attribute ==========

    /**
     * Sets the y coordinate of the rectangle.
     * <p>
     * Uses write-only optimization. Use {@link #yRW(double)} if you need to read the value back.
     * </p>
     *
     * @param y the y coordinate in user units
     * @return this element for method chaining
     */
    public RectElement y(double y) {
        setWriteOnlyAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the y coordinate of the rectangle with a unit or percentage.
     * <p>
     * Uses write-only optimization. Use {@link #yRW(String)} if you need to read the value back.
     * </p>
     *
     * @param y the y coordinate (e.g., "10", "50%", "10px")
     * @return this element for method chaining
     */
    public RectElement y(String y) {
        setWriteOnlyAttribute("y", y);
        return this;
    }

    /**
     * Sets the y coordinate of the rectangle (read-write).
     *
     * @param y the y coordinate in user units
     * @return this element for method chaining
     */
    public RectElement yRW(double y) {
        setAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the y coordinate of the rectangle with a unit or percentage (read-write).
     *
     * @param y the y coordinate (e.g., "10", "50%", "10px")
     * @return this element for method chaining
     */
    public RectElement yRW(String y) {
        setAttribute("y", y);
        return this;
    }

    // ========== width attribute ==========

    /**
     * Sets the width of the rectangle.
     * <p>
     * Uses write-only optimization. Use {@link #widthRW(double)} if you need to read the value back.
     * </p>
     *
     * @param width the width in user units
     * @return this element for method chaining
     */
    public RectElement width(double width) {
        setWriteOnlyAttribute("width", String.valueOf(width));
        return this;
    }

    /**
     * Sets the width of the rectangle with a unit or percentage.
     * <p>
     * Uses write-only optimization. Use {@link #widthRW(String)} if you need to read the value back.
     * </p>
     *
     * @param width the width (e.g., "100", "50%", "100px")
     * @return this element for method chaining
     */
    public RectElement width(String width) {
        setWriteOnlyAttribute("width", width);
        return this;
    }

    /**
     * Sets the width of the rectangle (read-write).
     *
     * @param width the width in user units
     * @return this element for method chaining
     */
    public RectElement widthRW(double width) {
        setAttribute("width", String.valueOf(width));
        return this;
    }

    /**
     * Sets the width of the rectangle with a unit or percentage (read-write).
     *
     * @param width the width (e.g., "100", "50%", "100px")
     * @return this element for method chaining
     */
    public RectElement widthRW(String width) {
        setAttribute("width", width);
        return this;
    }

    // ========== height attribute ==========

    /**
     * Sets the height of the rectangle.
     * <p>
     * Uses write-only optimization. Use {@link #heightRW(double)} if you need to read the value back.
     * </p>
     *
     * @param height the height in user units
     * @return this element for method chaining
     */
    public RectElement height(double height) {
        setWriteOnlyAttribute("height", String.valueOf(height));
        return this;
    }

    /**
     * Sets the height of the rectangle with a unit or percentage.
     * <p>
     * Uses write-only optimization. Use {@link #heightRW(String)} if you need to read the value back.
     * </p>
     *
     * @param height the height (e.g., "100", "50%", "100px")
     * @return this element for method chaining
     */
    public RectElement height(String height) {
        setWriteOnlyAttribute("height", height);
        return this;
    }

    /**
     * Sets the height of the rectangle (read-write).
     *
     * @param height the height in user units
     * @return this element for method chaining
     */
    public RectElement heightRW(double height) {
        setAttribute("height", String.valueOf(height));
        return this;
    }

    /**
     * Sets the height of the rectangle with a unit or percentage (read-write).
     *
     * @param height the height (e.g., "100", "50%", "100px")
     * @return this element for method chaining
     */
    public RectElement heightRW(String height) {
        setAttribute("height", height);
        return this;
    }

    // ========== rx attribute ==========

    /**
     * Sets the horizontal corner radius of the rectangle.
     * <p>
     * If {@code ry} is not specified, it defaults to the value of {@code rx}.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #rxRW(double)} if you need to read the value back.
     * </p>
     *
     * @param rx the horizontal corner radius in user units
     * @return this element for method chaining
     */
    public RectElement rx(double rx) {
        setWriteOnlyAttribute("rx", String.valueOf(rx));
        return this;
    }

    /**
     * Sets the horizontal corner radius of the rectangle with a unit or percentage.
     * <p>
     * If {@code ry} is not specified, it defaults to the value of {@code rx}.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #rxRW(String)} if you need to read the value back.
     * </p>
     *
     * @param rx the horizontal corner radius (e.g., "10", "50%", "10px")
     * @return this element for method chaining
     */
    public RectElement rx(String rx) {
        setWriteOnlyAttribute("rx", rx);
        return this;
    }

    /**
     * Sets the horizontal corner radius of the rectangle (read-write).
     *
     * @param rx the horizontal corner radius in user units
     * @return this element for method chaining
     */
    public RectElement rxRW(double rx) {
        setAttribute("rx", String.valueOf(rx));
        return this;
    }

    /**
     * Sets the horizontal corner radius of the rectangle with a unit or percentage (read-write).
     *
     * @param rx the horizontal corner radius (e.g., "10", "50%", "10px")
     * @return this element for method chaining
     */
    public RectElement rxRW(String rx) {
        setAttribute("rx", rx);
        return this;
    }

    // ========== ry attribute ==========

    /**
     * Sets the vertical corner radius of the rectangle.
     * <p>
     * If {@code rx} is not specified, it defaults to the value of {@code ry}.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #ryRW(double)} if you need to read the value back.
     * </p>
     *
     * @param ry the vertical corner radius in user units
     * @return this element for method chaining
     */
    public RectElement ry(double ry) {
        setWriteOnlyAttribute("ry", String.valueOf(ry));
        return this;
    }

    /**
     * Sets the vertical corner radius of the rectangle with a unit or percentage.
     * <p>
     * If {@code rx} is not specified, it defaults to the value of {@code ry}.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #ryRW(String)} if you need to read the value back.
     * </p>
     *
     * @param ry the vertical corner radius (e.g., "10", "50%", "10px")
     * @return this element for method chaining
     */
    public RectElement ry(String ry) {
        setWriteOnlyAttribute("ry", ry);
        return this;
    }

    /**
     * Sets the vertical corner radius of the rectangle (read-write).
     *
     * @param ry the vertical corner radius in user units
     * @return this element for method chaining
     */
    public RectElement ryRW(double ry) {
        setAttribute("ry", String.valueOf(ry));
        return this;
    }

    /**
     * Sets the vertical corner radius of the rectangle with a unit or percentage (read-write).
     *
     * @param ry the vertical corner radius (e.g., "10", "50%", "10px")
     * @return this element for method chaining
     */
    public RectElement ryRW(String ry) {
        setAttribute("ry", ry);
        return this;
    }

    // ========== pathLength attribute ==========

    /**
     * Sets the total length of the rectangle's perimeter in user units.
     * <p>
     * This value is used to calibrate the browser's distance calculations
     * with those of the author, by scaling all distance computations using
     * the ratio pathLength / (computed value of perimeter).
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #pathLengthRW(double)} if you need to read the value back.
     * </p>
     *
     * @param pathLength the total path length in user units
     * @return this element for method chaining
     */
    public RectElement pathLength(double pathLength) {
        setWriteOnlyAttribute("pathLength", String.valueOf(pathLength));
        return this;
    }

    /**
     * Sets the total length of the rectangle's perimeter in user units (read-write).
     *
     * @param pathLength the total path length in user units
     * @return this element for method chaining
     */
    public RectElement pathLengthRW(double pathLength) {
        setAttribute("pathLength", String.valueOf(pathLength));
        return this;
    }

    // ========== Convenience methods ==========

    /**
     * Convenience method to set position (x, y) at once.
     * <p>
     * Uses write-only optimization. Use {@link #positionRW(double, double)} if you need to read the values back.
     * </p>
     *
     * @param x the x coordinate in user units
     * @param y the y coordinate in user units
     * @return this element for method chaining
     */
    public RectElement position(double x, double y) {
        return x(x).y(y);
    }

    /**
     * Convenience method to set position (x, y) at once (read-write).
     *
     * @param x the x coordinate in user units
     * @param y the y coordinate in user units
     * @return this element for method chaining
     */
    public RectElement positionRW(double x, double y) {
        return xRW(x).yRW(y);
    }

    /**
     * Convenience method to set size (width, height) at once.
     * <p>
     * Uses write-only optimization. Use {@link #sizeRW(double, double)} if you need to read the values back.
     * </p>
     *
     * @param width the width in user units
     * @param height the height in user units
     * @return this element for method chaining
     */
    public RectElement size(double width, double height) {
        return width(width).height(height);
    }

    /**
     * Convenience method to set size (width, height) at once (read-write).
     *
     * @param width the width in user units
     * @param height the height in user units
     * @return this element for method chaining
     */
    public RectElement sizeRW(double width, double height) {
        return widthRW(width).heightRW(height);
    }

    /**
     * Convenience method to set bounds (x, y, width, height) at once.
     * <p>
     * Uses write-only optimization. Use {@link #boundsRW(double, double, double, double)} if you need to read the values back.
     * </p>
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
     * Convenience method to set bounds (x, y, width, height) at once (read-write).
     *
     * @param x the x coordinate in user units
     * @param y the y coordinate in user units
     * @param width the width in user units
     * @param height the height in user units
     * @return this element for method chaining
     */
    public RectElement boundsRW(double x, double y, double width, double height) {
        return xRW(x).yRW(y).widthRW(width).heightRW(height);
    }

    /**
     * Convenience method to set equal corner radius for both axes.
     * <p>
     * Uses write-only optimization. Use {@link #cornerRadiusRW(double)} if you need to read the values back.
     * </p>
     *
     * @param radius the corner radius in user units
     * @return this element for method chaining
     */
    public RectElement cornerRadius(double radius) {
        return rx(radius).ry(radius);
    }

    /**
     * Convenience method to set equal corner radius for both axes (read-write).
     *
     * @param radius the corner radius in user units
     * @return this element for method chaining
     */
    public RectElement cornerRadiusRW(double radius) {
        return rxRW(radius).ryRW(radius);
    }

    /**
     * Convenience method to set different corner radii for horizontal and vertical axes.
     * <p>
     * Uses write-only optimization. Use {@link #cornerRadiusRW(double, double)} if you need to read the values back.
     * </p>
     *
     * @param rx the horizontal corner radius in user units
     * @param ry the vertical corner radius in user units
     * @return this element for method chaining
     */
    public RectElement cornerRadius(double rx, double ry) {
        return rx(rx).ry(ry);
    }

    /**
     * Convenience method to set different corner radii for horizontal and vertical axes (read-write).
     *
     * @param rx the horizontal corner radius in user units
     * @param ry the vertical corner radius in user units
     * @return this element for method chaining
     */
    public RectElement cornerRadiusRW(double rx, double ry) {
        return rxRW(rx).ryRW(ry);
    }
}
