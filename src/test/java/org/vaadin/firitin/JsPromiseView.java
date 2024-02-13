package org.vaadin.firitin;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.util.JsPromise;

import java.time.LocalDateTime;

@Route
public class JsPromiseView extends VerticalLayout {

    public JsPromiseView() {

        add(new VButton("Get slow String from JS", e -> {
            JsPromise.resolveString("""
                            debugger;
                            setTimeout(() => {
                                resolve("It works!");
                            }, 2000);
                            """)
                    .thenAccept(s -> Notification.show(s));
        }));

        add(new VButton("Get slow Integer from JS", e -> {
            JsPromise.resolveInteger("""
                            setTimeout(() => {
                                resolve(1);
                            }, 2000);
                            """)
                    .thenAccept(s -> Notification.show("Integer value " + s));
        }));

        add(new VButton("Get slow Double from JS", e -> {
            JsPromise.resolveDouble("""
                            setTimeout(() => {
                                resolve(1.1234567);
                            }, 2000);
                            """)
                    .thenAccept(s -> Notification.show("Double value " + s));
        }));

        add(new VButton("Get slow Boolean from JS", e -> {
            JsPromise.resolveBoolean("""
                            setTimeout(() => {
                                resolve(true);
                            }, 2000);
                            """)
                    .thenAccept(s -> Notification.show("Boolean value " + s));
        }));

        record PersonDto(String name, Integer age, Double d) {
        }

        add(new VButton("Get a JSON serialized DTO from JS", e -> {
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


        add(new VButton("Resolve in modal dialog", e -> {
            Dialog d = new Dialog();
            d.add(new VButton("Get slow String from JS", ev -> {
                JsPromise.resolveString("""
                                setTimeout(() => {
                                    resolve("It works!");
                                }, 2000);
                                """)
                        .thenAccept(s -> Notification.show(s));
            }));
            d.open();

        }));


        add(new H3("Core API tests related to async JS"));

        add(new VButton("Related, call @ClientCallable server method, handle return value from the promise", e -> {
            testReturnValueFromClientCallable();
        }));

        add(new VButton("Return a Promise from Element.executeJs(String)", e -> {
            getElement().executeJs("""
                    return new Promise((resolve, reject) => {resolve("jorma");});
                    """).then(String.class, val -> {
                Notification.show(val);
            });
        }));


        add(new VButton("Return a Promise with async keyword from Element.executeJs(String)", e -> {
            getElement().executeJs("""
                    return (async () => {
                        return "jorma2";
                    }).apply();
                    """).then(String.class, val -> {
                Notification.show(val);
            });
        }));

        add(new VButton("Return a Promise from @ClientCallable", e -> {
            getElement().executeJs("""
                    return this.$server.getServerTimeStampWithMsg("It's complicated :-)");
                    """).then(String.class, val -> {
                Notification.show(val);
            });
        }));

    }

    private void testReturnValueFromClientCallable() {
        getElement().executeJs("""
                this.$server.getServerTimeStampWithMsg("Jorma!")
                    .then(msg => {window.alert(msg);});
                """);
    }

    @ClientCallable
    private String getServerTimeStampWithMsg(String msg) {
        // The return value will be there in a JS Promise for the caller
        return LocalDateTime.now() + " " + msg;
    }

}
