package org.vaadin.firitin;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.testdomain.Person;
import org.vaadin.firitin.testdomain.Service;

/**
 * The property name of a column given as a method reference to the getter, and
 * what VGrid can do once it knows which property a column is for.
 */
@Route
public class GridColumnsWithoutStrings extends VerticalLayout {

    public GridColumnsWithoutStrings() {
        add(new RichText().withMarkDown("""
                # Grid columns without property name strings

                Vaadin core Grid names properties with strings, which the compiler
                does not check and the IDE's rename refactoring does not follow.
                VGrid takes a method reference to the getter anywhere a property
                name is expected:

                ```java
                setColumns(Person::getFirstName, Person::getLastName, Person::getAge);
                ```

                **The first grid** is built that way. Its *Age* column is then
                re-rendered with a component, in place:

                ```java
                setComponentRenderer(Person::getAge, person -> new AgeBadge(person.getAge()));
                ```

                Only the rendering changed. The column kept its position, its header
                and its sorting — click the *Age* header and it still sorts by the
                number, not by the badge. Without this you would have had to remove
                every column and add them back around the custom one.

                **The second grid** has no bean type at all and adds its columns one
                by one. `addColumn(Person::getFirstName)` is enough: the header, the
                column key and the sorting all come from the getter, so there is no
                `setHeader("First Name")` to write and keep in sync.
                """));
        add(new PeopleGrid());
        add(new HandBuiltGrid());
    }

    /**
     * Columns picked with getter references, one of them re-rendered with a
     * component afterwards.
     */
    public static class PeopleGrid extends VGrid<Person> {
        public PeopleGrid() {
            super(Person.class, false);
            setColumns(Person::getFirstName, Person::getLastName, Person::getAge);
            setComponentRenderer(Person::getAge, person -> new AgeBadge(person.getAge()));
            setAllRowsVisible(true);
            setItems(Service.getListOfPersons(10));
        }
    }

    /**
     * No bean type, columns added one at a time, and no headers spelled out.
     */
    public static class HandBuiltGrid extends VGrid<Person> {
        public HandBuiltGrid() {
            addColumn(Person::getFirstName);
            addColumn(Person::getLastName);
            addColumn(Person::getAge);
            setAllRowsVisible(true);
            setItems(Service.getListOfPersons(10));
        }
    }

    public static class AgeBadge extends Span {
        public AgeBadge(Integer age) {
            super(age + " years");
            getElement().getThemeList().add("badge");
            if (age >= 65) {
                getElement().getThemeList().add("contrast");
            } else if (age < 18) {
                getElement().getThemeList().add("success");
            }
        }
    }
}
