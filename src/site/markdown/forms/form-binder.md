## FormBinder

`FormBinder` connects a domain object to the fields that edit it. It is a fresh
take on the Vaadin core `Binder`, built around three decisions:

* **Binding is by name.** A property called `firstName` is bound to a field
  called `firstName`. There is no per-field configuration to write.
* **Only non-buffered mode.** Changes go into the object as they are made, so
  validation logic — including cross-field logic — can be written against the
  object itself.
* **Records are first class.** An immutable value is as bindable as a bean.

Validation is deliberately *not* this class's job. It accepts the results of
validation from wherever you do it, in the Bean Validation form or as a plain
map. If you would rather have that arranged for you, see
[BeanValidationForm](bean-validation-form.html).

### Binding

Give the binder the type and the component holding the fields:

```java
public class PersonForm extends VerticalLayout {
    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");
    IntegerField age = new IntegerField("Age");
}

PersonForm form = new PersonForm();
FormBinder<Person> binder = new FormBinder<>(Person.class, form);
binder.setValue(person);
```

The fields are found by reflecting over the component's own declared fields and
those of the classes it extends, so a base form class carrying the fields common
to several forms works as you would expect. A field whose name matches no
property is simply not bound — if a value is not appearing, that is the first
thing to check.

You can also hand the editors over explicitly, which is useful when they are not
fields of one component:

```java
FormBinder<Person> binder = new FormBinder<>(Person.class,
        Map.of("firstName", firstName, "age", age));
```

### Records

A record is bound the same way, and a new instance is constructed whenever the
value is read. Only some of its components need an editor:

```java
record CounterEdit(long id, String comment, Double target) {}

class CounterRow extends VerticalLayout {
    TextField comment = new TextField();
    NumberField target = new NumberField();
}
```

`id` has no field and needs none — it is part of the value, not something the
reader edits. The binder keeps what it was given for components nothing edits,
so the record that comes out is the record that went in, with the edited parts
replaced. The one case it cannot do this for is a primitive component with
nothing set to read from, which fails with a message naming the component.

### Constraints reach the fields

Bean Validation constraints are read while binding, and the ones a field can
enforce for itself are handed over to it:

| Constraint | On the field |
|---|---|
| `@NotNull`, `@NotEmpty`, `@NotBlank` | required indicator — and a required field refuses to be emptied |
| `@Size` | `setMaxLength` / `setMinLength` |
| `@Min`, `@Max`, inclusive `@DecimalMin` / `@DecimalMax` | `setMin` / `setMax` |

So this is all it takes for the field to stop the reader at 64 characters:

```java
record Counter(@Size(max = 64) String comment) {}
```

Three things are deliberately left out, and all of them are still checked where
they always were:

* **Strict bounds.** `@Positive` means "greater than zero" while a field's
  minimum is inclusive, and there is no next `double` after zero to use instead.
  A field that allowed zero while the constraint refused it would be a worse lie
  than no limit at all.
* **`@Pattern`.** The expression would be handed to the browser, and a Java
  regular expression is not a JavaScript one.
* **Constraints belonging to a validation group.** Which groups are active is
  decided after binding, and a limit cannot be un-set per group.

A limit you set yourself is never overwritten. You knew something the annotation
did not.

### Validation

The binder shows violations; it does not produce them. Hand it the result:

```java
binder.addValueChangeListener(e -> {
    Set<ConstraintViolation<Person>> violations = validator.validate(binder.getValue());
    binder.setConstraintViolations(violations);
});
```

Violations that name a bound property are shown on that field. Violations that
name none — a class level constraint, the cross-field case — go to a component
of your choosing:

```java
binder.setClassLevelViolationDisplay(someLayout);
```

Without Bean Validation on the classpath, the same in plain strings:

```java
binder.setRawConstraintViolations(Map.of(
        "age", "Has to be a positive number",
        "", "The dates overlap"));   // the empty key: belongs to no field
```

### Reacting to usable changes

A form that saves as the reader types has two conditions to check on every event:
that the change came from the client rather than from your own code filling the
fields, and that nothing is currently reported as wrong. Both in one place:

```java
binder.addValidValueChangeListener(e -> service.save(binder.getValue()));
```

"Wrong" means what the binder has been told — a conversion error it noticed
itself, and the violations last handed to `setConstraintViolations`. If you feed
those from a listener of your own, add that listener first, or this one answers
about the change before last. With
[BeanValidationForm](bean-validation-form.html) there is no such order to get
right, and `setEagerSavedHandler` says the same thing in one line.

### Converters

When a field's type differs from the property's, register a converter by
property name:

```java
binder.setConverter("birthDate", new StringToDateConverter());
```

Input that cannot be converted marks the field and is reported by
`hasInputConversionErrors()` and `getInputConversionErrors()`.

### The value

`getValue()` never returns null. For a record it builds a new instance from the
editors on every call. For a mutable bean it returns the object that was set —
the same object every time, with the changes written into it. When nothing has
been set it builds one on the first ask and keeps it, so a caller may hold on to
what it was given.

`setValue(null)` empties the form: every editor is cleared to its own empty
value and the validation errors go with it. `clear()` does the same, being
defined as exactly that.

`isEmpty()` asks the editors, rather than comparing the value with an empty one
— an empty value cannot always be built, and for a bean it could not be
recognised. It answers about the form, not about the value: a component nothing
edits leaves the form empty while the value still carries it.

### Value change mode

Fields that have one are set to `LAZY`, so that a binder does not validate on
every keystroke. A mode you set before binding is kept:

```java
comment.setValueChangeMode(ValueChangeMode.EAGER);   // survives binding
```

### A value inside a value

The binder matches simple property names, so a nested value is not bound by
reaching into it. Edit it as one field instead — a `CustomField`, which can use
a `FormBinder` of its own inside:

```java
public class BandsField extends CustomField<Bands> {

    NumberField low = new NumberField();
    NumberField high = new NumberField();

    private final FormBinder<Bands> binder;

    public BandsField() {
        super(null, true);   // manualValueUpdate: this composite decides when it changed
        add(low, high);
        binder = new FormBinder<>(Bands.class, this);
        binder.addValueChangeListener(e -> {
            if (e.isFromClient()) {
                updateValue();
            }
        });
    }

    @Override
    protected Bands generateModelValue() {
        return binder.getValue();
    }

    @Override
    protected void setPresentationValue(Bands bands) {
        binder.setValue(bands);
    }
}
```

The outer form then has a field named after the property, like any other:

```java
record Settings(String name, @Valid Bands bands) {}

class SettingsForm extends VerticalLayout {
    TextField name = new TextField();
    BandsField bands = new BandsField();
}
```

Two details in that constructor are worth the words they cost. `manualValueUpdate`
turns off the automatic update, which a `CustomField` otherwise does from a DOM
`change` event — meaning it never happens in a browserless test, and never
happens at all for an inner field that does not directly change the logical value
(a search box, a picker). Driving it from the binder instead keeps the whole path
on the server. The `isFromClient` guard is what stops filling the fields
programmatically from reporting itself as a change the reader made.

A `FormBinder` cannot be a field of another form: it binds fields, it is not one.
Trying says so.

Constraints on the nested type reach the inner fields through the inner binder,
and a class level violation on it is reported on the `CustomField` itself, since
that is the thing it is about.
