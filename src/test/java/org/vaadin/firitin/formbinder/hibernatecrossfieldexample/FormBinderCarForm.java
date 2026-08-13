package org.vaadin.firitin.formbinder.hibernatecrossfieldexample;

import com.vaadin.flow.component.AttachEvent;
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
import tools.jackson.databind.ObjectMapper;

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
    Pre errorDisplay = new Pre() {{
        getStyle().setColor("orange");
    }};

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

        setSavedHandler(car -> {
            Notification.show("Saved");
            showCurrentStateAsJson(car);
        });

        /*
           Nothing here says where the violation is shown, because nothing has to:
           BeanValidationForm owns a component for the violations that belong to no
           field — getClassLevelViolationsDisplay() — and hands it to the binder when
           it binds. The default createContent() puts it between the fields and the
           toolbar. A form that lays itself out and forgets it gets it appended.
        */
        getContent().add(new Button("Show state & report validation errors", e-> {
            Car car = getBinder().getValue();
            var constraintViolations = doBeanValidation(car);
            getBinder().setConstraintViolations(constraintViolations);
            showCurrentStateAsJson(car);
        }));

        setEntity(new Car(2, Arrays.asList(new Person("Jorma"),new Person("Ville"), new Person("Kalle"))));

    }

    private void showCurrentStateAsJson(Car car) {
        String string = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(car);
        display.setText(string);
        getContent().add(display);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getContent().addComponentAsFirst(new RichText().withMarkDown("""
                # Car class level validation example from Hibernate docs

                The save button is enabled if there are some changes **and** if the form is valid.                
                 The ValidPassengerCountValidator check all persons can fit to the seats of this car. 
                 Increase the car size or **remove** persons to make save button enabled.
                 Adding one makes the car less valid, not more.

                 The message about it appears **under the fields**, not on either of
                 them: it is about the two together, so it belongs to neither. That
                 place is the form's own, and every BeanValidationForm has one.
                 
                """));
    }

    @Override
    protected List<Component> getFormComponents() {
        return Arrays.asList(
                seatCount,
                passengers);
    }
}
