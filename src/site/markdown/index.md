## Flow Viritin

Flow Viritin is a general purpose helper library for [Vaadin](https://vaadin.com/) developers.
It provides Java-only additions that don't require a custom client-side bundle, making
it easy to drop in to any Vaadin project.

The library covers several areas:

* **[Application Layout](appframework/index.html)** -- AppLayout helpers with an auto-built navigation menu, plus the adaptive, phone-style MobileMainLayout.
* **[Components](components/index.html)** -- Fluent API wrappers for Vaadin core components, VGrid, Tree, uploads/downloads, and more.
* **[Browser APIs](browser-api/index.html)** -- Java APIs for browser capabilities like ResizeObserver, Geolocation, Web Notifications, Share, Fullscreen, and more.
* **[Forms & Binding](forms/index.html)** -- FormBinder, BeanValidationForm, and field components for productive form building.
* **[RAD Tools](rad/index.html)** -- Automatic form generation and DTO pretty-printing for rapid prototyping.
* **[Utilities](util/index.html)** -- Helpers for cookies, styles, page visibility, web storage, and other cross-cutting concerns.

### Coordinates

```xml
<dependency>
    <groupId>in.virit</groupId>
    <artifactId>viritin</artifactId>
    <version>LATEST</version>
</dependency>
```

### Version compatibility

| Viritin | Vaadin |
|---------|--------|
| 3.x     | 25.0+  |
| 2.16+   | 24.8+  |
| 2.x     | 24     |
| 1.7.1+  | 23.3+  |

### Source code & issues

The project is hosted on GitHub: <https://github.com/viritin/flow-viritin>

The [test source directory](https://github.com/viritin/flow-viritin/tree/v25/src/test/java/org/vaadin/firitin)
contains runnable example views for most features.
