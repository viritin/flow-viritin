package org.vaadin.firitin.components;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.Renderer;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentFocusable;
import org.vaadin.firitin.fluency.ui.FluentHasDataProvider;
import org.vaadin.firitin.fluency.ui.FluentHasSize;
import org.vaadin.firitin.fluency.ui.FluentHasValidation;
import org.vaadin.firitin.fluency.ui.FluentHasValue;
import org.vaadin.firitin.fluency.ui.internal.FluentHasAutofocus;
import org.vaadin.firitin.fluency.ui.internal.FluentHasLabel;

import java.util.Collection;

@SuppressWarnings("unchecked")
public class VTextField extends TextField implements FluentHasSize<VTextField>, FluentHasValidation<VTextField>,
        FluentFocusable<TextField, VTextField>,
        FluentHasValue<VTextField, ComponentValueChangeEvent<TextField, String>, String>, FluentComponent<VTextField>,
        FluentHasLabel<VTextField>, FluentHasAutofocus<VTextField> {

    public VTextField() {
        super();
    }

    public VTextField(String label) {
        super(label);
    }

    public VTextField withRequired(boolean required) {
        setRequired(required);
        return this;
    }

    public VTextField withPlaceholder(String placeholder) {
        setPlaceholder(placeholder);
        return this;
    }

    public VTextField withPattern(String pattern) {
        setPattern(pattern);
        return this;
    }

    @Override
    public void setValue(String value) {
        // avoid NPE's with null values and just show empty fields
        // like all users would expect
        if(value == null) {
            value = "";
        }
        super.setValue(value);
    }

}
