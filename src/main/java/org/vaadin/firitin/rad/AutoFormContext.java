package org.vaadin.firitin.rad;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.FieldSet;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.IntegerField;
import org.vaadin.firitin.components.checkbox.VCheckBox;
import org.vaadin.firitin.components.customfield.VCustomField;
import org.vaadin.firitin.components.datepicker.VDatePicker;
import org.vaadin.firitin.components.datetimepicker.VDateTimePicker;
import org.vaadin.firitin.components.textfield.VBigDecimalField;
import org.vaadin.firitin.components.textfield.VIntegerField;
import org.vaadin.firitin.components.textfield.VNumberField;
import org.vaadin.firitin.components.textfield.VTextArea;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.components.timepicker.VTimePicker;
import org.vaadin.firitin.fields.ElementCollectionField;
import org.vaadin.firitin.fields.EnumSelect;
import org.vaadin.firitin.fields.LongField;
import org.vaadin.firitin.fields.ShortField;
import org.vaadin.firitin.layouts.HorizontalFloatLayout;
import org.vaadin.firitin.util.VStyleUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Experimental feature, API/naming might change.
 */
public class AutoFormContext {

    // Helper "Jack" to do introspection, TODO check to use the same instance as in FormBinder
    static final ObjectMapper jack = new ObjectMapper();
    static List<PropertyPrinter> _defaultPropertyPrinters = new ArrayList<>();
    private final List<PropertyPrinter> propertyEditors;
    private final List<PropertyHeaderPrinter> propertyHeaderPrinters;
    private Locale locale;
    // TODO consider changing this to a filterchain that can hide properties based on
    // conxtex (not just by names as now)
    private Set<String> hiddenProperties = new HashSet<>() {{
        add("id");
    }};
    private boolean annotateTypes = false;
    private boolean defaultBeanValidation = true;

    public AutoFormContext() {
        this(new ArrayList<>(getDefaultPropertyPrinters()));
    }

    public AutoFormContext(List<PropertyPrinter> propertyPrinters) {
        this.propertyEditors = new ArrayList<>(propertyPrinters);
        this.propertyHeaderPrinters = new ArrayList<>();
    }

    public static List<PropertyPrinter> getDefaultPropertyPrinters() {
        if (_defaultPropertyPrinters.isEmpty()) {
            _defaultPropertyPrinters.add(new StringEditor());
            _defaultPropertyPrinters.add(new TypeBasePrinter(VIntegerField.class, int.class, Integer.class));
            _defaultPropertyPrinters.add(new TypeBasePrinter(LongField.class, Long.class, long.class));
            _defaultPropertyPrinters.add(new TypeBasePrinter(ShortField.class, Short.class, short.class));
            _defaultPropertyPrinters.add(new TypeBasePrinter(VNumberField.class, double.class, Double.class));
            _defaultPropertyPrinters.add(new TypeBasePrinter(VBigDecimalField.class, BigDecimal.class));
            _defaultPropertyPrinters.add(new TypeBasePrinter(VDatePicker.class, java.util.Date.class, java.time.LocalDate.class));
            _defaultPropertyPrinters.add(new TypeBasePrinter(VDateTimePicker.class, java.util.Date.class, java.time.LocalDateTime.class, java.time.Instant.class));
            _defaultPropertyPrinters.add(new TypeBasePrinter(VTimePicker.class, java.time.LocalTime.class));
            _defaultPropertyPrinters.add(new TypeBasePrinter(VCheckBox.class, Boolean.class, boolean.class));
            _defaultPropertyPrinters.add(new TypeBasePrinter(EnumSelect.class, java.lang.Enum.class));
            _defaultPropertyPrinters.add(new EnumSelectPrinter());
            _defaultPropertyPrinters.add(new ElementCollectionPrinter());
            _defaultPropertyPrinters.add(new EmbeddablePrinter());
            // This eats everything else, shows the toString() of the object
            _defaultPropertyPrinters.add(new ObjectPrinter());
        }
        return Collections.unmodifiableList(_defaultPropertyPrinters);
    }

    static BasicBeanDescription inrospect(Object dto) {
        if (dto == null) {
            return null;
        }
        Class<?> type = dto.getClass();
        return introspectClass(type);
    }

    static BasicBeanDescription introspectClass(Class<?> type) {
        JavaType javaType = jack.getTypeFactory().constructType(type);
        return (BasicBeanDescription) jack.getSerializationConfig().introspect(javaType);
    }

    public boolean isAnnotateTypes() {
        return annotateTypes;
    }

    public void setAnnotateTypes(boolean annotateTypes) {
        this.annotateTypes = annotateTypes;
    }

    public List<PropertyPrinter> getPropertyPrinters() {
        return propertyEditors;
    }

    public <T> AutoForm<T> createForm(T value) {
        AutoForm dtoDisplay = new AutoForm(this, inrospect(value), value);
        propertyHeaderPrinters.forEach(dtoDisplay::withPropertyHeaderPrinter);
        return dtoDisplay;
    }

    public <T> AutoForm<T> createForm(Class<T> type) {
        AutoForm dtoDisplay = new AutoForm(this, introspectClass(type), null);
        propertyHeaderPrinters.forEach(dtoDisplay::withPropertyHeaderPrinter);
        return dtoDisplay;
    }

    /**
     * Registers a custom property editor that can be used to edit a property of a bean.
     * The editors are tried in order, first one that returns a non-null value is used.
     * The given editor is added to the beginning of the list, so it is tried first.
     *
     * @param propertyEditor the editor to add
     * @return this for chaining
     */
    public AutoFormContext withPropertyEditor(PropertyPrinter propertyEditor) {
        propertyEditors.add(0, propertyEditor);
        return this;
    }

    public AutoFormContext withPropertyHeaderPrinter(PropertyHeaderPrinter printer) {
        propertyHeaderPrinters.add(0, printer);
        return this;
    }

    /**
     * Disables the default bean validation. By default, the bean validation is enabled if found from the classpath.
     *
     * @return this for chaining
     */
    public AutoFormContext disableBeanValidation() {
        defaultBeanValidation = false;
        return this;
    }

    public boolean isDefaultBeanValidation() {
        return defaultBeanValidation;
    }

    public Locale getLocale() {
        if (locale == null) {
            UI ui = UI.getCurrent();
            if (ui != null) {
                locale = ui.getLocale();
            } else {
                locale = Locale.getDefault();
            }
        }
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Set<String> getHiddenProperties() {
        return hiddenProperties;
    }

    private static class ObjectPrinter implements PropertyPrinter {
        @Override
        public Component printValue(PropertyContext ctx) {
            String msg = "Editing " + ctx.getName() + " not supported. Type: " + ctx.beanPropertyDefinition().getPrimaryType();
            return new Paragraph(msg);
        }
    }

    private static class TypeBasePrinter implements PropertyPrinter {

        private final List<Class> propertyType;
        private final Class<? extends HasValue> componentType;

        public TypeBasePrinter(Class componentType, Class... propertyType) {
            this.propertyType = Arrays.asList(propertyType);
            this.componentType = componentType;
        }

        @Override
        public Component printValue(PropertyContext ctx) {
            Class<?> rawClass = ctx.beanPropertyDefinition().getPrimaryType().getRawClass();
            if (propertyType.contains(rawClass)) {
                try {
                    return (Component) componentType.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        }
    }

    // TODO Figure out if this can be there by default!?
    // Sometimes work very well, but sometimes not, e.g. (many to many relation, where editing the relation is not desired)
    // Maybe enable only if e.g. JPAs @ElementCollection is present
    private static class ElementCollectionPrinter implements PropertyPrinter {
        @Override
        public Component printValue(PropertyContext ctx) {
            if (ctx.beanPropertyDefinition().getPrimaryType().isCollectionLikeType()) {
                JavaType primaryType = ctx.beanPropertyDefinition().getPrimaryType();
                JavaType javaType = primaryType.containedType(0);
                Class<?> rawClass = javaType.getRawClass();
                // TODO figure out how one could configure the (form)layout. E.g. this component could consume a full row.
                return new ElementCollectionField<>(rawClass);
            }
            return null;
        }
    }

    private static class EmbeddablePrinter implements PropertyPrinter {
        // TODO this is a very quick and dirty implementation, but seems to work for trivial records, should be refactored

        @Override
        public Component printValue(PropertyContext ctx) {
            if (ctx.beanPropertyDefinition().getPrimaryType().isRecordType()) {
                // TODO refactor, the PropertyContext should be able to provide AutoFormContext in some clean way
                AutoForm owner = (AutoForm) ctx.owner();
                AutoFormContext autoFormContext = owner.getAutoFormContext();
                AutoForm<?> form = autoFormContext.createForm(ctx.beanPropertyDefinition().getPrimaryType().getRawClass());
                return new RecordField<>(form);
            }
            return null;
        }

        private static class RecordField<T> extends VCustomField<T> {
            private final AutoForm<T> form;

            public RecordField(AutoForm<T> form) {
                super(null);
                this.form = form;
                addClassNames("full-width","v-record-field");
                VStyleUtil.injectAsFirst("""
                        .v-record-field fieldset  {
                            padding: 0 var(--lumo-space-m);
                            border: 1px dashed var(--lumo-contrast-30pct);
                            border-radius: var(--lumo-border-radius-l);
                        }
                """);
                FieldSet fieldSet = new FieldSet();
                add(fieldSet);
                fieldSet.add(form);
                form.getBinder().addValueChangeListener(e -> {
                    if(e.isFromClient()) {
                        setModelValue(e.getValue(), true);
                    }
                });
            }

            @Override
            protected T generateModelValue() {
                return form.getValue();
            }

            @Override
            protected void setPresentationValue(T newPresentationValue) {
                form.getBinder().setValue(newPresentationValue);
            }

        }
    }

    private static class EnumSelectPrinter implements PropertyPrinter {
        @Override
        public Component printValue(PropertyContext ctx) {
            if (ctx.beanPropertyDefinition().getPrimaryType().getRawClass().isEnum()) {
                return new EnumSelect(ctx.beanPropertyDefinition().getPrimaryType().getRawClass());
            }
            return null;
        }
    }


    private static class ComboBoxPrinter implements PropertyPrinter {
        @Override
        public Component printValue(PropertyContext ctx) {
            // TODO figure out a good heuristics to pick ComboBox for reference selection
            if (true) {
                return new ComboBox<>();

            }
            return null;
        }
    }


    private static class StringEditor implements PropertyPrinter {
        @Override
        public Object printValue(PropertyContext ctx) {
            if (String.class == ctx.beanPropertyDefinition().getPrimaryType().getRawClass()) {
                if (ctx.getName().toString().equals("description")) {
                    return new VTextArea();
                }
                // PasswordField probably makes no sense with autoform
                return new VTextField();
            }
            return null;
        }
    }

    private static class IntegerEditor implements PropertyPrinter {
        @Override
        public Component printValue(PropertyContext ctx) {
            if (Integer.class == ctx.beanPropertyDefinition().getPrimaryType().getRawClass()
                    || int.class == ctx.beanPropertyDefinition().getPrimaryType().getRawClass()) {
                return new IntegerField();
            }
            return null;
        }
    }

    private static class BooleanEditor implements PropertyPrinter {
        @Override
        public Component printValue(PropertyContext ctx) {
            if (Boolean.class == ctx.beanPropertyDefinition().getPrimaryType().getRawClass()
                    || boolean.class == ctx.beanPropertyDefinition().getPrimaryType().getRawClass()) {
                return new Checkbox();
            }
            return null;
        }
    }

}
