package org.vaadin.firitin.util;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Digs the property name out of a method reference to a getter.
 * <p>
 * Works with any serializable lambda, so also with plain Vaadin functional
 * interfaces like {@link com.vaadin.flow.function.ValueProvider}, not only with
 * {@link PropertyRef}. The JVM generates a {@code writeReplace} method for
 * serializable lambdas, which returns a {@link SerializedLambda} describing the
 * implementation method. Both javac and the Eclipse compiler produce it, so this
 * is not a compiler specific trick.
 * </p>
 */
public final class PropertyRefs {

    private static final Map<Class<?>, String> CACHE = new ConcurrentHashMap<>();

    private PropertyRefs() {
    }

    /**
     * Resolves the property name from a method reference to a getter, e.g.
     * {@code Person::getFirstName} resolves to {@code firstName}.
     * <p>
     * The result is cached per method reference call site, so the reflection cost
     * is paid only once per {@code Foo::getBar} expression in the source code.
     * </p>
     *
     * @param getterReference the method reference
     * @return the property name
     * @throws IllegalArgumentException if the given lambda is not a method reference
     */
    public static String propertyName(Serializable getterReference) {
        return CACHE.computeIfAbsent(getterReference.getClass(),
                type -> resolvePropertyName(getterReference));
    }

    /**
     * Returns the {@link SerializedLambda} describing the given serializable lambda.
     *
     * @param lambda the lambda or method reference
     * @return the serialized form, with e.g. the implementation method name
     */
    public static SerializedLambda serializedLambda(Serializable lambda) {
        try {
            Method writeReplace = lambda.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            if (writeReplace.invoke(lambda) instanceof SerializedLambda serializedLambda) {
                return serializedLambda;
            }
            throw new IllegalArgumentException(
                    "The given object is not a lambda: " + lambda.getClass());
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("""
                    The given object is not a serializable lambda: %s. Note that the \
                    functional interface must extend Serializable for the property name \
                    to be resolvable.""".formatted(lambda.getClass()), e);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to introspect the method reference", e);
        }
    }

    /**
     * Maps a getter method name to a property name, following the JavaBeans naming
     * convention. Names that don't look like JavaBeans getters, like record
     * accessors, are returned as such.
     *
     * @param methodName the name of the getter method
     * @return the property name
     */
    public static String propertyNameFromGetterName(String methodName) {
        if (methodName.startsWith("get") && methodName.length() > 3) {
            return decapitalize(methodName.substring(3));
        }
        if (methodName.startsWith("is") && methodName.length() > 2) {
            return decapitalize(methodName.substring(2));
        }
        // Record accessors and "fluent" getters like person.name()
        return methodName;
    }

    private static String resolvePropertyName(Serializable getterReference) {
        SerializedLambda serializedLambda = serializedLambda(getterReference);
        String implMethodName = serializedLambda.getImplMethodName();
        if (implMethodName.startsWith("lambda$")) {
            throw new IllegalArgumentException("""
                    Can't resolve a property name from a lambda expression. Use a method \
                    reference to the getter instead, e.g. Person::getFirstName instead of \
                    person -> person.getFirstName().""");
        }
        return propertyNameFromGetterName(implMethodName);
    }

    /**
     * JavaBeans style decapitalization: leaves names starting with two upper case
     * characters alone, so that e.g. getURL maps to URL.
     */
    private static String decapitalize(String name) {
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1))) {
            return name;
        }
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
}
