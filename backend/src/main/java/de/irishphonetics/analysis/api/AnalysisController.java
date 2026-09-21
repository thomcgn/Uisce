package de.irishphonetics.analysis.api;

import de.irishphonetics.analysis.application.AnalyzeWordUseCase;
import de.irishphonetics.analysis.pronunciation.PronunciationGuideRegistry;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analysis")
@CrossOrigin(origins = "${uisce.frontend.origin:http://localhost:3000}")
public class AnalysisController {
    private final AnalyzeWordUseCase useCase;
    private final PronunciationGuideRegistry guides;

    public AnalysisController(AnalyzeWordUseCase useCase, PronunciationGuideRegistry guides) {
        this.useCase = useCase;
        this.guides = guides;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AnalysisResponse analyze(@Valid @RequestBody AnalysisRequest request) {
        var guide = guides.forLanguage(request.nativeLanguage());
        return AnalysisResponse.from(useCase.analyze(request.word()), guide);
    }
}
