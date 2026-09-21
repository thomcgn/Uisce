package de.irishphonetics.analysis.infrastructure;

import de.irishphonetics.analysis.domain.PronunciationEntry;
import de.irishphonetics.analysis.domain.PronunciationLexicon;
import de.irishphonetics.analysis.domain.ReviewStatus;
import de.irishphonetics.analysis.domain.SpellingAlias;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcPronunciationLexicon implements PronunciationLexicon {
    private final JdbcTemplate jdbc;

    public JdbcPronunciationLexicon(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<PronunciationEntry> find(String normalizedWord) {
        return jdbc.query("""
                SELECT display_word, ipa, dialects, source_url, review_status
                FROM pronunciation_entry
                WHERE normalized_word = ?
                ORDER BY ipa, dialects
                """, (rs, rowNum) -> new PronunciationEntry(
                rs.getString("display_word"),
                rs.getString("ipa"),
                rs.getString("dialects").isEmpty() ? List.of()
                        : Arrays.asList(rs.getString("dialects").split(",")),
                rs.getString("source_url"),
                ReviewStatus.valueOf(rs.getString("review_status"))),
                normalizedWord);
    }

    @Override
    public Optional<SpellingAlias> findAlias(String normalizedWord) {
        return jdbc.query("""
                SELECT canonical_word, explanation, source_url
                FROM spelling_alias
                WHERE normalized_alias = ?
                """, (rs, rowNum) -> new SpellingAlias(
                rs.getString("canonical_word"),
                rs.getString("explanation"),
                rs.getString("source_url")), normalizedWord).stream().findFirst();
    }
}
