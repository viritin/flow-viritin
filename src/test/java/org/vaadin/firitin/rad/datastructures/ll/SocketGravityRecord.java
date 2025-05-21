package org.vaadin.firitin.rad.datastructures.ll;

import com.fasterxml.jackson.annotation.JsonValue;

public record SocketGravityRecord (int... values) {

    @JsonValue
    public Object json() {
        if (values.length == 0) {
            return null;
        }
        if (values.length == 1) {
            return values[0];
        }
        return values;
    }

}
