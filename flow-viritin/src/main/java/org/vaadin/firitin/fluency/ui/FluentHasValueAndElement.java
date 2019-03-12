package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;

@SuppressWarnings("unchecked")
public interface FluentHasValueAndElement<S extends FluentHasValueAndElement<S, E, V>, E extends ValueChangeEvent<V>, V>
        extends HasValue<E, V>, FluentHasEnabled<S> {

    default S withReadOnly(boolean readOnly) {
        setReadOnly(readOnly);
        return (S) this;
    }

    default S withRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        setRequiredIndicatorVisible(requiredIndicatorVisible);
        return (S) this;
    }

    default S withValue(V value) {
        setValue(value);
        return (S) this;
    }

    default S withValueChangeListener(ValueChangeListener<? super ValueChangeEvent<V>> listener) {
        addValueChangeListener(listener);
        return (S) this;
    }
}
