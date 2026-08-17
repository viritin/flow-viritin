package org.vaadin.firitin.util;

import com.vaadin.flow.function.ValueProvider;

/**
 * A reference to a bean property, expressed as a method reference to its getter,
 * instead of the property name as a String.
 * <p>
 * The main point is to get compile time safety and refactoring support for APIs
 * that traditionally take property names as strings:
 * </p>
 * <pre><code>
 * grid.setColumns(Person::getFirstName, Person::getLastName);
 * </code></pre>
 * <p>
 * The property name is resolved from the method reference at runtime, using the
 * {@link java.lang.invoke.SerializedLambda} that the JVM creates for serializable
 * lambdas (all Vaadin functional interfaces are serializable). The name resolution
 * is cached per method reference call site, see {@link PropertyRefs}.
 * </p>
 * <p>
 * Nested properties can be expressed by chaining:
 * </p>
 * <pre><code>
 * grid.setColumns(PropertyRef.of(Person::getAddress).then(Address::getStreet));
 * </code></pre>
 * <p>
 * Note that only actual method references work, a lambda expression like
 * {@code p -> p.getFirstName()} carries no property name and fails fast with an
 * {@link IllegalArgumentException}.
 * </p>
 *
 * @param <T> the bean type
 * @param <V> the property type
 */
@FunctionalInterface
public interface PropertyRef<T, V> extends ValueProvider<T, V> {

    /**
     * Resolves the name of the property this reference points to. A getter named
     * {@code getFoo}/{@code isFoo} maps to {@code foo}, other method names (like
     * record accessors) are used as such.
     *
     * @return the property name, potentially a dot separated path for nested
     *         references created with {@link #then(PropertyRef)}
     */
    default String getPropertyName() {
        return PropertyRefs.propertyName(this);
    }

    /**
     * Creates a reference to a nested property.
     *
     * @param nested the reference to the property in the type of this property
     * @param <U>    the type of the nested property
     * @return a reference whose property name is a dot separated path
     */
    default <U> PropertyRef<T, U> then(PropertyRef<V, U> nested) {
        return new Nested<>(getPropertyName() + "." + nested.getPropertyName(), this, nested);
    }

    /**
     * Helper to give the compiler a target type for a method reference, needed to
     * start a nested property path.
     *
     * @param getterReference the method reference to the getter
     * @param <T>             the bean type
     * @param <V>             the property type
     * @return the reference as such
     */
    static <T, V> PropertyRef<T, V> of(PropertyRef<T, V> getterReference) {
        return getterReference;
    }

    /**
     * A nested property reference, created by {@link PropertyRef#then(PropertyRef)}.
     * The property name is pre-resolved, as it can't be dug out of a composed lambda.
     *
     * @param <T> the root bean type
     * @param <M> the intermediate property type
     * @param <V> the leaf property type
     */
    class Nested<T, M, V> implements PropertyRef<T, V> {

        private final String propertyName;
        private final PropertyRef<T, M> root;
        private final PropertyRef<M, V> leaf;

        Nested(String propertyName, PropertyRef<T, M> root, PropertyRef<M, V> leaf) {
            this.propertyName = propertyName;
            this.root = root;
            this.leaf = leaf;
        }

        @Override
        public String getPropertyName() {
            return propertyName;
        }

        @Override
        public V apply(T bean) {
            M intermediate = root.apply(bean);
            return intermediate == null ? null : leaf.apply(intermediate);
        }
    }
}
