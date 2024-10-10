package org.vaadin.firitin.rad;

import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface ValueContext {
    Object value();

    ValueContext parent();

    BasicBeanDescription beanDescription();

    Locale getLocale();

    PrettyPrinter getPrettyPrinter();

    PropertyContext getPropertyContext(BeanPropertyDefinition property);

    int getLevel();

    default String toShortString() {
        if (value() == null) {
            return "null";
        }
        List<BeanPropertyDefinition> properties = beanDescription().findProperties();
        if (properties.isEmpty()) {
            return DtoDisplay.toShortString(value());
        } else {
            Optional<BeanPropertyDefinition> name = properties.stream().filter(p -> p.getName().equals("name")).findFirst();
            if (name.isPresent()) {
                return DtoDisplay.toShortString(getPropertyContext(name.get()).getPropertyValue());
            }
        }
        return DtoDisplay.toShortString(value());
    }
}
