package org.vaadin.firitin.components.checkbox;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.theme.aura.Aura;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * A toggle button that is essentially a styled checkbox. CSS derived from similarly named Vaadin
 * add-on to be compatible with the latest Vaadin 24.7.
 */
@StyleSheet("context://frontend/org/vaadin/firitin/components/toggle-button.css")
public class ToggleButton extends VCheckBox {

    public static Class<?> baseTheme = Lumo.class;

    public ToggleButton() {
    }

    public ToggleButton(boolean initialValue) {
        super(initialValue);
    }

    public ToggleButton(String labelText, boolean initialValue) {
        super(labelText, initialValue);
    }

    public ToggleButton(String label, ValueChangeListener<ComponentValueChangeEvent<Checkbox, Boolean>> listener) {
        super(label, listener);
    }

    public ToggleButton(String labelText) {
        super(labelText);
    }

    protected void addClassName() {

        if(baseTheme == Aura.class) {
            setClassName("toggle-button-aura");
        } else {
            setClassName("toggle-button");
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        addClassName();
    }
}
