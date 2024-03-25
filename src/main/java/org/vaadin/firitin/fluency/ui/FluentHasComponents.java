package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.RouteParameters;

import java.util.Optional;

@SuppressWarnings("unchecked")
public interface FluentHasComponents<S extends FluentHasComponents<S>> extends HasComponents {

    default S withComponents(Component... components) {
        add(components);
        return (S) this;
    }

    default S withComponentAsFirst(Component component) {
        addComponentAsFirst(component);
        return (S) this;
    }

    default S withComponentAtIndex(int index, Component component) {
        addComponentAtIndex(index, component);
        return (S) this;
    }

    /**
     * Navigates the UI of this component to the view corresponding to the given navigation
     * target. This is essentially a shortcut to UI.getCurrent().navigate()
     * <p>
     * Besides the navigation to the {@code location} this method also updates
     * the browser location (and page history).
     * <p>
     * If the view change actually happens (e.g. the view itself doesn't cancel
     * the navigation), all navigation listeners are notified and a reference of
     * the new view is returned for additional configuration.
     *
     *
     * @param navigationTarget
     *            navigation target to navigate to
     * @throws IllegalArgumentException
     *             if navigationTarget is a {@link HasUrlParameter} with a
     *             mandatory parameter.
     * @throws NotFoundException
     *             in case there is no route defined for the given
     *             navigationTarget.
     * @return the view instance, if navigation actually happened
     */
    default <T extends Component> Optional<T> navigate(Class<T> navigationTarget) {
        return UI.getCurrent().navigate(navigationTarget);
    }
}
