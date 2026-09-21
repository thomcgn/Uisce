package de.irishphonetics.analysis.domain;

import de.irishphonetics.phonology.domain.GraphemeToken;
import de.irishphonetics.phonology.domain.IrishOrthography;
import de.irishphonetics.phonology.domain.MutationDetector;
import de.irishphonetics.phonology.domain.PhoneticContext;
import de.irishphonetics.phonology.domain.Position;
import de.irishphonetics.phonology.domain.QualityResolver;
import de.irishphonetics.phonology.domain.SoundRule;
import de.irishphonetics.phonology.domain.SoundRuleEngine;
import de.irishphonetics.phonology.domain.Tokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class IrishPhoneticEngine {
    private final WordNormalizer normalizer;
    private final Tokenizer tokenizer;
    private final MutationDetector mutationDetector;
    private final QualityResolver qualityResolver;
    private final SoundRuleEngine soundRuleEngine;
    private final PronunciationLexicon lexicon;

    public IrishPhoneticEngine(WordNormalizer normalizer, Tokenizer tokenizer,
                              MutationDetector mutationDetector, QualityResolver qualityResolver,
                              SoundRuleEngine soundRuleEngine) {
        this(normalizer, tokenizer, mutationDetector, qualityResolver, soundRuleEngine,
                word -> List.of());
    }

    public IrishPhoneticEngine(WordNormalizer normalizer, Tokenizer tokenizer,
                              MutationDetector mutationDetector, QualityResolver qualityResolver,
                              SoundRuleEngine soundRuleEngine, PronunciationLexicon lexicon) {
        this.normalizer = Objects.requireNonNull(normalizer, "normalizer");
        this.tokenizer = Objects.requireNonNull(tokenizer, "tokenizer");
        this.mutationDetector = Objects.requireNonNull(mutationDetector, "mutationDetector");
        this.qualityResolver = Objects.requireNonNull(qualityResolver, "qualityResolver");
        this.soundRuleEngine = Objects.requireNonNull(soundRuleEngine, "soundRuleEngine");
        this.lexicon = Objects.requireNonNull(lexicon, "lexicon");
    }

    public AnalysisResult analyze(String input) {
        String word = normalizer.normalize(input);
        List<PronunciationEntry> knownPronunciations = lexicon.find(word.toLowerCase(java.util.Locale.ROOT));
        if (!knownPronunciations.isEmpty()) {
            return AnalysisResult.fromLexicon(word, knownPronunciations);
        }
        List<GraphemeToken> tokens = tokenizer.tokenize(word);
        long vowelLetters = tokens.stream()
                .filter(token -> IrishOrthography.isVowel(token.grapheme())).count();
        if (vowelLetters != 1) {
            // TODO: Resolve silent quality markers, diphthongs and multi-syllable stress.
            throw new UnsupportedAnalysisException("Vokalfolgen und mehrsilbige Wörter sind noch nicht abgedeckt");
        }
        List<AnalyzedSegment> segments = new ArrayList<>();

        for (int index = 0; index < tokens.size(); index++) {
            GraphemeToken token = tokens.get(index);
            var quality = qualityResolver.resolve(tokens, index)
                    .orElseThrow(() -> new UnsupportedAnalysisException(
                            "Unklare Vokalumgebung bei " + token.grapheme()));
            var context = new PhoneticContext(token.grapheme(), position(index, tokens.size()),
                    quality, mutationDetector.detect(tokens, index));
            SoundRule rule = soundRuleEngine.resolve(context)
                    .orElseThrow(() -> new UnsupportedAnalysisException(
                            "Keine gesicherte Lautregel für " + token.grapheme()));
            segments.add(new AnalyzedSegment(context, rule));
        }

        String ipa = segments.stream().map(segment -> segment.rule().ipa())
                .collect(Collectors.joining());
        String hint = segments.stream().map(segment -> segment.rule().germanHint())
                .filter(part -> !part.isBlank()).collect(Collectors.joining(" "));
        return new AnalysisResult(word, ipa, hint, segments);
    }

    private Position position(int index, int count) {
        if (index == 0) {
            return Position.INITIAL;
        }
        return index == count - 1 ? Position.FINAL : Position.MEDIAL;
    }
}
