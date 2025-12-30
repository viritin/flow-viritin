package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <use>} element.
 * <p>
 * The {@code <use>} element takes nodes from within the SVG document and
 * duplicates them somewhere else. The effect is the same as if the nodes
 * were deeply cloned and then pasted where the {@code <use>} element is.
 * </p>
 * <p>
 * The {@code <use>} element has optional attributes x, y, width and height
 * which define the position and size of the referenced element.
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
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/use">MDN: use element</a>
 */
public class UseElement extends SvgGraphicsElement {

    private static int idCounter = 0;

    public UseElement() {
        super("use");
    }

    /**
     * Creates a use element referencing the given element.
     * An ID is automatically generated for the referenced element if not already set.
     *
     * @param element the element to reference (typically a SymbolElement)
     */
    public UseElement(SvgElement element) {
        super("use");
        ref(element);
    }

    /**
     * Sets the reference to the given element.
     * An ID is automatically generated for the referenced element if not already set.
     * <p>
     * Note: This method uses read-write approach to ensure ID is properly set.
     * </p>
     *
     * @param element the element to reference
     * @return this element for method chaining
     */
    public UseElement ref(SvgElement element) {
        setAttribute("href", "#" + ensureId(element));
        return this;
    }

    // ========== href attribute ==========

    /**
     * Sets the href attribute to reference another element by ID.
     * <p>
     * Uses write-only optimization. Use {@link #hrefRW(String)} if you need to read the value back.
     * </p>
     *
     * @param href the reference URL (e.g., "#myElement" or "sprites.svg#icon")
     * @return this element for method chaining
     */
    public UseElement href(String href) {
        setWriteOnlyAttribute("href", href);
        return this;
    }

    /**
     * Sets the href attribute to reference another element by ID (read-write).
     *
     * @param href the reference URL (e.g., "#myElement" or "sprites.svg#icon")
     * @return this element for method chaining
     */
    public UseElement hrefRW(String href) {
        setAttribute("href", href);
        return this;
    }

    private static String ensureId(SvgElement element) {
        String id = element.getAttribute("id");
        if (id == null || id.isEmpty()) {
            id = "svg-use-" + (++idCounter);
            element.setAttribute("id", id);
        }
        return id;
    }

    // ========== x attribute ==========

    /**
     * Sets the x coordinate where the referenced element will be placed.
     * <p>
     * Uses write-only optimization. Use {@link #xRW(double)} if you need to read the value back.
     * </p>
     *
     * @param x the x coordinate
     * @return this element for method chaining
     */
    public UseElement x(double x) {
        setWriteOnlyAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the x coordinate with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #xRW(String)} if you need to read the value back.
     * </p>
     *
     * @param x the x coordinate with unit (e.g., "10%", "50px")
     * @return this element for method chaining
     */
    public UseElement x(String x) {
        setWriteOnlyAttribute("x", x);
        return this;
    }

    /**
     * Sets the x coordinate where the referenced element will be placed (read-write).
     *
     * @param x the x coordinate
     * @return this element for method chaining
     */
    public UseElement xRW(double x) {
        setAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the x coordinate with a unit (read-write).
     *
     * @param x the x coordinate with unit (e.g., "10%", "50px")
     * @return this element for method chaining
     */
    public UseElement xRW(String x) {
        setAttribute("x", x);
        return this;
    }

    // ========== y attribute ==========

    /**
     * Sets the y coordinate where the referenced element will be placed.
     * <p>
     * Uses write-only optimization. Use {@link #yRW(double)} if you need to read the value back.
     * </p>
     *
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public UseElement y(double y) {
        setWriteOnlyAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the y coordinate with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #yRW(String)} if you need to read the value back.
     * </p>
     *
     * @param y the y coordinate with unit (e.g., "10%", "50px")
     * @return this element for method chaining
     */
    public UseElement y(String y) {
        setWriteOnlyAttribute("y", y);
        return this;
    }

    /**
     * Sets the y coordinate where the referenced element will be placed (read-write).
     *
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public UseElement yRW(double y) {
        setAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the y coordinate with a unit (read-write).
     *
     * @param y the y coordinate with unit (e.g., "10%", "50px")
     * @return this element for method chaining
     */
    public UseElement yRW(String y) {
        setAttribute("y", y);
        return this;
    }

    // ========== width attribute ==========

    /**
     * Sets the width of the use element.
     * <p>
     * Uses write-only optimization. Use {@link #widthRW(double)} if you need to read the value back.
     * </p>
     *
     * @param width the width
     * @return this element for method chaining
     */
    public UseElement width(double width) {
        setWriteOnlyAttribute("width", String.valueOf(width));
        return this;
    }

    /**
     * Sets the width with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #widthRW(String)} if you need to read the value back.
     * </p>
     *
     * @param width the width with unit (e.g., "100%", "200px")
     * @return this element for method chaining
     */
    public UseElement width(String width) {
        setWriteOnlyAttribute("width", width);
        return this;
    }

    /**
     * Sets the width of the use element (read-write).
     *
     * @param width the width
     * @return this element for method chaining
     */
    public UseElement widthRW(double width) {
        setAttribute("width", String.valueOf(width));
        return this;
    }

    /**
     * Sets the width with a unit (read-write).
     *
     * @param width the width with unit (e.g., "100%", "200px")
     * @return this element for method chaining
     */
    public UseElement widthRW(String width) {
        setAttribute("width", width);
        return this;
    }

    // ========== height attribute ==========

    /**
     * Sets the height of the use element.
     * <p>
     * Uses write-only optimization. Use {@link #heightRW(double)} if you need to read the value back.
     * </p>
     *
     * @param height the height
     * @return this element for method chaining
     */
    public UseElement height(double height) {
        setWriteOnlyAttribute("height", String.valueOf(height));
        return this;
    }

    /**
     * Sets the height with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #heightRW(String)} if you need to read the value back.
     * </p>
     *
     * @param height the height with unit (e.g., "100%", "200px")
     * @return this element for method chaining
     */
    public UseElement height(String height) {
        setWriteOnlyAttribute("height", height);
        return this;
    }

    /**
     * Sets the height of the use element (read-write).
     *
     * @param height the height
     * @return this element for method chaining
     */
    public UseElement heightRW(double height) {
        setAttribute("height", String.valueOf(height));
        return this;
    }

    /**
     * Sets the height with a unit (read-write).
     *
     * @param height the height with unit (e.g., "100%", "200px")
     * @return this element for method chaining
     */
    public UseElement heightRW(String height) {
        setAttribute("height", height);
        return this;
    }

    // ========== Convenience methods ==========

    /**
     * Sets the position of the use element.
     * <p>
     * Uses write-only optimization. Use {@link #positionRW(double, double)} if you need to read the values back.
     * </p>
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public UseElement position(double x, double y) {
        x(x);
        y(y);
        return this;
    }

    /**
     * Sets the position of the use element (read-write).
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public UseElement positionRW(double x, double y) {
        xRW(x);
        yRW(y);
        return this;
    }

    /**
     * Sets the size of the use element.
     * <p>
     * Uses write-only optimization. Use {@link #sizeRW(double, double)} if you need to read the values back.
     * </p>
     *
     * @param width  the width
     * @param height the height
     * @return this element for method chaining
     */
    public UseElement size(double width, double height) {
        width(width);
        height(height);
        return this;
    }

    /**
     * Sets the size of the use element (read-write).
     *
     * @param width  the width
     * @param height the height
     * @return this element for method chaining
     */
    public UseElement sizeRW(double width, double height) {
        widthRW(width);
        heightRW(height);
        return this;
    }
}
