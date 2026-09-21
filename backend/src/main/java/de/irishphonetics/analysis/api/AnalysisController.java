package de.irishphonetics.analysis.api;

import de.irishphonetics.analysis.application.AnalyzeWordUseCase;
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

    public AnalysisController(AnalyzeWordUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AnalysisResponse analyze(@Valid @RequestBody AnalysisRequest request) {
        return AnalysisResponse.from(useCase.analyze(request.word()));
    }
}
