package org.vaadin.firitin.rad;

import com.vaadin.flow.component.Component;

/**
 * Experimental, not yet stable API.
 * <p>
 * TODO, figure out if this could be useful even without Vaadin dependencies.
 * </p>
 */
public interface PropertyPrinter {
    /**
     * Prints the value of the property. The value can be simple string or a more complex Vaadin {@link Component}.
     *
     * @param ctx the value context
     * @return the component representing the property value or null if this printer does not know how to print the value
     */
    Object printValue(ValueContext ctx);

    default Object getPropertyHeader(ValueContext ctx) {
        return null;
    }

}
