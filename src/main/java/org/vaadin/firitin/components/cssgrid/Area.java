package org.vaadin.firitin.components.cssgrid;

import java.util.UUID;

/**
 * Defines a named area. The same area can then be defined to exist
 * in multiple "cells" in the {@link CssGrid} and components can be assigned to
 * this area.
 *
 * @param name the name/id used for the area. Alternatively, use {@link #create()}
 *             that automatically generates a name for the area.
 */
public record Area(String name) {
    public static Area create() {
        return new Area(UUID.randomUUID().toString());
    }
}
