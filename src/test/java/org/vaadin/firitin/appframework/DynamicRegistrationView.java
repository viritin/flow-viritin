package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.RouteConfiguration;
import org.vaadin.firitin.components.RichText;

@MenuItem(icon = VaadinIcon.PLUS_CIRCLE, title = "Dynamic registration")
public class DynamicRegistrationView extends MyAbstractView {

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (!attachEvent.isInitialAttach()) {
            return;
        }
        add(new RichText().withMarkDown("""
                # Dynamic route registration

                `DynamicView` is annotated with `@Route(registerAtStartup = false)`.
                Use the buttons below to register and unregister it via
                `RouteConfiguration.forSessionScope()`. Once registered, it should
                appear in the side navigation menu.
                """));

        Button register = new Button("Register DynamicView", e -> {
            RouteConfiguration.forSessionScope().setAnnotatedRoute(DynamicView.class);
        });
        Button unregister = new Button("Unregister DynamicView", e -> {
            RouteConfiguration.forSessionScope().removeRoute(DynamicView.class);
        });
        Button status = new Button("Show registration status", e -> {
            boolean registered = RouteConfiguration.forSessionScope().isRouteRegistered(DynamicView.class);
            add(new Paragraph("DynamicView registered in session scope: " + registered));
        });
        add(register, unregister, status);
    }
}
