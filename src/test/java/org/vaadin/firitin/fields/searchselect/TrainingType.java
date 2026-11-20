package org.vaadin.firitin.fields.searchselect;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * Test data from NettiHPK, the app this add-on was built for: a user's ~100
 * training types, e.g. "Juoksu – (PK1) kevyt" (sport and type).
 */
public record TrainingType(String sport, String name) {

    public String label() {
        return sport + " – " + name;
    }

    /** The training types of {@code training-types.tsv} (sport TAB name per line). */
    public static List<TrainingType> all() {
        try (var reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(TrainingType.class.getResourceAsStream("/training-types.tsv")),
                StandardCharsets.UTF_8))) {
            return reader.lines()
                    .filter(line -> !line.isBlank())
                    .map(line -> line.split("\t", 2))
                    .map(parts -> new TrainingType(parts[0], parts.length > 1 ? parts[1] : ""))
                    .toList();
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }
}
