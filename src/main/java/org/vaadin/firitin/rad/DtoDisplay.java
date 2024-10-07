package org.vaadin.firitin.rad;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.firitin.components.details.VDetails;
import org.vaadin.firitin.components.html.VCode;
import org.vaadin.firitin.fields.internalhtmltable.Table;
import org.vaadin.firitin.fields.internalhtmltable.TableHeaderCell;
import org.vaadin.firitin.fields.internalhtmltable.TableRow;
import org.vaadin.firitin.util.VStyleUtil;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.vaadin.firitin.rad.ValueContext.jack;

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
public class DtoDisplay extends Composite<Div> {

    public static final int SHORT_STRING_THRESHOLD = 50;
    private static List<PropertyPrinter> _propertyPrinters = new ArrayList<>();

    static {
        _propertyPrinters.add(new PrimitivePrinter());
        _propertyPrinters.add(new EnumPrinter());
        _propertyPrinters.add(new RecordTypePrinter());
        _propertyPrinters.add(new CollectionPropertyPrinter());
        // This eats everything else
        _propertyPrinters.add(new ObjectPrinter());
    }

    private final ValueContext context;

    private List<PropertyPrinter> propertyPrinters;

    /**
     * Creates a new instance of DtoDisplay.
     *
     * @param dto the DTO to display
     */
    public DtoDisplay(Object dto) {
        this(new ArrayList<>(getDefaultPropertyPrinters()), new ValueContext(dto));
    }

    public DtoDisplay(List<PropertyPrinter> propertyPrinters, ValueContext context) {
        this.propertyPrinters = propertyPrinters;
        this.context = context;
    }

    public static List<PropertyPrinter> getDefaultPropertyPrinters() {
        return Collections.unmodifiableList(_propertyPrinters);
    }

    private static boolean isLongString(Object object) {
        return object != null && object.toString().length() > SHORT_STRING_THRESHOLD;
    }

    private static String toShortString(Object object) {
        String toString = object == null ? "" : object.toString();
        return toShortString(toString);
    }

    private static String toShortString(String toString) {
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
        Table table = new Table();
        context.getBeanDescription().findProperties().forEach(p -> {
            TableRow tableRow = table.addRow();

            context.setProperty(p);

            Component value = null;
            for (PropertyPrinter propertyPrinter : propertyPrinters) {
                value = propertyPrinter.printValue(context);
                if (value != null) {
                    String propertyHeader = propertyPrinter.getPropertyHeader(context);
                    TableHeaderCell tableHeaderCell = tableRow.addHeaderCell();
                    tableHeaderCell.setText(propertyHeader);
                    break;
                }
            }

            if (value != null) {
                tableRow.addCells(value);
            } else {
                TableHeaderCell tableHeaderCell = tableRow.addHeaderCell();
                tableHeaderCell.setText(p.getName());
                Object value1 = p.getGetter().getValue(context.getValue());
                tableRow.addCells((value1 == null ? "null" : value1.toString()) + " (no printer found)");
            }

        });

        getContent().add(table);
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
                    .dto-display td>div>p:first-child {
                        margin-top: 0;
                    }
                    .dto-display th {
                        text-align: left;
                        color: var(--lumo-secondary-text-color);
                        font-weight: 500;
                    }
                    
                    .dto-display>table>tr>th {
                        text-align: right;
                        white-space: nowrap;
                        align-items: start;
                        vertical-align: top;
                        padding-right: var(--lumo-space-s);
                    }
                    .dto-display td>vaadin-details>vaadin-details-summary {
                        padding: 0;
                    }
                        
                """);
    }

    public DtoDisplay withDefaultHeader() {
        getContent().addComponentAsFirst(new H1(deCamelCased(context.getValue().getClass().getSimpleName()) + ":"));
        return this;
    }

    public DtoDisplay withPropertyPrinter(PropertyPrinter printer) {
        propertyPrinters.add(0, printer);
        return this;
    }

    // TODO, figure out if needed, this is essentially the same as object printer
    private static class RecordTypePrinter implements PropertyPrinter {

        @Override
        public Component printValue(ValueContext ctx) {
            if (ctx.getProperty().getPrimaryType().isRecordType()) {
                Object value = ctx.getProperty().getGetter().getValue(ctx.getValue());
                String header = toShortString(value);
                return new VDetails(header, () -> new DtoDisplay(value));
            }
            return null;
        }
    }

    private static class ObjectPrinter implements PropertyPrinter {
        @Override
        public Component printValue(ValueContext ctx) {
            // TODO, figure out if circular references should be handled/visualized somehow in special way
            Object value;
            if (ctx.getProperty().hasGetter()) {
                AnnotatedMethod getter = ctx.getProperty().getGetter();
                value = getter.getValue(ctx.getValue());
            } else {
                value = ctx.getProperty().getAccessor().getValue(ctx.getValue());
            }
            String header = toShortString(value);
            return new VDetails(header, () -> new DtoDisplay(value));
        }
    }

    private static class CollectionPropertyPrinter implements PropertyPrinter {
        @Override
        public Component printValue(ValueContext ctx) {
            JavaType primaryType = ctx.getProperty().getPrimaryType();
            if (primaryType instanceof CollectionLikeType || primaryType instanceof ArrayType) {
                // if the value is an array we add a new table for it
                Table subTable = new Table();
                TableRow header = subTable.addRow();

                JavaType contentType = primaryType.getContentType();
                BasicBeanDescription contentTypeBbd = (BasicBeanDescription) jack.getSerializationConfig().introspect(contentType);
                contentTypeBbd.findProperties().forEach(subP -> {
                    header.addHeaderCell().setText(deCamelCased(subP.getName()));
                });

                Object collection = ctx.getPropertyValue();
                Class<?> collectionClass = collection.getClass();
                if (collectionClass.isArray()) {
                    Object[] array = (Object[]) collection;
                    for (Object e : array) {
                        TableRow subTableRow = subTable.addRow();
                        contentTypeBbd.findProperties().forEach(subP -> {
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
            return null;
        }
    }

    private static class PrimitivePrinter implements PropertyPrinter {
        @Override
        public Component printValue(ValueContext ctx) {
            if (ctx.getProperty().getPrimaryType().isPrimitive() ||
                    String.class == ctx.getProperty().getPrimaryType().getRawClass() ||
                    ClassUtils.isPrimitiveOrWrapper(ctx.getProperty().getRawPrimaryType())) {
                // TODO improve basic formatting. E.g. for numbers we could use NumberFormat and booleans with more
                // visual checkboxes or similar.
                Object propertyValue = ctx.getPropertyValue();
                if (propertyValue instanceof Number number) {
                    Locale locale = ctx.getLocale();
                    System.out.println("Number formatted with " + locale);
                    propertyValue = NumberFormat.getInstance(locale).format(propertyValue);
                }
                return new Span("" + propertyValue);
            }
            return null;
        }
    }

    private static class EnumPrinter implements PropertyPrinter {
        @Override
        public Component printValue(ValueContext ctx) {
            // TODO figure out if this should be merged with primitive printer 🤔
            // Now only formatting with "code" tag to separate from basic string
            if (ctx.getProperty().getPrimaryType().isEnumType() ||
                    ctx.getProperty().getPrimaryType().isEnumImplType()) {
                return new VCode("" + ctx.getPropertyValue());
            }
            return null;
        }
    }
}
