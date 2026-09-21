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
  segments: Segment[];
  pronunciations: Pronunciation[];
};
