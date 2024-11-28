package org.vaadin.firitin.rad;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.firitin.components.checkbox.VCheckBox;
import org.vaadin.firitin.components.datepicker.VDatePicker;
import org.vaadin.firitin.components.datetimepicker.VDateTimePicker;
import org.vaadin.firitin.components.textfield.VIntegerField;
import org.vaadin.firitin.components.textfield.VNumberField;
import org.vaadin.firitin.components.timepicker.VTimePicker;
import org.vaadin.firitin.fields.internalhtmltable.Table;
import org.vaadin.firitin.fields.internalhtmltable.TableHeaderCell;
import org.vaadin.firitin.fields.internalhtmltable.TableRow;
import org.vaadin.firitin.form.FormBinder;
import org.vaadin.firitin.util.VStyleUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.vaadin.firitin.rad.DtoDisplay.deCamelCased;

/**
 * Experimental feature, API/naming might change.
 */
public class AutoForm {

    // Helper "Jack" to do introspection, TODO check to use the same instance as in FormBinder
    static final ObjectMapper jack = new ObjectMapper();
    static List<PropertyPrinter> _defaultPropertyPrinters = new ArrayList<>();
    private static final AutoForm INSTANCE = new AutoForm(getDefaultPropertyPrinters());

    static {
        _defaultPropertyPrinters.add(new StringEditor());
        _defaultPropertyPrinters.add(new TypeBasePrinter(VIntegerField.class, int.class, Integer.class));
        _defaultPropertyPrinters.add(new TypeBasePrinter(VNumberField.class, double.class, Double.class));
        _defaultPropertyPrinters.add(new TypeBasePrinter(VDatePicker.class, java.util.Date.class, java.time.LocalDate.class));
        _defaultPropertyPrinters.add(new TypeBasePrinter(VDateTimePicker.class, java.util.Date.class, java.time.LocalDateTime.class));
        _defaultPropertyPrinters.add(new TypeBasePrinter(VTimePicker.class, java.time.LocalTime.class));
        _defaultPropertyPrinters.add(new TypeBasePrinter(VCheckBox.class, Boolean.class, boolean.class));
        // This eats everything else, shows the toString() of the object
        _defaultPropertyPrinters.add(new ObjectPrinter());

    }

    private final List<PropertyPrinter> propertyPrinters;
    private final List<PropertyHeaderPrinter> propertyHeaderPrinters;
    private Locale locale;

    public AutoForm() {
        this(new ArrayList<>(getDefaultPropertyPrinters()));
    }

    public AutoForm(List<PropertyPrinter> propertyPrinters) {
        this.propertyPrinters = propertyPrinters;
        this.propertyHeaderPrinters = new ArrayList<>();
    }

    public static List<PropertyPrinter> getDefaultPropertyPrinters() {
        return Collections.unmodifiableList(_defaultPropertyPrinters);
    }

    static AutoForm getDefault() {
        return INSTANCE;
    }

    static BasicBeanDescription inrospect(Object dto) {
        if(dto == null) {
            return null;
        }
        JavaType javaType = jack.getTypeFactory().constructType(dto.getClass());
        return (BasicBeanDescription) jack.getSerializationConfig().introspect(javaType);
    }

    public List<PropertyPrinter> getPropertyPrinters() {
        return propertyPrinters;
    }

    public <T> Form<T> createForm(T value) {
        AutoFormValueContextImpl valueContext = new AutoFormValueContextImpl(this, value);
        return createForm(valueContext);
    }

    Form createForm(ValueContext ctx) {
        FormDisplay dtoDisplay = new FormDisplay(propertyPrinters, ctx);
        propertyHeaderPrinters.forEach(dtoDisplay::withPropertyHeaderPrinter);
        return dtoDisplay;
    }

    public AutoForm withPropertyPrinter(PropertyPrinter printer) {
        propertyPrinters.add(0, printer);
        return this;
    }

    public AutoForm withPropertyHeaderPrinter(PropertyHeaderPrinter printer) {
        propertyHeaderPrinters.add(0, printer);
        return this;
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

    private static class ObjectPrinter implements PropertyPrinter {
        @Override
        public Component printValue(PropertyContext ctx) {
            // TODO, figure out if circular references should be handled/visualized somehow in special way
            ValueContext propCtx = ctx.asValueContext();

            String header = propCtx.toShortString();
            return new Paragraph(propCtx.value() + " Editing not supported: " + ctx.beanPropertyDefinition().getPrimaryType());
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
            if(propertyType.contains(rawClass)) {
                try {
                    return (Component) componentType.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        }
    }


    private static class StringEditor extends TypeBasePrinter {
        public StringEditor() {
            super(TextField.class, String.class);
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
    /**
     * A simple component to display a DTO in a human-readable way with minimal amount of code.
     * Can be used to display simple results on the UI or in RAD (Rapid Application Development)
     * to quickly see the content of a complex DTO coming from some API.
     * <p>
     * Note, this is very early draft and likely the formatting will change
     * in upcoming versions. Current version uses Jackson to read the first level of data,
     * but in future versions it might use some other library or custom reflection code
     * and might display deeper object trees.
     * </p>
     */
    public static class FormDisplay<T> extends Composite<Div> implements Form<T> {

        public static final int SHORT_STRING_THRESHOLD = 50;

        private final ValueContext context;
        private final FormBinder<T> formBinder;

        private List<PropertyPrinter> propertyPrinters;
        private List<PropertyHeaderPrinter> headerPrinters = new ArrayList<>();

        public FormDisplay(Object dto) {
            this(new ArrayList<>(getDefaultPropertyPrinters()), new ValueContextImpl(PrettyPrinter.getDefault(), dto));
        }

        public FormDisplay(List<PropertyPrinter> propertyPrinters, ValueContext context) {
            this.propertyPrinters = propertyPrinters;
            this.context = context;
            this.formBinder = new FormBinder<T>(context.beanDescription());
        }

        public static List<PropertyPrinter> getDefaultPropertyPrinters() {
            return Collections.unmodifiableList(_defaultPropertyPrinters);
        }

        private static boolean isLongString(Object object) {
            return object != null && object.toString().length() > SHORT_STRING_THRESHOLD;
        }

        static String toShortString(Object object) {
            String toString = object == null ? "" : object.toString();
            return toShortString(toString);
        }

        static String toShortString(String toString) {
            if (toString == null) {
                return "";
            }
            if (toString.length() > SHORT_STRING_THRESHOLD) {
                toString = toString.substring(0, 50) + "...";
                ;
            }
            return toString;
        }

        static String deCamelCased(String propertyName) {
            String deCamelCased = propertyName.replaceAll("([a-z])([A-Z]+)", "$1 $2");
            deCamelCased = StringUtils.capitalize(deCamelCased);
            return deCamelCased;
        }

        private void buildTable() {
            injectStyles();
            FormLayout formLayout = new FormLayout();
            context.beanDescription().findProperties().forEach(p -> {
                PropertyContext propertyContext = context.getPropertyContext(p);

                Object value = null;
                for (PropertyPrinter propertyPrinter : propertyPrinters) {
                    value = propertyPrinter.printValue(propertyContext);
                    if (value != null) {
                        if (HasValue.class.isAssignableFrom(value.getClass())) {
                            // Data binding with the form binder
                            formBinder.bindProperty(p, (HasValue) value);
                        }
                        // PropertyPrinter can override header if it wants, otherwise use the first one that returns or
                        // default header
                        Object propertyHeader = propertyPrinter.getPropertyHeader(propertyContext);
                        if(propertyHeader == null) {
                            propertyHeader = headerPrinters.stream().map(headerPrinter -> headerPrinter.printHeader(propertyContext))
                                    .filter(h -> h != null).findFirst().orElse(PropertyHeaderPrinter.defaultHeader(propertyContext));
                        }
                        if (propertyHeader instanceof Component c) {
                            throw new RuntimeException("WTF!?");
                        } else {
                            if(value != null && value instanceof HasLabel hl) {
                                hl.setLabel(propertyHeader.toString());
                            } else {
                                // TODO wrap the content somehow!?

                            }
                        }
                        break;
                    }
                }

                if (value != null) {
                    if (value instanceof Component c) {
                        formLayout.add(c);
                    } else {
                        formLayout.add(value.toString());
                    }
                } else {
                    throw new RuntimeException("No printer found for " + p.getName());
                    /*

                    TableHeaderCell tableHeaderCell = tableRow.addHeaderCell();
                    tableHeaderCell.setText(p.getName());
                    Object value1 = p.getGetter().getValue(context.value());
                    tableRow.addCells((value1 == null ? "null" : value1.toString()) + " (no printer found)");

                     */
                }

            });

            getContent().add(formLayout);
            formBinder.setValue((T) context.value());
        }

        @Override
        protected void onAttach(AttachEvent attachEvent) {
            super.onAttach(attachEvent);
            getContent().removeAll();
            buildTable();
        }

        private void injectStyles() {
            getContent().setClassName("dto-display");

            VStyleUtil.injectAsFirst("""
                    .dto-display table {
                        border-collapse: collapse;
                    }
                    .dto-display td, .dto-display th {
                        padding: var(--lumo-space-xs);
                    }
                    .dto-display tr:first-child td, .dto-display tr:first-child th {
                        padding-top: 0;
                    }
                    .dto-display td>div>p:first-child,
                    .dto-display td>p:first-child {
                        margin: 0;
                    }
                    .dto-display th {
                        text-align: left;
                        color: var(--lumo-secondary-text-color);
                        font-weight: 500;
                    }
                    .dto-display th,
                    .dto-display td {
                        vertical-align: top;
                    }
                    .dto-display>table>tr>th {
                        text-align: right;
                        white-space: nowrap;
                        align-items: start;
                        padding-right: var(--lumo-space-s);
                    }
                    .dto-display td>vaadin-details>vaadin-details-summary {
                        padding: 0;
                    }
                        
                """);
        }

        public FormDisplay withDefaultHeader() {
            getContent().addComponentAsFirst(new H1(deCamelCased(context.value().getClass().getSimpleName()) + ":"));
            return this;
        }

        public FormDisplay withPropertyPrinter(PropertyPrinter printer) {
            propertyPrinters.add(0, printer);
            return this;
        }

        public FormDisplay withPropertyHeaderPrinter(PropertyHeaderPrinter printer) {
            headerPrinters.add(0, printer);
            return this;
        }

        @Override
        public Component getComponent() {
            return this;
        }

        @Override
        public T getValue() {
            return formBinder.getValue();
        }

        @Override
        public FormBinder<T> getBinder() {
            return formBinder;
        }
    }
}
