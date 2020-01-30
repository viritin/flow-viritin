package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.HasPrefixAndSuffix;

public interface FluentHasPrefixAndSuffix<S extends FluentHasPrefixAndSuffix<S>> extends HasPrefixAndSuffix {

    default S withPrefixComponent(Component component) {
        setPrefixComponent(component);
        return (S) this;
    }

    default S withSuffixComponent(Component component) {
        setSuffixComponent(component);
        return (S) this;
    }

}
