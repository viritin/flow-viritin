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
 * <h2>Write-Only vs Read-Write Methods</h2>
 * <p>
 * This class provides two variants for each attribute setter:
 * </p>
 * <ul>
 *   <li><strong>Default methods</strong> (e.g., {@code fill()}, {@code stroke()}) - Use an optimized
 *       write-only approach. Attribute values are NOT stored on the server and cannot be
 *       retrieved via {@code getAttribute()}.</li>
 *   <li><strong>RW methods</strong> (e.g., {@code fillRW()}, {@code strokeRW()}) - Use traditional
 *       {@code setAttribute()} which stores values on the server for later retrieval.</li>
 * </ul>
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
     * <p>
     * Uses write-only optimization. Use {@link #fillRW(Color)} if you need to read the value back.
     * </p>
     *
     * @param color the fill color
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T fill(Color color) {
        setWriteOnlyAttribute("fill", color.toString());
        return (T) this;
    }

    /**
     * Sets the fill color using a string value.
     * <p>
     * Uses write-only optimization. Use {@link #fillRW(String)} if you need to read the value back.
     * </p>
     *
     * @param fill the fill color (e.g., "red", "#ff0000", "rgb(255,0,0)", "url(#gradient)")
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T fill(String fill) {
        setWriteOnlyAttribute("fill", fill);
        return (T) this;
    }

    /**
     * Sets the fill color using a Color object (read-write).
     *
     * @param color the fill color
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T fillRW(Color color) {
        setAttribute("fill", color.toString());
        return (T) this;
    }

    /**
     * Sets the fill color using a string value (read-write).
     *
     * @param fill the fill color (e.g., "red", "#ff0000", "rgb(255,0,0)", "url(#gradient)")
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T fillRW(String fill) {
        setAttribute("fill", fill);
        return (T) this;
    }

    /**
     * Sets the fill to none (transparent).
     * <p>
     * Uses write-only optimization. Use {@link #noFillRW()} if you need to read the value back.
     * </p>
     *
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T noFill() {
        setWriteOnlyAttribute("fill", "none");
        return (T) this;
    }

    /**
     * Sets the fill to none (transparent) (read-write).
     *
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T noFillRW() {
        setAttribute("fill", "none");
        return (T) this;
    }

    /**
     * Sets the fill opacity.
     * <p>
     * Uses write-only optimization. Use {@link #fillOpacityRW(double)} if you need to read the value back.
     * </p>
     *
     * @param opacity the opacity value from 0.0 (fully transparent) to 1.0 (fully opaque)
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T fillOpacity(double opacity) {
        setWriteOnlyAttribute("fill-opacity", String.valueOf(opacity));
        return (T) this;
    }

    /**
     * Sets the fill opacity (read-write).
     *
     * @param opacity the opacity value from 0.0 (fully transparent) to 1.0 (fully opaque)
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T fillOpacityRW(double opacity) {
        setAttribute("fill-opacity", String.valueOf(opacity));
        return (T) this;
    }

    // ========== Stroke Attributes ==========

    /**
     * Sets the stroke color using a Color object.
     * <p>
     * Uses write-only optimization. Use {@link #strokeRW(Color)} if you need to read the value back.
     * </p>
     *
     * @param color the stroke color
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T stroke(Color color) {
        setWriteOnlyAttribute("stroke", color.toString());
        return (T) this;
    }

    /**
     * Sets the stroke color using a string value.
     * <p>
     * Uses write-only optimization. Use {@link #strokeRW(String)} if you need to read the value back.
     * </p>
     *
     * @param stroke the stroke color (e.g., "black", "#000000", "rgb(0,0,0)", "url(#gradient)")
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T stroke(String stroke) {
        setWriteOnlyAttribute("stroke", stroke);
        return (T) this;
    }

    /**
     * Sets the stroke color using a Color object (read-write).
     *
     * @param color the stroke color
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeRW(Color color) {
        setAttribute("stroke", color.toString());
        return (T) this;
    }

    /**
     * Sets the stroke color using a string value (read-write).
     *
     * @param stroke the stroke color (e.g., "black", "#000000", "rgb(0,0,0)", "url(#gradient)")
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeRW(String stroke) {
        setAttribute("stroke", stroke);
        return (T) this;
    }

    /**
     * Sets the stroke to none (no outline).
     * <p>
     * Uses write-only optimization. Use {@link #noStrokeRW()} if you need to read the value back.
     * </p>
     *
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T noStroke() {
        setWriteOnlyAttribute("stroke", "none");
        return (T) this;
    }

    /**
     * Sets the stroke to none (no outline) (read-write).
     *
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T noStrokeRW() {
        setAttribute("stroke", "none");
        return (T) this;
    }

    /**
     * Sets the stroke width.
     * <p>
     * Uses write-only optimization. Use {@link #strokeWidthRW(double)} if you need to read the value back.
     * </p>
     *
     * @param width the stroke width in user units
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeWidth(double width) {
        setWriteOnlyAttribute("stroke-width", String.valueOf(width));
        return (T) this;
    }

    /**
     * Sets the stroke width with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #strokeWidthRW(String)} if you need to read the value back.
     * </p>
     *
     * @param width the stroke width (e.g., "2", "2px", "0.5em")
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeWidth(String width) {
        setWriteOnlyAttribute("stroke-width", width);
        return (T) this;
    }

    /**
     * Sets the stroke width (read-write).
     *
     * @param width the stroke width in user units
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeWidthRW(double width) {
        setAttribute("stroke-width", String.valueOf(width));
        return (T) this;
    }

    /**
     * Sets the stroke width with a unit (read-write).
     *
     * @param width the stroke width (e.g., "2", "2px", "0.5em")
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeWidthRW(String width) {
        setAttribute("stroke-width", width);
        return (T) this;
    }

    /**
     * Sets the stroke opacity.
     * <p>
     * Uses write-only optimization. Use {@link #strokeOpacityRW(double)} if you need to read the value back.
     * </p>
     *
     * @param opacity the opacity value from 0.0 (fully transparent) to 1.0 (fully opaque)
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeOpacity(double opacity) {
        setWriteOnlyAttribute("stroke-opacity", String.valueOf(opacity));
        return (T) this;
    }

    /**
     * Sets the stroke opacity (read-write).
     *
     * @param opacity the opacity value from 0.0 (fully transparent) to 1.0 (fully opaque)
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeOpacityRW(double opacity) {
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
     * <p>
     * Uses write-only optimization. Use {@link #strokeLinecapRW(LineCap)} if you need to read the value back.
     * </p>
     *
     * @param lineCap the line cap style
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeLinecap(LineCap lineCap) {
        setWriteOnlyAttribute("stroke-linecap", lineCap.toString());
        return (T) this;
    }

    /**
     * Sets the shape of line endings (read-write).
     *
     * @param lineCap the line cap style
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeLinecapRW(LineCap lineCap) {
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
     * <p>
     * Uses write-only optimization. Use {@link #strokeLinejoinRW(LineJoin)} if you need to read the value back.
     * </p>
     *
     * @param lineJoin the line join style
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeLinejoin(LineJoin lineJoin) {
        setWriteOnlyAttribute("stroke-linejoin", lineJoin.toString());
        return (T) this;
    }

    /**
     * Sets the shape of line corners (read-write).
     *
     * @param lineJoin the line join style
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeLinejoinRW(LineJoin lineJoin) {
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
     * <p>
     * Uses write-only optimization. Use {@link #strokeMiterlimitRW(double)} if you need to read the value back.
     * </p>
     *
     * @param limit the miter limit (default is 4)
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeMiterlimit(double limit) {
        setWriteOnlyAttribute("stroke-miterlimit", String.valueOf(limit));
        return (T) this;
    }

    /**
     * Sets the miter limit for miter line joins (read-write).
     *
     * @param limit the miter limit (default is 4)
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeMiterlimitRW(double limit) {
        setAttribute("stroke-miterlimit", String.valueOf(limit));
        return (T) this;
    }

    /**
     * Sets the dash pattern for the stroke.
     * <p>
     * The pattern is specified as a comma or space separated list of lengths.
     * Odd positions define dash lengths, even positions define gap lengths.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #strokeDasharrayRW(String)} if you need to read the value back.
     * </p>
     *
     * @param dasharray the dash pattern (e.g., "5,10", "5 10 5", "10,5,5,5")
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeDasharray(String dasharray) {
        setWriteOnlyAttribute("stroke-dasharray", dasharray);
        return (T) this;
    }

    /**
     * Sets the dash pattern for the stroke using numeric values.
     * <p>
     * Uses write-only optimization. Use {@link #strokeDasharrayRW(double...)} if you need to read the value back.
     * </p>
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
        setWriteOnlyAttribute("stroke-dasharray", sb.toString());
        return (T) this;
    }

    /**
     * Sets the dash pattern for the stroke (read-write).
     *
     * @param dasharray the dash pattern (e.g., "5,10", "5 10 5", "10,5,5,5")
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeDasharrayRW(String dasharray) {
        setAttribute("stroke-dasharray", dasharray);
        return (T) this;
    }

    /**
     * Sets the dash pattern for the stroke using numeric values (read-write).
     *
     * @param values the dash and gap lengths alternating
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeDasharrayRW(double... values) {
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
     * <p>
     * Uses write-only optimization. Use {@link #strokeDashoffsetRW(double)} if you need to read the value back.
     * </p>
     *
     * @param offset the offset in user units
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeDashoffset(double offset) {
        setWriteOnlyAttribute("stroke-dashoffset", String.valueOf(offset));
        return (T) this;
    }

    /**
     * Sets the offset for the dash pattern (read-write).
     *
     * @param offset the offset in user units
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeDashoffsetRW(double offset) {
        setAttribute("stroke-dashoffset", String.valueOf(offset));
        return (T) this;
    }

    // ========== General Opacity ==========

    /**
     * Sets the overall opacity of the element (both fill and stroke).
     * <p>
     * Uses write-only optimization. Use {@link #opacityRW(double)} if you need to read the value back.
     * </p>
     *
     * @param opacity the opacity value from 0.0 (fully transparent) to 1.0 (fully opaque)
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T opacity(double opacity) {
        setWriteOnlyAttribute("opacity", String.valueOf(opacity));
        return (T) this;
    }

    /**
     * Sets the overall opacity of the element (read-write).
     *
     * @param opacity the opacity value from 0.0 (fully transparent) to 1.0 (fully opaque)
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T opacityRW(double opacity) {
        setAttribute("opacity", String.valueOf(opacity));
        return (T) this;
    }

    // ========== Clipping ==========

    /**
     * Sets a clipping path for this element.
     * <p>
     * Parts of the element outside the clipping region will not be rendered.
     * An ID is automatically generated for the clip path if not already set.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #clipPathRW(ClipPathElement)} if you need to read the value back.
     * </p>
     *
     * @param clipPath the clip path element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T clipPath(ClipPathElement clipPath) {
        setWriteOnlyAttribute("clip-path", "url(#" + ensureId(clipPath) + ")");
        return (T) this;
    }

    /**
     * Sets a clipping path for this element (read-write).
     *
     * @param clipPath the clip path element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T clipPathRW(ClipPathElement clipPath) {
        setAttribute("clip-path", "url(#" + ensureId(clipPath) + ")");
        return (T) this;
    }

    /**
     * Sets a mask for this element.
     * <p>
     * The mask controls the transparency of the element based on
     * the luminance or alpha values of the mask contents.
     * An ID is automatically generated for the mask if not already set.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #maskRW(MaskElement)} if you need to read the value back.
     * </p>
     *
     * @param mask the mask element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T mask(MaskElement mask) {
        setWriteOnlyAttribute("mask", "url(#" + ensureId(mask) + ")");
        return (T) this;
    }

    /**
     * Sets a mask for this element (read-write).
     *
     * @param mask the mask element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T maskRW(MaskElement mask) {
        setAttribute("mask", "url(#" + ensureId(mask) + ")");
        return (T) this;
    }

    /**
     * Sets the fill to use a gradient.
     * <p>
     * An ID is automatically generated for the gradient if not already set.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #fillRW(LinearGradientElement)} if you need to read the value back.
     * </p>
     *
     * @param gradient the gradient element (linear or radial)
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T fill(LinearGradientElement gradient) {
        setWriteOnlyAttribute("fill", "url(#" + ensureId(gradient) + ")");
        return (T) this;
    }

    /**
     * Sets the fill to use a gradient (read-write).
     *
     * @param gradient the gradient element (linear or radial)
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T fillRW(LinearGradientElement gradient) {
        setAttribute("fill", "url(#" + ensureId(gradient) + ")");
        return (T) this;
    }

    /**
     * Sets the fill to use a radial gradient.
     * <p>
     * An ID is automatically generated for the gradient if not already set.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #fillRW(RadialGradientElement)} if you need to read the value back.
     * </p>
     *
     * @param gradient the radial gradient element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T fill(RadialGradientElement gradient) {
        setWriteOnlyAttribute("fill", "url(#" + ensureId(gradient) + ")");
        return (T) this;
    }

    /**
     * Sets the fill to use a radial gradient (read-write).
     *
     * @param gradient the radial gradient element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T fillRW(RadialGradientElement gradient) {
        setAttribute("fill", "url(#" + ensureId(gradient) + ")");
        return (T) this;
    }

    /**
     * Sets the fill to use a pattern.
     * <p>
     * An ID is automatically generated for the pattern if not already set.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #fillRW(PatternElement)} if you need to read the value back.
     * </p>
     *
     * @param pattern the pattern element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T fill(PatternElement pattern) {
        setWriteOnlyAttribute("fill", "url(#" + ensureId(pattern) + ")");
        return (T) this;
    }

    /**
     * Sets the fill to use a pattern (read-write).
     *
     * @param pattern the pattern element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T fillRW(PatternElement pattern) {
        setAttribute("fill", "url(#" + ensureId(pattern) + ")");
        return (T) this;
    }

    /**
     * Sets the stroke to use a gradient.
     * <p>
     * An ID is automatically generated for the gradient if not already set.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #strokeRW(LinearGradientElement)} if you need to read the value back.
     * </p>
     *
     * @param gradient the gradient element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T stroke(LinearGradientElement gradient) {
        setWriteOnlyAttribute("stroke", "url(#" + ensureId(gradient) + ")");
        return (T) this;
    }

    /**
     * Sets the stroke to use a gradient (read-write).
     *
     * @param gradient the gradient element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeRW(LinearGradientElement gradient) {
        setAttribute("stroke", "url(#" + ensureId(gradient) + ")");
        return (T) this;
    }

    /**
     * Sets the stroke to use a radial gradient.
     * <p>
     * An ID is automatically generated for the gradient if not already set.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #strokeRW(RadialGradientElement)} if you need to read the value back.
     * </p>
     *
     * @param gradient the radial gradient element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T stroke(RadialGradientElement gradient) {
        setWriteOnlyAttribute("stroke", "url(#" + ensureId(gradient) + ")");
        return (T) this;
    }

    /**
     * Sets the stroke to use a radial gradient (read-write).
     *
     * @param gradient the radial gradient element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeRW(RadialGradientElement gradient) {
        setAttribute("stroke", "url(#" + ensureId(gradient) + ")");
        return (T) this;
    }

    /**
     * Sets the stroke to use a pattern.
     * <p>
     * An ID is automatically generated for the pattern if not already set.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #strokeRW(PatternElement)} if you need to read the value back.
     * </p>
     *
     * @param pattern the pattern element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T stroke(PatternElement pattern) {
        setWriteOnlyAttribute("stroke", "url(#" + ensureId(pattern) + ")");
        return (T) this;
    }

    /**
     * Sets the stroke to use a pattern (read-write).
     *
     * @param pattern the pattern element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T strokeRW(PatternElement pattern) {
        setAttribute("stroke", "url(#" + ensureId(pattern) + ")");
        return (T) this;
    }

    // ========== Marker Attributes ==========

    /**
     * Sets a marker to be drawn at the first vertex of the element's path.
     * <p>
     * An ID is automatically generated for the marker if not already set.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #markerStartRW(MarkerElement)} if you need to read the value back.
     * </p>
     *
     * @param marker the marker element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T markerStart(MarkerElement marker) {
        setWriteOnlyAttribute("marker-start", "url(#" + ensureId(marker) + ")");
        return (T) this;
    }

    /**
     * Sets a marker to be drawn at the first vertex of the element's path (read-write).
     *
     * @param marker the marker element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T markerStartRW(MarkerElement marker) {
        setAttribute("marker-start", "url(#" + ensureId(marker) + ")");
        return (T) this;
    }

    /**
     * Sets a marker to be drawn at all vertices except the first and last.
     * <p>
     * An ID is automatically generated for the marker if not already set.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #markerMidRW(MarkerElement)} if you need to read the value back.
     * </p>
     *
     * @param marker the marker element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T markerMid(MarkerElement marker) {
        setWriteOnlyAttribute("marker-mid", "url(#" + ensureId(marker) + ")");
        return (T) this;
    }

    /**
     * Sets a marker to be drawn at all vertices except the first and last (read-write).
     *
     * @param marker the marker element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T markerMidRW(MarkerElement marker) {
        setAttribute("marker-mid", "url(#" + ensureId(marker) + ")");
        return (T) this;
    }

    /**
     * Sets a marker to be drawn at the last vertex of the element's path.
     * <p>
     * An ID is automatically generated for the marker if not already set.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #markerEndRW(MarkerElement)} if you need to read the value back.
     * </p>
     *
     * @param marker the marker element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T markerEnd(MarkerElement marker) {
        setWriteOnlyAttribute("marker-end", "url(#" + ensureId(marker) + ")");
        return (T) this;
    }

    /**
     * Sets a marker to be drawn at the last vertex of the element's path (read-write).
     *
     * @param marker the marker element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T markerEndRW(MarkerElement marker) {
        setAttribute("marker-end", "url(#" + ensureId(marker) + ")");
        return (T) this;
    }

    /**
     * Sets markers to be drawn at the start, mid-points, and end of the element's path.
     * <p>
     * An ID is automatically generated for the marker if not already set.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #markerRW(MarkerElement)} if you need to read the value back.
     * </p>
     *
     * @param marker the marker element to use for all positions
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T marker(MarkerElement marker) {
        String url = "url(#" + ensureId(marker) + ")";
        setWriteOnlyAttribute("marker-start", url);
        setWriteOnlyAttribute("marker-mid", url);
        setWriteOnlyAttribute("marker-end", url);
        return (T) this;
    }

    /**
     * Sets markers to be drawn at the start, mid-points, and end of the element's path (read-write).
     *
     * @param marker the marker element to use for all positions
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T markerRW(MarkerElement marker) {
        String url = "url(#" + ensureId(marker) + ")";
        setAttribute("marker-start", url);
        setAttribute("marker-mid", url);
        setAttribute("marker-end", url);
        return (T) this;
    }

    private static int idCounter = 0;

    /**
     * Ensures the element has an ID, generating one if necessary.
     *
     * @param element the element to check
     * @return the element's ID
     */
    private static String ensureId(SvgElement element) {
        String id = element.getAttribute("id");
        if (id == null || id.isEmpty()) {
            id = "svg-auto-" + (++idCounter);
            element.setAttribute("id", id);
        }
        return id;
    }

    // ========== Transform Attributes ==========

    /**
     * Sets the transform attribute directly.
     * <p>
     * Multiple transforms can be specified separated by spaces or commas.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #transformRW(String)} if you need to read the value back.
     * </p>
     *
     * @param transform the transform string (e.g., "translate(30,40) rotate(45)")
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T transform(String transform) {
        setWriteOnlyAttribute("transform", transform);
        return (T) this;
    }

    /**
     * Sets the transform attribute directly (read-write).
     *
     * @param transform the transform string (e.g., "translate(30,40) rotate(45)")
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T transformRW(String transform) {
        setAttribute("transform", transform);
        return (T) this;
    }

    /**
     * Moves the element to a new position.
     * <p>
     * Uses write-only optimization. Use {@link #translateRW(double, double)} if you need to read the value back.
     * </p>
     *
     * @param x the x offset
     * @param y the y offset
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T translate(double x, double y) {
        appendTransform("translate(%s,%s)".formatted(x, y));
        return (T) this;
    }

    /**
     * Moves the element to a new position (read-write).
     *
     * @param x the x offset
     * @param y the y offset
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T translateRW(double x, double y) {
        appendTransformRW("translate(%s,%s)".formatted(x, y));
        return (T) this;
    }

    /**
     * Moves the element horizontally.
     * <p>
     * Uses write-only optimization. Use {@link #translateXRW(double)} if you need to read the value back.
     * </p>
     *
     * @param x the x offset
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T translateX(double x) {
        appendTransform("translate(%s,0)".formatted(x));
        return (T) this;
    }

    /**
     * Moves the element horizontally (read-write).
     *
     * @param x the x offset
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T translateXRW(double x) {
        appendTransformRW("translate(%s,0)".formatted(x));
        return (T) this;
    }

    /**
     * Moves the element vertically.
     * <p>
     * Uses write-only optimization. Use {@link #translateYRW(double)} if you need to read the value back.
     * </p>
     *
     * @param y the y offset
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T translateY(double y) {
        appendTransform("translate(0,%s)".formatted(y));
        return (T) this;
    }

    /**
     * Moves the element vertically (read-write).
     *
     * @param y the y offset
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T translateYRW(double y) {
        appendTransformRW("translate(0,%s)".formatted(y));
        return (T) this;
    }

    /**
     * Rotates the element around the origin (0,0).
     * <p>
     * Uses write-only optimization. Use {@link #rotateRW(double)} if you need to read the value back.
     * </p>
     *
     * @param angle the rotation angle in degrees
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T rotate(double angle) {
        appendTransform("rotate(%s)".formatted(angle));
        return (T) this;
    }

    /**
     * Rotates the element around the origin (0,0) (read-write).
     *
     * @param angle the rotation angle in degrees
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T rotateRW(double angle) {
        appendTransformRW("rotate(%s)".formatted(angle));
        return (T) this;
    }

    /**
     * Rotates the element around a specified center point.
     * <p>
     * Uses write-only optimization. Use {@link #rotateRW(double, double, double)} if you need to read the value back.
     * </p>
     *
     * @param angle the rotation angle in degrees
     * @param cx    the x coordinate of the rotation center
     * @param cy    the y coordinate of the rotation center
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T rotate(double angle, double cx, double cy) {
        appendTransform("rotate(%s,%s,%s)".formatted(angle, cx, cy));
        return (T) this;
    }

    /**
     * Rotates the element around a specified center point (read-write).
     *
     * @param angle the rotation angle in degrees
     * @param cx    the x coordinate of the rotation center
     * @param cy    the y coordinate of the rotation center
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T rotateRW(double angle, double cx, double cy) {
        appendTransformRW("rotate(%s,%s,%s)".formatted(angle, cx, cy));
        return (T) this;
    }

    /**
     * Scales the element uniformly.
     * <p>
     * Uses write-only optimization. Use {@link #scaleRW(double)} if you need to read the value back.
     * </p>
     *
     * @param factor the scale factor (1.0 = no change, 0.5 = half size, 2.0 = double size)
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T scale(double factor) {
        appendTransform("scale(%s)".formatted(factor));
        return (T) this;
    }

    /**
     * Scales the element uniformly (read-write).
     *
     * @param factor the scale factor (1.0 = no change, 0.5 = half size, 2.0 = double size)
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T scaleRW(double factor) {
        appendTransformRW("scale(%s)".formatted(factor));
        return (T) this;
    }

    /**
     * Scales the element with different factors for x and y axes.
     * <p>
     * Uses write-only optimization. Use {@link #scaleRW(double, double)} if you need to read the value back.
     * </p>
     *
     * @param sx the x scale factor
     * @param sy the y scale factor
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T scale(double sx, double sy) {
        appendTransform("scale(%s,%s)".formatted(sx, sy));
        return (T) this;
    }

    /**
     * Scales the element with different factors for x and y axes (read-write).
     *
     * @param sx the x scale factor
     * @param sy the y scale factor
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T scaleRW(double sx, double sy) {
        appendTransformRW("scale(%s,%s)".formatted(sx, sy));
        return (T) this;
    }

    /**
     * Skews the element along the x axis.
     * <p>
     * Uses write-only optimization. Use {@link #skewXRW(double)} if you need to read the value back.
     * </p>
     *
     * @param angle the skew angle in degrees
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T skewX(double angle) {
        appendTransform("skewX(%s)".formatted(angle));
        return (T) this;
    }

    /**
     * Skews the element along the x axis (read-write).
     *
     * @param angle the skew angle in degrees
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T skewXRW(double angle) {
        appendTransformRW("skewX(%s)".formatted(angle));
        return (T) this;
    }

    /**
     * Skews the element along the y axis.
     * <p>
     * Uses write-only optimization. Use {@link #skewYRW(double)} if you need to read the value back.
     * </p>
     *
     * @param angle the skew angle in degrees
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T skewY(double angle) {
        appendTransform("skewY(%s)".formatted(angle));
        return (T) this;
    }

    /**
     * Skews the element along the y axis (read-write).
     *
     * @param angle the skew angle in degrees
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T skewYRW(double angle) {
        appendTransformRW("skewY(%s)".formatted(angle));
        return (T) this;
    }

    /**
     * Applies a 2D transformation matrix.
     * <p>
     * The matrix transforms coordinates as:
     * <pre>
     * x_new = a*x + c*y + e
     * y_new = b*x + d*y + f
     * </pre>
     * <p>
     * Uses write-only optimization. Use {@link #matrixRW(double, double, double, double, double, double)}
     * if you need to read the value back.
     * </p>
     *
     * @param a the a component (scale x)
     * @param b the b component (skew y)
     * @param c the c component (skew x)
     * @param d the d component (scale y)
     * @param e the e component (translate x)
     * @param f the f component (translate y)
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T matrix(double a, double b, double c, double d, double e, double f) {
        appendTransform("matrix(%s,%s,%s,%s,%s,%s)".formatted(a, b, c, d, e, f));
        return (T) this;
    }

    /**
     * Applies a 2D transformation matrix (read-write).
     *
     * @param a the a component (scale x)
     * @param b the b component (skew y)
     * @param c the c component (skew x)
     * @param d the d component (scale y)
     * @param e the e component (translate x)
     * @param f the f component (translate y)
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T matrixRW(double a, double b, double c, double d, double e, double f) {
        appendTransformRW("matrix(%s,%s,%s,%s,%s,%s)".formatted(a, b, c, d, e, f));
        return (T) this;
    }

    /**
     * Clears all transforms from the element.
     *
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgGraphicsElement> T clearTransform() {
        removeAttribute("transform");
        return (T) this;
    }

    /**
     * Appends a transform to the existing transform attribute (write-only).
     *
     * @param transform the transform to append
     */
    private void appendTransform(String transform) {
        String existing = getPendingOrAttribute("transform");
        if (existing == null || existing.isEmpty()) {
            setWriteOnlyAttribute("transform", transform);
        } else {
            setWriteOnlyAttribute("transform", existing + " " + transform);
        }
    }

    /**
     * Appends a transform to the existing transform attribute (read-write).
     * <p>
     * Also checks pending write-only attributes to allow mixing write-only
     * and read-write transform calls.
     * </p>
     *
     * @param transform the transform to append
     */
    private void appendTransformRW(String transform) {
        String existing = getPendingOrAttribute("transform");
        if (existing == null || existing.isEmpty()) {
            setAttribute("transform", transform);
        } else {
            setAttribute("transform", existing + " " + transform);
        }
    }
}