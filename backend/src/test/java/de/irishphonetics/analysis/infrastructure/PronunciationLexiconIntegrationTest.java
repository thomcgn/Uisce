package de.irishphonetics.analysis.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import de.irishphonetics.analysis.domain.IrishPhoneticEngine;
import de.irishphonetics.analysis.domain.ReviewStatus;
import de.irishphonetics.phonology.domain.Mutation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(properties = "uisce.lexicon.seed.enabled=true")
class PronunciationLexiconIntegrationTest {
    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private JdbcPronunciationLexicon lexicon;

    @Autowired
    private IrishPhoneticEngine engine;

    @Autowired
    private WiktionaryLexiconSeeder seeder;

    @Test
    void loadsSourcedPronunciationsAndKeepsDialectAlternatives() {
        Integer rows = jdbc.queryForObject("SELECT COUNT(*) FROM pronunciation_entry",
                Integer.class);
        assertThat(rows).isEqualTo(14970);

        var baile = engine.analyze("baile");
        assertThat(baile.ipa()).isEmpty();
        assertThat(baile.pronunciations()).hasSizeGreaterThan(1);
        assertThat(baile.pronunciations()).allSatisfy(pronunciation -> {
            assertThat(pronunciation.reviewStatus()).isEqualTo(ReviewStatus.SOURCED);
            assertThat(pronunciation.sourceUrl()).contains("wiktionary.org");
        });
    }

    @Test
    void exposesUnambiguousSourceIpaWithoutInventingSegments() {
        var dia = engine.analyze("Dia");

        assertThat(dia.ipa()).isEqualTo("dʲiə");
        assertThat(dia.segments()).isEmpty();
        assertThat(dia.pronunciations()).isNotEmpty();
        assertThat(lexicon.find("dia")).isNotEmpty();
    }

    @Test
    void sourceCoversTheEightInitialRegressionWords() {
        for (String word : new String[] {
                "baile", "mbaile", "bhfuil", "oíche", "Sláinte", "Dia", "teach", "tsráid"}) {
            assertThat(engine.analyze(word).pronunciations()).as(word).isNotEmpty();
        }
    }

    @Test
    void recognizesMutationPatternsEvenWhenIpaComesFromLexicon() {
        var eclipsed = engine.analyze("bhfuil");
        assertThat(eclipsed.pronunciations()).isNotEmpty();
        assertThat(eclipsed.segments()).isEmpty();
        assertThat(eclipsed.orthography().orElseThrow().segments().getFirst().grapheme())
                .isEqualTo("bhf");
        assertThat(eclipsed.orthography().orElseThrow().segments().getFirst().mutation())
                .isEqualTo(Mutation.ECLIPSIS);
    }

    @Test
    void resolvesObsoleteSidheThroughItsSourcedModernSpelling() {
        var result = engine.analyze("Sidhe");

        assertThat(result.word()).isEqualTo("Sidhe");
        assertThat(result.ipa()).isEqualTo("ʃiː");
        assertThat(result.spellingAlias()).isPresent();
        assertThat(result.spellingAlias().orElseThrow().canonicalWord()).isEqualTo("sí");
        assertThat(result.spellingAlias().orElseThrow().sourceUrl())
                .isEqualTo("https://en.wiktionary.org/wiki/sidhe#Irish");
        assertThat(result.pronunciations()).isNotEmpty();
        assertThat(result.orthography().orElseThrow().word()).isEqualTo("sí");
        assertThat(result.orthography().orElseThrow().segments())
                .noneMatch(segment -> segment.mutation() == Mutation.LENITION);
    }

    @Test
    void repeatedSeedDoesNotDuplicatePronunciations() throws Exception {
        seeder.run(null);

        Integer rows = jdbc.queryForObject("SELECT COUNT(*) FROM pronunciation_entry", Integer.class);
        assertThat(rows).isEqualTo(14970);
    }
}
