package org.vaadin.firitin.rad;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.firitin.fields.internalhtmltable.Table;
import org.vaadin.firitin.fields.internalhtmltable.TableHeaderCell;
import org.vaadin.firitin.fields.internalhtmltable.TableRow;
import org.vaadin.firitin.util.VStyleUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.vaadin.firitin.rad.PrettyPrinter._defaultPropertyPrinters;

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
                    .dto-display td>div>p:first-child,
                    .dto-display td>p:first-child {
                        margin: 0;
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

}
