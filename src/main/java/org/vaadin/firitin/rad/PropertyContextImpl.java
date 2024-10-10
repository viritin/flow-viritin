package org.vaadin.firitin.rad;

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

public record PropertyContextImpl(ValueContext owner,
                                  BeanPropertyDefinition beanPropertyDefinition) implements PropertyContext {
}
