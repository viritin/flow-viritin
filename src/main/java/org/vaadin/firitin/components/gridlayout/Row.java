package org.vaadin.firitin.components.gridlayout;

import java.util.Arrays;

public record Row(Area... areas) {
    String toCss() {
        return "\"" + String.join(" ", Arrays.stream(areas()).map(Area::name).toList()) + "\"";
    }
}
