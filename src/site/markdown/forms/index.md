## Forms &amp; Binding

Flow Viritin provides several approaches to form building, from low-level
binding to fully automatic form generation.

### FormBinder

`FormBinder` connects domain objects — POJOs or Java `record`s — to UI fields
using **name-based binding**: a property called `firstName` is bound to a field
called `firstName`, with no per-field configuration to write. It works
non-buffered, so validation logic can be written against the object itself,
including logic that spans several of its properties.

It shows violations rather than producing them, and takes them from the Bean
Validation API or as a plain `Map<String, String>`. Constraints that a field can
enforce for itself — a maximum length, a minimum value, a required value — are
handed over to the field while binding.

[Read more about FormBinder](form-binder.html), including records, nested values
and what the binder does with a constraint.

### BeanValidationForm

An opinionated form built on `FormBinder`, for the ordinary case where the
validation is Bean Validation and the form has a save button. It validates on
every change and when the entity is set, keeps the save, cancel and delete
buttons in a state that matches, and gives the violations that belong to no
single field — cross-field constraints — a place of their own. It can open
itself in a dialog, which is often all a simple CRUD needs.

[Read more about BeanValidationForm](bean-validation-form.html), including
class level constraints, validation groups and using Spring's validator.

### AbstractForm

The older form solution based on the Vaadin core `Binder`. Still available for
backward compatibility but no longer actively developed.

### Field components

* **CommaSeparatedStringField** -- Edit `List<String>` with Binder.
* **SubListSelector** -- Pick a `List<T>` from a larger set.
* **EnumSelect** -- ComboBox pre-filled with enum constants.
* **ElementCollectionField** -- Edit `List<Address>`-style collections.
* **IndeterminateCheckbox** -- Tri-state checkbox for nullable `Boolean`.
* **Selection API** -- `selectAll()`, `getCursorPosition()`,
  `setSelection()` and more for text inputs.
