package org.vaadin.firitin.formbinder.hibernatecrossfieldexample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.components.textfield.VIntegerField;
import org.vaadin.firitin.fields.CommaSeparatedStringField;
import org.vaadin.firitin.form.BeanValidationForm;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Route
public class FormBinderCarForm extends BeanValidationForm<Car> {

    IntegerField seatCount = new VIntegerField("Seat count");

    CommaSeparatedStringField passengers  = new CommaSeparatedStringField("Passengers (comma separated)")
            .withFullWidth();

    Pre display = new Pre();

    public FormBinderCarForm() {
        super(Car.class);
        getBinder().setConverter("passengers", new Converter<Set<String>, List<Person>>() {
            @Override
            public Result<List<Person>> convertToModel(Set<String> value, ValueContext context) {
                try {
                    return Result.ok(value.stream().map(s -> new Person(s)).toList());
                } catch (Exception e) {
                    return Result.error(e.getMessage());
                }
            }

            @Override
            public Set<String> convertToPresentation(List<Person> value, ValueContext context) {
                return value.stream().map(p -> p.getName()).collect(Collectors.toSet());
            }
        });

        // TODO why do I need to do this!?
        getDeleteButton().setVisible(false);

        setSavedHandler(car -> {
            Notification.show("Saved");
            showCurrentStateAsJson(car);
        });

        getContent().add(new Button("Show state & report validation errors", e-> {
            Car car = getBinder().getValue();
            var constraintViolations = doBeanValidation(car);
            getBinder().setConstraintViolations(constraintViolations);
            showCurrentStateAsJson(car);
        }));

        setEntity(new Car(2, Arrays.asList(new Person("Jorma"),new Person("Ville"), new Person("Kalle"))));

    }

    private void showCurrentStateAsJson(Car car) {
        try {
            String string = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(car);
            display.setText(string);
            getContent().add(display);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected List<Component> getFormComponents() {
        return Arrays.asList(
                new RichText().withMarkDown("""
                # Car class level validation example from Hibernate docs

                The save button is enabled if there are some changes **and** if the form is valid.                
                 The ValidPassengerCountValidator check all persons can fit to the seats of this car. 
                 Increase the car size or remove persons to make save button enabled. 
                """),
                seatCount,
                passengers);
    }
}
