package org.vaadin.firitin;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.RequestHandler;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.util.JsPromise;

import java.time.LocalDateTime;

@Route
public class JsPromiseView extends VerticalLayout {

    public JsPromiseView() {

        add(new VButton("Get slow String from JS", e -> {
            JsPromise.resolveString("""
                            setTimeout(() => {
                                resolve("It works!");
                            }, 2000);
                            """)
                    .thenAccept(s -> Notification.show(s));
        }));

        add(new VButton("Throw error while resolving a String", e -> {
            JsPromise.resolveString("""
                            setTimeout(() => {
                                reject(new Error("It failed!"));
                            }, 2000);
                            """)
                    .thenAccept(s -> Notification.show(s))
                    .exceptionally(ex -> {
                        Notification.show(ex.toString());
                        return null;
                    });
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

        add(new VButton("Get String from async method body", e -> {
            JsPromise.computeString("""
                            return "It works from async";
                            """)
                    .thenAccept(s -> Notification.show(s));
        }).withTooltip("Doesn't make too much sense when used like here, but in this method body you can use await keyword when consuming API that returns a Promise. Check the fetch API example below to figure out why this makes sense with some APIs."));
        add(new VButton("Error handling from async method body", e -> {
            JsPromise.computeString("""
                            if(true) throw new Error("It failed!");
                            return "It works from async";
                            """)
                    .thenAccept(s -> Notification.show(s))
                    .exceptionally(ex -> {
                        Notification.show(ex.getMessage());
                        return null;
                    });
        }));
        add(new VButton("Get Integer from async method body", e -> {
            JsPromise.computeInteger("""
                            return 0;
                            """)
                    .thenAccept(i -> Notification.show(i.toString()));
        }).withTooltip("Doesn't make too much sense when used like here, but in this method body you can use await keyword when consuming API that returns a Promise. Check the fetch API example below to figure out why this makes sense with some APIs."));
        add(new VButton("Get Double from async method body", e -> {
            JsPromise.computeDouble("""
                            return 12.2333;
                            """)
                    .thenAccept(d -> Notification.show(d.toString()));
        }).withTooltip("Doesn't make too much sense when used like here, but in this method body you can use await keyword when consuming API that returns a Promise. Check the fetch API example below to figure out why this makes sense with some APIs."));
        add(new VButton("Get Boolean from async method body", e -> {
            JsPromise.computeBoolean("""
                            return true;
                            """)
                    .thenAccept(b-> Notification.show(b.toString()));
        }).withTooltip("Doesn't make too much sense when used like here, but in this method body you can use await keyword when consuming API that returns a Promise. Check the fetch API example below to figure out why this makes sense with some APIs."));

        record PersonDto(String name, Integer age, Double d) {
        }

        // Register a tiny REST endpoint that we can test with fetch API
        UI.getCurrent().getSession().addRequestHandler((RequestHandler) (session, request, response) -> {
            if(request.getPathInfo().startsWith("/personjson")) {
                response.getOutputStream().write("""
                    {
                        "name" : "Jorma from REST endpoint",
                        "age" : 69,
                        "d" : 1.234
                    }
                    """.getBytes());
                return true;
            }
            return false;
        });

        add(new VButton("Get a PersonDTO via browsers fetch call that is de-serialized from JSON and then passed back to server. MIND BLOWN, HAH?", e -> {
            JsPromise.compute("""
            // This method body is executed in async function, 
            // we can use await magic 🥳
            const response = await fetch("/personjson");
            const jsonString = await response.text();
            const person = JSON.parse(jsonString);
            return person;
            """, PersonDto.class, 22.23).thenAccept(
                    (PersonDto dto) -> Notification.show("PersonDTO value " + dto));
        }));

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
