package org.vaadin.firitin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.RequestHandler;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.util.JsPromise;

@Route
public class PromiseBlogExamplesView extends VerticalLayout {

    public PromiseBlogExamplesView() {

        // Register a tiny REST endpoint that we can test with fetch API
        UI.getCurrent().getSession().addRequestHandler((RequestHandler) (session, request, response) -> {
            if (request.getPathInfo().startsWith("/personjson")) {
                try {
                    // tiny pause to make test more meaningful
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // Don't do this in real life, use JAX-RS or Spring instead
                new ObjectMapper().writeValue(
                        response.getOutputStream(),
                        new PersonDto("Jorma from REST endpoint", 69, 1.2345)
                );
                return true;
            }
            return false;
        });


        add(new VButton("Return using @ClientCallable server method", e -> {
            returnValueFromClientCallable();
        }));

        add(new VButton("Return using custom event", e -> {
            returnValueUsingEvent();
        }));

        add(new VButton("Return JS Promise as a return type", e -> {
            returnValueUsingAPromise();
        }));

        add(new VButton("With anonymous async function", e -> {
            returnValueUsingUsinganAnonymousAsyncFunction();
        }));

        add(new VButton("With JsPromise.compute() helper from Viritin", e -> {
            returnWithJsPromiseCompute();
        }));

    }

    private void returnWithJsPromiseCompute() {
        JsPromise.compute("""
                const response = await fetch("/personjson");
                const jsObject = await response.json();
                // JS side will automatically JSON.stringify
                return jsObject; 
                """, PersonDto.class)
                .thenAccept(this::showDtoInUI);
    }

    private void returnValueUsingUsinganAnonymousAsyncFunction() {
        getElement().executeJs("""
                return (async () => {
                    // Now we can use await keyword to make asynchronous
                    // API look almost like a synchronous one
                    const response = await fetch("/personjson");
                    const jsObject = await response.json();
                    const jsonToServer = JSON.stringify(jsObject);;
                    return jsonToServer;
                }).apply(this, arguments); // anonymous async func -> Promise
                """).toCompletableFuture(String.class).thenAccept(json -> {
                    try {
                        showDtoInUI(new ObjectMapper()
                                .readValue(json, PersonDto.class));
                    } catch (JsonProcessingException ex) {
                        throw new RuntimeException(ex);
                    }
                });
  }

    private void returnValueUsingAPromise() {
        getElement().executeJs("""
                return fetch("/personjson").then(response => {
                        return response.json(); // this is also a Promise
                    }).then(json => {
                        const jsonStringToServer = JSON.stringify(json);
                        return jsonStringToServer;
                    });
                """).then(String.class, json -> {
                    try {
                        showDtoInUI(new ObjectMapper()
                                .readValue(json, PersonDto.class));
                    } catch (JsonProcessingException ex) {
                        throw new RuntimeException(ex);
                    }
                });
    }

    private void returnValueUsingEvent() {
        getElement().addEventListener("my-custom-event", e -> {
            String json = e.getEventData().getString("event.data");
            try {
                showDtoInUI(new ObjectMapper()
                        .readValue(json, PersonDto.class));
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }).addEventData("event.data");
        getElement().executeJs("""
            const el = this; // closure to element
            return fetch("/personjson").then(response => {
                    return response.json();
                }).then(json => {
                    const event = new Event("my-custom-event");
                    const jsonStringToServer = JSON.stringify(json);
                    event.data = jsonStringToServer;
                    el.dispatchEvent(event);
                });
            """);
    }

    private void returnValueFromClientCallable() {
        getElement().executeJs("""
                const el = this; // closure to element
                return fetch("/personjson").then(response => {
                        return response.json();
                    }).then(json => {
                        const jsonStringToServer = JSON.stringify(json);
                        // call the proxy to exposed Java method
                        el.$server.clientCallable(jsonStringToServer);
                    });
                """);
    }

    @ClientCallable
    private void clientCallable(String msg) {
        try {
            showDtoInUI(new ObjectMapper().readValue(msg, PersonDto.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void showDtoInUI(PersonDto dto) {
        Notification.show(dto.toString());
    }

    record PersonDto(String name, Integer age, Double d) {
    }

}
