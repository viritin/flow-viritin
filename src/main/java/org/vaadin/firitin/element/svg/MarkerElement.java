package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <marker>} element.
 * <p>
 * The {@code <marker>} element defines a graphic used for drawing arrows or polymarkers
 * on shape elements like {@code <path>}, {@code <line>}, {@code <polyline>}, and {@code <polygon>}.
 * </p>
 * <p>
 * Markers must be placed inside a {@code <defs>} element and referenced
 * by ID using the marker-start, marker-mid, or marker-end attributes.
 * </p>
 * <h3>Example - Creating an arrowhead marker:</h3>
 * <pre>{@code
 * MarkerElement arrow = new MarkerElement("arrowhead")
 *     .viewBox(0, 0, 10, 10)
 *     .refX(10).refY(5)
 *     .markerWidth(6).markerHeight(6)
 *     .orient(MarkerElement.Orient.AUTO);
 *
 * PathElement arrowPath = new PathElement()
 *     .d(p -> p.moveTo(0, 0).lineTo(10, 5).lineTo(0, 10).closePath())
 *     .fill(HexColor.of("#ff0000"));
 * arrow.add(arrowPath);
 *
 * // Use on a line
 * lineElement.markerEnd(arrow);
 * }</pre>
 * <h2>Write-Only vs Read-Write Methods</h2>
 * <p>
 * This class provides two variants for each attribute setter:
 * </p>
 * <ul>
 *   <li><strong>Default methods</strong> (e.g., {@code refX()}, {@code refY()}) - Use an optimized
 *       write-only approach. Attribute values are NOT stored on the server and cannot be
 *       retrieved via {@code getAttribute()}.</li>
 *   <li><strong>RW methods</strong> (e.g., {@code refXRW()}, {@code refYRW()}) - Use traditional
 *       {@code setAttribute()} which stores values on the server for later retrieval.</li>
 * </ul>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/marker">MDN: marker element</a>
 */
public class MarkerElement extends SvgElement {

    public MarkerElement() {
        super("marker");
    }

    /**
     * Creates a marker with the given ID.
     *
     * @param id the ID for referencing this marker
     */
    public MarkerElement(String id) {
        super("marker");
        id(id);
    }

    // ========== viewBox attribute ==========

    /**
     * Sets the viewBox for the marker contents.
     * <p>
     * Uses write-only optimization. Use {@link #viewBoxRW(double, double, double, double)} if you need to read the value back.
     * </p>
     *
     * @param minX   the minimum x value
     * @param minY   the minimum y value
     * @param width  the width
     * @param height the height
     * @return this element for method chaining
     */
    public MarkerElement viewBox(double minX, double minY, double width, double height) {
        setWriteOnlyAttribute("viewBox", "%s %s %s %s".formatted(minX, minY, width, height));
        return this;
    }

    /**
     * Sets the viewBox for the marker contents (read-write).
     *
     * @param minX   the minimum x value
     * @param minY   the minimum y value
     * @param width  the width
     * @param height the height
     * @return this element for method chaining
     */
    public MarkerElement viewBoxRW(double minX, double minY, double width, double height) {
        setAttribute("viewBox", "%s %s %s %s".formatted(minX, minY, width, height));
        return this;
    }

    // ========== refX attribute ==========

    /**
     * Sets the x coordinate of the marker's reference point.
     * <p>
     * This is the point within the marker that is placed at the marked position.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #refXRW(double)} if you need to read the value back.
     * </p>
     *
     * @param refX the x coordinate of the reference point
     * @return this element for method chaining
     */
    public MarkerElement refX(double refX) {
        setWriteOnlyAttribute("refX", String.valueOf(refX));
        return this;
    }

    /**
     * Sets the x coordinate of the marker's reference point with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #refXRW(String)} if you need to read the value back.
     * </p>
     *
     * @param refX the x coordinate (e.g., "10", "left", "center", "right")
     * @return this element for method chaining
     */
    public MarkerElement refX(String refX) {
        setWriteOnlyAttribute("refX", refX);
        return this;
    }

    /**
     * Sets the x coordinate of the marker's reference point (read-write).
     *
     * @param refX the x coordinate of the reference point
     * @return this element for method chaining
     */
    public MarkerElement refXRW(double refX) {
        setAttribute("refX", String.valueOf(refX));
        return this;
    }

    /**
     * Sets the x coordinate of the marker's reference point with a unit (read-write).
     *
     * @param refX the x coordinate (e.g., "10", "left", "center", "right")
     * @return this element for method chaining
     */
    public MarkerElement refXRW(String refX) {
        setAttribute("refX", refX);
        return this;
    }

    // ========== refY attribute ==========

    /**
     * Sets the y coordinate of the marker's reference point.
     * <p>
     * This is the point within the marker that is placed at the marked position.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #refYRW(double)} if you need to read the value back.
     * </p>
     *
     * @param refY the y coordinate of the reference point
     * @return this element for method chaining
     */
    public MarkerElement refY(double refY) {
        setWriteOnlyAttribute("refY", String.valueOf(refY));
        return this;
    }

    /**
     * Sets the y coordinate of the marker's reference point with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #refYRW(String)} if you need to read the value back.
     * </p>
     *
     * @param refY the y coordinate (e.g., "10", "top", "center", "bottom")
     * @return this element for method chaining
     */
    public MarkerElement refY(String refY) {
        setWriteOnlyAttribute("refY", refY);
        return this;
    }

    /**
     * Sets the y coordinate of the marker's reference point (read-write).
     *
     * @param refY the y coordinate of the reference point
     * @return this element for method chaining
     */
    public MarkerElement refYRW(double refY) {
        setAttribute("refY", String.valueOf(refY));
        return this;
    }

    /**
     * Sets the y coordinate of the marker's reference point with a unit (read-write).
     *
     * @param refY the y coordinate (e.g., "10", "top", "center", "bottom")
     * @return this element for method chaining
     */
    public MarkerElement refYRW(String refY) {
        setAttribute("refY", refY);
        return this;
    }

    // ========== Convenience method for ref ==========

    /**
     * Sets both refX and refY coordinates at once.
     * <p>
     * Uses write-only optimization. Use {@link #refRW(double, double)} if you need to read the values back.
     * </p>
     *
     * @param refX the x coordinate of the reference point
     * @param refY the y coordinate of the reference point
     * @return this element for method chaining
     */
    public MarkerElement ref(double refX, double refY) {
        return refX(refX).refY(refY);
    }

    /**
     * Sets both refX and refY coordinates at once (read-write).
     *
     * @param refX the x coordinate of the reference point
     * @param refY the y coordinate of the reference point
     * @return this element for method chaining
     */
    public MarkerElement refRW(double refX, double refY) {
        return refXRW(refX).refYRW(refY);
    }

    // ========== markerWidth attribute ==========

    /**
     * Sets the width of the marker viewport.
     * <p>
     * Uses write-only optimization. Use {@link #markerWidthRW(double)} if you need to read the value back.
     * </p>
     *
     * @param width the marker width
     * @return this element for method chaining
     */
    public MarkerElement markerWidth(double width) {
        setWriteOnlyAttribute("markerWidth", String.valueOf(width));
        return this;
    }

    /**
     * Sets the width of the marker viewport with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #markerWidthRW(String)} if you need to read the value back.
     * </p>
     *
     * @param width the marker width (e.g., "10", "10px")
     * @return this element for method chaining
     */
    public MarkerElement markerWidth(String width) {
        setWriteOnlyAttribute("markerWidth", width);
        return this;
    }

    /**
     * Sets the width of the marker viewport (read-write).
     *
     * @param width the marker width
     * @return this element for method chaining
     */
    public MarkerElement markerWidthRW(double width) {
        setAttribute("markerWidth", String.valueOf(width));
        return this;
    }

    /**
     * Sets the width of the marker viewport with a unit (read-write).
     *
     * @param width the marker width (e.g., "10", "10px")
     * @return this element for method chaining
     */
    public MarkerElement markerWidthRW(String width) {
        setAttribute("markerWidth", width);
        return this;
    }

    // ========== markerHeight attribute ==========

    /**
     * Sets the height of the marker viewport.
     * <p>
     * Uses write-only optimization. Use {@link #markerHeightRW(double)} if you need to read the value back.
     * </p>
     *
     * @param height the marker height
     * @return this element for method chaining
     */
    public MarkerElement markerHeight(double height) {
        setWriteOnlyAttribute("markerHeight", String.valueOf(height));
        return this;
    }

    /**
     * Sets the height of the marker viewport with a unit.
     * <p>
     * Uses write-only optimization. Use {@link #markerHeightRW(String)} if you need to read the value back.
     * </p>
     *
     * @param height the marker height (e.g., "10", "10px")
     * @return this element for method chaining
     */
    public MarkerElement markerHeight(String height) {
        setWriteOnlyAttribute("markerHeight", height);
        return this;
    }

    /**
     * Sets the height of the marker viewport (read-write).
     *
     * @param height the marker height
     * @return this element for method chaining
     */
    public MarkerElement markerHeightRW(double height) {
        setAttribute("markerHeight", String.valueOf(height));
        return this;
    }

    /**
     * Sets the height of the marker viewport with a unit (read-write).
     *
     * @param height the marker height (e.g., "10", "10px")
     * @return this element for method chaining
     */
    public MarkerElement markerHeightRW(String height) {
        setAttribute("markerHeight", height);
        return this;
    }

    // ========== Convenience method for marker size ==========

    /**
     * Sets both markerWidth and markerHeight at once.
     * <p>
     * Uses write-only optimization. Use {@link #markerSizeRW(double, double)} if you need to read the values back.
     * </p>
     *
     * @param width  the marker width
     * @param height the marker height
     * @return this element for method chaining
     */
    public MarkerElement markerSize(double width, double height) {
        return markerWidth(width).markerHeight(height);
    }

    /**
     * Sets both markerWidth and markerHeight at once (read-write).
     *
     * @param width  the marker width
     * @param height the marker height
     * @return this element for method chaining
     */
    public MarkerElement markerSizeRW(double width, double height) {
        return markerWidthRW(width).markerHeightRW(height);
    }

    // ========== orient attribute ==========

    /**
     * Orientation options for markers.
     */
    public enum Orient {
        /** Marker points in a fixed direction (0 degrees) */
        ZERO("0"),
        /** Marker rotates to match the direction of the path at the marker position */
        AUTO("auto"),
        /** Marker rotates to point in the opposite direction of the path */
        AUTO_START_REVERSE("auto-start-reverse");

        private final String value;

        Orient(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Sets the orientation of the marker.
     * <p>
     * Uses write-only optimization. Use {@link #orientRW(Orient)} if you need to read the value back.
     * </p>
     *
     * @param orient the orientation
     * @return this element for method chaining
     */
    public MarkerElement orient(Orient orient) {
        setWriteOnlyAttribute("orient", orient.toString());
        return this;
    }

    /**
     * Sets the orientation of the marker to a specific angle in degrees.
     * <p>
     * Uses write-only optimization. Use {@link #orientRW(double)} if you need to read the value back.
     * </p>
     *
     * @param angle the rotation angle in degrees
     * @return this element for method chaining
     */
    public MarkerElement orient(double angle) {
        setWriteOnlyAttribute("orient", String.valueOf(angle));
        return this;
    }

    /**
     * Sets the orientation of the marker (read-write).
     *
     * @param orient the orientation
     * @return this element for method chaining
     */
    public MarkerElement orientRW(Orient orient) {
        setAttribute("orient", orient.toString());
        return this;
    }

    /**
     * Sets the orientation of the marker to a specific angle in degrees (read-write).
     *
     * @param angle the rotation angle in degrees
     * @return this element for method chaining
     */
    public MarkerElement orientRW(double angle) {
        setAttribute("orient", String.valueOf(angle));
        return this;
    }

    /**
     * Sets the marker to automatically orient along the path direction.
     * <p>
     * Uses write-only optimization. Use {@link #orientAutoRW()} if you need to read the value back.
     * </p>
     *
     * @return this element for method chaining
     */
    public MarkerElement orientAuto() {
        return orient(Orient.AUTO);
    }

    /**
     * Sets the marker to automatically orient along the path direction (read-write).
     *
     * @return this element for method chaining
     */
    public MarkerElement orientAutoRW() {
        return orientRW(Orient.AUTO);
    }

    /**
     * Sets the marker to automatically orient opposite to the path direction at the start.
     * <p>
     * This is useful for creating arrows where the start arrow points backward.
     * </p>
     * <p>
     * Uses write-only optimization. Use {@link #orientAutoStartReverseRW()} if you need to read the value back.
     * </p>
     *
     * @return this element for method chaining
     */
    public MarkerElement orientAutoStartReverse() {
        return orient(Orient.AUTO_START_REVERSE);
    }

    /**
     * Sets the marker to automatically orient opposite to the path direction at the start (read-write).
     *
     * @return this element for method chaining
     */
    public MarkerElement orientAutoStartReverseRW() {
        return orientRW(Orient.AUTO_START_REVERSE);
    }

    // ========== markerUnits attribute ==========

    /**
     * Marker unit options.
     */
    public enum MarkerUnits {
        /** markerWidth and markerHeight are in stroke-width units */
        STROKE_WIDTH("strokeWidth"),
        /** markerWidth and markerHeight are in user coordinate system units */
        USER_SPACE_ON_USE("userSpaceOnUse");

        private final String value;

        MarkerUnits(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Sets the coordinate system for markerWidth and markerHeight.
     * <p>
     * Uses write-only optimization. Use {@link #markerUnitsRW(MarkerUnits)} if you need to read the value back.
     * </p>
     *
     * @param units the marker units
     * @return this element for method chaining
     */
    public MarkerElement markerUnits(MarkerUnits units) {
        setWriteOnlyAttribute("markerUnits", units.toString());
        return this;
    }

    /**
     * Sets the coordinate system for markerWidth and markerHeight (read-write).
     *
     * @param units the marker units
     * @return this element for method chaining
     */
    public MarkerElement markerUnitsRW(MarkerUnits units) {
        setAttribute("markerUnits", units.toString());
        return this;
    }

    // ========== preserveAspectRatio attribute ==========

    /**
     * Sets the preserveAspectRatio attribute.
     * <p>
     * Uses write-only optimization. Use {@link #preserveAspectRatioRW(String)} if you need to read the value back.
     * </p>
     *
     * @param value the preserveAspectRatio value
     * @return this element for method chaining
     */
    public MarkerElement preserveAspectRatio(String value) {
        setWriteOnlyAttribute("preserveAspectRatio", value);
        return this;
    }

    /**
     * Sets the preserveAspectRatio attribute (read-write).
     *
     * @param value the preserveAspectRatio value
     * @return this element for method chaining
     */
    public MarkerElement preserveAspectRatioRW(String value) {
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
    public MarkerElement preserveAspectRatio(PreserveAspectRatio ratio) {
        setWriteOnlyAttribute("preserveAspectRatio", ratio.toString());
        return this;
    }

    /**
     * Sets the preserveAspectRatio attribute (read-write).
     *
     * @param ratio the preserveAspectRatio setting
     * @return this element for method chaining
     */
    public MarkerElement preserveAspectRatioRW(PreserveAspectRatio ratio) {
        setAttribute("preserveAspectRatio", ratio.toString());
        return this;
    }

    // ========== Content methods ==========

    /**
     * Adds content elements to this marker.
     *
     * @param elements the elements that define the marker graphic
     * @return this element for method chaining
     */
    public MarkerElement add(SvgElement... elements) {
        appendChild(elements);
        return this;
    }
}