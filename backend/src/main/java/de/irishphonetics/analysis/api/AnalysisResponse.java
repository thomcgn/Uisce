package de.irishphonetics.analysis.api;

import de.irishphonetics.analysis.domain.AnalysisResult;
import de.irishphonetics.analysis.domain.ReviewStatus;
import de.irishphonetics.phonology.domain.Mutation;
import de.irishphonetics.phonology.domain.Position;
import de.irishphonetics.phonology.domain.Quality;
import java.util.List;

public record AnalysisResponse(String word, String ipa, String pronunciationHint,
                               List<SegmentResponse> segments,
                               List<PronunciationResponse> pronunciations) {
    public static AnalysisResponse from(AnalysisResult result) {
        return new AnalysisResponse(result.word(), result.ipa(), result.pronunciationHint(),
                result.segments().stream().map(segment -> new SegmentResponse(
                        segment.context().grapheme(), segment.context().quality(),
                        segment.context().mutation(), segment.context().position(),
                        segment.rule().ipa(), segment.rule().germanHint(),
                        segment.rule().description())).toList(),
                result.pronunciations().stream().map(entry -> new PronunciationResponse(
                        entry.ipa(), entry.dialects(), entry.sourceUrl(),
                        entry.reviewStatus())).toList());
    }

    public record SegmentResponse(String grapheme, Quality quality, Mutation mutation,
                                  Position position, String ipa, String pronunciationHint,
                                  String description) {
    }

    public record PronunciationResponse(String ipa, List<String> dialects, String sourceUrl,
                                        ReviewStatus reviewStatus) {
    }
}
