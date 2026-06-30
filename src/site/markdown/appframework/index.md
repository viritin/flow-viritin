## Application Layout

Flow Viritin builds a small, opinionated application-framework layer on top of
Vaadin's [`AppLayout`](https://vaadin.com/docs/latest/components/app-layout).
Instead of wiring a navbar, a drawer and a navigation menu by hand for every
project, you extend one base class, annotate your views, and the layout — menu,
view title, access control and all — is built for you.

There are three layers, each adding more on top of the previous one:

* **`VAppLayout`** — thin helpers and defaults over `AppLayout`.
* **`MainLayout`** — adds an automatically populated navigation drawer.
* **[`MobileMainLayout`](mobile-main-layout.html)** — an adaptive, phone-style
  variant with a bottom navigation bar.

### MainLayout: a menu with zero plumbing

`MainLayout` is the usual starting point. Extend it and implement a single
method — the drawer header — and every view that uses the layout shows up in
the navigation drawer automatically:

```java
public class MyLayout extends MainLayout {
    @Override
    protected Object getDrawerHeader() {
        return "My Application"; // a String, or any Component
    }
}
```

```java
@Route(value = "dashboard", layout = MyLayout.class)
public class DashboardView extends VerticalLayout { ... }
```

`MainLayout` scans the route registry for every view bound to it (via the
`layout` attribute or the `@Layout` annotation) and builds the drawer from them.
Routes added or removed at runtime — including dev-mode hot reload — update the
menu automatically.

### Configuring menu entries

By default the caption comes from the view's class name and the icon is a
generic file icon. The `@MenuItem` annotation overrides that per view:

```java
@Route(value = "dashboard", layout = MyLayout.class)
@MenuItem(title = "Dashboard", icon = VaadinIcon.DASHBOARD, order = 100)
public class DashboardView extends VerticalLayout { ... }
```

`@MenuItem` supports:

* `title` — menu caption (defaults to the class-name-derived text).
* `icon` / `iconUrl` — a `VaadinIcon`, or a URL to a custom `Icon`/`SvgIcon`.
* `order` — sort order (`BEGINNING`, `DEFAULT`, `END`, or any int).
* `hidden` — keep the route routable but leave it out of the menu.
* `enabled` — show the item disabled (does not block deep-link navigation).
* `parent` — nest this view under another menu item to form a **group**.

Vaadin's own `@Menu` annotation is recognised too, so views written for the
platform's menu work without changes.

### Grouping (sub-menus)

Point several views at the same `parent` to get a collapsible group in the
drawer:

```java
@Route(value = "users", layout = MyLayout.class)
@MenuItem(title = "Users", icon = VaadinIcon.USERS, parent = AdminGroup.class)
public class UsersView extends VerticalLayout { ... }
```

The parent can be a marker class (a `SubMenu`) or another view. `openByDefault`
and `collapsible` on the parent's `@MenuItem` tune how the group behaves.

### Access control

Override `checkAccess(NavigationItem)` to hide entries the current user may not
see. It runs while the menu is built, so it is the single place to gate the
whole navigation:

```java
@Override
protected boolean checkAccess(NavigationItem item) {
    return securityService.hasAccess(item.getNavigationTarget());
}
```

(Hiding a menu item does not block direct navigation to its URL — enforce that
with Vaadin's `BeforeEnter` / security mechanisms as usual.)

### Internationalisation

Override `getMenuText(Class<?> target, String defaultText)` to translate
captions. The default returns the annotation/class-name text; return your
localized string instead and the drawer, the view title and (in
`MobileMainLayout`) the bottom bar all follow.

### View title and per-view navbar components

The current view's title is shown in the navbar and kept in sync on
navigation. Override it manually with `setViewTitle(String)`.

`addNavbarHelper(Component)` places a view-specific component (a search field, an
action button…) at the right edge of the navbar. Helpers are cleared
automatically when you navigate away, so each view manages only its own.

### Sub-views (a lightweight view stack)

`openSubView(component, title)` pushes a component "on top" of the current view
with a breadcrumb-style title, and `closeSubView()` pops back. Handy for
master–detail flows that should not get their own route.

### Going mobile

For phone-first applications, [`MobileMainLayout`](mobile-main-layout.html)
swaps the side drawer for a native-style bottom navigation bar while keeping the
exact same menu model, annotations and access control described above. On wide
screens it falls back to the regular drawer, so one layout serves both.
