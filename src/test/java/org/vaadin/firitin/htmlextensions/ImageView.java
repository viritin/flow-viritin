package org.vaadin.firitin.htmlextensions;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.html.VImage;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route
public class ImageView extends VVerticalLayout {
    public ImageView() {
        add(new VImage("https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png", "Google logo"));


        VImage googleLogoWithInvalidSource = new VImage("https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.pngX", "Google logo");
        googleLogoWithInvalidSource.addErrorListener(event -> {
            Notification.show("Failed to load image");
            event.getSource().getStyle().setBorder("10px solid red");
            event.getSource().getStyle().setWidth("200px");
            event.getSource().getStyle().setHeight("200px");
        });
        add(googleLogoWithInvalidSource);

    }
}
