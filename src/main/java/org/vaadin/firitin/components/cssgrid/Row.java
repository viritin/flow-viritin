package org.vaadin.firitin.components.cssgrid;

import java.util.Arrays;

/**
 * Defines a row of defined areas.
 *
 * @param areas
 */
public record Row(Area... areas) {
    String toCss() {
        return "\"" + String.join(" ", Arrays.stream(areas()).map(Area::name).toList()) + "\"";
    }
}
