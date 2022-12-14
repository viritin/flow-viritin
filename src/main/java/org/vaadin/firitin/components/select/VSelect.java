package org.vaadin.firitin.components.select;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializablePredicate;
import org.vaadin.firitin.fluency.ui.*;
import org.vaadin.firitin.fluency.ui.internal.FluentHasLabel;

import java.util.Collection;
import java.util.stream.Stream;

public class VSelect<T> extends Select<T> implements
        FluentHasComponents<VSelect<T>>, FluentHasSize<VSelect<T>>, FluentHasValidation<VSelect<T>>,
        FluentHasStyle<VSelect<T>>, FluentFocusable<Select<T>, VSelect<T>>, FluentHasValueAndElement<VSelect<T>, AbstractField.ComponentValueChangeEvent<Select<T>, T>, T>, FluentHasLabel<VSelect<T>>,
        FluentHasHelper<VSelect<T>>, FluentHasTooltip<VSelect<T>> {

    public VSelect() {
        super();
    }

    public VSelect(String label, Collection<T> items) {
        super();
        setLabel(label);
        setItems(items);
    }

    public VSelect(String label, T... items) {
        super(items);
        setLabel(label);
    }

    public VSelect(String label) {
        super();
        setLabel(label);
    }

    public VSelect<T> withRenderer(ComponentRenderer<? extends Component, T> renderer) {
        setRenderer(renderer);
        return this;
    }

    public VSelect<T> withTextRenderer(ItemLabelGenerator<T> itemLabelGenerator) {
        setTextRenderer(itemLabelGenerator);
        return this;
    }

    public VSelect<T> withEmptySelectionAllowed(boolean emptySelectionAllowed) {
        setEmptySelectionAllowed(emptySelectionAllowed);
        return this;
    }

    public VSelect<T> withEmptySelectionCaption(String emptySelectionCaption) {
        setEmptySelectionCaption(emptySelectionCaption);
        return this;
    }

    public VSelect<T> withItemEnabledProvider(SerializablePredicate<T> itemEnabledProvider) {
        setItemEnabledProvider(itemEnabledProvider);
        return this;
    }

    public VSelect<T> withItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
        setItemLabelGenerator(itemLabelGenerator);
        return this;
    }

    public VSelect<T> withPlaceholder(String placeholder) {
        setPlaceholder(placeholder);
        return this;
    }

    public VSelect<T> withAutofocus(boolean autofocus) {
        setAutofocus(autofocus);
        return this;
    }

    public VSelect<T> with(Component... components) {
        add(components);
        return this;
    }

    public VSelect<T> withComponents(T afterItem, Component... components) {
        addComponents(afterItem, components);
        return this;
    }

    public VSelect<T> withPrependComponents(T beforeItem, Component... components) {
        prependComponents(beforeItem, components);
        return this;
    }

    public VSelect<T> withDataProvider(DataProvider<T, ?> dataProvider) {
        setDataProvider(dataProvider);
        return this;
    }

    public VSelect<T> withItems(Collection<T> items) {
        setItems(items);
        return this;
    }

    @Deprecated
    public VSelect<T> withItems(Stream<T> streamOfItems) {
        setItems(streamOfItems);
        return this;
    }

    public VSelect<T> withItems(T... items) {
        setItems(items);
        return this;
    }
}
