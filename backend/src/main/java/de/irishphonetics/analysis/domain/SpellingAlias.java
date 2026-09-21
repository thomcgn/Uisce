package de.irishphonetics.analysis.domain;

import java.util.Objects;

public record SpellingAlias(String canonicalWord, String explanation, String sourceUrl) {
    public SpellingAlias {
        if (canonicalWord == null || canonicalWord.isBlank()) {
            throw new IllegalArgumentException("canonicalWord must not be blank");
        }
        Objects.requireNonNull(explanation, "explanation");
        Objects.requireNonNull(sourceUrl, "sourceUrl");
    }
}
