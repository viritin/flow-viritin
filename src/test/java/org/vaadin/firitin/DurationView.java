package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.validation.constraints.NotNull;
import org.vaadin.firitin.fields.DurationField;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.form.FormBinder;

import java.time.Duration;

@Route
public class DurationView extends VVerticalLayout {

    public record Dto(String text, @NotNull Duration duration) {}

    DurationField duration = new DurationField("Input duration here");
    TextField text = new TextField("Basic text");

    public DurationView() {

        duration.setManualValidation(true);


        duration.setLabel("Label for field");
        duration.setRequiredIndicatorVisible(true);
        //duration.setTooltipText("This showed when hovered with mouse.");
        duration.setInvalid(true);
        duration.setErrorMessage("This makes the field red and error message visible.");
        duration.setHelperText("This is so called helper text. Kind of like tooltip but always visible.");

        add(duration, text);

        add("Duration (Duration.toString()):");
        var valueDisplay = new Paragraph();
        add(valueDisplay);

        FormBinder<Dto> binder = new FormBinder<>(Dto.class, this);

        duration.addValueChangeListener(e -> {
            Duration d = e.getValue();
            valueDisplay.setText(""+d);
        });

        add(new VButton("Binder dto.toString()", () -> Notification.show(binder.getValue().toString())));

        add(new Button("Set 24h", e-> {
            duration.setValue(Duration.ofDays(1));
        }));

        add(new Button("Set 1s", e-> {
            duration.setValue(Duration.ofSeconds(1));
        }));

        add(new Button("Set 1m", e-> {
            duration.setValue(Duration.ofMinutes(1));
        }));

        add(new Button("Set 10m", e-> {
            duration.setValue(Duration.ofMinutes(10));
        }));
        add(new Button("Set 61m", e-> {
            duration.setValue(Duration.ofMinutes(61));
        }));

    }


}
