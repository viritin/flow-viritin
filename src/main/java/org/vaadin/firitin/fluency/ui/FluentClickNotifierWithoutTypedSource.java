package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.ComponentEventListener;

public interface FluentClickNotifierWithoutTypedSource<S extends FluentClickNotifierWithoutTypedSource<S>>
        extends ClickNotifier {

    @SuppressWarnings("unchecked")
    default S withClickListener(ComponentEventListener<ClickEvent<?>> listener) {
        // XXX This adds the listener in a fluent manner, but the Registration can't be
        // used to unregister the listener
        addClickListener(listener);
        return (S) this;
    }
}
