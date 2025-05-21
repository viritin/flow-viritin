package org.vaadin.firitin.rad.datastructures.ll;

import java.util.List;

public class SocketGravity {
    private List<Object> values; // Each value is String or Number

    public SocketGravity(Object singleValue) {
        this.values = List.of(singleValue);
    }

    public SocketGravity(List<Object> values) {
        this.values = values;
    }

    public List<Object> getValues() {
        return values;
    }
}
