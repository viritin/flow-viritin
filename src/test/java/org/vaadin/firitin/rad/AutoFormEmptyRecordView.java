package org.vaadin.firitin.rad;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Route
public class AutoFormEmptyRecordView extends VerticalLayout {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    private boolean autovalidate = false;

    public AutoFormEmptyRecordView() {
        add(new H1("AutoForm editing record type (initially empty)"));

        AutoFormContext ctx = new AutoFormContext();

        HorizontalLayout horizontalLayout = new HorizontalLayout() {
            {

                AutoForm<PersonRecord> form = ctx.createForm(PersonRecord.class)
                        .withBeanValidation();
                //form.getBinder().setConverter("oldSchoolDate", new LocalDateToDateConverter(ZoneId.systemDefault()));

                form.getBinder().addValueChangeListener(e1 -> {
                    if (autovalidate && e1.isFromClient()) {
                        var violations = validator.validate(form.getValue());
                        form.getBinder().setConstraintViolations(violations);
                    }
                });

                add(form.getFormBody());
                add(new VerticalLayout() {
                    {
                        add(
                                new Button("Show Value ->", e -> {
                                    if (getChildren().count() > 2)
                                        getChildren().toList().get(2).removeFromParent();
                                    try{
                                        PersonRecord record = form.getValue();
                                        add(PrettyPrinter.toVaadin(record));
                                    } catch (Exception ex) {
                                        Notification.show("Error: " + ex.getMessage());
                                    }
                                }),
                                new Button("Validate", e -> {

                                }),
                                new Checkbox("Toggle autovalidation", e -> {
                                    autovalidate = !autovalidate;
                                    Notification.show("Autovalidate is now " + autovalidate);
                                })
                        );
                    }
                });
            }
        };

        add(horizontalLayout);


    }

    public record PersonRecord(
            String firstName,
            String lastName,
            int age,
            boolean active
    ) {}

}
