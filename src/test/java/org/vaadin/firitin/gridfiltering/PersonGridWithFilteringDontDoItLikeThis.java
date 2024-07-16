package org.vaadin.firitin.gridfiltering;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.function.Consumer;

/**
 * This example is pretty much like vaadin.com/docs currently urges you to do it.
 * Don't do it. Reasoning:
 *
 *  * Uses Vaadin specific interfaces where it brings no value on top of standard JDK features
 *  * The filtering logic is spread out, making it harder to understand
 *  * Doesn't promote good programming practices
 *  * The code in general is spoiled with unnecessary things (regarding filtering itself),
 *    making it hard to figure out how to adapt the example to your needs
 */
@Route
public class PersonGridWithFilteringDontDoItLikeThis extends Div {

    public PersonGridWithFilteringDontDoItLikeThis() {
        Grid<Person> grid = new Grid<>(Person.class, false);
        Grid.Column<Person> nameColumn = grid.addColumn(Person::getFullName)
                .setWidth("230px").setFlexGrow(0);
        Grid.Column<Person> emailColumn = grid.addColumn(Person::getEmail);
        Grid.Column<Person> professionColumn = grid
                .addColumn(Person::getProfession);

        List<Person> people = Person.getListOfPeople();
        GridListDataView<Person> dataView = grid.setItems(people);
        PersonFilter personFilter = new PersonFilter(dataView);

        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();

        headerRow.getCell(nameColumn).setComponent(
                createFilterHeader("Name", personFilter::setFullName));
        headerRow.getCell(emailColumn).setComponent(
                createFilterHeader("Email", personFilter::setEmail));
        headerRow.getCell(professionColumn).setComponent(
                createFilterHeader("Profession", personFilter::setProfession));

        add(grid);
    }

    private static Component createFilterHeader(String labelText,
            Consumer<String> filterChangeConsumer) {
            NativeLabel label = new NativeLabel(labelText);
        label.getStyle().set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-xs)");
        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        textField.setWidthFull();
        textField.getStyle().set("max-width", "100%");
        textField.addValueChangeListener(
                e -> filterChangeConsumer.accept(e.getValue()));
        VerticalLayout layout = new VerticalLayout(label, textField);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
    }

    private static class PersonFilter {
        private final GridListDataView<Person> dataView;

        private String fullName;
        private String email;
        private String profession;

        public PersonFilter(GridListDataView<Person> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
            this.dataView.refreshAll();
        }

        public void setEmail(String email) {
            this.email = email;
            this.dataView.refreshAll();
        }

        public void setProfession(String profession) {
            this.profession = profession;
            this.dataView.refreshAll();
        }

        public boolean test(Person person) {
            boolean matchesFullName = matches(person.getFullName(), fullName);
            boolean matchesEmail = matches(person.getEmail(), email);
            boolean matchesProfession = matches(person.getProfession(),
                    profession);

            return matchesFullName && matchesEmail && matchesProfession;
        }

        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty()
                    || value.toLowerCase().contains(searchTerm.toLowerCase());
        }
    }


}