package de.irishphonetics.phonology.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class TokenizerTest {
    private final Tokenizer tokenizer = new Tokenizer();

    @Test
    void longestMatchKeepsEclipsedBhfTogether() {
        List<GraphemeToken> tokens = tokenizer.tokenize("bhfuil");

        assertThat(tokens).extracting(GraphemeToken::grapheme)
                .containsExactly("bhf", "u", "i", "l");
        assertThat(tokens.getFirst()).isEqualTo(new GraphemeToken("bhf", 0, 3));
    }

    @Test
    void preservesCaseFadaAndOffsets() {
        List<GraphemeToken> tokens = tokenizer.tokenize("Oíche");

        assertThat(tokens).extracting(GraphemeToken::grapheme)
                .containsExactly("O", "í", "ch", "e");
        assertThat(tokens.get(2)).isEqualTo(new GraphemeToken("ch", 2, 4));
    }

    @Test
    void keepsOtherMutationGraphemesTogether() {
        assertThat(tokenizer.tokenize("mbaile")).extracting(GraphemeToken::grapheme)
                .containsExactly("mb", "a", "i", "l", "e");
        assertThat(tokenizer.tokenize("tsráid")).extracting(GraphemeToken::grapheme)
                .containsExactly("ts", "r", "á", "i", "d");
    }

    @Test
    void regressionWordsRoundTripWithoutChangingTheirSpelling() {
        for (String word : new String[] {
                "baile", "mbaile", "bhfuil", "oíche", "Sláinte", "Dia", "teach", "tsráid"}) {
            String reconstructed = tokenizer.tokenize(word).stream()
                    .map(GraphemeToken::grapheme).collect(java.util.stream.Collectors.joining());
            assertThat(reconstructed).isEqualTo(word);
        }
    }

    @Test
    void rejectsNonWordCharacters() {
        assertThatThrownBy(() -> tokenizer.tokenize("baile!"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> tokenizer.tokenize("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
