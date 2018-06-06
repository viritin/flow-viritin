package org.vaadin.firitin.components;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.renderer.TextRenderer;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasDataProvider;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;
import org.vaadin.firitin.fluency.ui.FluentHasValueAndElement;

public class VRadioButtonGroup<T> extends RadioButtonGroup<T> implements FluentHasDataProvider<VRadioButtonGroup<T>, T>,
        FluentHasStyle<VRadioButtonGroup<T>>, FluentComponent<VRadioButtonGroup<T>>,
        FluentHasValueAndElement<VRadioButtonGroup<T>, ComponentValueChangeEvent<RadioButtonGroup<T>, T>, T>
// TODO fluent HasItemsAndComponents<T>, SingleSelect<RadioButtonGroup<T>, T>
{
    public VRadioButtonGroup() {
        super();
    }

    public VRadioButtonGroup(String label) {
        super();
        add(new Label(label));
    }

    @Override
    public VRadioButtonGroup<T> withId(String id) {
        setId(id);
        return this;
    }

    public VRadioButtonGroup<T> withTextRenderer(ItemLabelGenerator<T> itemLabelGenerator) {
        setRenderer(new TextRenderer<>(itemLabelGenerator));
        return this;
    }
}
