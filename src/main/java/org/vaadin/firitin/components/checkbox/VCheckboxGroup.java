package org.vaadin.firitin.components.checkbox;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.selection.MultiSelectionListener;
import com.vaadin.flow.function.SerializablePredicate;
import org.vaadin.firitin.fluency.ui.*;
import org.vaadin.firitin.fluency.ui.internal.FluentHasLabel;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public class VCheckboxGroup<T> extends CheckboxGroup<T> implements
        FluentComponent<VCheckboxGroup<T>>, FluentHasStyle<VCheckboxGroup<T>>,
        FluentHasValueAndElement<VCheckboxGroup<T>, ComponentValueChangeEvent<CheckboxGroup<T>, Set<T>>, Set<T>>,
        FluentHasSize<VCheckboxGroup<T>>, FluentHasValidation<VCheckboxGroup<T>>,
        FluentHasLabel<VCheckboxGroup<T>>, FluentHasHelper<VCheckboxGroup<T>>,
        FluentHasTooltip<VCheckBox> {

    public VCheckboxGroup<T> withDataProvider(DataProvider<T, ?> dataProvider) {
        setDataProvider(dataProvider);
        return this;
    }

    public VCheckboxGroup<T> withUpdateSelection(Set<T> addedItems, Set<T> removedItems) {
        updateSelection(addedItems, removedItems);
        return this;
    }

    public VCheckboxGroup<T> withSelectionListener(MultiSelectionListener<CheckboxGroup<T>, T> listener) {
        addSelectionListener(listener);
        return this;
    }

    public VCheckboxGroup<T> withReadOnly(boolean readOnly) {
        setReadOnly(readOnly);
        return this;
    }

    public VCheckboxGroup<T> withThemeVariants(CheckboxGroupVariant... variants) {
        addThemeVariants(variants);
        return this;
    }

    public VCheckboxGroup<T> withItemEnabledProvider(SerializablePredicate<T> itemEnabledProvider) {
        setItemEnabledProvider(itemEnabledProvider);
        return this;
    }

    public VCheckboxGroup<T> withItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
        setItemLabelGenerator(itemLabelGenerator);
        return this;
    }

    public VCheckboxGroup<T> withErrorMessage(String errorMessage) {
        setErrorMessage(errorMessage);
        return this;
    }

    public VCheckboxGroup<T> withRequired(boolean required) {
        setRequired(required);
        return this;
    }

    public VCheckboxGroup<T> withInvalid(boolean invalid) {
        setInvalid(invalid);
        return this;
    }

    public VCheckboxGroup<T> withItems(Collection<T> items) {
        setItems(items);
        return this;
    }

    @Deprecated
    public VCheckboxGroup<T> withItems(Stream<T> streamOfItems) {
        setItems(streamOfItems);
        return this;
    }

    public VCheckboxGroup<T> withItems(T... items) {
        setItems(items);
        return this;
    }
}
