package org.vaadin.firitin.fluency.ui;

@SuppressWarnings("unchecked")
public interface FluentComponent<S extends FluentComponent<S>>
        extends FluentAttachNotifier<S>, FluentDetachNotifier<S> {

    void setVisible(boolean visible);

    void setId(String id);

    default S withVisible(boolean visible) {
        setVisible(visible);
        return (S) this;
    }
    
    default S withId(String id) {
        setId(id);
        return (S) this;
    }
}
