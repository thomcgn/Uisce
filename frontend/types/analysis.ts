export type Segment = {
  grapheme: string;
  quality: "BROAD" | "SLENDER";
  mutation: string;
  position: string;
  ipa: string;
  pronunciationHint: string;
  description: string;
};

export type Pronunciation = {
  ipa: string;
  pronunciationHint: string;
  pronunciationNotes: string[];
  dialects: string[];
  sourceUrl: string;
  reviewStatus: "SOURCED" | "VERIFIED";
};

export type Analysis = {
  word: string;
  ipa: string;
  pronunciationHint: string;
  pronunciationNotes: string[];
  spelling: {
    canonicalWord: string;
    explanation: string;
    sourceUrl: string;
  } | null;
  orthography: {
    word: string;
    segments: {
      grapheme: string;
      position: string;
      quality: "BROAD" | "SLENDER" | null;
      mutation: string;
    }[];
  } | null;
  segments: Segment[];
  pronunciations: Pronunciation[];
};
