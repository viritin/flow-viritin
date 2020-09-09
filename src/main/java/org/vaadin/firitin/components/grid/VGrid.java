package org.vaadin.firitin.components.grid;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import org.vaadin.firitin.fluency.ui.*;

import java.util.Collection;
import java.util.stream.Stream;

public class VGrid<T> extends Grid<T>
        implements FluentComponent<VGrid<T>>, FluentHasStyle<VGrid<T>>, FluentHasSize<VGrid<T>>,
        FluentFocusable<Grid<T>, VGrid<T>>, FluentHasTheme<VGrid<T>> {

    public VGrid() {
        super();
    }

    public VGrid(int pageSize) {
        super(pageSize);
    }

    public VGrid(Class<T> beanType) {
        super(beanType);
    }

    public VGrid<T> withSelectionMode(Grid.SelectionMode selectionMode) {
        setSelectionMode(selectionMode);
        return this;
    }

    public VGrid<T> withSelectionModel(GridSelectionModel<T> selectionModel, Grid.SelectionMode selectionMode) {
        setSelectionModel(selectionModel, selectionMode);
        return this;
    }

    public VGrid<T> withProperties(String... propertyNames) {
        setColumns(propertyNames);
        return this;
    }

    public VGrid<T> withThemeVariants(GridVariant... variants) {
        addThemeVariants(variants);
        return this;
    }

    public VGrid<T> setDataProvider(CallbackDataProvider.FetchCallback<T, Void> fetchCallback,
                                    CallbackDataProvider.CountCallback<T, Void> countCallback) {
        setDataProvider(DataProvider.fromCallbacks(fetchCallback, countCallback));
        return this;
    }

    public VGrid<T> withItems(Collection<T> items) {
        setItems(items);
        return this;
    }

    @Deprecated
    public VGrid<T> withItems(Stream<T> streamOfItems) {
        setItems(streamOfItems);
        return this;
    }

    public VGrid<T> withItems(T... items) {
        setItems(items);
        return this;
    }
}
