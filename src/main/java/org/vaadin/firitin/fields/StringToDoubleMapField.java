package org.vaadin.firitin.fields;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.textfield.TextArea;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A field to edit a set of String using one simple text field. Strings are separated using a comma and optional white-space.
 */
public class StringToDoubleMapField extends AbstractCompositeField<TextArea, StringToDoubleMapField, Map<String,Double>> implements HasSize {

    private Map<String, Double> oldValue;

    public StringToDoubleMapField() {
        super(Collections.emptyMap());
        getContent().addValueChangeListener(e -> {
            fireEvent(new AbstractField.ComponentValueChangeEvent<>(
                    StringToDoubleMapField.this,
                    StringToDoubleMapField.this,
                    oldValue,
                    e.isFromClient()));
        });
    }

    public StringToDoubleMapField(String label) {
        this();
        setLabel(label);
    }

    @Override
    public void setValue(Map<String,Double> value) {
        super.setValue(value);
        this.oldValue = value;
    }

    @Override
    public Map<String, Double> getValue() {
        HashMap<String, Double> set = new HashMap<>();
        for (String s : getContent().getValue().split(",\\s*")) {
            String[] parts = s.split("=");
            if(parts.length == 2) {
                Double d = Double.parseDouble(parts[1].trim());
                String key = parts[0].trim();            
                set.put(key, d);
            }
        }
        return set;
    }

    @Override
    protected void setPresentationValue(Map<String, Double> newPresentationValue) {
        
        System.out.println(newPresentationValue);
        
        getContent().setValue(
                newPresentationValue.toString()
                        .replaceAll("\\{", "")
                        .replaceAll("}", "")
        );

    }

    public void setLabel(String label) {
        getContent().setLabel(label);
    }

    public String getLabel() {
        return getContent().getLabel();
    }
}
