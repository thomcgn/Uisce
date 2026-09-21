package de.irishphonetics.analysis.domain;

import java.util.List;
import java.util.Optional;

public interface PronunciationLexicon {
    List<PronunciationEntry> find(String normalizedWord);

    default Optional<SpellingAlias> findAlias(String normalizedWord) {
        return Optional.empty();
    }
}
