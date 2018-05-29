package org.vaadin.firitin.fluency.ui.internal;

import com.vaadin.flow.component.HasElement;

@SuppressWarnings("unchecked")
public interface FluentHasLabel<S extends FluentHasLabel<S>> extends HasElement {
    void setLabel(String label);

    String getLabel();

    default S withLabel(String label) {
        setLabel(label);
        return (S) this;
    }
}
