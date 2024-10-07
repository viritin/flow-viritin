package org.vaadin.firitin.rad;

import com.vaadin.flow.component.Component;

/**
 * Experimental feature, API/naming might change.
 */
public class PrettyPrinter {

    public static Component toVaadin(Object value) {
        return new DtoDisplay(value);
    }

}
