package org.vaadin.firitin.components.textfield;

import com.vaadin.flow.component.shared.HasTextSelection;
import com.vaadin.flow.function.SerializableConsumer;

import java.io.Serializable;
import tools.jackson.databind.node.ObjectNode;

/**
 * Text selection helpers for field components wrapping a native input.
 * <p>
 * Since Vaadin 25.4 the setters ({@link #setSelectionRange(int, int)},
 * {@link #selectAll()} and {@link #setCursorPosition(int)}) come from the core
 * {@link HasTextSelection} interface. This interface adds asynchronous getters
 * for the current selection and cursor position, and makes the selection
 * setters available also for fields that don't support them in core (like
 * EmailField, NumberField, IntegerField and DatePicker).
 * </p>
 */
public interface SelectionApi extends HasTextSelection {

    interface SelectionRangeCallback extends Serializable {
        /**
         * This method is called with the current selection of the
         * field.
         *
         * @param start the start of the selection (inclusive)
         * @param end the end of the selection (exclusive)
         * @param content the string content currently selected
         */
        void selectionRange(int start, int end, String content);
    }

    /**
     * Asynchronously gets the current selection for this field.
     *
     * @param callback the callback to notify the selection
     */
    default void getSelectionRange(SelectionRangeCallback callback) {
        getElement().executeJs("" +
                "var res = {};" +
                "res.start = this.inputElement.selectionStart;" +
                "res.end = this.inputElement.selectionEnd;" +
                "res.content = this.inputElement.value.substring(res.start, res.end);" +
                "return res;").then(jsonValue -> {
                    if (jsonValue instanceof ObjectNode) {
                        ObjectNode jso = (ObjectNode) jsonValue;
                        callback.selectionRange(
                                jso.get("start").asInt(),
                                jso.get("end").asInt(),
                                jso.get("content").asString()
                        );
                    }
                });
    };

    /**
     * Asynchronously gets the current cursor position for this field.
     *
     * @param callback the callback to notify the position
     */
    default void getCursorPosition(SerializableConsumer<Integer> callback) {
        this.getSelectionRange( (start, e, c) -> {
            callback.accept(start);
        });
    };
}
