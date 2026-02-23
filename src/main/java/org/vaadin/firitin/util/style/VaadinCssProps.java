package org.vaadin.firitin.util.style;

/**
 * Enumeration of shared Vaadin CSS properties (base style properties)
 * with handy helper methods to define them in a component scope or
 * globally and to use the property as css variable.
 * <p>
 * These properties are shared between themes (Aura, Lumo) and
 * provide a common set of design tokens for colors, spacing, sizing,
 * and other styles.
 * </p>
 *
 * @see <a href="https://vaadin.com/docs/latest/styling/themes/base">Base Style Properties Reference</a>
 */
public enum VaadinCssProps implements CssPropertyEnum {

    // Text Color
    TEXT_COLOR,
    TEXT_COLOR_SECONDARY,
    TEXT_COLOR_DISABLED,

    // Border Color
    BORDER_COLOR,
    BORDER_COLOR_SECONDARY,

    // Background Color
    BACKGROUND_COLOR,
    BACKGROUND_CONTAINER,
    BACKGROUND_CONTAINER_STRONG,

    // Gap
    GAP_XS,
    GAP_S,
    GAP_M,
    GAP_L,
    GAP_XL,

    // Padding
    PADDING_XS,
    PADDING_S,
    PADDING_M,
    PADDING_L,
    PADDING_XL,
    PADDING_INLINE_CONTAINER,
    PADDING_BLOCK_CONTAINER,

    // Radius
    RADIUS_S,
    RADIUS_M,
    RADIUS_L,

    // Focus Ring
    FOCUS_RING_COLOR,
    FOCUS_RING_WIDTH,

    // Cursor
    CLICKABLE_CURSOR,
    DISABLED_CURSOR,

    // Icons
    ICON_SIZE,
    ICON_VISUAL_SIZE,
    ICON_STROKE_WIDTH;

    @Override
    public String cssPrefix() {
        return "--vaadin-";
    }
}
