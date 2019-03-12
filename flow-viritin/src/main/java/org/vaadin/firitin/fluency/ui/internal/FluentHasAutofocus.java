package org.vaadin.firitin.fluency.ui.internal;

import com.vaadin.flow.component.HasElement;

@SuppressWarnings("unchecked")
public interface FluentHasAutofocus<S extends FluentHasAutofocus<S>> extends HasElement {
    void setAutofocus(boolean autofocus);

    boolean isAutofocus();

    default S withAutofocus(boolean autofocus) {
        setAutofocus(autofocus);
        return (S) this;
    }
}
