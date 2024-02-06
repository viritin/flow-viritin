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
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A new start for the Binder.
 * <p>
 * Design principles:
 * <p>
 * * Only support "non-buffered mode" so that validation logic can use the bean/record + simplicity of the implementation
 * * Validation is "just validation", and not concern of this class. BUT, API must support binding external validation logic, like Bean Validation API
 * * Must support Records & immutable objects as well
 *
 * @param <T>
 */
// TODO make this implement HasValue  -> could be used within existing form using the core Binder
public class VBinder<T> {

    // Helper "Jack" to do introspection
    private static final ObjectMapper jack = new ObjectMapper();
    private final Class<T> tClass;
    private final Component[] formComponents;
    private final BasicBeanDescription bbd;

    Map<BeanPropertyDefinition, HasValue> bpdToEditorField = new HashMap<>();
    Map<String, HasValue> nameToEditorField = new HashMap<>();
    private Set<Component> errorMsgs = new HashSet<>();
    private T valueObject;

    public VBinder(Class<T> tClass, Component... componentsForNameBasedBinding) {
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

    protected void configureEditor(BeanPropertyDefinition property, HasValue hasValue) {
        if (hasValue instanceof HasValueChangeMode hvcm) {
            hvcm.setValueChangeMode(ValueChangeMode.LAZY);
        }
        if(!isImmutable()) {
            // Mutate
            hasValue.addValueChangeListener(e -> {
                try {
                    property.getSetter().callOnWith(valueObject, e.getValue());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });

        }
    }

    protected static boolean isRequired(BeanPropertyDefinition property) {
        return property.getGetter().getAnnotation(NotEmpty.class) != null ||
                property.getGetter().getAnnotation(NotNull.class) != null ||
                property.getGetter().getAnnotation(NotBlank.class) != null
                ;
    }

    public T getValue() {
        if (isImmutable()) {
            return constructRecord();
        } else {
            if(valueObject == null) {
                return constructPojo();
            }
        }
        return valueObject;
    }

    public void setValue(T valueObject) {
        this.valueObject = valueObject;
        for (BeanPropertyDefinition pd : bbd.findProperties()) {
            Object pValue;
            if(isImmutable()) {
                pValue = pd.getAccessor().getValue(valueObject);
            } else {
                pValue = pd.getGetter().getValue(valueObject);
            }
            HasValue hasValue = bpdToEditorField.get(pd);
            hasValue.setValue(pValue);
        }

    }

    protected boolean isImmutable() {
        return bbd.isRecordType();
    }

    protected T constructRecord() {
        AnnotatedConstructor annotatedConstructor = bbd.getConstructors().get(0);
        List<BeanPropertyDefinition> properties = bbd.findProperties();
        Object[] args = new Object[properties.size()];
        for (int i = 0; i < properties.size(); i++) {
            args[i] = bpdToEditorField.get(properties.get(i)).getValue();
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
        bpdToEditorField.forEach( (bpd, hasValue) -> {
            try {
                bpd.getSetter().callOnWith(o, hasValue.getValue());
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
                }
                nonReported.remove(cv);
            }
        });
        // TODO handle class level and other violations
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

    private void clearValidationErrors() {
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

}
