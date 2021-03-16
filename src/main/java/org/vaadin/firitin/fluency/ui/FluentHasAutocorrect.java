package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.textfield.HasAutocorrect;

public interface FluentHasAutocorrect<S extends HasAutocorrect> extends HasAutocorrect {

    default S withAutocorrect(boolean autocorrect) {
        setAutocorrect(autocorrect);
        return (S) this;
    }
}
