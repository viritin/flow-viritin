## MobileMainLayout

> **Experimental.** `MobileMainLayout` is new and marked `@Deprecated` on
> purpose: its API may still evolve, even in patch releases. It is ready to use
> and to give feedback on — just pin your version if you depend on the details.

`MobileMainLayout` is a [`MainLayout`](index.html) tuned for phones, in the
spirit of native iOS/Android apps:

* the primary navigation is an icon (optionally + caption) **bottom bar** instead
  of a side drawer, and
* the view title is a large heading at the top of the content that **scrolls
  away** as the user scrolls down.

It is **adaptive**, not phone-only: the same instance keeps the familiar side
drawer on wide (desktop) viewports and shows the bottom bar on phone-sized ones
(decided on viewport width, so it also appears in a narrow desktop window — handy
for testing). An app whose primary device is a phone still looks right on a
laptop.

Because it extends `MainLayout`, **the whole menu model carries over
unchanged**: `@MenuItem`/`@Menu` icons and ordering, grouping, `checkAccess`
access control and `getMenuText` i18n all work exactly as
[documented for `MainLayout`](index.html).

### Basic usage

Extend the layout, implement the drawer header, and annotate the views as
usual:

```java
public class MobileLayout extends MobileMainLayout {
    @Override
    protected Object getDrawerHeader() {
        return "My App";
    }
}
```

```java
@Route(value = "", layout = MobileLayout.class)
@MenuItem(title = "Home", icon = VaadinIcon.HOME, order = 0)
public class HomeView extends VerticalLayout { ... }

@Route(value = "profile", layout = MobileLayout.class)
@MenuItem(title = "Profile", icon = VaadinIcon.USER)
public class ProfileView extends VerticalLayout { ... }
```

Each top-level view becomes a bottom-bar item — a tappable icon that highlights
when its route is active.

### The "More" overflow and two-level groups

A bottom bar only fits a handful of icons, so at most
`getMaxBottomNavItems()` (**five** by default) are shown. When there are more
top-level views — or any grouped items, which a flat bar cannot display — the
last slot becomes a **"More"** action.

By default "More" opens the full drawer. A **group** item (a view with children
via `@MenuItem(parent = ...)`) instead opens a small **popover** above the bar
listing its children, so a two-level hierarchy stays reachable without the
drawer.

Override the overflow behaviour if you like:

```java
@Override
protected void openOverflow() {
    // e.g. open a custom dialog instead of the drawer
}

@Override
protected String getOverflowText() {
    return "Lisää"; // localise the "More" label
}
```

### Full-bleed views (edge to edge)

A view that should run under the floating bar — a map, a photo, a video — opts
out of the bottom-bar clearance by adding the `EDGE_TO_EDGE` style class:

```java
public class MapView extends Div {
    public MapView() {
        setSizeFull();
        addClassName(MobileMainLayout.EDGE_TO_EDGE);
    }
}
```

The content then fills the screen and the translucent bar simply overlays it.

### Per-view navbar actions

The [`addNavbarHelper(Component)`](index.html) pattern works here too. On a wide
screen the helper sits in the navbar; on a phone it appears at the **top-right of
the content header**, pinned (sticky) so it stays reachable while the content
scrolls under it — the mobile equivalent of the wide layout's top-right actions
(e.g. an options "…" button opening a popover). It shows even when the
scroll-away title is turned off, so a view's actions are always available without
having to enable the title.

```java
@Override
protected void onAttach(AttachEvent e) {
    super.onAttach(e);
    Button options = new Button(VaadinIcon.ELLIPSIS_DOTS_H.create());
    Popover popover = new Popover();
    popover.setTarget(options);
    popover.add(/* the view's options controls */);
    add(popover);
    MainLayout.getCurrent().addNavbarHelper(options);
}
```

### Configuration

All options have sensible defaults; set them in the layout's constructor.

| Method | Default | Effect |
| --- | --- | --- |
| `setBottomNavLabelsVisible(boolean)` | `false` | Show a small text caption under each bottom-bar icon. |
| `setViewTitleVisible(boolean)` | `false` | Show the large, scroll-away view title in the content. A wide desktop always shows the title next to the drawer toggle regardless of this setting. |
| `setMaxBottomNavItems(int)` | `5` | How many icons before the last slot becomes "More". |
| `setBottomNavHideOnScroll(boolean)` | `true` | Slide the bar out of the way when scrolling down, iOS-style. |
| `setBodyScrolling(boolean)` | `true` | Scroll the whole page (lets the mobile browser's address bar collapse and content run edge-to-edge under the status bar) instead of just the content area. |

```java
public class MobileLayout extends MobileMainLayout {
    public MobileLayout() {
        setBottomNavLabelsVisible(true);
        setViewTitleVisible(true);
    }

    @Override
    protected Object getDrawerHeader() {
        return "My App";
    }
}
```

### Theming

The layout is **theme-agnostic** — it styles itself with the shared `--vaadin-*`
base properties, so it looks right under both **Lumo** and **Aura**:

* **Lumo** renders a solid bottom bar separated from the content by a divider.
* **Aura** renders an iOS-style translucent **"liquid glass"** floating pill,
  with the active item marked by a soft lozenge. A subtle background tint keeps
  the icons legible over busy content (e.g. a map), in both light and dark mode.

Safe-area insets are respected throughout, so the bar and the scroll-away title
clear the notch / home indicator when the app's viewport uses
`viewport-fit=cover`.

### How the adaptive switch works

Under the hood the bottom bar is Vaadin `AppLayout`'s touch-optimized navbar.
`MobileMainLayout` shows it whenever the viewport is phone-sized
(`max-width: 800px`) and otherwise lets `AppLayout` show the persistent drawer —
the drawer toggle remains available in the content header on wide screens. You
generally do not need to think about this; it is mentioned only because it
explains why narrowing a desktop browser window flips the layout to the bottom
bar.
