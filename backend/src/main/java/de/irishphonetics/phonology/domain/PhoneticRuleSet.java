package de.irishphonetics.phonology.domain;

import java.util.Optional;

public interface PhoneticRuleSet {
    Optional<SoundRule> find(PhoneticContext context);
}
