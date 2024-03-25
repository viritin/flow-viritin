package org.vaadin.firitin.components.textfield;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.function.SerializableConsumer;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

import java.io.Serializable;

public interface SelectionApi extends HasElement {

    /**
     * Selects all text in the field and moves the focus to the field.
     */
    default void selectAll() {
        getElement().executeJs("setTimeout(()=>{this.inputElement.select()},0);");
    };

    /**
     * Sets the start and end positions of the current text selection.
     * <p>Note, that the method simply proxies the parameters to the similarly named method in the browser, without sanity checks or any synchronization.</p>
     *
     * @param selectionStart The 0-based index of the first selected character. An index greater than the length of the element's value is treated as pointing to the end of the value.
     * @param selectionEnd The 0-based index for the end of the selection (exclusive). An index greater than the length of the element's value is treated as pointing to the end of the value.
     */
    default void setSelectionRange(int selectionStart, int selectionEnd) {
        getElement().executeJs("setTimeout(()=>{this.inputElement.setSelectionRange($0,$1)},0);",selectionStart,selectionEnd);
    };

    /**
     * Sets the cursor position to given index.
     *
     * @param cursorPosition the cursor position
     */
    default void setCursorPosition(int cursorPosition) {
        setSelectionRange(cursorPosition, cursorPosition);
    };

    interface SelectionRangeCallback extends Serializable {
        /**
         * This method is called with the current selection of the
         * field.
         *
         * @param start the start of the selection (inclusive)
         * @param end the end of the selection (inclusive)
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
                    if (jsonValue instanceof JsonObject) {
                        JsonObject jso = (JsonObject) jsonValue;
                        callback.selectionRange(
                                (int) jso.getNumber("start"),
                                (int) jso.getNumber("end"),
                                jso.getString("content")
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
