package org.vaadin.firitin.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.shared.Registration;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class JsPromise {

    private static final ObjectMapper jackson = new ObjectMapper();

    /**
     * Asynchronously returns a string from the client side. The JS given for this
     * method is a JS Promise body with "resolve" and "reject" defined for returning
     * the value or failing the request.
     *
     * @param promiseBody a JS snippet that resolves a promise
     * @param args the extra arguments interpolated into JS as in Element.executeJs
     * @return the string as {@link CompletableFuture}
     */
    public static CompletableFuture<String> resolveString(String promiseBody, Serializable... args) {
        return resolve(promiseBody, String.class, args);
    }

    /**
     * Asynchronously returns a boolean from the client side. The JS given for this
     * method is a JS Promise body with "resolve" and "reject" defined for returning
     * the value or failing the request.
     *
     * @param promiseBody a JS snippet that resolves a promise
     * @param args the extra arguments interpolated into JS as in Element.executeJs
     * @return the boolean as {@link CompletableFuture}
     */
    public static CompletableFuture<Boolean> resolveBoolean(String promiseBody, Serializable... args) {
        return resolve(promiseBody, Boolean.class, args);
    }

    /**
     * Asynchronously returns an integer from the client side. The JS given for this
     * method is a JS Promise body with "resolve" and "reject" defined for returning
     * the value or failing the request.
     *
     * @param promiseBody a JS snippet that resolves a promise
     * @param args the extra arguments interpolated into JS as in Element.executeJs
     * @return the integer as {@link CompletableFuture}
     */
    public static CompletableFuture<Integer> resolveInteger(String promiseBody, Serializable... args) {
        return resolve(promiseBody, Integer.class, args);
    }

    /**
     * Asynchronously returns a double from the client side. The JS given for this
     * method is a JS Promise body with "resolve" and "reject" defined for returning
     * the value or failing the request.
     *
     * @param promiseBody a JS snippet that resolves a promise
     * @param args the extra arguments interpolated into JS as in Element.executeJs
     * @return the double as {@link CompletableFuture}
     */
    public static CompletableFuture<Double> resolveDouble(String promiseBody, Serializable... args) {
        return resolve(promiseBody, Double.class, args);
    }

    /**
     * Asynchronously returns a value from the client side. The JS given for this
     * method is a JS Promise body with "resolve" and "reject" defined for returning
     * the value or failing the request.
     *
     * @param <T>         the return type, if not a basic data type, the return parameter in browser is expected to be JSON that is then mapped to given type with Jackson
     * @param promiseBody a JS snippet that resolves a promise
     * @param returnType  the return value type: String, Integer, Double, Boolean or an object that gets serialized to JSON and deserialized with default Jackson settings on the server side.
     * @param args the extra arguments interpolated into JS as in Element.executeJs
     * @return the future to get the value
     */
    public static <T> CompletableFuture<T> resolve(String promiseBody, Class<T> returnType, Serializable... args) {
        CompletableFuture<T> future = new CompletableFuture<>();
        UI current = UI.getCurrent();
        final String eventtype = UUID.randomUUID().toString();
        AtomicReference<Registration> reg = new AtomicReference<>();
        reg.set(current.getElement().addEventListener(eventtype, e -> {
            if (String.class.isAssignableFrom(returnType)) {
                future.complete((T) e.getEventData().getString("event.payload"));
            } else if(Integer.class.isAssignableFrom(returnType)) {
                int number = (int) e.getEventData().getNumber("event.payload");
                future.complete((T) Integer.valueOf(number));
            } else if(Double.class.isAssignableFrom(returnType)) {
                double number = e.getEventData().getNumber("event.payload");
                future.complete((T) Double.valueOf(number));
            } else if(Boolean.class.isAssignableFrom(returnType)) {
                boolean b = e.getEventData().getBoolean("event.payload");
                future.complete((T) Boolean.valueOf(b));
            } else {
                try {
                    future.complete(jackson.readValue(e.getEventData().getString("event.payload"), returnType));
                } catch (JsonProcessingException ex) {
                    future.completeExceptionally(ex);
                }
            }
            reg.get().remove();

        }).addEventData("event.payload"));

        current.getElement().executeJs("""
            const ui = this;
            const executeAsyncPromise = new Promise((resolve, reject) => {
                %s
            });
            const event = new Event("%s");
            executeAsyncPromise.then(val => {
                    if(typeof val === 'object') {
                        event.payload = JSON.stringify(val);
                    } else {
                        event.payload = val;
                    }
                    ui.dispatchEvent(event);
                }, () => {
                    ui.dispatchEvent(event);
                }
            );
            """.formatted(promiseBody, eventtype), args);
        return future;
    }

}
