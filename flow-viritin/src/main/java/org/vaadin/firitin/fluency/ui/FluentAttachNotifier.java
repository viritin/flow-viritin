package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.AttachNotifier;
import com.vaadin.flow.component.ComponentEventListener;

@SuppressWarnings("unchecked")
public interface FluentAttachNotifier<S extends FluentAttachNotifier<S>> extends AttachNotifier {

    default S withAttachListener(ComponentEventListener<AttachEvent> listener) {
        addAttachListener(listener);
        return (S) this;
    }
}
