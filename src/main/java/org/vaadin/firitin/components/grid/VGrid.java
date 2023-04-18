package org.vaadin.firitin.components.grid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import org.vaadin.firitin.fluency.ui.*;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
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
}
