package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.*;

public interface FluentCompositionNotifier<S extends CompositionNotifier> extends CompositionNotifier {

    default S withCompositionStartListener(ComponentEventListener<CompositionStartEvent> listener) {
        addCompositionStartListener(listener);
        return (S) this;
    }

    default S withCompositionUpdateListener(ComponentEventListener<CompositionUpdateEvent> listener) {
        addCompositionUpdateListener(listener);
        return (S) this;
    }

    default S withCompositionEndListener(ComponentEventListener<CompositionEndEvent> listener) {
        addCompositionEndListener(listener);
        return (S) this;
    }

}
