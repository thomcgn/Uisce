package de.irishphonetics.analysis.domain;

import java.util.List;
import java.util.Objects;

public record AnalysisResult(String word, String ipa, String pronunciationHint,
                             List<AnalyzedSegment> segments,
                             List<PronunciationEntry> pronunciations) {
    public AnalysisResult(String word, String ipa, String pronunciationHint,
                          List<AnalyzedSegment> segments) {
        this(word, ipa, pronunciationHint, segments, List.of());
    }

    public AnalysisResult {
        if (word == null || word.isBlank()) {
            throw new IllegalArgumentException("word must not be blank");
        }
        Objects.requireNonNull(ipa, "ipa");
        Objects.requireNonNull(pronunciationHint, "pronunciationHint");
        segments = List.copyOf(segments);
        pronunciations = List.copyOf(pronunciations);
        if (segments.isEmpty() && pronunciations.isEmpty()) {
            throw new IllegalArgumentException("analysis must contain segments or pronunciations");
        }
    }

    public static AnalysisResult fromLexicon(String word, List<PronunciationEntry> entries) {
        List<PronunciationEntry> variants = List.copyOf(entries);
        if (variants.isEmpty()) {
            throw new IllegalArgumentException("pronunciations must not be empty");
        }
        List<String> distinctIpa = variants.stream().map(PronunciationEntry::ipa).distinct().toList();
        String unambiguousIpa = distinctIpa.size() == 1 ? distinctIpa.getFirst() : "";
        return new AnalysisResult(word, unambiguousIpa, "", List.of(), variants);
    }
}
