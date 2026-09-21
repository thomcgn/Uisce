package de.irishphonetics.phonology.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Tokenizer {
    // The order matters: an eclipsed bhf must win over bh and b.
    private static final List<String> MULTI_LETTER_GRAPHEMES = List.of(
            "bhf", "bh", "ch", "dh", "fh", "gh", "mh", "ph", "sh", "th",
            "mb", "gc", "nd", "bp", "dt", "ng", "ts", "h-", "t-", "n-");

    public List<GraphemeToken> tokenize(String word) {
        if (word == null || word.isBlank()) {
            throw new IllegalArgumentException("word must not be blank");
        }

        String lower = word.toLowerCase(Locale.ROOT);
        List<GraphemeToken> tokens = new ArrayList<>();
        for (int index = 0; index < word.length();) {
            String match = null;
            for (String grapheme : MULTI_LETTER_GRAPHEMES) {
                if (lower.startsWith(grapheme, index)) {
                    match = grapheme;
                    break;
                }
            }

            int end = index + (match == null ? 1 : match.length());
            if (match == null && !Character.isLetter(word.charAt(index))) {
                throw new IllegalArgumentException("word contains unsupported character at index " + index);
            }
            tokens.add(new GraphemeToken(word.substring(index, end), index, end));
            index = end;
        }
        return List.copyOf(tokens);
    }
}
