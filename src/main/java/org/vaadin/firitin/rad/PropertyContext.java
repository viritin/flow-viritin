package org.vaadin.firitin.rad;

import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import java.util.Locale;

public interface PropertyContext {

    ValueContext owner();

    BeanPropertyDefinition beanPropertyDefinition();

    default Object getPropertyValue() {
        Object value;
        Object ownerValue = owner().value();
        if (beanPropertyDefinition().hasGetter()) {
            AnnotatedMethod getter = beanPropertyDefinition().getGetter();
            value = getter.getValue(ownerValue);
        } else {
            value = beanPropertyDefinition().getAccessor().getValue(ownerValue);
        }
        return value;
    };

    default String getName() {
        return beanPropertyDefinition().getName();
    }

    default String shortToString() {
        return DtoDisplay.toShortString(getPropertyValue());
    }

    default Locale getLocale() {
        return owner().getLocale();
    };

    default ValueContext asValueContext() {
        return new ValueContextImpl(getPrettyPrinter(), getPropertyValue());
    };

    default int getLevel() {
        return owner().getLevel() + 1;
    };

    default PrettyPrinter getPrettyPrinter() {
        return owner().getPrettyPrinter();
    };
}
