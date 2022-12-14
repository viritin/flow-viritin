package org.vaadin.firitin.components.customfield;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.customfield.CustomField;
import org.vaadin.firitin.fluency.ui.*;
import org.vaadin.firitin.fluency.ui.internal.FluentHasLabel;

public abstract class VCustomField<T> extends CustomField<T> implements FluentHasSize<VCustomField<T>>,
        FluentHasValidation<VCustomField<T>>,
        FluentFocusable<CustomField, VCustomField<T>>, FluentHasValueAndElement<VCustomField<T>, ComponentValueChangeEvent<CustomField<T>, T>, T>,
        FluentComponent<VCustomField<T>>, FluentHasLabel<VCustomField<T>>, FluentHasHelper<VCustomField<T>>, FluentHasTooltip<VCustomField<T>> {

    public VCustomField() {
        super();
    }

    public VCustomField(T defaultValue) {
        super(defaultValue);
    }

    public VCustomField<T> withInvalid(boolean invalid) {
        setInvalid(invalid);
        return this;
    }

    public VCustomField<T> withErrorMessage(String errorMessage) {
        setErrorMessage(errorMessage);
        return this;
    }

}
