package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.HasSize;

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

    // Javadoc copied form Vaadin Framework

    /**
     * Clears any size settings.
     *
     * @return this (for method chaining)
     * @see #setSizeUndefined()
     */
    public default S withUndefinedSize() {
        setSizeUndefined();
        return (S) this;
    }

    default S withSize(String width, String height) {
        setWidth(width);
        setHeight(height);
        return (S) this;
    }
}
