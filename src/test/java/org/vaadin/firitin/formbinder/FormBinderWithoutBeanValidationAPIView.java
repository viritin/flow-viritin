package org.vaadin.firitin.formbinder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.form.FormBinder;

import java.util.Collections;
import java.util.Map;

@Route
public class FormBinderWithoutBeanValidationAPIView extends VerticalLayout {

    TextField name = new TextField("Name!");

    public FormBinderWithoutBeanValidationAPIView() {

        add(name);

        Dto dto = new Dto();
        dto.setName("Initial name value");

        var binder = new FormBinder<>(Dto.class, this)
                .withValue(dto);

        Pre value = new Pre();

        Button button = new Button("Validate and show dto value",
                event -> {
                    if(dto.getName().isBlank()) {
                        var constraintViolationsWithoutBeanValidations =
                                Map.of("name", "Name must be filled, now String.isBlank()"
                                ,"any-key-not-found-maps-to-toplevel", "This is a bean level error message, e.g. cross field validation error");
                        binder.setRawConstraintViolations(constraintViolationsWithoutBeanValidations);
                    } else {
                        binder.setRawConstraintViolations(Collections.emptyMap());
                    }

                    try {
                        String string = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dto);
                        value.setText(string);
                        add(value);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });

        add(button);
    }

    public static class Dto {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}
