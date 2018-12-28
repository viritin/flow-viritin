package org.vaadin.firitin.fields;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * A field to pick a list of objects from a superset.
 * <p>Usage example: selecting runners to a relay team.</p>
 */
public class SubListSelector<T> extends Composite<VerticalLayout>
        implements HasValue<HasValue.ValueChangeEvent<List<T>>, List<T>>, HasSize {

    private final Class<T> clazz;
    private List<T> availableOptions;

    private List<T> value;

    protected ComboBox<T> addToSelectionCb = new ComboBox<>();
    protected Grid<T> grid;

    public SubListSelector(Class<T> clazz) {
        this.clazz = clazz;
        grid = new Grid<>(clazz);
        configureGridColumns();
        addDeleteButtonColumn();

        addToSelectionCb.setPlaceholder("add entry to selection...");
        addToSelectionCb.addValueChangeListener(e -> {
            T newValue = e.getValue();
            if(newValue != null) {
                addToSelection(newValue);
            }
        });

        addToSelectionCb.setAllowCustomValue(true);
        addToSelectionCb.addCustomValueSetListener(e-> {
            T newInstance = createValueFromString(e.getDetail());
            if(newInstance != null) {
                availableOptions.add(newInstance);
                addToSelection(newInstance);
            }
        });

        getContent().setPadding(false);
        getContent().add(addToSelectionCb, grid);
    }
    
    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
        addToSelectionCb.setItemLabelGenerator(itemLabelGenerator);
    }

    protected T createValueFromString(String detail) {
        try {
            Constructor<T> constructor = clazz.getConstructor(String.class);
            return constructor.newInstance(detail);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addToSelection(T newValue) {
        value.add(newValue);
        grid.setItems(value);
        fireValueChange();
        addToSelectionCb.setValue(null);
        adjustAvailableItemsInCombobox();
    }

    protected void configureGridColumns() {
    }

    protected void addDeleteButtonColumn() {
        grid.addComponentColumn( t -> new Button("Remove", e-> {
            value.remove(t);
            grid.setItems(value);
            adjustAvailableItemsInCombobox();
            fireValueChange();
        }));

    }
    private void fireValueChange() {
        fireEvent(new AbstractField.ComponentValueChangeEvent<SubListSelector, List<T>>(this,this,null,true));
    };

    public void setAvailableOptions(List<T> options) {
        availableOptions = options;
        adjustAvailableItemsInCombobox();
    }

    private void adjustAvailableItemsInCombobox() {
        ArrayList<T> list = new ArrayList<>(availableOptions);
        if(value != null) {
            list.removeAll(value);
        }
        addToSelectionCb.setItems(list);
    }

    @Override
    public void setValue(List<T> value) {
        this.value = value;
        grid.setItems(this.value);
        adjustAvailableItemsInCombobox();
    }

    @Override
    public List<T> getValue() {
        return value;
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<? super ValueChangeEvent<List<T>>> listener) {
        @SuppressWarnings("rawtypes")
        ComponentEventListener componentListener = event -> {
            AbstractField.ComponentValueChangeEvent<SubListSelector, List<T>> valueChangeEvent = (AbstractField.ComponentValueChangeEvent<SubListSelector, List<T>>) event;
            listener.valueChanged(valueChangeEvent);
        };
        return addListener(AbstractField.ComponentValueChangeEvent.class,
                componentListener);
    }

    @Override
    public void setReadOnly(boolean readOnly) {

    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {

    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return false;
    }
}
