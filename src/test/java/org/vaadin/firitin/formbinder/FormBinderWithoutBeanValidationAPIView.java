package org.vaadin.firitin.formbinder;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.form.FormBinder;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Map;

@Route
public class FormBinderWithoutBeanValidationAPIView extends VerticalLayout {

    TextField name = new TextField("Name!");

    public FormBinderWithoutBeanValidationAPIView() {

        add(new RichText().withMarkDown("""
        # FormBinder without the Bean Validation API

        Nothing is annotated here and no validator runs by itself. The rule is
        written by hand, in the button below:

        ```java
        if (dto.getName().isBlank()) { ... }
        ```

        Press it with the field emptied to see what that produces. The binder is
        handed a plain `Map<String, String>` of messages, and where each one ends up
        is decided by its key:

        * `"name"` is a property of the DTO, so that message is shown on the field
          editing it.
        * the other key in the map matches no property — deliberately, it is called
          `any-key-not-found-maps-to-toplevel` — so that message is shown under the
          form, which is where a rule spanning several fields belongs.

        Press it with something typed in the field, and both go away. The JSON
        underneath is the DTO itself: the field writes into it as you type, so it is
        already up to date when the button asks.

        """));

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

                    String string = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dto);
                    value.setText(string);
                    add(value);
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
