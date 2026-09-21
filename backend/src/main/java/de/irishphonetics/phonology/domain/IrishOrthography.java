package de.irishphonetics.phonology.domain;

public final class IrishOrthography {
    private static final String BROAD_VOWELS = "aáoóuú";
    private static final String SLENDER_VOWELS = "eéií";

    private IrishOrthography() {
    }

    public static boolean isVowel(String grapheme) {
        return vowelQuality(grapheme) != null;
    }

    static Quality vowelQuality(String grapheme) {
        if (grapheme.length() != 1) {
            return null;
        }
        String lower = grapheme.toLowerCase(java.util.Locale.ROOT);
        if (BROAD_VOWELS.contains(lower)) {
            return Quality.BROAD;
        }
        if (SLENDER_VOWELS.contains(lower)) {
            return Quality.SLENDER;
        }
        return null;
    }
}
