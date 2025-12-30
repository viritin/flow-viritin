package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <symbol>} element.
 * <p>
 * The {@code <symbol>} element is used to define graphical template objects
 * that can be instantiated by a {@code <use>} element. Using {@code <symbol>}
 * elements for graphics that are used multiple times adds structure and
 * semantics.
 * </p>
 * <p>
 * Unlike {@code <g>}, a {@code <symbol>} element itself is not rendered.
 * Only instances of a {@code <symbol>} element (i.e., a reference to it
 * from a {@code <use>} element) are rendered.
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
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/symbol">MDN: symbol element</a>
 */
public class SymbolElement extends SvgElement {

    public SymbolElement() {
        super("symbol");
    }

    /**
     * Creates a symbol with the given ID.
     *
     * @param id the ID for this symbol (used by use element to reference it)
     */
    public SymbolElement(String id) {
        super("symbol");
        id(id);
    }

    // ========== viewBox attribute ==========

    /**
     * Sets the viewBox for this symbol.
     * <p>
     * The viewBox defines the coordinate system for the symbol's contents.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #viewBoxRW(double, double, double, double)} if you need to read the value back.
     * </p>
     *
     * @param minX   the minimum x value
     * @param minY   the minimum y value
     * @param width  the width of the viewBox
     * @param height the height of the viewBox
     * @return this element for method chaining
     */
    public SymbolElement viewBox(double minX, double minY, double width, double height) {
        setWriteOnlyAttribute("viewBox", "%s %s %s %s".formatted(minX, minY, width, height));
        return this;
    }

    /**
     * Sets the viewBox for this symbol (read-write).
     * <p>
     * The viewBox defines the coordinate system for the symbol's contents.
     * </p>
     *
     * @param minX   the minimum x value
     * @param minY   the minimum y value
     * @param width  the width of the viewBox
     * @param height the height of the viewBox
     * @return this element for method chaining
     */
    public SymbolElement viewBoxRW(double minX, double minY, double width, double height) {
        setAttribute("viewBox", "%s %s %s %s".formatted(minX, minY, width, height));
        return this;
    }

    // ========== preserveAspectRatio attribute ==========

    /**
     * Sets the preserveAspectRatio attribute.
     * <p>
     * Uses write-only optimization. Use {@link #preserveAspectRatioRW(String)} if you need to read the value back.
     * </p>
     *
     * @param value the preserveAspectRatio value (e.g., "xMidYMid meet")
     * @return this element for method chaining
     */
    public SymbolElement preserveAspectRatio(String value) {
        setWriteOnlyAttribute("preserveAspectRatio", value);
        return this;
    }

    /**
     * Sets the preserveAspectRatio attribute (read-write).
     *
     * @param value the preserveAspectRatio value (e.g., "xMidYMid meet")
     * @return this element for method chaining
     */
    public SymbolElement preserveAspectRatioRW(String value) {
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
    public SymbolElement preserveAspectRatio(PreserveAspectRatio ratio) {
        setWriteOnlyAttribute("preserveAspectRatio", ratio.toString());
        return this;
    }

    /**
     * Sets the preserveAspectRatio attribute (read-write).
     *
     * @param ratio the preserveAspectRatio setting
     * @return this element for method chaining
     */
    public SymbolElement preserveAspectRatioRW(PreserveAspectRatio ratio) {
        setAttribute("preserveAspectRatio", ratio.toString());
        return this;
    }

    // ========== x attribute ==========

    /**
     * Sets the x coordinate for symbol positioning (when used in certain contexts).
     * <p>
     * Uses write-only optimization. Use {@link #xRW(double)} if you need to read the value back.
     * </p>
     *
     * @param x the x coordinate
     * @return this element for method chaining
     */
    public SymbolElement x(double x) {
        setWriteOnlyAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the x coordinate for symbol positioning (read-write).
     *
     * @param x the x coordinate
     * @return this element for method chaining
     */
    public SymbolElement xRW(double x) {
        setAttribute("x", String.valueOf(x));
        return this;
    }

    // ========== y attribute ==========

    /**
     * Sets the y coordinate for symbol positioning (when used in certain contexts).
     * <p>
     * Uses write-only optimization. Use {@link #yRW(double)} if you need to read the value back.
     * </p>
     *
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public SymbolElement y(double y) {
        setWriteOnlyAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the y coordinate for symbol positioning (read-write).
     *
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public SymbolElement yRW(double y) {
        setAttribute("y", String.valueOf(y));
        return this;
    }

    // ========== width attribute ==========

    /**
     * Sets the width for this symbol.
     * <p>
     * Uses write-only optimization. Use {@link #widthRW(double)} if you need to read the value back.
     * </p>
     *
     * @param width the width
     * @return this element for method chaining
     */
    public SymbolElement width(double width) {
        setWriteOnlyAttribute("width", String.valueOf(width));
        return this;
    }

    /**
     * Sets the width for this symbol (read-write).
     *
     * @param width the width
     * @return this element for method chaining
     */
    public SymbolElement widthRW(double width) {
        setAttribute("width", String.valueOf(width));
        return this;
    }

    // ========== height attribute ==========

    /**
     * Sets the height for this symbol.
     * <p>
     * Uses write-only optimization. Use {@link #heightRW(double)} if you need to read the value back.
     * </p>
     *
     * @param height the height
     * @return this element for method chaining
     */
    public SymbolElement height(double height) {
        setWriteOnlyAttribute("height", String.valueOf(height));
        return this;
    }

    /**
     * Sets the height for this symbol (read-write).
     *
     * @param height the height
     * @return this element for method chaining
     */
    public SymbolElement heightRW(double height) {
        setAttribute("height", String.valueOf(height));
        return this;
    }

    /**
     * Adds child elements to this symbol.
     *
     * @param children the elements to add
     * @return this element for method chaining
     */
    public SymbolElement add(SvgElement... children) {
        appendChild(children);
        return this;
    }
}
