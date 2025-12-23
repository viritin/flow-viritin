package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <mask>} element.
 * <p>
 * The {@code <mask>} element defines an alpha mask for compositing the
 * current object into the background. A mask is used to create transparency
 * effects that are more complex than simple opacity.
 * </p>
 * <p>
 * Unlike clip paths which create hard edges, masks can create soft edges
 * and gradual transparency using the luminance or alpha values of the
 * mask contents.
 * </p>
 * <p>
 * Masks must be placed inside a {@code <defs>} element and referenced
 * using the mask attribute (e.g., mask="url(#myMask)").
 * </p>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/mask">MDN: mask element</a>
 */
public class MaskElement extends SvgElement {

    public MaskElement() {
        super("mask");
    }

    /**
     * Creates a mask with the given ID.
     *
     * @param id the ID for referencing this mask
     */
    public MaskElement(String id) {
        super("mask");
        id(id);
    }

    /**
     * Sets the x coordinate of the mask area.
     *
     * @param x the x coordinate
     * @return this element for method chaining
     */
    public MaskElement x(double x) {
        setAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the x coordinate with a unit.
     *
     * @param x the x coordinate (e.g., "-10%")
     * @return this element for method chaining
     */
    public MaskElement x(String x) {
        setAttribute("x", x);
        return this;
    }

    /**
     * Sets the y coordinate of the mask area.
     *
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public MaskElement y(double y) {
        setAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the y coordinate with a unit.
     *
     * @param y the y coordinate (e.g., "-10%")
     * @return this element for method chaining
     */
    public MaskElement y(String y) {
        setAttribute("y", y);
        return this;
    }

    /**
     * Sets the width of the mask area.
     *
     * @param width the width
     * @return this element for method chaining
     */
    public MaskElement width(double width) {
        setAttribute("width", String.valueOf(width));
        return this;
    }

    /**
     * Sets the width with a unit.
     *
     * @param width the width (e.g., "120%")
     * @return this element for method chaining
     */
    public MaskElement width(String width) {
        setAttribute("width", width);
        return this;
    }

    /**
     * Sets the height of the mask area.
     *
     * @param height the height
     * @return this element for method chaining
     */
    public MaskElement height(double height) {
        setAttribute("height", String.valueOf(height));
        return this;
    }

    /**
     * Sets the height with a unit.
     *
     * @param height the height (e.g., "120%")
     * @return this element for method chaining
     */
    public MaskElement height(String height) {
        setAttribute("height", height);
        return this;
    }

    /**
     * Sets the bounds of the mask area.
     *
     * @param x      the x coordinate
     * @param y      the y coordinate
     * @param width  the width
     * @param height the height
     * @return this element for method chaining
     */
    public MaskElement bounds(double x, double y, double width, double height) {
        x(x);
        y(y);
        width(width);
        height(height);
        return this;
    }

    /**
     * Mask unit options.
     */
    public enum MaskUnits {
        /** Coordinates are relative to the user coordinate system */
        USER_SPACE_ON_USE("userSpaceOnUse"),
        /** Coordinates are relative to the bounding box (default) */
        OBJECT_BOUNDING_BOX("objectBoundingBox");

        private final String value;

        MaskUnits(String value) {
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
     * @param units the mask units
     * @return this element for method chaining
     */
    public MaskElement maskUnits(MaskUnits units) {
        setAttribute("maskUnits", units.toString());
        return this;
    }

    /**
     * Sets the coordinate system for the mask contents.
     *
     * @param units the mask content units
     * @return this element for method chaining
     */
    public MaskElement maskContentUnits(MaskUnits units) {
        setAttribute("maskContentUnits", units.toString());
        return this;
    }

    /**
     * Adds content elements to this mask.
     * <p>
     * White areas in the mask content will be fully visible,
     * black areas will be fully transparent, and gray areas
     * will be partially transparent.
     * </p>
     *
     * @param elements the elements that define the mask
     * @return this element for method chaining
     */
    public MaskElement add(SvgElement... elements) {
        appendChild(elements);
        return this;
    }
}
