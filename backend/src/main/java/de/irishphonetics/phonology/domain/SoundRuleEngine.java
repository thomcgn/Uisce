package de.irishphonetics.phonology.domain;

import java.util.Objects;
import java.util.Optional;

public final class SoundRuleEngine {
    private final PhoneticRuleSet ruleSet;

    public SoundRuleEngine(PhoneticRuleSet ruleSet) {
        this.ruleSet = Objects.requireNonNull(ruleSet, "ruleSet");
    }

    public Optional<SoundRule> resolve(PhoneticContext context) {
        return ruleSet.find(context);
    }
}
