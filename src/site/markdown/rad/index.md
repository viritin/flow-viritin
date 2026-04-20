## RAD Tools

The Rapid Application Development package (`org.vaadin.firitin.rad`) provides
tools for quickly building admin UIs and prototypes with minimal boilerplate.

### AutoForm

`AutoFormContext` generates editable forms for any POJO or Java record.
Fields, labels, and layout are derived automatically from the bean's properties
using Jackson introspection.

```java
AutoFormContext ctx = new AutoFormContext();
AutoForm<Person> form = ctx.createForm(person);
form.setSaveHandler(p -> personService.save(p));
form.openInDialog();
```

Features:

* Automatic field type selection (text field, date picker, checkbox, etc.).
* Bean Validation support out of the box.
* Custom property editors via `withPropertyEditor()`.
* Custom label rendering via `withPropertyHeaderPrinter()`.
* i18n support via `withTranslationProvider()`.

### DtoDisplay / PrettyPrinter

`PrettyPrinter` renders any Java object as a read-only Vaadin component tree.
Useful for debugging, admin panels, or quick data visualisation.

```java
add(PrettyPrinter.toVaadin(myObject));
```
