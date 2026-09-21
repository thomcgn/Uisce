package de.irishphonetics.phonology.domain;

import java.util.List;
import java.util.Optional;

public final class QualityResolver {

    public Optional<Quality> resolve(List<GraphemeToken> tokens, int index) {
        if (tokens == null || index < 0 || index >= tokens.size()) {
            throw new IllegalArgumentException("invalid token index");
        }

        Quality ownVowel = IrishOrthography.vowelQuality(tokens.get(index).grapheme());
        if (ownVowel != null) {
            return Optional.of(ownVowel);
        }

        Quality left = nearestVowel(tokens, index, -1);
        Quality right = nearestVowel(tokens, index, 1);
        if (left != null && right != null && left != right) {
            return Optional.empty();
        }
        return Optional.ofNullable(right != null ? right : left);
    }

    private Quality nearestVowel(List<GraphemeToken> tokens, int index, int direction) {
        for (int i = index + direction; i >= 0 && i < tokens.size(); i += direction) {
            Quality quality = IrishOrthography.vowelQuality(tokens.get(i).grapheme());
            if (quality != null) {
                return quality;
            }
        }
        return null;
    }
}
