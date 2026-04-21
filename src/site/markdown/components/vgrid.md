## VGrid

`VGrid` is Flow Viritin's enhanced Grid. Beyond the usual fluent
"`with*`-returns-this" treatment, it ships with a richer styling and
formatting API than Vaadin core Grid, plus sensible defaults for
record and bean-based data.

### Why VGrid over Grid

* Reads columns in declaration order for both POJOs and Java
  `record`s (the core introspector returns them in a random order).
* Falls back to Jackson when the default Vaadin mechanism cannot
  resolve a column (e.g. for default methods introduced in Java 8).
* `Column.getStyle()` actually applies the style -- the core API
  silently drops style rules.
* Adds an optional built-in column selector menu similar to the one
  in Vaadin 8.

### Column styling with typed Style methods

`Column.getStyle()` returns the same `Style` instance you use elsewhere
in Flow, so strongly typed setters like `setBackground`,
`setTextAlign` or `setOutline` are available -- no more
`set("border", "1px solid")` string fiddling.

```java
VGrid<Person> grid = new VGrid<>(Person.class);
grid.setItems(people);

grid.getColumnByKey("firstName")
    .setHeader("First name")
    .getStyle()
        .setBackground("red")
        .setColor("white")
        .setFont("bold 20px Arial")
        .setTextAlign(Style.TextAlign.RIGHT)
        .setOutline("2px dotted black");
```

Assigning the same rule to multiple columns reuses the underlying
style element, so the overhead of styling many columns stays small:

```java
grid.getColumnByKey("firstName").getStyle().set("color", "red");
// Reuses the "color: red" style element from above
grid.getColumnByKey("lastName").getStyle().set("color", "red");
```

### Per-row styling with a RowStyler

`withRowStyler` lets you compute a row's style from the item itself.
The style is applied to the whole row without template tricks:

```java
VGrid<Person> grid = new VGrid<>(Person.class)
    .withRowStyler((person, style) -> {
        if (person.getId() % 5 == 0) {
            style.setColor("blue");
            style.setBackgroundColor("lightgray");
        }
    });
```

### Grid-wide CellFormatter

Rather than configuring each column's renderer individually, a single
`CellFormatter` can format values for every column. It receives the
column and the cell value, and returns the string to display.

```java
VGrid<Person> grid = new VGrid<>(Person.class)
    .withCellFormatter((column, value) -> {
        if (value instanceof LocalDateTime dt) {
            return dt.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        // Target specific columns by header text or key if needed:
        // if (column.getHeaderText().contains("Wind")) {
        //     return "%.0f m/s".formatted((Double) value);
        // }
        return VGrid.CellFormatter.defaultVaadinFormatting(value);
    });
```

`VGrid.CellFormatter.defaultVaadinFormatting` handles the common cases
(`null` becomes the empty string, everything else goes through
`String.valueOf`), so your formatter only has to worry about the
types it wants to customise.

### Built-in column selector menu

Enable an in-header menu that lets users show or hide columns:

```java
VGrid<Person> grid = new VGrid<>(Person.class)
    .withColumnSelector();
```

### Works with records

No extra setup is needed for Java `record`s -- columns appear in the
order the components are declared in the record:

```java
record Person(String name, int age) {}

VGrid<Person> grid = new VGrid<>(Person.class);
grid.setItems(
    new Person("Alice", 25),
    new Person("Bob", 30),
    new Person("Charlie", 22)
);
```

`setColumns("age", "name")` still works if you want a different order.
