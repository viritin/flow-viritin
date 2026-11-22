package org.vaadin.firitin.appframework.menuaccess;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** A view the test user may not open: {@link MenuAccessLayout#checkAccess} denies it. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Secret {
}
