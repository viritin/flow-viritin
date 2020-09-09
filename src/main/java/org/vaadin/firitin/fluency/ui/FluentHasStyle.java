package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.HasStyle;

@SuppressWarnings("unchecked")
public interface FluentHasStyle<S extends FluentHasStyle<S>> extends HasStyle {

    /**
     * Sets the CSS class names of this component.This method overwrites any
     * previous set class names.
     *
     * @param className a space-separated string of class names to set, or
     *                  <code>null</code> to remove all class names
     * @return the configured component
     */
    default S withClassName(String className) {
        addClassName(className);
        return (S) this;
    }

    /**
     * Adds one or more CSS class names to this component.Multiple class names can
     * be specified by using multiple parameters.
     *
     * @param classNames the CSS class name or class names to be added to the component
     * @return the configured component
     */
    default S withAddedClassName(String... classNames) {
        for (String className : classNames) {
            addClassName(className);
        }
        return (S) this;
    }


    default S withStyle(String name, String value) {
        getStyle().set(name, value);
        return (S) this;
    }
}
