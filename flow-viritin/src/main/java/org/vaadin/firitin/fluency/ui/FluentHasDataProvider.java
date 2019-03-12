package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.data.binder.HasDataProvider;
import com.vaadin.flow.data.provider.DataProvider;

@SuppressWarnings("unchecked")
public interface FluentHasDataProvider<S extends FluentHasDataProvider<S, T>, T>
        extends HasDataProvider<T>, FluentHasItems<S, T> {

    default S withDataProvider(DataProvider<T, ?> dataProvider) {
        setDataProvider(dataProvider);
        return (S) this;
    }

}
