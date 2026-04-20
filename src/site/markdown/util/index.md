## Utilities

Various cross-cutting helpers that don't fit neatly into the other categories.

### VStyle

An extension of Vaadin's `Style` object with support for typed CSS colors.
Can also assign styles to elements not directly bound to a component -- by
Element reference, CSS selector, or by injecting rules into the document head.

### VStyleUtil

Static helpers for common styling tasks such as injecting global CSS rules.

### LumoProps

Java enumeration of Lumo theme CSS custom properties for easier
discoverability, reuse, and redefinition.

### BrowserCookie

Read and write cookies from server-side Java code. Works correctly even when
the application uses web sockets for push.

### BrowserPrompt

Show a native browser `prompt()` dialog and receive the user's input on
the server side.

### WindowScroller

Read and set the browser window scroll position.
