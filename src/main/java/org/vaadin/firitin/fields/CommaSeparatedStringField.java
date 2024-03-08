package org.vaadin.firitin.fields;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.fluency.ui.FluentHasSize;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A field to edit a set of String using one simple text field. Strings are separated using a comma and optional white-space.
 */
public class CommaSeparatedStringField extends AbstractCompositeField<VTextField, CommaSeparatedStringField, Set<String>> implements FluentHasSize<CommaSeparatedStringField> {

    private Set<String> oldValue;

    public CommaSeparatedStringField() {
        super(Collections.emptySet());
        getContent().addValueChangeListener(e -> {
            fireEvent(new AbstractField.ComponentValueChangeEvent<CommaSeparatedStringField, Set<String>>(
                    CommaSeparatedStringField.this,
                    CommaSeparatedStringField.this,
                    oldValue,
                    e.isFromClient()));
        });
    }

    public CommaSeparatedStringField(String label) {
        this();
        setLabel(label);
    }

    @Override
    public void setValue(Set<String> value) {
        super.setValue(value);
        this.oldValue = value;
    }

    @Override
    public Set<String> getValue() {
        HashSet<String> set = new HashSet<>();
        for (String s : getContent().getValue().split(",\\s*")) {
            set.add(s);
        }
        return set;
    }

    @Override
    protected void setPresentationValue(Set<String> newPresentationValue) {
        getContent().setValue(
                newPresentationValue.toString()
                        .replaceAll("\\[", "")
                        .replaceAll("]", "")
        );

    }

    public void setLabel(String label) {
        getContent().setLabel(label);
    }

    public String getLabel() {
        return getContent().getLabel();
    }
}
