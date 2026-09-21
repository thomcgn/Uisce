package de.irishphonetics.analysis.domain;

import de.irishphonetics.phonology.domain.GraphemeToken;
import de.irishphonetics.phonology.domain.MutationDetector;
import de.irishphonetics.phonology.domain.Position;
import de.irishphonetics.phonology.domain.QualityResolver;
import de.irishphonetics.phonology.domain.Tokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class OrthographicAnalyzer {
    private final Tokenizer tokenizer;
    private final MutationDetector mutationDetector;
    private final QualityResolver qualityResolver;

    public OrthographicAnalyzer(Tokenizer tokenizer, MutationDetector mutationDetector,
                                QualityResolver qualityResolver) {
        this.tokenizer = tokenizer;
        this.mutationDetector = mutationDetector;
        this.qualityResolver = qualityResolver;
    }

    public Optional<OrthographicAnalysis> analyze(String word) {
        List<GraphemeToken> tokens;
        try {
            tokens = tokenizer.tokenize(word);
        } catch (IllegalArgumentException unsupportedSpelling) {
            return Optional.empty();
        }
        List<OrthographicSegment> segments = new ArrayList<>();
        for (int index = 0; index < tokens.size(); index++) {
            Position position = index == 0 ? Position.INITIAL
                    : index == tokens.size() - 1 ? Position.FINAL : Position.MEDIAL;
            segments.add(new OrthographicSegment(tokens.get(index).grapheme(), position,
                    qualityResolver.resolve(tokens, index), mutationDetector.detect(tokens, index)));
        }
        return Optional.of(new OrthographicAnalysis(word, segments));
    }
}
