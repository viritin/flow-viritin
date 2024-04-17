package org.vaadin.firitin.util.style;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;

/**
 * Enumeration of Lumo CSS properties with handy helper methods
 * to define them in a component scope or globally and to use
 * the property as css variable.
 * <p>
 * Consider this class still to be in experimental phase.
 * </p>
 */
public enum LumoProps {

    // Lumo Color
    BASE_COLOR,
    CONTRAST_5PCT,
    CONTRAST_10PCT,
    CONTRAST_20PCT,
    CONTRAST_30PCT,
    CONTRAST_40PCT,
    CONTRAST_50PCT,
    CONTRAST_60PCT,
    CONTRAST_70PCT,
    CONTRAST_80PCT,
    CONTRAST_90PCT,
    CONTRAST,
    PRIMARY_COLOR,
    HEADER_TEXT_COLOR,
    BODY_TEXT_COLOR,
    SECONDARY_TEXT_COLOR,
    TERTIARY_TEXT_COLOR,
    DISABLED_TEXT_COLOR,
    PRIMARY_COLOR_10PCT,
    PRIMARY_COLOR_50PCT,
    PRIMARY_TEXT_COLOR,
    PRIMARY_CONTRAST_COLOR,
    ERROR_COLOR,
    ERROR_COLOR_10PCT,
    ERROR_COLOR_50PCT,
    ERROR_TEXT_COLOR,
    ERROR_CONTRAST_COLOR,
    WARNING_COLOR,
    WARNING_COLOR_10PCT,
    WARNING_TEXT_COLOR,
    WARNING_CONTRAST_COLOR,
    SUCCESS_COLOR,
    SUCCESS_COLOR_10PCT,
    SUCCESS_COLOR_50PCT,
    SUCCESS_TEXT_COLOR,
    SUCCESS_CONTRAST_COLOR,

    // Lumo Typography
    FONT_FAMILY,
    FONT_SIZE_XXXL,
    FONT_SIZE_XXL,
    FONT_SIZE_XL,
    FONT_SIZE_L,
    FONT_SIZE_M,
    FONT_SIZE_S,
    FONT_SIZE_XS,
    FONT_SIZE_XXS,
    LINE_HEIGHT_M,
    LINE_HEIGHT_S,
    LINE_HEIGHT_XS,

    // Lumo Size and Shape
    SIZE_XL,
    SIZE_L,
    SIZE_M,
    SIZE_S,
    SIZE_XS,
    ICON_SIZE_L,
    ICON_SIZE_M,
    ICON_SIZE_S,
    SPACE_XL,
    SPACE_L,
    SPACE_M,
    SPACE_S,
    SPACE_XS,

    // Lumo Shape

    BORDER_RADIUS_L,
    BORDER_RADIUS_M,
    BORDER_RADIUS_S,

    // Lumo Elevation
    BOX_SHADOW_XL,
    BOX_SHADOW_L,
    BOX_SHADOW_M,
    BOX_SHADOW_S,
    BOX_SHADOW_XS,

    // Lumo Interaction
    CLICKABLE_CURSOR;

    private static final String PREFIX = "--lumo-";

    public String getCssName() {
        return PREFIX + name().toLowerCase().replace('_', '-');
    }

    public void define(Component scope, String value) {
        scope.getStyle().set(getCssName(), value);
    }

    public void define(String value) {
        UI.getCurrent().getElement().executeJs("""
        window.document.documentElement.style.setProperty("%s", "%s"); """
                .formatted(getCssName(), value));
    }

    public String var() {
        return "var(" + getCssName() + ")";
    }
}
