package org.vaadin.firitin.element.svg;

/**
 * A fluent builder for SVG path data strings.
 * <p>
 * This class provides a typed API for building SVG path definitions
 * that can be used with both {@link PathElement} and {@link AnimateMotionElement}.
 * </p>
 * <p><b>Example usage with PathElement:</b></p>
 * <pre>{@code
 * PathElement path = new PathElement()
 *     .d(p -> p
 *         .moveTo(50, 50)
 *         .lineTo(100, 50)
 *         .quadraticBezierTo(150, 50, 150, 100)
 *         .lineTo(150, 150)
 *         .closePath())
 *     .fill(HexColor.of("#3366cc"));
 * }</pre>
 * <p><b>Example usage with AnimateMotionElement:</b></p>
 * <pre>{@code
 * rect.animateMotion()
 *     .path(p -> p
 *         .moveTo(50, 80)
 *         .horizontalLineTo(250)
 *         .quadraticBezierTo(280, 80, 280, 60)
 *         .closePath())
 *     .dur(Duration.ofSeconds(3))
 *     .rotateAuto()
 *     .repeatIndefinitely();
 * }</pre>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Tutorial/Paths">MDN: SVG Paths</a>
 */
public class PathBuilder {

    private final StringBuilder pathData = new StringBuilder();

    /**
     * Creates an empty path builder.
     * <p>
     * Package-private constructor. Use the lambda-based API on elements instead:
     * <pre>{@code
     * pathElement.d(p -> p.moveTo(10, 10).lineTo(100, 100));
     * animateMotion.path(p -> p.moveTo(0, 0).horizontalLineTo(300));
     * }</pre>
     */
    PathBuilder() {
    }

    // ========== Move Commands ==========

    /**
     * Move to the specified point (absolute coordinates).
     * <p>
     * Equivalent to the "M" command.
     * </p>
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return this builder for method chaining
     */
    public PathBuilder moveTo(double x, double y) {
        appendCommand("M", x, y);
        return this;
    }

    /**
     * Move to the specified point (relative coordinates).
     * <p>
     * Equivalent to the "m" command.
     * </p>
     *
     * @param dx the relative x offset
     * @param dy the relative y offset
     * @return this builder for method chaining
     */
    public PathBuilder moveToRelative(double dx, double dy) {
        appendCommand("m", dx, dy);
        return this;
    }

    // ========== Line Commands ==========

    /**
     * Draw a line to the specified point (absolute coordinates).
     * <p>
     * Equivalent to the "L" command.
     * </p>
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return this builder for method chaining
     */
    public PathBuilder lineTo(double x, double y) {
        appendCommand("L", x, y);
        return this;
    }

    /**
     * Draw a line to the specified point (relative coordinates).
     * <p>
     * Equivalent to the "l" command.
     * </p>
     *
     * @param dx the relative x offset
     * @param dy the relative y offset
     * @return this builder for method chaining
     */
    public PathBuilder lineToRelative(double dx, double dy) {
        appendCommand("l", dx, dy);
        return this;
    }

    /**
     * Draw a horizontal line to the specified x coordinate (absolute).
     * <p>
     * Equivalent to the "H" command.
     * </p>
     *
     * @param x the x coordinate
     * @return this builder for method chaining
     */
    public PathBuilder horizontalLineTo(double x) {
        appendCommand("H", x);
        return this;
    }

    /**
     * Draw a horizontal line by the specified offset (relative).
     * <p>
     * Equivalent to the "h" command.
     * </p>
     *
     * @param dx the relative x offset
     * @return this builder for method chaining
     */
    public PathBuilder horizontalLineToRelative(double dx) {
        appendCommand("h", dx);
        return this;
    }

    /**
     * Draw a vertical line to the specified y coordinate (absolute).
     * <p>
     * Equivalent to the "V" command.
     * </p>
     *
     * @param y the y coordinate
     * @return this builder for method chaining
     */
    public PathBuilder verticalLineTo(double y) {
        appendCommand("V", y);
        return this;
    }

    /**
     * Draw a vertical line by the specified offset (relative).
     * <p>
     * Equivalent to the "v" command.
     * </p>
     *
     * @param dy the relative y offset
     * @return this builder for method chaining
     */
    public PathBuilder verticalLineToRelative(double dy) {
        appendCommand("v", dy);
        return this;
    }

    // ========== Close Path ==========

    /**
     * Close the current path by drawing a straight line back to the start.
     * <p>
     * Equivalent to the "Z" or "z" command.
     * </p>
     *
     * @return this builder for method chaining
     */
    public PathBuilder closePath() {
        if (!pathData.isEmpty()) {
            pathData.append(" ");
        }
        pathData.append("Z");
        return this;
    }

    // ========== Cubic Bézier Commands ==========

    /**
     * Draw a cubic Bézier curve (absolute coordinates).
     * <p>
     * Equivalent to the "C" command.
     * </p>
     *
     * @param x1 x coordinate of the first control point
     * @param y1 y coordinate of the first control point
     * @param x2 x coordinate of the second control point
     * @param y2 y coordinate of the second control point
     * @param x  x coordinate of the end point
     * @param y  y coordinate of the end point
     * @return this builder for method chaining
     */
    public PathBuilder cubicBezierTo(double x1, double y1, double x2, double y2, double x, double y) {
        appendCommand("C", x1, y1, x2, y2, x, y);
        return this;
    }

    /**
     * Draw a cubic Bézier curve (relative coordinates).
     * <p>
     * Equivalent to the "c" command.
     * </p>
     *
     * @param dx1 relative x offset of the first control point
     * @param dy1 relative y offset of the first control point
     * @param dx2 relative x offset of the second control point
     * @param dy2 relative y offset of the second control point
     * @param dx  relative x offset of the end point
     * @param dy  relative y offset of the end point
     * @return this builder for method chaining
     */
    public PathBuilder cubicBezierToRelative(double dx1, double dy1, double dx2, double dy2, double dx, double dy) {
        appendCommand("c", dx1, dy1, dx2, dy2, dx, dy);
        return this;
    }

    /**
     * Draw a smooth cubic Bézier curve (absolute coordinates).
     * <p>
     * Equivalent to the "S" command. The first control point is assumed to be
     * the reflection of the second control point of the previous command.
     * </p>
     *
     * @param x2 x coordinate of the second control point
     * @param y2 y coordinate of the second control point
     * @param x  x coordinate of the end point
     * @param y  y coordinate of the end point
     * @return this builder for method chaining
     */
    public PathBuilder smoothCubicBezierTo(double x2, double y2, double x, double y) {
        appendCommand("S", x2, y2, x, y);
        return this;
    }

    /**
     * Draw a smooth cubic Bézier curve (relative coordinates).
     * <p>
     * Equivalent to the "s" command.
     * </p>
     *
     * @param dx2 relative x offset of the second control point
     * @param dy2 relative y offset of the second control point
     * @param dx  relative x offset of the end point
     * @param dy  relative y offset of the end point
     * @return this builder for method chaining
     */
    public PathBuilder smoothCubicBezierToRelative(double dx2, double dy2, double dx, double dy) {
        appendCommand("s", dx2, dy2, dx, dy);
        return this;
    }

    // ========== Quadratic Bézier Commands ==========

    /**
     * Draw a quadratic Bézier curve (absolute coordinates).
     * <p>
     * Equivalent to the "Q" command.
     * </p>
     *
     * @param cx x coordinate of the control point
     * @param cy y coordinate of the control point
     * @param x  x coordinate of the end point
     * @param y  y coordinate of the end point
     * @return this builder for method chaining
     */
    public PathBuilder quadraticBezierTo(double cx, double cy, double x, double y) {
        appendCommand("Q", cx, cy, x, y);
        return this;
    }

    /**
     * Draw a quadratic Bézier curve (relative coordinates).
     * <p>
     * Equivalent to the "q" command.
     * </p>
     *
     * @param dcx relative x offset of the control point
     * @param dcy relative y offset of the control point
     * @param dx  relative x offset of the end point
     * @param dy  relative y offset of the end point
     * @return this builder for method chaining
     */
    public PathBuilder quadraticBezierToRelative(double dcx, double dcy, double dx, double dy) {
        appendCommand("q", dcx, dcy, dx, dy);
        return this;
    }

    /**
     * Draw a smooth quadratic Bézier curve (absolute coordinates).
     * <p>
     * Equivalent to the "T" command. The control point is assumed to be
     * the reflection of the control point of the previous command.
     * </p>
     *
     * @param x x coordinate of the end point
     * @param y y coordinate of the end point
     * @return this builder for method chaining
     */
    public PathBuilder smoothQuadraticBezierTo(double x, double y) {
        appendCommand("T", x, y);
        return this;
    }

    /**
     * Draw a smooth quadratic Bézier curve (relative coordinates).
     * <p>
     * Equivalent to the "t" command.
     * </p>
     *
     * @param dx relative x offset of the end point
     * @param dy relative y offset of the end point
     * @return this builder for method chaining
     */
    public PathBuilder smoothQuadraticBezierToRelative(double dx, double dy) {
        appendCommand("t", dx, dy);
        return this;
    }

    // ========== Arc Commands ==========

    /**
     * Draw an elliptical arc (absolute coordinates).
     * <p>
     * Equivalent to the "A" command.
     * </p>
     *
     * @param rx            x radius of the ellipse
     * @param ry            y radius of the ellipse
     * @param xAxisRotation rotation of the ellipse in degrees
     * @param largeArc      if true, draw the larger arc
     * @param sweep         if true, draw the arc in positive angle direction
     * @param x             x coordinate of the end point
     * @param y             y coordinate of the end point
     * @return this builder for method chaining
     */
    public PathBuilder arcTo(double rx, double ry, double xAxisRotation,
                             boolean largeArc, boolean sweep, double x, double y) {
        if (!pathData.isEmpty()) {
            pathData.append(" ");
        }
        pathData.append("A")
                .append(rx).append(",").append(ry).append(" ")
                .append(xAxisRotation).append(" ")
                .append(largeArc ? 1 : 0).append(",")
                .append(sweep ? 1 : 0).append(" ")
                .append(x).append(",").append(y);
        return this;
    }

    /**
     * Draw an elliptical arc (relative coordinates).
     * <p>
     * Equivalent to the "a" command.
     * </p>
     *
     * @param rx            x radius of the ellipse
     * @param ry            y radius of the ellipse
     * @param xAxisRotation rotation of the ellipse in degrees
     * @param largeArc      if true, draw the larger arc
     * @param sweep         if true, draw the arc in positive angle direction
     * @param dx            relative x offset of the end point
     * @param dy            relative y offset of the end point
     * @return this builder for method chaining
     */
    public PathBuilder arcToRelative(double rx, double ry, double xAxisRotation,
                                     boolean largeArc, boolean sweep, double dx, double dy) {
        if (!pathData.isEmpty()) {
            pathData.append(" ");
        }
        pathData.append("a")
                .append(rx).append(",").append(ry).append(" ")
                .append(xAxisRotation).append(" ")
                .append(largeArc ? 1 : 0).append(",")
                .append(sweep ? 1 : 0).append(" ")
                .append(dx).append(",").append(dy);
        return this;
    }

    // ========== Build ==========

    /**
     * Builds and returns the path data string.
     *
     * @return the SVG path data string
     */
    public String build() {
        return pathData.toString();
    }

    /**
     * Returns the path data string.
     * <p>
     * Equivalent to {@link #build()}.
     * </p>
     *
     * @return the SVG path data string
     */
    @Override
    public String toString() {
        return build();
    }

    // ========== Private Helpers ==========

    private void appendCommand(String command, double... values) {
        if (!pathData.isEmpty()) {
            pathData.append(" ");
        }
        pathData.append(command);
        for (int i = 0; i < values.length; i++) {
            if (i > 0) pathData.append(",");
            pathData.append(values[i]);
        }
    }

    // ========== Static Factory Methods for Common Shapes ==========

    /**
     * Creates a rectangular path.
     *
     * @param x      x coordinate of the top-left corner
     * @param y      y coordinate of the top-left corner
     * @param width  width of the rectangle
     * @param height height of the rectangle
     * @return a new PathBuilder with the rectangle path
     */
    public static PathBuilder rectangle(double x, double y, double width, double height) {
        return new PathBuilder()
                .moveTo(x, y)
                .horizontalLineToRelative(width)
                .verticalLineToRelative(height)
                .horizontalLineToRelative(-width)
                .closePath();
    }

    /**
     * Creates a rounded rectangular path.
     *
     * @param x      x coordinate of the top-left corner
     * @param y      y coordinate of the top-left corner
     * @param width  width of the rectangle
     * @param height height of the rectangle
     * @param radius corner radius
     * @return a new PathBuilder with the rounded rectangle path
     */
    public static PathBuilder roundedRectangle(double x, double y, double width, double height, double radius) {
        return new PathBuilder()
                .moveTo(x + radius, y)
                .horizontalLineTo(x + width - radius)
                .quadraticBezierTo(x + width, y, x + width, y + radius)
                .verticalLineTo(y + height - radius)
                .quadraticBezierTo(x + width, y + height, x + width - radius, y + height)
                .horizontalLineTo(x + radius)
                .quadraticBezierTo(x, y + height, x, y + height - radius)
                .verticalLineTo(y + radius)
                .quadraticBezierTo(x, y, x + radius, y)
                .closePath();
    }

    /**
     * Creates a circular path.
     *
     * @param cx x coordinate of the center
     * @param cy y coordinate of the center
     * @param r  radius
     * @return a new PathBuilder with the circle path
     */
    public static PathBuilder circle(double cx, double cy, double r) {
        return new PathBuilder()
                .moveTo(cx + r, cy)
                .arcTo(r, r, 0, false, true, cx - r, cy)
                .arcTo(r, r, 0, false, true, cx + r, cy)
                .closePath();
    }

    /**
     * Creates an elliptical path.
     *
     * @param cx x coordinate of the center
     * @param cy y coordinate of the center
     * @param rx horizontal radius
     * @param ry vertical radius
     * @return a new PathBuilder with the ellipse path
     */
    public static PathBuilder ellipse(double cx, double cy, double rx, double ry) {
        return new PathBuilder()
                .moveTo(cx + rx, cy)
                .arcTo(rx, ry, 0, false, true, cx - rx, cy)
                .arcTo(rx, ry, 0, false, true, cx + rx, cy)
                .closePath();
    }
}