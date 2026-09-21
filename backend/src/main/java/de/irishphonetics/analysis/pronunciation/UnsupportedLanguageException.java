package de.irishphonetics.analysis.pronunciation;

public final class UnsupportedLanguageException extends RuntimeException {
    public UnsupportedLanguageException(String languageTag) {
        super("Aussprachehilfe für Sprache '" + languageTag + "' ist noch nicht verfügbar");
    }
}
