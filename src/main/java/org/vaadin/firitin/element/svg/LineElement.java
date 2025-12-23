package org.vaadin.firitin.element.svg;

import in.virit.color.Color;

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
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/line">MDN: line element</a>
 */
public class LineElement extends SvgElement {

    public LineElement() {
        super("line");
    }

    /**
     * Sets the x-axis coordinate of the line starting point.
     *
     * @param x1 the x coordinate of the start point in user units
     * @return this element for method chaining
     */
    public LineElement x1(double x1) {
        setAttribute("x1", String.valueOf(x1));
        return this;
    }

    /**
     * Sets the x-axis coordinate of the line starting point with a unit or percentage.
     *
     * @param x1 the x coordinate of the start point (e.g., "10", "10%", "10px")
     * @return this element for method chaining
     */
    public LineElement x1(String x1) {
        setAttribute("x1", x1);
        return this;
    }

    /**
     * Sets the y-axis coordinate of the line starting point.
     *
     * @param y1 the y coordinate of the start point in user units
     * @return this element for method chaining
     */
    public LineElement y1(double y1) {
        setAttribute("y1", String.valueOf(y1));
        return this;
    }

    /**
     * Sets the y-axis coordinate of the line starting point with a unit or percentage.
     *
     * @param y1 the y coordinate of the start point (e.g., "10", "10%", "10px")
     * @return this element for method chaining
     */
    public LineElement y1(String y1) {
        setAttribute("y1", y1);
        return this;
    }

    /**
     * Sets the x-axis coordinate of the line ending point.
     *
     * @param x2 the x coordinate of the end point in user units
     * @return this element for method chaining
     */
    public LineElement x2(double x2) {
        setAttribute("x2", String.valueOf(x2));
        return this;
    }

    /**
     * Sets the x-axis coordinate of the line ending point with a unit or percentage.
     *
     * @param x2 the x coordinate of the end point (e.g., "90", "90%", "90px")
     * @return this element for method chaining
     */
    public LineElement x2(String x2) {
        setAttribute("x2", x2);
        return this;
    }

    /**
     * Sets the y-axis coordinate of the line ending point.
     *
     * @param y2 the y coordinate of the end point in user units
     * @return this element for method chaining
     */
    public LineElement y2(double y2) {
        setAttribute("y2", String.valueOf(y2));
        return this;
    }

    /**
     * Sets the y-axis coordinate of the line ending point with a unit or percentage.
     *
     * @param y2 the y coordinate of the end point (e.g., "90", "90%", "90px")
     * @return this element for method chaining
     */
    public LineElement y2(String y2) {
        setAttribute("y2", y2);
        return this;
    }

    /**
     * Sets the total length for the line's path in user units.
     * <p>
     * This value is used to calibrate the browser's distance calculations
     * with those of the author, by scaling all distance computations using
     * the ratio pathLength / (computed value of path length).
     * </p>
     *
     * @param pathLength the total path length in user units
     * @return this element for method chaining
     */
    public LineElement pathLength(double pathLength) {
        setAttribute("pathLength", String.valueOf(pathLength));
        return this;
    }

    /**
     * Convenience method to set the starting point (x1, y1) at once.
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
     *
     * @param x1 the x coordinate of the start point (e.g., "10%")
     * @param y1 the y coordinate of the start point (e.g., "10%")
     * @return this element for method chaining
     */
    public LineElement from(String x1, String y1) {
        return x1(x1).y1(y1);
    }

    /**
     * Convenience method to set the ending point (x2, y2) at once.
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
     *
     * @param x2 the x coordinate of the end point (e.g., "90%")
     * @param y2 the y coordinate of the end point (e.g., "90%")
     * @return this element for method chaining
     */
    public LineElement to(String x2, String y2) {
        return x2(x2).y2(y2);
    }

    /**
     * Convenience method to set both start and end points at once.
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
     * Sets the stroke color of the line.
     * <p>
     * Lines require a stroke to be visible.
     * </p>
     *
     * @param color the stroke color
     * @return this element for method chaining
     */
    public LineElement stroke(Color color) {
        setAttribute("stroke", color.toString());
        return this;
    }

    /**
     * Sets the stroke color of the line.
     * <p>
     * Lines require a stroke to be visible.
     * </p>
     *
     * @param stroke the stroke color (e.g., "black", "#000", "rgb(0,0,0)")
     * @return this element for method chaining
     */
    public LineElement stroke(String stroke) {
        setAttribute("stroke", stroke);
        return this;
    }

    /**
     * Sets the stroke width of the line.
     *
     * @param width the stroke width in user units
     * @return this element for method chaining
     */
    public LineElement strokeWidth(double width) {
        setAttribute("stroke-width", String.valueOf(width));
        return this;
    }

    /**
     * Sets the stroke width of the line with a unit.
     *
     * @param width the stroke width (e.g., "2", "2px")
     * @return this element for method chaining
     */
    public LineElement strokeWidth(String width) {
        setAttribute("stroke-width", width);
        return this;
    }
}
