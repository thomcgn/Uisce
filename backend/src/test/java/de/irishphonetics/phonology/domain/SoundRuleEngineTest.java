package de.irishphonetics.phonology.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SoundRuleEngineTest {
    private final SoundRuleEngine engine = new SoundRuleEngine(new StandardIrishRuleSet());

    @Test
    void resolvesLongVowelAndSlenderConsonantSeparately() {
        var m = engine.resolve(new PhoneticContext("M", Position.INITIAL,
                Quality.SLENDER, Mutation.NONE));
        var e = engine.resolve(new PhoneticContext("é", Position.FINAL,
                Quality.SLENDER, Mutation.NONE));

        assertThat(m).get().extracting(SoundRule::ipa).isEqualTo("mʲ");
        assertThat(e).get().extracting(SoundRule::ipa).isEqualTo("eː");
    }

    @Test
    void eclipsisSpeaksTheCoveringConsonant() {
        var rule = engine.resolve(new PhoneticContext("mb", Position.INITIAL,
                Quality.BROAD, Mutation.ECLIPSIS));

        assertThat(rule).get().extracting(SoundRule::ipa).isEqualTo("mˠ");
    }

    @Test
    void fhIsExplicitlySilent() {
        var rule = engine.resolve(new PhoneticContext("fh", Position.INITIAL,
                Quality.BROAD, Mutation.LENITION));

        assertThat(rule).get().extracting(SoundRule::ipa).isEqualTo("");
    }

    @Test
    void missingOrMismatchedRuleDoesNotFallBackToPlaceholder() {
        assertThat(engine.resolve(new PhoneticContext("a", Position.INITIAL,
                Quality.BROAD, Mutation.NONE))).isEmpty();
        assertThat(engine.resolve(new PhoneticContext("mb", Position.INITIAL,
                Quality.BROAD, Mutation.NONE))).isEmpty();
    }
}
