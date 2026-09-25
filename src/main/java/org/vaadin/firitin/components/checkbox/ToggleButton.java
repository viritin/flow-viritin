package org.vaadin.firitin.components.checkbox;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * A toggle button that is essentially a styled checkbox. CSS derived from similarly named Vaadin
 * add-on to be compatible with the latest Vaadin 24.7.
 * <p>
 * Both Lumo and Aura are supported: the stylesheet detects the active theme in
 * the browser (Aura's {@code --vaadin-aura-theme} marker via a style container
 * query), so no configuration is needed.
 * <p>
 * By default the label is shown first and the switch after it, like in the
 * settings lists of iOS and Android: a switch is typically an instantly
 * applied setting, so one reads what it is about before its state. When mixed
 * with regular checkboxes in a form, it may be better to align the controls
 * the same way and put the switch first by removing the reverse variant:
 * {@code removeThemeVariants(CheckboxVariant.AURA_REVERSE)} (works with Lumo
 * too).
 */
@StyleSheet("context://assets/org/vaadin/firitin/components/toggle-button.css")
public class ToggleButton extends VCheckBox {

    /**
     * @deprecated No longer used, the theme is now detected automatically in
     * the browser. Will be removed in a future version.
     */
    @Deprecated(forRemoval = true)
    public static Class<?> baseTheme = Lumo.class;

    {
        addClassName("toggle-button");
        // Label first, switch after it. Remove the variant to put the switch
        // first. Works with Lumo too, implemented in toggle-button.css.
        addThemeVariants(CheckboxVariant.AURA_REVERSE);
    }

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
}
