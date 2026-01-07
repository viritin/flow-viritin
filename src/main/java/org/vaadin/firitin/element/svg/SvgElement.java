package org.vaadin.firitin.element.svg;

import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementUtil;
import com.vaadin.flow.dom.impl.CustomAttribute;
import com.vaadin.flow.internal.StateNode;
import org.jsoup.nodes.Document;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.vaadin.firitin.util.VStyle;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * A class for root SVG elements and a superclass for svg elements.
 * <p>
 * A hacky workaround around over-engineered Element "feature" that prevents
 * its usage with Svg and MathML elements. E.g.  Can be used to build components that
 * utilise SVG DOM with Element API.
 * </p>
 * <h2>Write-Only vs Read-Write Attributes</h2>
 * <p>
 * This SVG API provides two variants for setting attribute values:
 * </p>
 * <ul>
 *   <li><strong>Default methods</strong> (e.g., {@code x1()}, {@code fill()}) - These use an
 *       optimized write-only approach where attribute values are batched and sent to the client
 *       via JavaScript execution. This is more efficient for typical SVG usage where attributes
 *       are set but rarely read back. The attribute values are NOT stored on the server side.</li>
 *   <li><strong>RW (Read-Write) methods</strong> (e.g., {@code x1RW()}, {@code fillRW()}) - These
 *       use the traditional {@link #setAttribute(String, String)} approach where values are stored
 *       on the server and can be retrieved via {@link #getAttribute(String)}. Use these when you
 *       need to read attribute values back in your Java code.</li>
 * </ul>
 * <p>
 * The write-only optimization batches multiple attribute changes and sends them in a single
 * JavaScript call just before the response is sent to the client. Multiple changes to the same
 * attribute within a request-response cycle are coalesced, sending only the final value.
 * </p>
 * <p>
 * <strong>Important:</strong> Write-only attributes will be lost if the element is removed from
 * the DOM and later re-attached. Since the values are not stored on the server, they cannot be
 * restored when the element is re-added. Use the RW (Read-Write) variants if your application
 * needs to detach and re-attach SVG elements while preserving their attribute values.
 * </p>
 */
public class SvgElement extends Element {

    private Map<String, String> pendingAttributes;
    private boolean beforeClientResponseScheduled = false;

    /**
     * Gets an attribute value, checking pending write-only attributes first.
     * <p>
     * This is useful when you need to read an attribute that may have been set
     * via {@link #setWriteOnlyAttribute(String, String)} earlier in the same
     * request-response cycle.
     * </p>
     *
     * @param attribute the attribute name
     * @return the pending value if set, otherwise the stored attribute value
     */
    protected String getPendingOrAttribute(String attribute) {
        if (pendingAttributes != null && pendingAttributes.containsKey(attribute)) {
            return pendingAttributes.get(attribute);
        }
        return getAttribute(attribute);
    }

    public static SvgElement emptySvgRoot() {
        SvgElement svg = new SvgElement("svg");
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
     * Sets the viewBox attribute, which defines the position and dimension of the SVG viewport.
     *
     * @param minX   the x-coordinate of the viewBox origin
     * @param minY   the y-coordinate of the viewBox origin
     * @param width  the width of the viewBox
     * @param height the height of the viewBox
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgElement> T viewBox(double minX, double minY, double width, double height) {
        setAttribute("viewBox", "%s %s %s %s".formatted(minX, minY, width, height));
        return (T) this;
    }

    /**
     * Sets the width of the SVG element.
     *
     * @param width the width value
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgElement> T width(double width) {
        setAttribute("width", String.valueOf(width));
        return (T) this;
    }

    /**
     * Sets the width of the SVG element with a unit.
     *
     * @param width the width with unit (e.g., "100%", "200px")
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgElement> T width(String width) {
        setAttribute("width", width);
        return (T) this;
    }

    /**
     * Sets the height of the SVG element.
     *
     * @param height the height value
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgElement> T height(double height) {
        setAttribute("height", String.valueOf(height));
        return (T) this;
    }

    /**
     * Sets the height of the SVG element with a unit.
     *
     * @param height the height with unit (e.g., "100%", "200px")
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgElement> T height(String height) {
        setAttribute("height", height);
        return (T) this;
    }

    /**
     * Sets the size of the SVG element.
     *
     * @param width  the width value
     * @param height the height value
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgElement> T size(double width, double height) {
        width(width);
        height(height);
        return (T) this;
    }

    /**
     * Sets the size of the SVG element with units.
     *
     * @param width  the width with unit (e.g., "100%", "200px")
     * @param height the height with unit (e.g., "100%", "200px")
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgElement> T size(String width, String height) {
        width(width);
        height(height);
        return (T) this;
    }

    /**
     * Preserve aspect ratio options for the SVG element.
     */
    public enum PreserveAspectRatio {
        /** Do not force uniform scaling */
        NONE("none"),
        /** Scale to fit, centered, preserve aspect ratio (default) */
        XMID_YMID_MEET("xMidYMid meet"),
        /** Scale to fill, centered, preserve aspect ratio (may crop) */
        XMID_YMID_SLICE("xMidYMid slice"),
        /** Scale to fit, aligned to top-left */
        XMIN_YMIN_MEET("xMinYMin meet"),
        /** Scale to fill, aligned to top-left (may crop) */
        XMIN_YMIN_SLICE("xMinYMin slice"),
        /** Scale to fit, aligned to top-center */
        XMID_YMIN_MEET("xMidYMin meet"),
        /** Scale to fit, aligned to top-right */
        XMAX_YMIN_MEET("xMaxYMin meet"),
        /** Scale to fit, aligned to middle-left */
        XMIN_YMID_MEET("xMinYMid meet"),
        /** Scale to fit, aligned to middle-right */
        XMAX_YMID_MEET("xMaxYMid meet"),
        /** Scale to fit, aligned to bottom-left */
        XMIN_YMAX_MEET("xMinYMax meet"),
        /** Scale to fit, aligned to bottom-center */
        XMID_YMAX_MEET("xMidYMax meet"),
        /** Scale to fit, aligned to bottom-right */
        XMAX_YMAX_MEET("xMaxYMax meet");

        private final String value;

        PreserveAspectRatio(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Sets the preserveAspectRatio attribute.
     *
     * @param ratio the preserve aspect ratio setting
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgElement> T preserveAspectRatio(PreserveAspectRatio ratio) {
        setAttribute("preserveAspectRatio", ratio.toString());
        return (T) this;
    }

    /**
     * Sets the preserveAspectRatio attribute with a custom value.
     *
     * @param ratio the preserve aspect ratio value
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends SvgElement> T preserveAspectRatio(String ratio) {
        setAttribute("preserveAspectRatio", ratio);
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

    /**
     * Sets an attribute using write-only optimization.
     * <p>
     * Unlike {@link #setAttribute(String, String)}, this method does NOT store the attribute
     * value on the server side. Instead, it batches attribute changes and sends them to the
     * client via JavaScript execution just before the response is sent. This is more efficient
     * for typical SVG usage where attributes are written once and never read back.
     * </p>
     * <p>
     * Multiple changes to the same attribute within a single request-response cycle are
     * coalesced, with only the final value being sent to the client.
     * </p>
     *
     * @param attribute the attribute name
     * @param value the attribute value
     * @return this element for method chaining
     */
    protected Element setWriteOnlyAttribute(String attribute, String value) {
        validateAttribute(attribute, value);
        if (pendingAttributes == null) {
            pendingAttributes = new LinkedHashMap<>();
        }
        pendingAttributes.put(attribute, value);
        scheduleBeforeClientResponse();
        return this;
    }

    protected void scheduleBeforeClientResponse() {
        if (!beforeClientResponseScheduled) {
            beforeClientResponseScheduled = true;
            StateNode node = getNode();
            node.runWhenAttached(ui -> {
                ui.getInternals().getStateTree().beforeClientResponse(node, ctx -> {
                    flushPendingAttributes();
                });
            });
        }
    }

    protected void flushPendingAttributes() {
        beforeClientResponseScheduled = false;
        if (pendingAttributes == null || pendingAttributes.isEmpty()) {
            return;
        }

        // Build a JavaScript call that sets all pending attributes
        StringBuilder js = new StringBuilder();
        js.append("const el=$0;");
        for (Map.Entry<String, String> entry : pendingAttributes.entrySet()) {
            String attrName = entry.getKey();
            String attrValue = entry.getValue();
            // Use setAttributeNS with null namespace for SVG attributes
            js.append("el.setAttributeNS(null,");
            js.append(escapeJsString(attrName));
            js.append(",");
            js.append(escapeJsString(attrValue));
            js.append(");");
        }
        executeJs(js.toString());
        pendingAttributes.clear();
    }

    private static String escapeJsString(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
            }
        }
        sb.append("\"");
        return sb.toString();
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
