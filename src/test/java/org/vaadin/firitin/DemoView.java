package org.vaadin.firitin;

import org.vaadin.firitin.button.VButton;
import org.vaadin.firitin.button.VButton.ButtonColor;
import org.vaadin.firitin.button.VButton.ButtonType;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;

@Route("")
public class DemoView extends Div {

    public DemoView() {
        VButton button = new VButton().withText("Click me").withColor(ButtonColor.SUCCESS).withType(ButtonType.PRIMARY)
                .withClickListener(e -> Notification.show("Hello!"));

        button.addBlurListener(e -> {
           Notification.show("Blurred!"); 
        });
        add(button);
    }
}
