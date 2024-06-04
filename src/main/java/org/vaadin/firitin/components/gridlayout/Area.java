package org.vaadin.firitin.components.gridlayout;

import java.util.UUID;

public record Area(String name) {
    public static Area create() {
        return new Area(UUID.randomUUID().toString());
    }
}
