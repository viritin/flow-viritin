package org.vaadin.firitin.util;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.ThemeList;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class VStyleUtil {

    public static void inject(String css) {
        UI.getCurrent().getElement().executeJs("""
            var style = document.createElement('style');
            style.innerHTML = $0;
            document.head.append(style);
        """, css);
    }

    /**
     * Injects given CSS to the beginning of the head element, so it can be overridden by other styles
     * without strong selectors.
     *
     * @param css the CSS to inject
     */
    public static void injectAsFirst(String css) {
        UI.getCurrent().getElement().executeJs("""
            var style = document.createElement('style');
            style.innerHTML = $0;
            document.head.insertBefore(style, document.head.firstChild);
        """, css);
    }

    private VStyleUtil() {

    }

    public interface ThemeStyle<T extends Enum<T>> {
        public String getThemeName();

        default void applyTheme(HasElement component) {
            clearThemes(component);
            ThemeList themes = component.getElement().getThemeList();

            String themeName = getThemeName();
            if (!themeName.isEmpty()) {
                themes.add(themeName);
            }
        }

        default void clearThemes(HasElement component) {
            Objects.requireNonNull(component);
            Objects.requireNonNull(component.getElement());

            ThemeList themes = component.getElement().getThemeList();
            Stream.of(getClass().getEnumConstants()).forEach(theme -> themes.remove(theme.getThemeName()));
        }
    }

    public static <T extends Enum<T>> void applyOrElse(ThemeStyle<T> value, ThemeStyle<T> defaultValue,
            HasElement component) {
        Optional.ofNullable(value).orElse(defaultValue).applyTheme(component);
    }

    public enum FlexDirection {
        ROW, COLUMN
    }

    public static void setFlexShrink(double shrink, HasStyle component) {
        component.getStyle().set("flex-shrink", Double.toString(shrink));
    }

    public static void setFlexDirection(HasStyle component, FlexDirection direction) {
        if (direction == FlexDirection.ROW) {
            component.getStyle().set("flex-direction", "row");
        } else if (direction == FlexDirection.COLUMN) {
            component.getStyle().set("flex-direction", "column");
        }
    }
}
