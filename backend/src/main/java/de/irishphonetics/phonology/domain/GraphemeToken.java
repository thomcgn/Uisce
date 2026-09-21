package de.irishphonetics.phonology.domain;

import java.util.Locale;

public record GraphemeToken(String grapheme, int start, int end) {
    public GraphemeToken {
        if (grapheme == null || grapheme.isBlank()) {
            throw new IllegalArgumentException("grapheme must not be blank");
        }
        if (start < 0 || end <= start || end - start != grapheme.length()) {
            throw new IllegalArgumentException("invalid grapheme offsets");
        }
    }

    public String lowerCaseGrapheme() {
        return grapheme.toLowerCase(Locale.ROOT);
    }
}
