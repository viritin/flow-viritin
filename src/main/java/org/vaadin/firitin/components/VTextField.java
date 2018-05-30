package org.vaadin.firitin.components;

import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.firitin.fluency.ui.*;

public class VTextField extends TextField
        implements FluentHasSize<VTextField>, FluentHasValidation<VTextField>,
        FluentHasStyle<VTextField>, FluentFocusable<TextField, VTextField>, FluentAttachNotifier<VTextField>, FluentDetachNotifier<VTextField> // from the parent classes
    // TODO Fluent implementation of HasValueChangeMode, HasPrefixAndSuffix, InputNotifier, KeyNotifier, CompositionNotifier, HasAutocomplete, HasAutocapitalize, HasAutocorrect
{
    public VTextField() {
        super();
    }

    public VTextField(String label) {
        super(label);
    }

    public VTextField(String label, String placeholder) {
        super(label, placeholder);
    }

    public VTextField(String label, String initialValue, String placeholder) {
        super(label, initialValue, placeholder);
    }

    public VTextField(ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener) {
        super(listener);
    }

    public VTextField(String label, ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener) {
        super(label, listener);
    }

    public VTextField(String label, String initialValue, ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener) {
        super(label, initialValue, listener);
    }
}
