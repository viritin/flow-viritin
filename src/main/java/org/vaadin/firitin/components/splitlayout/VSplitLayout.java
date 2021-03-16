package org.vaadin.firitin.components.splitlayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;
import org.vaadin.firitin.fluency.ui.*;

public class VSplitLayout extends SplitLayout implements FluentComponent<VSplitLayout>, FluentHasStyle<VSplitLayout>, FluentHasSize<VSplitLayout>, FluentClickNotifier<SplitLayout, VSplitLayout>, FluentHasTheme<VSplitLayout> {

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

    public VSplitLayout withThemeVariants(SplitLayoutVariant... variants) {
        addThemeVariants(variants);
        return this;
    }

    public VSplitLayout withPrimaryStyle(String styleName, String value) {
        setPrimaryStyle(styleName, value);
        return this;
    }

    public VSplitLayout withSecondaryStyle(String styleName, String value) {
        setSecondaryStyle(styleName, value);
        return this;
    }
}
