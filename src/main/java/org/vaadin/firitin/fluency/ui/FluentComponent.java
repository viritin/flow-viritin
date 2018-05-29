package org.vaadin.firitin.fluency.ui;

public interface FluentComponent<S extends FluentComponent<S>>
        extends FluentAttachNotifier<FluentComponent<S>>, FluentDetachNotifier<S> {

}
