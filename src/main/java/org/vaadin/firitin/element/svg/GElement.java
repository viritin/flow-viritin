package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <g>} (group) element.
 * <p>
 * The {@code <g>} element is a container used to group other SVG elements.
 * Transformations applied to the {@code <g>} element are performed on its
 * child elements. Attributes applied to the {@code <g>} element are inherited
 * by its children.
 * </p>
 * <p>
 * Groups are useful for:
 * <ul>
 *   <li>Applying transformations to multiple elements at once</li>
 *   <li>Applying common styles to multiple elements</li>
 *   <li>Organizing complex drawings into logical parts</li>
 * </ul>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/g">MDN: g element</a>
 */
public class GElement extends SvgGraphicsElement {

    public GElement() {
        super("g");
    }

    /**
     * Appends child elements to this group.
     *
     * @param children the elements to add
     * @return this element for method chaining
     */
    public GElement add(SvgElement... children) {
        appendChild(children);
        return this;
    }

    // ========== Animation methods ==========

    /**
     * Creates a translation animation and appends it to this group.
     * <p>
     * This animates the entire group and all its children.
     * Example usage:
     * <pre>{@code
     * group.animateTranslate()
     *     .translateFromTo(0, 0, 100, 50)
     *     .dur(Duration.ofSeconds(2))
     *     .beginElement();
     * }</pre>
     *
     * @return the animation element for further configuration
     */
    public AnimateTransformElement animateTranslate() {
        AnimateTransformElement animate = new AnimateTransformElement()
                .type(AnimateTransformElement.Type.TRANSLATE);
        appendChild(animate);
        return animate;
    }

    /**
     * Creates a rotation animation and appends it to this group.
     * <p>
     * This animates the entire group and all its children.
     * Example usage:
     * <pre>{@code
     * group.animateRotate()
     *     .rotateFromTo(0, 360, 50, 50)  // rotate around center (50,50)
     *     .dur(Duration.ofSeconds(2))
     *     .beginElement();
     * }</pre>
     *
     * @return the animation element for further configuration
     */
    public AnimateTransformElement animateRotate() {
        AnimateTransformElement animate = new AnimateTransformElement()
                .type(AnimateTransformElement.Type.ROTATE);
        appendChild(animate);
        return animate;
    }

    /**
     * Creates a scale animation and appends it to this group.
     * <p>
     * This animates the entire group and all its children.
     * Example usage:
     * <pre>{@code
     * group.animateScale()
     *     .scaleFromTo(1, 2)  // scale from 1x to 2x
     *     .dur(Duration.ofSeconds(1))
     *     .beginElement();
     * }</pre>
     *
     * @return the animation element for further configuration
     */
    public AnimateTransformElement animateScale() {
        AnimateTransformElement animate = new AnimateTransformElement()
                .type(AnimateTransformElement.Type.SCALE);
        appendChild(animate);
        return animate;
    }
}
