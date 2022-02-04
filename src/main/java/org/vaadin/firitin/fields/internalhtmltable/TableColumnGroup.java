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
import com.vaadin.flow.component.Tag;

/**
 * Represents a table column group element ({@code <colgroup>}). Contains a list of column elements.
 *
 * @see TableColumn
 *
 * @author Stefan Uebe
 */
@Tag("colgroup")
public class TableColumnGroup extends Component {

    /**
     * Adds a single column instance to this group. The created column is returned for further configuration.
     * @return the created column
     */
    public TableColumn addColumn() {
        TableColumn column = new TableColumn();
        addColumns(column);
        return column;
    }

    /**
     * Adds the given columns to this instance.
     * @param columns columns
     */
    public void addColumns(TableColumn... columns) {
        getElement().appendChild(ElementHelper.asElements(columns));
    }

    /**
     * Adds multiple columns to this instance based on the given integer (must be greater than 0).
     * The created columns are returned as an array, that can be used for further configuration.
     * @param columns amount of columns to add
     * @return created column objects
     */
    public TableColumn[] addColumns(int columns) {
        if (columns < 1) {
            throw new IllegalArgumentException("Columns must be greater than 0");
        }

        TableColumn[] colObjects = IntStream
                .range(0, columns)
                .mapToObj(i -> new TableColumn())
                .toArray(TableColumn[]::new);
        addColumns(colObjects);
        return colObjects;
    }

    /**
     * Inserts a single column instance at the given index to this group. Existing items will be placed after the inserted items.
     * The created column is returned for further configuration.
     *
     * @param index index to insert the column at
     * @return the created column
     */
    public TableColumn insertColumn(int index) {
        TableColumn tableColumn = new TableColumn();
        insertColumns(index, tableColumn);
        return tableColumn;
    }

    /**
     * Inserts the given columns at the given index to this instance. Existing items will be placed after the inserted items.
     * @param index index to insert the columns at
     * @param columns columns
     */
    public void insertColumns(int index, TableColumn... columns) {
        getElement().insertChild(index, ElementHelper.asElements(columns));
    }

    /**
     * Replaces a single column instance to the given index in this group and replaces the existing column.
     * <br><br>
     * This method has the same functionality as {@link #replaceColumn(int, TableColumn)}.
     *
     * @see #replaceColumn(int, TableColumn)
     * @param index index to set the new item to
     * @param column column
     */
    public void setColumn(int index, TableColumn column) {
        replaceColumn(index, column);
    }

    /**
     * Replaces a single column instance to the given index in this group and replaces the existing column.
     * @param index index to set the new item to
     * @param column column
     */
    public void replaceColumn(int index, TableColumn column) {
        getElement().setChild(index, column.getElement());
    }

    /**
     * Removes the column with the given index. Noop, if no column has been found for that index.
     * @param index index to remove
     */
    public void removeColumn(int index) {
        getColumn(index).ifPresent(this::removeColumns);
    }

    /**
     * Removes the given columns from this instance. Items, that are not child of this instance, will lead to an
     * exception.
     * @param columns columns to remove
     */
    public void removeColumns(TableColumn... columns) {
        getElement().removeChild(ElementHelper.asElements(columns));
    }

    /**
     * Removes all columns.
     */
    public void removeAllColumns() {
        removeColumns(streamColumns().toArray(TableColumn[]::new));
    }

    /**
     * Returns the columns of this instance as a list. Changes to this list do not affect this instance.
     * Empty if no columns have been added yet.
     * @return columns as list
     */
    public List<TableColumn> getColumns() {
        return streamColumns().collect(Collectors.toList());
    }

    /**
     * Returns the column with the given index or an empty optional, if the index is out of bounds.
     * @param index index
     * @return column or empty
     */
    public Optional<TableColumn> getColumn(int index) {
        return streamColumns().skip(index).findFirst();
    }

    /**
     * Returns the columns of this instance as a stream. Empty if no columns have been added yet.
     * @return columns as stream
     */
    public Stream<TableColumn> streamColumns() {
        return getChildren().filter(c -> c instanceof TableColumn).map(c -> (TableColumn) c);
    }
}
