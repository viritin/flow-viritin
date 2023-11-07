package org.vaadin.firitin;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.Tree;
import org.vaadin.firitin.components.TreeTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route
public class ClassHierarchyTreeAndTreeTableView extends VerticalLayout {

    public static abstract class AbstractPlace {
        private String id;

        private String name;

        private String description;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class City extends AbstractPlace {

        private List<Street> streets = new ArrayList<>();

        public List<Street> getStreets() {
            return streets;
        }

        public void setStreets(List<Street> streets) {
            this.streets = streets;
        }
    }

    public static class Street extends AbstractPlace {

        private List<House> houses = new ArrayList<>();

        public List<House> getHouses() {
            return houses;
        }

        public void setHouses(List<House> houses) {
            this.houses = houses;
        }
    }

    public static class House extends AbstractPlace {

    }

    private List<City> cities = new ArrayList<>();


    public ClassHierarchyTreeAndTreeTableView() {

        createDemoData();


        TreeTable<AbstractPlace> treeGrid = new TreeTable<>(AbstractPlace.class);
        treeGrid.removeAllColumns();
        treeGrid.addHierarchyColumn(AbstractPlace::getName).setHeader("Name");
        treeGrid.addColumn("description").setHeader("Description");
        treeGrid.addColumn("id").setHeader("ID");
        treeGrid.setLevelModel(i -> {
            if(i instanceof City c) {
                return 0;
            }
            if(i instanceof Street s) {
                return 1;
            }
            if(i instanceof House h) {
                return 2;
            }
            return 0;
        });
        List<AbstractPlace> rootItems = new ArrayList<>();
        rootItems.addAll(cities);

        treeGrid.setRootItems(rootItems, i -> {
            List<AbstractPlace> children = new ArrayList<>();
            if(i instanceof City c) {
                children.addAll(c.getStreets());
                return children;
            }
            if (i instanceof Street s) {
                children.addAll(s.getHouses());
                return children;
            }
            return Collections.emptyList();
        });

        add(treeGrid);

        Tree<AbstractPlace> tree = new Tree<>();
        tree.setItemLabelGenerator(AbstractPlace::getName);
        tree.setItems(cities, 
                (Tree.ChildrenProvider<City>) c -> c.getStreets(), 
                (Tree.ChildrenProvider<Street>) s -> s.getHouses()
        );
        add(tree);

    }

    private void createDemoData() {
        // Create some test data
        City city = new City();
        city.setId("1");
        city.setName("Helsinki");
        city.setDescription("Capital of Finland");
        cities.add(city);

        Street street = new Street();
        street.setId("2");
        street.setName("Mannerheimintie");
        street.setDescription("Main street of Helsinki");
        city.getStreets().add(street);

        House house = new House();
        house.setId("3");
        house.setName("Stockmann");
        house.setDescription("Main building of Helsinki :-)");
        street.getHouses().add(house);


    }

}
