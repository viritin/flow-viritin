package org.vaadin.firitin.rad;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Route;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.vaadin.firitin.form.FormBinder;
import org.vaadin.firitin.rad.datastructures.ll.Dash;
import org.vaadin.firitin.rad.datastructures.ll.LeaderLineOptions;

@Route
public class AutoFormLeaderLineOptionsView extends VerticalLayout {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    private boolean autovalidate = false;


    public AutoFormLeaderLineOptionsView() {

        add(new H1("AutoFormView: LeaderLineOptions"));

        AutoFormContext context = new AutoFormContext();

        context.setAnnotateTypes(true);


        HorizontalLayout horizontalLayout = new HorizontalLayout() {
            {
                if (true) {

                    AutoForm<LeaderLineOptions> form = context.createForm(new LeaderLineOptions());

                    form.setSaveHandler(person -> {
                        Notification.show("Save action!");
                        if (getChildren().count() > 2)
                            getChildren().toList().get(2).removeFromParent();
                        if (form.getValue() != null) {
                            add(PrettyPrinter.toVaadin(form.getValue()));
                        }
                    });

                    form.setResetHandler(person -> {
                        Notification.show("Reset action!");
                    });

                    form.setDeleteHandler(person -> {
                        Notification.show("Delete action!");
                    });

                    add(new VerticalLayout(
                            form.getActions(),
                            form.getFormBody()
                    ));

                    add(
                            new Button("Show Value ->", e -> {
                                if (getChildren().count() > 2)
                                    getChildren().toList().get(2).removeFromParent();
                                if (form.getValue() != null) {
                                    add(PrettyPrinter.toVaadin(form.getValue()));
                                }
                            })
                    );

                }

            }
        };

        add(horizontalLayout);

    }

}
