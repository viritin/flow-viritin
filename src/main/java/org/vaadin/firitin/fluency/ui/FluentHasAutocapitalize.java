package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.HasAutocapitalize;

public interface FluentHasAutocapitalize<S extends HasAutocapitalize> extends HasAutocapitalize {

    default S withAutocapitalize(Autocapitalize autocapitalize) {
        setAutocapitalize(autocapitalize);
        return (S) this;
    }
}
