CREATE TABLE spelling_alias (
    normalized_alias VARCHAR(256) PRIMARY KEY,
    canonical_word VARCHAR(256) NOT NULL,
    explanation VARCHAR(512) NOT NULL,
    source_url VARCHAR(1024) NOT NULL
);

INSERT INTO spelling_alias (normalized_alias, canonical_word, explanation, source_url)
VALUES ('sidhe', 'sí',
        'Veraltete irische Schreibweise von sí (Feenhügel); Sidhe ist auch im Englischen gebräuchlich.',
        'https://en.wiktionary.org/wiki/sidhe#Irish');
