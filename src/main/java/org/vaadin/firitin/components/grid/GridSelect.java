package org.vaadin.firitin.components.grid;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.shared.Registration;

/** A grid that implements {@link SingleSelect} interface.  */
public class GridSelect<T> extends VGrid<T> implements SingleSelect<Grid<T>, T> {

    public GridSelect() {
        setSelectionMode(SelectionMode.SINGLE);
    }

    public GridSelect(int pageSize) {
        super(pageSize);
        setSelectionMode(SelectionMode.SINGLE);
    }

    public GridSelect(Class<T> beanType) {
        super(beanType);
        setSelectionMode(SelectionMode.SINGLE);
    }

    public GridSelect(Class<T> beanType, boolean autoCreateColumns) {
        super(beanType, autoCreateColumns);
        setSelectionMode(SelectionMode.SINGLE);
    }

    @Override
    public void setValue(T value) {
        asSingleSelect().setValue(value);
    }

    @Override
    public T getValue() {
        return asSingleSelect().getValue();
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<? super AbstractField.ComponentValueChangeEvent<Grid<T>, T>> listener) {
        return asSingleSelect().addValueChangeListener(listener);
    }

}
