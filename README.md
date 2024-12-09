[![Published on Vaadin  Directory](https://img.shields.io/badge/Vaadin%20Directory-published-00b4f0.svg)](https://vaadin.com/directory/component/flow-viritin)
[![Stars on vaadin.com/directory](https://img.shields.io/vaadin-directory/star/flow-viritin.svg)](https://vaadin.com/directory/component/flow-viritin)

# Flow Viritin

A similar helper library as "old Viritin" was for Vaadin 8 and earlier. See [the Directory page](https://vaadin.com/directory/component/flow-viritin) for more details. Tries to add missing features, fix some issues in the core components and to work as an agile arena to test new potential features for Vaadin.

*Version matrix:*
 * 2.x series for Vaadin 24 (Maven central)
 * 1.7.1+ for Vaadin 23.3+ (distribution via Maven central, directory repository is not necessary needed, "coordinates": in.virit:viritin)
 * 0.9.0 for Vaadin 22+ (distribution via Maven central, directory repository is not necessary needed, "coordinates": in.virit:viritin)
 * 0.4.0 for Vaadin 14
 * The [old add-on](https://vaadin.com/directory/component/viritin) for Vaadin 8 and 7

## Feature summary (not a complete list):

###  Fluent API wrappers for most vaadin-core components

"V" prefixed components provide most core APIs are available with a version that returns the component itself. These methods typically start with "with" instead of "set". This allows configuration of objects
e.g. in field declaration or without declaring an extra variable. When used in appropriate places, this can produce more concise and readable code. 

### Uploads & Download like they should be

 * UploadFileHandler aka Upload component with [a proper Java API](https://vaadin.com/blog/uploads-and-downloads-inputs-and-outputs).
   * Simply passes the input stream for you to handle, no weird Vaadin specific APIs
   * Read/handle the contents while it is being uploaded, also works with Spring Boot
   * Throttles amount of concurrent connections used, instead of choking the network
   * Implements HasEnabled
 * DynamicFileDownloader for simple generation of dynamically generated file downloads. No need to buffer content in memory, but simply write it to the OutputStream, like you would do with e.g. raw Servlet API
 * ByteArrayUploadField to bind a file upload to a `byte[]` field with Binder.

### Access to browser API & others

 * Geolocation API using a pure Java API
 * BrowserCookie helper that works well with web sockets too
 * LumoProps, Java enumeration for easier discoverability (& redefining/using) of the handy Lumo theme css properties.
 * ~WebStorage helper to save data in the browsers localStorage or sessionStorage~ (available for backwards compatibility, but the same implementation found in core as well these days)
 * Like with [JS developer can with the ResizeObserver](https://developer.mozilla.org/en-US/docs/Web/API/ResizeObserver), with Viritin one can observe the component sizes and hook Java logic to changes. If you are using Viritin components or implementing `FluentHasSize` on your component, you can [hook listener directly to the component](https://github.com/viritin/flow-viritin/blob/v24/src/test/java/org/vaadin/firitin/resizeobserver/ResizeObserverCoreApiDrafting.java#L32-L38). Otherwise, you can use the [ResizeObserver utility](https://github.com/viritin/flow-viritin/blob/v24/src/test/java/org/vaadin/firitin/resizeobserver/ResizeObserverView.java).

### VGrid

 * Fluent API configuring like with other Viritin components
 * [Supports Java `record`s](https://github.com/viritin/flow-viritin/blob/v24/src/test/java/org/vaadin/firitin/RecordsWithGrid.java)
 * Optional [built-in column selector](https://github.com/viritin/flow-viritin/blob/320c544e6a314115b18109d8469c2e727ac77da7/src/test/java/org/vaadin/firitin/Grids.java#L81) (menu in the header) like in Vaadin 8.
 * The default order of columns (via introspection) is what you would expect, instead of random, both for basic Java beans and records. [Uses Jackson instead of JDK Introspector](https://github.com/viritin/flow-viritin/blob/c99a31cab256ff2ed455c7413e164a87c85b507a/src/main/java/org/vaadin/firitin/components/grid/VGrid.java#L66-L75) from `java.desktop` package (that infamously stores properties in HashSet 🤦‍). Excellent for rapid application development!
 * In case the default Vaadin mechanism fails to generate column (e.g. using default method introduced in Java 8), VGrid [falls back to Jackson to generate to column](https://github.com/viritin/flow-viritin/blob/c99a31cab256ff2ed455c7413e164a87c85b507a/src/main/java/org/vaadin/firitin/components/grid/VGrid.java#L121-L147). Less manual column definition.
 * [CellFormatter](https://github.com/viritin/flow-viritin/blob/v24/src/test/java/org/vaadin/firitin/Grids.java#L45-L63) to modify all "raw data cells" columns or based on column details.
 * [Column.getStyle() method works](https://github.com/viritin/flow-viritin/blob/v24/src/test/java/org/vaadin/firitin/Grids.java#L68-L73) instead of silently eating the style rules. 
 * Ability to programmatically set [style rules for rows](https://github.com/viritin/flow-viritin/blob/fe58310e178e15be99a4ea0619549738a83d5ac8/src/test/java/org/vaadin/firitin/Grids.java#L82-L87).

### Fields and form components

 * CommaSeparatedStringField to edit List<String> with Binder
 * SubListSelector to pick a List<T> from a large set of Ts with Binder (note, no re-ordering yet).
 * DeleteButton, shows confirm dialog and is styled so that one don't accidentally click it
 * Tree component to visualise hierarchical structures
 * EnumSelect
 * ElementCollectionField a field to edit e.g. List<Address> type structure
 * ~LocalDateTimeField for editing LocalDateTime objects with Binder~ Use DateTimePicker from core, that is available these days.
 * VLocalDateTimePicker and VLocalDatePicker who use the month names from the selected locale.
 * Text [Selection API](https://javadoc.dokku1.parttio.org/in.virit/viritin/2.5.2/org/vaadin/firitin/components/textfield/SelectionApi.html) (selectAll, getCursorPosition setSelection, getSelection et al.) for inputs that are based on keyboard input. Essentials to fine tune the UX of "pro user views".

### Forms and Form binding

**FormBinder** (since 2.8.0) helps you to connect your domain objects to your form fields. It supports both POJOs and Java records. The API in FormBinder encourages you to write your validation logic separately from your UI components, but support you to display your constraints violations in your fields and form (for class level validators). You can provide validation messages either with Java Bean Validation API or as a raw String-String map, in case you can't or don't want to use Bean Validation.

`FormBinder` uses "name based binding" by default, so if your property in POJO/record is `firstName`, a UI component to edit that property is expected to be found as a field with name `firstName` from one of the "container objects" you pass for the FormBinder in its constructor. A non-goal of FormBinder is to workaround missing property literals in Java itself, and thus strings are still needed to refer fields/bindings e.g. during validation. The plan is to add annotation(s) with binding metadata for cases where the name based binding is not applicable (waiting for good example cases 🤓).

*Why not using the `Binder` component in the Vaadin core? Well, it has a couple of design flaws that make it close to impossible for us to provide a proper Bean Validation API support for it, it doesn't support records and has a massive API that easily guides toward bad practices, like writing your validation logic against your UI fields.*

**BeanValidationForm** (since 2.8.0) is an opinionated abstract super class for "bulk forms". It uses `FormBinder` to do the binding and this way supports all the things it does as well, but provides you automatic basic Java Bean Validation (customizable validation groups!), basic layout, automatically adjusted save/cancel buttons etc. If you "just want a basic form for your DTO", a coded, not autogenerated, you might want to use this as the basis. *Note, that this class is still in very early phase and tested less than the FormBinder itself. Although it draws ideas from a similar older version, expect some bugs and changes for its API.*

**AbstractForm** is the older solution, based on the Binder from Vaadin core. It also configures binder properly for naming convention based binding and provides basic for features such as save and cancel buttons that enable themselves based on the user actions. Currently this is more battle provent, but problematic in many ways (too many options due to Binder), so I expect the development of this helper to be ended.

### Rapid Application Development

 * DtoDisplay, a component to visualize a given DTO value in a grid layout.

### Uncategorised helpers

 * RichText component to easily show Html or Markdown formatted text. [Online demo](https://addons.dokku1.parttio.org/)
 * Traditional paging with PagingGrid. [Online demo](https://addons.dokku1.parttio.org/paginggrid)
 * CustomLayout component (renders raw html superfast, while you can still place Vaadin components inside it)
 * BorderLayout (like in ~ Swing, implemented with CSS Grid)
 * A generic MainLayout suitable for many small to medium sized projects, based on the AppLayout and SideNave components. Populates your views to the menu automatically and this way removes a ton of boilerplate code of your app.
 * TreeTable. Like TreeGrid in the core, but with much easier API to populate items. Also properly supports [lazy loading](https://vaadin.com/blog/lazy-loading-hierarchical-data-from-ui-to-database) and scrolling to given item without extra hacks.
 * JsPromise, an Element.executeJs replacement for modern async JS APIs.

## Documentation

The [test directory](https://github.com/viritin/flow-viritin/tree/v24/src/test/java/org/vaadin/firitin) contains usage examples for components. Some components have decent JavaDocs. An actual manual would be great and an easy way to contribute back if you find the helpers useful 🤓

## Contributing

Yes please! TIP: Check [the project](https://github.com/viritin/flow-viritin) out alongside your app project and create handy helpers directly to Viritin. Send in pull requests and join the effort!  

Mirjan Merruko and Stefan Freude are so far the most active contributors to this great tool.



## Development instructions

Starting the test/demo server:

Run the main method of TestServer in your IDE or run the following command in the project root:

```
mvn org.springframework.boot:spring-boot-maven-plugin:test-run
```

This deploys demo views at http://localhost:9998
