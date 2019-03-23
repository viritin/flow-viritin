package org.vaadin.firitin.components.applayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.dom.Element;
import org.vaadin.firitin.fluency.ui.FluentComponent;

public class VAppLayout extends AppLayout implements FluentComponent<VAppLayout> {

    public VAppLayout withBranding(Component branding) {
        setBranding(branding);
        return this;
    }

    public VAppLayout withBranding(Element branding) {
        setBranding(branding);
        return this;
    }

    public VAppLayout withContent(Component content) {
        setContent(content);
        return this;
    }

    public VAppLayout withContent(Element content) {
        setContent(content);
        return this;
    }


    public VAppLayout withMenu(HasElement menu) {
        setMenu(menu);
        return this;
    }

    public VAppLayout withMenu(Element menu) {
        setMenu(menu);
        return this;
    }



}
