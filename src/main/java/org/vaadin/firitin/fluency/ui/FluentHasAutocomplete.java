package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.HasAutocomplete;

public interface FluentHasAutocomplete<S extends HasAutocomplete> extends HasAutocomplete {

    default S withAutocomplete(Autocomplete autocomplete) {
        setAutocomplete(autocomplete);
        return (S) this;
    }
}
