package de.irishphonetics.analysis.pronunciation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public final class PronunciationGuideRegistry {
    private final Map<String, PronunciationGuide> guides;

    public PronunciationGuideRegistry(List<PronunciationGuide> guides) {
        this.guides = guides.stream().collect(Collectors.toUnmodifiableMap(
                PronunciationGuide::languageTag, Function.identity()));
    }

    public Optional<PronunciationGuide> forLanguage(String languageTag) {
        if (languageTag == null || languageTag.isBlank()) {
            return Optional.of(guides.get("de"));
        }
        if (languageTag.equals("none")) {
            return Optional.empty();
        }
        PronunciationGuide guide = guides.get(languageTag);
        if (guide == null) {
            throw new UnsupportedLanguageException(languageTag);
        }
        return Optional.of(guide);
    }
}
