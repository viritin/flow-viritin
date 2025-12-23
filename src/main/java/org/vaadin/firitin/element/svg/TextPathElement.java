package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <textPath>} element.
 * <p>
 * The {@code <textPath>} element renders text along the shape of a path.
 * The text is enclosed within a {@code <text>} element and references a
 * {@code <path>} element using the href attribute.
 * </p>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/textPath">MDN: textPath element</a>
 */
public class TextPathElement extends SvgGraphicsElement {

    private static int idCounter = 0;

    public TextPathElement() {
        super("textPath");
    }

    /**
     * Creates a textPath element referencing the given path.
     * An ID is automatically generated for the path if not already set.
     *
     * @param path the path element to follow
     */
    public TextPathElement(PathElement path) {
        super("textPath");
        path(path);
    }

    /**
     * Creates a textPath element with path reference and text.
     * An ID is automatically generated for the path if not already set.
     *
     * @param path the path element to follow
     * @param text the text content
     */
    public TextPathElement(PathElement path, String text) {
        super("textPath");
        path(path);
        setText(text);
    }

    /**
     * Sets the text content.
     *
     * @param text the text content
     * @return this element for method chaining
     */
    public TextPathElement text(String text) {
        setText(text);
        return this;
    }

    /**
     * Sets the path element for the text to follow.
     * An ID is automatically generated for the path if not already set.
     *
     * @param path the path element
     * @return this element for method chaining
     */
    public TextPathElement path(PathElement path) {
        setAttribute("href", "#" + ensureId(path));
        return this;
    }

    /**
     * Sets the href to reference a path element by ID.
     *
     * @param href the reference URL (e.g., "#myPath")
     * @return this element for method chaining
     */
    public TextPathElement href(String href) {
        setAttribute("href", href);
        return this;
    }

    private static String ensureId(SvgElement element) {
        String id = element.getAttribute("id");
        if (id == null || id.isEmpty()) {
            id = "svg-textpath-" + (++idCounter);
            element.setAttribute("id", id);
        }
        return id;
    }

    /**
     * Sets the offset along the path where text rendering starts.
     *
     * @param offset the start offset
     * @return this element for method chaining
     */
    public TextPathElement startOffset(double offset) {
        setAttribute("startOffset", String.valueOf(offset));
        return this;
    }

    /**
     * Sets the offset along the path with a unit.
     *
     * @param offset the start offset (e.g., "50%", "100px")
     * @return this element for method chaining
     */
    public TextPathElement startOffset(String offset) {
        setAttribute("startOffset", offset);
        return this;
    }

    /**
     * Text path method options.
     */
    public enum Method {
        /** Characters are aligned perpendicular to the path (default) */
        ALIGN("align"),
        /** Characters are stretched or compressed to fit the path */
        STRETCH("stretch");

        private final String value;

        Method(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Sets the method used to render individual glyphs along the path.
     *
     * @param method the rendering method
     * @return this element for method chaining
     */
    public TextPathElement method(Method method) {
        setAttribute("method", method.toString());
        return this;
    }

    /**
     * Text path spacing options.
     */
    public enum Spacing {
        /** Spacing between glyphs is adjusted (default) */
        AUTO("auto"),
        /** Glyphs are rendered exactly as specified */
        EXACT("exact");

        private final String value;

        Spacing(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Sets the spacing between glyphs.
     *
     * @param spacing the spacing mode
     * @return this element for method chaining
     */
    public TextPathElement spacing(Spacing spacing) {
        setAttribute("spacing", spacing.toString());
        return this;
    }

    /**
     * Text path side options.
     */
    public enum Side {
        /** Text is rendered on the left side of the path (default) */
        LEFT("left"),
        /** Text is rendered on the right side of the path */
        RIGHT("right");

        private final String value;

        Side(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Sets which side of the path the text is rendered on.
     *
     * @param side the side
     * @return this element for method chaining
     */
    public TextPathElement side(Side side) {
        setAttribute("side", side.toString());
        return this;
    }

    /**
     * Sets the total length of the text.
     *
     * @param length the text length
     * @return this element for method chaining
     */
    public TextPathElement textLength(double length) {
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
    public TextPathElement fontFamily(String fontFamily) {
        setAttribute("font-family", fontFamily);
        return this;
    }

    /**
     * Sets the font size.
     *
     * @param size the font size
     * @return this element for method chaining
     */
    public TextPathElement fontSize(double size) {
        setAttribute("font-size", String.valueOf(size));
        return this;
    }

    /**
     * Sets the font size with a unit.
     *
     * @param size the font size (e.g., "12px", "1em")
     * @return this element for method chaining
     */
    public TextPathElement fontSize(String size) {
        setAttribute("font-size", size);
        return this;
    }

    /**
     * Sets the font weight.
     *
     * @param weight the font weight
     * @return this element for method chaining
     */
    public TextPathElement fontWeight(TextElement.FontWeight weight) {
        setAttribute("font-weight", weight.toString());
        return this;
    }
}
