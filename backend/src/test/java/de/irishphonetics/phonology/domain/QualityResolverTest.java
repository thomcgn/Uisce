package de.irishphonetics.phonology.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class QualityResolverTest {
    private final Tokenizer tokenizer = new Tokenizer();
    private final QualityResolver resolver = new QualityResolver();

    @Test
    void usesNearestVowelEnvironmentForConsonants() {
        var baile = tokenizer.tokenize("baile");

        assertThat(resolver.resolve(baile, 0)).contains(Quality.BROAD);
        assertThat(resolver.resolve(baile, 3)).contains(Quality.SLENDER);
    }

    @Test
    void resolvesInitialClusterFromFollowingVowel() {
        var tsraid = tokenizer.tokenize("tsráid");

        assertThat(resolver.resolve(tsraid, 0)).contains(Quality.BROAD);
        assertThat(resolver.resolve(tokenizer.tokenize("teach"), 0)).contains(Quality.SLENDER);
    }

    @Test
    void fadaKeepsItsVowelQuality() {
        var oiche = tokenizer.tokenize("oíche");

        assertThat(resolver.resolve(oiche, 1)).contains(Quality.SLENDER);
        assertThat(resolver.resolve(oiche, 2)).contains(Quality.SLENDER);
        assertThat(resolver.resolve(tokenizer.tokenize("Sláinte"), 2)).contains(Quality.BROAD);
    }

    @Test
    void leavesContradictoryOrMissingVowelEnvironmentUnresolved() {
        assertThat(resolver.resolve(tokenizer.tokenize("abe"), 1)).isEmpty();
        assertThat(resolver.resolve(tokenizer.tokenize("brr"), 0)).isEmpty();
    }
}
