package org.vaadin.firitin.components.grid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.Style;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentFocusable;
import org.vaadin.firitin.fluency.ui.FluentHasSize;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;
import org.vaadin.firitin.fluency.ui.FluentHasTheme;
import org.vaadin.firitin.util.VStyleUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class VGrid<T> extends Grid<T>
        implements FluentComponent<VGrid<T>>, FluentHasStyle<VGrid<T>>, FluentHasSize<VGrid<T>>,
        FluentFocusable<Grid<T>, VGrid<T>>, FluentHasTheme<VGrid<T>> {

    public VGrid() {
        super();
    }

    public VGrid(int pageSize) {
        super(pageSize);
    }

    public VGrid(Class<T> beanType) {
        super(beanType);
    }

    @Override
    protected BiFunction<Renderer<T>, String, Column<T>> getDefaultColumnFactory() {
        return (tRenderer, s) -> new VColumn<>(VGrid.this, s, tRenderer);
    }

    public VGrid<T> withSelectionMode(Grid.SelectionMode selectionMode) {
        setSelectionMode(selectionMode);
        return this;
    }

    public VGrid<T> withSelectionModel(GridSelectionModel<T> selectionModel, Grid.SelectionMode selectionMode) {
        setSelectionModel(selectionModel, selectionMode);
        return this;
    }

    public VGrid<T> withProperties(String... propertyNames) {
        setColumns(propertyNames);
        return this;
    }

    public VGrid<T> withThemeVariants(GridVariant... variants) {
        addThemeVariants(variants);
        return this;
    }

    public VGrid<T> setDataProvider(CallbackDataProvider.FetchCallback<T, Void> fetchCallback,
                                    CallbackDataProvider.CountCallback<T, Void> countCallback) {
        setDataProvider(DataProvider.fromCallbacks(fetchCallback, countCallback));
        return this;
    }

    public VGrid<T> withItems(Collection<T> items) {
        setItems(items);
        return this;
    }

    public VGrid<T> withItems(T... items) {
        setItems(items);
        return this;
    }

    /**
     * Scrolls to the row presenting the given item.
     *
     * @param item the item to scroll to
     * @deprecated Note, with lazy loaded content, calling this method
     * may cause performance issues
     */
    @Deprecated
    public void scrollToItem(T item) {
        int index;
        Stream<T> items;
        try {
            items = getListDataView().getItems();
        } catch (IllegalStateException exception) {
            // lazy loaded, this might be slow
            // TODO, figure out if we could optimze this
            // for the rows that happen to be already in
            // the viewport
            items = getGenericDataView().getItems();
        }
        AtomicInteger i = new AtomicInteger(); // any mutable integer wrapper
        index = items.peek(v -> i.incrementAndGet())
                .anyMatch(itm -> itm.equals(item)) ?
                i.get() - 1 : -1;
        scrollToIndex(index);
    }

    /**
     * Adds a Vaadin 3,4,5,6,7,8 style column selector.
     *
     * @return the grid with column selector
     */
    public VGrid<T> withColumnSelector() {
        ContextMenu columnSelector = new ContextMenu();
        getColumns().forEach(col -> {
            MenuItem item = columnSelector.addItem(col.getHeaderText());
            item.setCheckable(true);
            item.setChecked(col.isVisible());
            item.addClickListener(e -> {
                col.setVisible(!col.isVisible());
                item.setChecked(col.isVisible());
            });
        });

        Grid.Column fakeColumn = addColumn(s -> "");
        fakeColumn.setKey("column-selector-fake-column");
        fakeColumn.setWidth("0px");
        fakeColumn.setFlexGrow(0);

        Button b = new Button(VaadinIcon.CHEVRON_CIRCLE_DOWN_O.create());
        b.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        // TODO figure out a good way to set proper margin right, now hardcoded to 16px
        // TODO figure out a good way set previous column margin, in case it is hidden
        b.getElement().executeJs("""
                const el = this;
                setTimeout(() => {
                    const w = el.offsetWidth;
                    el.parentElement.style.overflow = "visible";
                    el.parentElement.previousSibling.style.marginRight = (w - 16) + "px";
                    el.style.setProperty('margin-left', '-' + (w+16) + 'px');
                }, 0);
                """);
        columnSelector.setTarget(b);
        columnSelector.setOpenOnClick(true);
        fakeColumn.setHeader(b);
        return this;
    }

    public static class VColumn<T> extends Column<T> {

        private Style customStyle;

        /**
         * Constructs a new Column for use inside a Grid.
         *
         * @param grid     the grid this column is attached to
         * @param columnId unique identifier of this column
         * @param renderer the renderer to use in this column, must not be
         *                 {@code null}
         */
        public VColumn(Grid<T> grid, String columnId, Renderer<T> renderer) {
            super(grid, columnId, renderer);
        }

        @Override
        public Style getStyle() {
            // super implementation is completely useless

            if(customStyle != null) {
                return customStyle;
            }

            int indexOfColumn = getGrid().getColumns().indexOf(this);

            customStyle = new Style(){
                String partName = UUID.randomUUID().toString().replaceAll("[0-9-]", "a");

                private LinkedHashMap<String, String> styles = new LinkedHashMap<>();

                private boolean deferred;

                @Override
                public String get(String s) {
                    return null;
                }

                @Override
                public Style set(String s, String s1) {
                    styles.put(s, s1);
                    deferredApply();
                    return this;
                }

                private void deferredApply() {
                    if(!deferred) {
                        String headerText = getHeaderText();
                        if(headerText != null) {
                            setHeader(new Span(headerText));
                        }
                        if(getHeaderComponent() != null) {
                            getHeaderComponent().addClassName(partName + "-hc");
                        }
                        getGrid().getElement().getNode().runWhenAttached(ui -> doApply());
                        deferred = true;
                    }
                }

                /** For the header we are adding the Style rules for th and the header component/text to make it easier to override default styles with strong selectors. Some rules are harmful here though, like outline/border etc */
                private static final String[] harmfulAsDuplicate = new String[] {
                        "border", "outline", "padding", "margin", "zoom"
                };

                private void doApply() {
                    // This is important, TODO, optimize
                    setPartNameGenerator(p -> partName);

                    getGrid().getElement().executeJs("const g = this; setTimeout(() => {g.shadowRoot.querySelector('th:nth-child(" + (indexOfColumn + 1) + ")').part.add('" + partName + "');}, 1);");

                    StringBuilder cellCssBody = new StringBuilder();
                    StringBuilder headerContentCssBody = new StringBuilder();
                    styles.forEach((k, v) -> {
                        cellCssBody.append("%s: %s;".formatted(k, v));
                        if(Arrays.stream(harmfulAsDuplicate).noneMatch(k::contains)) {
                            headerContentCssBody.append("%s: %s;".formatted(k, v));
                        }
                    });
                    VStyleUtil.inject("""
                            vaadin-grid::part(%s) {
                                %s
                            }
                            .%s-hc {
                                %s
                            }
                            """.formatted(
                                    partName,
                                    cellCssBody.toString(),
                                    partName,
                                    headerContentCssBody.toString())
                    );
                }

                @Override
                public Style remove(String s) {
                    styles.remove(s);
                    return this;
                }

                @Override
                public Style clear() {
                    customStyle.clear();
                    return this;
                }

                @Override
                public boolean has(String s) {
                    return customStyle.has(s);
                }

                @Override
                public Stream<String> getNames() {
                    return customStyle.getNames();
                }
            };

            return customStyle;
        }
    }
}
