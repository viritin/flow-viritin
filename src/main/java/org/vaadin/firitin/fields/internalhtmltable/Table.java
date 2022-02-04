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

import java.util.stream.Stream;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Tag;

/**
 * Represents the html table element ({@code <table>}). Can contain
 * <ul>
 *     <li>a caption</li>
 *     <li>a column group</li>
 *     <li>a thead</li>
 *     <li>a tfoot</li>
 *     <li>a tbody or a set of table rows</li>
 * </ul>
 * <br><br>
 * Sub elements except for rows are created when calling the respective getter, for instance {@link #getHead()}. Since
 * {@code <table>} expects a certain order of elements, this class takes care of positioning them in the correct
 * order (e.g. head {@code <thead>}, then {@code <tbody>} and then {@code <tfoot>}, etc).
 * <br><br>
 * Also a table must not contain rows as direct children, when having a {@code <tbody>}. Therefore, all previously to the
 * table assigned rows are automatically assigned to the {@code <tbody>}, when {@link #getBody()} is called.
 * <br><br>
 * Also all subsequent calls to any table row method are automatically delegated to the {@code <tbody>}.
 *
 * @author Stefan Uebe
 * @see TableCaption
 * @see TableColumnGroup
 * @see TableRow
 * @see TableHead
 * @see TableBody
 * @see TableFoot
 */
@Tag("table")
public class Table extends HtmlComponent implements TableRowContainer {

    private TableCaption caption;

    private TableColumnGroup columnGroup;

    private TableHead head;
    private TableBody body;
    private TableFoot foot;

    /**
     * Returns the {@link TableCaption} for this instance. Creates a new instance on the first call.
     * @return caption
     */
    public TableCaption getCaption() {
        if (caption == null) {
            caption = insertIndexedChild(new TableCaption());
        }

        return caption;
    }

    /**
     * Returns the {@link TableColumnGroup} for this instance. Creates a new instance on the first call.
     * @return column group
     */
    public TableColumnGroup getColumnGroup() {
        if (columnGroup == null) {
            columnGroup = insertIndexedChild(new TableColumnGroup(), caption);
        }

        return columnGroup;
    }

    /**
     * Returns the {@link TableHead} for this instance. Creates a new instance on the first call.
     * @return table head
     */
    public TableHead getHead() {
        if (head == null) {
            head = insertIndexedChild(new TableHead(), caption, columnGroup);
        }

        return head;
    }

    /**
     * Removes the head instance from this table.
     * <br><br>
     * Does <b>not</b> move any rows from the head to the table itself. That has
     * to be done manually.
     */
    public void removeHead() {
        if (head != null) {
            getElement().removeChild(head.getElement());
        }
    }

    /**
     * Removes the foot instance from this table.
     * <br><br>
     * Does <b>not</b> move any rows from the foot to the table itself. That has
     * to be done manually.
     */
    public void removeFoot() {
        if (foot != null) {
            getElement().removeChild(foot.getElement());
        }
    }


    /**
     * Removes the body instance from this table.
     * <br><br>
     * Does <b>not</b> move any rows from the body to the table itself. That has
     * to be done manually.
     */
    public void removeBody() {
        if (body != null) {
            getElement().removeChild(body.getElement());
        }
    }

    /**
     * Removes the caption instance from this table.
     */
    public void removeCaption() {
        if (caption != null) {
            getElement().removeChild(caption.getElement());
        }
    }

    /**
     * Removes the column group instance from this table.
     */
    public void removeColumnGroup() {
        if (columnGroup != null) {
            getElement().removeChild(columnGroup.getElement());
        }
    }

    /**
     * Returns the {@link TableBody} for this instance. Creates a new instance on the first call.
     * <br><br>
     * When rows have been assigned to this table instance before calling this method, these rows will
     * be reassigned to the body to prevent creating an invalid dom structure.
     * <br><br>
     * Any subsequent calls to this table's {@link #addRows(TableRow...)} method will be delegated to the
     * body's one.
     * @return table body
     */
    public TableBody getBody() {
        if (body == null) {
            body = insertIndexedChild(new TableBody(), caption, columnGroup, head);
            body.addRows(streamRows().toArray(TableRow[]::new));
        }

        return body;
    }

    /**
     * Returns the {@link TableFoot} for this instance. Creates a new instance on the first call.
     * @return foot
     */
    public TableFoot getFoot() {
        if (foot == null) {
            foot = insertIndexedChild(new TableFoot(), caption, columnGroup, head, body);
        }

        return foot;
    }

    /**
     * Calculates an 0-based index based on the given components. Each non null component will increase
     * the index by 1. That means, passing (component1, component2, null, component3) will return an index
     * of 3. Passing nothing returns 0.
     * @param predecessors predecessors (null values allowed)
     * @return index based on the given non null components.
     */
    private int calculateIndex(Component... predecessors) {
        int index = 0;
        for (Component predecessor : predecessors) {
            if (predecessor != null) {
                index++;
            }
        }
        return index;
    }

    /**
     * Adds a child to this table with an index based on the given predecessors. Uses {@link #calculateIndex(Component...)}.
     * @param childToAdd child to add
     * @param predecessors predecessors (null values allowed)
     * @param <T> type of child to add
     * @return the parameter childToAdd (for subsequent calls)
     */
    private <T extends Component> T insertIndexedChild(T childToAdd, Component... predecessors) {
        int index = calculateIndex(predecessors);
        getElement().insertChild(index, childToAdd.getElement());
        return childToAdd;
    }

    /**
     * Adds the given list of rows.
     * <br><br>
     * When this table has a body, it will automatically delegate the call to the body's respective method.
     * @param rows rows
     */
    @Override
    public void addRows(TableRow... rows) {
        if (body != null) {
            body.addRows(rows);
        } else {
            TableRowContainer.super.addRows(rows);
        }
    }

    /**
     * Inserts the given rows at the given index to this instance. Existing items will be placed after the inserted items.
     * <br><br>
     * When this table has a body, it will automatically delegate the call to the body's respective method.
     * @param rows rows
     */
    @Override
    public void insertRows(int index, TableRow... rows) {
        if (body != null) {
            body.insertRows(index, rows);
        } else {
            TableRowContainer.super.insertRows(index, rows);
        }
    }

    /**
     * Replaces a single row instance to the given index in this container and replaces the existing row.
     * <br><br>
     * When this table has a body, it will automatically delegate the call to the body's respective method.
     * @param index index to set the new item to
     * @param row row
     */
    @Override
    public void replaceRow(int index, TableRow row) {
        if (body != null) {
            body.replaceRow(index, row);
        } else {
            TableRowContainer.super.replaceRow(index, row);
        }
    }

    /**
     * Removes the given rows from this instance. Items, that are not child of this instance, will lead to an
     * exception.
     * <br><br>
     * When this table has a body, it will automatically delegate the call to the body's respective method.
     * @param rows rows to remove
     */
    @Override
    public void removeRows(TableRow... rows) {
        if (body != null) {
            body.removeRows(rows);
        } else {
            TableRowContainer.super.removeRows(rows);
        }
    }

    /**
     * Returns the rows of this instance as a stream. Empty if no rows have been added yet.
     * <br><br>
     * When this table has a body, it will automatically delegate the call to the body's respective method.
     * @return rows as stream
     */
    @Override
    public Stream<TableRow> streamRows() {
        if (body != null) {
            return body.streamRows();
        } else {
            return TableRowContainer.super.streamRows();
        }
    }

    /**
     * Adds a single row instance to this container. The created row is returned for further configuration.
     * <br><br>
     * When this table has a body, it will automatically delegate the call to the body's respective method.
     * @return the created row
     */
    @Override
    public TableRow addRow() {
        return TableRowContainer.super.addRow();
    }

    /**
     * Adds multiple rows to this instance based on the given integer (must be greater than 0).
     * The created rows are returned as an array, that can be used for further configuration.
     * <br><br>
     * When this table has a body, it will automatically delegate the call to the body's respective method.
     * @param rows amount of rows to add
     * @return created row objects
     */
    @Override
    public TableRow[] addRows(int rows) {
        return TableRowContainer.super.addRows(rows);
    }

    /**
     * Inserts a single row instance at the given index to this container. Existing items will be placed after the inserted items.
     * The created row is returned for further configuration.
     * <br><br>
     * When this table has a body, it will automatically delegate the call to the body's respective method.
     *
     * @return the created row
     */
    @Override
    public TableRow insertRow(int index) {
        return TableRowContainer.super.insertRow(index);
    }

    /**
     * Replaces a single row instance to the given index in this container and replaces the existing row.
     * <br><br>
     * This method has the same functionality as {@link #replaceRow(int, TableRow)}.
     * <br><br>
     * When this table has a body, it will automatically delegate the call to the body's respective method.
     *
     * @see #replaceRow(int, TableRow)
     * @param index index to set the new item to
     * @param row row
     */
    @Override
    public void setRow(int index, TableRow row) {
        TableRowContainer.super.setRow(index, row);
    }

    /**
     * Removes all rows.
     * <br><br>
     * When this table has a body, it will automatically delegate the call to the body's respective method.
     */
    @Override
    public void removeAllRows() {
        TableRowContainer.super.removeAllRows();
    }

    /**
     * Removes the row with the given index. Noop, if no row has been found for that index.
     * <br><br>
     * When this table has a body, it will automatically delegate the call to the body's respective method.
     * @param index index to remove
     */
    @Override
    public void removeRow(int index) {
        TableRowContainer.super.removeRow(index);
    }


}
