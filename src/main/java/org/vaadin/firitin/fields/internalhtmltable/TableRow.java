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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.dom.Element;

/**
 * Represents the table row element ({@code <trow>}). Can contain a list of table cells.
 *
 * @see TableCell
 * @see TableDataCell
 * @see TableHeaderCell
 *
 * @author Stefan Uebe
 */
@Tag("tr")
public class TableRow extends HtmlComponent {

    public TableRow() {
    }

    /**
     * Creates new Table row with given components wrapped as cells.
     * @param cells the components to be placed in cells of the row
     */
    public TableRow(Component... cells) {
        addCells(cells);
    }

    /**
     * Adds a data cell instance to this row. The created cell is returned for further configuration.
     * @return the created cell
     */
    public TableDataCell addDataCell() {
        TableDataCell cell = new TableDataCell();
        addCells(cell);
        return cell;
    }

    /**
     * Adds a header cell instance to this row. The created cell is returned for further configuration.
     * @return the created cell
     */
    public TableHeaderCell addHeaderCell() {
        TableHeaderCell cell = new TableHeaderCell();
        addCells(cell);
        return cell;
    }

    /**
     * Wrapps given components into header cells and adds them to this instance.
     * @param components cells to add
     */
    public void addHeaderCells(Component... components) {
        for (int i = 0; i < components.length; i++) {
            TableHeaderCell td = addHeaderCell();
            td.add(components[i]);
        }
    }


    /**
     * Adds header cells with given string content
     *
     * @param contents strings to add to row
     */
    public void addHeaderCells(String... contents) {
        for (int i = 0; i < contents.length; i++) {
            TableHeaderCell td = addHeaderCell();
            td.add(contents[i]);
        }
    }

    /**
     * Adds multiple data cells to this instance based on the given integer (must be greater than 0).
     * The created cells are returned as an array, that can be used for further configuration.
     * @param cells amount of cells to add
     * @return created cell objects
     */
    public TableDataCell[] addDataCells(int cells) {
        TableDataCell[] cellsObjects = IntStream.range(0, cells).mapToObj(i -> new TableDataCell()).toArray(TableDataCell[]::new);
        addCells(cellsObjects);
        return cellsObjects;
    }

    /**
     * Adds multiple header cells to this instance based on the given integer (must be greater than 0).
     * The created cells are returned as an array, that can be used for further configuration.
     * @param cells amount of cells to add
     * @return created cell objects
     */
    public TableHeaderCell[] addHeaderCells(int cells) {
        TableHeaderCell[] cellsObjects = IntStream.range(0, cells).mapToObj(i -> new TableHeaderCell()).toArray(TableHeaderCell[]::new);
        addCells(cellsObjects);
        return cellsObjects;
    }

    /**
     * Adds the given cells to this instance.
     * @param cells cells to add
     */
    public void addCells(TableCell... cells) {
        getElement().appendChild(Arrays.stream(cells).map(Component::getElement).toArray(Element[]::new));
    }

    /**
     * Wrapps given components into table cells and adds them to this instance.
     * @param components cells to add
     */
    public void addCells(Component... components) {
        for (int i = 0; i < components.length; i++) {
            TableDataCell td = addDataCell();
            td.add(components[i]);
        }
    }

    /**
     * Adds data cells with given string content
     *
     * @param contents strings to add to row
     */
    public void addCells(String... contents) {
        for (int i = 0; i < contents.length; i++) {
            TableDataCell td = addDataCell();
            td.add(contents[i]);
        }
    }

    /**
     * Removes the given cells from this instance. Cells, that are not child of this instance, will lead to an
     * exception.
     * @param cells cells to remove
     */
    public void removeCells(TableCell... cells) {
        getElement().removeChild(Arrays.stream(cells).map(Component::getElement).toArray(Element[]::new));
    }

    /**
     * Removes the cell with the given index. Noop, if no cell has been found for that index.
     * @param index index to remove
     */
    public void removeCell(int index) {
        getCell(index).ifPresent(this::removeCells);
    }

    /**
     * Removes all cells from this instance.
     */
    public void removeAllCells() {
        removeCells(streamCells().toArray(TableCell[]::new));
    }

    /**
     * Removes the data cell with the given index. Noop, if no data cell has been found for that index.
     * <br><br>
     * The index is expected to relate on data cells only, which means, that any header cells are ignores / not counted.
     * For instance in a row with the two cells (header, data), this method expects a 0 to map the data cell instance
     * (whereas {@link #getCell(int)} would map it with 1).
     *
     * @param dataCellIndex index to remove
     */
    public void removeDataCell(int dataCellIndex) {
        getDataCell(dataCellIndex).ifPresent(this::removeCells);
    }

    /**
     * Removes all data cells from this instance. Header cells are kept. Noop if the row is empty or contains only
     * header cells.
     */
    public void removeAllDataCells() {
        removeCells(streamDataCells().toArray(TableCell[]::new));
    }

    /**
     * Removes the header cell with the given index. Noop, if no header cell has been found for that index.
     * <br><br>
     * The index is expected to relate on header cells only, which means, that any header cells are ignores / not counted.
     * For instance in a row with the two cells (data, header), this method expects a 0 to map the header cell instance
     * (whereas {@link #getCell(int)} would map it with 1).
     *
     * @param headerCellIndex index to remove
     */
    public void removeHeaderCell(int headerCellIndex) {
        getHeaderCell(headerCellIndex).ifPresent(this::removeCells);
    }

    /**
     * Removes all header cells from this instance. Data cells are kept. Noop if the row is empty or contains only
     * data cells.
     */
    public void removeAllHeaderCells() {
        removeCells(streamHeaderCells().toArray(TableCell[]::new));
    }

    /**
     * Returns all cells of this row as a list. Empty when this row has no cells.
     * @return cells
     */
    public List<TableCell> getCells() {
        return streamCells().collect(Collectors.toList());
    }

    /**
     * Returns all data cells of this row as a list. Returns an empty list, if this row is empty or only contains
     * header cells.
     * @return data cells
     */
    public List<TableDataCell> getDataCells() {
        return streamDataCells().collect(Collectors.toList());
    }

    /**
     * Returns all header cells of this row as a list. Returns an empty list, if this row is empty or only contains
     * data cells.
     * @return header cells
     */
    public List<TableHeaderCell> getHeaderCells() {
        return streamHeaderCells().collect(Collectors.toList());
    }

    /**
     * Returns the cell with the given index or an empty optional, if the index is out of bounds.
     * @param index index
     * @return cell or empty
     */
    public Optional<TableCell> getCell(int index) {
        return streamCells().skip(index).findFirst();
    }

    /**
     * Returns the header cell with the given index or an empty optional, if the index is out of bounds or if no header
     * cell has been found for that index.
     * <br><br>
     * The index is expected to relate on header cells only, which means, that any header cells are ignores / not counted.
     * For instance in a row with the two cells (data, header), this method expects a 0 to map the header cell instance
     * (whereas {@link #getCell(int)} would map it with 1).
     *
     * @param headerCellIndex index
     * @return cell or empty
     */
    public Optional<TableHeaderCell> getHeaderCell(int headerCellIndex) {
        return streamHeaderCells().skip(headerCellIndex).findFirst();
    }

    /**
     * Returns the data cell with the given index or an empty optional, if the index is out of bounds or if no data
     * cell has been found for that index.
     * <br><br>
     * The index is expected to relate on data cells only, which means, that any data cells are ignores / not counted.
     * For instance in a row with the two cells (header, data), this method expects a 0 to map the data cell instance
     * (whereas {@link #getCell(int)} would map it with 1).
     *
     * @param dataCellIndex index
     * @return cell or empty
     */
    public Optional<TableDataCell> getDataCell(int dataCellIndex) {
        return streamDataCells().skip(dataCellIndex).findFirst();
    }

    /**
     * Returns all cells of this row as a stream. Empty when this row has no cells.
     * @return cells
     */
    public Stream<TableCell> streamCells() {
        return getChildren().filter(c -> c instanceof TableCell).map(c -> (TableCell) c);
    }

    /**
     * Returns all data cells of this row as a stream. Returns an empty stream, if this row is empty or only contains
     * header cells.
     * @return data cells
     */
    public Stream<TableDataCell> streamDataCells() {
        return getChildren().filter(c -> c instanceof TableDataCell).map(c -> (TableDataCell) c);
    }

    /**
     * Returns all header cells of this row as a stream. Returns an empty stream, if this row is empty or only contains
     * data cells.
     * @return header cells
     */
    public Stream<TableHeaderCell> streamHeaderCells() {
        return getChildren().filter(c -> c instanceof TableHeaderCell).map(c -> (TableHeaderCell) c);
    }
}
