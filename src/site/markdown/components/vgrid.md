## VGrid

`VGrid` is Flow Viritin's enhanced Grid. Beyond the usual fluent
"`with*`-returns-this" treatment, it ships with a richer styling and
formatting API than Vaadin core Grid, plus sensible defaults for
record and bean-based data.

### Why VGrid over Grid

* Columns can be referred to with getter method references instead of
  property name strings, so the compiler and the IDE's rename
  refactoring keep them in sync.
* `addColumn(Person::getFirstName)` gives the column a key, a header
  and sorting, instead of a bare, headerless column.
* Reads columns in declaration order for both POJOs and Java
  `record`s (the core introspector returns them in a random order).
* Falls back to Jackson when the default Vaadin mechanism cannot
  resolve a column (e.g. for default methods introduced in Java 8).
* `Column.getStyle()` actually applies the style -- the core API
  silently drops style rules.
* Adds an optional built-in column selector menu similar to the one
  in Vaadin 8.

### Columns without strings

Vaadin core Grid identifies properties by name, as strings. `VGrid`
accepts a method reference to the getter anywhere a property name is
expected, which the compiler checks and the IDE renames along with the
getter:

```java
VGrid<Person> grid = new VGrid<>(Person.class, false);

grid.setColumns(Person::getFirstName, Person::getLastName, Person::getAge);
grid.setSortableColumns(Person::getLastName);
grid.getColumnByKey(Person::getAge).setHeader("Years");
```

Every method that takes a property name has such a counterpart:

| String API | Getter reference API |
| --- | --- |
| `setColumns("firstName")` | `setColumns(Person::getFirstName)` |
| `withProperties("firstName")` | `withProperties(Person::getFirstName)` |
| `hideProperties("firstName")` | `hideProperties(Person::getFirstName)` |
| `addColumns("firstName")` | `addColumns(Person::getFirstName)` |
| `addColumn("firstName")` | `addPropertyColumn(Person::getFirstName)` |
| `setSortableColumns("firstName")` | `setSortableColumns(Person::getFirstName)` |
| `getColumnByKey("firstName")` | `getColumnByKey(Person::getFirstName)` |
| `removeColumnByKey("firstName")` | `removeColumnByKey(Person::getFirstName)` |
| `setColumnOrder(...)` | `setColumnOrder(Person::getAge, Person::getFirstName)` |
| `column.setKey("firstName")` | `column.withKey(Person::getFirstName)` |
| `column.setSortProperty("firstName")` | `column.withSortProperties(Person::getFirstName)` |

Record accessors work just as well as JavaBeans getters:

```java
record Person(String name, int age) {}

VGrid<Person> grid = new VGrid<>(Person.class, false);
grid.setColumns(Person::name, Person::age);
```

Nested properties, the equivalent of the `"address.street"` dot
notation, are expressed by chaining with `PropertyRef`:

```java
grid.setColumns(
    PropertyRef.of(Person::getName),
    PropertyRef.of(Person::getAddress).then(Address::getStreet));
```

The property name is dug out of the `SerializedLambda` that the JVM
creates for serializable lambdas, so this needs no extra dependency and
no particular compiler -- javac and the Eclipse compiler both work. The
resolution is cached per call site, so the cost is paid once per
`Person::getFirstName` expression in your source code.

Only an actual method reference carries a property name. A lambda
expression like `p -> p.getFirstName()` does not, and fails fast with
an `IllegalArgumentException` telling you to use a method reference.

### Columns added with a getter reference

`Grid.addColumn(ValueProvider)` produces a column with no key, no
header and no sorting, which is why applications tend to repeat what
the getter already said:

```java
// Vaadin core Grid
addColumn(Person::getFirstName).setHeader("First Name");
addColumn(Person::getLastName).setHeader("Last Name");
addColumn(Person::getAge).setHeader("Age");
```

`VGrid` recognises that the value provider is a getter reference and
configures the column just like `addColumn("firstName")` would: the
property name becomes the column key, the property caption becomes the
header, and a `Comparable` property becomes sortable. The above is
simply:

```java
// VGrid, same headers, and sortable as a bonus
addColumn(Person::getFirstName);
addColumn(Person::getLastName);
addColumn(Person::getAge);
```

Note that this is a runtime observation about the value provider, not a
rule about how the call is written, so a `ValueProvider` variable or
parameter that happens to hold a getter reference is treated the same
way.

Anything that is not a getter reference is left exactly as Vaadin core
leaves it. In particular a lambda expression, a method that is not a
JavaBeans getter or a property of the bean type, `toString`,
`getClass`, `hashCode`, and `addComponentColumn` in its entirety.

Whatever you set explicitly wins:

```java
// The header derived from the getter is replaced
addColumn(Person::getFirstName).setHeader("Etunimi");

// So is the key, no "Column key cannot be changed"
addColumn(Person::getFirstName).setKey("name");
```

If another column has already reserved the property name, the key is
simply not assigned, so two columns of the same property don't clash.

The behaviour can be turned off per grid, or for the whole application
if you are upgrading a code base that relies on the bare columns:

```java
grid.setAutoConfigureFromGetterReferences(false);

// Or once at startup, affects grids created afterwards
VGrid.setAutoConfigureFromGetterReferencesByDefault(false);
```

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

`setColumns(Person::age, Person::name)` -- or the string based
`setColumns("age", "name")` -- still works if you want a different
order.
