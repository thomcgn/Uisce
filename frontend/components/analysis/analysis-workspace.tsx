"use client";

import { useRef, useState, type FormEvent } from "react";
import { analyzeWord, AnalysisApiError } from "@/lib/api/analysis";
import type { Analysis } from "@/types/analysis";

const examples = ["Dia", "baile", "Sláinte", "oíche"];
type NativeLanguage = "de" | "none";

function dialectLabel(dialects: string[]) {
  return dialects.length ? dialects.join(" · ") : "Dialekt nicht angegeben";
}

export function AnalysisWorkspace() {
  const [word, setWord] = useState("");
  const [analysis, setAnalysis] = useState<Analysis | null>(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [nativeLanguage, setNativeLanguage] = useState<NativeLanguage>("de");
  const request = useRef<AbortController | null>(null);

  async function submit(value: string, language = nativeLanguage) {
    request.current?.abort();
    const normalized = value.trim();
    setWord(value);
    setAnalysis(null);
    setError("");
    if (!normalized) {
      setLoading(false);
      setError("Bitte gib ein irisches Wort ein.");
      return;
    }

    const controller = new AbortController();
    request.current = controller;
    setLoading(true);
    try {
      setAnalysis(await analyzeWord(normalized, language, controller.signal));
    } catch (cause) {
      if (controller.signal.aborted) return;
      setError(cause instanceof AnalysisApiError && cause.code === "UNSUPPORTED_ANALYSIS"
        ? "Für dieses Wort liegt noch keine gesicherte Aussprache vor."
        : cause instanceof AnalysisApiError ? cause.message : "Ein unerwarteter Fehler ist aufgetreten.");
    } finally {
      if (request.current === controller) setLoading(false);
    }
  }

  function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    void submit(word);
  }

  return (
    <div className="workspace">
      <section className="intro" aria-labelledby="page-title">
        <p className="eyebrow">UISCE · IRISCHE AUSSPRACHE</p>
        <h1 id="page-title">Ein Wort.<br /><span>Viele Klangfarben.</span></h1>
        <p className="lead">Erkunde die Aussprache irischer Wörter anhand belegter IPA-Transkriptionen und klar gekennzeichneter Dialektvarianten.</p>
      </section>

      <section className="search-panel" aria-label="Wort analysieren">
        <form onSubmit={onSubmit}>
          <div className="language-choice">
            <label htmlFor="native-language">DEINE MUTTERSPRACHE</label>
            <select id="native-language" value={nativeLanguage} onChange={(event) => {
              const language = event.target.value as NativeLanguage;
              setNativeLanguage(language);
              if (analysis || loading) void submit(word, language);
            }}>
              <option value="de">Deutsch</option>
              <option value="none">Nur IPA</option>
            </select>
          </div>
          <label htmlFor="word">IRISCHES WORT</label>
          <div className="search-row">
            <input id="word" name="word" value={word} onChange={(event) => setWord(event.target.value)}
              placeholder="z. B. Sláinte" autoComplete="off" spellCheck={false} aria-describedby="input-help" />
            <button type="submit" disabled={loading}>{loading ? "Analysiere …" : "Analysieren"}</button>
          </div>
          <p id="input-help" className="input-help">Fada und Großschreibung bleiben erhalten.</p>
        </form>
        <div className="examples" aria-label="Beispielwörter">
          <span>PROBIERE</span>
          {examples.map((example) => <button key={example} type="button" onClick={() => void submit(example)}>{example}</button>)}
        </div>
      </section>

      <div className="result-area" aria-live="polite" aria-busy={loading}>
        {loading && <p className="status-card">Aussprache wird nachgeschlagen …</p>}
        {error && <div className="status-card error" role="alert"><strong>Keine Analyse verfügbar</strong><p>{error}</p></div>}
        {analysis && <AnalysisDetails analysis={analysis} nativeLanguage={nativeLanguage} />}
        {!loading && !error && !analysis && <div className="empty-card"><span aria-hidden="true">◌</span><p>Deine Analyse erscheint hier.</p></div>}
      </div>
    </div>
  );
}

function AnalysisDetails({ analysis, nativeLanguage }: { analysis: Analysis; nativeLanguage: NativeLanguage }) {
  const unique = [...new Set(analysis.pronunciations.map((entry) => entry.ipa))];
  return (
    <article className="analysis-card">
      <header className="result-header"><div><p className="eyebrow">ANALYSE</p><h2>{analysis.word}</h2></div><span className="result-badge">IPA</span></header>
      {analysis.ipa ? <p className="main-ipa" aria-label={`IPA: ${analysis.ipa}`}>/{analysis.ipa}/</p>
        : <p className="variant-intro">{unique.length} belegte Aussprachen mit unterschiedlichen Dialektangaben.</p>}
      {analysis.spelling && <p className="spelling-note">{analysis.spelling.explanation} Heutige Schreibweise: <strong>{analysis.spelling.canonicalWord}</strong>. <a href={analysis.spelling.sourceUrl} target="_blank" rel="noreferrer">Schreibweisen-Quelle ↗</a></p>}
      {analysis.pronunciationHint && <p className="hint"><strong>Deutsch angenähert:</strong> {analysis.pronunciationHint}</p>}
      {analysis.pronunciationNotes.length > 0 && <ul className="hint-notes">{analysis.pronunciationNotes.map((note) => <li key={note}>{note}</li>)}</ul>}

      {analysis.pronunciations.length > 0 && <section className="variants" aria-labelledby="variants-title">
        <div className="section-heading"><h3 id="variants-title">Belegte Aussprachevarianten</h3><span>{analysis.pronunciations.length} Einträge</span></div>
        <ul>{analysis.pronunciations.map((entry, index) => <li key={`${entry.ipa}-${entry.sourceUrl}-${index}`}>
          <div><span className="variant-ipa">/{entry.ipa}/</span><span className="dialect">{dialectLabel(entry.dialects)}</span>
            {entry.pronunciationHint && <span className="variant-hint">Deutsch angenähert: <strong>{entry.pronunciationHint}</strong></span>}
            {entry.pronunciationNotes.length > 0 && <span className="variant-notes">{entry.pronunciationNotes.join(" · ")}</span>}</div>
          <a href={entry.sourceUrl} target="_blank" rel="noreferrer">Quelle ↗</a>
        </li>)}</ul>
        <p className="source-note">Quellenbelegt bedeutet nicht einzeln fachlich geprüft.</p>
      </section>}

      {nativeLanguage === "de" && <p className="guide-note">Die deutsche Schreibweise ist eine Lesehilfe. Breite und schmale irische Konsonanten kann sie nur annähernd wiedergeben. Fehlt eine unterstützte Übertragung, erscheint nur IPA.</p>}

      {analysis.orthography && <section className="segments" aria-labelledby="orthography-title">
        <div className="section-heading"><h3 id="orthography-title">Schreibweise und Mutationen</h3><span>{analysis.orthography.segments.length} Grapheme</span></div>
        {analysis.orthography.word !== analysis.word && <p>Analysierte heutige Schreibweise: <strong>{analysis.orthography.word}</strong></p>}
        <ul>{analysis.orthography.segments.map((segment, index) => <li key={`${segment.grapheme}-${index}`}>
          <div className="grapheme">{segment.grapheme}</div>
          <div><strong>{mutationLabel(segment.mutation)}</strong><p>{segment.quality === "BROAD" ? "Breit" : segment.quality === "SLENDER" ? "Schmal" : "Qualität offen"}</p></div>
        </li>)}</ul>
        <p className="source-note">Die Mutationsangabe beschreibt das erkennbare Schreibmuster. Ob eine grammatische Mutation vorliegt, hängt vom Satzkontext ab.</p>
      </section>}

      {analysis.segments.length > 0 && <section className="segments" aria-labelledby="segments-title">
        <div className="section-heading"><h3 id="segments-title">Laute im Wort</h3><span>{analysis.segments.length} Segmente</span></div>
        <ul>{analysis.segments.map((segment, index) => <li key={`${segment.grapheme}-${index}`}>
          <div className="grapheme">{segment.grapheme}</div>
          <div><strong>/{segment.ipa}/</strong><p>{segment.quality === "BROAD" ? "Breit" : "Schmal"}{segment.mutation !== "NONE" ? ` · ${segment.mutation}` : ""}</p><small>{segment.description}</small></div>
        </li>)}</ul>
      </section>}
    </article>
  );
}

function mutationLabel(mutation: string): string {
  switch (mutation) {
    case "LENITION": return "Lenitionsmuster";
    case "ECLIPSIS": return "Eklipse";
    case "H_PREFIX": return "h-Vorsilbe";
    case "T_PREFIX": return "t-Vorsilbe";
    case "N_PREFIX": return "n-Vorsilbe";
    default: return "Keine Mutation erkennbar";
  }
}
