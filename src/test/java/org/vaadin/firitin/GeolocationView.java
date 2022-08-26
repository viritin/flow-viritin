package org.vaadin.firitin;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.geolocation.Geolocation;
import org.vaadin.firitin.geolocation.GeolocationOptions;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Route
public class GeolocationView extends VVerticalLayout {

    private Geolocation geolocation = null;
    Paragraph location = new Paragraph();

    public GeolocationView() {

        add(location);

        Checkbox enableHighAccuracy = new Checkbox("High accuracy");
        IntegerField timeout = new IntegerField("Timeout");
        IntegerField maximumAge = new IntegerField("Max age");

        List<AbstractField> abstractSinglePropertyFields = Arrays.asList(enableHighAccuracy, timeout, maximumAge);

        Button button = new Button("Start tracking");
        button.addClickListener(e -> {
            if(geolocation == null) {
                geolocation = Geolocation.watchPosition(
                        event -> {
                            System.out.println(Instant.ofEpochMilli(event.getTimestamp()) + ":" + event.getCoords());
                            updateMyLocation(event.getCoords().getLatitude(), event.getCoords().getLongitude());
                        },
                        browserError -> {
                            Notification.show("ERROR: " + browserError);
                        },
                        new GeolocationOptions(enableHighAccuracy.getValue(), timeout.getValue(), maximumAge.getValue())
                );
                button.setText("Stop tracking");
                abstractSinglePropertyFields
                        .forEach(c -> c.setEnabled(false));
            } else {
                geolocation.cancel();
                geolocation = null;
                button.setText("Start tracking");
                abstractSinglePropertyFields
                        .forEach(c -> c.setEnabled(true));
            }
        });

        Button checkOnce = new Button("Check once", e -> {
            Geolocation.getCurrentPosition(
                    event -> {
                        System.out.println(Instant.ofEpochMilli(event.getTimestamp()) + ":" + event.getCoords());
                        updateMyLocation(event.getCoords().getLatitude(), event.getCoords().getLongitude());
                    },
                    browserError -> {
                        Notification.show("ERROR: " + browserError);
                    },
                    new GeolocationOptions(enableHighAccuracy.getValue(), timeout.getValue(), maximumAge.getValue())
            );
        });
        add(new HorizontalLayout(button, checkOnce, enableHighAccuracy, timeout, maximumAge));

    }

    private void updateMyLocation(double latitude, double longitude) {
        location.setText("Last update " + LocalTime.now() + " : " + latitude + ", " + longitude);
    }
}
