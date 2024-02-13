package org.vaadin.firitin.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.Element;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

/**
 * Helper methods to consume asynchronous JS APIs without extra JS
 * trickery. The given script bodies are executed on the UI element
 * and wrapped into a promise, either using Promise with resolve
 * and reject exposed to your script body or using an anonymous
 * async function.
 */
public class JsPromise {

    private static final ObjectMapper jackson = new ObjectMapper();

    /**
     * Asynchronously returns a string from the client side. The JS given for this
     * method is a JS Promise body with "resolve" and "reject" defined for returning
     * the value or failing the request.
     *
     * @param promiseBody a JS snippet that resolves a promise
     * @param args        the extra arguments interpolated into JS as in Element.executeJs
     * @return the string as {@link CompletableFuture}
     */
    public static CompletableFuture<String> resolveString(String promiseBody, Serializable... args) {
        return resolve(promiseBody, String.class, args);
    }

    /**
     * Executes given JS snippet in an async method, making it possible to use
     * "await" keyword to consume promises in an easier way. The return value
     * is passed back to the server side as a {@link CompletableFuture}.
     *
     * @param asyncMethodBody the JS method body
     * @param args            the extra arguments interpolated into JS as in Element.executeJs
     * @return the future to get the value
     */
    public static CompletableFuture<String> computeString(String asyncMethodBody, Serializable... args) {
        return compute(asyncMethodBody, String.class, args);
    }

    /**
     * Executes given JS snippet in an async method, making it possible to use
     * "await" keyword to consume promises in an easier way. The return value
     * is passed back to the server side as a {@link CompletableFuture}.
     *
     * @param asyncMethodBody the JS method body
     * @param args            the extra arguments interpolated into JS as in Element.executeJs
     * @return the future to get the value
     */
    public static CompletableFuture<Integer> computeInteger(String asyncMethodBody, Serializable... args) {
        return compute(asyncMethodBody, Integer.class, args);
    }

    /**
     * Executes given JS snippet in an async method, making it possible to use
     * "await" keyword to consume promises in an easier way. The return value
     * is passed back to the server side as a {@link CompletableFuture}.
     *
     * @param asyncMethodBody the JS method body
     * @param args            the extra arguments interpolated into JS as in Element.executeJs
     * @return the future to get the value
     */
    public static CompletableFuture<Double> computeDouble(String asyncMethodBody, Serializable... args) {
        return compute(asyncMethodBody, Double.class, args);
    }

    /**
     * Executes given JS snippet in an async method, making it possible to use
     * "await" keyword to consume promises in an easier way. The return value
     * is passed back to the server side as a {@link CompletableFuture}.
     *
     * @param asyncMethodBody the JS method body
     * @param args            the extra arguments interpolated into JS as in Element.executeJs
     * @return the future to get the value
     */
    public static CompletableFuture<Boolean> computeBoolean(String asyncMethodBody, Serializable... args) {
        return compute(asyncMethodBody, Boolean.class, args);
    }

    /**
     * Asynchronously returns a boolean from the client side. The JS given for this
     * method is a JS Promise body with "resolve" and "reject" defined for returning
     * the value or failing the request.
     *
     * @param promiseBody a JS snippet that resolves a promise
     * @param args        the extra arguments interpolated into JS as in Element.executeJs
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
     * @param args        the extra arguments interpolated into JS as in Element.executeJs
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
     * @param args        the extra arguments interpolated into JS as in Element.executeJs
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
     * @param args        the extra arguments interpolated into JS as in Element.executeJs
     * @return the future to get the value
     */
    public static <T> CompletableFuture<T> resolve(String promiseBody, Class<T> returnType, Serializable... args) {
        CompletableFuture<T> future = new CompletableFuture<>();
        UI current = UI.getCurrent();
        Element el = current.getElement();
        el.executeJs("""
                const ui = this;
                const executeAsyncPromise = new Promise((resolve, reject) => {
                    try {
                    %s
                    } catch (error) {
                        reject(error);
                    }
                }).then(val => {
                    if(typeof val === 'object') {
                        return JSON.stringify(val);
                    } else {
                        return val;
                    }
                });
                return executeAsyncPromise;
                """.formatted(promiseBody), args).then(jsonValue -> {
            if (String.class.isAssignableFrom(returnType)) {
                future.complete((T) jsonValue.asString());
            } else if (Integer.class.isAssignableFrom(returnType)) {
                int number = (int) jsonValue.asNumber();
                future.complete((T) Integer.valueOf(number));
            } else if (Double.class.isAssignableFrom(returnType)) {
                double number = jsonValue.asNumber();
                future.complete((T) Double.valueOf(number));
            } else if (Boolean.class.isAssignableFrom(returnType)) {
                boolean b = jsonValue.asBoolean();
                future.complete((T) Boolean.valueOf(b));
            } else {
                try {
                    future.complete(jackson.readValue(jsonValue.asString(), returnType));
                } catch (JsonProcessingException ex) {
                    future.completeExceptionally(ex);
                }
            }
        }, error -> {
            future.completeExceptionally(new RuntimeException(error));
        });
        return future;
    }

    /**
     * Executes given JS snippet wrapped in an async method, making it possible to use
     * "await" keyword to consume promises in an easier way. Once resolved, the return value is
     * passed back to the server in a {@link CompletableFuture}.
     *
     * @param asyncMethodBody the JS method body
     * @param returnType      the return value type: String, Integer, Double, Boolean or an object that gets serialized to JSON and deserialized with default Jackson settings on the server side.
     * @param args            the extra arguments interpolated into JS as in Element.executeJs
     * @param <T>             the return type, if not a basic data type, the return parameter in browser is expected to be JSON that is then mapped to given type with Jackson
     * @return the future to get the value
     */
    public static <T> CompletableFuture<T> compute(String asyncMethodBody, Class<T> returnType, Serializable... args) {
        CompletableFuture<T> future = new CompletableFuture<>();
        UI current = UI.getCurrent();
        Element el = current.getElement();
        el.executeJs("""
                return (async () => {
                    %s
                }).apply(this, arguments).then(val => {
                    if(typeof val === 'object') {
                        return JSON.stringify(val);
                    } else {
                        return val;
                    }
                });
                """.formatted(asyncMethodBody), args).then(jsonValue -> {
            if (String.class.isAssignableFrom(returnType)) {
                future.complete((T) jsonValue.asString());
            } else if (Integer.class.isAssignableFrom(returnType)) {
                int number = (int) jsonValue.asNumber();
                future.complete((T) Integer.valueOf(number));
            } else if (Double.class.isAssignableFrom(returnType)) {
                double number = jsonValue.asNumber();
                future.complete((T) Double.valueOf(number));
            } else if (Boolean.class.isAssignableFrom(returnType)) {
                boolean b = jsonValue.asBoolean();
                future.complete((T) Boolean.valueOf(b));
            } else {
                try {
                    future.complete(jackson.readValue(jsonValue.asString(), returnType));
                } catch (JsonProcessingException ex) {
                    future.completeExceptionally(ex);
                }
            }
        }, error -> {
            future.completeExceptionally(new RuntimeException(error));
        });
        return future;
    }

}
