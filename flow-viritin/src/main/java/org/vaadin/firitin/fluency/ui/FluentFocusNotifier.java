package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.FocusNotifier;

public interface FluentFocusNotifier<T extends Component, S extends FluentBlurNotifier<T, S>> extends FocusNotifier<T> {

    @SuppressWarnings("unchecked")
    default S withFocusListener(ComponentEventListener<FocusEvent<T>> listener) {
        addFocusListener(listener);
        return (S) this;
    }
}
