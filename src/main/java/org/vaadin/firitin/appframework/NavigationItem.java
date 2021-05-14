package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.RouterLink;

public class NavigationItem extends Tab {
    private final Class<? extends Component> navigationTarget;
    private final String text;

    public NavigationItem(String label, Class<? extends Component> navigationTarget) {
        super(new RouterLink(label, navigationTarget));
        this.text = label;
        this.navigationTarget = navigationTarget;
    }

    public String getText() {
        return text;
    }

    public Class<? extends Component> getNavigationTarget() {
        return navigationTarget;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        String color = enabled ? "" : "gray";
        getStyle().set("color", color);
    }
}
