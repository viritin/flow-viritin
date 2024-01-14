package org.vaadin.firitin.fields;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.fields.internalhtmltable.Table;
import org.vaadin.firitin.fields.internalhtmltable.TableCell;
import org.vaadin.firitin.fields.internalhtmltable.TableDataCell;
import org.vaadin.firitin.fields.internalhtmltable.TableRow;
import org.vaadin.firitin.form.AbstractForm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * A field to pick a list of objects from a superset.
 *
 * <p>Usage example: selecting runners to a relay team.</p>
 */
public class ElementCollectionField<T> extends Composite<VerticalLayout>
        implements HasValue<HasValue.ValueChangeEvent<List<T>>, List<T>>, HasSize {

    private final Class<T> clazz;
    private final Class<?> editorClass;

    private List<T> value;

    /**
     * The table that is used to display the collection.
     */
    protected Table table;
    private T newItem;
    private Binder newItemForm;

    private SerializableSupplier<Object> editorInstantiator;

    /**
     * Creates a new instance of the field.
     *
     * @param clazz the class of the objects in the collection
     * @param editorClass the class of the editor component
     */
    public ElementCollectionField(Class<T> clazz, Class<?> editorClass) {
        this.clazz = clazz;
        this.editorClass = editorClass;
        table = new Table();
        getContent().setPadding(false);
        getContent().add(table);
    }

    /**
     * Configures the instance to use given editor instantiator.
     * @param editorInstantiator the instantiator to use
     * @return the instance for further configuration
     */
    public ElementCollectionField<T> withEditorInstantiator(SerializableSupplier<Object> editorInstantiator) {
        this.editorInstantiator = editorInstantiator;
        return this;
    }

    /**
     * A hook to override the default column headers.
     */
    protected void configureColumneHeaders() {
        Field[] declaredFields;
        if(editorClass != null) {
            declaredFields = editorClass.getDeclaredFields();
        } else {
            declaredFields = clazz.getDeclaredFields();
        }
        TableRow tr = new TableRow();
        for (int i = 0; i < declaredFields.length; i++) {
            String fieldName = declaredFields[i].getName();
            tr.addHeaderCells(getHeaderForField(fieldName));
        }
        table.addRows(tr);
    }

    /**
     * Translates raw field name to header name. By default, decamelcases the name.
     * Override for e.g. localization.
     *
     * @param fieldName the raw field name of the row property
     * @return a string to be used as a header in the editor
     */
    protected String getHeaderForField(String fieldName) {
        return StringUtils.capitalize(
                StringUtils.join(
                        StringUtils.splitByCharacterTypeCamelCase(fieldName), " "));
    }

    /**
     * hook to overrid delect button columne
     * @param row the row to add the button t
     * @param item the item to delete
     */
    protected void addDeleteButtonColumn(TableRow row, T item) {
        TableDataCell cell = row.addDataCell();
        cell.add(new VButton(VaadinIcon.TRASH.create(), event -> {
            value.remove(item);
            row.getParent().ifPresent(p -> ((Table) p).removeRows(row));
            fireValueChange();
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
