package org.vaadin.firitin.formbinder;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Payload;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.form.FormBinder;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Set;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Route
public class FormBinderWithRecordView extends VerticalLayout {

    @BigShouldBeBigger
    record Person(@NotEmpty String name, @NotNull @Min(0) Integer small, @Max(100) int big) {

        // This is not used anywhere, but should not break binding
        public boolean isValid() {
            return big > small;
        }

    }

    public FormBinderWithRecordView() {

        add(new RichText().withMarkDown("""
        # Viritin FormBinder also support Java records 💪
        
        This example has a trivial Java record (with non-trivial validation)
        that is bound to a form.
        
        As an example there is also Java Bean Validation constraints to it,
        including a class level (aka cross-field) validator, and the violations
        are reported in the UI while you change the form.
        
        """));

        var personRecord = new Person("Jorma", 70, 69);

        // This class has fields to edit Person properties and
        // how to lay them in the UI
        PersonForm form = new PersonForm();
        add(form);
        // In typical cases, you'll use the dto type for binder and then assigning the value later
        // var binder = new FormBinder<>(Person.class, form);
        // binder.setValue(personRecord);

        // alternatively assign non-null value directly, type is then drawn from it
        var binder = new FormBinder<>(personRecord, form);

        // With a value change listener to the binder we can listen to all fields at once
        binder.addValueChangeListener(event -> {
            // As an example, we validate the current state with
            // Java Bean Validation API
            var violations = validate(binder.getValue());
            // and assign reported violation them to binder, which shows them
            // in the form (if connected to a property, next to its field)
            binder.setConstraintViolations(violations);
        });

        add(new Button("Show value", e -> {
            Notification.show("Value now:" + binder.getValue(), 3000, Notification.Position.BOTTOM_END);
        }));

        // alternatively
        add(new Button("Show validation errors", e -> {
            var constraintViolations = validate(binder.getValue());
            binder.setConstraintViolations(constraintViolations);
            Notification.show(constraintViolations.toString(), 3000, Notification.Position.TOP_END);
        }));

    }

    public static class PersonForm extends VerticalLayout {

        TextField name = new VTextField("Name");
        IntegerField small = new IntegerField("Small number");
        IntegerField big = new IntegerField("Big number");

        public PersonForm() {
            add(name,small,big);
        }
    }

    private static Set<ConstraintViolation<Person>> validate(Person record) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Person>> constraintViolations = validator.validate(record);
        return constraintViolations;
    }


    @Target({ TYPE, ANNOTATION_TYPE })
    @Retention(RUNTIME)
    @Constraint(validatedBy = { BigShouldBeBiggerValidator.class })
    public @interface BigShouldBeBigger {

        String message() default "Big should be bigger that the small!";

        Class<?>[] groups() default { };

        Class<? extends Payload>[] payload() default { };
    }


    public static class BigShouldBeBiggerValidator
            implements ConstraintValidator<BigShouldBeBigger, Person> {

        @Override
        public boolean isValid(Person car, ConstraintValidatorContext context) {
            if ( car == null ) {
                return true;
            }
            return car.big() > car.small();
        }

    }


}
