package de.irishphonetics.phonology.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class PhonologyDomainTest {

    @Test
    void contextPreservesFadaAndExplicitCategories() {
        var context = new PhoneticContext("á", Position.INITIAL, Quality.BROAD, Mutation.NONE);

        assertThat(context.grapheme()).isEqualTo("á");
        assertThat(context.position()).isEqualTo(Position.INITIAL);
        assertThat(context.quality()).isEqualTo(Quality.BROAD);
        assertThat(context.mutation()).isEqualTo(Mutation.NONE);
    }

    @Test
    void mutationCategoriesCoverPlannedPrefixForms() {
        assertThat(Mutation.values()).contains(
                Mutation.LENITION, Mutation.ECLIPSIS,
                Mutation.H_PREFIX, Mutation.T_PREFIX, Mutation.N_PREFIX);
    }

    @Test
    void contextRejectsMissingGraphemeOrClassification() {
        assertThatThrownBy(() -> new PhoneticContext(" ", Position.INITIAL, Quality.BROAD, Mutation.NONE))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatNullPointerException()
                .isThrownBy(() -> new PhoneticContext("b", Position.INITIAL, null, Mutation.NONE));
    }

    @Test
    void soundRuleKeepsIpaSeparateFromGermanHint() {
        var rule = new SoundRule("b", Quality.BROAD, Mutation.NONE, "bˠ", "b", "Breites b");

        assertThat(rule.ipa()).isEqualTo("bˠ");
        assertThat(rule.germanHint()).isEqualTo("b");
        assertThat(rule.description()).isEqualTo("Breites b");
    }

    @Test
    void soundRuleCanRepresentSilentGraphemeWithoutPlaceholder() {
        var rule = new SoundRule("fh", Quality.BROAD, Mutation.LENITION, "", "", "Stummes fh");

        assertThat(rule.ipa()).isEmpty();
        assertThat(rule.germanHint()).isEmpty();
    }

    @Test
    void soundRuleRejectsMissingContent() {
        assertThatThrownBy(() -> new SoundRule("", Quality.BROAD, Mutation.NONE, "b", "b", "Breites b"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatNullPointerException()
                .isThrownBy(() -> new SoundRule("b", Quality.BROAD, Mutation.NONE, null, "b", "Breites b"));
    }
}
