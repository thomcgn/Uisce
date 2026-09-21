package de.irishphonetics.analysis.pronunciation;

import java.util.List;
import java.util.Optional;

public interface PronunciationGuide {
    String languageTag();

    Optional<String> fromIpa(String ipa);

    default List<String> notes(String ipa) {
        return List.of();
    }
}
