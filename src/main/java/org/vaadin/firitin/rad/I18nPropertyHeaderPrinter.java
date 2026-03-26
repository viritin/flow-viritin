package org.vaadin.firitin.rad;

import java.util.Locale;

/**
 * A {@link PropertyHeaderPrinter} that resolves field labels via a {@link FormTranslationProvider}.
 * Performs two-tier key lookup:
 * <ol>
 *     <li>{@code ClassName.propertyName} (bean-specific)</li>
 *     <li>{@code propertyName} (generic/shared)</li>
 * </ol>
 * Returns {@code null} if neither key resolves, so the existing deCamelCased fallback kicks in.
 */
public class I18nPropertyHeaderPrinter implements PropertyHeaderPrinter {

    private final FormTranslationProvider translationProvider;
    private final Locale locale;

    public I18nPropertyHeaderPrinter(FormTranslationProvider translationProvider, Locale locale) {
        this.translationProvider = translationProvider;
        this.locale = locale;
    }

    @Override
    public Object printHeader(PropertyContext ctx) {
        String propertyName = ctx.getName();
        String className = ctx.owner().beanDescription().getBeanClass().getSimpleName();

        // Try bean-specific key first
        String translation = translationProvider.getTranslation(className + "." + propertyName, locale);
        if (translation != null) {
            return translation;
        }

        // Try generic key
        translation = translationProvider.getTranslation(propertyName, locale);
        return translation; // null triggers fallback
    }
}
