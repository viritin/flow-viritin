/*
 * Copyright 2021 by Stefan Uebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.firitin.fields.internalhtmltable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;

/**
 * A base interface for components, that contain table rows.
 *
 * @see TableRow
 *
 * @author Stefan Uebe
 */
public interface TableRowContainer extends HasElement {

    /**
     * Adds a single row instance to this container. The created row is returned for further configuration.
     * @return the created row
     */
    default TableRow addRow() {
        TableRow row = new TableRow();
        addRows(row);
        return row;
    }

    /**
     * Adds the given rows to this instance.
     * @param rows rows
     */
    default void addRows(TableRow... rows) {
        getElement().appendChild(ElementHelper.asElements(rows));
    }

    /**
     * Adds multiple rows to this instance based on the given integer (must be greater than 0).
     * The created rows are returned as an array, that can be used for further configuration.
     * @param rows amount of rows to add
     * @return created row objects
     */
    default TableRow[] addRows(int rows) {
        if (rows < 1) {
            throw new IllegalArgumentException("Rows must be greater than 0");
        }

        TableRow[] rowObjects = IntStream
                .range(0, rows)
                .mapToObj(i -> new TableRow())
                .toArray(TableRow[]::new);
        addRows(rowObjects);
        return rowObjects;
    }

    /**
     * Inserts a single row instance at the given index to this container. Existing items will be placed after the inserted items.
     * The created row is returned for further configuration.
     *
     * @param index to insert the row at
     * @return the created row
     */
    default TableRow insertRow(int index) {
        TableRow tableRow = new TableRow();
        insertRows(index, tableRow);
        return tableRow;
    }

    /**
     * Inserts the given rows at the given index to this instance. Existing items will be placed after the inserted items.
     * @param index to insert the rows at
     * @param rows rows
     */
    default void insertRows(int index, TableRow... rows) {
        getElement().insertChild(index, ElementHelper.asElements(rows));
    }

    /**
     * Replaces a single row instance to the given index in this container and replaces the existing row.
     * <br><br>
     * This method has the same functionality as {@link #replaceRow(int, TableRow)}.
     *
     * @see #replaceRow(int, TableRow)
     * @param index index to set the new item to
     * @param row row
     */
    default void setRow(int index, TableRow row) {
        replaceRow(index, row);
    }

    /**
     * Replaces a single row instance to the given index in this container and replaces the existing row.
     * @param index index to set the new item to
     * @param row row
     */
    default void replaceRow(int index, TableRow row) {
        getElement().setChild(index, row.getElement());
    }

    /**
     * Removes the row with the given index. Noop, if no row has been found for that index.
     * @param index index to remove
     */
    default void removeRow(int index) {
        getRow(index).ifPresent(this::removeRows);
    }

    /**
     * Removes the given rows from this instance. Items, that are not child of this instance, will lead to an
     * exception.
     * @param rows rows to remove
     */
    default void removeRows(TableRow... rows) {
        getElement().removeChild(ElementHelper.asElements(rows));
    }

    /**
     * Removes all rows.
     */
    default void removeAllRows() {
        removeRows(streamRows().toArray(TableRow[]::new));
    }

    /**
     * Returns the rows of this instance as a list. Changes to this list do not affect this instance.
     * Empty if no rows have been added yet.
     * @return rows as list
     */
    default List<TableRow> getRows() {
        return streamRows().collect(Collectors.toList());
    }

    /**
     * Returns the row with the given index or an empty optional, if the index is out of bounds.
     * @param index index
     * @return row or empty
     */
    default Optional<TableRow> getRow(int index) {
        return streamRows().skip(index).findFirst();
    }

    /**
     * Returns the rows of this instance as a stream. Empty if no rows have been added yet.
     * @return rows as stream
     */
    default Stream<TableRow> streamRows() {
        return getChildren().filter(c -> c instanceof TableRow).map(c -> (TableRow) c);
    }

    /**
     * Returns the children of this instance as a stream of components.
     * @return children
     */
    Stream<Component> getChildren();

}
