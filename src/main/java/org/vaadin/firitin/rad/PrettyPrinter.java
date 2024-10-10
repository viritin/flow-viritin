package org.vaadin.firitin.rad;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import org.apache.commons.lang3.ClassUtils;
import org.vaadin.firitin.components.details.VDetails;
import org.vaadin.firitin.components.html.VCode;
import org.vaadin.firitin.fields.internalhtmltable.Table;
import org.vaadin.firitin.fields.internalhtmltable.TableRow;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.vaadin.firitin.rad.DtoDisplay.deCamelCased;

/**
 * Experimental feature, API/naming might change.
 */
public class PrettyPrinter {

    // Helper "Jack" to do introspection
    static final ObjectMapper jack = new ObjectMapper();
    static List<PropertyPrinter> _defaultPropertyPrinters = new ArrayList<>();
    private static final PrettyPrinter INSTANCE = new PrettyPrinter(getDefaultPropertyPrinters());

    static {
        _defaultPropertyPrinters.add(new PrimitivePrinter());
        _defaultPropertyPrinters.add(new EnumPrinter());
        _defaultPropertyPrinters.add(new RecordTypePrinter());
        _defaultPropertyPrinters.add(new CollectionPropertyPrinter());
        // This eats everything else
        _defaultPropertyPrinters.add(new ObjectPrinter());
    }

    private final List<PropertyPrinter> propertyPrinters;
    private final List<PropertyHeaderPrinter> propertyHeaderPrinters;
    private Locale locale;

    public PrettyPrinter() {
        this(new ArrayList<>(getDefaultPropertyPrinters()));
    }

    public PrettyPrinter(List<PropertyPrinter> propertyPrinters) {
        this.propertyPrinters = propertyPrinters;
        this.propertyHeaderPrinters = new ArrayList<>();
    }

    public static List<PropertyPrinter> getDefaultPropertyPrinters() {
        return Collections.unmodifiableList(_defaultPropertyPrinters);
    }

    static PrettyPrinter getDefault() {
        return INSTANCE;
    }

    public static Component toVaadin(Object value) {
        return INSTANCE.printToVaadin(value);
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

    public Component printToVaadin(Object value) {
        ValueContextImpl valueContext = new ValueContextImpl(this, value);
        return printToVaadin(valueContext);
    }

    Component printToVaadin(ValueContext ctx) {
        DtoDisplay dtoDisplay = new DtoDisplay(propertyPrinters, ctx);
        propertyHeaderPrinters.forEach(dtoDisplay::withPropertyHeaderPrinter);
        return dtoDisplay;
    }

    public PrettyPrinter withPropertyPrinter(PropertyPrinter printer) {
        propertyPrinters.add(0, printer);
        return this;
    }

    public PrettyPrinter withPropertyHeaderPrinter(PropertyHeaderPrinter printer) {
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

    // TODO, figure out if needed, this is essentially the same as object printer
    private static class RecordTypePrinter implements PropertyPrinter {

        @Override
        public Component printValue(PropertyContext ctx) {
            if (ctx.beanPropertyDefinition().getPrimaryType().isRecordType()) {
                ValueContext propertyContext = ctx.asValueContext();
                String header = propertyContext.toShortString();
                return new VDetails(header, () -> propertyContext.getPrettyPrinter().printToVaadin(propertyContext));
            }
            return null;
        }
    }

    private static class ObjectPrinter implements PropertyPrinter {
        @Override
        public Component printValue(PropertyContext ctx) {
            // TODO, figure out if circular references should be handled/visualized somehow in special way
            ValueContext propCtx = ctx.asValueContext();
            String header = propCtx.toShortString();
            return new VDetails(header, () -> propCtx.getPrettyPrinter().printToVaadin(propCtx));
        }
    }

    private static class CollectionPropertyPrinter implements PropertyPrinter {
        @Override
        public Component printValue(PropertyContext ctx) {
            JavaType primaryType = ctx.beanPropertyDefinition().getPrimaryType();
            if (primaryType instanceof CollectionLikeType || primaryType instanceof ArrayType) {

                JavaType contentType = primaryType.getContentType();
                BasicBeanDescription contentTypeBbd = (BasicBeanDescription) jack.getSerializationConfig().introspect(contentType);
                List<BeanPropertyDefinition> properties = contentTypeBbd.findProperties();
                Object collection = ctx.getPropertyValue();
                Class<?> collectionClass = collection.getClass();

                if (properties.isEmpty()) {
                    String str;
                    if (collectionClass.isArray()) {
                        Object[] array = (Object[]) collection;
                        str = Arrays.stream(array).map(Object::toString).collect(Collectors.joining(", "));
                    } else if (collection instanceof Iterable<?> iterable) {
                        StringBuilder sb = new StringBuilder();
                        Iterator<?> iterator = iterable.iterator();
                        while (iterator.hasNext()) {
                            sb.append(iterator.next().toString());
                            if (iterator.hasNext()) {
                                sb.append(", ");
                            }
                        }
                        str = sb.toString();
                    } else {
                        str = collection.toString();
                    }
                    return new Paragraph(str);
                } else {
                    // if the value is an array we add a new table for it
                    Table subTable = new Table();
                    TableRow header = subTable.addRow();

                    contentTypeBbd.findProperties().forEach(subP -> {
                        header.addHeaderCell().setText(deCamelCased(subP.getName()));
                    });

                    if (collectionClass.isArray()) {
                        Object[] array = (Object[]) collection;
                        for (Object e : array) {
                            TableRow subTableRow = subTable.addRow();
                            properties.forEach(subP -> {
                                Object value = subP.getGetter().getValue(e);
                                subTableRow.addCells(value.toString());
                            });
                        }
                    } else if (collection instanceof Iterable<?> iterable) {
                        iterable.forEach(e -> {
                            TableRow subTableRow = subTable.addRow();
                            contentTypeBbd.findProperties().forEach(subP -> {
                                Object value = subP.getGetter().getValue(e);
                                subTableRow.addCells(value.toString());
                            });
                        });
                    }
                    return subTable;
                }

            }
            return null;
        }
    }

    private static class PrimitivePrinter implements PropertyPrinter {
        @Override
        public Component printValue(PropertyContext ctx) {
            if (ctx.beanPropertyDefinition().getPrimaryType().isPrimitive() ||
                    String.class == ctx.beanPropertyDefinition().getPrimaryType().getRawClass() ||
                    ClassUtils.isPrimitiveOrWrapper(ctx.beanPropertyDefinition().getRawPrimaryType())) {
                // TODO improve basic formatting. E.g. for numbers we could use NumberFormat and booleans with more
                // visual checkboxes or similar.
                Object propertyValue = ctx.getPropertyValue();
                if (propertyValue instanceof Number number) {
                    Locale locale = ctx.getLocale();
                    propertyValue = NumberFormat.getInstance(locale).format(propertyValue);
                }
                return new Span("" + propertyValue);
            }
            return null;
        }
    }

    private static class EnumPrinter implements PropertyPrinter {
        @Override
        public Component printValue(PropertyContext ctx) {
            // TODO figure out if this should be merged with primitive printer 🤔
            // Now only formatting with "code" tag to separate from basic string
            if (ctx.beanPropertyDefinition().getPrimaryType().isEnumType() ||
                    ctx.beanPropertyDefinition().getPrimaryType().isEnumImplType()) {
                return new VCode("" + ctx.getPropertyValue());
            }
            return null;
        }
    }
}
