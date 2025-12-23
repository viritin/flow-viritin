package org.vaadin.firitin.element.svg;

import in.virit.color.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * A typed Java API for the SVG {@code <polygon>} element.
 * <p>
 * The {@code <polygon>} element defines a closed shape consisting of a set of
 * connected straight line segments. The last point is connected to the first point.
 * </p>
 * <p>
 * For an open shape (where the last point is not connected to the first),
 * use a polyline element instead.
 * </p>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/polygon">MDN: polygon element</a>
 */
public class PolygonElement extends SvgElement {

    private final List<double[]> pointsList = new ArrayList<>();

    public PolygonElement() {
        super("polygon");
    }

    /**
     * Sets the points directly as a string.
     * <p>
     * This replaces any points added using the fluent methods.
     * </p>
     *
     * @param points the points string (e.g., "0,100 50,25 50,75 100,0")
     * @return this element for method chaining
     */
    public PolygonElement points(String points) {
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
    public PolygonElement points(double... coords) {
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
     * Adds a point to the polygon.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public PolygonElement addPoint(double x, double y) {
        pointsList.add(new double[]{x, y});
        updatePointsAttribute();
        return this;
    }

    /**
     * Adds multiple points to the polygon.
     *
     * @param coords the coordinates as x1,y1,x2,y2,... sequence
     * @return this element for method chaining
     */
    public PolygonElement addPoints(double... coords) {
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
     * Clears all points from the polygon.
     *
     * @return this element for method chaining
     */
    public PolygonElement clearPoints() {
        pointsList.clear();
        setAttribute("points", "");
        return this;
    }

    /**
     * Sets the total length for the polygon's perimeter in user units.
     *
     * @param pathLength the total path length
     * @return this element for method chaining
     */
    public PolygonElement pathLength(double pathLength) {
        setAttribute("pathLength", String.valueOf(pathLength));
        return this;
    }

    // ========== Convenience factory methods for common shapes ==========

    /**
     * Creates a regular polygon with the specified number of sides.
     *
     * @param cx     the x coordinate of the center
     * @param cy     the y coordinate of the center
     * @param radius the radius (distance from center to vertices)
     * @param sides  the number of sides (minimum 3)
     * @return this element for method chaining
     */
    public PolygonElement regularPolygon(double cx, double cy, double radius, int sides) {
        if (sides < 3) {
            throw new IllegalArgumentException("A polygon must have at least 3 sides");
        }
        pointsList.clear();
        double angleStep = 2 * Math.PI / sides;
        // Start from top (negative y direction)
        double startAngle = -Math.PI / 2;
        for (int i = 0; i < sides; i++) {
            double angle = startAngle + i * angleStep;
            double x = cx + radius * Math.cos(angle);
            double y = cy + radius * Math.sin(angle);
            pointsList.add(new double[]{x, y});
        }
        updatePointsAttribute();
        return this;
    }

    /**
     * Creates a triangle with the specified vertices.
     *
     * @param x1 x coordinate of first vertex
     * @param y1 y coordinate of first vertex
     * @param x2 x coordinate of second vertex
     * @param y2 y coordinate of second vertex
     * @param x3 x coordinate of third vertex
     * @param y3 y coordinate of third vertex
     * @return this element for method chaining
     */
    public PolygonElement triangle(double x1, double y1, double x2, double y2, double x3, double y3) {
        return points(x1, y1, x2, y2, x3, y3);
    }

    /**
     * Creates a star shape.
     *
     * @param cx          the x coordinate of the center
     * @param cy          the y coordinate of the center
     * @param outerRadius the outer radius (to the points)
     * @param innerRadius the inner radius (to the inner corners)
     * @param points      the number of points on the star (minimum 3)
     * @return this element for method chaining
     */
    public PolygonElement star(double cx, double cy, double outerRadius, double innerRadius, int points) {
        if (points < 3) {
            throw new IllegalArgumentException("A star must have at least 3 points");
        }
        pointsList.clear();
        double angleStep = Math.PI / points;
        double startAngle = -Math.PI / 2;
        for (int i = 0; i < points * 2; i++) {
            double angle = startAngle + i * angleStep;
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            double x = cx + radius * Math.cos(angle);
            double y = cy + radius * Math.sin(angle);
            pointsList.add(new double[]{x, y});
        }
        updatePointsAttribute();
        return this;
    }

    // ========== Stroke and fill helpers ==========

    /**
     * Sets the stroke color of the polygon.
     *
     * @param color the stroke color
     * @return this element for method chaining
     */
    public PolygonElement stroke(Color color) {
        setAttribute("stroke", color.toString());
        return this;
    }

    /**
     * Sets the stroke color of the polygon.
     *
     * @param stroke the stroke color (e.g., "black", "#000", "rgb(0,0,0)")
     * @return this element for method chaining
     */
    public PolygonElement stroke(String stroke) {
        setAttribute("stroke", stroke);
        return this;
    }

    /**
     * Sets the stroke width of the polygon.
     *
     * @param width the stroke width in user units
     * @return this element for method chaining
     */
    public PolygonElement strokeWidth(double width) {
        setAttribute("stroke-width", String.valueOf(width));
        return this;
    }

    /**
     * Sets the fill color of the polygon.
     *
     * @param color the fill color
     * @return this element for method chaining
     */
    public PolygonElement fill(Color color) {
        setFill(color);
        return this;
    }

    /**
     * Sets the fill to none (transparent).
     *
     * @return this element for method chaining
     */
    public PolygonElement noFill() {
        setAttribute("fill", "none");
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
        setAttribute("points", sb.toString());
    }
}
