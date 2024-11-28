package org.vaadin.firitin.rad;

import com.vaadin.flow.component.Component;
import org.vaadin.firitin.form.FormBinder;

// TODO consider nuking this
public interface Form<T> {
    Component getComponent();

    T getValue();

    FormBinder<T> getBinder();
}
