package de.irishphonetics.analysis.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record AnalysisResult(String word, String ipa, String pronunciationHint,
                             List<AnalyzedSegment> segments,
                             List<PronunciationEntry> pronunciations,
                             Optional<SpellingAlias> spellingAlias,
                             Optional<OrthographicAnalysis> orthography) {
    public AnalysisResult(String word, String ipa, String pronunciationHint,
                          List<AnalyzedSegment> segments) {
        this(word, ipa, pronunciationHint, segments, List.of(), Optional.empty(), Optional.empty());
    }

    public AnalysisResult(String word, String ipa, String pronunciationHint,
                          List<AnalyzedSegment> segments,
                          List<PronunciationEntry> pronunciations) {
        this(word, ipa, pronunciationHint, segments, pronunciations, Optional.empty(), Optional.empty());
    }

    public AnalysisResult {
        if (word == null || word.isBlank()) {
            throw new IllegalArgumentException("word must not be blank");
        }
        Objects.requireNonNull(ipa, "ipa");
        Objects.requireNonNull(pronunciationHint, "pronunciationHint");
        segments = List.copyOf(segments);
        pronunciations = List.copyOf(pronunciations);
        Objects.requireNonNull(spellingAlias, "spellingAlias");
        Objects.requireNonNull(orthography, "orthography");
        if (segments.isEmpty() && pronunciations.isEmpty()) {
            throw new IllegalArgumentException("analysis must contain segments or pronunciations");
        }
    }

    public static AnalysisResult fromLexicon(String word, List<PronunciationEntry> entries) {
        return fromLexicon(word, entries, Optional.empty(), Optional.empty());
    }

    public static AnalysisResult fromLexicon(String word, List<PronunciationEntry> entries,
                                             Optional<OrthographicAnalysis> orthography) {
        return fromLexicon(word, entries, Optional.empty(), orthography);
    }

    public static AnalysisResult fromLexicon(String word, List<PronunciationEntry> entries,
                                             SpellingAlias alias) {
        return fromLexicon(word, entries, Optional.of(alias), Optional.empty());
    }

    public static AnalysisResult fromLexicon(String word, List<PronunciationEntry> entries,
                                             SpellingAlias alias,
                                             Optional<OrthographicAnalysis> orthography) {
        return fromLexicon(word, entries, Optional.of(alias), orthography);
    }

    private static AnalysisResult fromLexicon(String word, List<PronunciationEntry> entries,
                                              Optional<SpellingAlias> alias,
                                              Optional<OrthographicAnalysis> orthography) {
        List<PronunciationEntry> variants = List.copyOf(entries);
        if (variants.isEmpty()) {
            throw new IllegalArgumentException("pronunciations must not be empty");
        }
        List<String> distinctIpa = variants.stream().map(PronunciationEntry::ipa).distinct().toList();
        String unambiguousIpa = distinctIpa.size() == 1 ? distinctIpa.getFirst() : "";
        return new AnalysisResult(word, unambiguousIpa, "", List.of(), variants, alias, orthography);
    }
}
