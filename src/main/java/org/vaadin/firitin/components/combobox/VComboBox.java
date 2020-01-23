package org.vaadin.firitin.components.combobox;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.renderer.Renderer;
import org.vaadin.firitin.fluency.ui.*;
import org.vaadin.firitin.fluency.ui.internal.FluentHasAutofocus;
import org.vaadin.firitin.fluency.ui.internal.FluentHasLabel;

import java.util.Collection;

@SuppressWarnings("unchecked")
public class VComboBox<T> extends ComboBox<T> implements FluentHasSize<VComboBox<T>>, FluentHasValidation<VComboBox<T>>,
        /*FluentHasDataProvider<VComboBox<T>, T>,*/ FluentHasItems<VComboBox<T>, T>, FluentFocusable<ComboBox<T>, VComboBox<T>>,
        FluentComponent<VComboBox<T>>, FluentHasLabel<VComboBox<T>>, FluentHasAutofocus<VComboBox<T>>,
        FluentHasStyle<VComboBox<T>>,
        FluentHasValueAndElement<VComboBox<T>, ComponentValueChangeEvent<ComboBox<T>, T>, T> {

    public VComboBox() {
        super();
    }

    public VComboBox(String label, Collection<T> items) {
        super(label, items);
    }

    public VComboBox(String label, T... items) {
        super(label, items);
    }

    public VComboBox(String label) {
        super(label);
    }

    public VComboBox<T> withRenderer(Renderer<T> renderer) {
        setRenderer(renderer);
        return this;
    }

    public VComboBox<T> withItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
        setItemLabelGenerator(itemLabelGenerator);
        return this;
    }

    public VComboBox<T> withOpened(boolean opened) {
        setOpened(opened);
        return this;
    }

    public VComboBox<T> withAllowCustomValue(boolean allowCustomValue) {
        setAllowCustomValue(allowCustomValue);
        return this;
    }

    public VComboBox<T> withPreventInvalidInput(boolean preventInvalidInput) {
        setPreventInvalidInput(preventInvalidInput);
        return this;
    }

    public VComboBox<T> withRequired(boolean required) {
        setRequired(required);
        return this;
    }

    public VComboBox<T> withPlaceholder(String placeholder) {
        setPlaceholder(placeholder);
        return this;
    }

    public VComboBox<T> withPattern(String pattern) {
        setPattern(pattern);
        return this;
    }
}
