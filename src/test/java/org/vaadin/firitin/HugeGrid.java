package org.vaadin.firitin;

import org.vaadin.firitin.components.CustomLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.fields.internalhtmltable.Table;
import org.vaadin.firitin.fields.internalhtmltable.TableRow;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Route
public class HugeGrid extends VerticalLayout {

    static List<List<String>> dataset = new ArrayList<>();
    static List<HierarchicalDto> datasetHierarchical = new ArrayList<>();

    static class HierarchicalDto {
        private List<HierarchicalDto> children = new ArrayList<>();
        private List<String> strings;

        public HierarchicalDto(List<String> strings) {
            this.strings = strings;
        }

        public List<HierarchicalDto> getChildren() {
            return children;
        }

        public void setChildren(List<HierarchicalDto> children) {
            this.children = children;
        }

        public List<String> getStrings() {
            return strings;
        }

        public void setStrings(List<String> strings) {
            this.strings = strings;
        }
    }

    static {
        for(int i = 0; i < 1000; i++) {
            ArrayList<String> row = new ArrayList<>();
            for(int j = 0; j < 90; j++) {
                row.add(new String("Celll" + i + "," +j));
            }
            dataset.add(row);
        }

        HierarchicalDto dto = null;
        LinkedList<HierarchicalDto> parents = new LinkedList<>();
        Random r = new Random(0);
        for (int i = 0; i < dataset.size(); i++) {
            dto = new HierarchicalDto(dataset.get(i));
            int m = r.nextInt(3);
            if(m == 1) {
                // add to current parent
                if(!parents.isEmpty()) {
                    parents.getLast().getChildren().add(dto);
                } else {
                    // root
                    datasetHierarchical.add(dto);
                }
            } else if (m == 2) {
                // create new level
                if(!parents.isEmpty()) {
                    parents.getLast().getChildren().add(dto);
                } else {
                    // root
                    datasetHierarchical.add(dto);
                }
                parents.add(dto);
            } else if(m == 0) {
                // shrink
                if(parents.size() == 0) {
                    datasetHierarchical.add(dto);
                } else {
                    int removeN = r.nextInt(parents.size());
                    for (int j = 0; parents.isEmpty() || j < removeN; j++) {
                        parents.removeLast();
                    }
                    if(!parents.isEmpty()) {
                        parents.getLast().getChildren().add(dto);
                    } else {
                        datasetHierarchical.add(dto);
                    }
                }

            }
        }


    }

    public HugeGrid() {
        add(new Paragraph("Comparison of different ways to build huge \"report view\" of 90 columns and 1000 rows."));
        Button b = new Button("Html table using Vaadin components (heavy flexible)");
        b.addThemeVariants(ButtonVariant.LUMO_SMALL);
        b.addClickListener(e -> htmlGridUsingVaadinComponent());
        add(b);
        b = new Button("Grid (less flexible, vertical lazy loading)");
        b.addThemeVariants(ButtonVariant.LUMO_SMALL);
        b.addClickListener(e -> grid());
        add(b);
        b = new Button("TreeGrid");
        b.addThemeVariants(ButtonVariant.LUMO_SMALL);
        b.addClickListener(e -> treegrid());
        add(b);
        b = new Button("Html table using Html component (constructs the DOM on the server side)");
        b.addThemeVariants(ButtonVariant.LUMO_SMALL);
        b.addClickListener(e -> htmlUsingHtml());
        add(b);
        b = new Button("Pre-built raw html");
        b.addThemeVariants(ButtonVariant.LUMO_SMALL);
        b.addClickListener(e -> htmlTable());
        add(b);

        b = new Button("Pre-built raw html with components aka CustomLayout");
        b.addThemeVariants(ButtonVariant.LUMO_SMALL);
        b.addClickListener(e -> customLayout());
        add(b);

        b = new Button("Cookbook customlayout with components");
        b.addThemeVariants(ButtonVariant.LUMO_SMALL);
        b.addClickListener(e -> cookbookCustomLayout());
        add(b);

    }

    private void htmlUsingHtml() {
        long currentTimeMillis = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<tr>");
        int cols = dataset.get(0).size();
        for(int i = 0; i < cols; i++) {
            sb.append("<th>");
            sb.append("Col " + i);
            sb.append("</th>");
        }
        sb.append("</tr>");

        for(int i = 0; i < dataset.size(); i++) {
            sb.append("<tr>");
            List<String> row = dataset.get(i);
            for(int j = 0; j < cols; j++) {
                sb.append("<td>");
                sb.append(row.get(j));
                sb.append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>");

        Html html = new Html(sb.toString());
        add(html);
        reportTime(currentTimeMillis);
    }

    private void cookbookCustomLayout() {
        long currentTimeMillis = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<tr>");
        int cols = dataset.get(0).size();
        sb.append("<th>");
        sb.append("Component column ");
        sb.append("</th>");
        for(int i = 0; i < cols; i++) {
            sb.append("<th>");
            sb.append("Col " + i);
            sb.append("</th>");
        }
        sb.append("</tr>");

        for(int i = 0; i < dataset.size(); i++) {
            sb.append("<tr>");
            sb.append("<td location=\"component-"+i+"\"></td>");
            int finalI = i;
            List<String> row = dataset.get(i);
            for(int j = 0; j < cols; j++) {
                sb.append("<td>");
                sb.append(row.get(j));
                sb.append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        CookbookCustomLayout customLayout = new CookbookCustomLayout(sb.toString());

        for(int i = 0 ; i < dataset.size(); i++) {
            final int finalI = i;
            customLayout.add( new Button("Hello", e -> {
                Notification.show("This is row" + finalI);
                customLayout.remove(e.getSource());
            }), "component-" + i);
        }

        add(customLayout);
        reportTime(currentTimeMillis);
    }

    private void customLayout() {
        long currentTimeMillis = System.currentTimeMillis();
        CustomLayout customLayout = new CustomLayout();
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<tr>");
        int cols = dataset.get(0).size();
        sb.append("<th>");
        sb.append("Component column ");
        sb.append("</th>");
        for(int i = 0; i < cols; i++) {
            sb.append("<th>");
            sb.append("Col " + i);
            sb.append("</th>");
        }
        sb.append("</tr>");

        for(int i = 0; i < dataset.size(); i++) {
            sb.append("<tr>");
            sb.append("<td id=\"component-"+i+"\"></td>");
            int finalI = i;
            customLayout.addComponent("component-" + i,
                    new Button("Hello", e -> {
                        Notification.show("This is row" + finalI);
                        customLayout.remove(e.getSource());
                    })
            );
            List<String> row = dataset.get(i);
            for(int j = 0; j < cols; j++) {
                sb.append("<td>");
                sb.append(row.get(j));
                sb.append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>");

        customLayout.setTemplate(sb.toString());

        add(customLayout);
        reportTime(currentTimeMillis);
    }

    private void htmlTable() {
        long currentTimeMillis = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<tr>");
        int cols = dataset.get(0).size();
        for(int i = 0; i < cols; i++) {
            sb.append("<th>");
            sb.append("Col " + i);
            sb.append("</th>");
        }
        sb.append("</tr>");

        for(int i = 0; i < dataset.size(); i++) {
            sb.append("<tr>");
            List<String> row = dataset.get(i);
            for(int j = 0; j < cols; j++) {
                sb.append("<td>");
                sb.append(row.get(j));
                sb.append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>");

        Div div = new Div();
        div.getElement().setProperty("innerHTML", sb.toString());
        add(div);
        reportTime(currentTimeMillis);
    }

    private void reportTime(long currentTimeMillis) {
        Paragraph paragraph = new Paragraph("Constructing UI in JVM took " + (System.currentTimeMillis() - currentTimeMillis) + "ms");
        long now = System.currentTimeMillis();
        // double setTimeout as TreeGrid seems to render in two phases
        paragraph.getElement().executeJs("var el = this; setTimeout(() => { setTimeout( () => {el.innerHTML = el.innerHTML + ', data transfer + client side rendering roughly ' + (Date.now() - "+now+") + 'ms'},0)},0);");
        addComponentAtIndex(getComponentCount() -1, paragraph);
    }

    private void grid() {
        long currentTimeMillis = System.currentTimeMillis();

        Grid<List<String>> grid = new Grid<>();
        List<String> firstrow = dataset.get(0);
        for(int i = 0; i < firstrow.size(); i++) {
            int finalI = i;
            grid.addColumn(l  -> l.get(finalI))
                    .setHeader("Col " + finalI);
        }

        grid.setItems(dataset);
        add(grid);
        reportTime(currentTimeMillis);
    }

    private void treegrid() {
        long currentTimeMillis = System.currentTimeMillis();

        TreeGrid<HierarchicalDto> grid = new TreeGrid<>();
        List<String> firstrow = dataset.get(0);
        grid.addHierarchyColumn(l  -> l.getStrings().get(0))
                .setHeader("Col 0").setWidth("300px");
        for(int i = 1; i < firstrow.size(); i++) {
            int finalI = i;
            grid.addColumn(l  -> l.getStrings().get(finalI))
                    .setHeader("Col " + finalI);
        }

        grid.setItems(datasetHierarchical, hierarchicalDto -> hierarchicalDto.getChildren());
        grid.expandRecursively(datasetHierarchical, 9999);
        add(grid);
        reportTime(currentTimeMillis);
    }

    private void htmlGridUsingVaadinComponent() {
        long currentTimeMillis = System.currentTimeMillis();
        Table t = new Table();

        dataset.forEach(r -> {
            TableRow tableRow = t.addRow();
            r.forEach(c -> {
                tableRow.addCells(c);
            });
        });
        add(t);
        reportTime(currentTimeMillis);
    }


    static class GridUsingTemplate extends LitTemplate {
        public GridUsingTemplate() {
        }
    }


    public static class CookbookCustomLayout extends Html {
        private Map<String, Component> locations = new HashMap<>();

        public CookbookCustomLayout(String template) {
            super(template);
        }

        public CookbookCustomLayout(InputStream stream) {
            super(stream);
        }

        public void add(Component child, String location) {
            remove(location);
            locations.put(location, child);

            // Establish parent-child relationship, but leave DOM attaching to us
            getElement().appendVirtualChild(child.getElement());

            // Attach to the specified location in the actual DOM
            getElement().executeJs("this.querySelector('[location=\"'+$0+'\"]').appendChild($1)", location,
                    child.getElement());

            // Ensure the element is removed from the DOM when it's detached
            child.addDetachListener(detachEvent -> {
                detachEvent.unregisterListener();
                getElement().executeJs("this.querySelector && this.querySelector('[location=\"'+$0+'\"]').lastChild.remove()", location);

                // Also clear the bookkeeping
                locations.remove(location, child);
            });
        }

        public void remove(String location) {
            Component oldChild = locations.remove(location);
            if (oldChild != null) {
                remove(oldChild);
            }
        }

        public void remove(Component child) {
            getElement().removeVirtualChild(child.getElement());
        }
    }
}
