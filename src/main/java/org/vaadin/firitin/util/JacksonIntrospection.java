package org.vaadin.firitin.util;

import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Provides a shared {@link ObjectMapper} configured for bean introspection.
 * <p>
 * Jackson 3 changed the default for {@link MapperFeature#SORT_PROPERTIES_ALPHABETICALLY}
 * to enabled, which breaks auto-generated UIs that rely on declaration order.
 * This utility provides an ObjectMapper with that feature disabled.
 */
public class JacksonIntrospection {

    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .build();

    public static ObjectMapper getMapper() {
        return MAPPER;
    }
}
