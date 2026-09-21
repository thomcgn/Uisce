package de.irishphonetics.analysis.domain;

import java.text.Normalizer;

public final class WordNormalizer {
    public String normalize(String word) {
        if (word == null || word.isBlank()) {
            throw new IllegalArgumentException("word must not be blank");
        }
        return Normalizer.normalize(word.strip(), Normalizer.Form.NFC);
    }
}
