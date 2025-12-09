package org.vaadin.firitin.formbinder;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

public class RecordConstructors {

    record TestRecord(String name, int age) {
        public TestRecord(String name) {
            this(name, 0);
        }
    }

    record TestRecordWithDefault(String name, int age) {

        public TestRecordWithDefault(String name) {
            this(name, 0);
        }

        public TestRecordWithDefault() {
            this("default", 0);
        }
    }

    @Test
    void whereIsThePrimaryConstructor() {

        Constructor<?>[] declaredConstructors = TestRecordWithDefault.class.getDeclaredConstructors();

        for (Constructor<?> constructor : declaredConstructors) {
            System.out.println("Declared Constructor: " + constructor);
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            for (Class<?> parameterType : parameterTypes) {
                System.out.println("Parameter type: " + parameterType);
            }
        }

        Constructor<?>[] constructors = TestRecordWithDefault.class.getConstructors();

        for (Constructor<?> constructor : constructors) {
            System.out.println("Constructor: " + constructor);
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            for (Class<?> parameterType : parameterTypes) {
                System.out.println("Parameter type: " + parameterType);
            }
        }
    }
}
