package org.vaadin.firitin.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

public class DisclosurePanel extends VerticalLayout {

    private static final long serialVersionUID = 6509419456771505782L;

    private Icon closedIcon = VaadinIcon.ARROW_CIRCLE_RIGHT.create();
    private Icon openIcon = VaadinIcon.ARROW_CIRCLE_DOWN.create();

    private final VButton toggle = new VButton(closedIcon);
    private final VVerticalLayout contentWrapper = new VVerticalLayout();

    public DisclosurePanel() {
        setMargin(false);
        setSpacing(false);
        toggle.addThemeVariants(ButtonVariant.LUMO_ICON);
        contentWrapper.setVisible(false);
        add(toggle, contentWrapper);
        toggle.addClickListener(e->setOpen(!isOpen()));
    }

    public DisclosurePanel(String caption, Component... content) {
        this();
        setCaption(caption);
        contentWrapper.add(content);
    }

    public boolean isOpen() {
        return contentWrapper.isVisible();
    }

    public DisclosurePanel setOpen(boolean open) {
        contentWrapper.setVisible(open);
        toggle.setIcon(open ? getOpenIcon() : getClosedIcon());
        return this;
    }

    public DisclosurePanel setContent(Component... content) {
        this.contentWrapper.removeAll();
        this.contentWrapper.add(content);
        return this;
    }

    public void setCaption(String caption) {
        toggle.setText(caption);
    }

    public VVerticalLayout getContentWrapper() {
        return contentWrapper;
    }

    public Icon getClosedIcon() {
        return closedIcon;
    }

    public DisclosurePanel setClosedIcon(Icon closedIcon) {
        this.closedIcon = closedIcon;
        return setOpen(isOpen());
    }

    public Icon getOpenIcon() {
        return openIcon;
    }

    public DisclosurePanel setOpenIcon(Icon openIcon) {
        this.openIcon = openIcon;
        return setOpen(isOpen());
    }

}
