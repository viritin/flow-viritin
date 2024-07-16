package org.vaadin.firitin.gridfiltering;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;

import java.util.function.Function;
import java.util.function.Predicate;

@Route
public class PersonGridWithFiltering extends Grid<Person> {

    private PersonFilterField fullNameFilter, emailFilter, professionFilter;

    public PersonGridWithFiltering() {
        super(Person.class, false);
        fullNameFilter = addFilterableColumn("Name", Person::getFullName);
        emailFilter = addFilterableColumn("Email", Person::getEmail);
        professionFilter =addFilterableColumn("Profession", Person::getProfession);

        // Initial listing
        listPersons();
    }

    public PersonFilterField addFilterableColumn(String headerText, ValueProvider<Person, String> valueProvider) {
        var filterField = new PersonFilterField(headerText, p -> valueProvider.apply(p));
        addColumn(valueProvider).setHeader(filterField);
        return filterField;
    }

    private void listPersons() {
        // filter with basic JDK features, filter fields implement JDK's Predicate
        var filteredList = Person.getListOfPeople().stream()
                .filter(fullNameFilter.and(emailFilter).and(professionFilter))
                .toList();
        // (re)set the displayed rows, just as efficient as doing refresh calls in "dataview"
        setItems(filteredList);
    }

    private class PersonFilterField extends TextField implements Predicate<Person> {

        private final Function<Person, String> property;

        public PersonFilterField(String label, Function<Person, String> property) {
            this.property = property;
            // Use lazy mode for filtering, not EAGER. LAZY is just as good for filtering, and avoids unnecessary server roundtrips.
            setValueChangeMode(ValueChangeMode.LAZY);
            addValueChangeListener(e -> {
                listPersons();
            });

            setLabel(label);
            setClearButtonVisible(true);
            addThemeVariants(TextFieldVariant.LUMO_SMALL);
            setWidthFull();
        }

        @Override
        public boolean test(Person person) {
            return property.apply(person).toLowerCase().contains(getValue().toLowerCase());
        }
    }

}