package org.vaadin.firitin.components.textfield;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import org.vaadin.firitin.fluency.ui.*;
import org.vaadin.firitin.fluency.ui.internal.FluentHasAutofocus;
import org.vaadin.firitin.fluency.ui.internal.FluentHasLabel;

public class VTextArea extends TextArea
        implements FluentHasSize<VTextArea>, FluentHasValidation<VTextArea>, FluentFocusable<TextArea, VTextArea>,
        FluentHasValue<VTextArea, ComponentValueChangeEvent<TextArea, String>, String>,
        FluentComponent<VTextArea>, FluentHasLabel<VTextArea>, FluentHasAutofocus<VTextArea>, FluentHasStyle<VTextArea>, FluentHasPrefixAndSuffix<VTextArea> {

    public VTextArea() {
        super();
    }

    public VTextArea(String label) {
        super(label);
    }

    public VTextArea(String label, String placeholder) {
        super(label, placeholder);
    }

    public VTextArea(String label, String initialValue, String placeholder) {
        super(label, initialValue, placeholder);
    }

    public VTextArea(ValueChangeListener<? super ComponentValueChangeEvent<TextArea, String>> listener) {
        super(listener);
    }

    public VTextArea(String label,
                     ValueChangeListener<? super ComponentValueChangeEvent<TextArea, String>> listener) {
        super(label, listener);
    }

    public VTextArea(String label, String initialValue,
                     ValueChangeListener<? super ComponentValueChangeEvent<TextArea, String>> listener) {
        super(label, initialValue, listener);
    }

    public VTextArea withRequired(boolean required) {
        setRequired(required);
        return this;
    }

    public VTextArea withPlaceholder(String placeholder) {
        setPlaceholder(placeholder);
        return this;
    }

    @Override
    public void setValue(String value) {
        // avoid NPE's with null values and just show empty fields
        // like all users would expect
        if (value == null) {
            value = "";
        }
        super.setValue(value);
    }

    public VTextArea withThemeVariants(TextAreaVariant... variants) {
        addThemeVariants(variants);
        return this;
    }

}
