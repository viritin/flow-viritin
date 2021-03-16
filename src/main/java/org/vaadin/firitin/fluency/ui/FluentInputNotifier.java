package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.InputEvent;
import com.vaadin.flow.component.InputNotifier;

public interface FluentInputNotifier<S extends InputNotifier> extends InputNotifier {

    default S withInputListener(ComponentEventListener<InputEvent> listener) {
        addInputListener(listener);
        return (S) this;
    }
}
