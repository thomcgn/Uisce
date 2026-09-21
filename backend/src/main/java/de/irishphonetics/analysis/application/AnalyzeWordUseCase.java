package de.irishphonetics.analysis.application;

import de.irishphonetics.analysis.domain.AnalysisResult;
import de.irishphonetics.analysis.domain.IrishPhoneticEngine;
import org.springframework.stereotype.Service;

@Service
public class AnalyzeWordUseCase {
    private final IrishPhoneticEngine engine;

    public AnalyzeWordUseCase(IrishPhoneticEngine engine) {
        this.engine = engine;
    }

    public AnalysisResult analyze(String word) {
        return engine.analyze(word);
    }
}
