package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <textPath>} element.
 * <p>
 * The {@code <textPath>} element renders text along the shape of a path.
 * The text is enclosed within a {@code <text>} element and references a
 * {@code <path>} element using the href attribute.
 * </p>
 * <h2>Write-Only vs Read-Write Methods</h2>
 * <p>
 * This class provides two variants for each attribute setter:
 * </p>
 * <ul>
 *   <li><strong>Default methods</strong> (e.g., {@code startOffset()}, {@code method()}) - Use an optimized
 *       write-only approach. Attribute values are NOT stored on the server and cannot be
 *       retrieved via {@code getAttribute()}.</li>
 *   <li><strong>RW methods</strong> (e.g., {@code startOffsetRW()}, {@code methodRW()}) - Use traditional
 *       {@code setAttribute()} which stores values on the server for later retrieval.</li>
 * </ul>
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
     * <p>
     * Note: This method uses read-write approach to ensure ID is properly set.
     * </p>
     *
     * @param path the path element
     * @return this element for method chaining
     */
    public TextPathElement path(PathElement path) {
        setAttribute("href", "#" + ensureId(path));
        return this;
    }

    // ========== href attribute ==========

    /**
     * Sets the href to reference a path element by ID.
     * <p>
     * Uses write-only optimization. Use {@link #hrefRW(String)} if you need to read the value back.
     * </p>
     *
     * @param href the reference URL (e.g., "#myPath")
     * @return this element for method chaining
     */
    public TextPathElement href(String href) {
        setWriteOnlyAttribute("href", href);
        return this;
    }

    /**
     * Sets the href to reference a path element by ID (read-write).
     *
     * @param href the reference URL (e.g., "#myPath")
     * @return this element for method chaining
     */
    public TextPathElement hrefRW(String href) {
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

    // ========== startOffset attribute ==========

    /**
     * Sets the offset along the path where text rendering starts.
     * <p>
     * Uses write-only optimization. Use {@link #startOffsetRW(double)} if you need to read the value back.
     * </p>
     *
     * @param offset the start offset
     * @return this element for method chaining
     */
    public TextPathElement startOffset(double offset) {
        setWriteOnlyAttribute("startOffset", String.valueOf(offset));
        return this;
    }

    /**
     * Sets the offset along the path with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #startOffsetRW(String)} if you need to read the value back.
     * </p>
     *
     * @param offset the start offset (e.g., "50%", "100px")
     * @return this element for method chaining
     */
    public TextPathElement startOffset(String offset) {
        setWriteOnlyAttribute("startOffset", offset);
        return this;
    }

    /**
     * Sets the offset along the path where text rendering starts (read-write).
     *
     * @param offset the start offset
     * @return this element for method chaining
     */
    public TextPathElement startOffsetRW(double offset) {
        setAttribute("startOffset", String.valueOf(offset));
        return this;
    }

    /**
     * Sets the offset along the path with a unit (read-write).
     *
     * @param offset the start offset (e.g., "50%", "100px")
     * @return this element for method chaining
     */
    public TextPathElement startOffsetRW(String offset) {
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

    // ========== method attribute ==========

    /**
     * Sets the method used to render individual glyphs along the path.
     * <p>
     * Uses write-only optimization. Use {@link #methodRW(Method)} if you need to read the value back.
     * </p>
     *
     * @param method the rendering method
     * @return this element for method chaining
     */
    public TextPathElement method(Method method) {
        setWriteOnlyAttribute("method", method.toString());
        return this;
    }

    /**
     * Sets the method used to render individual glyphs along the path (read-write).
     *
     * @param method the rendering method
     * @return this element for method chaining
     */
    public TextPathElement methodRW(Method method) {
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

    // ========== spacing attribute ==========

    /**
     * Sets the spacing between glyphs.
     * <p>
     * Uses write-only optimization. Use {@link #spacingRW(Spacing)} if you need to read the value back.
     * </p>
     *
     * @param spacing the spacing mode
     * @return this element for method chaining
     */
    public TextPathElement spacing(Spacing spacing) {
        setWriteOnlyAttribute("spacing", spacing.toString());
        return this;
    }

    /**
     * Sets the spacing between glyphs (read-write).
     *
     * @param spacing the spacing mode
     * @return this element for method chaining
     */
    public TextPathElement spacingRW(Spacing spacing) {
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

    // ========== side attribute ==========

    /**
     * Sets which side of the path the text is rendered on.
     * <p>
     * Uses write-only optimization. Use {@link #sideRW(Side)} if you need to read the value back.
     * </p>
     *
     * @param side the side
     * @return this element for method chaining
     */
    public TextPathElement side(Side side) {
        setWriteOnlyAttribute("side", side.toString());
        return this;
    }

    /**
     * Sets which side of the path the text is rendered on (read-write).
     *
     * @param side the side
     * @return this element for method chaining
     */
    public TextPathElement sideRW(Side side) {
        setAttribute("side", side.toString());
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
    public TextPathElement textLength(double length) {
        setWriteOnlyAttribute("textLength", String.valueOf(length));
        return this;
    }

    /**
     * Sets the total length of the text (read-write).
     *
     * @param length the text length
     * @return this element for method chaining
     */
    public TextPathElement textLengthRW(double length) {
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
    public TextPathElement fontFamily(String fontFamily) {
        setWriteOnlyAttribute("font-family", fontFamily);
        return this;
    }

    /**
     * Sets the font family (read-write).
     *
     * @param fontFamily the font family
     * @return this element for method chaining
     */
    public TextPathElement fontFamilyRW(String fontFamily) {
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
    public TextPathElement fontSize(double size) {
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
    public TextPathElement fontSize(String size) {
        setWriteOnlyAttribute("font-size", size);
        return this;
    }

    /**
     * Sets the font size (read-write).
     *
     * @param size the font size
     * @return this element for method chaining
     */
    public TextPathElement fontSizeRW(double size) {
        setAttribute("font-size", String.valueOf(size));
        return this;
    }

    /**
     * Sets the font size with a unit (read-write).
     *
     * @param size the font size (e.g., "12px", "1em")
     * @return this element for method chaining
     */
    public TextPathElement fontSizeRW(String size) {
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
    public TextPathElement fontWeight(TextElement.FontWeight weight) {
        setWriteOnlyAttribute("font-weight", weight.toString());
        return this;
    }

    /**
     * Sets the font weight (read-write).
     *
     * @param weight the font weight
     * @return this element for method chaining
     */
    public TextPathElement fontWeightRW(TextElement.FontWeight weight) {
        setAttribute("font-weight", weight.toString());
        return this;
    }
}
