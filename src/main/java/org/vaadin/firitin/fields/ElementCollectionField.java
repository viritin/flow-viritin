package org.vaadin.firitin.fields;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.form.AbstractForm;
import org.vaadin.stefan.table.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * A field to pick a list of objects from a superset.
 * <p>Usage example: selecting runners to a relay team.</p>
 */
public class ElementCollectionField<T> extends Composite<VerticalLayout>
        implements HasValue<HasValue.ValueChangeEvent<List<T>>, List<T>>, HasSize {

    private final Class<T> clazz;
    private final Class<?> editorClass;

    private List<T> value;

    protected Table table;
    private T newItem;
    private Binder newItemForm;

    private SerializableSupplier<Object> editorInstantiator;

    public ElementCollectionField(Class<T> clazz, Class<?> editorClass) {
        this.clazz = clazz;
        this.editorClass = editorClass;
        table = new Table();
        getContent().setPadding(false);
        getContent().add(table);
    }

    public ElementCollectionField<T> withEditorInstantiator(SerializableSupplier<Object> editorInstantiator) {
        this.editorInstantiator = editorInstantiator;
        return this;
    }

    protected void configureColumneHeaders() {
        Field[] declaredFields = clazz.getDeclaredFields();
        TableRow tr = new TableRow();
        for (int i = 0; i < declaredFields.length; i++) {
            tr.addHeaderCells(StringUtils.capitalize(
                    StringUtils.join(
                            StringUtils.splitByCharacterTypeCamelCase(
                                    declaredFields[i].getName()), " ")));
        }
        table.addRows(tr);
    }

    protected void addDeleteButtonColumn(TableRow row, T item) {
        TableDataCell cell = row.addDataCell();
        cell.add(new VButton(VaadinIcon.TRASH.create(), event -> {
            value.remove(item);
            row.getParent().ifPresent(p -> ((Table) p).removeRows(row));
        }));
    }
    private void fireValueChange() {
        fireEvent(new AbstractField.ComponentValueChangeEvent<ElementCollectionField, List<T>>(this,this,null,true));
    };


    @Override
    public void setValue(List<T> value) {
        this.value = value;
        table.removeAllRows();
        configureColumneHeaders();
        value.forEach(this::addNewRow);
        addRowForNewItem();
    }

    private void addRowForNewItem() {
        newItem = instantiateNewItem();
        newItemForm = addNewRow(newItem);
        // hide the delete button until actually added to the collection
        getLastCell().setVisible(false);
    }

    private TableCell getLastCell() {
        TableRow lastRow = table.getRows().get(table.getRows().size() - 1);
        return lastRow.getCells().get(lastRow.getCells().size() - 1);
    }

    private BeanValidationBinder<T> addNewRow(T item) {
        BeanValidationBinder<T> binder = new BeanValidationBinder<>(clazz);
        Object row = instantiateRowObject();
        binder.bindInstanceFields(row);
        binder.setBean(item);
        TableRow tr;
        if (row instanceof AbstractForm) {
            AbstractForm form = (AbstractForm) row;
            ((AbstractForm<T>) row).setEntity(item);
            tr = (TableRow) form.getContent().getChildren().findFirst().get();
        } else {
            Field[] fields = editorClass.getDeclaredFields();
            Component[] components = new Component[fields.length];
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                try {
                    components[i] =  (Component) field.get(row);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            tr = new TableRow(components);
        }
        binder.setBean(item);
        addDeleteButtonColumn(tr, item);
        binder.addValueChangeListener(e -> {
            if(binder == newItemForm) {
                // TODO add as new row if valid
                if(e.isFromClient() && newItemForm.isValid()) {
                    value.add(newItem);
                    // display the delete button column
                    getLastCell().setVisible(true);
                    addRowForNewItem();
                    fireValueChange();
                }
            } else {
                fireValueChange();
            }
        });
        table.addRows(tr);
        return binder;
    }

    protected T instantiateNewItem() {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Object instantiateRowObject() {
        try {
            if(editorInstantiator != null) {
                return editorInstantiator.get();
            }
            return editorClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<T> getValue() {
        return value;
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<? super ValueChangeEvent<List<T>>> listener) {
        @SuppressWarnings("rawtypes")
        ComponentEventListener componentListener = event -> {
            AbstractField.ComponentValueChangeEvent<ElementCollectionField, List<T>> valueChangeEvent = (AbstractField.ComponentValueChangeEvent<ElementCollectionField, List<T>>) event;
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
