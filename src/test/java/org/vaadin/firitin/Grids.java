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
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.testdomain.Person;
import org.vaadin.firitin.testdomain.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author mstahv
 */
@Route
public class Grids extends VerticalLayout {

    public Grids() {

        List<Person> list = Service.getListOfPersons(1000);


        Person somePerson = list.get(700);
        somePerson.setJoinTime(LocalDateTime.now());

        VGrid<Person> grid = new VGrid<>(Person.class)
                // Optional grid wide formatting function
                // instead of configuring all separately
                .withCellFormatter((col, value) -> {
                    // This would format all LocalDateTime cols
                    // at once:
                    if (value instanceof LocalDateTime dt) {
                        return dt.format(DateTimeFormatter.ISO_LOCAL_DATE);
                    }
                    // you could also target some by focusing col
                    // or e.g. based on column header/key. In my weather
                    // station data analyser I have for example this that
                    // hooks into all wind columns:
                    // if(col.getHeaderText().contains("Wind")) {
                    //    return "%.0f m/s".formatted(d);
                    // }

                    // nulls -> "", others String.valueOf
                    return VGrid.CellFormatter.defaultVaadinFormatting(value);
                });
        grid.setItems(list);

        var col = grid.getColumnByKey("firstName")
                .setHeader("FIRSTI")
                .getStyle()
                    .setBackground("red")
                    .setColor("white")
                    .setFont("bold 20px Arial")
                    .setTextAlign(Style.TextAlign.RIGHT)
                    .setOutline("2px dotted black")
        ;

        grid.scrollToItem(somePerson);
        add(grid);

        VGrid<Person> lazyLoaded = new VGrid<>(Person.class)
                .withColumnSelector();
        lazyLoaded.setItems(query ->
                list.subList(
                query.getOffset(),
                Math.min(list.size(), query.getOffset() + query.getLimit())
        ).stream());


        var col2 = lazyLoaded.getColumnByKey("firstName");
        col2.getStyle().set("color", "red");
        col2.setHeader("First");

        // This should now re-use the style element for "color: red"
        // and recycle that for both columns
        lazyLoaded.getColumnByKey("lastName").getStyle().set("color", "red");

        lazyLoaded.scrollToItem(somePerson);
        add(lazyLoaded);

    }
    
}
