package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <text>} element.
 * <p>
 * The {@code <text>} element defines a graphics element consisting of text.
 * It's possible to apply a gradient, pattern, clipping path, mask, or filter
 * to text like any other SVG graphics element.
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
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/text">MDN: text element</a>
 */
public class TextElement extends SvgGraphicsElement {

    public TextElement() {
        super("text");
    }

    /**
     * Creates a text element with the given text content.
     *
     * @param text the text content
     */
    public TextElement(String text) {
        super("text");
        setText(text);
    }

    /**
     * Creates a text element at the specified position.
     *
     * @param x    the x coordinate
     * @param y    the y coordinate
     * @param text the text content
     */
    public TextElement(double x, double y, String text) {
        super("text");
        x(x);
        y(y);
        setText(text);
    }

    /**
     * Sets the text content.
     *
     * @param text the text content
     * @return this element for method chaining
     */
    public TextElement text(String text) {
        setText(text);
        return this;
    }

    // ========== x attribute ==========

    /**
     * Sets the x coordinate of the text starting point.
     * <p>
     * Uses write-only optimization. Use {@link #xRW(double)} if you need to read the value back.
     * </p>
     *
     * @param x the x coordinate
     * @return this element for method chaining
     */
    public TextElement x(double x) {
        setWriteOnlyAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the x coordinate with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #xRW(String)} if you need to read the value back.
     * </p>
     *
     * @param x the x coordinate (e.g., "50%")
     * @return this element for method chaining
     */
    public TextElement x(String x) {
        setWriteOnlyAttribute("x", x);
        return this;
    }

    /**
     * Sets the x coordinate of the text starting point (read-write).
     *
     * @param x the x coordinate
     * @return this element for method chaining
     */
    public TextElement xRW(double x) {
        setAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the x coordinate with a unit (read-write).
     *
     * @param x the x coordinate (e.g., "50%")
     * @return this element for method chaining
     */
    public TextElement xRW(String x) {
        setAttribute("x", x);
        return this;
    }

    // ========== y attribute ==========

    /**
     * Sets the y coordinate of the text starting point.
     * <p>
     * Uses write-only optimization. Use {@link #yRW(double)} if you need to read the value back.
     * </p>
     *
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public TextElement y(double y) {
        setWriteOnlyAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the y coordinate with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #yRW(String)} if you need to read the value back.
     * </p>
     *
     * @param y the y coordinate (e.g., "50%")
     * @return this element for method chaining
     */
    public TextElement y(String y) {
        setWriteOnlyAttribute("y", y);
        return this;
    }

    /**
     * Sets the y coordinate of the text starting point (read-write).
     *
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public TextElement yRW(double y) {
        setAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the y coordinate with a unit (read-write).
     *
     * @param y the y coordinate (e.g., "50%")
     * @return this element for method chaining
     */
    public TextElement yRW(String y) {
        setAttribute("y", y);
        return this;
    }

    // ========== position convenience ==========

    /**
     * Sets the position of the text.
     * <p>
     * Uses write-only optimization. Use {@link #positionRW(double, double)} if you need to read the values back.
     * </p>
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public TextElement position(double x, double y) {
        x(x);
        y(y);
        return this;
    }

    /**
     * Sets the position of the text (read-write).
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public TextElement positionRW(double x, double y) {
        xRW(x);
        yRW(y);
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
    public TextElement dx(double dx) {
        setWriteOnlyAttribute("dx", String.valueOf(dx));
        return this;
    }

    /**
     * Sets the horizontal shift from the previous text position (read-write).
     *
     * @param dx the horizontal offset
     * @return this element for method chaining
     */
    public TextElement dxRW(double dx) {
        setAttribute("dx", String.valueOf(dx));
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
    public TextElement dy(double dy) {
        setWriteOnlyAttribute("dy", String.valueOf(dy));
        return this;
    }

    /**
     * Sets the vertical shift from the previous text position (read-write).
     *
     * @param dy the vertical offset
     * @return this element for method chaining
     */
    public TextElement dyRW(double dy) {
        setAttribute("dy", String.valueOf(dy));
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
    public TextElement rotate(String rotate) {
        setWriteOnlyAttribute("rotate", rotate);
        return this;
    }

    /**
     * Sets the rotation for each character (read-write).
     *
     * @param rotate the rotation angle(s) in degrees
     * @return this element for method chaining
     */
    public TextElement rotateRW(String rotate) {
        setAttribute("rotate", rotate);
        return this;
    }

    // ========== textLength attribute ==========

    /**
     * Sets the total length of the text.
     * <p>
     * Uses write-only optimization. Use {@link #textLengthRW(double)} if you need to read the value back.
     * </p>
     *
     * @param length the text length
     * @return this element for method chaining
     */
    public TextElement textLength(double length) {
        setWriteOnlyAttribute("textLength", String.valueOf(length));
        return this;
    }

    /**
     * Sets the total length of the text (read-write).
     *
     * @param length the text length
     * @return this element for method chaining
     */
    public TextElement textLengthRW(double length) {
        setAttribute("textLength", String.valueOf(length));
        return this;
    }

    // ========== Text anchor ==========

    /**
     * Text anchor options for horizontal alignment.
     */
    public enum TextAnchor {
        /** Text starts at the position (default) */
        START("start"),
        /** Text is centered on the position */
        MIDDLE("middle"),
        /** Text ends at the position */
        END("end");

        private final String value;

        TextAnchor(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Sets the text anchor (horizontal alignment).
     * <p>
     * Uses write-only optimization. Use {@link #textAnchorRW(TextAnchor)} if you need to read the value back.
     * </p>
     *
     * @param anchor the text anchor
     * @return this element for method chaining
     */
    public TextElement textAnchor(TextAnchor anchor) {
        setWriteOnlyAttribute("text-anchor", anchor.toString());
        return this;
    }

    /**
     * Sets the text anchor (horizontal alignment) (read-write).
     *
     * @param anchor the text anchor
     * @return this element for method chaining
     */
    public TextElement textAnchorRW(TextAnchor anchor) {
        setAttribute("text-anchor", anchor.toString());
        return this;
    }

    // ========== Dominant baseline ==========

    /**
     * Dominant baseline options for vertical alignment.
     */
    public enum DominantBaseline {
        AUTO("auto"),
        TEXT_BOTTOM("text-bottom"),
        ALPHABETIC("alphabetic"),
        IDEOGRAPHIC("ideographic"),
        MIDDLE("middle"),
        CENTRAL("central"),
        MATHEMATICAL("mathematical"),
        HANGING("hanging"),
        TEXT_TOP("text-top");

        private final String value;

        DominantBaseline(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Sets the dominant baseline (vertical alignment).
     * <p>
     * Uses write-only optimization. Use {@link #dominantBaselineRW(DominantBaseline)} if you need to read the value back.
     * </p>
     *
     * @param baseline the dominant baseline
     * @return this element for method chaining
     */
    public TextElement dominantBaseline(DominantBaseline baseline) {
        setWriteOnlyAttribute("dominant-baseline", baseline.toString());
        return this;
    }

    /**
     * Sets the dominant baseline (vertical alignment) (read-write).
     *
     * @param baseline the dominant baseline
     * @return this element for method chaining
     */
    public TextElement dominantBaselineRW(DominantBaseline baseline) {
        setAttribute("dominant-baseline", baseline.toString());
        return this;
    }

    // ========== Font styling ==========

    /**
     * Sets the font family.
     * <p>
     * Uses write-only optimization. Use {@link #fontFamilyRW(String)} if you need to read the value back.
     * </p>
     *
     * @param fontFamily the font family (e.g., "Arial", "sans-serif")
     * @return this element for method chaining
     */
    public TextElement fontFamily(String fontFamily) {
        setWriteOnlyAttribute("font-family", fontFamily);
        return this;
    }

    /**
     * Sets the font family (read-write).
     *
     * @param fontFamily the font family (e.g., "Arial", "sans-serif")
     * @return this element for method chaining
     */
    public TextElement fontFamilyRW(String fontFamily) {
        setAttribute("font-family", fontFamily);
        return this;
    }

    /**
     * Sets the font size.
     * <p>
     * Uses write-only optimization. Use {@link #fontSizeRW(double)} if you need to read the value back.
     * </p>
     *
     * @param size the font size
     * @return this element for method chaining
     */
    public TextElement fontSize(double size) {
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
    public TextElement fontSize(String size) {
        setWriteOnlyAttribute("font-size", size);
        return this;
    }

    /**
     * Sets the font size (read-write).
     *
     * @param size the font size
     * @return this element for method chaining
     */
    public TextElement fontSizeRW(double size) {
        setAttribute("font-size", String.valueOf(size));
        return this;
    }

    /**
     * Sets the font size with a unit (read-write).
     *
     * @param size the font size (e.g., "12px", "1em")
     * @return this element for method chaining
     */
    public TextElement fontSizeRW(String size) {
        setAttribute("font-size", size);
        return this;
    }

    // ========== Font weight ==========

    /**
     * Font weight options.
     */
    public enum FontWeight {
        NORMAL("normal"),
        BOLD("bold"),
        BOLDER("bolder"),
        LIGHTER("lighter"),
        W100("100"),
        W200("200"),
        W300("300"),
        W400("400"),
        W500("500"),
        W600("600"),
        W700("700"),
        W800("800"),
        W900("900");

        private final String value;

        FontWeight(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Sets the font weight.
     * <p>
     * Uses write-only optimization. Use {@link #fontWeightRW(FontWeight)} if you need to read the value back.
     * </p>
     *
     * @param weight the font weight
     * @return this element for method chaining
     */
    public TextElement fontWeight(FontWeight weight) {
        setWriteOnlyAttribute("font-weight", weight.toString());
        return this;
    }

    /**
     * Sets the font weight (read-write).
     *
     * @param weight the font weight
     * @return this element for method chaining
     */
    public TextElement fontWeightRW(FontWeight weight) {
        setAttribute("font-weight", weight.toString());
        return this;
    }

    // ========== Font style ==========

    /**
     * Font style options.
     */
    public enum FontStyle {
        NORMAL("normal"),
        ITALIC("italic"),
        OBLIQUE("oblique");

        private final String value;

        FontStyle(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Sets the font style.
     * <p>
     * Uses write-only optimization. Use {@link #fontStyleRW(FontStyle)} if you need to read the value back.
     * </p>
     *
     * @param style the font style
     * @return this element for method chaining
     */
    public TextElement fontStyle(FontStyle style) {
        setWriteOnlyAttribute("font-style", style.toString());
        return this;
    }

    /**
     * Sets the font style (read-write).
     *
     * @param style the font style
     * @return this element for method chaining
     */
    public TextElement fontStyleRW(FontStyle style) {
        setAttribute("font-style", style.toString());
        return this;
    }

    // ========== Text decoration ==========

    /**
     * Text decoration options.
     */
    public enum TextDecoration {
        NONE("none"),
        UNDERLINE("underline"),
        OVERLINE("overline"),
        LINE_THROUGH("line-through");

        private final String value;

        TextDecoration(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Sets the text decoration.
     * <p>
     * Uses write-only optimization. Use {@link #textDecorationRW(TextDecoration)} if you need to read the value back.
     * </p>
     *
     * @param decoration the text decoration
     * @return this element for method chaining
     */
    public TextElement textDecoration(TextDecoration decoration) {
        setWriteOnlyAttribute("text-decoration", decoration.toString());
        return this;
    }

    /**
     * Sets the text decoration (read-write).
     *
     * @param decoration the text decoration
     * @return this element for method chaining
     */
    public TextElement textDecorationRW(TextDecoration decoration) {
        setAttribute("text-decoration", decoration.toString());
        return this;
    }

    // ========== Letter spacing ==========

    /**
     * Sets the letter spacing.
     * <p>
     * Uses write-only optimization. Use {@link #letterSpacingRW(double)} if you need to read the value back.
     * </p>
     *
     * @param spacing the spacing value
     * @return this element for method chaining
     */
    public TextElement letterSpacing(double spacing) {
        setWriteOnlyAttribute("letter-spacing", String.valueOf(spacing));
        return this;
    }

    /**
     * Sets the letter spacing with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #letterSpacingRW(String)} if you need to read the value back.
     * </p>
     *
     * @param spacing the spacing (e.g., "0.1em", "2px")
     * @return this element for method chaining
     */
    public TextElement letterSpacing(String spacing) {
        setWriteOnlyAttribute("letter-spacing", spacing);
        return this;
    }

    /**
     * Sets the letter spacing (read-write).
     *
     * @param spacing the spacing value
     * @return this element for method chaining
     */
    public TextElement letterSpacingRW(double spacing) {
        setAttribute("letter-spacing", String.valueOf(spacing));
        return this;
    }

    /**
     * Sets the letter spacing with a unit (read-write).
     *
     * @param spacing the spacing (e.g., "0.1em", "2px")
     * @return this element for method chaining
     */
    public TextElement letterSpacingRW(String spacing) {
        setAttribute("letter-spacing", spacing);
        return this;
    }

    // ========== Word spacing ==========

    /**
     * Sets the word spacing.
     * <p>
     * Uses write-only optimization. Use {@link #wordSpacingRW(double)} if you need to read the value back.
     * </p>
     *
     * @param spacing the spacing value
     * @return this element for method chaining
     */
    public TextElement wordSpacing(double spacing) {
        setWriteOnlyAttribute("word-spacing", String.valueOf(spacing));
        return this;
    }

    /**
     * Sets the word spacing (read-write).
     *
     * @param spacing the spacing value
     * @return this element for method chaining
     */
    public TextElement wordSpacingRW(double spacing) {
        setAttribute("word-spacing", String.valueOf(spacing));
        return this;
    }

    /**
     * Adds child elements to this text element.
     * <p>
     * Typically used to add {@link TSpanElement} or {@link TextPathElement} children.
     * </p>
     *
     * @param children the child elements
     * @return this element for method chaining
     */
    public TextElement add(SvgElement... children) {
        appendChild(children);
        return this;
    }
}
