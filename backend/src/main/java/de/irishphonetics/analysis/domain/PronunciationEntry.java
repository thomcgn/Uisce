package de.irishphonetics.analysis.domain;

import java.util.List;
import java.util.Objects;

public record PronunciationEntry(String word, String ipa, List<String> dialects,
                                 String sourceUrl, ReviewStatus reviewStatus) {
    public PronunciationEntry {
        if (word == null || word.isBlank() || ipa == null || ipa.isBlank()) {
            throw new IllegalArgumentException("word and ipa must not be blank");
        }
        dialects = List.copyOf(dialects);
        Objects.requireNonNull(sourceUrl, "sourceUrl");
        Objects.requireNonNull(reviewStatus, "reviewStatus");
    }
}
