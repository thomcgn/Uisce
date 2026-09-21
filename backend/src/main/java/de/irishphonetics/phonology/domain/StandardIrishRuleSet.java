package de.irishphonetics.phonology.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class StandardIrishRuleSet implements PhoneticRuleSet {
    private final Map<RuleKey, SoundRule> rules;

    public StandardIrishRuleSet() {
        Map<RuleKey, SoundRule> entries = new HashMap<>();
        addVowel(entries, "á", Quality.BROAD, "aː", "langes a");
        addVowel(entries, "é", Quality.SLENDER, "eː", "langes e");
        addVowel(entries, "í", Quality.SLENDER, "iː", "langes i");
        addVowel(entries, "ó", Quality.BROAD, "oː", "langes o");
        addVowel(entries, "ú", Quality.BROAD, "uː", "langes u");

        addPair(entries, "b", Mutation.NONE, "bˠ", "bʲ", "b", "b");
        addPair(entries, "m", Mutation.NONE, "mˠ", "mʲ", "m", "m");
        addPair(entries, "p", Mutation.NONE, "pˠ", "pʲ", "p", "p");
        addPair(entries, "f", Mutation.NONE, "fˠ", "fʲ", "f", "f");
        addPair(entries, "c", Mutation.NONE, "kˠ", "kʲ", "k", "k");
        addPair(entries, "g", Mutation.NONE, "ɡˠ", "ɡʲ", "g", "g");
        addPair(entries, "t", Mutation.NONE, "t̪ˠ", "tʲ", "t", "t");
        addPair(entries, "d", Mutation.NONE, "d̪ˠ", "dʲ", "d", "d");
        addPair(entries, "n", Mutation.NONE, "n̪ˠ", "nʲ", "n", "n");
        addPair(entries, "s", Mutation.NONE, "sˠ", "ʃ", "s", "sch");
        addPair(entries, "mb", Mutation.ECLIPSIS, "mˠ", "mʲ", "m", "m");
        addPair(entries, "gc", Mutation.ECLIPSIS, "ɡˠ", "ɡʲ", "g", "g");
        addPair(entries, "nd", Mutation.ECLIPSIS, "n̪ˠ", "nʲ", "n", "n");
        addPair(entries, "bp", Mutation.ECLIPSIS, "bˠ", "bʲ", "b", "b");
        addPair(entries, "dt", Mutation.ECLIPSIS, "d̪ˠ", "dʲ", "d", "d");
        addPair(entries, "ph", Mutation.LENITION, "fˠ", "fʲ", "f", "f");
        addPair(entries, "fh", Mutation.LENITION, "", "", "", "");
        addPair(entries, "sh", Mutation.LENITION, "h", "h", "h", "h");
        addPair(entries, "th", Mutation.LENITION, "h", "h", "h", "h");
        rules = Map.copyOf(entries);
    }

    @Override
    public Optional<SoundRule> find(PhoneticContext context) {
        Objects.requireNonNull(context, "context");
        return Optional.ofNullable(rules.get(new RuleKey(
                context.grapheme().toLowerCase(java.util.Locale.ROOT),
                context.quality(), context.mutation())));
    }

    private static void addVowel(Map<RuleKey, SoundRule> rules, String grapheme,
                                 Quality quality, String ipa, String hint) {
        add(rules, new SoundRule(grapheme, quality, Mutation.NONE, ipa, hint, "Langer Vokal"));
    }

    private static void addPair(Map<RuleKey, SoundRule> rules, String grapheme,
                                Mutation mutation, String broadIpa, String slenderIpa,
                                String broadHint, String slenderHint) {
        add(rules, new SoundRule(grapheme, Quality.BROAD, mutation, broadIpa,
                broadHint, description(grapheme, Quality.BROAD, mutation)));
        add(rules, new SoundRule(grapheme, Quality.SLENDER, mutation, slenderIpa,
                slenderHint, description(grapheme, Quality.SLENDER, mutation)));
    }

    private static String description(String grapheme, Quality quality, Mutation mutation) {
        if (mutation == Mutation.ECLIPSIS) {
            return "Eklipse: Der ursprüngliche Konsonant wird nicht gesprochen";
        }
        if (grapheme.equals("fh")) {
            return "Leniertes f ist stumm";
        }
        if (mutation == Mutation.LENITION) {
            return "Lenierter " + (quality == Quality.BROAD ? "breiter" : "schlanker") + " Konsonant";
        }
        return quality == Quality.BROAD ? "Breiter Konsonant" : "Schlanker Konsonant";
    }

    private static void add(Map<RuleKey, SoundRule> rules, SoundRule rule) {
        RuleKey key = new RuleKey(rule.grapheme(), rule.quality(), rule.mutation());
        if (rules.putIfAbsent(key, rule) != null) {
            throw new IllegalStateException("duplicate sound rule: " + key);
        }
    }

    private record RuleKey(String grapheme, Quality quality, Mutation mutation) {
    }
}
