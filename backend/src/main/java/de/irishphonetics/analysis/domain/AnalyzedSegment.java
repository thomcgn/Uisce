package de.irishphonetics.analysis.domain;

import de.irishphonetics.phonology.domain.PhoneticContext;
import de.irishphonetics.phonology.domain.SoundRule;
import java.util.Objects;

public record AnalyzedSegment(PhoneticContext context, SoundRule rule) {
    public AnalyzedSegment {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(rule, "rule");
    }
}
