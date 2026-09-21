package de.irishphonetics.phonology.domain;

import java.util.Objects;

public record SoundRule(
        String grapheme,
        Quality quality,
        Mutation mutation,
        String ipa,
        String germanHint,
        String description
) {
    public SoundRule {
        if (grapheme == null || grapheme.isBlank()) {
            throw new IllegalArgumentException("grapheme must not be blank");
        }
        Objects.requireNonNull(quality, "quality");
        Objects.requireNonNull(mutation, "mutation");
        Objects.requireNonNull(ipa, "ipa");
        Objects.requireNonNull(germanHint, "germanHint");
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
    }
}
