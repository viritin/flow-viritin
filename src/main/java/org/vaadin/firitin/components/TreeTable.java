package org.vaadin.firitin.components;

import com.helger.commons.mutable.MutableInt;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.grid.dataview.GridDataView;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.data.provider.BackEndDataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.InMemoryDataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.function.SerializableFunction;
import org.vaadin.firitin.components.grid.VGrid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Design goals for this component:
 * - support lazy loading
 * - no weird HierarchicalDataProvider stuff
 * - support scrollToItem
 * - also support provide same simpler API for non-lazy loading mode
 * that is available for TreeGrid
 * <p>
 * Non-goals:
 * - workaround oddities of Grid itself
 *
 * <p>
 * The API is a bit different from TreeGrid, as it is not possible
 * to provide the HiearchicalDataProvider. In the same way as with the
 * official TreeGrid, you can provide the root items and a method to
 * retrieve children for each item using {@link #setRootItems(List, SerializableFunction)}.
 * </p>
 * <p>
 * Alternatively, you can provide the data in lazy loading mode with {@link #setItems(CallbackDataProvider.FetchCallback)}
 * or {@link #setItems(CallbackDataProvider.FetchCallback, CallbackDataProvider.CountCallback)}.
 * With these methods you will also need to define {@link TreeTableModel}
 * (or at least {@link LeafModel} & {@link LevelModel} separately) that
 * Grid uses to visualise the hierarchy. At least {@link OpenModel} should be
 * instance specific! The fetch callbacks need to take the current expanded state
 * into account and return visible subtrees as defined by the {@link OpenModel}.
 * </p>
 *
 * <p>Whether you were using lazy loading or in-memory data set,
 * you can override the {@link OpenModel} that controls whether
 * the node is open or not. TreeGrid calls the setter
 * {@link OpenModel#setOpen(Object, boolean)} when a node is
 * opened/closed, so you can persist that detail to your backend
 * if you wish to persist the state for longer than the current session.</p>
 *
 * @param <T> the (super) type of the items in the grid
 */
public class TreeTable<T> extends VGrid<T> {

    private OpenModel<T> openModel;
    private LeafModel<T> leafModel;
    private LevelModel<T> levelModel;
    private List<T> rootItems;
    private SerializableFunction<T, List<T>> childrenProvider;

    public Column<T> addHierarchyColumn(SerializableFunction<T, String> valueProvider) {
        return addComponentColumn(item -> {
            HierarchyColumnWrapper hcw = new HierarchyColumnWrapper(item);
            hcw.setText(valueProvider.apply(item));
            return hcw;
        });
    }

    public Column<T> addHierarchyComponentColumn(SerializableFunction<T, Component> valueProvider) {
        return addComponentColumn(item -> {
            HierarchyColumnWrapper hcw = new HierarchyColumnWrapper(item);
            hcw.getElement().appendChild(valueProvider.apply(item).getElement());
            return hcw;
        });
    }

    /**
     * Sets the tree table model (a combination of {@link OpenModel},
     * {@link LeafModel} and {@link LevelModel}. This (or separately
     * {@link LeafModel} and {@link LevelModel}) must be set in case the
     * rows are passed in with the lazy loading mode (using either
     * {@link #setItems(com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback)} or
     * {@link #setItems(com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback, com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback)}).
     *
     * @param model the tree table model.
     */
    public void setTreeTableModel(TreeTableModel model) {
        this.openModel = model;
        this.leafModel = model;
        this.levelModel = model;
    }

    /**
     * Set root items for the tree table. This is a simpler API for non-lazy loading mode.
     * You only need to provide the root items and a method to retrieve children for each item.
     * TreeTable will then automatically load all children recursively, when needed.
     * <p>
     * When using this method, customizing {@link LeafModel} or {@link LevelModel}
     * is not needed nor supported. If you want, you can override the default
     * in-memory {@link OpenModel} that keeps all items closed by default.
     * </p>
     *
     * @param rootItems        root items
     * @param childrenProvider a function to retrieve children for each item
     */
    public void setRootItems(List<T> rootItems, SerializableFunction<T, List<T>> childrenProvider) {
        this.rootItems = rootItems;
        this.childrenProvider = childrenProvider;
        reloadData();
    }

    private void addChildrenRecursively(T rootItem, Map<T, Integer> levelMap, MutableInt level, Function<T, List<T>> childrenProvider, List<T> visibleRows) {
        if (getOpenModel().isOpen(rootItem)) {
            List<T> children = childrenProvider.apply(rootItem);
            if (children != null) {
                level.inc();
                for (T child : children) {
                    visibleRows.add(child);
                    levelMap.put(child, level.intValue());
                    addChildrenRecursively(child, levelMap, level, childrenProvider, visibleRows);
                }
                level.dec();
            }
        }
    }

    public OpenModel<T> getOpenModel() {
        if (openModel == null) {
            openModel = new OpenByDefault();
        }
        return openModel;
    }

    public void setOpenModel(OpenModel<T> openModel) {
        this.openModel = openModel;
    }

    public LevelModel<T> getLevelModel() {
        assert levelModel != null;
        return levelModel;
    }

    public void setLevelModel(LevelModel<T> levelModel) {
        this.levelModel = levelModel;
    }

    public LeafModel<T> getLeafModel() {
        assert levelModel != null;
        return leafModel;
    }

    public void setLeafModel(LeafModel<T> leafModel) {
        this.leafModel = leafModel;
    }

    private void reloadData() {
        if (rootItems == null) {
            // default/lazyloading mode
            getGenericDataView().refreshAll();
        } else {
            // in-memory mode, rebuild visible rows from root items
            List<T> visibleRows = new ArrayList<>();
            Map<T, Integer> levelMap = new HashMap<>();
            MutableInt level = new MutableInt(0);
            // add all root items and their children recursively
            for (T rootItem : rootItems) {
                visibleRows.add(rootItem);
                levelMap.put(rootItem, level.intValue());
                addChildrenRecursively(rootItem, levelMap, level, childrenProvider, visibleRows);
            }
            setLevelModel(item -> levelMap.get(item));
            setLeafModel(item -> childrenProvider.apply(item).isEmpty());
            super.setItems(visibleRows);
        }
    }

    /**
     * Not supported in TreeTable, use setRootItems or setItems instead
     *
     * @inheritDoc
     */
    @Deprecated
    @Override
    public GridDataView<T> setItems(DataProvider<T, Void> dataProvider) {
        return super.setItems(dataProvider);
    }

    /**
     * Not supported in TreeTable, use setRootItems or setItems instead
     *
     * @inheritDoc
     */
    @Deprecated
    public GridDataView<T> setItems(InMemoryDataProvider<T> inMemoryDataProvider) {
        return super.setItems(inMemoryDataProvider);
    }

    /**
     * Not supported in TreeTable, use setRootItems or setItems instead
     *
     * @inheritDoc
     */
    @Deprecated
    @Override
    public GridLazyDataView<T> setItems(BackEndDataProvider<T, Void> dataProvider) {
        return super.setItems(dataProvider);
    }

    /**
     * Not supported in TreeTable, use setRootItems or setItems instead
     *
     * @inheritDoc
     */
    @Deprecated
    @Override
    public GridListDataView<T> setItems(T... items) {
        return super.setItems(items);
    }

    /**
     * Not supported in TreeTable, use setRootItems or setItems instead
     *
     * @inheritDoc
     */
    @Deprecated
    @Override
    public GridListDataView<T> setItems(ListDataProvider<T> dataProvider) {
        return super.setItems(dataProvider);
    }

    /**
     * Not supported in TreeTable, use setRootItems or setItems instead
     *
     * @inheritDoc
     */
    @Deprecated
    @Override
    public GridListDataView<T> setItems(Collection<T> items) {
        return super.setItems(items);
    }

    public interface OpenModel<T> {
        boolean isOpen(T item);

        void setOpen(T item, boolean open);
    }

    public interface LeafModel<T> {
        boolean isLeaf(T item);
    }

    public interface LevelModel<T> {
        int getLevel(T item);
    }

    public interface TreeTableModel<T> extends OpenModel<T>, LeafModel<T>, LevelModel<T> {

    }

    public static class OpenByDefault<T> implements OpenModel<T> {
        private final Set<T> closed = new HashSet<>();

        public Set<T> getClosed() {
            return closed;
        }

        @Override
        public boolean isOpen(T item) {
            return !closed.contains(item);
        }

        @Override
        public void setOpen(T item, boolean open) {
            if (open) {
                closed.remove(item);
            } else {
                closed.add(item);
            }
        }
    }

    public static class ClosedByDefault<T> implements OpenModel<T> {
        private final Set<T> open = new HashSet<>();

        public Set<T> getOpen() {
            return open;
        }

        @Override
        public boolean isOpen(T item) {
            return open.contains(item);
        }

        @Override
        public void setOpen(T item, boolean open) {
            if (open) {
                this.open.add(item);
            } else {
                this.open.remove(item);
            }
        }
    }

    @Tag("vaadin-grid-tree-toggle")
    public class HierarchyColumnWrapper extends Component
            implements HasText {

        private final T item;

        public HierarchyColumnWrapper(T item) {
            this.item = item;
            boolean open = TreeTable.this.getOpenModel().isOpen(item);
            getElement().setProperty("expanded", open);
            int level = TreeTable.this.getLevelModel().getLevel(item);
            getElement().setProperty("level", level);
            getElement().setProperty("leaf", TreeTable.this.getLeafModel().isLeaf(item));
            // for some reason this gets called for a lot of items, so let's do it lazily
            // Also Grid internals seems to catch this (and recycle element 🤔) and force loading items
            // so a hack with checking the value is really needed
            getElement().executeJs("""
                        var el = this;
                        setTimeout(function() {
                            el.addEventListener('expanded-changed', function(e) {
                                var open = %s;
                                if(el.expanded != open)
                                    el.$server.onExpandedChanged();
                            });
                        }, 100);
                    """.formatted(open));
        }

        @ClientCallable
        private void onExpandedChanged() {
            boolean open = getElement().getProperty("expanded", false);
            TreeTable.this.getOpenModel().setOpen(item, !open);
            reloadData();
        }

    }

}
