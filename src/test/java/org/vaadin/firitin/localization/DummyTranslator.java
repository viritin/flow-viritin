package org.vaadin.firitin.localization;

import org.vaadin.firitin.fields.localized.Translator;

import java.util.Locale;

/**
 * A fake {@link Translator} used when no real translation backend is configured.
 * It does not actually translate anything; it just tags the text with the
 * target language so that wiring (and the asynchronous "Translating…" state)
 * is visible in the demo. A small artificial delay simulates the latency of a
 * real network/LLM call so that server push can be observed working.
 */
public class DummyTranslator implements Translator {

    @Override
    public String translate(String text, Locale from, Locale to) {
        if (text == null || text.isBlank()) {
            return "";
        }
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return text + " [" + to.getDisplayLanguage(Locale.ENGLISH).toLowerCase() + "]";
    }
}
