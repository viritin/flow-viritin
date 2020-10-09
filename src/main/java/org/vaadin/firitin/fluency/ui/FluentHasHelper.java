package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasHelper;

public interface FluentHasHelper<S extends FluentHasHelper<S>> extends HasHelper {

    default S withHelperText(String helperText) {
        setHelperText(helperText);
        return (S) this;
    }

    default S withHelperComponent(Component component) {
        setHelperComponent(component);
        return (S) this;
    }
}
