package de.irishphonetics.analysis.pronunciation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GermanPronunciationGuideTest {
    private final GermanPronunciationGuide guide = new GermanPronunciationGuide();

    @Test
    void rendersCommonIrishSoundsAsApproximateGermanReadingAid() {
        assertThat(guide.fromIpa("dʲiə")).contains("Di-e");
        assertThat(guide.fromIpa("ʃeː")).contains("Schee");
        assertThat(guide.fromIpa("bˠɑːlʲə")).contains("Baale");
        assertThat(guide.fromIpa("tʲax")).contains("Tschach");
    }

    @Test
    void rendersBothSlainteDialectsWithoutInsertedJ() {
        assertThat(guide.fromIpa("ˈsˠl̪ˠaːn̠ʲtʲə")).contains("Slaan-che");
        assertThat(guide.fromIpa("ˈsˠl̪ˠɑːnʲtʲə")).contains("Slaan-che");
    }

    @Test
    void readsNasalizedAndRareSourcedSymbolsAsApproximations() {
        assertThat(guide.fromIpa("ˈĩːçə")).contains("Ieche");
        assertThat(guide.fromIpa("ˈãulˠ")).contains("Aul");
        assertThat(guide.notes("ˈĩːçə"))
                .contains("ch wie in ich", "nasal: Luft auch durch die Nase strömen lassen");
        assertThat(guide.notes("ax"))
                .containsExactly("ch wie in Bach");
    }

    @Test
    void distinguishesDschungelSoundFromIrishPalatalG() {
        assertThat(guide.fromIpa("dʒɔː")).contains("Dschoo");
        assertThat(guide.notes("dʒɔː"))
                .containsExactly("dsch wie in Dschungel (englisch jaw)");
        assertThat(guide.fromIpa("ˈd͡ʒ⁽ˠ⁾akiː")).contains("Dschakie");
        assertThat(guide.notes("ˈd͡ʒ⁽ˠ⁾akiː"))
                .containsExactly("dsch wie in Dschungel (englisch jaw)");
        assertThat(guide.fromIpa("ɟ")).contains("G");
        assertThat(guide.notes("ɟ"))
                .containsExactly("weiches g am Gaumen; nicht dsch wie in Dschungel");
    }

    @Test
    void refusesUnknownOrIncompleteIpa() {
        assertThat(guide.fromIpa("θ")).isEmpty();
        assertThat(guide.fromIpa("ː")).isEmpty();
        assertThat(guide.fromIpa("")).isEmpty();
    }
}
