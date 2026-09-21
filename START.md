# Irish Phonetic Engine – Project Bootstrap

## 1. Ziel

Erstelle aus diesem leeren Repository eine Full-Stack-Anwendung zur Analyse der irischen Aussprache.

Die Anwendung soll ein irisches Wort entgegennehmen und dessen Aussprache regelbasiert analysieren.

Version 1 verwendet allgemeine/standardisierte irische Ausspracheregeln. Dialektvarianten wie Munster, Connacht und Ulster sind nicht Bestandteil des MVP; die Architektur muss eine spätere modulare Erweiterung aber ermöglichen.

Die Anwendung darf keine externe KI benötigen, um die grundlegende Ausspracheanalyse durchzuführen.

## 2. Tech Stack

### Backend
- Java 21
- Spring Boot
- Maven
- Spring Web
- Bean Validation
- JUnit 5
- AssertJ
- Spring Boot Test

Package Base: `de.irishphonetics`

### Frontend
- Next.js
- App Router
- React
- TypeScript
- Tailwind CSS
- ESLint

Keine unnötigen zusätzlichen Dependencies installieren.

### Repository

```text
/
├── backend/
├── frontend/
├── docker-compose.yml
├── .gitignore
├── README.md
└── START.md
```

## 3. Entwicklungsprinzipien

Arbeite als Software Engineer und nicht als Codegenerator für einen schnellen Prototypen.

Beachte:
- Clean Code
- SOLID
- Domain Driven Design, soweit für die Projektgröße sinnvoll
- kleine Klassen mit klarer Verantwortung
- immutable Domain Objects bevorzugen
- Java Records verwenden, wenn sinnvoll
- keine Businesslogik in Controllern
- keine Businesslogik in React Components
- keine unnötigen Framework-Abstraktionen
- keine God Classes
- keine Magic Strings
- keine unnötigen Regex-Konstruktionen
- Testbarkeit als Architekturziel

## 4. Backend-Architektur

Verwende eine Package-Struktur nach fachlichen Verantwortlichkeiten.

```text
de.irishphonetics
├── analysis
│   ├── api
│   ├── application
│   └── domain
├── phonology
│   └── domain
└── shared
```

Die eigentliche Phonetik-Engine darf nicht von Spring abhängig sein.

```text
HTTP
 ↓
AnalysisController
 ↓
AnalyzeWordUseCase
 ↓
IrishPhoneticEngine
 ↓
Tokenizer
 ↓
MutationDetector
 ↓
PhoneticContextResolver
 ↓
SoundRuleEngine
 ↓
AnalysisResult
```

Spring dient lediglich als Application/API Layer.

## 5. Domain Model

Keine `boolean isSlender`-Flags verwenden. Modelliere die Fachbegriffe explizit.

```java
public enum Quality {
    BROAD,
    SLENDER
}
```

```java
public enum Position {
    INITIAL,
    MEDIAL,
    FINAL
}
```

```java
public enum Mutation {
    NONE,
    LENITION,
    ECLIPSIS,
    H_PREFIX,
    T_PREFIX,
    N_PREFIX
}
```

```java
public record PhoneticContext(
        String grapheme,
        Position position,
        Quality quality,
        Mutation mutation
) {}
```

Das Modell darf während der Implementierung verbessert werden, wenn dafür ein fachlicher Grund besteht.

## 6. Sound Rules

IPA ist die primäre phonetische Repräsentation. Eine deutsche Aussprachehilfe ist lediglich eine zusätzliche Lernhilfe.

```java
public record SoundRule(
        String grapheme,
        Quality quality,
        Mutation mutation,
        String ipa,
        String germanHint,
        String description
) {}
```

Keine Dialektinformationen in Version 1 einbauen.

Die Architektur soll später unterschiedliche Rule Sets ermöglichen:

```text
PhoneticRuleSet
      ├── StandardIrishRuleSet
      ├── MunsterRuleSet
      ├── ConnachtRuleSet
      └── UlsterRuleSet
```

Für das MVP wird ausschließlich `StandardIrishRuleSet` implementiert.

## 7. Linguistische Grundregeln

Broad vowels:

```text
a á
o ó
u ú
```

Slender vowels:

```text
e é
i í
```

Fada muss erhalten bleiben. Akzente nicht vor der Analyse entfernen.

## 8. Broad / Slender

Broad/slender darf nicht über eine globale Regex-Matrix bestimmt werden. Insbesondere keine Lösung wie:

```java
boolean[] isSlenderMatrix;
```

Stattdessen:
1. Wort tokenisieren
2. relevante Vokalumgebung bestimmen
3. Konsonant bzw. Konsonantengruppe betrachten
4. `Quality` aus dem orthographischen Kontext bestimmen
5. Ergebnis im `PhoneticContext` speichern

Berücksichtige das irische orthographische Prinzip `caol le caol agus leathan le leathan`.

## 9. Mutationen

### Lenition

Mindestens:

```text
bh ch dh fh gh mh ph sh th
```

### Eclipsis

Mindestens:

```text
mb gc nd bhf bp dt ng
```

Bei Eclipsis wird der ursprüngliche Konsonant durch den vorangestellten Konsonanten verdeckt:

```text
b → mb
c → gc
d → nd
f → bhf
p → bp
t → dt
g → ng
```

Diese Kombinationen nicht als unabhängige Konsonanten behandeln.

### Prefix Mutations

Architektur vorbereiten für:

```text
h-
t-
n-
```

## 10. Tokenizer

Implementiere einen eigenen Tokenizer mit Longest-Match-Prinzip:

```text
bhf
 ↓
bh
 ↓
b
```

`bhfuil` darf im entsprechenden Kontext nicht als `bh + f + u + i + l` zerlegt werden, wenn `bhf` eine Eclipsis bildet.

Der Tokenizer trifft noch keine Ausspracheentscheidung.

## 11. Verarbeitungspipeline

```text
Input
  ↓
Normalization
  ↓
Tokenization
  ↓
Mutation Detection
  ↓
Context Resolution
  ↓
Broad / Slender Resolution
  ↓
Sound Rule Resolution
  ↓
IPA
  ↓
German Learning Hint
  ↓
Analysis Result
```

Alle Schritte getrennt testbar halten.

## 12. API

Implementiere zunächst:

```http
POST /api/v1/analysis
```

Request:

```json
{
  "word": "baile"
}
```

Response-Struktur:

```json
{
  "word": "baile",
  "ipa": "...",
  "pronunciationHint": "...",
  "segments": [
    {
      "grapheme": "b",
      "quality": "BROAD",
      "mutation": "NONE",
      "position": "INITIAL",
      "ipa": "...",
      "pronunciationHint": "...",
      "description": "..."
    }
  ]
}
```

Keine erfundenen Placeholder-Laute als scheinbar echte Analyse zurückgeben.

## 13. Fehlerbehandlung

Leere und Whitespace-only Eingaben mit HTTP 400 beantworten.

Bean Validation für API-Eingaben verwenden. Backend Exceptions nicht ungefiltert an den Client weiterreichen. Kleines globales API Error Handling implementieren.

## 14. Frontend

Route: `/`

Minimale Oberfläche:

```text
Irish Phonetic Engine

[ Irish word                     ]

              [ Analyze ]

IPA
/ ... /

Pronunciation
...

Analysis

b
Broad
...
```

Priorität:
1. Funktionalität
2. Lesbarkeit
3. Accessibility
4. Responsive Layout
5. Design

## 15. Frontend-Struktur

```text
frontend/
├── app/
│   ├── page.tsx
│   ├── layout.tsx
│   └── globals.css
├── components/
│   └── analysis/
├── lib/
│   └── api/
└── types/
```

API-Zugriffe unter `lib/api/` zentralisieren.

## 16. Tests

Tests sind Bestandteil der Implementierung.

### Backend Unit Tests

Mindestens:
- Tokenizer
- MutationDetector
- QualityResolver
- SoundRuleEngine
- IrishPhoneticEngine

### API Tests

Mindestens:
- gültiges Wort
- leeres Wort
- Whitespace
- Unicode/Fada

### Linguistische Regressionstests

Beginne mindestens mit:

```text
baile
mbaile
bhfuil
oíche
Sláinte
Dia
teach
tsráid
```

Keine sprachlich ungesicherten Erwartungen aus dem alten Prototyp übernehmen. Unsichere Transkriptionen als TODO dokumentieren statt erfundene IPA-Ausgaben festzuschreiben.

## 17. Docker

Erstelle `docker-compose.yml` mit:

```text
backend
frontend
```

Keine Datenbank.

Ports:

```text
frontend: 3000
backend: 8080
```

## 18. Environment

Frontend:

```text
NEXT_PUBLIC_API_URL=http://localhost:8080
```

Erstelle `frontend/.env.example`. Keine Secrets committen.

## 19. README

README mit:
- Projektziel
- Architektur
- Voraussetzungen
- Backend starten
- Frontend starten
- Tests ausführen
- Docker Compose starten
- aktuelle MVP-Grenzen

## 20. Nicht Bestandteil des MVP

Noch NICHT implementieren:
- Munster-spezifische Regeln
- Kerry-spezifische Regeln
- Connacht-spezifische Regeln
- Ulster-spezifische Regeln
- Text-to-Speech
- Speech-to-Text
- Whisper
- Audioaufnahme
- Benutzerkonten
- Datenbank
- Login
- Wortlistenverwaltung
- KI-Auswertung
- LLM APIs
- Übersetzungsfunktion
- komplexes UI
- Gamification

## 21. Qualitätsanforderungen

Backend:

```bash
cd backend
./mvnw test
./mvnw spring-boot:run
```

Frontend:

```bash
cd frontend
npm install
npm run lint
npm run build
npm run dev
```

Docker:

```bash
docker compose build
docker compose up
```

## 22. Vorgehensweise für den Coding Agent

### Phase 1 – Bootstrap

Erzeuge:

```text
backend/
frontend/
docker-compose.yml
.gitignore
README.md
frontend/.env.example
```

Backend und Frontend müssen unabhängig starten können. Build und Tests ausführen und Fehler beheben, bevor Phase 2 begonnen wird.

### Phase 2 – Domain

Implementiere:
- Quality
- Position
- Mutation
- PhoneticContext
- SoundRule

mit Unit Tests.

### Phase 3 – Parsing

Implementiere:
- Tokenizer
- MutationDetector
- QualityResolver

mit Unit Tests.

### Phase 4 – Engine

Implementiere:
- PhoneticRuleSet
- StandardIrishRuleSet
- SoundRuleEngine
- IrishPhoneticEngine

mit Tests.

### Phase 5 – REST API

Implementiere `POST /api/v1/analysis` inklusive Validation, DTOs, Mapping, Error Handling und API Tests.

### Phase 6 – Frontend

Implementiere die minimale Analyseoberfläche und verbinde sie mit dem Backend.

### Phase 7 – Verification

```bash
./mvnw test
npm run lint
npm run build
docker compose build
```

Alle Fehler beheben.

## 23. Definition of Done

Das MVP ist fertig, wenn:
- Backend startet
- Frontend startet
- Docker Compose funktioniert
- ein irisches Wort eingegeben werden kann
- das Backend das Wort analysiert
- Grapheme strukturiert ausgegeben werden
- Mutationen erkannt werden
- broad/slender kontextbezogen bestimmt wird
- IPA und deutsche Lernhilfe getrennt modelliert sind
- Ergebnisse im Frontend dargestellt werden
- Backendtests erfolgreich laufen
- Frontend lint/build erfolgreich sind
- keine Dialektlogik fest in die Kernengine eingebaut wurde

## 24. Erste Aufgabe

Beginne jetzt ausschließlich mit **Phase 1 – Bootstrap**.

Erzeuge das Spring-Boot-Backend und das Next.js-Frontend im leeren Repository.

Stelle sicher, dass beide Projekte unabhängig erfolgreich bauen und starten.

Erzeuge außerdem:

```text
docker-compose.yml
.gitignore
README.md
frontend/.env.example
```

Führe die jeweiligen Build- und Testbefehle aus und behebe auftretende Fehler.

**Beginne Phase 2 erst, nachdem Phase 1 vollständig erfolgreich verifiziert wurde.**
