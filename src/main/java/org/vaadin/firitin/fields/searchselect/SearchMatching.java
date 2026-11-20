package org.vaadin.firitin.fields.searchselect;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * The default matching rule of {@link SearchSelect}, in Java: an item matches
 * when its search text contains every word of the filter, in any order, ignoring
 * case and accents ("pk juo" matches "Juoksu – (PK1) kevyt", "paiva" matches
 * "Päivä"). The browser applies exactly the same rule in JavaScript, so server
 * side code (lazy data, tests) can filter consistently with the UI.
 */
public final class SearchMatching {

    private static final Pattern MARKS = Pattern.compile("\\p{M}+");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private SearchMatching() {
    }

    /** Whether {@code text} contains all words of {@code filter} (an empty filter matches everything). */
    public static boolean matchesAllWords(String text, String filter) {
        String haystack = normalize(text);
        String needle = normalize(filter).trim();
        return needle.isEmpty() || Arrays.stream(WHITESPACE.split(needle)).allMatch(haystack::contains);
    }

    /** Lower case without accents: the form texts are compared in. */
    public static String normalize(String text) {
        if (text == null) {
            return "";
        }
        return MARKS.matcher(Normalizer.normalize(text, Normalizer.Form.NFD)).replaceAll("").toLowerCase(Locale.ROOT);
    }
}
