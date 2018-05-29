package org.vaadin.firitin.fluency.ui;

import java.util.Collection;
import java.util.stream.Stream;

import com.vaadin.flow.data.binder.HasItems;

@SuppressWarnings("unchecked")
public interface FluentHasItems<S extends FluentHasItems<S, T>, T> extends HasItems<T> {

    default S withItems(Collection<T> items) {
        setItems(items);
        return (S) this;
    }

    default S withItems(Stream<T> streamOfItems) {
        setItems(streamOfItems);
        return (S) this;
    }

    default S withItems(T... items) {
        setItems(items);
        return (S) this;
    }
    
}
