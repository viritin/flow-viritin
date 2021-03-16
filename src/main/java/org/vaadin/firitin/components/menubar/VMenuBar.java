package org.vaadin.firitin.components.menubar;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.menubar.MenuBar;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasSize;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;
import org.vaadin.firitin.fluency.ui.FluentHasTheme;

public class VMenuBar extends MenuBar implements FluentComponent<VMenuBar>, FluentHasSize<VMenuBar>, FluentHasStyle<VMenuBar>, FluentHasTheme<VMenuBar> {

    public VMenuBar withItem(String text) {
        addItem(text);
        return this;
    }

    public VMenuBar withItem(Component component) {
        addItem(component);
        return this;
    }

    public VMenuBar withItem(String text, ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        addItem(text, clickListener);
        return this;
    }

    public VMenuBar withItem(Component component, ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        addItem(component, clickListener);
        return this;
    }

    public VMenuBar withOpenOnHover(boolean openOnHover) {
        setOpenOnHover(openOnHover);
        return this;
    }
}
