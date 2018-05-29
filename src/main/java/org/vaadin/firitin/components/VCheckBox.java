package org.vaadin.firitin.components;

import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentFocusable;
import org.vaadin.firitin.fluency.ui.FluentHasEnabled;
import org.vaadin.firitin.fluency.ui.FluentHasSize;
import org.vaadin.firitin.fluency.ui.FluentHasValue;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.checkbox.Checkbox;

public class VCheckBox extends Checkbox implements FluentHasSize<VCheckBox>, FluentFocusable<Checkbox, VCheckBox>,
        FluentClickNotifier<Checkbox, VCheckBox>,
        FluentHasValue<VCheckBox, ComponentValueChangeEvent<Checkbox, Boolean>, Boolean>, FluentHasEnabled<VCheckBox>,
        FluentComponent<VCheckBox> {

    public VCheckBox() {
        super();
    }

    public VCheckBox(boolean initialValue) {
        super(initialValue);
    }

    public VCheckBox(String labelText, boolean initialValue) {
        super(labelText, initialValue);
    }

    public VCheckBox(String label, ValueChangeListener<ComponentValueChangeEvent<Checkbox, Boolean>> listener) {
        super(label, listener);
    }

    public VCheckBox(String labelText) {
        super(labelText);
    }

    public VCheckBox withLabel(String label) {
        setLabel(label);
        return this;
    }

    public VCheckBox withAriaLabel(String ariaLabel) {
        setAriaLabel(ariaLabel);
        return this;
    }

    public VCheckBox withAutofocus(boolean autofocus) {
        setAutofocus(autofocus);
        return this;
    }

    public VCheckBox withIndeterminate(boolean indeterminate) {
        setIndeterminate(indeterminate);
        return this;
    }

}
