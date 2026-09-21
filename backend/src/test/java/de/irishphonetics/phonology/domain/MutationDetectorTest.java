package de.irishphonetics.phonology.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MutationDetectorTest {
    private final Tokenizer tokenizer = new Tokenizer();
    private final MutationDetector detector = new MutationDetector();

    @Test
    void detectsEveryListedEclipsisAsOneInitialGrapheme() {
        for (String grapheme : new String[] {"mb", "gc", "nd", "bhf", "bp", "dt", "ng"}) {
            assertThat(detector.detect(tokenizer.tokenize(grapheme + "a"), 0))
                    .as(grapheme).isEqualTo(Mutation.ECLIPSIS);
        }
    }

    @Test
    void detectsListedLenitions() {
        for (String grapheme : new String[] {"bh", "ch", "dh", "fh", "gh", "mh", "ph", "sh", "th"}) {
            assertThat(detector.detect(tokenizer.tokenize(grapheme + "a"), 0))
                    .as(grapheme).isEqualTo(Mutation.LENITION);
        }
    }

    @Test
    void detectsExplicitPrefixSpellingsAndTsraid() {
        assertThat(detector.detect(tokenizer.tokenize("h-athair"), 0)).isEqualTo(Mutation.H_PREFIX);
        assertThat(detector.detect(tokenizer.tokenize("t-athair"), 0)).isEqualTo(Mutation.T_PREFIX);
        assertThat(detector.detect(tokenizer.tokenize("n-athair"), 0)).isEqualTo(Mutation.N_PREFIX);
        assertThat(detector.detect(tokenizer.tokenize("tsráid"), 0)).isEqualTo(Mutation.T_PREFIX);
    }

    @Test
    void doesNotCallMedialNgAnEclipsis() {
        assertThat(detector.detect(tokenizer.tokenize("anga"), 1)).isEqualTo(Mutation.NONE);
        assertThat(detector.detect(tokenizer.tokenize("baile"), 0)).isEqualTo(Mutation.NONE);
    }
}
