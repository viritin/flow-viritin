package org.vaadin.firitin.components.splitlayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasSize;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

public class VSplitLayout extends SplitLayout implements FluentComponent<VSplitLayout>, FluentHasStyle<VSplitLayout>, FluentHasSize<VSplitLayout>, FluentClickNotifier<SplitLayout, VSplitLayout> {

    public VSplitLayout() {
        super();
    }

    public VSplitLayout(Component primaryComponent,
                        Component secondaryComponent) {
        super(primaryComponent, secondaryComponent);
    }

    public VSplitLayout withOrientation(Orientation orientation) {
        setOrientation(orientation);
        return this;
    }

    public VSplitLayout withToPrimary(Component... components) {
        addToPrimary(components);
        return this;
    }


    public VSplitLayout withToSecondary(Component... components) {
        addToSecondary(components);
        return this;
    }

    public VSplitLayout withSplitterPosition(double position) {
        setSplitterPosition(position);
        return this;
    }

    public VSplitLayout withSplitterDragendListener(ComponentEventListener<SplitterDragendEvent<SplitLayout>> listener) {
        addSplitterDragendListener(listener);
        return this;
    }
}
