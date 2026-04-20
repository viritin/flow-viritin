## Components

Flow Viritin provides enhanced versions of many Vaadin core components as well as
entirely new UI building blocks.

### Fluent API wrappers

Most Vaadin core components have a "V"-prefixed wrapper that returns the
component itself from setter-like methods (named `with*` instead of `set*`).
This allows configuring components inline, for example in field declarations,
without introducing extra variables.

### VGrid

An enhanced Grid with sensible defaults:

* Supports Java `record` types out of the box.
* Columns appear in declaration order (not random).
* Built-in column selector menu (like Vaadin 8).
* Fallback column generation via Jackson when the default mechanism fails.
* `CellFormatter` for styling raw data columns.
* `Column.getStyle()` that actually works.
* Programmatic row styling.

### Uploads & Downloads

* **UploadFileHandler** -- Upload component with a straightforward
  `InputStream`-based Java API. Throttles concurrent connections and works
  with Spring Boot without special configuration.
* **DynamicFileDownloader** -- Generate file downloads on the fly by writing
  to an `OutputStream`.
* **ByteArrayUploadField** -- Bind a file upload to a `byte[]` property
  with Binder.

### Tree & TreeTable

* **Tree** -- Visualise hierarchical structures with a simple API.
* **TreeTable** -- Like `TreeGrid` but with an easier population API,
  proper lazy loading, and scrolling-to-item support.

### Other components

* **RichText** -- Display HTML or Markdown content.
* **CustomLayout** -- Render raw HTML while placing Vaadin components inside.
* **BorderLayout** -- Swing-style layout implemented with CSS Grid.
* **PagingGrid** -- Traditional paging for Grid.
* **DeleteButton** -- Confirmation dialog + error styling to prevent
  accidental clicks.
* **DisclosurePanel**, **Badge**, **VSvg**, and more.
