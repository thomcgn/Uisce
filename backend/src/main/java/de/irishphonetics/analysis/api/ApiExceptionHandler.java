package de.irishphonetics.analysis.api;

import de.irishphonetics.analysis.domain.UnsupportedAnalysisException;
import de.irishphonetics.analysis.pronunciation.UnsupportedLanguageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> invalidWord() {
        return ResponseEntity.badRequest().body(new ApiError("INVALID_WORD", "word darf nicht leer sein"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> invalidJson() {
        return ResponseEntity.badRequest().body(new ApiError("INVALID_REQUEST", "Ungültige JSON-Anfrage"));
    }

    @ExceptionHandler(UnsupportedAnalysisException.class)
    public ResponseEntity<ApiError> unsupported(UnsupportedAnalysisException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ApiError("UNSUPPORTED_ANALYSIS", exception.getMessage()));
    }

    @ExceptionHandler(UnsupportedLanguageException.class)
    public ResponseEntity<ApiError> unsupportedLanguage(UnsupportedLanguageException exception) {
        return ResponseEntity.badRequest()
                .body(new ApiError("UNSUPPORTED_LANGUAGE", exception.getMessage()));
    }

    public record ApiError(String code, String message) {
    }
}
