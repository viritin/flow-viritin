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
}
