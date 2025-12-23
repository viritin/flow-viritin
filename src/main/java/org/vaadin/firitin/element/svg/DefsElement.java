package org.vaadin.firitin.element.svg;

/**
 * A typed Java API for the SVG {@code <defs>} element.
 * <p>
 * The {@code <defs>} element is used to store graphical objects that will be
 * used at a later time. Objects created inside a {@code <defs>} element are
 * not rendered directly. To display them, you have to reference them using
 * a {@code <use>} element or reference them from presentation attributes
 * (e.g., as a gradient fill).
 * </p>
 * <p>
 * Common uses include:
 * <ul>
 *   <li>Gradient definitions</li>
 *   <li>Pattern definitions</li>
 *   <li>Clip paths</li>
 *   <li>Masks</li>
 *   <li>Reusable symbols</li>
 * </ul>
 * </p>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/defs">MDN: defs element</a>
 */
public class DefsElement extends SvgElement {

    public DefsElement() {
        super("defs");
    }

    /**
     * Adds definition elements to this defs container.
     *
     * @param definitions the elements to add as definitions
     * @return this element for method chaining
     */
    public DefsElement add(SvgElement... definitions) {
        appendChild(definitions);
        return this;
    }
}
