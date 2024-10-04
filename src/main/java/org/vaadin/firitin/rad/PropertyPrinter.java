package org.vaadin.firitin.rad;

import com.vaadin.flow.component.Component;

import static org.vaadin.firitin.rad.DtoDisplay.deCamelCased;

/**
 * Experimental, not yet stable API.
 * <p>
 * TODO, figure out if this could be useful even without Vaadin dependencies.
 * </p>
 */
public interface PropertyPrinter {
    /**
     * Prints the value of the property.
     *
     * @param ctx the value context
     * @return the component representing the property value or null if this printer does not know how to print the value
     */
    Component printValue(ValueContext ctx);

    default String getPropertyHeader(ValueContext ctx) {
        String propertyName = ctx.getProperty().getName();
        String deCamelCased = deCamelCased(propertyName);
        return deCamelCased + ":";
    }

}
