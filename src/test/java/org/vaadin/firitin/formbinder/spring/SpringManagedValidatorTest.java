package org.vaadin.firitin.formbinder.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Set;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.textfield.TextField;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.vaadin.firitin.form.BeanValidationForm;

/**
 * How to give a {@link BeanValidationForm} the validator a Spring application
 * already has — and what happens if you do not.
 *
 * <p>The form builds a validator of its own from the default provider, which is
 * enough while the constraints are self-contained. It stops being enough as soon as
 * a {@code ConstraintValidator} needs something from the application: a registry, a
 * repository, anything asked of the outside world. A validator built from the
 * default provider is instantiated by reflection, its injection points stay null,
 * and the check fails rather than answering. That is the first test here.
 *
 * <p>The fix is two lines in the form and no change to the library: hand it the
 * container's {@code Validator} and override {@link
 * BeanValidationForm#getValidator()}. Spring's validator instantiates constraint
 * validators through the bean factory, so they can be injected, and it interpolates
 * messages through the application's own {@code MessageSource}.
 *
 * <p>Deliberately no Spring module and no dependency on Spring in the add-on: one
 * overridable method costs less than a second artifact to keep in step.
 */
class SpringManagedValidatorTest {

    /*
       Public, all of it: Hibernate Validator instantiates a constraint validator by
       reflection and cannot reach a package private one at all. That failure —
       HV000064 — is a different one from the one this example is about, and it would
       hide it.
    */

    /** Something only the application knows, of the kind a validator has to ask. */
    public static class SensorRegistry {
        boolean knows(String id) {
            return Set.of("DHT", "RBF").contains(id);
        }
    }

    @Documented
    @Constraint(validatedBy = KnownSensorValidator.class)
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface KnownSensor {
        String message() default "No such sensor";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    /** Ordinary Spring: the validator is a bean like any other and gets what it needs. */
    public static class KnownSensorValidator implements ConstraintValidator<KnownSensor, String> {
        @Autowired
        private SensorRegistry registry;

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return value == null || value.isEmpty() || registry.knows(value);
        }
    }

    public record Reading(@KnownSensor String sensor) {
    }

    /**
     * The whole of the recipe: take the validator in, and answer with it.
     */
    static class ReadingForm extends BeanValidationForm<Reading> {

        TextField sensor = new TextField("Sensor");

        private final Validator validator;

        ReadingForm(Validator validator) {
            super(Reading.class);
            this.validator = validator;
            setSavedHandler(reading -> {
            });
            setEntity(new Reading("DHT"));
        }

        @Override
        protected Validator getValidator() {
            // Without this line the form builds one of its own; see the tests below.
            return validator == null ? super.getValidator() : validator;
        }

        @Override
        protected Component createContent() {
            return sensor;
        }

        @Override
        protected List<Component> getFormComponents() {
            return List.of();
        }
    }

    @Configuration
    static class ApplicationConfiguration {

        @Bean
        SensorRegistry sensorRegistry() {
            return new SensorRegistry();
        }

        @Bean
        LocalValidatorFactoryBean validator() {
            return new LocalValidatorFactoryBean();
        }
    }

    /**
     * The form's own validator cannot build a constraint validator that needs
     * anything: the field stays null and Hibernate Validator reports the failure as
     * HV000028. It happens the moment the form is given an entity, which is as good
     * a place as any to find out.
     */
    @Test
    void theFormsOwnValidatorCannotInjectAnything() {
        ValidationException failure =
                assertThrows(ValidationException.class, () -> new ReadingForm(null));

        assertTrue(failure.getMessage().contains("HV000028"), failure.getMessage());
    }

    @Test
    void theContainersValidatorCanAndTheFormThenWorks() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(ApplicationConfiguration.class)) {
            ReadingForm form = new ReadingForm(context.getBean(LocalValidatorFactoryBean.class));

            type(form.sensor, "XXX");
            assertTrue(form.sensor.isInvalid(), "the registry does not know it");
            assertEquals("No such sensor", form.sensor.getErrorMessage());
            assertFalse(form.getSaveButton().isEnabled());

            type(form.sensor, "RBF");
            assertFalse(form.sensor.isInvalid());
            assertTrue(form.getSaveButton().isEnabled(), "a known sensor is saveable");
        }
    }

    /** A change the reader made, as the browser delivers it. */
    private static void type(TextField field, String value) {
        String previous = field.getValue();
        field.setValue(value);
        ComponentUtil.fireEvent(field,
                new AbstractField.ComponentValueChangeEvent<>(field, field, previous, true));
    }
}
