package org.vaadin.firitin.components.radiobutton;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.function.SerializablePredicate;
import org.vaadin.firitin.fluency.ui.*;
import org.vaadin.firitin.fluency.ui.internal.FluentHasLabel;

public class VRadioButtonGroup<T> extends RadioButtonGroup<T> implements
        FluentHasStyle<VRadioButtonGroup<T>>, FluentComponent<VRadioButtonGroup<T>>, FluentHasValueAndElement<VRadioButtonGroup<T>, ComponentValueChangeEvent<RadioButtonGroup<T>, T>, T>, FluentHasValidation<VRadioButtonGroup<T>>,
        FluentHasHelper<VRadioButtonGroup<T>>, FluentHasLabel<VRadioButtonGroup<T>> {

    public VRadioButtonGroup() {
        super();
    }

    public VRadioButtonGroup(String label) {
        super();
        add(new Label(label));
    }

    public VRadioButtonGroup<T> withRenderer(ComponentRenderer<? extends Component, T> renderer) {
        setRenderer(renderer);
        return this;
    }

    public VRadioButtonGroup<T> withTextRenderer(ItemLabelGenerator<T> itemLabelGenerator) {
        setRenderer(new TextRenderer<>(itemLabelGenerator));
        return this;
    }

    public VRadioButtonGroup<T> withItemEnabledProvider(SerializablePredicate<T> itemEnabledProvider) {
        setItemEnabledProvider(itemEnabledProvider);
        return this;
    }

    public VRadioButtonGroup<T> withRequired(boolean required) {
        setRequired(required);
        return this;
    }
}
