package org.vaadin.firitin.util.style;

/**
 * Enumeration of Aura CSS properties with handy helper methods
 * to define them in a component scope or globally and to use
 * the property as css variable.
 * <p>
 * Consider this class still to be in experimental phase.
 * </p>
 *
 * @see <a href="https://vaadin.com/docs/latest/styling/themes/aura">Aura Theme Reference</a>
 */
public enum AuraProps implements CssPropertyEnum {

    // Aura Color - Color Scheme
    CONTENT_COLOR_SCHEME,
    NOTIFICATION_COLOR_SCHEME,

    // Aura Color - Background
    BACKGROUND_COLOR,
    BACKGROUND_COLOR_LIGHT,
    BACKGROUND_COLOR_DARK,
    APP_BACKGROUND,

    // Aura Color - Text & Border
    ACCENT_BORDER_COLOR,
    CONTRAST_LEVEL,

    // Aura Color - Palette (Neutral)
    NEUTRAL,
    NEUTRAL_LIGHT,
    NEUTRAL_DARK,

    // Aura Color - Palette (Saturated)
    RED,
    ORANGE,
    YELLOW,
    GREEN,
    BLUE,
    PURPLE,

    // Aura Color - Palette (Text)
    RED_TEXT,
    ORANGE_TEXT,
    YELLOW_TEXT,
    GREEN_TEXT,
    BLUE_TEXT,
    PURPLE_TEXT,

    // Aura Color - Accent
    ACCENT_COLOR,
    ACCENT_COLOR_LIGHT,
    ACCENT_COLOR_DARK,
    ACCENT_CONTRAST_COLOR,
    ACCENT_CONTRAST_COLOR_LIGHT,
    ACCENT_CONTRAST_COLOR_DARK,
    ACCENT_TEXT_COLOR,
    ACCENT_TEXT_COLOR_LIGHT,
    ACCENT_TEXT_COLOR_DARK,

    // Aura Color - Surface
    SURFACE_COLOR,
    SURFACE_COLOR_SOLID,
    SURFACE_LEVEL,
    SURFACE_OPACITY,
    OVERLAY_SURFACE_OPACITY,
    ACCENT_SURFACE,

    // Aura Typography - Font Family
    FONT_FAMILY,
    FONT_FAMILY_SYSTEM,
    FONT_FAMILY_INSTRUMENT_SANS,
    FONT_SMOOTHING,

    // Aura Typography - Font Size
    BASE_FONT_SIZE,
    FONT_SIZE_XS,
    FONT_SIZE_S,
    FONT_SIZE_M,
    FONT_SIZE_L,
    FONT_SIZE_XL,

    // Aura Typography - Line Height
    BASE_LINE_HEIGHT,
    LINE_HEIGHT_XS,
    LINE_HEIGHT_S,
    LINE_HEIGHT_M,
    LINE_HEIGHT_L,
    LINE_HEIGHT_XL,

    // Aura Typography - Font Weight
    FONT_WEIGHT_REGULAR,
    FONT_WEIGHT_MEDIUM,
    FONT_WEIGHT_SEMIBOLD,

    // Aura Size and Shape
    BASE_SIZE,
    BASE_RADIUS,

    // Aura Shadow & Overlay
    SHADOW_M,
    SHADOW_COLOR,
    OVERLAY_SHADOW,
    OVERLAY_OUTLINE_COLOR,
    OVERLAY_INNER_OUTLINE_COLOR,
    OVERLAY_BACKDROP_FILTER,

    // Aura App Layout
    APP_LAYOUT_INSET,
    APP_LAYOUT_BORDER_RADIUS,
    APP_LAYOUT_BORDER_WIDTH;

    @Override
    public String cssPrefix() {
        return "--aura-";
    }
}
