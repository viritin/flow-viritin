package org.vaadin.firitin.components.ironlist;

import com.vaadin.flow.component.ironlist.IronList;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;
import org.vaadin.firitin.fluency.ui.*;

public class VIronList<T> extends IronList<T> implements FluentComponent<VIronList<T>>, FluentHasDataProvider<VIronList<T>, T>, FluentHasStyle<VIronList<T>>, FluentHasSize<VIronList<T>>, FluentFocusable<IronList<T>, VIronList<T>> {


    public VIronList<T> withRenderer(ValueProvider<T, String> valueProvider) {
        setRenderer(valueProvider);
        return this;
    }

    public VIronList<T> withRenderer(Renderer<T> renderer) {
        setRenderer(renderer);
        return this;
    }

    public VIronList<T> withPlaceholderItem(T placeholderItem) {
        setPlaceholderItem(placeholderItem);
        return this;
    }

    public VIronList<T> withGridLayout(boolean gridLayout) {
        setGridLayout(gridLayout);
        return this;
    }

    public VIronList<T> withOnEnabledStateChanged(boolean enabled) {
        onEnabledStateChanged(enabled);
        return this;
    }
}
