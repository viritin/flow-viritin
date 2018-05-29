package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;

public interface FluentFocusable<T extends Component, S extends FluentFocusable<T, S>>
        extends Focusable<T>, FluentFocusNotifier<T, S>, FluentBlurNotifier<T, S> {

    @SuppressWarnings("unchecked")
    default S withTabIndex(int tabIndex) {
        setTabIndex(tabIndex);
        return (S) this;
    }
}
