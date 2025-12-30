package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <tspan>} element.
 * <p>
 * The {@code <tspan>} element defines a subtext within a {@code <text>} element
 * or another {@code <tspan>} element. It allows for adjustment of the style
 * and/or position of that subtext as needed.
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

    // ========== x attribute ==========

    /**
     * Sets the absolute x coordinate for this tspan.
     * <p>
     * Uses write-only optimization. Use {@link #xRW(double)} if you need to read the value back.
     * </p>
     *
     * @param x the x coordinate
     * @return this element for method chaining
     */
    public TSpanElement x(double x) {
        setWriteOnlyAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the absolute x coordinate with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #xRW(String)} if you need to read the value back.
     * </p>
     *
     * @param x the x coordinate (e.g., "50%")
     * @return this element for method chaining
     */
    public TSpanElement x(String x) {
        setWriteOnlyAttribute("x", x);
        return this;
    }

    /**
     * Sets the absolute x coordinate for this tspan (read-write).
     *
     * @param x the x coordinate
     * @return this element for method chaining
     */
    public TSpanElement xRW(double x) {
        setAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the absolute x coordinate with a unit (read-write).
     *
     * @param x the x coordinate (e.g., "50%")
     * @return this element for method chaining
     */
    public TSpanElement xRW(String x) {
        setAttribute("x", x);
        return this;
    }

    // ========== y attribute ==========

    /**
     * Sets the absolute y coordinate for this tspan.
     * <p>
     * Uses write-only optimization. Use {@link #yRW(double)} if you need to read the value back.
     * </p>
     *
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public TSpanElement y(double y) {
        setWriteOnlyAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the absolute y coordinate with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #yRW(String)} if you need to read the value back.
     * </p>
     *
     * @param y the y coordinate (e.g., "50%")
     * @return this element for method chaining
     */
    public TSpanElement y(String y) {
        setWriteOnlyAttribute("y", y);
        return this;
    }

    /**
     * Sets the absolute y coordinate for this tspan (read-write).
     *
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public TSpanElement yRW(double y) {
        setAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the absolute y coordinate with a unit (read-write).
     *
     * @param y the y coordinate (e.g., "50%")
     * @return this element for method chaining
     */
    public TSpanElement yRW(String y) {
        setAttribute("y", y);
        return this;
    }

    // ========== dx attribute ==========

    /**
     * Sets the horizontal shift from the previous text position.
     * <p>
     * Uses write-only optimization. Use {@link #dxRW(double)} if you need to read the value back.
     * </p>
     *
     * @param dx the horizontal offset
     * @return this element for method chaining
     */
    public TSpanElement dx(double dx) {
        setWriteOnlyAttribute("dx", String.valueOf(dx));
        return this;
    }

    /**
     * Sets the horizontal shift with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #dxRW(String)} if you need to read the value back.
     * </p>
     *
     * @param dx the horizontal offset (e.g., "1em")
     * @return this element for method chaining
     */
    public TSpanElement dx(String dx) {
        setWriteOnlyAttribute("dx", dx);
        return this;
    }

    /**
     * Sets the horizontal shift from the previous text position (read-write).
     *
     * @param dx the horizontal offset
     * @return this element for method chaining
     */
    public TSpanElement dxRW(double dx) {
        setAttribute("dx", String.valueOf(dx));
        return this;
    }

    /**
     * Sets the horizontal shift with a unit (read-write).
     *
     * @param dx the horizontal offset (e.g., "1em")
     * @return this element for method chaining
     */
    public TSpanElement dxRW(String dx) {
        setAttribute("dx", dx);
        return this;
    }

    // ========== dy attribute ==========

    /**
     * Sets the vertical shift from the previous text position.
     * <p>
     * Uses write-only optimization. Use {@link #dyRW(double)} if you need to read the value back.
     * </p>
     *
     * @param dy the vertical offset
     * @return this element for method chaining
     */
    public TSpanElement dy(double dy) {
        setWriteOnlyAttribute("dy", String.valueOf(dy));
        return this;
    }

    /**
     * Sets the vertical shift with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #dyRW(String)} if you need to read the value back.
     * </p>
     *
     * @param dy the vertical offset (e.g., "1em")
     * @return this element for method chaining
     */
    public TSpanElement dy(String dy) {
        setWriteOnlyAttribute("dy", dy);
        return this;
    }

    /**
     * Sets the vertical shift from the previous text position (read-write).
     *
     * @param dy the vertical offset
     * @return this element for method chaining
     */
    public TSpanElement dyRW(double dy) {
        setAttribute("dy", String.valueOf(dy));
        return this;
    }

    /**
     * Sets the vertical shift with a unit (read-write).
     *
     * @param dy the vertical offset (e.g., "1em")
     * @return this element for method chaining
     */
    public TSpanElement dyRW(String dy) {
        setAttribute("dy", dy);
        return this;
    }

    // ========== rotate attribute ==========

    /**
     * Sets the rotation for each character.
     * <p>
     * Uses write-only optimization. Use {@link #rotateRW(String)} if you need to read the value back.
     * </p>
     *
     * @param rotate the rotation angle(s) in degrees
     * @return this element for method chaining
     */
    public TSpanElement rotate(String rotate) {
        setWriteOnlyAttribute("rotate", rotate);
        return this;
    }

    /**
     * Sets the rotation for each character (read-write).
     *
     * @param rotate the rotation angle(s) in degrees
     * @return this element for method chaining
     */
    public TSpanElement rotateRW(String rotate) {
        setAttribute("rotate", rotate);
        return this;
    }

    // ========== textLength attribute ==========

    /**
     * Sets the total length of the text in this tspan.
     * <p>
     * Uses write-only optimization. Use {@link #textLengthRW(double)} if you need to read the value back.
     * </p>
     *
     * @param length the text length
     * @return this element for method chaining
     */
    public TSpanElement textLength(double length) {
        setWriteOnlyAttribute("textLength", String.valueOf(length));
        return this;
    }

    /**
     * Sets the total length of the text in this tspan (read-write).
     *
     * @param length the text length
     * @return this element for method chaining
     */
    public TSpanElement textLengthRW(double length) {
        setAttribute("textLength", String.valueOf(length));
        return this;
    }

    // ========== Font styling ==========

    /**
     * Sets the font family.
     * <p>
     * Uses write-only optimization. Use {@link #fontFamilyRW(String)} if you need to read the value back.
     * </p>
     *
     * @param fontFamily the font family
     * @return this element for method chaining
     */
    public TSpanElement fontFamily(String fontFamily) {
        setWriteOnlyAttribute("font-family", fontFamily);
        return this;
    }

    /**
     * Sets the font family (read-write).
     *
     * @param fontFamily the font family
     * @return this element for method chaining
     */
    public TSpanElement fontFamilyRW(String fontFamily) {
        setAttribute("font-family", fontFamily);
        return this;
    }

    // ========== fontSize attribute ==========

    /**
     * Sets the font size.
     * <p>
     * Uses write-only optimization. Use {@link #fontSizeRW(double)} if you need to read the value back.
     * </p>
     *
     * @param size the font size
     * @return this element for method chaining
     */
    public TSpanElement fontSize(double size) {
        setWriteOnlyAttribute("font-size", String.valueOf(size));
        return this;
    }

    /**
     * Sets the font size with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #fontSizeRW(String)} if you need to read the value back.
     * </p>
     *
     * @param size the font size (e.g., "12px", "1em")
     * @return this element for method chaining
     */
    public TSpanElement fontSize(String size) {
        setWriteOnlyAttribute("font-size", size);
        return this;
    }

    /**
     * Sets the font size (read-write).
     *
     * @param size the font size
     * @return this element for method chaining
     */
    public TSpanElement fontSizeRW(double size) {
        setAttribute("font-size", String.valueOf(size));
        return this;
    }

    /**
     * Sets the font size with a unit (read-write).
     *
     * @param size the font size (e.g., "12px", "1em")
     * @return this element for method chaining
     */
    public TSpanElement fontSizeRW(String size) {
        setAttribute("font-size", size);
        return this;
    }

    // ========== fontWeight attribute ==========

    /**
     * Sets the font weight.
     * <p>
     * Uses write-only optimization. Use {@link #fontWeightRW(TextElement.FontWeight)} if you need to read the value back.
     * </p>
     *
     * @param weight the font weight
     * @return this element for method chaining
     */
    public TSpanElement fontWeight(TextElement.FontWeight weight) {
        setWriteOnlyAttribute("font-weight", weight.toString());
        return this;
    }

    /**
     * Sets the font weight (read-write).
     *
     * @param weight the font weight
     * @return this element for method chaining
     */
    public TSpanElement fontWeightRW(TextElement.FontWeight weight) {
        setAttribute("font-weight", weight.toString());
        return this;
    }

    // ========== fontStyle attribute ==========

    /**
     * Sets the font style.
     * <p>
     * Uses write-only optimization. Use {@link #fontStyleRW(TextElement.FontStyle)} if you need to read the value back.
     * </p>
     *
     * @param style the font style
     * @return this element for method chaining
     */
    public TSpanElement fontStyle(TextElement.FontStyle style) {
        setWriteOnlyAttribute("font-style", style.toString());
        return this;
    }

    /**
     * Sets the font style (read-write).
     *
     * @param style the font style
     * @return this element for method chaining
     */
    public TSpanElement fontStyleRW(TextElement.FontStyle style) {
        setAttribute("font-style", style.toString());
        return this;
    }

    // ========== textDecoration attribute ==========

    /**
     * Sets the text decoration.
     * <p>
     * Uses write-only optimization. Use {@link #textDecorationRW(TextElement.TextDecoration)} if you need to read the value back.
     * </p>
     *
     * @param decoration the text decoration
     * @return this element for method chaining
     */
    public TSpanElement textDecoration(TextElement.TextDecoration decoration) {
        setWriteOnlyAttribute("text-decoration", decoration.toString());
        return this;
    }

    /**
     * Sets the text decoration (read-write).
     *
     * @param decoration the text decoration
     * @return this element for method chaining
     */
    public TSpanElement textDecorationRW(TextElement.TextDecoration decoration) {
        setAttribute("text-decoration", decoration.toString());
        return this;
    }

    // ========== baselineShift attribute ==========

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
     * <p>
     * Uses write-only optimization. Use {@link #baselineShiftRW(BaselineShift)} if you need to read the value back.
     * </p>
     *
     * @param shift the baseline shift
     * @return this element for method chaining
     */
    public TSpanElement baselineShift(BaselineShift shift) {
        setWriteOnlyAttribute("baseline-shift", shift.toString());
        return this;
    }

    /**
     * Sets the baseline shift (for subscript/superscript) (read-write).
     *
     * @param shift the baseline shift
     * @return this element for method chaining
     */
    public TSpanElement baselineShiftRW(BaselineShift shift) {
        setAttribute("baseline-shift", shift.toString());
        return this;
    }

    /**
     * Sets the baseline shift with a custom value.
     * <p>
     * Uses write-only optimization. Use {@link #baselineShiftRW(String)} if you need to read the value back.
     * </p>
     *
     * @param shift the baseline shift (e.g., "30%", "-5px")
     * @return this element for method chaining
     */
    public TSpanElement baselineShift(String shift) {
        setWriteOnlyAttribute("baseline-shift", shift);
        return this;
    }

    /**
     * Sets the baseline shift with a custom value (read-write).
     *
     * @param shift the baseline shift (e.g., "30%", "-5px")
     * @return this element for method chaining
     */
    public TSpanElement baselineShiftRW(String shift) {
        setAttribute("baseline-shift", shift);
        return this;
    }
}
