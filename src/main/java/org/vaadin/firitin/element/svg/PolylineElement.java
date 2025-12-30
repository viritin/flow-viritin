package org.vaadin.firitin.element.svg;

import java.util.ArrayList;
import java.util.List;

/**
 * A typed Java API for the SVG {@code <polyline>} element.
 * <p>
 * The {@code <polyline>} element defines a shape consisting of a set of
 * connected straight line segments. Unlike a polygon, the last point is
 * NOT automatically connected to the first point.
 * </p>
 * <p>
 * For a closed shape (where the last point connects to the first),
 * use a polygon element instead.
 * </p>
 * <h2>Write-Only vs Read-Write Methods</h2>
 * <p>
 * This class provides two variants for attribute setters:
 * </p>
 * <ul>
 *   <li><strong>Default methods</strong> (e.g., {@code points()}, {@code pathLength()}) - Use an optimized
 *       write-only approach. Attribute values are NOT stored on the server and cannot be
 *       retrieved via {@code getAttribute()}.</li>
 *   <li><strong>RW methods</strong> (e.g., {@code pointsRW()}, {@code pathLengthRW()}) - Use traditional
 *       {@code setAttribute()} which stores values on the server for later retrieval.</li>
 * </ul>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/polyline">MDN: polyline element</a>
 */
public class PolylineElement extends SvgGraphicsElement {

    private final List<double[]> pointsList = new ArrayList<>();

    public PolylineElement() {
        super("polyline");
    }

    /**
     * Sets the points directly as a string.
     * <p>
     * This replaces any points added using the fluent methods.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #pointsRW(String)} if you need to read the value back.
     * </p>
     *
     * @param points the points string (e.g., "0,40 40,40 40,80 80,80 80,120")
     * @return this element for method chaining
     */
    public PolylineElement points(String points) {
        pointsList.clear();
        setWriteOnlyAttribute("points", points);
        return this;
    }

    /**
     * Sets the points directly as a string (read-write).
     * <p>
     * This replaces any points added using the fluent methods.
     * </p>
     *
     * @param points the points string (e.g., "0,40 40,40 40,80 80,80 80,120")
     * @return this element for method chaining
     */
    public PolylineElement pointsRW(String points) {
        pointsList.clear();
        setAttribute("points", points);
        return this;
    }

    /**
     * Sets the points from an array of coordinate pairs.
     * <p>
     * Each pair of values represents an x,y coordinate.
     * </p>
     *
     * @param coords the coordinates as x1,y1,x2,y2,... sequence
     * @return this element for method chaining
     */
    public PolylineElement points(double... coords) {
        if (coords.length % 2 != 0) {
            throw new IllegalArgumentException("Coordinates must be provided in pairs (x,y)");
        }
        pointsList.clear();
        for (int i = 0; i < coords.length; i += 2) {
            pointsList.add(new double[]{coords[i], coords[i + 1]});
        }
        updatePointsAttribute();
        return this;
    }

    /**
     * Adds a point to the polyline.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public PolylineElement addPoint(double x, double y) {
        pointsList.add(new double[]{x, y});
        updatePointsAttribute();
        return this;
    }

    /**
     * Adds multiple points to the polyline.
     *
     * @param coords the coordinates as x1,y1,x2,y2,... sequence
     * @return this element for method chaining
     */
    public PolylineElement addPoints(double... coords) {
        if (coords.length % 2 != 0) {
            throw new IllegalArgumentException("Coordinates must be provided in pairs (x,y)");
        }
        for (int i = 0; i < coords.length; i += 2) {
            pointsList.add(new double[]{coords[i], coords[i + 1]});
        }
        updatePointsAttribute();
        return this;
    }

    /**
     * Clears all points from the polyline.
     *
     * @return this element for method chaining
     */
    public PolylineElement clearPoints() {
        pointsList.clear();
        setWriteOnlyAttribute("points", "");
        return this;
    }

    /**
     * Sets the total length for the polyline in user units.
     * <p>
     * Uses write-only optimization. Use {@link #pathLengthRW(double)} if you need to read the value back.
     * </p>
     *
     * @param pathLength the total path length
     * @return this element for method chaining
     */
    public PolylineElement pathLength(double pathLength) {
        setWriteOnlyAttribute("pathLength", String.valueOf(pathLength));
        return this;
    }

    /**
     * Sets the total length for the polyline in user units (read-write).
     *
     * @param pathLength the total path length
     * @return this element for method chaining
     */
    public PolylineElement pathLengthRW(double pathLength) {
        setAttribute("pathLength", String.valueOf(pathLength));
        return this;
    }

    // ========== Private helpers ==========

    private void updatePointsAttribute() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pointsList.size(); i++) {
            if (i > 0) sb.append(" ");
            double[] point = pointsList.get(i);
            sb.append(point[0]).append(",").append(point[1]);
        }
        setWriteOnlyAttribute("points", sb.toString());
    }
}
