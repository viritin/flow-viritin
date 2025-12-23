package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <tspan>} element.
 * <p>
 * The {@code <tspan>} element defines a subtext within a {@code <text>} element
 * or another {@code <tspan>} element. It allows for adjustment of the style
 * and/or position of that subtext as needed.
 * </p>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/tspan">MDN: tspan element</a>
 */
public class TSpanElement extends SvgGraphicsElement {

    public TSpanElement() {
        super("tspan");
    }

    /**
     * Creates a tspan element with the given text content.
     *
     * @param text the text content
     */
    public TSpanElement(String text) {
        super("tspan");
        setText(text);
    }

    /**
     * Sets the text content.
     *
     * @param text the text content
     * @return this element for method chaining
     */
    public TSpanElement text(String text) {
        setText(text);
        return this;
    }

    /**
     * Sets the absolute x coordinate for this tspan.
     *
     * @param x the x coordinate
     * @return this element for method chaining
     */
    public TSpanElement x(double x) {
        setAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the absolute x coordinate with a unit.
     *
     * @param x the x coordinate (e.g., "50%")
     * @return this element for method chaining
     */
    public TSpanElement x(String x) {
        setAttribute("x", x);
        return this;
    }

    /**
     * Sets the absolute y coordinate for this tspan.
     *
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public TSpanElement y(double y) {
        setAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the absolute y coordinate with a unit.
     *
     * @param y the y coordinate (e.g., "50%")
     * @return this element for method chaining
     */
    public TSpanElement y(String y) {
        setAttribute("y", y);
        return this;
    }

    /**
     * Sets the horizontal shift from the previous text position.
     *
     * @param dx the horizontal offset
     * @return this element for method chaining
     */
    public TSpanElement dx(double dx) {
        setAttribute("dx", String.valueOf(dx));
        return this;
    }

    /**
     * Sets the horizontal shift with a unit.
     *
     * @param dx the horizontal offset (e.g., "1em")
     * @return this element for method chaining
     */
    public TSpanElement dx(String dx) {
        setAttribute("dx", dx);
        return this;
    }

    /**
     * Sets the vertical shift from the previous text position.
     *
     * @param dy the vertical offset
     * @return this element for method chaining
     */
    public TSpanElement dy(double dy) {
        setAttribute("dy", String.valueOf(dy));
        return this;
    }

    /**
     * Sets the vertical shift with a unit.
     *
     * @param dy the vertical offset (e.g., "1em")
     * @return this element for method chaining
     */
    public TSpanElement dy(String dy) {
        setAttribute("dy", dy);
        return this;
    }

    /**
     * Sets the rotation for each character.
     *
     * @param rotate the rotation angle(s) in degrees
     * @return this element for method chaining
     */
    public TSpanElement rotate(String rotate) {
        setAttribute("rotate", rotate);
        return this;
    }

    /**
     * Sets the total length of the text in this tspan.
     *
     * @param length the text length
     * @return this element for method chaining
     */
    public TSpanElement textLength(double length) {
        setAttribute("textLength", String.valueOf(length));
        return this;
    }

    // ========== Font styling ==========

    /**
     * Sets the font family.
     *
     * @param fontFamily the font family
     * @return this element for method chaining
     */
    public TSpanElement fontFamily(String fontFamily) {
        setAttribute("font-family", fontFamily);
        return this;
    }

    /**
     * Sets the font size.
     *
     * @param size the font size
     * @return this element for method chaining
     */
    public TSpanElement fontSize(double size) {
        setAttribute("font-size", String.valueOf(size));
        return this;
    }

    /**
     * Sets the font size with a unit.
     *
     * @param size the font size (e.g., "12px", "1em")
     * @return this element for method chaining
     */
    public TSpanElement fontSize(String size) {
        setAttribute("font-size", size);
        return this;
    }

    /**
     * Sets the font weight.
     *
     * @param weight the font weight
     * @return this element for method chaining
     */
    public TSpanElement fontWeight(TextElement.FontWeight weight) {
        setAttribute("font-weight", weight.toString());
        return this;
    }

    /**
     * Sets the font style.
     *
     * @param style the font style
     * @return this element for method chaining
     */
    public TSpanElement fontStyle(TextElement.FontStyle style) {
        setAttribute("font-style", style.toString());
        return this;
    }

    /**
     * Sets the text decoration.
     *
     * @param decoration the text decoration
     * @return this element for method chaining
     */
    public TSpanElement textDecoration(TextElement.TextDecoration decoration) {
        setAttribute("text-decoration", decoration.toString());
        return this;
    }

    /**
     * Baseline shift options.
     */
    public enum BaselineShift {
        SUB("sub"),
        SUPER("super"),
        BASELINE("baseline");

        private final String value;

        BaselineShift(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Sets the baseline shift (for subscript/superscript).
     *
     * @param shift the baseline shift
     * @return this element for method chaining
     */
    public TSpanElement baselineShift(BaselineShift shift) {
        setAttribute("baseline-shift", shift.toString());
        return this;
    }

    /**
     * Sets the baseline shift with a custom value.
     *
     * @param shift the baseline shift (e.g., "30%", "-5px")
     * @return this element for method chaining
     */
    public TSpanElement baselineShift(String shift) {
        setAttribute("baseline-shift", shift);
        return this;
    }
}
