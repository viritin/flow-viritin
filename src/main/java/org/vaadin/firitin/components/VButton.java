package org.vaadin.firitin.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import org.vaadin.firitin.fluency.ui.*;
import org.vaadin.firitin.util.VStyleUtil.ThemeStyle;

import static org.vaadin.firitin.util.VStyleUtil.applyOrElse;

public class VButton extends Button implements FluentHasSize<VButton>, FluentHasEnabled<VButton>,
        FluentClickNotifier<Button, VButton>, FluentHasText<VButton>, FluentFocusable<Button, VButton>,
        FluentComponent<VButton>, FluentHasElement<VButton>, FluentHasStyle<VButton> {

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

    public VButton() {
        super();
    }

    public VButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(icon, clickListener);
    }

    public VButton(Component icon) {
        super(icon);
    }

    public VButton(String text, Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, icon, clickListener);
    }

    public VButton(String text, Component icon) {
        super(text, icon);
    }

    public VButton(String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, clickListener);
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
}
