package org.vaadin.firitin.util.style;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;

/**
 * Interface for CSS property enums providing shared default methods
 * for working with CSS custom properties (design tokens).
 * <p>
 * Each implementing enum only needs to provide {@link #cssPrefix()}
 * returning its CSS variable prefix (e.g. {@code "--aura-"}).
 * </p>
 */
public interface CssPropertyEnum {

    /** Provided by the enum itself. */
    String name();

    /** Returns the CSS variable prefix, e.g. {@code "--aura-"}. */
    String cssPrefix();

    /** Returns the full CSS custom property name, e.g. {@code "--aura-base-color"}. */
    default String getCssName() {
        return cssPrefix() + name().toLowerCase().replace('_', '-');
    }

    /** Defines (sets) this CSS property on the given component's inline style. */
    default void define(Component scope, String value) {
        scope.getStyle().set(getCssName(), value);
    }

    /** Defines (sets) this CSS property globally on the document root element. */
    default void define(String value) {
        UI.getCurrent().getElement().executeJs("""
        window.document.documentElement.style.setProperty("%s", "%s"); """
                .formatted(getCssName(), value));
    }

    /** Returns a {@code var(--...)} reference to this CSS property. */
    default String var() {
        return "var(" + getCssName() + ")";
    }
}
