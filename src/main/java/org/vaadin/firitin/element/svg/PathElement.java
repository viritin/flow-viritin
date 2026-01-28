package org.vaadin.firitin.element.svg;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * A typed Java API for the SVG {@code <path>} element.
 * <p>
 * The {@code <path>} element is the generic element to define a shape.
 * All basic shapes can be created with a path element.
 * </p>
 * <p>
 * Use the {@link #d(String)} method to set the path data directly, or use
 * the fluent path builder methods like {@link #moveTo(double, double)},
 * {@link #lineTo(double, double)}, etc.
 * </p>
 * <p><b>Write-Only vs Read-Write Methods</b></p>
 * <p>
 * This class provides two variants for attribute setters:
 * </p>
 * <ul>
 *   <li><strong>Default methods</strong> (e.g., {@code d()}, {@code pathLength()}) - Use an optimized
 *       write-only approach. Attribute values are NOT stored on the server and cannot be
 *       retrieved via {@code getAttribute()}.</li>
 *   <li><strong>RW methods</strong> (e.g., {@code dRW()}, {@code pathLengthRW()}) - Use traditional
 *       {@code setAttribute()} which stores values on the server for later retrieval.</li>
 * </ul>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/path">MDN: path element</a>
 */
public class PathElement extends SvgGraphicsElement {

    private StringBuilder pathData;

    public PathElement() {
        super("path");
    }

    /**
     * Creates a path element with the given path data.
     *
     * @param pathConfigurator a consumer that configures the path builder
     */
    public PathElement(Consumer<PathBuilder> pathConfigurator) {
        super("path");
        d(pathConfigurator);
    }

    /**
     * Sets the path data directly.
     * <p>
     * This replaces any path data built using the fluent methods.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #dRW(String)} if you need to read the value back.
     * </p>
     *
     * @param d the path data string
     * @return this element for method chaining
     */
    public PathElement d(String d) {
        pathData = null;
        setWriteOnlyAttribute("d", d);
        return this;
    }

    /**
     * Sets the path data using a lambda expression that configures a PathBuilder.
     * <p>
     * This replaces any path data built using the fluent methods.
     * </p>
     * <p>
     * Example usage:
     * <pre>{@code
     * PathElement path = new PathElement()
     *     .d(p -> p
     *         .moveTo(10, 10)
     *         .lineTo(100, 10)
     *         .quadraticBezierTo(150, 10, 150, 60)
     *         .lineTo(150, 100)
     *         .closePath())
     *     .fill(HexColor.of("#3366cc"))
     *     .stroke(HexColor.of("#000000"));
     * }</pre>
     * <p>
     * Uses write-only optimization. Use {@link #dRW(Consumer)} if you need to read the value back.
     * </p>
     *
     * @param pathConfigurator a consumer that configures the path builder
     * @return this element for method chaining
     */
    public PathElement d(Consumer<PathBuilder> pathConfigurator) {
        pathData = null;
        PathBuilder builder = new PathBuilder();
        pathConfigurator.accept(builder);
        setWriteOnlyAttribute("d", builder.build());
        return this;
    }

    /**
     * Sets the path data directly (read-write).
     * <p>
     * This replaces any path data built using the fluent methods.
     * </p>
     *
     * @param d the path data string
     * @return this element for method chaining
     */
    public PathElement dRW(String d) {
        setAttribute("d", d);
        return this;
    }

    /**
     * Sets the path data using a lambda expression that configures a PathBuilder (read-write).
     * <p>
     * This replaces any path data built using the fluent methods.
     * </p>
     *
     * @param pathConfigurator a consumer that configures the path builder
     * @return this element for method chaining
     */
    public PathElement dRW(Consumer<PathBuilder> pathConfigurator) {
        PathBuilder builder = new PathBuilder();
        pathConfigurator.accept(builder);
        setAttribute("d", builder.build());
        return this;
    }

    /**
     * Clears the currently scheduled drawing commands.
     *
     * @return this element for method chaining
     */
    public PathElement clear() {
        pathData = new StringBuilder();
        scheduleBeforeClientResponse();
        return this;
    }

    /**
     * Sets the total length for the path in user units.
     * <p>
     * Uses write-only optimization. Use {@link #pathLengthRW(double)} if you need to read the value back.
     * </p>
     *
     * @param pathLength the total path length
     * @return this element for method chaining
     */
    public PathElement pathLength(double pathLength) {
        setWriteOnlyAttribute("pathLength", String.valueOf(pathLength));
        return this;
    }

    /**
     * Sets the total length for the path in user units (read-write).
     *
     * @param pathLength the total path length
     * @return this element for method chaining
     */
    public PathElement pathLengthRW(double pathLength) {
        setAttribute("pathLength", String.valueOf(pathLength));
        return this;
    }

    // ========== Animation Methods ==========

    /**
     * Animates the path data (d attribute) from its current value to a new path.
     * <p>
     * Both paths should have the same number and types of commands for smooth animation.
     * If the paths have different structures, the browser will attempt to interpolate
     * but results may be unpredictable.
     * </p>
     * <p><b>Example usage:</b></p>
     * <pre>{@code
     * pathElement.animateD(
     *     to -> to.moveTo(10, 10).lineTo(100, 10).lineTo(100, 100),
     *     Duration.ofMillis(400)
     * );
     * }</pre>
     *
     * @param to a consumer that configures the ending path
     * @param duration the animation duration
     * @return the created AnimateElement for further configuration (e.g., easing, repeat)
     */
    public AnimateElement animateD(Consumer<PathBuilder> to, Duration duration) {
        PathBuilder toBuilder = new PathBuilder();
        to.accept(toBuilder);

        AnimateElement anim = new AnimateElement()
                .attributeName("d")
                .to(toBuilder.build())
                .dur(duration)
                .freeze();
        appendChild(anim);
        anim.beginElement();
        return anim;
    }

    /**
     * Animates the path data (d attribute) from one path to another.
     * <p>
     * Both paths should have the same number and types of commands for smooth animation.
     * If the paths have different structures, the browser will attempt to interpolate
     * but results may be unpredictable.
     * </p>
     * <p><b>Example usage:</b></p>
     * <pre>{@code
     * pathElement.animateD(
     *     from -> from.moveTo(10, 10).lineTo(50, 10).lineTo(50, 50),
     *     to -> to.moveTo(10, 10).lineTo(100, 10).lineTo(100, 100),
     *     Duration.ofMillis(400)
     * );
     * }</pre>
     *
     * @param from a consumer that configures the starting path
     * @param to a consumer that configures the ending path
     * @param duration the animation duration
     * @return the created AnimateElement for further configuration (e.g., easing, repeat)
     */
    public AnimateElement animateD(Consumer<PathBuilder> from, Consumer<PathBuilder> to, Duration duration) {
        PathBuilder fromBuilder = new PathBuilder();
        from.accept(fromBuilder);

        PathBuilder toBuilder = new PathBuilder();
        to.accept(toBuilder);

        AnimateElement anim = new AnimateElement()
                .attributeName("d")
                .from(fromBuilder.build())
                .to(toBuilder.build())
                .dur(duration)
                .freeze();
        appendChild(anim);
        anim.beginElement();
        return anim;
    }

    // ========== Path Commands (Fluent Builder) ==========

    /**
     * Move to the specified point (absolute coordinates).
     * <p>
     * Equivalent to the "M" command.
     * </p>
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public PathElement moveTo(double x, double y) {
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
     * @return this element for method chaining
     */
    public PathElement moveToRelative(double dx, double dy) {
        appendCommand("m", dx, dy);
        return this;
    }

    /**
     * Draw a line to the specified point (absolute coordinates).
     * <p>
     * Equivalent to the "L" command.
     * </p>
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public PathElement lineTo(double x, double y) {
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
     * @return this element for method chaining
     */
    public PathElement lineToRelative(double dx, double dy) {
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
     * @return this element for method chaining
     */
    public PathElement horizontalLineTo(double x) {
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
     * @return this element for method chaining
     */
    public PathElement horizontalLineToRelative(double dx) {
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
     * @return this element for method chaining
     */
    public PathElement verticalLineTo(double y) {
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
     * @return this element for method chaining
     */
    public PathElement verticalLineToRelative(double dy) {
        appendCommand("v", dy);
        return this;
    }

    /**
     * Close the current path by drawing a straight line back to the start.
     * <p>
     * Equivalent to the "Z" or "z" command.
     * </p>
     *
     * @return this element for method chaining
     */
    public PathElement closePath() {
        pathData.append(" Z");
        scheduleBeforeClientResponse();
        return this;
    }

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
     * @return this element for method chaining
     */
    public PathElement cubicBezierTo(double x1, double y1, double x2, double y2, double x, double y) {
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
     * @return this element for method chaining
     */
    public PathElement cubicBezierToRelative(double dx1, double dy1, double dx2, double dy2, double dx, double dy) {
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
     * @return this element for method chaining
     */
    public PathElement smoothCubicBezierTo(double x2, double y2, double x, double y) {
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
     * @return this element for method chaining
     */
    public PathElement smoothCubicBezierToRelative(double dx2, double dy2, double dx, double dy) {
        appendCommand("s", dx2, dy2, dx, dy);
        return this;
    }

    /**
     * Draw a quadratic Bézier curve (absolute coordinates).
     * <p>
     * Equivalent to the "Q" command.
     * </p>
     *
     * @param x1 x coordinate of the control point
     * @param y1 y coordinate of the control point
     * @param x  x coordinate of the end point
     * @param y  y coordinate of the end point
     * @return this element for method chaining
     */
    public PathElement quadraticBezierTo(double x1, double y1, double x, double y) {
        appendCommand("Q", x1, y1, x, y);
        return this;
    }

    /**
     * Draw a quadratic Bézier curve (relative coordinates).
     * <p>
     * Equivalent to the "q" command.
     * </p>
     *
     * @param dx1 relative x offset of the control point
     * @param dy1 relative y offset of the control point
     * @param dx  relative x offset of the end point
     * @param dy  relative y offset of the end point
     * @return this element for method chaining
     */
    public PathElement quadraticBezierToRelative(double dx1, double dy1, double dx, double dy) {
        appendCommand("q", dx1, dy1, dx, dy);
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
     * @return this element for method chaining
     */
    public PathElement smoothQuadraticBezierTo(double x, double y) {
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
     * @return this element for method chaining
     */
    public PathElement smoothQuadraticBezierToRelative(double dx, double dy) {
        appendCommand("t", dx, dy);
        return this;
    }

    /**
     * Draw an elliptical arc (absolute coordinates).
     * <p>
     * Equivalent to the "A" command.
     * </p>
     *
     * @param rx             x radius of the ellipse
     * @param ry             y radius of the ellipse
     * @param xAxisRotation  rotation of the ellipse in degrees
     * @param largeArcFlag   if true, draw the larger arc
     * @param sweepFlag      if true, draw the arc in positive angle direction
     * @param x              x coordinate of the end point
     * @param y              y coordinate of the end point
     * @return this element for method chaining
     */
    public PathElement arcTo(double rx, double ry, double xAxisRotation,
                             boolean largeArcFlag, boolean sweepFlag, double x, double y) {
        pathData.append(" A ")
                .append(rx).append(",").append(ry).append(" ")
                .append(xAxisRotation).append(" ")
                .append(largeArcFlag ? 1 : 0).append(",")
                .append(sweepFlag ? 1 : 0).append(" ")
                .append(x).append(",").append(y);
        scheduleBeforeClientResponse();
        return this;
    }

    /**
     * Draw an elliptical arc (relative coordinates).
     * <p>
     * Equivalent to the "a" command.
     * </p>
     *
     * @param rx             x radius of the ellipse
     * @param ry             y radius of the ellipse
     * @param xAxisRotation  rotation of the ellipse in degrees
     * @param largeArcFlag   if true, draw the larger arc
     * @param sweepFlag      if true, draw the arc in positive angle direction
     * @param dx             relative x offset of the end point
     * @param dy             relative y offset of the end point
     * @return this element for method chaining
     */
    public PathElement arcToRelative(double rx, double ry, double xAxisRotation,
                                     boolean largeArcFlag, boolean sweepFlag, double dx, double dy) {
        pathData.append(" a ")
                .append(rx).append(",").append(ry).append(" ")
                .append(xAxisRotation).append(" ")
                .append(largeArcFlag ? 1 : 0).append(",")
                .append(sweepFlag ? 1 : 0).append(" ")
                .append(dx).append(",").append(dy);
        scheduleBeforeClientResponse();
        return this;
    }

    // ========== Private helpers ==========

    private void appendCommand(String command, double... values) {
        if(pathData == null) {
            pathData = new StringBuilder();
        } else {
            pathData.append(" ");
        }

        pathData.append(command);
        for (int i = 0; i < values.length; i++) {
            if (i > 0) pathData.append(",");
            pathData.append(values[i]);
        }
        scheduleBeforeClientResponse();
    }

    @Override
    protected void flushPendingAttributes() {
        if(pathData != null) {
            setWriteOnlyAttribute("d", pathData.toString());
            pathData = null;
        }
        super.flushPendingAttributes();
    }
}
