package org.vaadin.firitin.rad.datastructures.ll;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PlugType {
    DISC, SQUARE, ARROW1, ARROW2, ARROW3, BEHIND;

    @JsonValue
    public String json() {
        return name().toLowerCase();
    }
}
