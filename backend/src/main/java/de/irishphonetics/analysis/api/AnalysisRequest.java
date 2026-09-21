package de.irishphonetics.analysis.api;

import jakarta.validation.constraints.NotBlank;

public record AnalysisRequest(@NotBlank(message = "word darf nicht leer sein") String word) {
}
