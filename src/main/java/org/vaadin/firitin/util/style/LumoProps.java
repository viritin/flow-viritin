package org.vaadin.firitin.util.style;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
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
    baseColor,
    //--lumo-contrast-5pct
    contrast5pct,
    contrast10pct,
    contrast20pct,
    contrast30pct,
    contrast40pct,
    contrast50pct,
    contrast60pct,
    contrast70pct,
    contrast80pct,
    contrast90pct,
    contrast,
    primaryColor,
    headerTextColor,
    bodyTextColor,
    secondaryTextColor,
    tertiaryTextColor,
    disabledTextColor,
    primaryColor10pct,
    primaryColor50pct,
    primaryTextColor,
    primaryContrastColor,
    errorColor,
    errorColor10pct,
    errorColor50pct,
    errorTextColor,
    errorContrastColor,
    warningColor,
    warningColor10pct,
    warningTextColor,
    warningContrastColor,
    successColor,
    successColor10pct,
    successColor50pct,
    successTextColor,
    successContrastColor,

    // Lumo Typography
    fontFamily,
    fontSizeXxxl,
    fontSizeXxl,
    fontSizeXl,
    fontSizeL,
    fontSizeM,
    fontSizeS,
    fontSizeXs,
    fontSizeXxs,
    lineHeightM,
    lineHeightS,
    lineHeightXS,

    // Lumo Size and Shape
    sizeXl,
    sizeL,
    sizeM,
    sizeS,
    sizeXs,
    iconSizeL,
    iconSizeM,
    iconSizeS,
    spaceXL,
    spaceL,
    spaceM,
    spaceS,
    spaceXs,

    // Lumo Shape

    borderRadiusL,
    borderRadiusM,
    borderRadiusS,

    // Lumo Elevantion
    boxShadowXl,
    boxShadowL,
    boxShadowM,
    boxShadowS,
    boxShadowXs,

    // Lumo Interaction

    clickableCursor

    ;

    private static final String PREFIX = "--lumo-";
    static PropertyNamingStrategy.KebabCaseStrategy kebab = new PropertyNamingStrategy.KebabCaseStrategy();

    public String getCssName() {
        return PREFIX + kebab.translate(name());
    }

    public void define(Component scope, String value) {
        scope.getStyle().set(getCssName(), value);
    }

    public void define(String value) {
        define(UI.getCurrent(), value);
    }

    public String var() {
        return "var(" + getCssName() + ")";
    }

}
