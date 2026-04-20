## ResizeObserver

`ResizeObserver` is a Java wrapper around the browser's
[ResizeObserver API](https://developer.mozilla.org/en-US/docs/Web/API/ResizeObserver).
It lets you observe size changes of any Vaadin component and react to them in
server-side Java code.

### Why use it?

Vaadin's built-in `Page.addBrowserWindowResizeListener` only tells you about
the *browser window* size and you have to wait for the first resize event (or
combine it with `retrieveExtendedClientDetails`) to get the initial dimensions.

`ResizeObserver` improves on this in several ways:

* Reports the size of **any component**, not just the window.
* Fires the **initial size immediately** -- no need for a separate "get current
  size" call.
* Events are **debounced** (100 ms by default) to avoid flooding the server
  during continuous resizing.
* Proper **cleanup on detach** -- listeners are removed automatically when the
  observed component is detached from the UI.

### Getting the instance

There is one `ResizeObserver` per UI. Obtain it with:

```java
// Using the current UI (most common)
ResizeObserver ro = ResizeObserver.get();

// Or for a specific UI
ResizeObserver ro = ResizeObserver.of(myUi);
```

### Observing a single component

There are two listener styles. Pick whichever fits your code better.

#### Simple listener

The `observe` method accepts a component and a `SizeChangeListener` that
receives a `Dimensions` record:

```java
ResizeObserver.get().observe(myComponent, dimensions -> {
    int w = dimensions.width();
    int h = dimensions.height();
    // react to new size
});
```

You can chain multiple `observe` calls:

```java
ResizeObserver.get()
    .observe(header, d -> header.setText("Header: " + d.width() + "px"))
    .observe(grid, d -> {
        if (d.width() > 600) {
            grid.setColumns("name", "email", "phone", "role");
        } else {
            grid.setColumns("name", "email");
        }
    });
```

#### Vaadin core-style listener

The `addResizeListener` method returns a `Registration` and delivers a
`SizeChangeEvent`:

```java
Registration reg = ResizeObserver.get()
    .addResizeListener(myComponent, event -> {
        int width = event.getWidth();
        int height = event.getHeight();
        Dimensions d = event.getDimensions();
    });

// Later, stop listening:
reg.remove();
```

### Observing multiple components together

When you need **coordinated dimensions** of several components at once (e.g. to
draw a line between two elements), use the multi-component overload:

```java
Registration reg = ResizeObserver.get()
    .observe((dimensionsMap) -> {
        Dimensions d1 = dimensionsMap.get(button1);
        Dimensions d2 = dimensionsMap.get(button2);
        // both are fresh, measured at the same time
        drawLineBetween(d1, d2);
    }, button1, button2);
```

Whenever *any* of the observed components changes size, the listener receives a
map with up-to-date `Dimensions` for **all** of them.

### The Dimensions record

`Dimensions` mirrors the browser's
[DOMRectReadOnly](https://developer.mozilla.org/en-US/docs/Web/API/DOMRectReadOnly)
plus offset properties:

| Field | Description |
|-------|-------------|
| `x`, `y` | Origin of the content rect |
| `width`, `height` | Content box size |
| `top`, `right`, `bottom`, `left` | Edge positions |
| `offsetLeft`, `offsetTop` | Element offset position |
| `offsetWidth`, `offsetHeight` | Element offset size (includes borders) |

### Stopping observation

With the simple API, pass the same listener reference to `unobserve`:

```java
ResizeObserver.SizeChangeListener listener = d -> { /* ... */ };
ResizeObserver.get().observe(component, listener);

// Later:
ResizeObserver.get().unobserve(component, listener);
```

With the Vaadin-style API, use the returned `Registration`:

```java
Registration reg = ResizeObserver.get().addResizeListener(component, e -> { /* ... */ });
reg.remove();
```

### Tuning the debounce timeout

By default, resize events are debounced at 100 ms. You can change this:

```java
ResizeObserver.get().withDebounceTimeout(250); // 250 ms
```

### Full example: responsive Grid

Viritin's `VGrid` (and other components implementing `FluentHasSize`)
provides a convenient `addResizeListener` helper that observes the
component's own size. This makes it easy to build a Grid that adapts its
columns to the available space -- showing all columns on wide screens and
collapsing to a single essential column on handheld-sized devices:

```java
@Route
public class MyResponsiveGrid extends VGrid<Person> {
    private Mode mode;

    public MyResponsiveGrid() {
        super(Person.class);
        addResizeListener(event -> {
            configureColumns(event.getWidth() < 800 ? Mode.MOBILE : Mode.DESKTOP);
        });
    }

    private void configureColumns(Mode mode) {
        if (this.mode != mode) {
            this.mode = mode;
            if (mode == Mode.MOBILE) {
                setColumns("name");
            } else {
                setColumns("name", "email", "phone", "role");
            }
        }
    }

    enum Mode {
        MOBILE, DESKTOP
    }
}
```

The `addResizeListener` on `FluentHasSize` is a shorthand that
delegates to `ResizeObserver.get().addResizeListener(this, listener)`.
The guard `if (this.mode != mode)` avoids reconfiguring columns on every
resize event when the mode hasn't actually changed.

Because the listener reacts to the **component's own width** (not the
browser window), the Grid adapts correctly even when placed inside a
`SplitLayout` or a narrow sidebar.
