package org.vaadin.firitin;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.function.ValueProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.testdomain.Address;
import org.vaadin.firitin.testdomain.PersRecord;
import org.vaadin.firitin.testdomain.Person;
import org.vaadin.firitin.util.PropertyRef;
import org.vaadin.firitin.util.PropertyRefs;

import java.util.List;

/**
 * Prototype for referring to bean properties with getter method references
 * instead of strings, like Spring Data 2026.0 does for sorting and projections.
 */
public class PropertyRefTest {

    public static class Customer {
        private String name;
        private boolean active;
        private Address address = new Address();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    @Test
    public void resolvesPropertyNamesFromGetterReferences() {
        Assertions.assertEquals("firstName",
                PropertyRef.of(Person::getFirstName).getPropertyName());
        // "is" prefixed boolean getter
        Assertions.assertEquals("active",
                PropertyRef.of(Customer::isActive).getPropertyName());
        // record accessor, no JavaBeans prefix at all
        Assertions.assertEquals("name",
                PropertyRef.of(PersRecord::name).getPropertyName());
        // works for any serializable functional interface, not only PropertyRef
        Assertions.assertEquals("lastName",
                PropertyRefs.propertyName((ValueProvider<Person, String>) Person::getLastName));
    }

    @Test
    public void resolvesNestedPropertyPaths() {
        Assertions.assertEquals("address.street",
                PropertyRef.of(Customer::getAddress).then(Address::getStreet).getPropertyName());
    }

    @Test
    public void nestedRefReadsTheValueNullSafely() {
        PropertyRef<Customer, String> street = PropertyRef.of(Customer::getAddress)
                .then(Address::getStreet);
        Customer customer = new Customer();
        customer.getAddress().setStreet("Ruukinkatu 2");
        Assertions.assertEquals("Ruukinkatu 2", street.apply(customer));

        customer.setAddress(null);
        Assertions.assertNull(street.apply(customer));
    }

    @Test
    public void plainLambdaFailsFastWithAHelpfulMessage() {
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> PropertyRef.of((Person p) -> p.getFirstName()).getPropertyName());
        Assertions.assertTrue(e.getMessage().contains("method reference"), e.getMessage());
    }

    @Test
    public void gridColumnsCanBeConfiguredWithGetterReferences() {
        VGrid<Person> grid = new VGrid<>(Person.class, false);
        grid.setColumns(Person::getFirstName, Person::getLastName, Person::getAge);

        Assertions.assertEquals(List.of("firstName", "lastName", "age"), columnKeys(grid));
        // the string based API produces exactly the same columns
        VGrid<Person> reference = new VGrid<>(Person.class, false);
        reference.setColumns("firstName", "lastName", "age");
        Assertions.assertEquals(columnKeys(reference), columnKeys(grid));
    }

    @Test
    public void gridColumnsWorkWithRecordsToo() {
        VGrid<PersRecord> grid = new VGrid<>(PersRecord.class, false);
        grid.setColumns(PersRecord::name, PersRecord::age);

        Assertions.assertEquals(List.of("name", "age"), columnKeys(grid));
    }

    @Test
    public void gridSupportsNestedPropertyPaths() {
        VGrid<Customer> grid = new VGrid<>(Customer.class, false);
        grid.setColumns(PropertyRef.of(Customer::getName),
                PropertyRef.of(Customer::getAddress).then(Address::getStreet));

        Assertions.assertEquals(List.of("name", "address.street"), columnKeys(grid));
    }

    @Test
    public void columnsCanBeAddedAndLookedUpWithGetterReferences() {
        VGrid<Person> grid = new VGrid<>(Person.class, false);
        grid.addColumns(Person::getFirstName, Person::getLastName);
        VGrid.VColumn<Person> age = grid.addPropertyColumn(Person::getAge);
        age.setHeader("Ikä");

        Assertions.assertEquals(List.of("firstName", "lastName", "age"), columnKeys(grid));
        Assertions.assertSame(age, grid.getColumnByKey(Person::getAge));
        Assertions.assertEquals("Ikä", grid.getColumnByKey(Person::getAge).getHeaderText());
        Assertions.assertNull(grid.getColumnByKey(Person::getJoinTime));
    }

    @Test
    public void columnsCanBeSortedRemovedAndReorderedWithGetterReferences() {
        VGrid<Person> grid = new VGrid<>(Person.class, false);
        grid.setColumns(Person::getFirstName, Person::getLastName, Person::getAge);

        grid.setSortableColumns(Person::getLastName);
        Assertions.assertTrue(grid.getColumnByKey(Person::getLastName).isSortable());
        Assertions.assertFalse(grid.getColumnByKey(Person::getFirstName).isSortable());

        grid.setColumnOrder(Person::getAge, Person::getLastName, Person::getFirstName);
        Assertions.assertEquals(List.of("age", "lastName", "firstName"), columnKeys(grid));

        grid.removeColumnByKey(Person::getAge);
        Assertions.assertEquals(List.of("lastName", "firstName"), columnKeys(grid));

        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> grid.setColumnOrder(Person::getAge, Person::getLastName));
        Assertions.assertTrue(e.getMessage().contains("age"), e.getMessage());
    }

    @Test
    public void customRendererColumnCanBeKeyedWithAGetterReference() {
        VGrid<Person> grid = new VGrid<>(Person.class, false);
        VGrid.VColumn<Person> column = (VGrid.VColumn<Person>) grid
                .addColumn(person -> person.getFirstName() + " " + person.getLastName());
        column.withKey(Person::getFirstName);

        Assertions.assertSame(column, grid.getColumnByKey(Person::getFirstName));
    }

    @Test
    public void backendSortPropertiesCanBeGivenAsGetterReferences() {
        VGrid<Person> grid = new VGrid<>(Person.class, false);
        VGrid.VColumn<Person> column = grid.addPropertyColumn(Person::getFirstName)
                .withSortProperties(Person::getFirstName, Person::getLastName);

        Assertions.assertEquals(List.of("firstName", "lastName"),
                column.getSortOrder(SortDirection.ASCENDING)
                        .map(QuerySortOrder::getSorted).toList());
    }

    private static List<String> columnKeys(Grid<?> grid) {
        return grid.getColumns().stream().map(Grid.Column::getKey).toList();
    }
}
