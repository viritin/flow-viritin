package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.HasEnabled;

public interface FluentHasEnabled<S extends FluentHasEnabled<S> & HasEnabled> extends HasEnabled {

    @SuppressWarnings("unchecked")
    default S withEnabled(boolean enabled) {
        setEnabled(enabled);
        return (S) this;
    }
}
