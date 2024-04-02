package org.vaadin.firitin.fields;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.datepicker.VDatePicker;
import org.vaadin.firitin.components.datetimepicker.VDateTimePicker;
import org.vaadin.firitin.components.textfield.VIntegerField;
import org.vaadin.firitin.components.textfield.VNumberField;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.fields.internalhtmltable.Table;
import org.vaadin.firitin.fields.internalhtmltable.TableCell;
import org.vaadin.firitin.fields.internalhtmltable.TableDataCell;
import org.vaadin.firitin.fields.internalhtmltable.TableRow;
import org.vaadin.firitin.form.AbstractForm;
import org.vaadin.firitin.form.FormBinder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A field to pick a list of objects from a superset.
 *
 * <p>Usage example: selecting runners to a relay team.</p>
 */
public class ElementCollectionField<T> extends Composite<VerticalLayout>
        implements HasValue<HasValue.ValueChangeEvent<List<T>>, List<T>>, HasSize {

    // Helper "Jack" to do introspection
    private static final ObjectMapper jack = new ObjectMapper();

    private final Class<T> clazz;
    private final Class<?> editorClass;

    private List<T> value;

    /**
     * The table that is used to display the collection.
     */
    protected Table table;
    private T newItem;
    private FormBinder newItemForm;

    private SerializableSupplier<Object> editorInstantiator;
    private BasicBeanDescription bbd;

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
     * Creates a new instance of the field.
     *
     * @param clazz the class of the objects in the collection
     */
    public ElementCollectionField(Class<T> clazz) {
        this.clazz = clazz;
        this.editorClass = null;
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
        List<String> fieldNames;
        if(editorClass != null) {
            fieldNames = Arrays.stream(editorClass.getDeclaredFields()).map(f -> f.getName()).toList();
        } else {
            // Full autogeneration, let mr Jackson do it on the element type
            JavaType javaType = jack.getTypeFactory().constructType(clazz);
            bbd = (BasicBeanDescription) jack.getSerializationConfig().introspect(javaType);
            fieldNames = bbd.findProperties().stream().map(p -> p.getName()).toList();
        }
        TableRow tr = new TableRow();
        for(String fieldName : fieldNames) {
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

    private FormBinder<T> addNewRow(T item) {
        Object row = instantiateRowObject();
        FormBinder<T> binder;
        if(row != null) {
            binder = new FormBinder<>(clazz,row);
        } else {
            Map<String, HasValue> propertyEditors = generateEditors();
            binder = new FormBinder<>(clazz, propertyEditors);
        }
        TableRow tr;
        if (row instanceof AbstractForm) {
            // TODO check if this is used somewhere, best guestimate:
            // converted from V7 version
            AbstractForm form = (AbstractForm) row;
            ((AbstractForm<T>) row).setEntity(item);
            tr = (TableRow) form.getContent().getChildren().findFirst().get();
        } else {
            List<String> bindings = binder.getBoundProperties();
            Component[] components = new Component[bindings.size()];
            for (int i = 0; i < bindings.size(); i++) {
                String property = bindings.get(i);
                HasValue editor = binder.getEditor(property);
                components[i] = (Component) editor;
            }
            tr = new TableRow(components);
        }
        binder.setValue(item);
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

    /**
     * No editor fields are created try to generate.
     *
     * @return map of editors for properties
     */
    private Map<String, HasValue> generateEditors() {
        // TODO figure out how to/if should use field factory from Vaadin core
        // TODO figure out how to make this configurable
        // TODO figure out all possible types that could be supported out of the box with various fields & converters
        Map<String,HasValue> editors = new HashMap<>();
        List<BeanPropertyDefinition> properties = bbd.findProperties();
        for (BeanPropertyDefinition property : properties) {
            Class<?> type = property.getRawPrimaryType();
            HasValue hv = null;
            if(String.class == type) {
                hv = new VTextField();
            } else if(Integer.class == type) {
                hv = new VIntegerField();
            } else if(Double.class == type) {
                hv = new VNumberField();
            } else if(LocalDate.class == type) {
                hv = new VDatePicker();
            } else if(LocalDateTime.class == type) {
                hv = new VDateTimePicker();
            } else if(Enum.class.isAssignableFrom(type)) {
                hv = new EnumSelect<>(type);
            } else {
                throw new UnsupportedOperationException("No field type generated for" + type.getName());
            }
            editors.put(property.getName(), hv);
        }
        return editors;
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
            if(editorClass != null) {
                return editorClass.getDeclaredConstructor().newInstance();
            }
            return null;
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
