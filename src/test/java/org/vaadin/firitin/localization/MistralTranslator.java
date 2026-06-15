package org.vaadin.firitin.localization;

import org.vaadin.firitin.fields.localized.Translator;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A real {@link Translator} that asks Mistral AI's chat completion API to
 * translate the text. Intentionally implemented with the plain JDK HTTP client
 * and Jackson rather than Spring AI, to keep the add-on's API free of any
 * particular AI library — an application is free to plug in a Spring AI based
 * implementation instead.
 * <p>
 * Used in the demo only when a {@code mistral.api-key} is configured (see
 * {@link TranslatorConfig}); otherwise {@link DummyTranslator} is used.
 */
public class MistralTranslator implements Translator {

    private static final String ENDPOINT = "https://api.mistral.ai/v1/chat/completions";

    private final String apiKey;
    private final String model;
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public MistralTranslator(String apiKey) {
        this(apiKey, "mistral-small-latest");
    }

    public MistralTranslator(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public String translate(String text, Locale from, Locale to) {
        if (text == null || text.isBlank()) {
            return "";
        }
        try {
            String body = mapper.writeValueAsString(Map.of(
                    "model", model,
                    "temperature", 0.2,
                    "messages", List.of(
                            Map.of("role", "system", "content",
                                    "You are a translation engine. Translate the user's"
                                    + " message from " + from.getDisplayLanguage(Locale.ENGLISH)
                                    + " to " + to.getDisplayLanguage(Locale.ENGLISH)
                                    + ". Reply with the translation only, no quotes,"
                                    + " no explanations."),
                            Map.of("role", "user", "content", text))));

            HttpRequest request = HttpRequest.newBuilder(URI.create(ENDPOINT))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = http.send(request,
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                throw new RuntimeException("Mistral API returned HTTP "
                        + response.statusCode() + ": " + response.body());
            }
            JsonNode root = mapper.readTree(response.body());
            return root.path("choices").path(0).path("message").path("content")
                    .asText().trim();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Translation request failed", e);
        }
    }
}
