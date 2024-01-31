/*
 * Copyright 2019 Viritin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.firitin;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.testdomain.PersRecord;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author mstahv
 */
@Route
public class RecordsWithGrid extends VerticalLayout {


    public RecordsWithGrid() {
        withRecords();
    }

    private void withRecords() {
        VGrid<PersRecord> grid = new VGrid<>(PersRecord.class);

        List<PersRecord> people = Arrays.asList(
                new PersRecord("Alice", 25),
                new PersRecord("Bob", 30),
                new PersRecord("Charlie", 22)
        );
        grid.setItems(people);

        boolean nameFirst = grid.getColumns().get(0).getKey().equals("name");
        assert nameFirst;

        grid.setColumns("age", "name");
        nameFirst = grid.getColumns().get(0).getKey().equals("name");
        assert !nameFirst;

        add(grid);
    }

}
