package org.vaadin.firitin.rad;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.dom.Style;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.firitin.fields.internalhtmltable.Table;
import org.vaadin.firitin.fields.internalhtmltable.TableHeaderCell;
import org.vaadin.firitin.fields.internalhtmltable.TableRow;

/**
 * A simple component to display a DTO as a human-readable table. Can be
 * helpful in RAD (Rapid Application Development) to quickly see the content
 * of a DTO coming from some API (on the screen and visually).
 * <p>
 * Note, this is very early draft and likely the formatting will change
 * in upcoming versions. Current version uses Jackson to read the first level of data,
 * but in future versions it might use some other library or custom reflection code
 * and might display deeper object trees.
 * </p>
 */
public class DtoDisplay extends Composite<Div> {

    /**
     * Creates a new instance of DtoDisplay.
     *
     * @param dto the DTO to display
     */
    public DtoDisplay(Object dto) {
        String simpleName = dto.getClass().getSimpleName();
        getContent().add(new H3(deCamelCased(simpleName) + ":"));
        Table table = new Table();
        JsonNode jsonNode = new ObjectMapper().valueToTree(dto);

        jsonNode.fields().forEachRemaining(f -> {
            TableRow tableRow = table.addRow();
            TableHeaderCell tableHeaderCell = tableRow.addHeaderCell();
            String propertyName = f.getKey();
            String deCamelCased = deCamelCased(propertyName);
            tableHeaderCell.setText(deCamelCased + ":");
            tableHeaderCell.getStyle().setTextAlign(Style.TextAlign.RIGHT);
            tableHeaderCell.getStyle().set("vertical-align", "top");
            tableHeaderCell.getStyle().setAlignItems(Style.AlignItems.START);
            if(f.getValue().isArray()) {
                // if the value is an array we add a new table for it
                Table subTable = new Table();
                TableRow header = subTable.addRow();
                JsonNode first = f.getValue().get(0);
                first.fields().forEachRemaining(subF -> {
                    header.addHeaderCell().setText(deCamelCased(subF.getKey()));
                });
                f.getValue().elements().forEachRemaining(e -> {
                    TableRow subTableRow = subTable.addRow();
                    e.fields().forEachRemaining(subF -> {
                        subTableRow.addCells(subF.getValue().asText());
                    });
                });
                tableRow.addCells(subTable);

            } else {
                // by default we just add the value as text
                tableRow.addCells(f.getValue().asText());
            }
        });
        getContent().add(table);
    }

    private static String deCamelCased(String propertyName) {
        String deCamelCased = propertyName.replaceAll("([a-z])([A-Z]+)", "$1 $2");
        deCamelCased = StringUtils.capitalize(deCamelCased);
        return deCamelCased;
    }
}
