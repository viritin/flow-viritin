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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A new start for the Binder. Note, that this is still a fairly new class,
 * so there might be changes to it.
 * <p>
 * Design principles:
 * <p>
 * * Only support "non-buffered mode" so that validation logic can use the bean/record + simplicity of the implementation
 * * Validation is "just validation", and not concern of this class. BUT, API must support binding external validation logic, like Bean Validation API
 * * Must support Records & immutable objects as well
 * * No requirements for BeanValidation or Spring DataBinding stuff, but optional support (or extensible for those)
 *
 * Non-goals:
 * * Aiming for binding anything without property names (for good solution this needs to be resolved at language level and supported with thing like Bean Validation first)
 *
 * @param <T>
 */

public class FormBinder<T> implements HasValue<FormBinderValueChangeEvent<T>, T> {

    // Helper "Jack" to do introspection
    private static final ObjectMapper jack = new ObjectMapper();
    private final Class<T> tClass;
    private final Component[] formComponents;
    private final BasicBeanDescription bbd;

    Map<BeanPropertyDefinition, HasValue> bpdToEditorField = new HashMap<>();
    Map<String, HasValue> nameToEditorField = new HashMap<>();
    Map<String, Converter> nameToConverter = new HashMap<>();
    private Set<Component> errorMsgs = new HashSet<>();
    private T valueObject;
    private List<ValueChangeListener> valueChangeListeners;

    public FormBinder(Class<T> tClass, Component... componentsForNameBasedBinding) {
        this.tClass = tClass;
        formComponents = componentsForNameBasedBinding;
        JavaType javaType = jack.getTypeFactory().constructType(tClass);
        this.bbd = (BasicBeanDescription) jack.getSerializationConfig().introspect(javaType);

        for (Component formComponent : formComponents) {
            Class<? extends Component> aClass = formComponent.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field f : declaredFields) {
                // TODO, figure out other naming strategies
                // TODO, inspect the class hierarchy to some known core component
                Class<?> type = f.getType();
                if (HasValue.class.isAssignableFrom(type)) {
                    BeanPropertyDefinition property = bbd.findProperty(new PropertyName(f.getName()));
                    property.getAccessor().fixAccess(true);

                    if (property != null) {
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

    protected static boolean isRequired(BeanPropertyDefinition property) {
        if (property.getPrimaryType().isPrimitive()) {
            return true;
        }

        try {
            return property.getGetter().getAnnotation(NotEmpty.class) != null ||
                    property.getGetter().getAnnotation(NotNull.class) != null ||
                    property.getGetter().getAnnotation(NotBlank.class) != null
                    ;
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
            hasValue.addValueChangeListener(e -> {
                Object value = e.getValue();
                value = convertInputValue(value, property, ctx);
                try {
                    property.getSetter().callOnWith(valueObject, value);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
        hasValue.addValueChangeListener(e -> {
            var event = new FormBinderValueChangeEvent<T>(FormBinder.this, e.isFromClient());
            if (valueChangeListeners != null) {
                for (ValueChangeListener vcl : valueChangeListeners.toArray(new ValueChangeListener[0])) {
                    vcl.valueChanged(event);
                }
            }
        });
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

    HashMap<String,String> propertyToInputValueConversionError = new HashMap<>();

    /**
     * Handles input conversion error. By default, the error message saved
     * and set to the field.
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
            // TODO show the conversion error somewho in UI/binder API if not of type HasValidationProperties
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
     * @param valueObject
     *            the new value
     */
    @Override
    public void setValue(T valueObject) {
        this.valueObject = valueObject;
        for (BeanPropertyDefinition pd : bbd.findProperties()) {
            Object pValue;
            if (isImmutable()) {
                pValue = pd.getAccessor().getValue(valueObject);
            } else {
                pValue = pd.getGetter().getValue(valueObject);
            }
            HasValue hasValue = bpdToEditorField.get(pd);
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
        // TODO
        return false;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        // TODO
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        // TODO check up
        return false;
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        // TODO figure out if this should throw or stop using HashValue altogether
        // Passing to fields is simply wrong and we don't have a place to show the
    }

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

    public void setConstraintViolations(Set<ConstraintViolation<T>> violations) {
        clearValidationErrors();
        HashSet<ConstraintViolation<T>> nonReported = new HashSet<>(violations);
        violations.forEach(cv -> {
            String property = cv.getPropertyPath().toString();
            if (!property.isEmpty()) {
                HasValue hasValue = nameToEditorField.get(property);
                if (hasValue instanceof HasValidationProperties hvp) {
                    hvp.setInvalid(true);
                    hvp.setErrorMessage(cv.getMessage()); // TODO proper interpolation
                    nonReported.remove(cv);
                }
            }
        });
        handleClassLevelValidations(nonReported);
    }

    protected void handleClassLevelValidations(Set<ConstraintViolation<T>> violations) {
        if (formComponents[0] instanceof HasComponents hc) {
            for (ConstraintViolation cv : violations) {
                // TODO proper interpolation etc
                Paragraph paragraph = new Paragraph();
                paragraph.addClassNames(LumoUtility.TextColor.ERROR);
                paragraph.setText(cv.getMessage());
                errorMsgs.add(paragraph);
                hc.add(paragraph);
            }
        }
    }

    /**
     * An alternative API to report constraint violations without
     * BeanValidation on the classpath.
     *
     * @param propertyToViolation
     * @deprecated try to use the standard Java Bean Validation API based method instead
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
                    hvp.setErrorMessage(msg); // TODO proper interpolation
                    nonReported.remove(property);
                }
            }
        });
        handleClassLevelValidations(nonReported);
    }

    private void handleClassLevelValidations(HashMap<String, String> nonReported) {
        if (formComponents[0] instanceof HasComponents hc) {
            nonReported.forEach((property, cv) -> {
                // TODO proper interpolation etc
                Paragraph paragraph = new Paragraph();
                paragraph.addClassNames(LumoUtility.TextColor.ERROR);
                paragraph.setText(cv);
                errorMsgs.add(paragraph);
                hc.add(paragraph);
            });
        }

    }

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

    public void setConverter(String bar, Converter<?, ?> strToDt) {
        nameToConverter.put(bar, strToDt);
    }

    protected Converter forProperty(String propertyName) {
        return nameToConverter.get(propertyName);
    }

    public boolean hasInputConversionErrors() {
        return !propertyToInputValueConversionError.isEmpty();
    }

    public Map<String,String> getInputConversionErrors() {
        return propertyToInputValueConversionError;
    }
}
