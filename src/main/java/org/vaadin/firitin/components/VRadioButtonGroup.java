package org.vaadin.firitin.components;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import org.vaadin.firitin.fluency.ui.*;
import org.vaadin.firitin.fluency.ui.internal.FluentHasAutofocus;
import org.vaadin.firitin.fluency.ui.internal.FluentHasLabel;

public class VRadioButtonGroup<T> extends RadioButtonGroup<T> implements FluentHasDataProvider<VRadioButtonGroup<T>, T>,
        FluentHasStyle<VRadioButtonGroup<T>>, FluentComponent<VRadioButtonGroup<T>>
// TODO fluent HasItemsAndComponents<T>, SingleSelect<RadioButtonGroup<T>, T>, HasValueAndElement<ComponentValueChangeEvent<C, T>, T>
{
    public VRadioButtonGroup() {
        super();
    }

    @Override
    public VRadioButtonGroup withId(String id) {
        setId(id);
        return this;
    }
}
