package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.HasSize;

public interface FluentHasSize<S extends FluentHasSize<S> & HasSize> extends HasSize {

    default S withHeight(String height) {
        setHeight(height);
        return (S) this;
    }

    default S withSizeFull() {
        setSizeFull();
        return (S) this;
    }

    default S withSizeUndefined() {
        setSizeUndefined();
        return (S) this;
    }

    default S withWidth(String width) {
        setWidth(width);
        return (S) this;
    }
}
