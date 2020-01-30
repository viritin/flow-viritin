package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

public interface FluentFlexComponent<T extends Component, S extends FluentFlexComponent<T, S>> extends FlexComponent<T> {

    default S withAlignItems(FlexComponent.Alignment alignment) {
        setAlignItems(alignment);
        return (S) this;
    }

    default S withAlignSelf(FlexComponent.Alignment alignment, HasElement... elementContainers) {
        setAlignSelf(alignment, elementContainers);
        return (S) this;
    }

    default S withFlexGrow(double flexGrow, HasElement... elementContainers) {
        setFlexGrow(flexGrow, elementContainers);
        return (S) this;
    }
}
