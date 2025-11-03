package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.shared.HasPrefix;

public interface FluentHasPrefix<S extends FluentHasPrefix<S>> extends HasPrefix {

    default S withPrefixComponent(Component component) {
        setPrefixComponent(component);
        return (S) this;
    }

}
