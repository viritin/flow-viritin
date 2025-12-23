package org.vaadin.firitin.element.svg;

import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementUtil;
import com.vaadin.flow.dom.impl.CustomAttribute;
import org.jsoup.nodes.Document;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.vaadin.firitin.util.VStyle;

import java.util.Locale;
import java.util.Optional;

/**
 * A class for root SVG elements and a superclass for svg elements.
 * <p>
 * A hacky workaround around over-engineered Element "feature" that prevents
 * its usage with Svg and MathML elements. E.g.  Can be used to build components that
 * utilise SVG DOM with Element API.
 * </p>
 */
public class SvgElement extends Element {

    public static SvgElement emptySvgRoot() {
        SvgElement svg = new SvgElement("svg");
        svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");
        svg.setAttribute("version", "1.1");
        return svg;
    }

    public SvgElement(String tag) {
        super(tag);
    }

    /**
     * Sets the ID of this element.
     * <p>
     * The ID can be used to reference this element from other elements,
     * such as {@code <use>} elements or gradient/pattern fills.
     * </p>
     *
     * @param id the ID for this element
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgElement> T id(String id) {
        setAttribute("id", id);
        return (T) this;
    }

    /**
     * Creates an SVG root element with the specified viewBox dimensions.
     *
     * @param minX    The minimum x-coordinate of the viewBox.
     * @param minY    The minimum y-coordinate of the viewBox.
     * @param width   The width of the viewBox.
     * @param height  The height of the viewBox.
     */
    public SvgElement(int minX, int minY, int width, int height) {
        super("svg");
        setAttribute("xmlns", "http://www.w3.org/2000/svg");
        setAttribute("version", "1.1");
        setAttribute("viewBox", "%s %s %s %s".formatted(minX, minY, width, height));
    }

    @Override
    public Element setAttribute(String attribute, String value) {
        String lowerCasedAttribute = validateAttribute(attribute, value);

        Optional<CustomAttribute> customAttribute = CustomAttribute
                .get(lowerCasedAttribute);
        if (customAttribute.isPresent()) {
            customAttribute.get().setAttribute(this, value);
        } else {
            // ignore the lowercased attribute as it really doesn't matter and breaks SVG
            getStateProvider().setAttribute(getNode(), attribute,
                    value);
        }
        return this;
    }

    private String validateAttribute(String attribute, Object value) {
        if (attribute == null) {
            throw new IllegalArgumentException("Attribute name cannot be null");
        }

        String lowerCaseAttribute = attribute.toLowerCase(Locale.ENGLISH);
        if (!ElementUtil.isValidAttributeName(lowerCaseAttribute)) {
            throw new IllegalArgumentException(String.format(
                    "Attribute \"%s\" is not a valid attribute name",
                    lowerCaseAttribute));
        }

        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        return lowerCaseAttribute;
    }

    @Override
    public String getOuterHTML() {
        // TODO figure out if recycling via JSOUP is needed at all, probably overdesign and
        // excess validation with regular HTML components as well
        // Parser.htmlParser()
        //        .settings(ParseSettings.preserveCase)
        Document document = new Document(Parser.NamespaceSvg, "");
        document.parser().settings(ParseSettings.preserveCase);
        return ElementUtil.toJsoup(document, this).outerHtml();
    }

    @Override
    public VStyle getStyle() {
        return VStyle.wrap(super.getStyle());
    }

}
