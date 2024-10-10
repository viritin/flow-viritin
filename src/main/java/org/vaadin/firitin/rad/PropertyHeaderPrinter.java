package org.vaadin.firitin.rad;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;

import static org.vaadin.firitin.rad.DtoDisplay.deCamelCased;

/**
 * Experimental, not yet stable API.
 * <p>
 * TODO, figure out if this could be useful even without Vaadin dependencies.
 * </p>
 */
public interface PropertyHeaderPrinter {
    /**
     * Prints the header of the property. The header can be simple string or a more complex component.
     *
     * @param ctx the value context
     * @return the component representing the property header or null if this printer does not know how to print the value
     */
    Object printHeader(PropertyContext ctx);

    static String defaultHeader(PropertyContext ctx) {
        String propertyName = ctx.getName();
        String deCamelCased = deCamelCased(propertyName);
        return deCamelCased + ":";
    }

}
