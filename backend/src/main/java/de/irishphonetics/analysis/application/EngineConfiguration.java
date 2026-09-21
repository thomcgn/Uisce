package de.irishphonetics.analysis.application;

import de.irishphonetics.analysis.domain.IrishPhoneticEngine;
import de.irishphonetics.analysis.domain.PronunciationLexicon;
import de.irishphonetics.analysis.domain.WordNormalizer;
import de.irishphonetics.phonology.domain.MutationDetector;
import de.irishphonetics.phonology.domain.QualityResolver;
import de.irishphonetics.phonology.domain.SoundRuleEngine;
import de.irishphonetics.phonology.domain.StandardIrishRuleSet;
import de.irishphonetics.phonology.domain.Tokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EngineConfiguration {
    @Bean
    IrishPhoneticEngine irishPhoneticEngine(PronunciationLexicon lexicon) {
        return new IrishPhoneticEngine(new WordNormalizer(), new Tokenizer(),
                new MutationDetector(), new QualityResolver(),
                new SoundRuleEngine(new StandardIrishRuleSet()), lexicon);
    }
}
