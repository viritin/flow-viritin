package org.vaadin.firitin.localization;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vaadin.firitin.fields.localized.Translator;

/**
 * Wires up the {@link Translator} used by the demo. If a {@code mistral.api-key}
 * is configured (e.g. as an environment variable {@code MISTRAL_API_KEY} or a
 * system/application property), a real {@link MistralTranslator} is injected;
 * otherwise the offline {@link DummyTranslator} is used so the feature can be
 * demonstrated without any credentials.
 */
@Configuration
public class TranslatorConfig {

    @Bean
    @ConditionalOnProperty(name = "mistral.api-key")
    Translator mistralTranslator(
            @org.springframework.beans.factory.annotation.Value("${mistral.api-key}") String apiKey) {
        return new MistralTranslator(apiKey);
    }

    @Bean
    @ConditionalOnMissingBean(Translator.class)
    Translator dummyTranslator() {
        return new DummyTranslator();
    }
}
