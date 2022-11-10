package org.vaadin.firitin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.fields.internalhtmltable.Table;
import org.vaadin.firitin.fields.internalhtmltable.TableRow;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route
public class HugeGrid extends VerticalLayout {

    static List<List<String>> dataset = new ArrayList<>();

    static {
        for(int i = 0; i < 1000; i++) {
            ArrayList<String> row = new ArrayList<>();
            for(int j = 0; j < 90; j++) {
                row.add(new String("Celll" + i + "," +j));
            }
            dataset.add(row);
        }
    }

    public HugeGrid() {

        Button b = new Button("Html table using Vaadin components (heavy flexible)");
        b.addClickListener(e -> htmlGridUsingVaadinComponent());
        add(b);
        b = new Button("Grid (less flexible, vertical lazy loading)");
        b.addClickListener(e -> grid());
        add(b);
        b = new Button("Pre-built html");
        b.addClickListener(e -> htmlTable());
        add(b);

        b = new Button("Pre-built html with components");
        b.addClickListener(e -> customLayout());
        add(b);

        b = new Button("Cookbook customlayout with components");
        b.addClickListener(e -> cookbookCustomLayout());
        add(b);

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
            customLayout.add( new Button("Hello", e -> Notification.show("This is row" + finalI)), "component-" + i);
        }

        reportTime(currentTimeMillis);
        add(customLayout);
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
            customLayout.addComponent("component-" + i, new Button("Hello", e -> Notification.show("This is row" + finalI)));
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

        reportTime(currentTimeMillis);
        add(customLayout);
    }

    /*    private void htmlTable() {
            long currentTimeMillis = System.currentTimeMillis();

            Grid<List<String>> grid = new Grid<>();
            List<String> firstrow = dataset.get(0);
            for(int i = 0; i < firstrow.size(); i++) {
                int finalI = i;
                grid.addColumn(l  -> l.get(finalI))
                        .setHeader("Col " + finalI);
            }

            grid.setItems(dataset);
            add(new Paragraph("Constructing UI in JVM took" + (System.currentTimeMillis() - currentTimeMillis)));
            add(grid);
        }
    */
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
        reportTime(currentTimeMillis);
        add(div);
    }

    private void reportTime(long currentTimeMillis) {
        Paragraph paragraph = new Paragraph("Constructing UI in JVM took " + (System.currentTimeMillis() - currentTimeMillis) + "ms");
        long now = System.currentTimeMillis();
        paragraph.getElement().executeJs("var el = this; setTimeout(() => { el.innerHTML = el.innerHTML + ', data transfer + client side rendering ~ ' + (Date.now() - "+now+") + 'ms'},0);");
        add(paragraph);
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
        reportTime(currentTimeMillis);
        add(grid);
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
        reportTime(currentTimeMillis);
        add(t);
    }

    static class CustomLayout extends Div {
        Div tmpl = new Div();
        Div fakeComponentHolder = new Div();

        public CustomLayout() {
            add(tmpl, fakeComponentHolder);
        }

        public CustomLayout(String htmlTemplate) {
            this();
            setTemplate(htmlTemplate);
        }

        public void addComponent(String slot, Component c) {
            fakeComponentHolder.add(c);
            tmpl.getElement().executeJs("document.getElementById(\""+slot+"\").appendChild($0);", c.getElement());
        }

        public void setTemplate(String htmlTemplate) {
            tmpl.getElement().setProperty("innerHTML", htmlTemplate);
        }
    };

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
