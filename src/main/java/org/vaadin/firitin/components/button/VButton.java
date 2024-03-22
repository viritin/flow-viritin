package org.vaadin.firitin.components.button;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.shared.Registration;
import org.vaadin.firitin.fluency.ui.*;
import org.vaadin.firitin.util.VStyleUtil.ThemeStyle;

import java.io.Serializable;

import static org.vaadin.firitin.util.VStyleUtil.applyOrElse;

public class VButton extends Button implements FluentHasSize<VButton>, FluentHasEnabled<VButton>,
        FluentClickNotifier<Button, VButton>, FluentHasText<VButton>, FluentFocusable<Button, VButton>,
        FluentComponent<VButton>, FluentHasStyle<VButton>, FluentThemableLayout<VButton>, FluentHasTooltip<VButton> {

    private ButtonSize size;
    private ButtonType type;
    private ButtonColor color;

    public enum ButtonType implements ThemeStyle<ButtonType> {
        PRIMARY("primary"), SECONDARY, TERTIARY("tertiary"), TERTIARY_INLINE("tertiary-inline");

        private String themeName;

        private ButtonType() {
            this("");
        }

        private ButtonType(String themeName) {
            this.themeName = themeName;
        }

        @Override
        public String getThemeName() {
            return themeName;
        }
    }

    public enum ButtonColor implements ThemeStyle<ButtonColor> {
        NONE, CONTRAST("contrast"), SUCCESS("success"), ERROR("error");

        private String color;

        private ButtonColor() {
            this("");
        }

        private ButtonColor(String color) {
            this.color = color;

        }

        @Override
        public String getThemeName() {
            return color;
        }
    }

    public enum ButtonSize implements ThemeStyle<ButtonSize> {
        SMALL("small"), NORMAL, LARGE("large");

        private String size;

        private ButtonSize() {
            this("");
        }

        private ButtonSize(String size) {
            this.size = size;
        }

        @Override
        public String getThemeName() {
            return size;
        }
    }

    public interface BasicClickListener extends Serializable {
        void onClick();
    }

    public VButton() {
        super();
    }

    public VButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(icon, clickListener);
    }

    /**
     * Creates a new Button
     *
     * @param icon the icon as Component
     * @param clickListener the listeners
     * @deprecated consider using some of the better typed version
     */
    public VButton(Component icon, BasicClickListener clickListener) {
        super(icon, e -> clickListener.onClick());
    }

    /**
     * Creates a new Button
     *
     * @param icon the icon as Component
     * @deprecated consider using some of the better typed version
     */
    @Deprecated
    public VButton(Component icon) {
        super(icon);
    }

    public VButton(Icon icon, BasicClickListener clickListener) {
        super(icon, e -> clickListener.onClick());
    }

    public VButton(Icon icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(icon, clickListener);
    }

    public VButton(Icon icon) {
        super(icon);
    }

    public VButton(VaadinIcon icon, BasicClickListener clickListener) {
        super(icon.create(), e -> clickListener.onClick());
    }

    public VButton(VaadinIcon icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(icon.create(), clickListener);
    }

    public VButton(VaadinIcon icon) {
        super(icon.create());
    }

    /**
     * Creates a new Button
     * @param text
     * @param icon
     * @param clickListener
     * @deprecated Use the versions with proper icon typing instead
     */
    @Deprecated
    public VButton(String text, Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, icon, clickListener);
    }
    public VButton(String text, Icon icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, icon, clickListener);
    }
    public VButton(String text, VaadinIcon icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, icon.create(), clickListener);
    }

    /**
     * Creates a new Button
     * @param icon the icon
     * @param text the text
     * @param clickListener the click listener
     * @deprecated Use the versions with proper icon typing instead
     */
    @Deprecated
    public VButton(Component icon, String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, icon, clickListener);
    }

    /**
     * Creates a new Button
     * @param icon the icon
     * @param text the text
     * @param clickListener the click listener
     */
    public VButton(Icon icon, String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, icon, clickListener);
    }
    public VButton(VaadinIcon icon, String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, icon.create(), clickListener);
    }

    /**
     * Creates a new button
     *
     * @param text the text
     * @param icon the icon
     * @param clickListener the listener
     * @deprecated Use the versions with better typed icon instead
     */
    @Deprecated
    public VButton(String text, Component icon, BasicClickListener clickListener) {
        super(text, icon, e -> clickListener.onClick());
    }

    public VButton(String text, Icon icon, BasicClickListener clickListener) {
        super(text, icon, e -> clickListener.onClick());
    }
    public VButton(String text, VaadinIcon icon, BasicClickListener clickListener) {
        super(text, icon.create(), e -> clickListener.onClick());
    }

    /**
     * Creates a new icon
     * @param text the text
     * @param icon the icon
     * @deprecated use the better typed versions instead
     */
    @Deprecated
    public VButton(String text, Component icon) {
        super(text, icon);
    }
    public VButton(String text, VaadinIcon icon) {
        super(text, icon.create());
    }
    public VButton(String text, Icon icon) {
        super(text, icon);
    }

    /**
     * Creates a new icon
     * @param icon the icon
     * @param text the text
     * @deprecated use the better typed versions instead
     */
    @Deprecated
    public VButton(Component icon, String text) {
        super(text, icon);
    }
    public VButton(VaadinIcon icon, String text) {
        super(text, icon.create());
    }
    public VButton(Icon icon, String text) {
        super(text, icon);
    }

    public VButton(String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, clickListener);
    }

    public VButton(String text, BasicClickListener clickListener) {
        super(text, e -> clickListener.onClick());
    }

    public VButton(String text) {
        super(text);
    }

    public void setType(ButtonType type) {
        this.type = type;
        applyOrElse(type, ButtonType.SECONDARY, this);
    }

    public VButton withType(ButtonType type) {
        setType(type);
        return this;
    }

    public ButtonType getType() {
        return type;
    }

    public void setSize(ButtonSize size) {
        this.size = size;
        applyOrElse(size, ButtonSize.NORMAL, this);
    }

    public VButton withSize(ButtonSize size) {
        setSize(size);
        return this;
    }

    public ButtonSize getSize() {
        return size;
    }

    public void setColor(ButtonColor color) {
        this.color = color;
        applyOrElse(color, ButtonColor.NONE, this);
    }

    public VButton withColor(ButtonColor color) {
        setColor(color);
        return this;
    }

    public ButtonColor getColor() {
        return color;
    }

    public VButton withIcon(Component icon) {
        setIcon(icon);
        return this;
    }

    public VButton withIconAfterText(boolean iconAfterText) {
        setIconAfterText(iconAfterText);
        return this;
    }

    public VButton withAutofocus(boolean autofocus) {
        setAutofocus(autofocus);
        return this;
    }

    public VButton withThemeVariants(ButtonVariant... variants) {
        addThemeVariants(variants);
        return this;
    }

    public Registration addClickListener(BasicClickListener clickListener) {
        return super.addClickListener(e -> clickListener.onClick());
    }

    public VButton onClick(BasicClickListener clickListener) {
        super.addClickListener(e -> clickListener.onClick());
        return this;
    }

    public VButton withClickShortcut(Key key, KeyModifier... keyModifiers) {
        addClickShortcut(key, keyModifiers);
        return this;
    }
}
