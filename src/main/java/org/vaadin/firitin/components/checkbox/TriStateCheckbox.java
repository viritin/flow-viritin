package org.vaadin.firitin.components.checkbox;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;

/**
 * A checkbox that reports its value as null when in indeterminate mode. When user
 * toggles the checkbox, it will switch from non-checked (false) to a non-indeterminate
 * state (null) and from there again to checked (true).
 */
public class TriStateCheckbox extends CustomField<Boolean> {

    private final Checkbox impl;
    boolean indeterminate = true;

    /**
     * Creates an IndeterminateCheckbox with the initial state set to indeterminate (null).
     */
    public TriStateCheckbox() {
        this.impl = new Checkbox();
        impl.setIndeterminate(true);
        impl.addValueChangeListener(event -> {
            if(!indeterminate && !event.getOldValue() && event.isFromClient()) {
                // if user unchecks the checkbox, we set it to indeterminate again
                impl.setValue(false);
                impl.setIndeterminate(true);
                indeterminate = true;
            } else {
                indeterminate = false;
            }
        });
       add(impl);
    }

    /**
     * Creates an IndeterminateCheckbox with the given label.
     *
     * @param label the label for the checkbox
     */
    public TriStateCheckbox(String label) {
        this();
        setLabel(label);
    }

    @Override
    protected Boolean generateModelValue() {
        if (indeterminate) {
            return null; // null means indeterminate
        } else {
            return impl.getValue();
        }
    }

    @Override
    protected void setPresentationValue(Boolean value) {
        if(value == null) {
            impl.setIndeterminate(true);
            indeterminate = true;
        } else {
            impl.setIndeterminate(false);
            impl.setValue(value);
            indeterminate = false;
        }
    }

}
