package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.DetachNotifier;

@SuppressWarnings("unchecked")
public interface FluentDetachNotifier<S extends FluentDetachNotifier<S>> extends DetachNotifier {

    default S withDetachListener(ComponentEventListener<DetachEvent> listener) {
        addDetachListener(listener);
        return (S) this;
    }
}
