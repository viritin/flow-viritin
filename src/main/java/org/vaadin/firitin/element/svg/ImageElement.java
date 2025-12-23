package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <image>} element.
 * <p>
 * The {@code <image>} element includes images inside SVG documents.
 * It can display raster image files (PNG, JPEG) or other SVG files.
 * </p>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/image">MDN: image element</a>
 */
public class ImageElement extends SvgGraphicsElement {

    public ImageElement() {
        super("image");
    }

    /**
     * Creates an image element with the given href.
     *
     * @param href the URL of the image
     */
    public ImageElement(String href) {
        super("image");
        href(href);
    }

    /**
     * Creates an image element with position and size.
     *
     * @param href   the URL of the image
     * @param x      the x coordinate
     * @param y      the y coordinate
     * @param width  the width
     * @param height the height
     */
    public ImageElement(String href, double x, double y, double width, double height) {
        super("image");
        href(href);
        x(x);
        y(y);
        width(width);
        height(height);
    }

    /**
     * Sets the URL of the image.
     *
     * @param href the image URL
     * @return this element for method chaining
     */
    public ImageElement href(String href) {
        setAttribute("href", href);
        return this;
    }

    /**
     * Sets the x coordinate of the image.
     *
     * @param x the x coordinate
     * @return this element for method chaining
     */
    public ImageElement x(double x) {
        setAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the x coordinate with a unit.
     *
     * @param x the x coordinate (e.g., "10%")
     * @return this element for method chaining
     */
    public ImageElement x(String x) {
        setAttribute("x", x);
        return this;
    }

    /**
     * Sets the y coordinate of the image.
     *
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public ImageElement y(double y) {
        setAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the y coordinate with a unit.
     *
     * @param y the y coordinate (e.g., "10%")
     * @return this element for method chaining
     */
    public ImageElement y(String y) {
        setAttribute("y", y);
        return this;
    }

    /**
     * Sets the width of the image.
     *
     * @param width the width
     * @return this element for method chaining
     */
    public ImageElement width(double width) {
        setAttribute("width", String.valueOf(width));
        return this;
    }

    /**
     * Sets the width with a unit.
     *
     * @param width the width (e.g., "100%", "200px")
     * @return this element for method chaining
     */
    public ImageElement width(String width) {
        setAttribute("width", width);
        return this;
    }

    /**
     * Sets the height of the image.
     *
     * @param height the height
     * @return this element for method chaining
     */
    public ImageElement height(double height) {
        setAttribute("height", String.valueOf(height));
        return this;
    }

    /**
     * Sets the height with a unit.
     *
     * @param height the height (e.g., "100%", "200px")
     * @return this element for method chaining
     */
    public ImageElement height(String height) {
        setAttribute("height", height);
        return this;
    }

    /**
     * Sets the position of the image.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public ImageElement position(double x, double y) {
        x(x);
        y(y);
        return this;
    }

    /**
     * Sets the size of the image.
     *
     * @param width  the width
     * @param height the height
     * @return this element for method chaining
     */
    public ImageElement size(double width, double height) {
        width(width);
        height(height);
        return this;
    }

    /**
     * Sets the bounds of the image (position and size).
     *
     * @param x      the x coordinate
     * @param y      the y coordinate
     * @param width  the width
     * @param height the height
     * @return this element for method chaining
     */
    public ImageElement bounds(double x, double y, double width, double height) {
        position(x, y);
        size(width, height);
        return this;
    }

    /**
     * Sets how the image should be scaled to fit.
     *
     * @param preserveAspectRatio the preserveAspectRatio value (e.g., "xMidYMid meet")
     * @return this element for method chaining
     */
    public ImageElement preserveAspectRatio(String preserveAspectRatio) {
        setAttribute("preserveAspectRatio", preserveAspectRatio);
        return this;
    }

    /**
     * Sets how the image should be scaled to fit.
     *
     * @param ratio the preserveAspectRatio setting
     * @return this element for method chaining
     */
    public ImageElement preserveAspectRatio(PreserveAspectRatio ratio) {
        setAttribute("preserveAspectRatio", ratio.toString());
        return this;
    }

    /**
     * Sets the crossorigin attribute for CORS requests.
     *
     * @param crossorigin the crossorigin value ("anonymous" or "use-credentials")
     * @return this element for method chaining
     */
    public ImageElement crossorigin(String crossorigin) {
        setAttribute("crossorigin", crossorigin);
        return this;
    }
}
