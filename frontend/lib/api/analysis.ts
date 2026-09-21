import type { Analysis } from "@/types/analysis";

const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

export class AnalysisApiError extends Error {
  constructor(public readonly code: string, message: string) {
    super(message);
  }
}

export async function analyzeWord(word: string, nativeLanguage: string, signal?: AbortSignal): Promise<Analysis> {
  let response: Response;
  try {
    response = await fetch(`${API_URL}/api/v1/analysis`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ word, nativeLanguage }),
      signal,
    });
  } catch (error) {
    if (error instanceof Error && error.name === "AbortError") throw error;
    throw new AnalysisApiError("NETWORK_ERROR", "Der Analysedienst ist derzeit nicht erreichbar.");
  }

  if (!response.ok) {
    const body: unknown = await response.json().catch(() => null);
    if (body && typeof body === "object" && "code" in body && "message" in body &&
        typeof body.code === "string" && typeof body.message === "string") {
      throw new AnalysisApiError(body.code, body.message);
    }
    throw new AnalysisApiError("HTTP_ERROR", "Die Analyse konnte nicht geladen werden.");
  }

  return response.json() as Promise<Analysis>;
}
