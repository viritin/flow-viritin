package org.vaadin.firitin.element.svg;

import in.virit.color.Color;

/**
 * Base class for SVG graphics elements that can be rendered with fill and stroke.
 * <p>
 * This class provides common presentation attributes for SVG shapes like
 * rectangles, circles, ellipses, lines, paths, and polygons.
 * </p>
 * <p>
 * Mirrors the SVG DOM hierarchy where SVGGraphicsElement is the base for
 * renderable elements.
 * </p>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/SVGGraphicsElement">MDN: SVGGraphicsElement</a>
 */
public class SvgGraphicsElement extends SvgElement {

    public SvgGraphicsElement(String tag) {
        super(tag);
    }

    // ========== Fill Attributes ==========

    /**
     * Sets the fill color using a Color object.
     *
     * @param color the fill color
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T fill(Color color) {
        setAttribute("fill", color.toString());
        return (T) this;
    }

    /**
     * Sets the fill color using a string value.
     *
     * @param fill the fill color (e.g., "red", "#ff0000", "rgb(255,0,0)", "url(#gradient)")
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T fill(String fill) {
        setAttribute("fill", fill);
        return (T) this;
    }

    /**
     * Sets the fill to none (transparent).
     *
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T noFill() {
        setAttribute("fill", "none");
        return (T) this;
    }

    /**
     * Sets the fill opacity.
     *
     * @param opacity the opacity value from 0.0 (fully transparent) to 1.0 (fully opaque)
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T fillOpacity(double opacity) {
        setAttribute("fill-opacity", String.valueOf(opacity));
        return (T) this;
    }

    // ========== Stroke Attributes ==========

    /**
     * Sets the stroke color using a Color object.
     *
     * @param color the stroke color
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T stroke(Color color) {
        setAttribute("stroke", color.toString());
        return (T) this;
    }

    /**
     * Sets the stroke color using a string value.
     *
     * @param stroke the stroke color (e.g., "black", "#000000", "rgb(0,0,0)", "url(#gradient)")
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T stroke(String stroke) {
        setAttribute("stroke", stroke);
        return (T) this;
    }

    /**
     * Sets the stroke to none (no outline).
     *
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T noStroke() {
        setAttribute("stroke", "none");
        return (T) this;
    }

    /**
     * Sets the stroke width.
     *
     * @param width the stroke width in user units
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeWidth(double width) {
        setAttribute("stroke-width", String.valueOf(width));
        return (T) this;
    }

    /**
     * Sets the stroke width with a unit.
     *
     * @param width the stroke width (e.g., "2", "2px", "0.5em")
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeWidth(String width) {
        setAttribute("stroke-width", width);
        return (T) this;
    }

    /**
     * Sets the stroke opacity.
     *
     * @param opacity the opacity value from 0.0 (fully transparent) to 1.0 (fully opaque)
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeOpacity(double opacity) {
        setAttribute("stroke-opacity", String.valueOf(opacity));
        return (T) this;
    }

    /**
     * Line cap styles for stroke endings.
     */
    public enum LineCap {
        /** Straight edge perpendicular to the stroke direction */
        BUTT("butt"),
        /** Rounded end cap */
        ROUND("round"),
        /** Extends the stroke slightly beyond the path end with a square */
        SQUARE("square");

        private final String value;

        LineCap(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Sets the shape of line endings.
     *
     * @param lineCap the line cap style
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeLinecap(LineCap lineCap) {
        setAttribute("stroke-linecap", lineCap.toString());
        return (T) this;
    }

    /**
     * Line join styles for stroke corners.
     */
    public enum LineJoin {
        /** Sharp corner with extended edges */
        MITER("miter"),
        /** Rounded corner */
        ROUND("round"),
        /** Beveled/flattened corner */
        BEVEL("bevel");

        private final String value;

        LineJoin(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Sets the shape of line corners.
     *
     * @param lineJoin the line join style
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeLinejoin(LineJoin lineJoin) {
        setAttribute("stroke-linejoin", lineJoin.toString());
        return (T) this;
    }

    /**
     * Sets the miter limit for miter line joins.
     * <p>
     * When two lines meet at a sharp angle and miter join is used,
     * the miter can extend far beyond the stroke width. This limit
     * controls when to switch to a bevel join instead.
     * </p>
     *
     * @param limit the miter limit (default is 4)
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeMiterlimit(double limit) {
        setAttribute("stroke-miterlimit", String.valueOf(limit));
        return (T) this;
    }

    /**
     * Sets the dash pattern for the stroke.
     * <p>
     * The pattern is specified as a comma or space separated list of lengths.
     * Odd positions define dash lengths, even positions define gap lengths.
     * </p>
     *
     * @param dasharray the dash pattern (e.g., "5,10", "5 10 5", "10,5,5,5")
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeDasharray(String dasharray) {
        setAttribute("stroke-dasharray", dasharray);
        return (T) this;
    }

    /**
     * Sets the dash pattern for the stroke using numeric values.
     *
     * @param values the dash and gap lengths alternating
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeDasharray(double... values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(values[i]);
        }
        setAttribute("stroke-dasharray", sb.toString());
        return (T) this;
    }

    /**
     * Sets the offset for the dash pattern.
     *
     * @param offset the offset in user units
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeDashoffset(double offset) {
        setAttribute("stroke-dashoffset", String.valueOf(offset));
        return (T) this;
    }

    // ========== General Opacity ==========

    /**
     * Sets the overall opacity of the element (both fill and stroke).
     *
     * @param opacity the opacity value from 0.0 (fully transparent) to 1.0 (fully opaque)
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T opacity(double opacity) {
        setAttribute("opacity", String.valueOf(opacity));
        return (T) this;
    }
}
