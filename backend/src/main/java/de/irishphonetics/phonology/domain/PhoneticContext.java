package de.irishphonetics.phonology.domain;

import java.util.Objects;

public record PhoneticContext(
        String grapheme,
        Position position,
        Quality quality,
        Mutation mutation
) {
    public PhoneticContext {
        if (grapheme == null || grapheme.isBlank()) {
            throw new IllegalArgumentException("grapheme must not be blank");
        }
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(quality, "quality");
        Objects.requireNonNull(mutation, "mutation");
    }
}
