package de.irishphonetics.analysis.domain;

import java.util.List;

public interface PronunciationLexicon {
    List<PronunciationEntry> find(String normalizedWord);
}
