package de.irishphonetics.analysis.infrastructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "uisce.lexicon.seed.enabled", havingValue = "true")
public class WiktionaryLexiconSeeder implements ApplicationRunner {
    private static final String SOURCE_NAME = "Wiktionary/Kaikki";
    private static final String SNAPSHOT = "2026-09-02";
    private static final String RESOURCE = "lexicon/irish-wiktionary-2026-09-02.tsv.gz";
    private static final int BATCH_SIZE = 500;

    private final JdbcTemplate jdbc;

    public WiktionaryLexiconSeeder(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Transactional(rollbackFor = IOException.class)
    public void run(org.springframework.boot.ApplicationArguments args) throws IOException {
        Integer existing = jdbc.queryForObject("""
                SELECT COUNT(*) FROM pronunciation_entry
                WHERE source_name = ? AND source_snapshot = ?
                """, Integer.class, SOURCE_NAME, SNAPSHOT);
        if (existing != null && existing > 0) {
            return;
        }

        try (var reader = new BufferedReader(new InputStreamReader(
                new GZIPInputStream(new ClassPathResource(RESOURCE).getInputStream()),
                StandardCharsets.UTF_8))) {
            List<String[]> batch = new ArrayList<>(BATCH_SIZE);
            for (String line; (line = reader.readLine()) != null;) {
                String[] fields = line.split("\t", -1);
                if (fields.length != 5) {
                    throw new IllegalStateException("Invalid lexicon row");
                }
                batch.add(fields);
                if (batch.size() == BATCH_SIZE) {
                    insert(batch);
                    batch.clear();
                }
            }
            insert(batch);
        }
    }

    private void insert(List<String[]> batch) {
        if (batch.isEmpty()) {
            return;
        }
        jdbc.batchUpdate("""
                INSERT INTO pronunciation_entry
                (normalized_word, display_word, ipa, dialects, source_url,
                 source_name, source_snapshot, review_status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """, batch, BATCH_SIZE, (statement, fields) -> {
            for (int i = 0; i < fields.length; i++) {
                statement.setString(i + 1, fields[i]);
            }
            statement.setString(6, SOURCE_NAME);
            statement.setString(7, SNAPSHOT);
            statement.setString(8, "SOURCED");
        });
    }
}
