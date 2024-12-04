package org.vaadin.firitin.components.grid;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.selection.MultiSelect;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.data.selection.MultiSelectionListener;
import com.vaadin.flow.shared.Registration;

import java.util.Set;

/** A grid that implements {@link MultiSelect} interface.  */
public class GridMultiSelect<T> extends VGrid<T> implements MultiSelect<Grid<T>, T> {

    public GridMultiSelect() {
        setSelectionMode(SelectionMode.SINGLE);
    }

    public GridMultiSelect(int pageSize) {
        super(pageSize);
        setSelectionMode(SelectionMode.MULTI);
    }

    public GridMultiSelect(Class<T> beanType) {
        super(beanType);
        setSelectionMode(SelectionMode.MULTI);
    }

    public GridMultiSelect(Class<T> beanType, boolean autoCreateColumns) {
        super(beanType, autoCreateColumns);
        setSelectionMode(SelectionMode.MULTI);
    }


    @Override
    public Registration addValueChangeListener(ValueChangeListener<? super AbstractField.ComponentValueChangeEvent<Grid<T>, Set<T>>> listener) {
        return asMultiSelect().addSelectionListener(new MultiSelectionListener<Grid<T>, T>() {
            @Override
            public void selectionChange(MultiSelectionEvent<Grid<T>, T> event) {
                listener.valueChanged(event);
            }
        });
    }

    @Override
    public void updateSelection(Set<T> addedItems, Set<T> removedItems) {
        asMultiSelect().updateSelection(addedItems, removedItems);
    }

    @Override
    public Registration addSelectionListener(MultiSelectionListener<Grid<T>, T> listener) {
        return asMultiSelect().addSelectionListener(listener);
    }

}
