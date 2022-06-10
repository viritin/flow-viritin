package org.vaadin.firitin.fluency.ui.internal;

import com.vaadin.flow.component.HasLabel;

@SuppressWarnings("unchecked")
public interface FluentHasLabel<S extends FluentHasLabel<S>> extends HasLabel {
    default void setLabel(String label) {
    
    };

    default String getLabel() {
        return "";
    };
    

    default S withLabel(String label) {
        setLabel(label);
        return (S) this;
    }
}
