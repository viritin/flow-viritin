package org.vaadin.firitin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TestTheme {
    Class<?> value();
}
