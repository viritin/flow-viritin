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

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.testdomain.Person;
import org.vaadin.firitin.testdomain.Service;

import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author mstahv
 */
@Route
public class Grids extends VerticalLayout {

    public Grids() {

        List<Person> list = Service.getListOfPersons(1000);

        Person somePerson = list.get(700);

        VGrid<Person> grid = new VGrid<>(Person.class);

        grid.setItems(list);

        grid.scrollItem(somePerson);
        add(grid);

        VGrid<Person> lazyLoaded = new VGrid<>(Person.class);
        lazyLoaded.setItems(query ->
                list.subList(
                query.getOffset(),
                Math.min(list.size(), query.getOffset() + query.getLimit())
        ).stream());

        lazyLoaded.scrollItem(somePerson);
        add(lazyLoaded);

    }
    
}
