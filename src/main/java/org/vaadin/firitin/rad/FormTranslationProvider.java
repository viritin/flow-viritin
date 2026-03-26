package org.vaadin.firitin.rad;

import java.util.Locale;

/**
 * A provider for translating form-related keys (field labels, button text, enum values, etc.).
 * Returns {@code null} if no translation is available, which triggers the default fallback behavior.
 */
@FunctionalInterface
public interface FormTranslationProvider {
    /**
     * Returns the translation for the given key and locale.
     *
     * @param key    the translation key
     * @param locale the locale to translate to
     * @return the translated string, or {@code null} if no translation is available
     */
    String getTranslation(String key, Locale locale);
}
