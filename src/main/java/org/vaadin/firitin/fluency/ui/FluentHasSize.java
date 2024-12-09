package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.shared.Registration;
import org.vaadin.firitin.util.ResizeObserver;

@SuppressWarnings("unchecked")
public interface FluentHasSize<S extends FluentHasSize<S>> extends HasSize {

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

    /**
     * Adds a listener that is notified when the size of the component changes on the client side. The listener is also
     * notified when the dimensions of the component become available for the first time.
     *
     * @param listener the listener to add
     * @return a registration object for removing the listener
     * @deprecated Hoping to get this feature to the core soon, there might be API conflicts at that point.
     * Use the lower level {@link ResizeObserver} directly if you are concerned about potential future API conflicts.
     */
    @Deprecated
    default Registration addResizeListener(ComponentEventListener<ResizeObserver.SizeChangeEvent> listener) {
        return ResizeObserver.get().addResizeListener((Component) this, listener);
    };

    // Javadoc copied form Vaadin Framework

    /**
     * Sets the width to 100%.
     *
     * @return this (for method chaining)
     */
    default S withFullWidth() {
        return withWidth("100%");
    }

    // Javadoc copied form Vaadin Framework

    /**
     * Sets the height to 100%.
     *
     * @return this (for method chaining)
     */
    default S withFullHeight() {
        return withHeight("100%");
    }

    default S withSize(String width, String height) {
        setWidth(width);
        setHeight(height);
        return (S) this;
    }

    default S withMinWidth(String minWidth) {
        setMinWidth(minWidth);
        return (S) this;
    }

    default S withMaxWidth(String maxWidth) {
        setMaxWidth(maxWidth);
        return (S) this;
    }

    default S withMinHeight(String minHeight) {
        setMinHeight(minHeight);
        return (S) this;
    }

    default S withMaxHeight(String maxHeight) {
        setMaxHeight(maxHeight);
        return (S) this;
    }

    default S withMinSize(String width, String height) {
        setMinWidth(width);
        setMinHeight(height);
        return (S) this;
    }

    default S withMaxSize(String width, String height) {
        setMaxWidth(width);
        setMaxWidth(height);
        return (S) this;
    }
}
