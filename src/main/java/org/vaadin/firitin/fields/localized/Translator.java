package org.vaadin.firitin.fields.localized;

import java.util.Locale;

/**
 * Strategy for translating a piece of text from one language to another, used
 * by {@link LocalizedField} to fill in the language versions the user has not
 * typed in themselves.
 * <p>
 * The interface is intentionally independent of any particular translation
 * backend: implement it with an online translation service, a large language
 * model (e.g. via Spring AI), a dictionary, or anything else. Implementations
 * are allowed to block (do network I/O); {@link LocalizedField} invokes them
 * off the UI thread and applies the results via {@code UI.access(...)}.
 */
@FunctionalInterface
public interface Translator {

    /**
     * Translates the given text.
     *
     * @param text the text to translate, never {@code null}
     * @param from the language of {@code text}
     * @param to   the language to translate into
     * @return the translated text
     */
    String translate(String text, Locale from, Locale to);
}
