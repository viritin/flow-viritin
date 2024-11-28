package org.vaadin.firitin.rad;

import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import java.util.Locale;

/**
 * ValueContext is a helper class to pass around the context of the value being displayed/printed.
 *
 * <p>Experimental, not yet stable API.</p>
 */
public record AutoFormValueContextImpl(AutoForm prettyPrinter, Class type, Object value, ValueContext parent, BasicBeanDescription beanDescription) implements ValueContext {
    public AutoFormValueContextImpl(AutoForm prettyPrinter, Object dto) {
        this(prettyPrinter,
                dto == null ? null : dto.getClass(),
                dto, null, PrettyPrinter.inrospect(dto));
    }

    @Override
    public Locale getLocale() {
        return prettyPrinter.getLocale();
    }

    @Override
    public PrettyPrinter getPrettyPrinter() {
        // TODO check!!
        return PrettyPrinter.getDefault();
    }

    @Override
    public PropertyContext getPropertyContext(BeanPropertyDefinition property) {
        return new PropertyContextImpl(this, property);
    }

    /**
     * @return the level of the property in the object graph
     */
    @Override
    public int getLevel() {
        int level = 0;
        ValueContext parent = parent();
        while (parent != null) {
            level++;
            parent = parent.parent();
        }
        return level;
    }
}
