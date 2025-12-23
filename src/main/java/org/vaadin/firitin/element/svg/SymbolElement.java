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

    /**
     * Sets the viewBox for this symbol.
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
    public SymbolElement viewBox(double minX, double minY, double width, double height) {
        setAttribute("viewBox", "%s %s %s %s".formatted(minX, minY, width, height));
        return this;
    }

    /**
     * Sets the preserveAspectRatio attribute.
     *
     * @param value the preserveAspectRatio value (e.g., "xMidYMid meet")
     * @return this element for method chaining
     */
    public SymbolElement preserveAspectRatio(String value) {
        setAttribute("preserveAspectRatio", value);
        return this;
    }

    /**
     * Sets the preserveAspectRatio attribute.
     *
     * @param ratio the preserveAspectRatio setting
     * @return this element for method chaining
     */
    public SymbolElement preserveAspectRatio(PreserveAspectRatio ratio) {
        setAttribute("preserveAspectRatio", ratio.toString());
        return this;
    }

    /**
     * Sets the x coordinate for symbol positioning (when used in certain contexts).
     *
     * @param x the x coordinate
     * @return this element for method chaining
     */
    public SymbolElement x(double x) {
        setAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the y coordinate for symbol positioning (when used in certain contexts).
     *
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public SymbolElement y(double y) {
        setAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the width for this symbol.
     *
     * @param width the width
     * @return this element for method chaining
     */
    public SymbolElement width(double width) {
        setAttribute("width", String.valueOf(width));
        return this;
    }

    /**
     * Sets the height for this symbol.
     *
     * @param height the height
     * @return this element for method chaining
     */
    public SymbolElement height(double height) {
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
