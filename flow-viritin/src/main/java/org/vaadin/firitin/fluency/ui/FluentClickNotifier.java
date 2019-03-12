package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;

public interface FluentClickNotifier<T extends Component, S extends FluentClickNotifier<T, S>>
        extends ClickNotifier<T> {

    @SuppressWarnings("unchecked")
    default S withClickListener(ComponentEventListener<ClickEvent<T>> listener) {
        // XXX This adds the listener in a fluent manner, but the Registration can't be
        // used to unregister the listener
        addClickListener(listener);
        return (S) this;
    }
}
