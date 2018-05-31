package org.vaadin.firitin.components;

import java.util.Collection;

import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentFocusable;
import org.vaadin.firitin.fluency.ui.FluentHasDataProvider;
import org.vaadin.firitin.fluency.ui.FluentHasSize;
import org.vaadin.firitin.fluency.ui.FluentHasValidation;
import org.vaadin.firitin.fluency.ui.FluentHasValue;
import org.vaadin.firitin.fluency.ui.internal.FluentHasAutofocus;
import org.vaadin.firitin.fluency.ui.internal.FluentHasLabel;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ItemLabelGenerator;

@SuppressWarnings("unchecked")
public class VComboBox<T> extends ComboBox<T> implements FluentHasSize<VComboBox<T>>, FluentHasValidation<VComboBox<T>>,
        FluentHasDataProvider<VComboBox<T>, T>, FluentFocusable<ComboBox<T>, VComboBox<T>>,
        FluentHasValue<VComboBox<T>, ComponentValueChangeEvent<ComboBox<T>, T>, T>, FluentComponent<VComboBox<T>>,
        FluentHasLabel<VComboBox<T>>, FluentHasAutofocus<VComboBox<T>> {

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

    public VComboBox<T> withFilteredItems(T... filteredItems) {
        setFilteredItems(filteredItems);
        return this;
    }

    public VComboBox<T> withFilteredItems(Collection<T> filteredItems) {
        setFilteredItems(filteredItems);
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

    @Override
    public VComboBox<T> withId(String id) {
        setId(id);
        return this;
    }
}
