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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mstahv
 */
@Route
public class AdvancedBeanIntrospectionWithGrid extends VerticalLayout {

    public interface HasName {

        default String getName() {
            return this.getLastName() + ", " + this.getFirstName();
        }
        default void setName(String name) {
            String[] split = name.split(" ");
            this.setLastName(split[0]);
            this.setFirstName(split[1]);
        }

        String getLastName();
        void setLastName(String lastName);

        String getFirstName();
        void setFirstName(String firstName);
    }

    public class MyClass implements HasName {

        private String lastName;
        private String firstName;

        @Override
        public String getLastName() {
            return this.lastName;
        }
        @Override
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        @Override
        public String getFirstName() {
            return this.firstName;
        }
        @Override
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
    }


    public AdvancedBeanIntrospectionWithGrid() {
        withRecords();
    }

    private void withRecords() {

        List<MyClass> people = new ArrayList<>();
        MyClass c = new MyClass();
        c.setName("Jormalainen Jorma");
        people.add(c);
        c = new MyClass();
        c.setName("Kallelainen Kalle");
        people.add(c);

        VGrid<MyClass> grid = new VGrid<>(MyClass.class);
        grid.setItems(people);

        add(grid);
    }

}
