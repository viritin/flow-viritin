package org.vaadin.firitin;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
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
    public void hidePropertiesDropsOnlyTheGivenColumns() {
        VGrid<Person> grid = new VGrid<>(Person.class, false);
        grid.setColumns(Person::getFirstName, Person::getLastName, Person::getAge);

        grid.hideProperties(Person::getLastName);

        Assertions.assertEquals(List.of("firstName", "age"), columnKeys(grid));
        // the remaining columns are all left visible
        Assertions.assertTrue(grid.getColumns().stream().allMatch(Grid.Column::isVisible));
    }

    @Test
    public void addColumnConfiguresTheColumnFromAGetterReference() {
        VGrid<Person> grid = new VGrid<>(Person.class, false);
        Grid.Column<Person> column = grid.addColumn(Person::getFirstName);

        Assertions.assertEquals("firstName", column.getKey());
        Assertions.assertEquals("First Name", column.getHeaderText());
        Assertions.assertTrue(column.isSortable());
    }

    @Test
    public void addColumnConfiguresGettersOfABeanlessGridToo() {
        // No bean type, so only the JavaBeans naming convention to lean on
        VGrid<Person> grid = new VGrid<>();
        Grid.Column<Person> column = grid.addColumn(Person::getAge);

        Assertions.assertEquals("age", column.getKey());
        Assertions.assertEquals("Age", column.getHeaderText());
        Assertions.assertTrue(column.isSortable());
    }

    @Test
    public void lambdasAndNonPropertyMethodsAreLeftAlone() {
        VGrid<Person> grid = new VGrid<>(Person.class, false);
        Grid.Column<Person> computed = grid
                .addColumn(p -> p.getFirstName() + " " + p.getLastName());
        Grid.Column<Person> notAProperty = grid.addColumn(Person::toString);

        Assertions.assertNull(computed.getKey());
        Assertions.assertNull(computed.getHeaderText());
        Assertions.assertFalse(computed.isSortable());
        Assertions.assertNull(notAProperty.getKey());
        Assertions.assertNull(notAProperty.getHeaderText());
    }

    @Test
    public void explicitKeyOverridesTheOneDerivedFromTheGetterReference() {
        VGrid<Person> grid = new VGrid<>(Person.class, false);
        Grid.Column<Person> column = grid.addColumn(Person::getFirstName).setKey("name");

        Assertions.assertEquals("name", column.getKey());
        Assertions.assertSame(column, grid.getColumnByKey("name"));
        // the automatically assigned key is properly released
        Assertions.assertNull(grid.getColumnByKey("firstName"));
        // and a column of that property can be added again
        Assertions.assertEquals("firstName", grid.addColumn(Person::getFirstName).getKey());
    }

    @Test
    public void explicitHeaderOverridesTheDerivedOne() {
        VGrid<Person> grid = new VGrid<>(Person.class, false);
        Grid.Column<Person> column = grid.addColumn(Person::getFirstName).setHeader("Etunimi");

        Assertions.assertEquals("Etunimi", column.getHeaderText());
        Assertions.assertEquals("firstName", column.getKey());
    }

    @Test
    public void twoColumnsOfTheSamePropertyDoNotClash() {
        VGrid<Person> grid = new VGrid<>(Person.class, false);
        Grid.Column<Person> first = grid.addColumn(Person::getFirstName);
        Grid.Column<Person> second = grid.addColumn(Person::getFirstName);

        Assertions.assertEquals("firstName", first.getKey());
        // the second one keeps the old behaviour instead of throwing
        Assertions.assertNull(second.getKey());
        Assertions.assertEquals("First Name", second.getHeaderText());
        Assertions.assertSame(first, grid.getColumnByKey(Person::getFirstName));
    }

    @Test
    public void autoConfigurationCanBeTurnedOff() {
        VGrid<Person> grid = new VGrid<>(Person.class, false);
        grid.setAutoConfigureFromGetterReferences(false);
        Grid.Column<Person> column = grid.addColumn(Person::getFirstName);

        Assertions.assertNull(column.getKey());
        Assertions.assertNull(column.getHeaderText());
        Assertions.assertFalse(column.isSortable());
    }

    @Test
    public void realWorldGridGetsItsHeadersWithoutBoilerplate() {
        PopoverView.PersonGrid grid = new PopoverView.PersonGrid();

        Assertions.assertEquals(List.of("First Name", "Last Name", "Age", "Actions"),
                grid.getColumns().stream().map(Grid.Column::getHeaderText).toList());
        Assertions.assertEquals("firstName", grid.getColumns().get(0).getKey());
    }

    @Test
    public void oneColumnOfAnOtherwiseStandardGridCanBeRenderedWithComponents() {
        VGrid<Person> grid = new VGrid<>(Person.class);
        List<String> keysBefore = columnKeys(grid);
        Grid.Column<Person> column = grid.getColumnByKey(Person::getLastName);
        column.setWidth("10em");

        grid.setComponentRenderer(Person::getLastName,
                person -> new Span(person.getLastName().toUpperCase()));

        // The column stays where it was, with everything but the rendering intact
        Assertions.assertEquals(keysBefore, columnKeys(grid));
        Assertions.assertSame(column, grid.getColumnByKey(Person::getLastName));
        Assertions.assertEquals("Last Name", column.getHeaderText());
        Assertions.assertEquals("10em", column.getWidth());
        Assertions.assertTrue(column.isSortable());
        // and in memory sorting still works on the underlying value
        Assertions.assertNotNull(column.getComparator(SortDirection.ASCENDING));

        Assertions.assertInstanceOf(ComponentRenderer.class, column.getRenderer());
        Span rendered = (Span) ((ComponentRenderer<?, Person>) column.getRenderer())
                .createComponent(new Person(1, "Jorma", "Testaaja", 40));
        Assertions.assertEquals("TESTAAJA", rendered.getText());
    }

    @Test
    public void theColumnToReRenderCanBeNamedWithAStringToo() {
        VGrid<Person> grid = new VGrid<>(Person.class);
        grid.setComponentRenderer("lastName", person -> new Span(person.getLastName()));

        Assertions.assertInstanceOf(ComponentRenderer.class,
                grid.getColumnByKey("lastName").getRenderer());
    }

    @Test
    public void reRenderingAnUnknownColumnTellsWhichKeysThereAre() {
        VGrid<Person> grid = new VGrid<>(Person.class, false);
        grid.setColumns(Person::getFirstName, Person::getLastName);

        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> grid.setComponentRenderer(Person::getAge, person -> new Span()));
        Assertions.assertTrue(e.getMessage().contains("age"), e.getMessage());
        Assertions.assertTrue(e.getMessage().contains("firstName"), e.getMessage());
    }

    @Test
    public void componentRendererCanAlsoBeSetOnTheColumn() {
        VGrid<Person> grid = new VGrid<>(Person.class);
        VGrid.VColumn<Person> column = grid.getColumnByKey(Person::getFirstName)
                .setComponentRenderer(person -> new Span(person.getFirstName()));

        Assertions.assertInstanceOf(ComponentRenderer.class, column.getRenderer());
        Assertions.assertEquals("First Name", column.getHeaderText());
    }

    @Test
    public void theDemoViewDemonstratesWhatItClaims() {
        GridColumnsWithoutStrings.PeopleGrid peopleGrid = new GridColumnsWithoutStrings.PeopleGrid();
        // The Age column is re-rendered, but keeps its place, header and sorting
        Assertions.assertEquals(List.of("firstName", "lastName", "age"), columnKeys(peopleGrid));
        Grid.Column<Person> age = peopleGrid.getColumnByKey(Person::getAge);
        Assertions.assertInstanceOf(ComponentRenderer.class, age.getRenderer());
        Assertions.assertEquals("Age", age.getHeaderText());
        Assertions.assertTrue(age.isSortable());
        Assertions.assertNotNull(age.getComparator(SortDirection.ASCENDING));

        // The second grid has no bean type, yet gets its headers from the getters
        GridColumnsWithoutStrings.HandBuiltGrid handBuilt = new GridColumnsWithoutStrings.HandBuiltGrid();
        Assertions.assertEquals(List.of("First Name", "Last Name", "Age"),
                handBuilt.getColumns().stream().map(Grid.Column::getHeaderText).toList());
    }

    @Test
    public void componentColumnsAreNotAffected() {
        VGrid<Person> grid = new VGrid<>(Person.class, false);
        Grid.Column<Person> column = grid
                .addComponentColumn(person -> new Span(person.getFirstName()));

        Assertions.assertNull(column.getKey());
        Assertions.assertNull(column.getHeaderText());
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
