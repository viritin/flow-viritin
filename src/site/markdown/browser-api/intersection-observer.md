## IntersectionObserver

`IntersectionObserver` is a Java wrapper around the browser's
[IntersectionObserver API](https://developer.mozilla.org/en-US/docs/Web/API/IntersectionObserver).
It lets you detect when Vaadin components become visible (or hidden) within the
viewport or a scrollable container, and react in server-side Java code.

### Why use it?

Detecting whether an element is visible on screen is surprisingly hard to do
correctly with scroll listeners alone. The browser's `IntersectionObserver`
handles this efficiently and this helper brings it to server-side Java.

Common use cases:

* **Lazy loading** -- load data or images only when a placeholder scrolls into
  view.
* **Infinite scroll** -- append more content when the user reaches the bottom of
  a list.
* **Visibility tracking** -- trigger animations or analytics when an element
  first appears.
* **Resource management** -- pause expensive operations (video, polling) for
  off-screen components.

Key features:

* Observe intersection with the **viewport** or with a specific **scrollable
  container** (custom root).
* Events are **debounced** (100 ms by default) -- intersection changes are
  accumulated on the client and sent to the server in a single roundtrip.
* Proper **cleanup on detach** -- observers are removed automatically when the
  component leaves the UI.

### Getting an instance

For viewport-level observation (is the component visible in the browser window?):

```java
// Using the current UI (most common)
IntersectionObserver io = IntersectionObserver.get();

// Or for a specific UI
IntersectionObserver io = IntersectionObserver.of(myUi);
```

For observation within a specific scrollable container (e.g. a `Scroller`):

```java
IntersectionObserver io = IntersectionObserver.of(myUi, myScroller);
```

The viewport observer is a singleton per UI (like `ResizeObserver`).
Root-specific observers are created per call -- keep a reference if you need to
add observations later.

### Observing a component

Pass a component and a listener that receives an `IntersectionEntry`:

```java
IntersectionObserver.get().observe(myComponent, entry -> {
    if (entry.isIntersecting()) {
        // Component is now visible
    }
    double ratio = entry.intersectionRatio(); // 0.0 .. 1.0
});
```

You can chain multiple `observe` calls:

```java
IntersectionObserver.get()
    .observe(banner, entry -> {
        if (entry.isIntersecting()) {
            trackImpression(banner);
        }
    })
    .observe(lazyImage, entry -> {
        if (entry.isIntersecting()) {
            lazyImage.setSrc(actualImageUrl);
        }
    });
```

### The IntersectionEntry record

| Field | Type | Description |
|-------|------|-------------|
| `isIntersecting()` | `boolean` | `true` when the component overlaps the root |
| `intersectionRatio()` | `double` | Fraction of the component that is visible (0.0 to 1.0) |

### Fire-once observation

A common pattern is to react only the *first time* a component becomes visible,
then stop observing it to avoid further server traffic:

```java
var io = IntersectionObserver.get();
io.observe(myComponent, entry -> {
    if (entry.isIntersecting()) {
        doSomething();
        io.unobserve(myComponent);
    }
});
```

### Stopping observation

Pass the same listener reference to remove a specific listener:

```java
IntersectionObserver.IntersectionListener listener = entry -> { /* ... */ };
io.observe(component, listener);

// Later:
io.unobserve(component, listener);
```

Or stop observing a component entirely (removes all listeners):

```java
io.unobserve(component);
```

### Configuration

#### Custom root (scrollable container)

By default the viewport is used as the intersection root. To observe visibility
within a scrollable container like `VScroller`, pass it as the root:

```java
VScroller scroller = new VScroller(content);
scroller.setHeight("400px");

var io = IntersectionObserver.of(UI.getCurrent(), scroller);
io.observe(someChild, entry -> {
    // fires when someChild scrolls into/out of the scroller's visible area
});
```

#### Thresholds

Control at which visibility ratios the observer fires:

```java
// Fire when the component is 0%, 50%, and 100% visible
io.withThresholds(0, 0.5, 1.0);
```

The default threshold is `0` -- the listener fires as soon as even one pixel is
visible (and again when the component is fully hidden).

#### Root margin

Expand or shrink the root's detection area. This is useful for triggering
actions *before* an element actually scrolls into view (pre-loading):

```java
// Start loading 200px before the element becomes visible
io.withRootMargin("200px");
```

#### Debounce timeout

Intersection changes are accumulated on the client side and flushed to the
server after a debounce timeout. This prevents flooding the server during rapid
scrolling. The default is 100 ms.

```java
io.withDebounceTimeout(250); // 250 ms
```

### Full example: marking rows visible in a Scroller

This example creates a scrollable list of 100 rows. As the user scrolls, each
row's text is updated the first time it becomes visible:

```java
@Route
public class ScrollerExample extends VVerticalLayout {

    public ScrollerExample() {
        VerticalLayout content = new VerticalLayout();
        Span[] rows = new Span[100];
        for (int i = 0; i < 100; i++) {
            rows[i] = new Span("Row " + i);
            content.add(rows[i]);
        }

        VScroller scroller = new VScroller(content);
        scroller.setHeight("300px");
        scroller.setWidth("300px");
        add(scroller);

        addAttachListener(e -> {
            var io = IntersectionObserver.of(e.getUI(), scroller);
            for (Span row : rows) {
                io.observe(row, entry -> {
                    if (entry.isIntersecting()) {
                        row.setText(row.getText() + " now visible");
                        io.unobserve(row);
                    }
                });
            }
        });
    }
}
```

The `addAttachListener` ensures the UI is available when creating the observer.
Each row is unobserved after its first intersection to avoid repeated server
visits.
