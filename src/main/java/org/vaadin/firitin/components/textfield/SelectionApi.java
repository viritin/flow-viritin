package org.vaadin.firitin.components.textfield;

import com.vaadin.flow.component.HasElement;

public interface SelectionApi extends HasElement {

    /**
     * Selects all text in the field and moves the focus to the field.
     */
    default void selectAll() {
        getElement().executeJs("this.inputElement.select();");
    };

    /**
     * Sets the start and end positions of the current text selection.
     * <p>Note, that the method simply proxies the parameters to the similarly named method in the browser, without sanity checks or any synchronization.</p>
     *
     * @param selectionStart The 0-based index of the first selected character. An index greater than the length of the element's value is treated as pointing to the end of the value.
     * @param selectionEnd The 0-based index for the end of the selection (exclusive). An index greater than the length of the element's value is treated as pointing to the end of the value.
     */
    default void setSelectionRange(int selectionStart, int selectionEnd) {
        getElement().executeJs("this.inputElement.setSelectionRange($0,$1);",selectionStart,selectionEnd);
    };

}
