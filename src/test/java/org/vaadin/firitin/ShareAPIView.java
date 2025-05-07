package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.util.Share;

import java.net.URI;
import java.net.URISyntaxException;

@Route
public class ShareAPIView extends VVerticalLayout {

    public ShareAPIView() {
        add(new H1("Share API"));
        add(new Paragraph("This is a demo for the Web Share API. It opens native share dialog on the devices."));
        add(new Button("Share a link to vaadin.com", e -> {
            try {
                Share.share("Vaadin", "Vaadin is a must have tools that make all Java developers web developers.", new URI("https://vaadin.com/"))
                        // optionally you can handle the result or cancellation...
                        .then(voidJson -> {
                                    Notification.show("Shared successfully");
                                },
                                errorMsg -> {
                                    Notification.show("Error sharing:" + errorMsg.toString());
                                });
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
        }));
        add(new Button("Share weird link", e -> {
            try {
                Share.share("Vaa'das`df\"in", "Vaadin is a must have tools that make all Java developers web developers.", new URI("https://vaadin.com/"))
                        // optionally you can handle the result or cancellation...
                        .then(voidJson -> {
                                    Notification.show("Shared successfully");
                                },
                                errorMsg -> {
                                    Notification.show("Error sharing:" + errorMsg.toString());
                                });
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
        }));
    }
}
