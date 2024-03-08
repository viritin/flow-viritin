package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
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
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.form.FormBinder;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Set;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Route
public class FormBinderView extends VerticalLayout {

    @BigShouldBeBigger
    public static class Person {
        @NotEmpty String name;
        @NotNull
        @Min(0) Integer small;
        @Max(100) Integer big;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getSmall() {
            return small;
        }

        public void setSmall(Integer small) {
            this.small = small;
        }

        public Integer getBig() {
            return big;
        }

        public void setBig(Integer big) {
            this.big = big;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", small=" + small +
                    ", big=" + big +
                    '}';
        }
    }

    public FormBinderView() {

        var p = new Person();
        p.setName("Jorma");
        p.setSmall(70);
        p.setBig(69);


        PersonForm form = new PersonForm();
        var binder = new FormBinder<>(Person.class, form);
        binder.setConverter("big", new Converter<String, Integer>() {
            @Override
            public Result<Integer> convertToModel(String value, ValueContext context) {
                try {
                    Integer integer = Integer.valueOf(value);
                    return Result.ok(integer);
                } catch (Exception ex) {
                    return Result.error("Couldn't convert " + value + " to integer");
                }
            }

            @Override
            public String convertToPresentation(Integer value, ValueContext context) {
                return value.toString();
            }
        });
        binder.setValue(p);

        binder.addValueChangeListener(e -> {
            if(!binder.hasInputConversionErrors()) {
                binder.setConstraintViolations(validate(binder.getValue()));
            }
        });

        add(form);

        add(new Button("Show value", e -> {
            Notification.show("Value now:" + binder.getValue());
        }));

        add(new Button("Show validation errors", e -> {
            var constraintViolations = validate(binder.getValue());
            binder.setConstraintViolations(constraintViolations);
            Notification.show(constraintViolations.toString());
        }));

    }

    @Target({ TYPE, ANNOTATION_TYPE })
    @Retention(RUNTIME)
    @Constraint(validatedBy = { BigShouldBeBiggerValidator.class })
    public @interface BigShouldBeBigger {

        String message() default "Big should be bigger!";

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
            if(car.getBig() == null) {
                return false;
            }
            return car.getBig() > car.getSmall();
        }
    }

    public static class PersonForm extends VerticalLayout {

        TextField name = new VTextField("Name");
        IntegerField small = new IntegerField("Small number");

        // mismatching field to property type, needs configured converter
        TextField big = new TextField("Big bound with TextField");

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

}
