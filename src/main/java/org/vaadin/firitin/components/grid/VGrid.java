package org.vaadin.firitin.components.grid;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.ColumnPathRenderer;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.shared.util.SharedUtil;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentFocusable;
import org.vaadin.firitin.fluency.ui.FluentHasSize;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;
import org.vaadin.firitin.fluency.ui.FluentHasTheme;
import org.vaadin.firitin.util.PropertyRef;
import org.vaadin.firitin.util.PropertyRefs;
import org.vaadin.firitin.util.VStyle;
import org.vaadin.firitin.util.VStyleUtil;
import org.vaadin.firitin.util.JacksonIntrospection;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.introspect.AnnotatedMethod;
import tools.jackson.databind.introspect.BasicBeanDescription;
import tools.jackson.databind.introspect.BeanPropertyDefinition;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class VGrid<T> extends Grid<T>
        implements FluentComponent<VGrid<T>>, FluentHasStyle<VGrid<T>>, FluentHasSize<VGrid<T>>,
        FluentFocusable<Grid<T>, VGrid<T>>, FluentHasTheme<VGrid<T>> {

    // Not really used for object mapping, but introspection
    private static ObjectMapper dummyOm;
    private static boolean autoConfigureFromGetterReferencesByDefault = true;
    private boolean autoConfigureFromGetterReferences = autoConfigureFromGetterReferencesByDefault;
    private BasicBeanDescription bbd;
    private Set<String> columnCssKeys;
    private Set<String> rowCssKeys;
    private CellFormatter<T> cellFormatter;

    public VGrid() {
        super();
    }

    public VGrid(int pageSize) {
        super(pageSize);
    }

    public VGrid(Class<T> beanType) {
        this(beanType, true);
    }

    /**
     * Creates a new Grid with given bean type.
     *
     * @param beanType          the bean/record type
     * @param autoCreateColumns if true, columns are created automatically for all introspected properties
     */
    public VGrid(Class<T> beanType, boolean autoCreateColumns) {
        // Make Grid skip column detection, we can do better work here
        super(beanType, false);
        // Now lets get columns with Jackson, and pick the missing ones for records
        if (dummyOm == null) {
            dummyOm = JacksonIntrospection.getMapper();
        }

        JavaType javaType = dummyOm.getTypeFactory().constructType(beanType);

        this.bbd = (BasicBeanDescription) dummyOm._deserializationContext().introspectBeanDescription(javaType);
        if (autoCreateColumns) {
            List<String> propertyNames = getBeanPropertyNames();
            setColumns(propertyNames.toArray(new String[0]));
        }
    }

    protected List<BeanPropertyDefinition> getBeanPropertyDefinitions() {
        return this.bbd.findProperties();
    }

    protected List<String> getBeanPropertyNames() {
        return getBeanPropertyDefinitions().stream().map(BeanPropertyDefinition::getName).toList();
    }

    public void focus() {
        //super.focus();
        // see https://github.com/vaadin/flow-components/issues/2180
        getElement().executeJs("""
                    setTimeout(function() {
                        $0.shadowRoot.querySelector("tr").focus();
                    }, 100);
                """);
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

    /**
     * Configures the columns shown by the grid, using method references to the
     * getters instead of property name strings:
     *
     * <pre><code>
     * grid.withProperties(Person::getFirstName, Person::getLastName);
     * </code></pre>
     *
     * @param properties method references to the getters of the properties to show
     * @return the grid for further configuration
     */
    @SafeVarargs
    public final VGrid<T> withProperties(PropertyRef<T, ?>... properties) {
        return withProperties(toPropertyNames(properties));
    }

    /**
     * Hides given columns.
     *
     * @param propertyNamesToHide the property names/column keys to hide
     * @return the grid for further configuration
     */
    public VGrid<T> hideProperties(String... propertyNamesToHide) {
        List<String> properties = new ArrayList<>(getColumns().stream().map(col -> col.getKey()).toList());
        for (String pToHide : propertyNamesToHide) {
            properties.remove(pToHide);
        }
        setColumns(properties.toArray(new String[properties.size()]));
        return this;
    }

    /**
     * Hides given columns, using method references to the getters instead of
     * property name strings.
     *
     * @param propertiesToHide method references to the getters of the properties to hide
     * @return the grid for further configuration
     */
    @SafeVarargs
    public final VGrid<T> hideProperties(PropertyRef<T, ?>... propertiesToHide) {
        return hideProperties(toPropertyNames(propertiesToHide));
    }

    /**
     * Configures the columns shown by the grid, using method references to the
     * getters instead of property name strings:
     *
     * <pre><code>
     * grid.setColumns(Person::getFirstName, Person::getLastName);
     * </code></pre>
     *
     * Nested properties can be referenced by chaining:
     *
     * <pre><code>
     * grid.setColumns(PropertyRef.of(Person::getAddress).then(Address::getStreet));
     * </code></pre>
     *
     * @param properties method references to the getters of the properties to show
     */
    @SafeVarargs
    public final void setColumns(PropertyRef<T, ?>... properties) {
        setColumns(toPropertyNames(properties));
    }

    /**
     * Adds a column for the given property, using a method reference to its getter
     * instead of a property name string:
     *
     * <pre><code>
     * grid.addPropertyColumn(Person::getFirstName).setHeader("Etunimi");
     * </code></pre>
     *
     * @param property a method reference to the getter of the property to show
     * @return the new column, for further configuration
     */
    public VColumn<T> addPropertyColumn(PropertyRef<T, ?> property) {
        return asVColumn(addColumn(property.getPropertyName()));
    }

    /**
     * Replaces the way the column of the given property is rendered, leaving
     * everything else about the column as it is: its position among the other
     * columns, its key, header, width and sorting all stay put. Handy for taking an
     * otherwise standard grid and giving just one of its columns a hand-written
     * presentation.
     *
     * <pre><code>
     * VGrid&lt;Person&gt; grid = new VGrid&lt;&gt;(Person.class);
     * grid.setComponentRenderer(Person::getEmail, person -&gt; new Anchor("mailto:" + person.getEmail(), person.getEmail()));
     * </code></pre>
     *
     * Note that the column keeps the comparator it was created with, so in memory
     * sorting keeps working on the underlying property value even though the cell
     * now shows a component.
     *
     * @param property          a method reference to the getter of the property whose column to re-render
     * @param componentProvider creates the component to show for a row
     * @param <C>               the component type
     * @return the grid for further configuration
     * @throws IllegalArgumentException if this grid has no column for the property
     */
    public <C extends Component> VGrid<T> setComponentRenderer(PropertyRef<T, ?> property,
                                                                ValueProvider<T, C> componentProvider) {
        return setComponentRenderer(property.getPropertyName(), componentProvider);
    }

    /**
     * Replaces the way the column with the given key is rendered, leaving everything
     * else about the column as it is.
     *
     * @param columnKey         the key of the column to re-render
     * @param componentProvider creates the component to show for a row
     * @param <C>               the component type
     * @return the grid for further configuration
     * @throws IllegalArgumentException if this grid has no column with the key
     * @see #setComponentRenderer(PropertyRef, ValueProvider)
     */
    public <C extends Component> VGrid<T> setComponentRenderer(String columnKey,
                                                                ValueProvider<T, C> componentProvider) {
        return setRenderer(columnKey, new ComponentRenderer<>(componentProvider));
    }

    /**
     * Replaces the renderer of the column of the given property, leaving everything
     * else about the column as it is. The general form of
     * {@link #setComponentRenderer(PropertyRef, ValueProvider)}, for cases where a
     * {@link com.vaadin.flow.data.renderer.LitRenderer} or one of the built-in
     * renderers is a better fit than a server side component.
     *
     * @param property a method reference to the getter of the property whose column to re-render
     * @param renderer the new renderer
     * @return the grid for further configuration
     * @throws IllegalArgumentException if this grid has no column for the property
     */
    public VGrid<T> setRenderer(PropertyRef<T, ?> property, Renderer<T> renderer) {
        return setRenderer(property.getPropertyName(), renderer);
    }

    /**
     * Replaces the renderer of the column with the given key, leaving everything
     * else about the column as it is.
     *
     * @param columnKey the key of the column to re-render
     * @param renderer  the new renderer
     * @return the grid for further configuration
     * @throws IllegalArgumentException if this grid has no column with the key
     * @see #setRenderer(PropertyRef, Renderer)
     */
    public VGrid<T> setRenderer(String columnKey, Renderer<T> renderer) {
        requireColumn(columnKey).setRenderer(renderer);
        return this;
    }

    /**
     * Adds columns for the given properties, using method references to their
     * getters instead of property name strings.
     *
     * @param properties method references to the getters of the properties to show
     */
    @SafeVarargs
    public final void addColumns(PropertyRef<T, ?>... properties) {
        addColumns(toPropertyNames(properties));
    }

    /**
     * Makes the columns of the given properties sortable, using method references
     * to their getters instead of property name strings.
     *
     * @param properties method references to the getters of the properties to make sortable
     */
    @SafeVarargs
    public final void setSortableColumns(PropertyRef<T, ?>... properties) {
        setSortableColumns(toPropertyNames(properties));
    }

    /**
     * Sets the order of the columns, using method references to the getters instead
     * of property name strings. Note that all columns of the grid must be listed.
     *
     * @param properties method references to the getters of the properties, in the wanted order
     * @throws IllegalArgumentException if any of the properties has no column in this grid
     */
    @SafeVarargs
    public final void setColumnOrder(PropertyRef<T, ?>... properties) {
        setColumnOrder(Arrays.stream(properties)
                .map(property -> requireColumn(property.getPropertyName()))
                .toList());
    }

    /**
     * Finds the column of the given property, using a method reference to its getter
     * instead of a property name string.
     *
     * @param property a method reference to the getter of the property
     * @return the column, or null if this grid has no column for the property
     */
    public VColumn<T> getColumnByKey(PropertyRef<T, ?> property) {
        Column<T> column = getColumnByKey(property.getPropertyName());
        return column == null ? null : asVColumn(column);
    }

    private Column<T> requireColumn(String columnKey) {
        Column<T> column = getColumnByKey(columnKey);
        if (column == null) {
            throw new IllegalArgumentException(
                    "This grid has no column with the key '%s'. The keyed columns are %s."
                            .formatted(columnKey, getColumns().stream()
                                    .map(Column::getKey)
                                    .filter(Objects::nonNull)
                                    .toList()));
        }
        return column;
    }

    private VColumn<T> asVColumn(Column<T> column) {
        if (column instanceof VColumn<T> vColumn) {
            return vColumn;
        }
        throw new IllegalStateException(
                "The column factory of this grid does not produce VColumns, but "
                        + column.getClass().getName());
    }

    /**
     * Removes the column of the given property, using a method reference to its
     * getter instead of a property name string.
     *
     * @param property a method reference to the getter of the property
     */
    public void removeColumnByKey(PropertyRef<T, ?> property) {
        removeColumnByKey(property.getPropertyName());
    }

    private static String[] toPropertyNames(PropertyRef<?, ?>[] properties) {
        return Arrays.stream(properties)
                .map(PropertyRef::getPropertyName)
                .toArray(String[]::new);
    }

    @Override
    public void setColumns(String... propertyNames) {
        if (getBeanType().isRecord()) {
            removeAllColumns();
            RecordComponent[] recordComponents = getBeanType().getRecordComponents();
            for (String p : propertyNames) {
                for (RecordComponent r : recordComponents) {
                    String name = r.getName();
                    if (name.equals(p)) {
                        addRecordColumn(r, name);
                    }
                }
            }
        } else {
            super.setColumns(propertyNames);
        }
    }

    @Override
    public Column<T> addColumn(String propertyName) {
        try {
            return super.addColumn(propertyName);
        } catch (IllegalArgumentException exception) {
            // Vaadin don't by default support modern Java like default methods, records
            // try falling back to create column using Jackson introspection
            if (bbd != null) {
                var d = bbd.findProperties().stream().filter(p -> p.getName().equals(propertyName)).findFirst().get();
                AnnotatedMethod getter = d.getGetter();
                Column<T> col = addColumn(i -> {
                    try {
                        if(getter == null) {
                            return d.getAccessor().getValue(i);
                        }
                        return getter.callOn(i);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                try {
                    col.setKey(propertyName);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(
                            "Multiple columns for the same property: "
                                    + propertyName);
                }
                if (Comparable.class.isAssignableFrom(d.getPrimaryType().getRawClass())) {
                    col.setSortable(true);
                }
                col.setHeader(SharedUtil.capitalize(propertyName));
                return col;
            } else {
                throw exception;
            }
        }
    }

    private void addRecordColumn(RecordComponent r, String name) {
        addColumn(v -> {
            try {
                r.getAccessor().setAccessible(true);
                return r.getAccessor().invoke(v);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }).setKey(name).setHeader(SharedUtil.capitalize(name));
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
        List<Column<T>> columns = getColumns();
        for (int i = 0; i < columns.size(); i++) {
            Column<T> col = columns.get(i);
            String headerText = col.getHeaderText();
            if (headerText == null) {
                headerText = col.getKey();
            }
            if (headerText == null) {
                headerText = "Column " + i;
            }
            MenuItem item = columnSelector.addItem(headerText);
            item.setCheckable(true);
            item.setChecked(col.isVisible());
            item.addClickListener(e -> {
                col.setVisible(!col.isVisible());
                item.setChecked(col.isVisible());
            });
        }

        Button b = new Button(VaadinIcon.CHEVRON_CIRCLE_DOWN_O.create()) {{
            addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            getStyle()
                    .setPosition(Style.Position.ABSOLUTE)
                    .setRight("0")
                    .setMinWidth("1em")
                    .setMarginRight("0")
                    .setMarginTop("0.5em")
                    .setPadding("0");
            getElement().executeJs("""
                    const el = this;
                    const gridel = $0;
                    gridel.shadowRoot.getElementById("scroller").appendChild(el);
                    """, VGrid.this.getElement());
        }};
        VGrid.this.getElement().appendVirtualChild(b.getElement());
        columnSelector.setTarget(b);
        columnSelector.setOpenOnClick(true);

        return this;
    }

    private VColumn<T> colById(String columnId) {
        // Reflection extension 🤪: Map<String, Column<T>> idToColumnMap
        try {
            final Field field = Grid.class.getDeclaredField("idToColumnMap");
            field.setAccessible(true);
            Map<String, Column<T>> idToColumnMap = (Map<String, Column<T>>) field.get(this);
            return (VColumn<T>) idToColumnMap.get(columnId);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected <C extends Column<T>> C addColumn(ValueProvider<T, ?> valueProvider, BiFunction<Renderer<T>, String, C> columnFactory) {
        String columnId = createColumnId(false);

        C column = addColumn(
                new ColumnPathRenderer<T>(columnId,
                        item -> formatColumnValue(colById(columnId),
                                applyValueProvider(valueProvider, item))),
                columnFactory);
        // Set comparator in the same way as in super implementation using reflection
        // setComparator has side effects
        try {
            final Field field = Column.class.getDeclaredField("comparator");
            field.setAccessible(true);
            SerializableComparator<T> c = ((a, b) -> compareMaybeComparables(
                    applyValueProvider(valueProvider, a),
                    applyValueProvider(valueProvider, b)));
            field.set(column, c);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (autoConfigureFromGetterReferences) {
            autoConfigureFromGetterReference(column, valueProvider);
        }
        return column;
    }

    /**
     * If the given value provider is a method reference to a getter, configures the
     * column like {@link #addColumn(String)} would: the property name becomes the
     * column key, the property caption becomes the header and a column of a
     * {@link Comparable} property becomes sortable.
     * <p>
     * Everything here is best effort. A value provider that can't be tied to a
     * property, a plain lambda expression in particular, leaves the column
     * untouched, as do a property name that is already taken by another column and
     * a method reference that doesn't look like a getter of this grid's bean type.
     * </p>
     *
     * @param column        the freshly created column
     * @param valueProvider the value provider the column was created from
     */
    protected void autoConfigureFromGetterReference(Column<T> column, ValueProvider<T, ?> valueProvider) {
        String propertyName = resolvePropertyName(valueProvider);
        if (propertyName == null) {
            return;
        }
        if (column instanceof VColumn<T> vColumn) {
            vColumn.setAutomaticKey(propertyName);
        }
        // Nested paths are captioned by their last part, like Vaadin does for beans
        String caption = propertyName.substring(propertyName.lastIndexOf('.') + 1);
        column.setHeader(SharedUtil.propertyIdToHumanFriendly(caption));
        if (isComparableProperty(propertyName, valueProvider)) {
            column.setSortable(true);
        }
    }

    /**
     * Digs the property name out of a value provider, if it is a method reference
     * to a getter of this grid's bean type.
     *
     * @param valueProvider the value provider
     * @return the property name, or null if the value provider is not a getter reference
     */
    private String resolvePropertyName(ValueProvider<T, ?> valueProvider) {
        if (valueProvider instanceof PropertyRef<?, ?> propertyRef) {
            // The developer has stated the intent explicitly
            try {
                return propertyRef.getPropertyName();
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        SerializedLambda lambda;
        try {
            lambda = PropertyRefs.serializedLambda(valueProvider);
        } catch (RuntimeException e) {
            return null;
        }
        // Not a method reference but a lambda expression, or not a no-arg getter
        if (lambda.getImplMethodName().startsWith("lambda$")
                || !lambda.getImplMethodSignature().startsWith("()")) {
            return null;
        }
        String methodName = lambda.getImplMethodName();
        if (methodName.equals("getClass") || methodName.equals("toString")
                || methodName.equals("hashCode")) {
            return null;
        }
        String propertyName = PropertyRefs.propertyNameFromGetterName(methodName);
        if (bbd != null) {
            // The bean type is known, so only accept its actual properties. This also
            // covers records and other accessors without a JavaBeans style prefix.
            return getBeanPropertyNames().contains(propertyName) ? propertyName : null;
        }
        // Without a bean type all we can do is trust the JavaBeans naming convention
        return propertyName.equals(methodName) ? null : propertyName;
    }

    private boolean isComparableProperty(String propertyName, ValueProvider<T, ?> valueProvider) {
        if (bbd != null) {
            return getBeanPropertyDefinitions().stream()
                    .filter(p -> p.getName().equals(propertyName))
                    .findFirst()
                    .map(p -> Comparable.class.isAssignableFrom(p.getPrimaryType().getRawClass()))
                    .orElse(false);
        }
        try {
            String signature = PropertyRefs.serializedLambda(valueProvider).getImplMethodSignature();
            String returnType = signature.substring(signature.indexOf(')') + 1);
            if (returnType.length() == 1) {
                // A primitive, all of which have Comparable wrapper types, except void
                return !returnType.equals("V");
            }
            if (returnType.startsWith("L")) {
                String className = returnType.substring(1, returnType.length() - 1).replace('/', '.');
                return Comparable.class.isAssignableFrom(
                        Class.forName(className, false, valueProvider.getClass().getClassLoader()));
            }
        } catch (RuntimeException | ClassNotFoundException e) {
            // Can't tell, so leave the column as it was
        }
        return false;
    }

    /**
     * Defines whether columns added with a method reference to a getter, like
     * {@code grid.addColumn(Person::getFirstName)}, are configured with the key,
     * header and sortability of that property. On by default.
     * <p>
     * Turn this off to get the behaviour of Vaadin's raw
     * {@link Grid#addColumn(ValueProvider)}, where such a column has no key and no
     * header at all.
     * </p>
     *
     * @param autoConfigureFromGetterReferences true to configure the columns, false to leave them bare
     */
    public void setAutoConfigureFromGetterReferences(boolean autoConfigureFromGetterReferences) {
        this.autoConfigureFromGetterReferences = autoConfigureFromGetterReferences;
    }

    /**
     * @return true if getter reference columns are configured with their property's
     *         key, header and sortability
     */
    public boolean isAutoConfigureFromGetterReferences() {
        return autoConfigureFromGetterReferences;
    }

    /**
     * Defines the default of {@link #setAutoConfigureFromGetterReferences(boolean)}
     * for grids created after this call. Meant as an escape hatch for an
     * application that has a lot of grids and wants the pre 3.8 behaviour.
     *
     * @param autoConfigure true to configure the columns, false to leave them bare
     */
    public static void setAutoConfigureFromGetterReferencesByDefault(boolean autoConfigure) {
        autoConfigureFromGetterReferencesByDefault = autoConfigure;
    }

    // Copy pasted from Grid to override formatting
    private Object applyValueProvider(ValueProvider<T, ?> valueProvider,
                                      T item) {
        Object value;
        try {
            value = valueProvider.apply(item);
        } catch (NullPointerException npe) {
            value = null;
            if (NestedNullBehavior.THROW == getNestedNullBehavior()) {
                throw npe;
            }
        }
        return value;
    }

    private String formatColumnValue(VColumn<T> col, Object value) {
        if (cellFormatter != null) {
            return cellFormatter.formatColumnValue(col, value);
        }
        try {
            return CellFormatter.defaultVaadinFormatting(value);
        } catch (Exception e) {
            // Yes, toString can fail, e.g. sometimes with HbnProxy classes...
            if (value == null) {
                return "null";
            } else {
                return value.getClass().getSimpleName();
            }
        }
    }

    /**
     * Defines a formatter used for all basic data columns.
     *
     * @param formatter the formatter
     * @return the VGrid for further configuration
     */
    public VGrid<T> withCellFormatter(CellFormatter<T> formatter) {
        this.cellFormatter = formatter;
        return this;
    }

    /**
     * Adds a strategy to style cells based on rows.
     *
     * @param rowStyler the RowStyler
     */
    public VGrid<T> withRowStyler(RowStyler<T> rowStyler) {
        var oldCNG = getPartNameGenerator();
        setPartNameGenerator((ValueProvider<T, String>) t -> {
            TreeMap<String, String> styleRules = new TreeMap<>();
            VStyle style = new VStyle() {
                @Override
                public String get(String name) {
                    return styleRules.get(name);
                }

                @Override
                public VStyle set(String name, String value) {
                    styleRules.put(name, value);
                    return this;
                }

                @Override
                public VStyle remove(String name) {
                    styleRules.remove(name);
                    return this;
                }

                @Override
                public VStyle clear() {
                    styleRules.clear();
                    return this;
                }

                @Override
                public boolean has(String name) {
                    return styleRules.containsKey(name);
                }

                @Override
                public Stream<String> getNames() {
                    return styleRules.keySet().stream();
                }

            };
            rowStyler.styleRow(t, style);
            if (styleRules.isEmpty()) {
                return oldCNG != null ? oldCNG.apply(t) : null;
            } else {
                StringBuilder cellCssBody = new StringBuilder();
                styleRules.forEach((k, v) -> {
                    cellCssBody.append("%s: %s;".formatted(k, v));
                });
                String cellCssBodyString = cellCssBody.toString();
                // part/class name unique for the similar style rules
                // if e.g. 5 rows are configured with same style, they will share the same style element
                // currently grid wide optimisation, could be per UI as well
                String key = "dynstyle" + cellCssBodyString.hashCode() + "-rc";
                if (rowCssKeys == null) {
                    rowCssKeys = new HashSet<>();
                }
                boolean newRule = rowCssKeys.add(key);
                if (newRule) {
                    VStyleUtil.inject("""
                            vaadin-grid::part(%s) {
                                %s
                            }
                            """.formatted(
                            key,
                            cellCssBodyString)
                    );
                }
                if (oldCNG != null) {
                    String oldNames = oldCNG.apply(t);
                    if (oldNames != null) {
                        return oldNames + " " + key;
                    }
                }
                return key;
            }
        });
        return this;
    }

    /**
     * Used to assign {@link Style} rules to row cells.
     *
     * @param <T> the row type
     */
    public interface RowStyler<T> {
        /**
         * Assignes {@link Style} rules to row rendered for given item.
         *
         * @param item  the item for which the row is rendered
         * @param style the style rules for given item
         */
        public void styleRow(T item, VStyle style);
    }

    /**
     * An interface to configure formatting of all data
     * cells in the Grid. Not that this does not apply to
     * columns defined with custom renderer.
     *
     * @param <T> the Item type
     */
    public interface CellFormatter<T> {

        public static String defaultVaadinFormatting(Object value) {
            if (value == null) {
                return "";
            }
            String string = String.valueOf(value);
            // strip avoid object name from records default toString (repeated in the column header anyways...)
            if(value.getClass().isRecord() && string.startsWith(value.getClass().getSimpleName())) {
                string =  string.substring(value.getClass().getSimpleName().length() + 1);
                if(string.endsWith("]")) {
                    string = string.substring(0, string.length() - 1);
                }
            }
            // Show first n bytes of byte arrays as hex string
            if (value instanceof byte[] bytes) {
                if (bytes.length > 0) {
                    int max = Integer.min(10, bytes.length);
                    String formatHex = HexFormat.ofDelimiter("").formatHex(bytes, 0, max);
                    if(max < bytes.length) {
                        formatHex += "...";
                    }
                    string = "[#"+formatHex+"]";
                } else {
                    string = "[]";
                }
            }

            return string;
        }

        /**
         * Formats the value in a raw data column.
         * By default, nulls are rendered as "" and non-nulls
         * with String.valueOf(Object).
         *
         * @param col   the column
         * @param value the value to render in the cell
         * @return String representation of the value to be sent to client
         */
        String formatColumnValue(VGrid.VColumn<T> col, Object value);
    }

    public static class VColumn<T> extends Column<T> {

        private Style customStyle;
        private boolean automaticKey;

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

        /**
         * Sets the properties this column is sorted by in the backend, using method
         * references to their getters instead of property name strings.
         *
         * @param properties method references to the getters of the sort properties
         * @return this column, for further configuration
         */
        @SafeVarargs
        public final VColumn<T> withSortProperties(PropertyRef<T, ?>... properties) {
            setSortProperty(toPropertyNames(properties));
            return this;
        }

        /**
         * Sets the key of this column, using a method reference to the getter of the
         * property instead of a string.
         *
         * @param property a method reference to the getter of the property
         * @return this column, for further configuration
         */
        public VColumn<T> withKey(PropertyRef<T, ?> property) {
            setKey(property.getPropertyName());
            return this;
        }

        /**
         * Replaces the way this column is rendered with a component, leaving the rest
         * of the column configuration untouched.
         *
         * @param componentProvider creates the component to show for a row
         * @param <C>               the component type
         * @return this column, for further configuration
         */
        public <C extends Component> VColumn<T> setComponentRenderer(ValueProvider<T, C> componentProvider) {
            return withRenderer(new ComponentRenderer<>(componentProvider));
        }

        /**
         * Replaces the renderer of this column, leaving the rest of the column
         * configuration untouched.
         *
         * @param renderer the new renderer
         * @return this column, for further configuration
         */
        public VColumn<T> withRenderer(Renderer<T> renderer) {
            setRenderer(renderer);
            return this;
        }

        /**
         * Assigns the key that was derived from a getter reference. Unlike a key set
         * by the developer, this one gives way to a later {@link #setKey(String)}
         * call, and is skipped altogether if another column has reserved the name.
         *
         * @param key the property name
         */
        void setAutomaticKey(String key) {
            if (getGrid().getColumnByKey(key) != null) {
                // Another column of the same property already exists, the old
                // behaviour of leaving this one without a key is the safe one
                return;
            }
            super.setKey(key);
            automaticKey = true;
        }

        @Override
        public VColumn<T> setKey(String key) {
            if (automaticKey) {
                releaseAutomaticKey();
            }
            super.setKey(key);
            automaticKey = false;
            return this;
        }

        private void releaseAutomaticKey() {
            try {
                Field keyToColumnMap = Grid.class.getDeclaredField("keyToColumnMap");
                keyToColumnMap.setAccessible(true);
                ((Map<?, ?>) keyToColumnMap.get(getGrid())).remove(getKey());
                Field columnKey = Column.class.getDeclaredField("columnKey");
                columnKey.setAccessible(true);
                columnKey.set(this, null);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(
                        "Failed to replace the column key derived from a getter reference", e);
            }
            automaticKey = false;
        }

        @Override
        public Style getStyle() {
            // super implementation is completely useless

            if (customStyle != null) {
                return customStyle;
            }

            int indexOfColumn = getGrid().getColumns().indexOf(this);

            customStyle = new Style() {

                /** For the header we are adding the Style rules for th and the header component/text to make it easier to override default styles with strong selectors. Some rules are harmful here though, like outline/border etc */
                private static final String[] harmfulAsDuplicate = new String[]{
                        "border", "outline", "padding", "margin", "zoom"
                };
                private TreeMap<String, String> styles = new TreeMap<>();
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
                    if (!deferred) {
                        getGrid().getElement().getNode().runWhenAttached(ui -> doApply());
                        deferred = true;
                    }
                }

                private void doApply() {
                    StringBuilder cellCssBody = new StringBuilder();
                    StringBuilder headerContentCssBody = new StringBuilder();
                    styles.forEach((k, v) -> {
                        cellCssBody.append("%s: %s;".formatted(k, v));
                        if (Arrays.stream(harmfulAsDuplicate).noneMatch(k::contains)) {
                            headerContentCssBody.append("%s: %s;".formatted(k, v));
                        }
                    });
                    String cellCssBodyString = cellCssBody.toString();
                    // part/class name unique for the similar style rules
                    // if 5 cols are mady with same style, they will share the same style element
                    // currently grid wide optimisation, could be per UI as well
                    String key = "dynstyle" + cellCssBodyString.hashCode();

                    String headerText = getHeaderText();
                    if (headerText != null) {
                        setHeader(new Span(headerText));
                    }
                    if (getHeaderComponent() != null) {
                        getHeaderComponent().addClassName(key + "-hc");
                    }
                    setPartNameGenerator(p -> key);

                    getGrid().getElement().executeJs("const g = this; setTimeout(() => {g.shadowRoot.querySelector('th:nth-child(" + (indexOfColumn + 1) + ")').part.add('" + key + "');}, 1);");

                    VGrid grid = findAncestor(VGrid.class);
                    if (grid.columnCssKeys == null) {
                        grid.columnCssKeys = new HashSet<>();
                    }
                    boolean newRule = grid.columnCssKeys.add(key);
                    if (newRule) {
                        VStyleUtil.inject("""
                                vaadin-grid::part(%s) {
                                    %s
                                }
                                .%s-hc {
                                    %s
                                }
                                """.formatted(
                                key,
                                cellCssBodyString,
                                key,
                                headerContentCssBody.toString())
                        );
                    }
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
