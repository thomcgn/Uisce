package de.irishphonetics.analysis.api;

import de.irishphonetics.analysis.domain.AnalysisResult;
import de.irishphonetics.analysis.domain.ReviewStatus;
import de.irishphonetics.analysis.pronunciation.PronunciationGuide;
import de.irishphonetics.phonology.domain.Mutation;
import de.irishphonetics.phonology.domain.Position;
import de.irishphonetics.phonology.domain.Quality;
import java.util.List;
import java.util.Optional;

public record AnalysisResponse(String word, String ipa, String pronunciationHint,
                               List<String> pronunciationNotes,
                               SpellingResponse spelling,
                               OrthographyResponse orthography,
                               List<SegmentResponse> segments,
                               List<PronunciationResponse> pronunciations) {
    public static AnalysisResponse from(AnalysisResult result, Optional<PronunciationGuide> guide) {
        return new AnalysisResponse(result.word(), result.ipa(), hint(guide, result.ipa()),
                notes(guide, result.ipa()),
                result.spellingAlias().map(alias -> new SpellingResponse(
                        alias.canonicalWord(), alias.explanation(), alias.sourceUrl()))
                        .orElse(null),
                result.orthography().map(item -> new OrthographyResponse(item.word(),
                        item.segments().stream().map(segment -> new OrthographicSegmentResponse(
                                segment.grapheme(), segment.position(),
                                segment.quality().orElse(null), segment.mutation())).toList()))
                        .orElse(null),
                result.segments().stream().map(segment -> new SegmentResponse(
                        segment.context().grapheme(), segment.context().quality(),
                        segment.context().mutation(), segment.context().position(),
                        segment.rule().ipa(), hint(guide, segment.rule().ipa()),
                        segment.rule().description())).toList(),
                result.pronunciations().stream().map(entry -> new PronunciationResponse(
                        entry.ipa(), hint(guide, entry.ipa()), notes(guide, entry.ipa()),
                        entry.dialects(), entry.sourceUrl(),
                        entry.reviewStatus())).toList());
    }

    private static String hint(Optional<PronunciationGuide> guide, String ipa) {
        return guide.flatMap(item -> item.fromIpa(ipa)).orElse("");
    }

    private static List<String> notes(Optional<PronunciationGuide> guide, String ipa) {
        return guide.map(item -> item.notes(ipa)).orElseGet(List::of);
    }

    public record SegmentResponse(String grapheme, Quality quality, Mutation mutation,
                                  Position position, String ipa, String pronunciationHint,
                                  String description) {
    }

    public record OrthographyResponse(String word, List<OrthographicSegmentResponse> segments) {
    }

    public record OrthographicSegmentResponse(String grapheme, Position position,
                                              Quality quality, Mutation mutation) {
    }

    public record SpellingResponse(String canonicalWord, String explanation, String sourceUrl) {
    }

    public record PronunciationResponse(String ipa, String pronunciationHint,
                                        List<String> pronunciationNotes,
                                        List<String> dialects, String sourceUrl,
                                        ReviewStatus reviewStatus) {
    }
}
