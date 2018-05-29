package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.HasValidation;

@SuppressWarnings("unchecked")
public interface FluentHasValidation<S extends FluentHasValidation<S>> extends HasValidation {

    default S withErrorMessage(String errorMessage) {
        setErrorMessage(errorMessage);
        return (S) this;
    }

    default S withInvalid(boolean invalid) {
        setInvalid(invalid);
        return (S) this;
    }
}
