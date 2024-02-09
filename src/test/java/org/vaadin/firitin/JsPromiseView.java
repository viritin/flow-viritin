package org.vaadin.firitin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.util.JsPromise;

@Route
public class JsPromiseView extends VerticalLayout {

    public JsPromiseView() {

        add(new VButton("Get slow String from JS", e-> {
            JsPromise.resolveString("""
                setTimeout(() => {
                    resolve("It works!");
                }, 2000);
                """)
                .thenAccept(s -> Notification.show(s));
        }));

        add(new VButton("Get slow Integer from JS", e-> {
            JsPromise.resolveInteger("""
                setTimeout(() => {
                    resolve(1);
                }, 2000);
                """)
                .thenAccept(s -> Notification.show("Integer value " + s));
        }));

        add(new VButton("Get slow Double from JS", e-> {
            JsPromise.resolveDouble("""
                setTimeout(() => {
                    resolve(1.1234567);
                }, 2000);
                """)
                    .thenAccept(s -> Notification.show("Double value " + s));
        }));

        add(new VButton("Get slow Boolean from JS", e-> {
            JsPromise.resolveBoolean("""
                setTimeout(() => {
                    resolve(true);
                }, 2000);
                """)
                    .thenAccept(s -> Notification.show("Boolean value " + s));
        }));

        record PersonDto(String name, Integer age, Double d) {}

        add(new VButton("Get a JSON serialized DTO from JS", e-> {
            JsPromise.resolve("""
                setTimeout(() => {
                    const person = {};
                    person.name = "Jorma";
                    person.age = 69;
                    person.d = $0
                    resolve(person);
                }, 2000);
                """, PersonDto.class, 22.23)
                    .thenAccept(s -> Notification.show("PersonDTO value " + s));
        }));

    }
}
