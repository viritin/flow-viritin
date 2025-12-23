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

    /**
     * Sets the x coordinate of the pattern tile.
     *
     * @param x the x coordinate
     * @return this element for method chaining
     */
    public PatternElement x(double x) {
        setAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the y coordinate of the pattern tile.
     *
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public PatternElement y(double y) {
        setAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the width of the pattern tile.
     *
     * @param width the width
     * @return this element for method chaining
     */
    public PatternElement width(double width) {
        setAttribute("width", String.valueOf(width));
        return this;
    }

    /**
     * Sets the width with a unit.
     *
     * @param width the width (e.g., "10%", "20px")
     * @return this element for method chaining
     */
    public PatternElement width(String width) {
        setAttribute("width", width);
        return this;
    }

    /**
     * Sets the height of the pattern tile.
     *
     * @param height the height
     * @return this element for method chaining
     */
    public PatternElement height(double height) {
        setAttribute("height", String.valueOf(height));
        return this;
    }

    /**
     * Sets the height with a unit.
     *
     * @param height the height (e.g., "10%", "20px")
     * @return this element for method chaining
     */
    public PatternElement height(String height) {
        setAttribute("height", height);
        return this;
    }

    /**
     * Sets the size of the pattern tile.
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
     * Sets the viewBox for the pattern contents.
     *
     * @param minX   the minimum x value
     * @param minY   the minimum y value
     * @param width  the width
     * @param height the height
     * @return this element for method chaining
     */
    public PatternElement viewBox(double minX, double minY, double width, double height) {
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

    /**
     * Sets the coordinate system for x, y, width, height attributes.
     *
     * @param units the pattern units
     * @return this element for method chaining
     */
    public PatternElement patternUnits(PatternUnits units) {
        setAttribute("patternUnits", units.toString());
        return this;
    }

    /**
     * Sets the coordinate system for the pattern contents.
     *
     * @param units the pattern content units
     * @return this element for method chaining
     */
    public PatternElement patternContentUnits(PatternUnits units) {
        setAttribute("patternContentUnits", units.toString());
        return this;
    }

    /**
     * Sets a transform on the pattern.
     *
     * @param transform the transform string
     * @return this element for method chaining
     */
    public PatternElement patternTransform(String transform) {
        setAttribute("patternTransform", transform);
        return this;
    }

    /**
     * References another pattern to inherit attributes from.
     *
     * @param href the reference (e.g., "#otherPattern")
     * @return this element for method chaining
     */
    public PatternElement href(String href) {
        setAttribute("href", href);
        return this;
    }

    /**
     * Sets the preserveAspectRatio attribute.
     *
     * @param value the preserveAspectRatio value
     * @return this element for method chaining
     */
    public PatternElement preserveAspectRatio(String value) {
        setAttribute("preserveAspectRatio", value);
        return this;
    }

    /**
     * Sets the preserveAspectRatio attribute.
     *
     * @param ratio the preserveAspectRatio setting
     * @return this element for method chaining
     */
    public PatternElement preserveAspectRatio(PreserveAspectRatio ratio) {
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
