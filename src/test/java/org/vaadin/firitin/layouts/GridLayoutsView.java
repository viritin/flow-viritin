package org.vaadin.firitin.layouts;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.cssgrid.Area;
import org.vaadin.firitin.components.cssgrid.CssGrid;
import org.vaadin.firitin.components.cssgrid.Row;
import org.vaadin.firitin.util.ResizeObserver;

import java.util.Random;

@Route
public class GridLayoutsView extends VerticalLayout {
    private Random random = new Random(0);


    CssGrid grid;

    public GridLayoutsView() {
        add(new HorizontalLayout(
                new VButton("Basic 12 col", e -> this.basic12Col()),
                new VButton("12 col with col gap", e -> this.basic12ColWithtGap()),
                new VButton("MDC example", e -> this.mdcExample())
                , new VButton("css grid example1", e -> this.cssGridLayoutAddonExample1())
                , new VButton("css grid example flexible grid", e -> this.cssGridLayoutAddonExampleFlexibleGrid())
                , new VButton("areas", e -> this.cssGridExampleAreaLayout())
                , new VButton("areas & responsive grid", e -> this.responsiveGrid())
        ));
        //basic12Col();
        //mdcExample();
        //cssGridExampleAreaLayout();
        responsiveGrid();
    }

    private void cssGridLayoutAddonExample1() {
        /* Original code
        FluentGridLayout layout = new FluentGridLayout();
        Component alignTestComponent = getDiv();
        layout.withTemplateRows(new Flex(), new Flex(), new Flex())
                .withTemplateColumns(new Flex(), new Flex(), new Flex())
                .withColumnAlign(alignTestComponent, ColumnAlign.END)
                .withRowAlign(alignTestComponent, RowAlign.END)
                .withRowAndColumn(alignTestComponent, 1, 1, 1, 3)
                .withRowAndColumn(getDiv(), 2, 1)
                .withRowAndColumn(getDiv(), 2, 2)
                .withRowAndColumn(getDiv(), 1, 3, 3, 3);
        layout.setWidth("100%");
        layout.setHeight("600px");
         */
        CssGrid gridLayout = new CssGrid();
        gridLayout.setWidth("100%");
        gridLayout.setHeight("600px");
        gridLayout.setTemplateColumns("1fr","1fr","1fr");
        gridLayout.setTemplateRows("1fr","1fr","1fr");
        gridLayout.add(createDiv())
                .withRow(1)
                .withColumns(1,3)
                .withAlign(Style.AlignSelf.END)
                .withJustifySelf(Style.JustifyContent.END);
        gridLayout.add(createDiv()).withRow(2).withColumn(1);
        gridLayout.add(createDiv()).withRow(2).withColumn(2);
        gridLayout.add(createDiv()).withRows(1,3).withColumns(3,3);
        showComponent(gridLayout);
    }

    private void cssGridLayoutAddonExampleFlexibleGrid() {
        CssGrid gridLayout = new CssGrid();
        gridLayout.setWidth("100%");
        gridLayout.setHeight("600px");
        gridLayout.setTemplateColumns("repeat(auto-fill, minmax(220px, 1fr))");
        gridLayout.setAutoRows("220px");
        gridLayout.add(createDiv());
        gridLayout.add(createDiv())
                .withRowSpan(2)
                .withColumnSpan(2);
        gridLayout.add(
                createDiv(), createDiv(), createDiv(), createDiv(), createDiv(), createDiv(),
                createDiv(), createDiv(), createDiv(), createDiv(), createDiv(), createDiv(),
                createDiv(), createDiv());
        showComponent(gridLayout);
        /* Original code
        FlexibleGridLayout flexibleGridLayout = new FlexibleGridLayout()
                .withColumns(Repeat.RepeatMode.AUTO_FILL, new MinMax(new Length("220px"), new Flex()))
                .withAutoRows(new Length("220px"))
                .withItems(
                        getDiv())
                .withItemWithSize(getDiv(), 2, 2)
                .withItems(
                        getDiv(), getDiv(), getDiv(), getDiv(), getDiv(), getDiv(),
                        getDiv(), getDiv(), getDiv(), getDiv(), getDiv(), getDiv(),
                        getDiv(), getDiv())
                .withOverflow(Overflow.AUTO)
                .withAutoFlow(AutoFlow.ROW_DENSE)
                .withPadding(true)
                .withSpacing(true);
        flexibleGridLayout.setWidth("100%");
        flexibleGridLayout.setHeight("600px");
        */
    }


    private void cssGridExampleAreaLayout() {
        // MDN example, but random colors

        CssGrid gridLayout = new CssGrid();
        gridLayout.setWidth("100%");

        var head = new Area("head");
        var nav = new Area("nav");
        var foot = new Area("foot");
        var main = new Area("main");
        gridLayout.setTemplateAreas(
                new Row( head, head ),
                new Row( nav , main ),
                new Row( nav , foot )
                );
        gridLayout.setTemplateRows("50px", "1fr", "50px");
        gridLayout.setTemplateColumns("150px", "1fr");
        gridLayout.add(createDiv()).withArea(foot);
        gridLayout.add(createDiv()).withArea(head);
        gridLayout.add(createDiv()).withArea(nav);

        Div mainContent = createDiv();
        mainContent.add(new Paragraph("This is..."));
        mainContent.add(new Paragraph("... main content..."));
        mainContent.add(new Paragraph("... area."));
        gridLayout.add(mainContent).withArea(main);

        showComponent(gridLayout);

    }

    public void responsiveGrid() {

        CssGrid gridLayout = new CssGrid();
        gridLayout.setWidth("100%");

        ResizeObserver.get().observe(gridLayout, size -> {
            if(size.width() < 800) {
                gridLayout.setTemplateAreas(
                        "head",
                        "nav",
                        "main",
                        "foot"
                );
                gridLayout.setTemplateRows("60px", "120px", "3fr", "60px");
                gridLayout.setTemplateColumns("1fr");
            } else {
                gridLayout.setTemplateAreas(
                        "head head",
                        "nav  main",
                        "nav  foot"
                );
                gridLayout.setTemplateRows("60px", "1fr", "60px");
                gridLayout.setTemplateColumns("150px", "1fr");
            }
        });


        // TODO consider providing some typed API for defining and referencing areas
        gridLayout.add(createDiv()).withArea("foot");
        gridLayout.add(createDiv()).withArea("head");
        gridLayout.add(createDiv()).withArea("nav");
        Div main = createDiv();
        main.add(new Paragraph("This is..."));
        main.add(new Paragraph("... main content..."));
        main.add(new Paragraph("... area."));
        gridLayout.add(main).withArea("main");
        showComponent(gridLayout);

    }

    int divCounter = 1;

    private Div createDiv() {
        int nextInt = random.nextInt(0xffffff + 1);
        String colorCode = String.format("#%06x", nextInt);
        Div div = new Div("DIV " + divCounter++ + " " + colorCode);
        div.getStyle()
                .setBackground(colorCode)
                .setPadding("20px")
        ;
        return div;
    }


    private void mdcExample() {
        CssGrid gridLayout = new CssGrid(3);
        gridLayout.setHeight("400px");
        gridLayout.setGap("1em");
        gridLayout.setAutoRows("minmax(100px, auto)");

        gridLayout.add(fullSizeComponent("one"))
                .withColumns(1,3)
                .withRow(1);
        gridLayout.add(fullSizeComponent("two"))
                .withColumns(2,4)
                .withRows(1,3);
        gridLayout.add(fullSizeComponent("three"))
                .withColumn(1)
                .withRows(2,5);
        gridLayout.add(fullSizeComponent("four"))
                .withColumn(3)
                .withRow(3);
        gridLayout.add(fullSizeComponent("five"))
                .withColumn(2)
                .withRow(4);
        gridLayout.add(fullSizeComponent("six"))
                .withColumn(3)
                .withRow(4);

        if(false) {
            gridLayout.add(component(1));
            gridLayout.add(component(2));
            gridLayout.add(component(3));
            gridLayout.add(component(4));
        }
        showComponent(gridLayout);
    }

    private void mdcUnequalCols() {
        CssGrid gridLayout = new CssGrid(3);

        gridLayout.add(fullSizeComponent("one"));
        gridLayout.add(fullSizeComponent("two"));
        gridLayout.add(fullSizeComponent("three"));
        gridLayout.add(fullSizeComponent("four"));
        gridLayout.add(fullSizeComponent("five"));
        showComponent(gridLayout);
    }


    private void basic12ColWithtGap() {
        basic12Col();
        grid.setColumnGap("1em");
        grid.setRowGap("2em");

        // Non css grid specific goes via basic style object
        grid.getStyle().setPadding("1em");
    }

    private void basic12Col() {
        CssGrid gridLayout = new CssGrid(12);
        gridLayout.getStyle().setBorder("1px solid blue");

        for(int i = 0; i < 24; i++) {
            gridLayout.add(component(i));
        }

        showComponent(gridLayout);
    }

    private static Button component(int i) {
        return component("B"+i);
    }

    private static VButton component(String i) {
        return new VButton(i, e -> Notification.show("It works"))
                .withStyle("margin", "0").withStyle("border", "1px solid black");
    }

    private static VButton fullSizeComponent(String i) {
        return component(i).withSizeFull();
    }

    private void showComponent(CssGrid gridLayout) {
        if(grid != null) {
            grid.removeFromParent();
        }
        add(gridLayout);
        grid = gridLayout;
        divCounter = 1;
        random = new Random(0);
    }
}
