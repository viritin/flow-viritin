package org.vaadin.firitin.fluency.ui;

public interface FluentComponent<S extends FluentComponent<S>>
        extends FluentAttachNotifier<S>, FluentDetachNotifier<S> {

    // TODO there is noch interface like HasId in Component, so that here could be used a default implementation
    S withId(String id);
}
