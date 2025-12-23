package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <text>} element.
 * <p>
 * The {@code <text>} element defines a graphics element consisting of text.
 * It's possible to apply a gradient, pattern, clipping path, mask, or filter
 * to text like any other SVG graphics element.
 * </p>
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

    /**
     * Sets the x coordinate of the text starting point.
     *
     * @param x the x coordinate
     * @return this element for method chaining
     */
    public TextElement x(double x) {
        setAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the x coordinate with a unit.
     *
     * @param x the x coordinate (e.g., "50%")
     * @return this element for method chaining
     */
    public TextElement x(String x) {
        setAttribute("x", x);
        return this;
    }

    /**
     * Sets the y coordinate of the text starting point.
     *
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public TextElement y(double y) {
        setAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the y coordinate with a unit.
     *
     * @param y the y coordinate (e.g., "50%")
     * @return this element for method chaining
     */
    public TextElement y(String y) {
        setAttribute("y", y);
        return this;
    }

    /**
     * Sets the position of the text.
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
     * Sets the horizontal shift from the previous text position.
     *
     * @param dx the horizontal offset
     * @return this element for method chaining
     */
    public TextElement dx(double dx) {
        setAttribute("dx", String.valueOf(dx));
        return this;
    }

    /**
     * Sets the vertical shift from the previous text position.
     *
     * @param dy the vertical offset
     * @return this element for method chaining
     */
    public TextElement dy(double dy) {
        setAttribute("dy", String.valueOf(dy));
        return this;
    }

    /**
     * Sets the rotation for each character.
     *
     * @param rotate the rotation angle(s) in degrees
     * @return this element for method chaining
     */
    public TextElement rotate(String rotate) {
        setAttribute("rotate", rotate);
        return this;
    }

    /**
     * Sets the total length of the text.
     *
     * @param length the text length
     * @return this element for method chaining
     */
    public TextElement textLength(double length) {
        setAttribute("textLength", String.valueOf(length));
        return this;
    }

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
     *
     * @param anchor the text anchor
     * @return this element for method chaining
     */
    public TextElement textAnchor(TextAnchor anchor) {
        setAttribute("text-anchor", anchor.toString());
        return this;
    }

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
     *
     * @param baseline the dominant baseline
     * @return this element for method chaining
     */
    public TextElement dominantBaseline(DominantBaseline baseline) {
        setAttribute("dominant-baseline", baseline.toString());
        return this;
    }

    // ========== Font styling ==========

    /**
     * Sets the font family.
     *
     * @param fontFamily the font family (e.g., "Arial", "sans-serif")
     * @return this element for method chaining
     */
    public TextElement fontFamily(String fontFamily) {
        setAttribute("font-family", fontFamily);
        return this;
    }

    /**
     * Sets the font size.
     *
     * @param size the font size
     * @return this element for method chaining
     */
    public TextElement fontSize(double size) {
        setAttribute("font-size", String.valueOf(size));
        return this;
    }

    /**
     * Sets the font size with a unit.
     *
     * @param size the font size (e.g., "12px", "1em")
     * @return this element for method chaining
     */
    public TextElement fontSize(String size) {
        setAttribute("font-size", size);
        return this;
    }

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
     *
     * @param weight the font weight
     * @return this element for method chaining
     */
    public TextElement fontWeight(FontWeight weight) {
        setAttribute("font-weight", weight.toString());
        return this;
    }

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
     *
     * @param style the font style
     * @return this element for method chaining
     */
    public TextElement fontStyle(FontStyle style) {
        setAttribute("font-style", style.toString());
        return this;
    }

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
     *
     * @param decoration the text decoration
     * @return this element for method chaining
     */
    public TextElement textDecoration(TextDecoration decoration) {
        setAttribute("text-decoration", decoration.toString());
        return this;
    }

    /**
     * Sets the letter spacing.
     *
     * @param spacing the spacing value
     * @return this element for method chaining
     */
    public TextElement letterSpacing(double spacing) {
        setAttribute("letter-spacing", String.valueOf(spacing));
        return this;
    }

    /**
     * Sets the letter spacing with a unit.
     *
     * @param spacing the spacing (e.g., "0.1em", "2px")
     * @return this element for method chaining
     */
    public TextElement letterSpacing(String spacing) {
        setAttribute("letter-spacing", spacing);
        return this;
    }

    /**
     * Sets the word spacing.
     *
     * @param spacing the spacing value
     * @return this element for method chaining
     */
    public TextElement wordSpacing(double spacing) {
        setAttribute("word-spacing", String.valueOf(spacing));
        return this;
    }
}
