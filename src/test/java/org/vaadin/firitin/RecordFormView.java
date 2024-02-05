package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.vaadin.firitin.form.VBinder;

import java.time.LocalDateTime;
import java.util.Set;

@Route
public class RecordFormView extends VerticalLayout {

    public RecordFormView() {

        var record = new VBinderTest.FooBar(
                "Foo", LocalDateTime.now(), 69
        );

        VBinderTest.FooBarForm form = new VBinderTest.FooBarForm();
        VBinder<VBinderTest.FooBar> binder = new VBinder<>(VBinderTest.FooBar.class, form);
        binder.setValue(record);

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

    private static Set<ConstraintViolation<VBinderTest.FooBar>> validate(VBinderTest.FooBar record) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<VBinderTest.FooBar>> constraintViolations = validator.validate(record);
        return constraintViolations;
    }
}
