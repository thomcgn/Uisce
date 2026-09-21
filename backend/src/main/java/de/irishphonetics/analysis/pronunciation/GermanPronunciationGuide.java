package de.irishphonetics.analysis.pronunciation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** A reading aid for German speakers, not a phonetic transcription. */
@Component
public final class GermanPronunciationGuide implements PronunciationGuide {
    private static final Map<Integer, String> SOUNDS = Map.ofEntries(
            Map.entry((int) 'a', "a"), Map.entry((int) 'ɑ', "a"),
            Map.entry((int) 'æ', "ä"), Map.entry((int) 'e', "e"),
            Map.entry((int) 'ɛ', "ä"), Map.entry((int) 'ə', "e"),
            Map.entry((int) 'i', "i"), Map.entry((int) 'ɪ', "i"),
            Map.entry((int) 'o', "o"), Map.entry((int) 'ɔ', "o"),
            Map.entry((int) 'u', "u"), Map.entry((int) 'ʊ', "u"),
            Map.entry((int) 'ɞ', "ö"), Map.entry((int) 'ɨ', "ü"),
            Map.entry((int) 'ɯ', "u"), Map.entry((int) 'ʌ', "a"),
            Map.entry((int) 'ɤ', "ö"), Map.entry((int) 'ã', "a"),
            Map.entry((int) 'õ', "o"), Map.entry((int) 'ĩ', "i"),
            Map.entry((int) 'ũ', "u"), Map.entry((int) 'ẽ', "e"),
            Map.entry((int) 'b', "b"), Map.entry((int) 'c', "k"),
            Map.entry((int) 'd', "d"), Map.entry((int) 'f', "f"),
            Map.entry((int) 'ɡ', "g"), Map.entry((int) 'g', "g"),
            Map.entry((int) 'h', "h"), Map.entry((int) 'j', "j"),
            Map.entry((int) 'k', "k"), Map.entry((int) 'l', "l"),
            Map.entry((int) 'm', "m"), Map.entry((int) 'n', "n"),
            Map.entry((int) 'ŋ', "ng"), Map.entry((int) 'ɲ', "n"),
            Map.entry((int) 'p', "p"), Map.entry((int) 'r', "r"),
            Map.entry((int) 'ɾ', "r"), Map.entry((int) 's', "s"),
            Map.entry((int) 'z', "s"),
            Map.entry((int) 'ʃ', "sch"), Map.entry((int) 't', "t"),
            Map.entry((int) 'ʒ', "sch"),
            Map.entry((int) 'v', "w"), Map.entry((int) 'w', "w"),
            Map.entry((int) 'ṽ', "w"),
            Map.entry((int) 'x', "ch"), Map.entry((int) 'ç', "ch"),
            Map.entry((int) 'ɣ', "ch"), Map.entry((int) 'ɟ', "g"),
            Map.entry((int) 'ʝ', "j"));

    @Override
    public String languageTag() {
        return "de";
    }

    @Override
    public Optional<String> fromIpa(String ipa) {
        if (ipa == null || ipa.isBlank()) {
            return Optional.empty();
        }
        String readableIpa = ipa.replace("ˠ", "").replace("̪", "").replace("̠", "")
                .replace("̯", "").replace("̞", "").replace("̥", "")
                .replace("̃", "").replace("̹", "").replace("͈", "")
                .replace("ʷ", "").replace("ˀ", "").replace("͡", "")
                .replace("⁽", "").replace("⁾", "");
        StringBuilder hint = new StringBuilder();
        String lastVowel = null;
        for (int offset = 0; offset < readableIpa.length();) {
            if (readableIpa.startsWith("nʲtʲə", offset)) {
                hint.append("n-che");
                offset += "nʲtʲə".length();
                lastVowel = null;
                continue;
            }
            if (readableIpa.startsWith("iə", offset)) {
                hint.append("i-e");
                offset += "iə".length();
                lastVowel = null;
                continue;
            }
            if (readableIpa.startsWith("tʲ", offset)) {
                hint.append("tsch");
                offset += "tʲ".length();
                lastVowel = null;
                continue;
            }
            int codePoint = readableIpa.codePointAt(offset);
            offset += Character.charCount(codePoint);
            if (codePoint == 'ː') {
                if (lastVowel != null) {
                    hint.append(lastVowel.equals("i") ? "e" : lastVowel);
                } else if (hint.isEmpty()) {
                    return Optional.empty();
                }
                lastVowel = null;
            } else if (codePoint == 'ʲ') {
                lastVowel = null;
            } else if (codePoint == 'ˈ' || codePoint == 'ˌ') {
                lastVowel = null;
            } else if (codePoint == 'ˑ') {
                lastVowel = null;
            } else if (codePoint == 'ʔ') {
                hint.append('-');
                lastVowel = null;
            } else if (codePoint == '.') {
                hint.append('-');
                lastVowel = null;
            } else if (codePoint == '-' || codePoint == ' ') {
                hint.appendCodePoint(codePoint);
                lastVowel = null;
            } else {
                String sound = SOUNDS.get(codePoint);
                if (sound == null) return Optional.empty();
                hint.append(sound);
                lastVowel = isVowel(codePoint) ? sound : null;
            }
        }
        if (hint.isEmpty()) return Optional.empty();
        hint.setCharAt(0, Character.toUpperCase(hint.charAt(0)));
        return Optional.of(hint.toString());
    }

    @Override
    public List<String> notes(String ipa) {
        if (ipa == null || ipa.isBlank()) return List.of();
        List<String> notes = new ArrayList<>();
        String untied = ipa.replace("͡", "");
        if (untied.contains("dʒ")) {
            notes.add("dsch wie in Dschungel (englisch jaw)");
        } else if (ipa.indexOf('ʒ') >= 0) {
            notes.add("stimmhaftes sch wie in Genie");
        }
        if (untied.contains("tʃ")) {
            notes.add("tsch wie in Tschüss");
        } else if (ipa.indexOf('ʃ') >= 0) {
            notes.add("sch wie in Schule");
        }
        if (ipa.indexOf('ɟ') >= 0) {
            notes.add("weiches g am Gaumen; nicht dsch wie in Dschungel");
        }
        if (ipa.indexOf('j') >= 0) notes.add("j wie in Jahr");
        if (ipa.indexOf('x') >= 0) notes.add("ch wie in Bach");
        if (ipa.indexOf('ç') >= 0) notes.add("ch wie in ich");
        if (ipa.indexOf('ɣ') >= 0) notes.add("stimmhaftes ch; kein genauer deutscher Laut");
        if (ipa.codePoints().anyMatch(point -> "ãõĩũẽṽ̃".indexOf(point) >= 0)) {
            notes.add("nasal: Luft auch durch die Nase strömen lassen");
        }
        return List.copyOf(notes);
    }

    private static boolean isVowel(int codePoint) {
        return "aɑæeɛəiɪoɔuʊɞɨɯʌɤãõĩũẽ".indexOf(codePoint) >= 0;
    }
}
