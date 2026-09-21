package de.irishphonetics.analysis.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.irishphonetics.phonology.domain.Mutation;
import de.irishphonetics.phonology.domain.MutationDetector;
import de.irishphonetics.phonology.domain.Position;
import de.irishphonetics.phonology.domain.Quality;
import de.irishphonetics.phonology.domain.QualityResolver;
import de.irishphonetics.phonology.domain.SoundRuleEngine;
import de.irishphonetics.phonology.domain.StandardIrishRuleSet;
import de.irishphonetics.phonology.domain.Tokenizer;
import org.junit.jupiter.api.Test;

class IrishPhoneticEngineTest {
    private final IrishPhoneticEngine engine = new IrishPhoneticEngine(
            new WordNormalizer(), new Tokenizer(), new MutationDetector(),
            new QualityResolver(), new SoundRuleEngine(new StandardIrishRuleSet()));

    @Test
    void analyzesSimpleWordWithBroadConsonantAndLongVowel() {
        AnalysisResult result = engine.analyze("bó");

        assertThat(result.ipa()).isEqualTo("bˠoː");
        assertThat(result.pronunciationHint()).isEqualTo("b langes o");
        assertThat(result.segments()).hasSize(2);
        assertThat(result.segments().getFirst().context().quality()).isEqualTo(Quality.BROAD);
        assertThat(result.segments().getFirst().context().position()).isEqualTo(Position.INITIAL);
    }

    @Test
    void analyzesSlenderConsonantAndPreservesFadaAfterNormalization() {
        AnalysisResult result = engine.analyze("  me\u0301  ");

        assertThat(result.word()).isEqualTo("mé");
        assertThat(result.ipa()).isEqualTo("mʲeː");
        assertThat(result.segments().getLast().context().grapheme()).isEqualTo("é");
    }

    @Test
    void analyzesEclipsedInitialAsOneSegment() {
        AnalysisResult result = engine.analyze("mbó");

        assertThat(result.ipa()).isEqualTo("mˠoː");
        assertThat(result.segments()).hasSize(2);
        assertThat(result.segments().getFirst().context().mutation()).isEqualTo(Mutation.ECLIPSIS);
        assertThat(result.segments().getFirst().context().grapheme()).isEqualTo("mb");
    }

    @Test
    void silentLenitionDoesNotInventAnIpaSymbol() {
        AnalysisResult result = engine.analyze("fhó");

        assertThat(result.ipa()).isEqualTo("oː");
        assertThat(result.segments().getFirst().rule().ipa()).isEmpty();
    }

    @Test
    void analyzesAdditionalReviewedConsonants() {
        assertThat(engine.analyze("cá").ipa()).isEqualTo("kˠaː");
        assertThat(engine.analyze("cé").ipa()).isEqualTo("kʲeː");
        assertThat(engine.analyze("tú").ipa()).isEqualTo("t̪ˠuː");
        assertThat(engine.analyze("ní").ipa()).isEqualTo("nʲiː");
        assertThat(engine.analyze("sé").ipa()).isEqualTo("ʃeː");
    }

    @Test
    void analyzesAdditionalEclipsedInitials() {
        assertThat(engine.analyze("gcá").ipa()).isEqualTo("ɡˠaː");
        assertThat(engine.analyze("bpó").ipa()).isEqualTo("bˠoː");
    }

    @Test
    void prefersSourcedPronunciationsAndKeepsVariantsSeparate() {
        var source = new PronunciationEntry("baile", "ˈbˠalʲə", java.util.List.of("Munster"),
                "https://en.wiktionary.org/wiki/baile#Irish", ReviewStatus.SOURCED);
        var alternative = new PronunciationEntry("baile", "ˈbˠælʲə", java.util.List.of("Connacht"),
                "https://en.wiktionary.org/wiki/baile#Irish", ReviewStatus.SOURCED);
        var withLexicon = new IrishPhoneticEngine(new WordNormalizer(), new Tokenizer(),
                new MutationDetector(), new QualityResolver(),
                new SoundRuleEngine(new StandardIrishRuleSet()), word -> java.util.List.of(source, alternative));

        AnalysisResult result = withLexicon.analyze("baile");

        assertThat(result.ipa()).isEmpty();
        assertThat(result.pronunciations()).containsExactly(source, alternative);
        assertThat(result.segments()).isEmpty();
    }

    @Test
    void detectsLenitionInLexiconWordWithoutInventingSegmentIpa() {
        var source = new PronunciationEntry("chonaic", "x", java.util.List.of(),
                "https://example.org/chonaic", ReviewStatus.SOURCED);
        var withLexicon = new IrishPhoneticEngine(new WordNormalizer(), new Tokenizer(),
                new MutationDetector(), new QualityResolver(),
                new SoundRuleEngine(new StandardIrishRuleSet()), word -> java.util.List.of(source));

        var result = withLexicon.analyze("chonaic");

        assertThat(result.orthography().orElseThrow().segments().getFirst().mutation())
                .isEqualTo(Mutation.LENITION);
        assertThat(result.segments()).isEmpty();
    }

    @Test
    void unsupportedWordsFailExplicitly() {
        assertThatThrownBy(() -> engine.analyze("lú"))
                .isInstanceOf(UnsupportedAnalysisException.class);
    }

    @Test
    void regressionWordsWaitForVerifiedVowelAndStressRules() {
        // TODO: Replace each expectation with reviewed IPA as rule coverage grows.
        for (String word : new String[] {
                "baile", "mbaile", "bhfuil", "oíche", "Sláinte", "Dia", "teach", "tsráid"}) {
            assertThatThrownBy(() -> engine.analyze(word))
                    .as(word).isInstanceOf(UnsupportedAnalysisException.class);
        }
    }

    @Test
    void rejectsBlankInput() {
        assertThatThrownBy(() -> engine.analyze("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
