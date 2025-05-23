package org.vaadin.firitin.util;

import com.fasterxml.jackson.annotation.JsonValue;
import com.vaadin.flow.dom.Style;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * A simple implementation of the Style interface using a TreeMap. Can be applied
 * on then with JS on the client side.
 */
class TreeMapStyle implements Style {
    TreeMap<String, String> styleRules = new TreeMap<>();

    @Override
    public String get(String name) {
        return styleRules.get(name);
    }

    @Override
    public Style set(String name, String value) {
        styleRules.put(name, value);
        return this;
    }

    @Override
    public Style remove(String name) {
        styleRules.remove(name);
        return this;
    }

    @Override
    public Style clear() {
        styleRules.clear();
        return this;
    }

    @Override
    public boolean has(String name) {
        return styleRules.containsKey(name);
    }

    @Override
    public Stream<String> getNames() {
        return styleRules.keySet().stream();
    }

    @JsonValue
    public Map getMap() {
        return styleRules;
    }

}
