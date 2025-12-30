package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <image>} element.
 * <p>
 * The {@code <image>} element includes images inside SVG documents.
 * It can display raster image files (PNG, JPEG) or other SVG files.
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

    // ========== href attribute ==========

    /**
     * Sets the URL of the image.
     * <p>
     * Uses write-only optimization. Use {@link #hrefRW(String)} if you need to read the value back.
     * </p>
     *
     * @param href the image URL
     * @return this element for method chaining
     */
    public ImageElement href(String href) {
        setWriteOnlyAttribute("href", href);
        return this;
    }

    /**
     * Sets the URL of the image (read-write).
     *
     * @param href the image URL
     * @return this element for method chaining
     */
    public ImageElement hrefRW(String href) {
        setAttribute("href", href);
        return this;
    }

    // ========== x attribute ==========

    /**
     * Sets the x coordinate of the image.
     * <p>
     * Uses write-only optimization. Use {@link #xRW(double)} if you need to read the value back.
     * </p>
     *
     * @param x the x coordinate
     * @return this element for method chaining
     */
    public ImageElement x(double x) {
        setWriteOnlyAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the x coordinate with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #xRW(String)} if you need to read the value back.
     * </p>
     *
     * @param x the x coordinate (e.g., "10%")
     * @return this element for method chaining
     */
    public ImageElement x(String x) {
        setWriteOnlyAttribute("x", x);
        return this;
    }

    /**
     * Sets the x coordinate of the image (read-write).
     *
     * @param x the x coordinate
     * @return this element for method chaining
     */
    public ImageElement xRW(double x) {
        setAttribute("x", String.valueOf(x));
        return this;
    }

    /**
     * Sets the x coordinate with a unit (read-write).
     *
     * @param x the x coordinate (e.g., "10%")
     * @return this element for method chaining
     */
    public ImageElement xRW(String x) {
        setAttribute("x", x);
        return this;
    }

    // ========== y attribute ==========

    /**
     * Sets the y coordinate of the image.
     * <p>
     * Uses write-only optimization. Use {@link #yRW(double)} if you need to read the value back.
     * </p>
     *
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public ImageElement y(double y) {
        setWriteOnlyAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the y coordinate with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #yRW(String)} if you need to read the value back.
     * </p>
     *
     * @param y the y coordinate (e.g., "10%")
     * @return this element for method chaining
     */
    public ImageElement y(String y) {
        setWriteOnlyAttribute("y", y);
        return this;
    }

    /**
     * Sets the y coordinate of the image (read-write).
     *
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public ImageElement yRW(double y) {
        setAttribute("y", String.valueOf(y));
        return this;
    }

    /**
     * Sets the y coordinate with a unit (read-write).
     *
     * @param y the y coordinate (e.g., "10%")
     * @return this element for method chaining
     */
    public ImageElement yRW(String y) {
        setAttribute("y", y);
        return this;
    }

    // ========== width attribute ==========

    /**
     * Sets the width of the image.
     * <p>
     * Uses write-only optimization. Use {@link #widthRW(double)} if you need to read the value back.
     * </p>
     *
     * @param width the width
     * @return this element for method chaining
     */
    public ImageElement width(double width) {
        setWriteOnlyAttribute("width", String.valueOf(width));
        return this;
    }

    /**
     * Sets the width with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #widthRW(String)} if you need to read the value back.
     * </p>
     *
     * @param width the width (e.g., "100%", "200px")
     * @return this element for method chaining
     */
    public ImageElement width(String width) {
        setWriteOnlyAttribute("width", width);
        return this;
    }

    /**
     * Sets the width of the image (read-write).
     *
     * @param width the width
     * @return this element for method chaining
     */
    public ImageElement widthRW(double width) {
        setAttribute("width", String.valueOf(width));
        return this;
    }

    /**
     * Sets the width with a unit (read-write).
     *
     * @param width the width (e.g., "100%", "200px")
     * @return this element for method chaining
     */
    public ImageElement widthRW(String width) {
        setAttribute("width", width);
        return this;
    }

    // ========== height attribute ==========

    /**
     * Sets the height of the image.
     * <p>
     * Uses write-only optimization. Use {@link #heightRW(double)} if you need to read the value back.
     * </p>
     *
     * @param height the height
     * @return this element for method chaining
     */
    public ImageElement height(double height) {
        setWriteOnlyAttribute("height", String.valueOf(height));
        return this;
    }

    /**
     * Sets the height with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #heightRW(String)} if you need to read the value back.
     * </p>
     *
     * @param height the height (e.g., "100%", "200px")
     * @return this element for method chaining
     */
    public ImageElement height(String height) {
        setWriteOnlyAttribute("height", height);
        return this;
    }

    /**
     * Sets the height of the image (read-write).
     *
     * @param height the height
     * @return this element for method chaining
     */
    public ImageElement heightRW(double height) {
        setAttribute("height", String.valueOf(height));
        return this;
    }

    /**
     * Sets the height with a unit (read-write).
     *
     * @param height the height (e.g., "100%", "200px")
     * @return this element for method chaining
     */
    public ImageElement heightRW(String height) {
        setAttribute("height", height);
        return this;
    }

    // ========== Convenience methods ==========

    /**
     * Sets the position of the image.
     * <p>
     * Uses write-only optimization. Use {@link #positionRW(double, double)} if you need to read the values back.
     * </p>
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
     * Sets the position of the image (read-write).
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return this element for method chaining
     */
    public ImageElement positionRW(double x, double y) {
        xRW(x);
        yRW(y);
        return this;
    }

    /**
     * Sets the size of the image.
     * <p>
     * Uses write-only optimization. Use {@link #sizeRW(double, double)} if you need to read the values back.
     * </p>
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
     * Sets the size of the image (read-write).
     *
     * @param width  the width
     * @param height the height
     * @return this element for method chaining
     */
    public ImageElement sizeRW(double width, double height) {
        widthRW(width);
        heightRW(height);
        return this;
    }

    /**
     * Sets the bounds of the image (position and size).
     * <p>
     * Uses write-only optimization. Use {@link #boundsRW(double, double, double, double)} if you need to read the values back.
     * </p>
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
     * Sets the bounds of the image (position and size) (read-write).
     *
     * @param x      the x coordinate
     * @param y      the y coordinate
     * @param width  the width
     * @param height the height
     * @return this element for method chaining
     */
    public ImageElement boundsRW(double x, double y, double width, double height) {
        positionRW(x, y);
        sizeRW(width, height);
        return this;
    }

    // ========== preserveAspectRatio attribute ==========

    /**
     * Sets how the image should be scaled to fit.
     * <p>
     * Uses write-only optimization. Use {@link #preserveAspectRatioRW(String)} if you need to read the value back.
     * </p>
     *
     * @param preserveAspectRatio the preserveAspectRatio value (e.g., "xMidYMid meet")
     * @return this element for method chaining
     */
    public ImageElement preserveAspectRatio(String preserveAspectRatio) {
        setWriteOnlyAttribute("preserveAspectRatio", preserveAspectRatio);
        return this;
    }

    /**
     * Sets how the image should be scaled to fit (read-write).
     *
     * @param preserveAspectRatio the preserveAspectRatio value (e.g., "xMidYMid meet")
     * @return this element for method chaining
     */
    public ImageElement preserveAspectRatioRW(String preserveAspectRatio) {
        setAttribute("preserveAspectRatio", preserveAspectRatio);
        return this;
    }

    /**
     * Sets how the image should be scaled to fit.
     * <p>
     * Uses write-only optimization. Use {@link #preserveAspectRatioRW(PreserveAspectRatio)} if you need to read the value back.
     * </p>
     *
     * @param ratio the preserveAspectRatio setting
     * @return this element for method chaining
     */
    public ImageElement preserveAspectRatio(PreserveAspectRatio ratio) {
        setWriteOnlyAttribute("preserveAspectRatio", ratio.toString());
        return this;
    }

    /**
     * Sets how the image should be scaled to fit (read-write).
     *
     * @param ratio the preserveAspectRatio setting
     * @return this element for method chaining
     */
    public ImageElement preserveAspectRatioRW(PreserveAspectRatio ratio) {
        setAttribute("preserveAspectRatio", ratio.toString());
        return this;
    }

    // ========== crossorigin attribute ==========

    /**
     * Sets the crossorigin attribute for CORS requests.
     * <p>
     * Uses write-only optimization. Use {@link #crossoriginRW(String)} if you need to read the value back.
     * </p>
     *
     * @param crossorigin the crossorigin value ("anonymous" or "use-credentials")
     * @return this element for method chaining
     */
    public ImageElement crossorigin(String crossorigin) {
        setWriteOnlyAttribute("crossorigin", crossorigin);
        return this;
    }

    /**
     * Sets the crossorigin attribute for CORS requests (read-write).
     *
     * @param crossorigin the crossorigin value ("anonymous" or "use-credentials")
     * @return this element for method chaining
     */
    public ImageElement crossoriginRW(String crossorigin) {
        setAttribute("crossorigin", crossorigin);
        return this;
    }
}
