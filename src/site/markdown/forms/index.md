## Forms & Binding

Flow Viritin provides several approaches to form building, from low-level
binding to fully automatic form generation.

### FormBinder

`FormBinder` connects domain objects (POJOs or Java records) to UI fields
using **name-based binding**. If your property is `firstName`, FormBinder looks
for a field named `firstName` in the container objects you provide.

Key features:

* Supports both POJOs and Java `record` types.
* Encourages writing validation logic separately from UI components.
* Displays constraint violations in fields and at class level.
* Accepts validation messages from Bean Validation API or as a raw
  `Map<String, String>`.
* Supports bean level validators (aka cross-field validators)

### BeanValidationForm

An opinionated abstract superclass for "bulk forms" built on top of
`FormBinder`. Provides:

* Automatic Bean Validation with customizable validation groups.
* Basic layout with save, cancel and delete buttons (shown if related handler is defined).
* Shows constraint violations on the related field (if relation is available), like in Vaadin, bean level violations (typically cross-field valitors) on in an area on top of the save/cancel buttons.
* Save/cancel buttons that adjust their enabled state based on user actions.
* Ability to show in a dialog (handy for simple CRUDs on desktop UIs)

### AbstractForm

The older form solution based on the Vaadin core `Binder`. Still available
for backward compatibility but no longer actively developed.

### Field components

* **CommaSeparatedStringField** -- Edit `List<String>` with Binder.
* **SubListSelector** -- Pick a `List<T>` from a larger set.
* **EnumSelect** -- ComboBox pre-filled with enum constants.
* **ElementCollectionField** -- Edit `List<Address>`-style collections.
* **IndeterminateCheckbox** -- Tri-state checkbox for nullable `Boolean`.
* **Selection API** -- `selectAll()`, `getCursorPosition()`,
  `setSelection()` and more for text inputs.
