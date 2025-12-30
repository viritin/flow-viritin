package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <pattern>} element.
 * <p>
 * The {@code <pattern>} element defines a repeating pattern that can be used
 * as a fill or stroke for other SVG elements. The pattern is defined by its
 * contents and is tiled to fill the target area.
 * </p>
 * <p>
 * Patterns must be placed inside a {@code <defs>} element and referenced
 * by ID (e.g., fill="url(#myPattern)").
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
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/pattern">MDN: pattern element</a>
 */
public class PatternElement extends SvgElement {

    public PatternElement() {
        super("pattern");
    }

    /**
     * Creates a pattern with the given ID.
     *
     * @param id the ID for referencing this pattern
     */
    public PatternElement(String id) {
        super("pattern");
        id(id);
    }

    // ========== x attribute ==========

    /**
     * Sets the x coordinate of the pattern tile.
     * <p>
     * Uses write-only optimization. Use {@link #xRW(double)} if you need to read the value back.
     * </p>
     *
     * @param x the x coordinate
     * @return this element for method chaining
     */
    public PatternElement x(double x) {
        setWriteOnlyAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the x coordinate of the pattern tile (read-write).
     *
     * @param x the x coordinate
     * @return this element for method chaining
     */
    public PatternElement xRW(double x) {
        setAttribute("x", String.valueOf(x));
        return this;
    }

    // ========== y attribute ==========

    /**
     * Sets the y coordinate of the pattern tile.
     * <p>
     * Uses write-only optimization. Use {@link #yRW(double)} if you need to read the value back.
     * </p>
     *
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public PatternElement y(double y) {
        setWriteOnlyAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the y coordinate of the pattern tile (read-write).
     *
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public PatternElement yRW(double y) {
        setAttribute("y", String.valueOf(y));
        return this;
    }

    // ========== width attribute ==========

    /**
     * Sets the width of the pattern tile.
     * <p>
     * Uses write-only optimization. Use {@link #widthRW(double)} if you need to read the value back.
     * </p>
     *
     * @param width the width
     * @return this element for method chaining
     */
    public PatternElement width(double width) {
        setWriteOnlyAttribute("width", String.valueOf(width));
        return this;
    }

    /**
     * Sets the width with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #widthRW(String)} if you need to read the value back.
     * </p>
     *
     * @param width the width (e.g., "10%", "20px")
     * @return this element for method chaining
     */
    public PatternElement width(String width) {
        setWriteOnlyAttribute("width", width);
        return this;
    }

    /**
     * Sets the width of the pattern tile (read-write).
     *
     * @param width the width
     * @return this element for method chaining
     */
    public PatternElement widthRW(double width) {
        setAttribute("width", String.valueOf(width));
        return this;
    }

    /**
     * Sets the width with a unit (read-write).
     *
     * @param width the width (e.g., "10%", "20px")
     * @return this element for method chaining
     */
    public PatternElement widthRW(String width) {
        setAttribute("width", width);
        return this;
    }

    // ========== height attribute ==========

    /**
     * Sets the height of the pattern tile.
     * <p>
     * Uses write-only optimization. Use {@link #heightRW(double)} if you need to read the value back.
     * </p>
     *
     * @param height the height
     * @return this element for method chaining
     */
    public PatternElement height(double height) {
        setWriteOnlyAttribute("height", String.valueOf(height));
        return this;
    }

    /**
     * Sets the height with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #heightRW(String)} if you need to read the value back.
     * </p>
     *
     * @param height the height (e.g., "10%", "20px")
     * @return this element for method chaining
     */
    public PatternElement height(String height) {
        setWriteOnlyAttribute("height", height);
        return this;
    }

    /**
     * Sets the height of the pattern tile (read-write).
     *
     * @param height the height
     * @return this element for method chaining
     */
    public PatternElement heightRW(double height) {
        setAttribute("height", String.valueOf(height));
        return this;
    }

    /**
     * Sets the height with a unit (read-write).
     *
     * @param height the height (e.g., "10%", "20px")
     * @return this element for method chaining
     */
    public PatternElement heightRW(String height) {
        setAttribute("height", height);
        return this;
    }

    // ========== Convenience methods ==========

    /**
     * Sets the size of the pattern tile.
     * <p>
     * Uses write-only optimization. Use {@link #sizeRW(double, double)} if you need to read the values back.
     * </p>
     *
     * @param width  the width
     * @param height the height
     * @return this element for method chaining
     */
    public PatternElement size(double width, double height) {
        width(width);
        height(height);
        return this;
    }

    /**
     * Sets the size of the pattern tile (read-write).
     *
     * @param width  the width
     * @param height the height
     * @return this element for method chaining
     */
    public PatternElement sizeRW(double width, double height) {
        widthRW(width);
        heightRW(height);
        return this;
    }

    // ========== viewBox attribute ==========

    /**
     * Sets the viewBox for the pattern contents.
     * <p>
     * Uses write-only optimization. Use {@link #viewBoxRW(double, double, double, double)} if you need to read the value back.
     * </p>
     *
     * @param minX   the minimum x value
     * @param minY   the minimum y value
     * @param width  the width
     * @param height the height
     * @return this element for method chaining
     */
    public PatternElement viewBox(double minX, double minY, double width, double height) {
        setWriteOnlyAttribute("viewBox", "%s %s %s %s".formatted(minX, minY, width, height));
        return this;
    }

    /**
     * Sets the viewBox for the pattern contents (read-write).
     *
     * @param minX   the minimum x value
     * @param minY   the minimum y value
     * @param width  the width
     * @param height the height
     * @return this element for method chaining
     */
    public PatternElement viewBoxRW(double minX, double minY, double width, double height) {
        setAttribute("viewBox", "%s %s %s %s".formatted(minX, minY, width, height));
        return this;
    }

    /**
     * Pattern unit options.
     */
    public enum PatternUnits {
        /** Coordinates are relative to the user coordinate system */
        USER_SPACE_ON_USE("userSpaceOnUse"),
        /** Coordinates are relative to the bounding box (default) */
        OBJECT_BOUNDING_BOX("objectBoundingBox");

        private final String value;

        PatternUnits(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    // ========== patternUnits attribute ==========

    /**
     * Sets the coordinate system for x, y, width, height attributes.
     * <p>
     * Uses write-only optimization. Use {@link #patternUnitsRW(PatternUnits)} if you need to read the value back.
     * </p>
     *
     * @param units the pattern units
     * @return this element for method chaining
     */
    public PatternElement patternUnits(PatternUnits units) {
        setWriteOnlyAttribute("patternUnits", units.toString());
        return this;
    }

    /**
     * Sets the coordinate system for x, y, width, height attributes (read-write).
     *
     * @param units the pattern units
     * @return this element for method chaining
     */
    public PatternElement patternUnitsRW(PatternUnits units) {
        setAttribute("patternUnits", units.toString());
        return this;
    }

    // ========== patternContentUnits attribute ==========

    /**
     * Sets the coordinate system for the pattern contents.
     * <p>
     * Uses write-only optimization. Use {@link #patternContentUnitsRW(PatternUnits)} if you need to read the value back.
     * </p>
     *
     * @param units the pattern content units
     * @return this element for method chaining
     */
    public PatternElement patternContentUnits(PatternUnits units) {
        setWriteOnlyAttribute("patternContentUnits", units.toString());
        return this;
    }

    /**
     * Sets the coordinate system for the pattern contents (read-write).
     *
     * @param units the pattern content units
     * @return this element for method chaining
     */
    public PatternElement patternContentUnitsRW(PatternUnits units) {
        setAttribute("patternContentUnits", units.toString());
        return this;
    }

    // ========== patternTransform attribute ==========

    /**
     * Sets a transform on the pattern.
     * <p>
     * Uses write-only optimization. Use {@link #patternTransformRW(String)} if you need to read the value back.
     * </p>
     *
     * @param transform the transform string
     * @return this element for method chaining
     */
    public PatternElement patternTransform(String transform) {
        setWriteOnlyAttribute("patternTransform", transform);
        return this;
    }

    /**
     * Sets a transform on the pattern (read-write).
     *
     * @param transform the transform string
     * @return this element for method chaining
     */
    public PatternElement patternTransformRW(String transform) {
        setAttribute("patternTransform", transform);
        return this;
    }

    // ========== href attribute ==========

    /**
     * References another pattern to inherit attributes from.
     * <p>
     * Uses write-only optimization. Use {@link #hrefRW(String)} if you need to read the value back.
     * </p>
     *
     * @param href the reference (e.g., "#otherPattern")
     * @return this element for method chaining
     */
    public PatternElement href(String href) {
        setWriteOnlyAttribute("href", href);
        return this;
    }

    /**
     * References another pattern to inherit attributes from (read-write).
     *
     * @param href the reference (e.g., "#otherPattern")
     * @return this element for method chaining
     */
    public PatternElement hrefRW(String href) {
        setAttribute("href", href);
        return this;
    }

    // ========== preserveAspectRatio attribute ==========

    /**
     * Sets the preserveAspectRatio attribute.
     * <p>
     * Uses write-only optimization. Use {@link #preserveAspectRatioRW(String)} if you need to read the value back.
     * </p>
     *
     * @param value the preserveAspectRatio value
     * @return this element for method chaining
     */
    public PatternElement preserveAspectRatio(String value) {
        setWriteOnlyAttribute("preserveAspectRatio", value);
        return this;
    }

    /**
     * Sets the preserveAspectRatio attribute (read-write).
     *
     * @param value the preserveAspectRatio value
     * @return this element for method chaining
     */
    public PatternElement preserveAspectRatioRW(String value) {
        setAttribute("preserveAspectRatio", value);
        return this;
    }

    /**
     * Sets the preserveAspectRatio attribute.
     * <p>
     * Uses write-only optimization. Use {@link #preserveAspectRatioRW(PreserveAspectRatio)} if you need to read the value back.
     * </p>
     *
     * @param ratio the preserveAspectRatio setting
     * @return this element for method chaining
     */
    public PatternElement preserveAspectRatio(PreserveAspectRatio ratio) {
        setWriteOnlyAttribute("preserveAspectRatio", ratio.toString());
        return this;
    }

    /**
     * Sets the preserveAspectRatio attribute (read-write).
     *
     * @param ratio the preserveAspectRatio setting
     * @return this element for method chaining
     */
    public PatternElement preserveAspectRatioRW(PreserveAspectRatio ratio) {
        setAttribute("preserveAspectRatio", ratio.toString());
        return this;
    }

    /**
     * Adds content elements to this pattern.
     *
     * @param elements the elements that define the pattern
     * @return this element for method chaining
     */
    public PatternElement add(SvgElement... elements) {
        appendChild(elements);
        return this;
    }
}
