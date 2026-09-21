package de.irishphonetics.phonology.domain;

import java.util.List;
import java.util.Set;

public final class MutationDetector {
    private static final Set<String> LENITED_GRAPHEMES = Set.of(
            "bh", "ch", "dh", "fh", "gh", "mh", "ph", "sh", "th");
    private static final Set<String> ECLIPSED_GRAPHEMES = Set.of(
            "mb", "gc", "nd", "bhf", "bp", "dt", "ng");

    public Mutation detect(List<GraphemeToken> tokens, int index) {
        if (tokens == null || index < 0 || index >= tokens.size()) {
            throw new IllegalArgumentException("invalid token index");
        }

        String grapheme = tokens.get(index).lowerCaseGrapheme();
        if (index == 0 && ECLIPSED_GRAPHEMES.contains(grapheme)) {
            return Mutation.ECLIPSIS;
        }
        if (LENITED_GRAPHEMES.contains(grapheme)) {
            return Mutation.LENITION;
        }
        if (index == 0) {
            return switch (grapheme) {
                case "h-" -> Mutation.H_PREFIX;
                case "t-", "ts" -> Mutation.T_PREFIX;
                case "n-" -> Mutation.N_PREFIX;
                default -> Mutation.NONE;
            };
        }
        return Mutation.NONE;
    }
}
