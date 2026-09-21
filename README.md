# Irish Phonetic Engine

Full-Stack-Projekt für eine regelbasierte und lexikongestützte Analyse der irischen Aussprache. **Phase 6: Frontend** verbindet die Eingabeoberfläche mit der REST-API und zeigt IPA-Varianten, Dialekte und Quellen an.

## Architektur

- `backend/`: Java 21, Spring Boot, Maven, Spring Web und Bean Validation. Phonologie und Engine sind von Spring unabhängig. Ein JDBC-Adapter liest Aussprachevarianten aus PostgreSQL; Flyway verwaltet das Schema.
- `frontend/`: Next.js mit App Router, React, TypeScript, Tailwind CSS und ESLint.
- `docker-compose.yml`: startet PostgreSQL, Backend und Frontend.

## Voraussetzungen

Java 21, Node.js 20.9 oder neuer, npm und Docker mit Compose. Der Maven Wrapper lädt Maven bei Bedarf selbst.

## Lokal starten

```bash
cp .env.example .env
# UISCE_DB_PASSWORD in .env setzen
docker compose up -d db
cd backend
./mvnw spring-boot:run
```

```bash
cd frontend
cp .env.example .env.local
npm install
npm run dev
```

Backend: <http://localhost:8080>; Frontend: <http://localhost:3000>. Die Backend-Startseite liefert 404; die API liegt unter `/api/v1/analysis`. PostgreSQL ist lokal auf Port 5433 erreichbar.

Auf der Startseite ein irisches Wort eingeben oder ein Beispiel wählen. Ein eindeutiger IPA-Wert wird groß angezeigt; bei mehreren verschiedenen Aussprachen erscheinen die Varianten einzeln. Nicht abgedeckte Wörter und Verbindungsprobleme werden als Fehlermeldung dargestellt.

Auch belegte ältere Schreibweisen können auf heutige Formen verweisen. Beispielsweise wird `Sidhe` als veraltete Schreibweise von `sí` erkannt; die Antwort zeigt die Zuordnung samt Quelle, bevor sie die Aussprache von `sí` ausgibt. Schreibweisen werden nicht pauschal durch Entfernen von Fada oder Buchstaben umgewandelt.

Im Feld **Deine Muttersprache** lässt sich derzeit `Deutsch` oder `Nur IPA` auswählen. Das deutsche Modul liefert eine vereinfachte Lesehilfe für unterstützte IPA-Zeichen, auch für einzelne Aussprachevarianten. Es ersetzt die IPA-Transkription nicht und bildet insbesondere die irische Unterscheidung zwischen breiten und schmalen Konsonanten nur annähernd ab. Für nicht unterstützte IPA-Zeichen bleibt die Lesehilfe leer. Weitere Sprachen können als eigenes `PronunciationGuide`-Modul ergänzt werden.

Alle 14.970 IPA-Varianten des eingebundenen Lexikons erhalten eine deutsche Lesehilfe. Bei Lauten, die sich mit deutscher Schreibweise leicht verwechseln lassen, erscheinen zusätzliche Hinweise an der Variante, etwa `ch wie in Bach`, `ch wie in ich` oder ein Hinweis auf nasale Vokale. Diese Umschrift ist eine systematische Annäherung und keine fachlich geprüfte deutsche Lautung jedes Wortes.

Die Hinweise verwenden vertraute Beispielwörter: IPA `/dʒ/` wird als `dsch wie in Dschungel` erklärt (wie der Anlaut von englisch `jaw`). Das irische `/ɟ/` erhält einen eigenen Hinweis als weiches `g` am Gaumen, weil es nicht derselbe Laut ist.

Die deutsche Hilfe liest die belegten Varianten von `Sláinte` beispielsweise als `Slaan-che`. Schmale Konsonanten erhalten nicht mehr pauschal ein angehängtes `j`; die Darstellung bleibt bewusst eine Annäherung.

## Analyse-API

Auch bei Lexikontreffern analysiert die Engine die Schreibweise: `orthography` enthält das analysierte Wort und seine Grapheme mit Position, erkannter Vokalqualität (falls eindeutig) und Mutationsmuster. `LENITION` bezeichnet dabei ein erkennbares Schreibmuster wie `bh` oder `ch`; ob eine grammatische Lenition vorliegt, erfordert Satzkontext. Die IPA der Lexikontreffer bleibt quellengebunden. `segments` enthält nur Lautsegmente, für die geprüfte Einzelregeln vorliegen. Bei historischen Aliasformen wie `Sidhe` wird die heutige Form `sí` orthografisch analysiert.

```bash
curl -s http://localhost:8080/api/v1/analysis \
  -H 'Content-Type: application/json' \
  -d '{"word":"Dia"}'
```

`POST /api/v1/analysis` erwartet ein JSON-Objekt mit einem nicht leeren `word` und optional `nativeLanguage` (`de`, standardmäßig, oder `none`). Die Antwort enthält `word`, `ipa`, `pronunciationHint`, `segments` und `pronunciations`. Lexikoneinträge erscheinen in `pronunciations` mit IPA, eigener `pronunciationHint`, Dialektangaben, Quell-URL und Prüfstatus `SOURCED`. Bei mehreren verschiedenen IPA-Varianten bleibt das einzelne `ipa`-Feld leer. Leere Eingaben, ungültiges JSON und noch nicht unterstützte Sprachen liefern HTTP 400 mit `code` und `message`; nicht abgedeckte Wörter liefern HTTP 422 (`UNSUPPORTED_ANALYSIS`).

## Prüfen

```bash
cd backend && ./mvnw test && ./mvnw package
cd frontend && npm run lint && npm run build
```

## Docker Compose

```bash
cp .env.example .env
# UISCE_DB_PASSWORD in .env setzen
docker compose build
docker compose up
```

`NEXT_PUBLIC_API_URL` ist standardmäßig `http://localhost:8080` und wird beim Frontend-Build gesetzt. Nach einer Änderung der URL das Frontend-Image neu bauen.

## CI und lokale Bereitstellung

Der GitHub-Actions-Workflow `.github/workflows/ci.yml` prüft bei Pushes und Pull Requests Backend-Tests, Frontend-Lint und -Build sowie beide Docker-Images. Er kann auch manuell gestartet werden. Die lokale Bereitstellung erfolgt mit `docker compose up --build -d`; `docker compose down` stoppt sie. Für Docker Desktop bei abweichendem Docker-Kontext `docker --context desktop-linux compose up --build -d` verwenden.

Die CI baut Images, veröffentlicht sie derzeit aber nicht in einer Registry. Ein automatisches Deployment zu Docker Desktop ist aus einem GitHub-Runner nicht möglich, weil dieser keinen Zugriff auf den lokalen Rechner hat. Die Backend-Tests verwenden eine temporäre H2-Datenbank.

## MVP-Grenzen

Geplant ist zunächst die Analyse einzelner Wörter. Audio, Benutzerkonten und KI-Dienste sind noch nicht implementiert. Dialektangaben aus dem Aussprachelexikon bleiben als Varianten erhalten; es wird keine einzige "Standardaussprache" daraus erfunden.

Das eingebundene Lexikon enthält 14.970 quellgebundene IPA-Einträge zu 8.824 normalisierten Schreibformen. Herkunft, Aussprachevarianten und Dialektangaben bleiben erhalten; Details und Lizenz stehen in [DATA-LICENSE.md](DATA-LICENSE.md). Die Einträge sind **quellgebunden, nicht einzeln fachlich geprüft**. Bei mehreren unterschiedlichen Varianten bleibt das einzelne `ipa`-Feld leer und die Varianten stehen getrennt im Ergebnis. Das regelbasierte Fallback deckt einfache Wörter mit einem langen Vokal ab. Nicht belegte oder komplexe Wörter liefern weiterhin `UnsupportedAnalysisException`. Eine garantierte IPA-Abdeckung aller irischen Wörter ist mit dem derzeitigen Datenbestand nicht möglich; insbesondere fehlen zahlreiche Formen und Dialektentscheidungen. Die Begrenzung ist linguistisch begründet, siehe [Understanding Irish Spelling, UCD/COGG](https://researchrepository.ucd.ie/entities/publication/ef4484ac-4042-4ba2-a441-cc8f84219e04).
