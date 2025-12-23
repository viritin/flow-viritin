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
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/use">MDN: use element</a>
 */
public class UseElement extends SvgGraphicsElement {

    public UseElement() {
        super("use");
    }

    /**
     * Creates a use element referencing the element with the given ID.
     *
     * @param elementId the ID of the element to reference (without #)
     */
    public UseElement(String elementId) {
        super("use");
        href("#" + elementId);
    }

    /**
     * Sets the href attribute to reference another element.
     *
     * @param href the reference URL (e.g., "#myElement" or "sprites.svg#icon")
     * @return this element for method chaining
     */
    public UseElement href(String href) {
        setAttribute("href", href);
        return this;
    }

    /**
     * Sets the x coordinate where the referenced element will be placed.
     *
     * @param x the x coordinate
     * @return this element for method chaining
     */
    public UseElement x(double x) {
        setAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the x coordinate with a unit.
     *
     * @param x the x coordinate with unit (e.g., "10%", "50px")
     * @return this element for method chaining
     */
    public UseElement x(String x) {
        setAttribute("x", x);
        return this;
    }

    /**
     * Sets the y coordinate where the referenced element will be placed.
     *
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public UseElement y(double y) {
        setAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the y coordinate with a unit.
     *
     * @param y the y coordinate with unit (e.g., "10%", "50px")
     * @return this element for method chaining
     */
    public UseElement y(String y) {
        setAttribute("y", y);
        return this;
    }

    /**
     * Sets the width of the use element.
     *
     * @param width the width
     * @return this element for method chaining
     */
    public UseElement width(double width) {
        setAttribute("width", String.valueOf(width));
        return this;
    }

    /**
     * Sets the width with a unit.
     *
     * @param width the width with unit (e.g., "100%", "200px")
     * @return this element for method chaining
     */
    public UseElement width(String width) {
        setAttribute("width", width);
        return this;
    }

    /**
     * Sets the height of the use element.
     *
     * @param height the height
     * @return this element for method chaining
     */
    public UseElement height(double height) {
        setAttribute("height", String.valueOf(height));
        return this;
    }

    /**
     * Sets the height with a unit.
     *
     * @param height the height with unit (e.g., "100%", "200px")
     * @return this element for method chaining
     */
    public UseElement height(String height) {
        setAttribute("height", height);
        return this;
    }

    /**
     * Sets the position of the use element.
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
     * Sets the size of the use element.
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
}
