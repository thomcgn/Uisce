# Irish Phonetic Engine

Full-Stack-Projekt für eine regelbasierte Analyse der irischen Aussprache. Der aktuelle Stand ist **Phase 2: Domain Model**. Die fachlichen Grundtypen sind implementiert; Parsing, Analysefunktion und REST-API folgen in späteren Phasen. Die Startseite zeigt derzeit nur den Projektstatus.

## Architektur

- `backend/`: Java 21, Spring Boot, Maven, Spring Web und Bean Validation. Die phonetischen Domänentypen unter `de.irishphonetics.phonology.domain` sind von Spring unabhängig.
- `frontend/`: Next.js mit App Router, React, TypeScript, Tailwind CSS und ESLint.
- `docker-compose.yml`: startet beide Dienste ohne Datenbank.

## Voraussetzungen

Java 21, Node.js 20.9 oder neuer, npm und optional Docker mit Compose. Der Maven Wrapper lädt Maven bei Bedarf selbst.

## Lokal starten

```bash
cd backend
./mvnw spring-boot:run
```

```bash
cd frontend
cp .env.example .env.local
npm install
npm run dev
```

Backend: <http://localhost:8080>; Frontend: <http://localhost:3000>. Da noch kein API-Endpunkt existiert, liefert die Backend-Startseite derzeit 404.

## Prüfen

```bash
cd backend && ./mvnw test && ./mvnw package
cd frontend && npm run lint && npm run build
```

## Docker Compose

```bash
docker compose build
docker compose up
```

`NEXT_PUBLIC_API_URL` ist standardmäßig `http://localhost:8080` und wird beim Frontend-Build gesetzt. Nach einer Änderung der URL das Frontend-Image neu bauen.

## MVP-Grenzen

Geplant ist zunächst nur eine allgemeine, standardisierte Analyse einzelner Wörter. Dialektregeln, Audio, Benutzerkonten, Datenbank und KI-Dienste sind nicht vorgesehen. Die eigentliche Analyse wird erst nach dem Bootstrap umgesetzt.
