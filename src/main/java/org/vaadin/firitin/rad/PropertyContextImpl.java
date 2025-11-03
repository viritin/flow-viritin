package org.vaadin.firitin.rad;


import tools.jackson.databind.introspect.BeanPropertyDefinition;

public record PropertyContextImpl(ValueContext owner,
                                  BeanPropertyDefinition beanPropertyDefinition) implements PropertyContext {
}
