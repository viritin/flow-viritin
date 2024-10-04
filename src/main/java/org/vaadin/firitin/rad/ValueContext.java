package org.vaadin.firitin.rad;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

/**
 * ValueContext is a helper class to pass around the context of the value being displayed/printed.
 *
 * <p>Experimental, not yet stable API.</p>
 */
public class ValueContext {
    // Helper "Jack" to do introspection
    static final ObjectMapper jack = new ObjectMapper();

    private Class type;
    private Object value;
    private String propertyName;
    private ValueContext parent;
    private BasicBeanDescription bbd;
    private BeanPropertyDefinition property;

    public ValueContext(Object dto) {
        setValue(dto);
    }

    public void setValue(Object dto) {
        this.value = dto;
        if(type == null) {
            type = dto.getClass();
        }
        JavaType javaType = jack.getTypeFactory().constructType(type);
        this.bbd = (BasicBeanDescription) jack.getSerializationConfig().introspect(javaType);
        setBeanDescription(bbd);
    }

    public Object getValue() {
        return value;
    }

    public ValueContext getParent() {
        return parent;
    }

    public void setParent(ValueContext parent) {
        this.parent = parent;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Class getType() {
        return type;
    }

    public void setBeanDescription(BasicBeanDescription bbd) {
        this.bbd = bbd;
    }

    public BasicBeanDescription getBeanDescription() {
        return bbd;
    }

    public void setProperty(BeanPropertyDefinition p) {
        this.property = p;
    }

    public BeanPropertyDefinition getProperty() {
        return property;
    }

    public Object getPropertyValue() {
        return property.getGetter().getValue(value);
    }
}
