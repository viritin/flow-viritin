## BeanValidationForm

`BeanValidationForm` is an opinionated form built on
[FormBinder](form-binder.html). Where the binder shows the violations you hand
it, this class produces them: it validates the entity with the Bean Validation
API on every change and when the entity is set, and moves the save button
accordingly.

What it adds:

* Bean Validation, with validation groups if you want them.
* Save, cancel and delete buttons, each appearing only when a handler for it is
  set, and enabling themselves according to the state of the form.
* A place for the violations that belong to no single field.
* Opening in a dialog, which is often all a simple CRUD needs.

```java
public class PersonForm extends BeanValidationForm<Person> {

    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");
    IntegerField age = new IntegerField("Age");

    public PersonForm() {
        super(Person.class);
        setSavedHandler(person -> service.save(person));
    }

    @Override
    protected List<Component> getFormComponents() {
        return List.of(firstName, lastName, age);
    }
}
```

```java
PersonForm form = new PersonForm();
form.setEntity(person);
```

### The layout

The default content is the fields from `getFormComponents()` in a
`FormLayout`, the class level violations under them and the toolbar at the
bottom. When that is not the form you want, override `createContent()` and build
it yourself — `getSaveButton()`, `getResetButton()`, `getDeleteButton()` and
`getClassLevelViolationsDisplay()` are all yours to place. `getFormComponents()`
is then unused; return an empty list.

If your layout does not take the class level violation display, the form appends
it, so a violation that belongs to no field is never lost silently.

### The entity, and the save button

`setEntity(entity)` binds and validates in one go. An entity that fails
validation therefore does not enable saving, whatever else happens.

The save button is enabled when the form has both changes and validity. When you
are handing over something already valid and want it saveable as it stands, say
so:

```java
form.setEntityWithEnabledSave(person);
```

`setEntity(null)` unbinds: the fields are emptied and the form hides itself.

A fresh, empty entity does not turn the form red — a missing value is not
reported for a field the reader has not touched yet, which is what the required
indicator is there for.

### Class level constraints

The reason to reach for this class rather than the core `Binder`. A constraint
on the type is checked like any other, and its message is shown in the display
described above:

```java
@ValidPassengerCount
public class Car {
    private int seatCount;
    private List<Person> passengers;
    // ...
}
```

The form is then honest from the moment it opens: given a car with three
passengers and two seats, it says so and refuses to offer Save, without waiting
for the reader to touch a field.

### Validation groups

```java
form.setValidationGroups(Draft.class);
```

Affects both what is validated and which fields show the required indicator.

### Saving without a save button

Sometimes a form is a row in a list, or a panel of settings, and a save button
of its own would be one button too many. Say that instead of a saved handler:

```java
setEagerSavedHandler(counter -> service.save(counter));
```

The handler is called after every change the reader makes that leaves the form
valid. A change that does not is shown on the field and not saved, so what is
stored keeps what it had — which is the part that is easy to get wrong when
saving on every event. No button appears, and `setSavedHandler` is not needed.

`EagerSaveView` in the test sources is a working example: a list of rows, each
saving itself as you type.

### Spring: use the container's validator

By default the form builds a validator from the default Bean Validation
provider. That is enough while your constraints are self-contained, and stops
being enough as soon as a `ConstraintValidator` needs something from the
application — a repository to ask whether an identifier is still free, a registry
of what exists. A validator built by the default provider is instantiated by
reflection, so its injection points stay null and the check fails with
`HV000028` instead of answering. Message templates come from
`ValidationMessages.properties` rather than from your application's own message
source, for the same reason.

Spring's own validator solves both, and handing it to the form is two lines:

```java
public class PersonForm extends BeanValidationForm<Person> {

    private final Validator validator;

    public PersonForm(Validator validator) {   // jakarta.validation.Validator
        super(Person.class);
        this.validator = validator;
    }

    @Override
    protected Validator getValidator() {
        return validator;
    }
}
```

With that, a constraint validator is a bean like any other:

```java
public class KnownSensorValidator implements ConstraintValidator<KnownSensor, String> {

    @Autowired
    private SensorRegistry registry;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || registry.knows(value);
    }
}
```

There is deliberately no Spring aware version of this class, and no dependency on
Spring: one overridable method costs less than a second artifact to keep in step.
`SpringManagedValidatorTest` in the test sources is the whole example, including
what the failure looks like without it.

### AbstractForm

The older form solution, built on the Vaadin core `Binder`. Still there for
backward compatibility, but `BeanValidationForm` is where the work goes — it is
the one that handles cross-field validation and validation groups properly.
