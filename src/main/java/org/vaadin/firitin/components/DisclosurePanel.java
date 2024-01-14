package org.vaadin.firitin.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

/**
 * @deprecated Use the nowadays available Details component from Vaadin core instead.
 */
@Deprecated(forRemoval = true)
public class DisclosurePanel extends VerticalLayout {

    private static final long serialVersionUID = 6509419456771505782L;

    private Icon closedIcon = VaadinIcon.ARROW_CIRCLE_RIGHT.create();
    private Icon openIcon = VaadinIcon.ARROW_CIRCLE_DOWN.create();

    private final VButton toggle = new VButton(closedIcon);
    private final VVerticalLayout contentWrapper = new VVerticalLayout();

    /**
     * Creates a new disclosure panel.
     */
    public DisclosurePanel() {
        setMargin(false);
        setSpacing(false);
        toggle.addThemeVariants(ButtonVariant.LUMO_ICON);
        contentWrapper.setVisible(false);
        add(toggle, contentWrapper);
        toggle.addClickListener(e->setOpen(!isOpen()));
    }

    /**
     * Creates a new disclosure panel with the given caption and content.
     *
     * @param caption the caption
     * @param content the content
     */
    public DisclosurePanel(String caption, Component... content) {
        this();
        setCaption(caption);
        contentWrapper.add(content);
    }

    /**
     * @return true if content is visible
     */
    public boolean isOpen() {
        return contentWrapper.isVisible();
    }

    /**
     * Sets the visibility of the content.
     * @param open true if content should be visible
     * @return the DisclosurePanel for further configuration
     */
    public DisclosurePanel setOpen(boolean open) {
        contentWrapper.setVisible(open);
        toggle.setIcon(open ? getOpenIcon() : getClosedIcon());
        return this;
    }

    /**
     * Sets the content of the panel.
     * @param content the content
     * @return the DisclosurePanel for further configuration
     */
    public DisclosurePanel setContent(Component... content) {
        this.contentWrapper.removeAll();
        this.contentWrapper.add(content);
        return this;
    }

    /**
     * Sets the caption of the panel.
     * @param caption the caption
     */
    public void setCaption(String caption) {
        toggle.setText(caption);
    }


    /**
     * @return the wrapper component
     */
    public VVerticalLayout getContentWrapper() {
        return contentWrapper;
    }

    /**
     * @return the icon
     */
    public Icon getClosedIcon() {
        return closedIcon;
    }

    public DisclosurePanel setClosedIcon(Icon closedIcon) {
        this.closedIcon = closedIcon;
        return setOpen(isOpen());
    }

    /**
     * @return the openIcon
     */
    public Icon getOpenIcon() {
        return openIcon;
    }

    /**
     * Sets the icon to used to show the component
     * @param openIcon the openIcon to set
     * @return the DisclosurePanel for further configuration
     */
    public DisclosurePanel setOpenIcon(Icon openIcon) {
        this.openIcon = openIcon;
        return setOpen(isOpen());
    }

}
