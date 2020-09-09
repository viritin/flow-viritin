package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.*;

public interface FluentKeyNotifier<S extends InputNotifier> extends KeyNotifier {

    default S withKeyDownListener(ComponentEventListener<KeyDownEvent> listener) {
        addKeyDownListener(listener);
        return (S) this;
    }

    default S withKeyPressListener(ComponentEventListener<KeyPressEvent> listener) {
        addKeyPressListener(listener);
        return (S) this;
    }

    default S withKeyUpListener(ComponentEventListener<KeyUpEvent> listener) {
        addKeyUpListener(listener);
        return (S) this;
    }

    default S withKeyDownListener(Key key, ComponentEventListener<KeyDownEvent> listener, KeyModifier... modifiers) {
        addKeyDownListener(key, listener, modifiers);
        return (S) this;
    }

    default S withKeyPressListener(Key key, ComponentEventListener<KeyPressEvent> listener, KeyModifier... modifiers) {
        addKeyPressListener(key, listener, modifiers);
        return (S) this;
    }

    default S withKeyUpListener(Key key, ComponentEventListener<KeyUpEvent> listener, KeyModifier... modifiers) {
        addKeyUpListener(key, listener, modifiers);
        return (S) this;
    }
}
