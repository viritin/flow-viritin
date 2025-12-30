package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <line>} element.
 * <p>
 * The {@code <line>} element is a basic SVG shape that draws a straight line
 * connecting two points.
 * </p>
 * <p>
 * <strong>Note:</strong> A line element must have a stroke color specified
 * to be visible, as lines have no fill.
 * </p>
 * <h2>Write-Only vs Read-Write Methods</h2>
 * <p>
 * This class provides two variants for each attribute setter:
 * </p>
 * <ul>
 *   <li><strong>Default methods</strong> (e.g., {@code x1()}, {@code y1()}) - Use an optimized
 *       write-only approach. Attribute values are NOT stored on the server and cannot be
 *       retrieved via {@code getAttribute()}.</li>
 *   <li><strong>RW methods</strong> (e.g., {@code x1RW()}, {@code y1RW()}) - Use traditional
 *       {@code setAttribute()} which stores values on the server for later retrieval.</li>
 * </ul>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/line">MDN: line element</a>
 */
public class LineElement extends SvgGraphicsElement {

    public LineElement() {
        super("line");
    }

    // ========== x1 attribute ==========

    /**
     * Sets the x-axis coordinate of the line starting point.
     * <p>
     * Uses write-only optimization. The value is NOT stored on the server.
     * Use {@link #x1RW(double)} if you need to read the value back.
     * </p>
     *
     * @param x1 the x coordinate of the start point in user units
     * @return this element for method chaining
     */
    public LineElement x1(double x1) {
        setWriteOnlyAttribute("x1", String.valueOf(x1));
        return this;
    }

    /**
     * Sets the x-axis coordinate of the line starting point with a unit or percentage.
     * <p>
     * Uses write-only optimization. The value is NOT stored on the server.
     * Use {@link #x1RW(String)} if you need to read the value back.
     * </p>
     *
     * @param x1 the x coordinate of the start point (e.g., "10", "10%", "10px")
     * @return this element for method chaining
     */
    public LineElement x1(String x1) {
        setWriteOnlyAttribute("x1", x1);
        return this;
    }

    /**
     * Sets the x-axis coordinate of the line starting point (read-write).
     * <p>
     * Uses {@link #setAttribute(String, String)} so the value can be retrieved
     * via {@link #getAttribute(String)}.
     * </p>
     *
     * @param x1 the x coordinate of the start point in user units
     * @return this element for method chaining
     */
    public LineElement x1RW(double x1) {
        setAttribute("x1", String.valueOf(x1));
        return this;
    }

    /**
     * Sets the x-axis coordinate of the line starting point with a unit or percentage (read-write).
     * <p>
     * Uses {@link #setAttribute(String, String)} so the value can be retrieved
     * via {@link #getAttribute(String)}.
     * </p>
     *
     * @param x1 the x coordinate of the start point (e.g., "10", "10%", "10px")
     * @return this element for method chaining
     */
    public LineElement x1RW(String x1) {
        setAttribute("x1", x1);
        return this;
    }

    // ========== y1 attribute ==========

    /**
     * Sets the y-axis coordinate of the line starting point.
     * <p>
     * Uses write-only optimization. The value is NOT stored on the server.
     * Use {@link #y1RW(double)} if you need to read the value back.
     * </p>
     *
     * @param y1 the y coordinate of the start point in user units
     * @return this element for method chaining
     */
    public LineElement y1(double y1) {
        setWriteOnlyAttribute("y1", String.valueOf(y1));
        return this;
    }

    /**
     * Sets the y-axis coordinate of the line starting point with a unit or percentage.
     * <p>
     * Uses write-only optimization. The value is NOT stored on the server.
     * Use {@link #y1RW(String)} if you need to read the value back.
     * </p>
     *
     * @param y1 the y coordinate of the start point (e.g., "10", "10%", "10px")
     * @return this element for method chaining
     */
    public LineElement y1(String y1) {
        setWriteOnlyAttribute("y1", y1);
        return this;
    }

    /**
     * Sets the y-axis coordinate of the line starting point (read-write).
     * <p>
     * Uses {@link #setAttribute(String, String)} so the value can be retrieved
     * via {@link #getAttribute(String)}.
     * </p>
     *
     * @param y1 the y coordinate of the start point in user units
     * @return this element for method chaining
     */
    public LineElement y1RW(double y1) {
        setAttribute("y1", String.valueOf(y1));
        return this;
    }

    /**
     * Sets the y-axis coordinate of the line starting point with a unit or percentage (read-write).
     * <p>
     * Uses {@link #setAttribute(String, String)} so the value can be retrieved
     * via {@link #getAttribute(String)}.
     * </p>
     *
     * @param y1 the y coordinate of the start point (e.g., "10", "10%", "10px")
     * @return this element for method chaining
     */
    public LineElement y1RW(String y1) {
        setAttribute("y1", y1);
        return this;
    }

    // ========== x2 attribute ==========

    /**
     * Sets the x-axis coordinate of the line ending point.
     * <p>
     * Uses write-only optimization. The value is NOT stored on the server.
     * Use {@link #x2RW(double)} if you need to read the value back.
     * </p>
     *
     * @param x2 the x coordinate of the end point in user units
     * @return this element for method chaining
     */
    public LineElement x2(double x2) {
        setWriteOnlyAttribute("x2", String.valueOf(x2));
        return this;
    }

    /**
     * Sets the x-axis coordinate of the line ending point with a unit or percentage.
     * <p>
     * Uses write-only optimization. The value is NOT stored on the server.
     * Use {@link #x2RW(String)} if you need to read the value back.
     * </p>
     *
     * @param x2 the x coordinate of the end point (e.g., "90", "90%", "90px")
     * @return this element for method chaining
     */
    public LineElement x2(String x2) {
        setWriteOnlyAttribute("x2", x2);
        return this;
    }

    /**
     * Sets the x-axis coordinate of the line ending point (read-write).
     * <p>
     * Uses {@link #setAttribute(String, String)} so the value can be retrieved
     * via {@link #getAttribute(String)}.
     * </p>
     *
     * @param x2 the x coordinate of the end point in user units
     * @return this element for method chaining
     */
    public LineElement x2RW(double x2) {
        setAttribute("x2", String.valueOf(x2));
        return this;
    }

    /**
     * Sets the x-axis coordinate of the line ending point with a unit or percentage (read-write).
     * <p>
     * Uses {@link #setAttribute(String, String)} so the value can be retrieved
     * via {@link #getAttribute(String)}.
     * </p>
     *
     * @param x2 the x coordinate of the end point (e.g., "90", "90%", "90px")
     * @return this element for method chaining
     */
    public LineElement x2RW(String x2) {
        setAttribute("x2", x2);
        return this;
    }

    // ========== y2 attribute ==========

    /**
     * Sets the y-axis coordinate of the line ending point.
     * <p>
     * Uses write-only optimization. The value is NOT stored on the server.
     * Use {@link #y2RW(double)} if you need to read the value back.
     * </p>
     *
     * @param y2 the y coordinate of the end point in user units
     * @return this element for method chaining
     */
    public LineElement y2(double y2) {
        setWriteOnlyAttribute("y2", String.valueOf(y2));
        return this;
    }

    /**
     * Sets the y-axis coordinate of the line ending point with a unit or percentage.
     * <p>
     * Uses write-only optimization. The value is NOT stored on the server.
     * Use {@link #y2RW(String)} if you need to read the value back.
     * </p>
     *
     * @param y2 the y coordinate of the end point (e.g., "90", "90%", "90px")
     * @return this element for method chaining
     */
    public LineElement y2(String y2) {
        setWriteOnlyAttribute("y2", y2);
        return this;
    }

    /**
     * Sets the y-axis coordinate of the line ending point (read-write).
     * <p>
     * Uses {@link #setAttribute(String, String)} so the value can be retrieved
     * via {@link #getAttribute(String)}.
     * </p>
     *
     * @param y2 the y coordinate of the end point in user units
     * @return this element for method chaining
     */
    public LineElement y2RW(double y2) {
        setAttribute("y2", String.valueOf(y2));
        return this;
    }

    /**
     * Sets the y-axis coordinate of the line ending point with a unit or percentage (read-write).
     * <p>
     * Uses {@link #setAttribute(String, String)} so the value can be retrieved
     * via {@link #getAttribute(String)}.
     * </p>
     *
     * @param y2 the y coordinate of the end point (e.g., "90", "90%", "90px")
     * @return this element for method chaining
     */
    public LineElement y2RW(String y2) {
        setAttribute("y2", y2);
        return this;
    }

    // ========== pathLength attribute ==========

    /**
     * Sets the total length for the line's path in user units.
     * <p>
     * This value is used to calibrate the browser's distance calculations
     * with those of the author, by scaling all distance computations using
     * the ratio pathLength / (computed value of path length).
     * </p>
     * <p>
     * Uses write-only optimization. The value is NOT stored on the server.
     * Use {@link #pathLengthRW(double)} if you need to read the value back.
     * </p>
     *
     * @param pathLength the total path length in user units
     * @return this element for method chaining
     */
    public LineElement pathLength(double pathLength) {
        setWriteOnlyAttribute("pathLength", String.valueOf(pathLength));
        return this;
    }

    /**
     * Sets the total length for the line's path in user units (read-write).
     * <p>
     * Uses {@link #setAttribute(String, String)} so the value can be retrieved
     * via {@link #getAttribute(String)}.
     * </p>
     *
     * @param pathLength the total path length in user units
     * @return this element for method chaining
     */
    public LineElement pathLengthRW(double pathLength) {
        setAttribute("pathLength", String.valueOf(pathLength));
        return this;
    }

    // ========== Convenience methods ==========

    /**
     * Convenience method to set the starting point (x1, y1) at once.
     * <p>
     * Uses write-only optimization. Use {@link #fromRW(double, double)} if you
     * need to read the values back.
     * </p>
     *
     * @param x1 the x coordinate of the start point in user units
     * @param y1 the y coordinate of the start point in user units
     * @return this element for method chaining
     */
    public LineElement from(double x1, double y1) {
        return x1(x1).y1(y1);
    }

    /**
     * Convenience method to set the starting point with strings (supports units/percentages).
     * <p>
     * Uses write-only optimization. Use {@link #fromRW(String, String)} if you
     * need to read the values back.
     * </p>
     *
     * @param x1 the x coordinate of the start point (e.g., "10%")
     * @param y1 the y coordinate of the start point (e.g., "10%")
     * @return this element for method chaining
     */
    public LineElement from(String x1, String y1) {
        return x1(x1).y1(y1);
    }

    /**
     * Convenience method to set the starting point (x1, y1) at once (read-write).
     *
     * @param x1 the x coordinate of the start point in user units
     * @param y1 the y coordinate of the start point in user units
     * @return this element for method chaining
     */
    public LineElement fromRW(double x1, double y1) {
        return x1RW(x1).y1RW(y1);
    }

    /**
     * Convenience method to set the starting point with strings (read-write).
     *
     * @param x1 the x coordinate of the start point (e.g., "10%")
     * @param y1 the y coordinate of the start point (e.g., "10%")
     * @return this element for method chaining
     */
    public LineElement fromRW(String x1, String y1) {
        return x1RW(x1).y1RW(y1);
    }

    /**
     * Convenience method to set the ending point (x2, y2) at once.
     * <p>
     * Uses write-only optimization. Use {@link #toRW(double, double)} if you
     * need to read the values back.
     * </p>
     *
     * @param x2 the x coordinate of the end point in user units
     * @param y2 the y coordinate of the end point in user units
     * @return this element for method chaining
     */
    public LineElement to(double x2, double y2) {
        return x2(x2).y2(y2);
    }

    /**
     * Convenience method to set the ending point with strings (supports units/percentages).
     * <p>
     * Uses write-only optimization. Use {@link #toRW(String, String)} if you
     * need to read the values back.
     * </p>
     *
     * @param x2 the x coordinate of the end point (e.g., "90%")
     * @param y2 the y coordinate of the end point (e.g., "90%")
     * @return this element for method chaining
     */
    public LineElement to(String x2, String y2) {
        return x2(x2).y2(y2);
    }

    /**
     * Convenience method to set the ending point (x2, y2) at once (read-write).
     *
     * @param x2 the x coordinate of the end point in user units
     * @param y2 the y coordinate of the end point in user units
     * @return this element for method chaining
     */
    public LineElement toRW(double x2, double y2) {
        return x2RW(x2).y2RW(y2);
    }

    /**
     * Convenience method to set the ending point with strings (read-write).
     *
     * @param x2 the x coordinate of the end point (e.g., "90%")
     * @param y2 the y coordinate of the end point (e.g., "90%")
     * @return this element for method chaining
     */
    public LineElement toRW(String x2, String y2) {
        return x2RW(x2).y2RW(y2);
    }

    /**
     * Convenience method to set both start and end points at once.
     * <p>
     * Uses write-only optimization. Use {@link #pointsRW(double, double, double, double)}
     * if you need to read the values back.
     * </p>
     *
     * @param x1 the x coordinate of the start point in user units
     * @param y1 the y coordinate of the start point in user units
     * @param x2 the x coordinate of the end point in user units
     * @param y2 the y coordinate of the end point in user units
     * @return this element for method chaining
     */
    public LineElement points(double x1, double y1, double x2, double y2) {
        return from(x1, y1).to(x2, y2);
    }

    /**
     * Convenience method to set both start and end points at once (read-write).
     *
     * @param x1 the x coordinate of the start point in user units
     * @param y1 the y coordinate of the start point in user units
     * @param x2 the x coordinate of the end point in user units
     * @param y2 the y coordinate of the end point in user units
     * @return this element for method chaining
     */
    public LineElement pointsRW(double x1, double y1, double x2, double y2) {
        return fromRW(x1, y1).toRW(x2, y2);
    }
}