package de.irishphonetics.analysis.pronunciation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.junit.jupiter.api.Test;

class GermanPronunciationGuideLexiconTest {
    @Test
    void rendersEverySourcedIpaVariant() throws Exception {
        var guide = new GermanPronunciationGuide();
        List<String> missing = new ArrayList<>();
        int count = 0;
        try (var stream = getClass().getResourceAsStream(
                "/lexicon/irish-wiktionary-2026-09-02.tsv.gz");
             var lines = new BufferedReader(new InputStreamReader(
                     new GZIPInputStream(stream), StandardCharsets.UTF_8))) {
            String line;
            while ((line = lines.readLine()) != null) {
                String[] fields = line.split("\t", -1);
                count++;
                var rendered = guide.fromIpa(fields[2]);
                if (rendered.isEmpty() || !rendered.get().matches("[A-Za-zÄÖÜäöü -]+")) {
                    missing.add(fields[1] + " /" + fields[2] + "/");
                }
            }
        }
        assertThat(count).isEqualTo(14970);
        assertThat(missing).isEmpty();
    }
}
