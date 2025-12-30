package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <clipPath>} element.
 * <p>
 * The {@code <clipPath>} element defines a clipping path to be used by the
 * clip-path property. A clipping path restricts the region to which paint
 * can be applied. Conceptually, parts of the drawing that lie outside of
 * the region bounded by the clipping path are not drawn.
 * </p>
 * <p>
 * Clip paths must be placed inside a {@code <defs>} element and referenced
 * by ID using the clip-path attribute (e.g., clip-path="url(#myClip)").
 * </p>
 * <h2>Write-Only vs Read-Write Methods</h2>
 * <p>
 * This class provides two variants for each attribute setter:
 * </p>
 * <ul>
 *   <li><strong>Default methods</strong> (e.g., {@code clipPathUnits()}) - Use an optimized
 *       write-only approach. Attribute values are NOT stored on the server and cannot be
 *       retrieved via {@code getAttribute()}.</li>
 *   <li><strong>RW methods</strong> (e.g., {@code clipPathUnitsRW()}) - Use traditional
 *       {@code setAttribute()} which stores values on the server for later retrieval.</li>
 * </ul>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/clipPath">MDN: clipPath element</a>
 */
public class ClipPathElement extends SvgElement {

    public ClipPathElement() {
        super("clipPath");
    }

    /**
     * Creates a clip path with the given ID.
     *
     * @param id the ID for referencing this clip path
     */
    public ClipPathElement(String id) {
        super("clipPath");
        id(id);
    }

    /**
     * Clip path unit options.
     */
    public enum ClipPathUnits {
        /** Coordinates are relative to the user coordinate system (default) */
        USER_SPACE_ON_USE("userSpaceOnUse"),
        /** Coordinates are relative to the bounding box of the element */
        OBJECT_BOUNDING_BOX("objectBoundingBox");

        private final String value;

        ClipPathUnits(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    // ========== clipPathUnits attribute ==========

    /**
     * Sets the coordinate system for the clip path contents.
     * <p>
     * Uses write-only optimization. Use {@link #clipPathUnitsRW(ClipPathUnits)} if you need to read the value back.
     * </p>
     *
     * @param units the clip path units
     * @return this element for method chaining
     */
    public ClipPathElement clipPathUnits(ClipPathUnits units) {
        setWriteOnlyAttribute("clipPathUnits", units.toString());
        return this;
    }

    /**
     * Sets the coordinate system for the clip path contents (read-write).
     *
     * @param units the clip path units
     * @return this element for method chaining
     */
    public ClipPathElement clipPathUnitsRW(ClipPathUnits units) {
        setAttribute("clipPathUnits", units.toString());
        return this;
    }

    /**
     * Adds shape elements to this clip path.
     *
     * @param shapes the shape elements that define the clipping region
     * @return this element for method chaining
     */
    public ClipPathElement add(SvgElement... shapes) {
        appendChild(shapes);
        return this;
    }
}
