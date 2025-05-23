package org.vaadin.firitin.util;

import com.vaadin.flow.dom.Style;
import in.virit.color.Color;

import java.util.stream.Stream;

public class VStyle implements Style {

    private final Style wrapped;

    public VStyle(Style wrapped) {
        this.wrapped = wrapped;
    }

    public VStyle setColor(Color color) {
        return this.set("color", color.toString());
    }

    public Color getColor() {
        String color = wrapped.get("color");
        if (color == null) {
            return null;
        }
        return Color.parseCssColor(color);
    }

    public VStyle setBackgroundColor(Color color) {
        return this.set("background-color", color.toString());
    }

    public Color getBackgroundColor() {
        String color = wrapped.get("background-color");
        if (color == null) {
            return null;
        }
        return Color.parseCssColor(color);
    }

    // Piggyback for the original Style

    public static VStyle wrap(Style original) {
        return new VStyle(original);
    }

    @Override
    public String get(String name) {
        return wrapped.get(name);
    }

    @Override
    public VStyle set(String name, String value) {
        wrapped.set(name, value);
        return this;
    }

    @Override
    public VStyle remove(String name) {
        wrapped.remove(name);
        return this;
    }

    @Override
    public VStyle clear() {
        wrapped.clear();
        return this;
    }

    @Override
    public boolean has(String name) {
        return wrapped.has(name);
    }

    @Override
    public Stream<String> getNames() {
        return wrapped.getNames();
    }
}
