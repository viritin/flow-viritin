package org.vaadin.firitin.form;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.shared.HasValidationProperties;
import com.vaadin.flow.component.textfield.TextField;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.vaadin.firitin.components.textfield.VTextField;

import java.lang.reflect.Field;
import java.util.HashMap;
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
// TODO make this implement HasValue -> could be used within existing form using the core Binder
public class VBinder<T> {

    // Helper "Jack" to do introspection
    private static final ObjectMapper jack = new ObjectMapper();
    private final Class<T> tClass;
    private final Component[] formComponents;
    private final BasicBeanDescription bbd;

    Map<BeanPropertyDefinition, HasValue> bpdToEditorField = new HashMap<>();
    Map<String, HasValue> nameToEditorField = new HashMap<>();

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
                            bpdToEditorField.put(property, (HasValue) f.get(formComponent));
                            nameToEditorField.put(property.getName(), (HasValue) f.get(formComponent));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }

    }

    public T getValue() {
        if (isImmutable()) {
            return constructRecord();
        }
        return null;
    }

    public void setValue(T value) {
        for (BeanPropertyDefinition pd : bbd.findProperties()) {
            Object pValue = pd.getAccessor().getValue(value);
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

    public void setConstraintViolations(Set<ConstraintViolation<T>> violations) {
        violations.forEach(cv -> {
            String property = cv.getPropertyPath().toString();
            HasValue hasValue = nameToEditorField.get(property);
            if(hasValue != null) {
                if (hasValue instanceof HasValidationProperties hvp) {
                    hvp.setInvalid(true);
                    hvp.setErrorMessage(cv.getMessage()); // TODO proper interpolation
                }
            }
        });
    }

}
