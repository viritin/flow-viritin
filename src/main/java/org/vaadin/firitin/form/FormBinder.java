package org.vaadin.firitin.form;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.shared.HasValidationProperties;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
    private static final ObjectMapper jack = new ObjectMapper();
    private final Class<T> tClass;
    private final BasicBeanDescription bbd;

    Map<BeanPropertyDefinition, HasValue> bpdToEditorField = new HashMap<>();
    Map<String, HasValue> nameToEditorField = new LinkedHashMap<>();
    Map<String, Converter> nameToConverter = new HashMap<>();
    HashMap<String, String> propertyToInputValueConversionError = new HashMap<>();
    private Set<Component> errorMsgs = new HashSet<>();
    private T valueObject;
    private List<ValueChangeListener> valueChangeListeners;
    private boolean constraintViolations;
    private HasComponents classLevelViolationDisplay;
    private boolean ignoreServerOriginatedChanges = true;

    List<Registration> registrations = new ArrayList<>();

    private SerializableFunction<String,Component> classLevelValidationViolationComponentProvider = new ParagraphWithErrorStyleClassLevelValidationViolationComponentProvider();

    /**
     * Constructs a new binder.
     *
     * @param tClass              the class of the bound entity/bean, set later with {@link #setValue(Object)}
     * @param containerComponents the components whose class contains the fields to bound
     */
    public FormBinder(Class<T> tClass, Component... containerComponents) {
        this.tClass = tClass;
        if(containerComponents[0] instanceof HasComponents hc) {
            classLevelViolationDisplay = hc;
        }
        JavaType javaType = jack.getTypeFactory().constructType(tClass);
        this.bbd = (BasicBeanDescription) jack.getSerializationConfig().introspect(javaType);

        for (Component formComponent : containerComponents) {
            Class<? extends Component> aClass = formComponent.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field f : declaredFields) {
                // TODO, figure out other naming strategies
                // TODO, inspect the class hierarchy to some known core component
                Class<?> type = f.getType();
                if (HasValue.class.isAssignableFrom(type)) {
                    BeanPropertyDefinition property = bbd.findProperty(new PropertyName(f.getName()));

                    if (property != null) {
                        property.getAccessor().fixAccess(true);
                        try {
                            f.setAccessible(true);
                            HasValue hasValue = (HasValue) f.get(formComponent);
                            if (isRequired(property)) {
                                hasValue.setRequiredIndicatorVisible(true);
                            }
                            bpdToEditorField.put(property, hasValue);
                            nameToEditorField.put(property.getName(), hasValue);
                            configureEditor(property, hasValue);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    /**
     * Constructs a new binder.
     *
     * @param tClass              the class of the bound entity/bean, set later with {@link #setValue(Object)}
     * @param editorObject        the editor object that contains the fields to bound, does not need to be a component
     * @deprecated not sure yet if this is a good idea, added for backwards compatibility
     */
    @Deprecated
    public FormBinder(Class<T> tClass, Object editorObject) {
        this.tClass = tClass;
        if (editorObject instanceof HasComponents hc) {
            classLevelViolationDisplay = hc;
        }
        JavaType javaType = jack.getTypeFactory().constructType(tClass);
        this.bbd = (BasicBeanDescription) jack.getSerializationConfig().introspect(javaType);
        Class<?> aClass = editorObject.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field f : declaredFields) {
            Class<?> type = f.getType();
            if (HasValue.class.isAssignableFrom(type)) {
                BeanPropertyDefinition property = bbd.findProperty(new PropertyName(f.getName()));

                if (property != null) {
                    property.getAccessor().fixAccess(true);
                    try {
                        f.setAccessible(true);
                        HasValue hasValue = (HasValue) f.get(editorObject);
                        if (isRequired(property)) {
                            hasValue.setRequiredIndicatorVisible(true);
                        }
                        bpdToEditorField.put(property, hasValue);
                        nameToEditorField.put(property.getName(), hasValue);
                        configureEditor(property, hasValue);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /**
     * Binds type to given property editors
     * @param tClass the type to bind
     * @param propertyNameToEditor pre-instantiated editors to bind
     */
    public FormBinder(Class<T> tClass, Map<String,HasValue> propertyNameToEditor) {
        this.tClass = tClass;
        JavaType javaType = jack.getTypeFactory().constructType(tClass);
        this.bbd = (BasicBeanDescription) jack.getSerializationConfig().introspect(javaType);
        for (BeanPropertyDefinition property : bbd.findProperties()) {
            if(propertyNameToEditor.containsKey(property.getName())) {
                HasValue hasValue = propertyNameToEditor.get(property.getName());
                if (isRequired(property)) {
                    hasValue.setRequiredIndicatorVisible(true);
                }
                bpdToEditorField.put(property, hasValue);
                nameToEditorField.put(property.getName(), hasValue);
                configureEditor(property, hasValue);
            }
        }
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

    protected static boolean isRequired(BeanPropertyDefinition property) {
        if (property.getPrimaryType().isPrimitive()) {
            return true;
        }

        try {
            return property.getGetter().getAnnotation(NotEmpty.class) != null
                    || property.getGetter().getAnnotation(NotNull.class) != null
                    || property.getGetter().getAnnotation(NotBlank.class) != null;
        } catch (java.lang.NoClassDefFoundError ex) {
            // No Bean Validation on classpath
            return false;
        }
    }

    protected void configureEditor(BeanPropertyDefinition property, HasValue hasValue) {

        if (hasValue instanceof HasValueChangeMode hvcm) {
            hvcm.setValueChangeMode(ValueChangeMode.LAZY);
        }
        if (!isImmutable()) {
            ValueContext ctx = new ValueContext((Component) hasValue);
            // Mutate
            registrations.add(hasValue.addValueChangeListener(e -> {
                boolean dropServerOriginateEvent  = !e.isFromClient() && ignoreServerOriginatedChanges;
                if (!dropServerOriginateEvent) {
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
     * Sets the value object bound to this form
     *
     * @param valueObject the new value
     */
    @Override
    public void setValue(T valueObject) {
        this.valueObject = valueObject;
        for (BeanPropertyDefinition pd : bbd.findProperties()) {
            HasValue hasValue = bpdToEditorField.get(pd);
            if (hasValue != null) {
                Object pValue;
                if (isImmutable()) {
                    pValue = pd.getAccessor().getValue(valueObject);
                } else {
                    pValue = pd.getGetter().getValue(valueObject);
                }

                if (pValue == null) {
                    hasValue.clear();
                } else {
                    Converter converter = nameToConverter.get(pd.getName());
                    if (converter != null) {
                        pValue = converter.convertToPresentation(pValue, new ValueContext((Component) hasValue));
                    }
                    try {
                        hasValue.setValue(pValue);
                    } catch (ClassCastException ex) {
                        throw new UnsupportedOperationException("FormBinder don't yet support automatic value conversion.", ex);
                    }
                }
            } else {
                // TODO figure out if non-bound fields needs to be handled some how, probably not
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
        AnnotatedConstructor annotatedConstructor = bbd.getConstructors().get(0);
        List<BeanPropertyDefinition> properties = bbd.findProperties();
        Object[] args = new Object[properties.size()];
        for (int i = 0; i < properties.size(); i++) {
            BeanPropertyDefinition definition = properties.get(i);
            HasValue hasValue = bpdToEditorField.get(definition);
            Object value = hasValue.getValue();
            value = convertInputValue(value, definition, new ValueContext((Component) hasValue));
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
                value = convertInputValue(value, bpd, new ValueContext((Component) hasValue));
                bpd.getSetter().callOnWith(o, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return o;
    }

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
                    hvp.setInvalid(true);
                    hvp.setErrorMessage(cv.getMessage());
                    nonReported.remove(cv);
                }
            }
        });
        handleClassLevelValidations(nonReported);
        constraintViolations = !violations.isEmpty();
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
     * An alternative API to report constraint violations without BeanValidation
     * on the classpath.
     *
     * @param propertyToViolation
     * @deprecated try to use the standard Java Bean Validation API based method
     * instead
     */
    @Deprecated
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
     * @param property the property
     * @param strToDt the converter
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

    public static class ParagraphWithErrorStyleClassLevelValidationViolationComponentProvider implements SerializableFunction<String,Component> {
        @Override
        public Component apply(String message) {
            Paragraph paragraph = new Paragraph();
            paragraph.addClassNames(LumoUtility.TextColor.ERROR);
            paragraph.setText(message);
            return paragraph;
        }
    }

}
