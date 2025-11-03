package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.shared.HasSuffix;

public interface FluentHasPrefixAndSuffix<S extends FluentHasPrefixAndSuffix<S>> extends FluentHasPrefix<S>, HasSuffix {

    default S withSuffixComponent(Component component) {
        setSuffixComponent(component);
        return (S) this;
    }

}
