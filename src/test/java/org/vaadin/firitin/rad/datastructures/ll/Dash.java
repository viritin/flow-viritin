package org.vaadin.firitin.rad.datastructures.ll;

public record Dash(boolean enabled, Double animation) {

    public Dash(boolean enabled) {
        this(enabled, null);
    }

}