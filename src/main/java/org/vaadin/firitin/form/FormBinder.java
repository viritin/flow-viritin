package org.vaadin.firitin.form;

import com.googlecode.gentyref.GenericTypeReflector;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.shared.HasValidationProperties;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.DefaultConverterFactory;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.shared.Registration;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.vaadin.firitin.util.JacksonIntrospection;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.introspect.AnnotatedConstructor;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.BasicBeanDescription;
import tools.jackson.databind.introspect.BeanPropertyDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A new start for the {@link com.vaadin.flow.data.binder.Binder}.
 * Note, that this is still a fairly new class, so there might be API changes to it.
 * <p>
 * Design principles:
 * <ul>
 * <li> Only support "non-buffered mode" so that validation logic can use the
 * bean/record + simplicity of the implementation</li>
 * <li> Validation is "just validation", and not concern of this class. BUT, API
 * must support binding external validation logic, like Bean Validation API</li>
 * <li> Must support Records and immutable objects as well</li>
 * <li> No requirements for BeanValidation or Spring DataBinding stuff, but
 * optional support (or extensible for those)</li>
 * </ul>
 * Non-goals:
 * <ul>
 * <li> Aiming for binding anything without property names (for good solution
 * this needs to be resolved at language level and supported with thing like
 * Bean Validation first)</li>
 * </ul>
 *
 * @param <T> The class/record type bound by this binder.
 */
public class FormBinder<T> implements HasValue<FormBinderValueChangeEvent<T>, T> {

    // Helper "Jack" to do introspection
    private static final ObjectMapper jack = JacksonIntrospection.getMapper();
    private final Class<T> tClass;
    private final BasicBeanDescription bbd;

    Map<BeanPropertyDefinition, HasValue> bpdToEditorField = new HashMap<>();
    Map<String, HasValue> nameToEditorField = new LinkedHashMap<>();
    Map<String, Converter> nameToConverter = new HashMap<>();
    HashMap<String, String> propertyToInputValueConversionError = new HashMap<>();
    List<Registration> registrations = new ArrayList<>();
    Set<HasValue> userModifiedFields = new HashSet<>();
    private Set<Component> errorMsgs = new HashSet<>();
    private T valueObject;
    private List<ValueChangeListener> valueChangeListeners;
    private boolean constraintViolations;
    private HasComponents classLevelViolationDisplay;
    private boolean ignoreServerOriginatedChanges = true;
    private Class<?>[] validationGroups;
    private SerializableFunction<String, Component> classLevelValidationViolationComponentProvider = new ParagraphWithErrorStyleClassLevelValidationViolationComponentProvider();

    /**
     * Constructs a new binder.
     *
     * @param tClass              the class of the bound entity/bean, set later with {@link #setValue(Object)}
     * @param containerComponents the components whose class contains the fields to bound
     */
    public FormBinder(Class<T> tClass, Component... containerComponents) {
        this.tClass = tClass;
        if (containerComponents[0] instanceof HasComponents hc) {
            classLevelViolationDisplay = hc;
        }
        JavaType javaType = jack.getTypeFactory().constructType(tClass);
        this.bbd = (BasicBeanDescription) jack._deserializationContext().introspectBeanDescription(javaType);;

        for (Component formComponent : containerComponents) {
            for (Field f : editorFields(formComponent.getClass())) {
                // TODO, figure out other naming strategies
                BeanPropertyDefinition property = bbd.findProperty(new PropertyName(f.getName()));

                if (property != null) {
                    property.getAccessor().fixAccess(true);
                    try {
                        f.setAccessible(true);
                        HasValue hasValue = (HasValue) f.get(formComponent);
                        if (isRequired(property)) {
                            hasValue.setRequiredIndicatorVisible(true);
                        }
                        bindProperty(property, hasValue);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /**
     * The fields that can hold an editor, from the given class and the classes it
     * extends.
     * <p>
     * A form built on a base class that already carries some of the fields is an
     * ordinary thing to write, and looking only at the concrete class would leave
     * the inherited ones silently unbound. The walk stops at Vaadin's own classes:
     * beyond that point the fields belong to the framework rather than to the form,
     * and a component's internals are not editors anyone asked to bind.
     * <p>
     * A field hides one of the same name in a base class, exactly as it does in
     * Java.
     *
     * @param formClass the class of the component holding the editors
     * @return the fields, subclass first
     */
    private static List<Field> editorFields(Class<?> formClass) {
        List<Field> fields = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (Class<?> c = formClass; c != null && !isFrameworkClass(c); c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                if (HasValue.class.isAssignableFrom(f.getType()) && seen.add(f.getName())) {
                    fields.add(f);
                }
            }
        }
        return fields;
    }

    private static boolean isFrameworkClass(Class<?> c) {
        return c.getName().startsWith("com.vaadin.");
    }

    /**
     * Constructs a new binder.
     *
     * @param tClass       the class of the bound entity/bean, set later with {@link #setValue(Object)}
     * @param editorObject the editor object that contains the fields to bound, does not need to be a component
     * @deprecated not sure yet if this is a good idea, added for backwards compatibility
     */
    @Deprecated
    public FormBinder(Class<T> tClass, Object editorObject) {
        this.tClass = tClass;
        if (editorObject instanceof HasComponents hc) {
            classLevelViolationDisplay = hc;
        }
        JavaType javaType = jack.getTypeFactory().constructType(tClass);
        this.bbd = (BasicBeanDescription) jack._deserializationContext().introspectBeanDescription(javaType);
        for (Field f : editorFields(editorObject.getClass())) {
            BeanPropertyDefinition property = bbd.findProperty(new PropertyName(f.getName()));

            if (property != null) {
                property.getAccessor().fixAccess(true);
                try {
                    f.setAccessible(true);
                    HasValue hasValue = (HasValue) f.get(editorObject);
                    bindProperty(property, hasValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Binds type to given property editors
     *
     * @param tClass               the type to bind
     * @param propertyNameToEditor pre-instantiated editors to bind
     */
    public FormBinder(Class<T> tClass, Map<String, HasValue> propertyNameToEditor) {
        this.tClass = tClass;
        JavaType javaType = jack.getTypeFactory().constructType(tClass);
        this.bbd = (BasicBeanDescription) jack._deserializationContext().introspectBeanDescription(javaType);;
        for (BeanPropertyDefinition property : bbd.findProperties()) {
            if (propertyNameToEditor.containsKey(property.getName())) {
                HasValue hasValue = propertyNameToEditor.get(property.getName());
                bindProperty(property, hasValue);
            }
        }
    }

    /**
     * Constructs a new binder for semi-manual wiring. Not designed for general use, but can be
     * handy for some special cases.
     */
    public FormBinder(BasicBeanDescription bdd) {
        this.bbd = bdd;
        this.tClass = (Class<T>) bbd.getType().getRawClass();
    }


    /**
     * Binds given dto to the UI fields found from given component(s).
     *
     * @param dto                 the object to bind. The type of the FormBinder will be taken
     *                            from this object.
     * @param containerComponents the components whose class contains the fields to bound
     */
    public FormBinder(T dto, Component... containerComponents) {
        this((Class<T>) dto.getClass(), containerComponents);
        setValue(dto);
    }

    /**
     * Sets the active validation groups. This affects which fields show the
     * required indicator based on their constraint annotations' group membership.
     *
     * @param groups the validation groups to activate
     */
    public void setValidationGroups(Class<?>... groups) {
        this.validationGroups = groups;
        // Re-evaluate required indicators for all bound fields
        bpdToEditorField.forEach((property, hasValue) -> {
            if (!property.getPrimaryType().isPrimitive() || property.getRawPrimaryType() == boolean.class) {
                hasValue.setRequiredIndicatorVisible(isRequired(property));
            }
        });
    }

    public Class<?>[] getValidationGroups() {
        return validationGroups;
    }

    protected boolean isRequired(BeanPropertyDefinition property) {
        return isRequired(property, validationGroups);
    }

    protected static boolean isRequired(BeanPropertyDefinition property, Class<?>[] activeGroups) {
        if (property.getPrimaryType().isPrimitive() && property.getRawPrimaryType() != boolean.class) {
            return true;
        }

        try {
            AnnotatedMember accessor = property.getAccessor();
            if(isRequiredAnnotationActive(accessor, activeGroups)) {
                return true;
            }
            // Now also check field, (e.g. quite typical for JPA entities that only fields are annotated)

            AnnotatedField field = property.getField();
            if(field != null) {
                return isRequiredAnnotationActive(field, activeGroups);
            }
            return false;
        } catch (java.lang.NoClassDefFoundError ex) {
            // No Bean Validation on classpath (or no getter)
            return false;
        }
    }

    private static boolean isRequiredAnnotationActive(AnnotatedMember member, Class<?>[] activeGroups) {
        NotNull notNull = member.getAnnotation(NotNull.class);
        if (notNull != null && isGroupActive(notNull.groups(), activeGroups)) {
            return true;
        }
        NotEmpty notEmpty = member.getAnnotation(NotEmpty.class);
        if (notEmpty != null && isGroupActive(notEmpty.groups(), activeGroups)) {
            return true;
        }
        NotBlank notBlank = member.getAnnotation(NotBlank.class);
        if (notBlank != null && isGroupActive(notBlank.groups(), activeGroups)) {
            return true;
        }
        return false;
    }

    private static boolean isGroupActive(Class<?>[] annotationGroups, Class<?>[] activeGroups) {
        if (activeGroups == null || activeGroups.length == 0) {
            // No specific groups configured: only default group is active,
            // so annotation must belong to default group (empty groups array)
            return annotationGroups.length == 0;
        }
        if (annotationGroups.length == 0) {
            // Annotation belongs to default group; check if default group is active
            for (Class<?> active : activeGroups) {
                if (active == jakarta.validation.groups.Default.class) {
                    return true;
                }
            }
            return false;
        }
        // Check if any of the annotation's groups match the active groups
        for (Class<?> ag : annotationGroups) {
            for (Class<?> active : activeGroups) {
                if (active == ag) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isReadOnly(BeanPropertyDefinition property) {
        if(!property.hasSetter()) {
            return !this.bbd.isRecordType();
        }
        return false;
    }

    /**
     * Binds given property to the given editor field.
     *
     * @param property the property to bind
     * @param hasValue the editor field to bind
     */
    public void bindProperty(BeanPropertyDefinition property, HasValue hasValue) {
        if (isRequired(property)) {
            hasValue.setRequiredIndicatorVisible(true);
        }
        hasValue.setReadOnly(isReadOnly(property));
        bpdToEditorField.put(property, hasValue);
        nameToEditorField.put(property.getName(), hasValue);
        configureEditor(property, hasValue);
    }

    protected void configureEditor(BeanPropertyDefinition property, HasValue hasValue) {

        if (hasValue instanceof HasValueChangeMode hvcm) {
            hvcm.setValueChangeMode(ValueChangeMode.LAZY);
        }
        if (!isImmutable()) {
            ValueContext ctx = fakeValueContext(hasValue);
            // Mutate
            registrations.add(hasValue.addValueChangeListener(e -> {
                boolean dropServerOriginateEvent = !e.isFromClient() && ignoreServerOriginatedChanges;
                // No value object to write into: the form has been emptied with
                // setValue(null), and getValue() will construct a new one.
                if (!dropServerOriginateEvent && valueObject != null) {
                    Object value = e.getValue();
                    value = convertInputValue(value, property, ctx);
                    try {
                        property.getSetter().callOnWith(valueObject, value);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }));
        }
        registrations.add(hasValue.addValueChangeListener(e -> {
            if(e.isFromClient()) {
                // mark field has been modified
                userModifiedFields.add(hasValue);
            }
            if (valueChangeListeners != null) {
                var event = new FormBinderValueChangeEvent<T>(FormBinder.this, e.isFromClient());
                for (ValueChangeListener vcl : valueChangeListeners.toArray(new ValueChangeListener[0])) {
                    vcl.valueChanged(event);
                }
            }
        }));
    }

    private Object convertInputValue(Object value, BeanPropertyDefinition property, ValueContext ctx) {
        Converter converter = nameToConverter.get(property.getName());

        if (converter != null) {
            Result result = converter.convertToModel(value, ctx);
            try {
                value = result.getOrThrow(msg -> new RuntimeException(msg.toString()));
                // clear possible previous errors
                propertyToInputValueConversionError.remove(property.getName());
            } catch (Throwable ex) {
                value = handleInputConversionError(property, ctx, ex.getMessage());
            }
        } else {
            if(value != null) {
                Class<?> presentationValueClass = value.getClass();
                if(property.getPrimaryType().isPrimitive() && value != null) {
                    // unboxing probably works here
                    return value;
                }
                if (!property.getPrimaryType().isTypeOrSuperTypeOf(presentationValueClass)) {
                    // Go and check Vaadin's default converters
                    converter = DefaultConverterFactory.INSTANCE.newInstance(
                            presentationValueClass, property.getPrimaryType().getRawClass()
                    ).orElseThrow(() -> new RuntimeException("No converter found for for " + presentationValueClass + " -> " + property.getPrimaryType()));
                    try {
                        value = converter.convertToModel(value, ctx).getOrThrow(em -> new IllegalArgumentException("Conversion failed" + em));
                    } catch (Throwable e) {
                        throw new RuntimeException("Conversion failed for " + property.getPrimaryType().getRawClass().getName());
                    }
                }
            }

        }
        return value;
    }

    /**
     * Handles input conversion error. By default, the error message saved and
     * set to the field.
     *
     * @param property
     * @param ctx
     * @param conversionErrorMsg
     * @return the value to be set to the edited object, null by default
     */
    protected Object handleInputConversionError(BeanPropertyDefinition property, ValueContext ctx, String conversionErrorMsg) {
        propertyToInputValueConversionError.put(property.getName(), conversionErrorMsg);
        // TODO figure out if it would be best to handle this somehow else,
        // now at least calling setConstraintViolations clears this easily
        Component component = ctx.getComponent().get();
        if (component instanceof HasValidationProperties f) {
            f.setErrorMessage(conversionErrorMsg);
            f.setInvalid(true);
        } else {
            // TODO show the conversion error somehow in UI/binder API if not of type HasValidationProperties
        }
        // The value of field should be null in this case !? or should get
        // the empty value via field and then convert again 🤷
        return null;
    }

    @Override
    public T getValue() {
        if (isImmutable()) {
            return constructRecord();
        } else {
            if (valueObject == null) {
                return constructPojo();
            }
        }
        return valueObject;
    }

    /**
     * Sets the value object bound to this form.
     * <p>
     * A {@code null} value is accepted and empties the form: every bound editor is
     * cleared to its own empty value and validation errors are removed. This is the
     * empty value of a binder, so {@link #clear()}, which the {@link HasValue}
     * contract defines as {@code setValue(getEmptyValue())}, does the same thing.
     * It also matches {@code Binder.setBean(null)} in Vaadin's own binder.
     * <p>
     * Note that {@link #getValue()} does not return null afterwards: with no value
     * object bound it constructs one from the current editor values, which is what
     * makes a binder usable for creating a new object as well as for editing one.
     *
     * @param valueObject the new value, or null to empty the form
     */
    @Override
    public void setValue(T valueObject) {
        userModifiedFields.clear();
        this.valueObject = valueObject;
        if (valueObject == null) {
            /*
               Nothing to read properties from. Clearing rather than leaving the
               editors as they were: the alternative is a form showing the values of
               an object it is no longer bound to, and the errors that came with it.
            */
            nameToEditorField.values().forEach(HasValue::clear);
            clearValidationErrors();
            return;
        }
        for (BeanPropertyDefinition pd : bbd.findProperties()) {
            HasValue hasValue = bpdToEditorField.get(pd);
            if (hasValue != null) {
                Object pValue = pd.getAccessor().getValue(valueObject);

                if (pValue == null) {
                    hasValue.clear();
                } else {
                    Converter converter = nameToConverter.get(pd.getName());
                    if (converter != null) {
                        pValue = converter.convertToPresentation(pValue, fakeValueContext(hasValue));
                    }
                    try {
                        hasValue.setValue(pValue);
                    } catch (ClassCastException ex) {
                        // Try checking out still the Vaadin's default conversions
                        try {
                            Type fieldValueType = GenericTypeReflector.getTypeParameter(
                                    hasValue.getClass(),
                                    HasValue.class.getTypeParameters()[1]);
                            Class<?> fieldValueClazz = GenericTypeReflector.erase(fieldValueType);
                            converter = DefaultConverterFactory.INSTANCE.newInstance(fieldValueClazz, pd.getPrimaryType().getRawClass())
                                    .orElseThrow(() -> new RuntimeException("No converter found for " + pd.getPrimaryType().getRawClass().getName()));
                            Object converted = converter.convertToPresentation(pValue, fakeValueContext(hasValue));
                            hasValue.setValue(converted);
                        } catch (Exception e) {
                            new RuntimeException("Conversion failed for " + pd.getPrimaryType().getRawClass().getName(), e);
                        }
                    }
                }
            } else {
                // TODO should probably remove this for non-records and throw exception for records??
                Logger.getLogger(FormBinder.class.getName()).log(Level.INFO, "No editor field for property " + pd.getName());
            }

        }
    }

    public FormBinder<T> withValue(T value) {
        setValue(value);
        return this;
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<? super FormBinderValueChangeEvent<T>> listener) {
        if (valueChangeListeners == null) {
            valueChangeListeners = new ArrayList<>();
        }
        valueChangeListeners.add(listener);
        return () -> valueChangeListeners.remove(listener);
    }

    @Override
    public boolean isReadOnly() {
        // TODO figure out what to do, coming via HasValue...
        return false;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        // TODO figure out what to do, coming via HasValue...
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        // TODO figure out what to do, coming via HasValue...
        return false;
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        throw new RuntimeException("Not supported");
        // TODO figure out if this should throw or stop using HasValue altogether
        // Passing to fields is simply wrong and we don't have a place to show the
    }

    // TODO figure out if opening this to public (and having explicit field)
    // makes sense. Would allow immutable POJOs (like those by immutable fanboys
    // that then force to use build patter et friends) to be bound as well, but would
    // force developer probably to provide custom constructor logic
    protected boolean isImmutable() {
        return bbd.isRecordType();
    }

    protected T constructRecord() {
        List<AnnotatedConstructor> constructors = bbd.getConstructors();
        // The default constructor is the last one with Jackson utils
        AnnotatedConstructor annotatedConstructor = constructors.get(constructors.size() - 1);
        List<BeanPropertyDefinition> properties = bbd.findProperties();
        Object[] args = new Object[annotatedConstructor.getParameterCount()];
        for (int i = 0; i < annotatedConstructor.getParameterCount(); i++) {
            BeanPropertyDefinition definition = properties.get(i);
            HasValue hasValue = bpdToEditorField.get(definition);
            Object value = hasValue.getValue();
            value = convertInputValue(value, definition, fakeValueContext(hasValue));
            args[i] = value;
            Class<?> rawType = definition.getGetter().getRawType();
            boolean primitive = definition.getGetter().getRawType().isPrimitive();
            if (primitive && hasValue.isRequiredIndicatorVisible() && args[i] == null) {
                throw new NullPointerException("Can't construct " + bbd.getType().getRawClass().getName() + ", parameter value " + i + " is null!");
            }
        }
        try {
            annotatedConstructor.fixAccess(true);
            return (T) annotatedConstructor.call(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected T constructPojo() {
        T o = (T) bbd.instantiateBean(true);
        bpdToEditorField.forEach((bpd, hasValue) -> {
            try {
                Object value = hasValue.getValue();
                value = convertInputValue(value, bpd, fakeValueContext(hasValue));
                bpd.getSetter().callOnWith(o, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return o;
    }

    private static final Set<String> emptyValidationMessages = Set.of(
            "{jakarta.validation.constraints.NotNull.message}",
            "{jakarta.validation.constraints.NotEmpty.message}",
            "{jakarta.validation.constraints.NotBlank.message}"
            );

    /**
     * Set the constraint violations found during validation. If violation is
     * bound to a bound property, it is shown next to the field, otherwise
     * shown at "form level", see {@link #setClassLevelViolationDisplay(HasComponents)}.
     *
     * @param violations the constraint violations that should be shown in the UI
     */
    public void setConstraintViolations(Set<ConstraintViolation<T>> violations) {
        clearValidationErrors();
        HashSet<ConstraintViolation<T>> nonReported = new HashSet<>(violations);
        violations.forEach(cv -> {
            String property = cv.getPropertyPath().toString();
            if (!property.isEmpty()) {
                HasValue hasValue = nameToEditorField.get(property);

                if (hasValue instanceof HasValidationProperties hvp) {
                    nonReported.remove(cv);
                    if(ignoreRequiredConstraintForField(cv, hasValue)) {
                        // {jakarta.validation.constraints.NotNull.message}
                        // user has not modified this fied yet, don't report, expect the required indicator (*) to be enough
                    } else {
                        hvp.setInvalid(true);
                        hvp.setErrorMessage(cv.getMessage());
                    }
                }
            }
        });
        handleClassLevelValidations(nonReported);
        constraintViolations = !violations.isEmpty();
    }

    /**
     * By default, if a validation is "required" validation for a field that user has not touched, it is not reported
     * in the UI, but is taken otherwise into accound.
     *
     * @param cv validation constraing
     * @param hasValue the field to be tested
     * @return true if should be ignored based on emptry validity
     */
    protected boolean ignoreRequiredConstraintForField(ConstraintViolation<T> cv, HasValue hasValue) {
        return emptyValidationMessages.contains(cv.getMessageTemplate()) && !userModifiedFields.contains(hasValue);
    }

    /**
     * Gets the class level violation display. If not set, the first container
     * component is used if of appropriate type.
     *
     * @return the component where constraint violations will be displayed
     */
    public HasComponents getClassLevelViolationDisplay() {
        if (classLevelViolationDisplay != null) {
            return classLevelViolationDisplay;
        }
        throw new RuntimeException("No place to report class level violations");
    }

    /**
     * Sets the container component where "class level" constraint violations
     * are displayed.
     *
     * @param display the component where the "class level" constraint violations will be displayed.
     */
    public void setClassLevelViolationDisplay(HasComponents display) {
        classLevelViolationDisplay = display;
    }

    protected void handleClassLevelValidations(Set<ConstraintViolation<T>> violations) {
        for (ConstraintViolation cv : violations) {
            String propertyPath = cv.getPropertyPath().toString();
            addClassLevelValidationViolation(
                    createClassLevelValidationComponent(
                            propertyPath.isEmpty() ? cv.getMessage() : propertyPath + " " + cv.getMessage()
                    )
            );
        }
    }

    public void setClassLevelValidationViolationComponentProvider(SerializableFunction<String, Component> classLevelValidationViolationComponentProvider) {
        this.classLevelValidationViolationComponentProvider = Objects.requireNonNull(classLevelValidationViolationComponentProvider);
    }

    /**
     * An alternative API to report constraint violations without BeanValidation API
     * on the classpath.
     *
     * @param propertyToViolation
     */
    public void setRawConstraintViolations(Map<String, String> propertyToViolation) {
        clearValidationErrors();
        HashMap<String, String> nonReported = new HashMap<>();
        nonReported.putAll(propertyToViolation);
        propertyToViolation.forEach((property, msg) -> {
            if (!property.isEmpty()) {
                HasValue hasValue = nameToEditorField.get(property);
                if (hasValue instanceof HasValidationProperties hvp) {
                    hvp.setInvalid(true);
                    hvp.setErrorMessage(msg);
                    nonReported.remove(property);
                }
            }
        });
        handleClassLevelValidations(nonReported);
        constraintViolations = !propertyToViolation.isEmpty();
    }

    private void handleClassLevelValidations(HashMap<String, String> nonReported) {
        nonReported.forEach((_property, cv) -> {
            addClassLevelValidationViolation(createClassLevelValidationComponent(cv));
        });
    }

    private Component createClassLevelValidationComponent(String message) {
        return classLevelValidationViolationComponentProvider.apply(message);
    }

    private void addClassLevelValidationViolation(Component validationViolationComponent) {
        HasComponents hc = getClassLevelViolationDisplay();
        errorMsgs.add(validationViolationComponent);
        hc.add(validationViolationComponent);
    }

    /**
     * Removes all validation errors from bound fields and {@link #getClassLevelViolationDisplay()}.
     */
    public void clearValidationErrors() {
        nameToEditorField.values().forEach(hv -> {
            if (hv instanceof HasValidationProperties hvp) {
                hvp.setInvalid(false);
                hvp.setErrorMessage(null);
            }
        });
        for (Component c : errorMsgs) {
            c.removeFromParent();
        }
        errorMsgs.clear();
    }

    /**
     * Sets a converter to use between the domain model property and the
     * corresponding UI component editing it.
     *
     * @param property the property
     * @param strToDt  the converter
     */
    public void setConverter(String property, Converter<?, ?> strToDt) {
        nameToConverter.put(property, strToDt);
    }

    /**
     * Checks if there have recently been errors to convert value from
     * the UI to the domain object.
     *
     * @return true if there are active conversion errors
     */
    public boolean hasInputConversionErrors() {
        return !propertyToInputValueConversionError.isEmpty();
    }

    /**
     * Returns a map containing input conversion errors (propertyname-error).
     *
     * @return input conversion errors
     */
    public Map<String, String> getInputConversionErrors() {
        return propertyToInputValueConversionError;
    }

    /**
     * @return true if the binging looks valid for the user: no  displayed
     * constraint violations nor input conversion errors.
     */
    public boolean isValid() {
        return getInputConversionErrors().isEmpty()
                && errorMsgs.isEmpty() && !constraintViolations;
    }

    /**
     * A flag to control whether server originated value change events
     * should be ignored. Currently only known to be needed for testing,
     * might be removed in the future.
     *
     * @param ignore true if non-client originated events should be ignored
     */
    public void setIgnoreServerOriginatedChanges(boolean ignore) {
        ignoreServerOriginatedChanges = ignore;
    }

    /**
     * Clears bindings, might be needed to clean up references if e.g. re-using fields
     */
    public void unBind() {
        registrations.forEach(Registration::remove);
        registrations.clear();
        this.valueObject = null;
    }

    public List<String> getBoundProperties() {
        return nameToEditorField.keySet().stream().toList();
    }

    public HasValue getEditor(String property) {
        return nameToEditorField.get(property);
    }

    public static class ParagraphWithErrorStyleClassLevelValidationViolationComponentProvider implements SerializableFunction<String, Component> {
        @Override
        public Component apply(String message) {
            return new ErrorMessage(message);
        }

    }

    private ValueContext fakeValueContext(HasValue hasValue) {
        Component component = (Component) hasValue;
        // TODO figure out if binder is really needed here, the constructor without it was removed in 25
        return new ValueContext(null, component);
    }

}
