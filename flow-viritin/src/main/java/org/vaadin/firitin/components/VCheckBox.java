package org.vaadin.firitin.components;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.checkbox.Checkbox;
import org.vaadin.firitin.fluency.ui.*;
import org.vaadin.firitin.fluency.ui.internal.FluentHasAutofocus;
import org.vaadin.firitin.fluency.ui.internal.FluentHasLabel;

public class VCheckBox extends Checkbox implements FluentHasSize<VCheckBox>, FluentFocusable<Checkbox, VCheckBox>,
        FluentClickNotifier<Checkbox, VCheckBox>, FluentHasEnabled<VCheckBox>, FluentComponent<VCheckBox>,
        FluentHasLabel<VCheckBox>, FluentHasAutofocus<VCheckBox>, FluentHasStyle<VCheckBox>,
        FluentHasValueAndElement<VCheckBox, ComponentValueChangeEvent<Checkbox, Boolean>, Boolean> {

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

    public VCheckBox withAriaLabel(String ariaLabel) {
        setAriaLabel(ariaLabel);
        return this;
    }

    public VCheckBox withIndeterminate(boolean indeterminate) {
        setIndeterminate(indeterminate);
        return this;
    }
}
