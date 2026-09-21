"""Create the reviewed-shape pronunciation snapshot from Kaikki Irish JSONL.

The output keeps sourced IPA only; it does not linguistically validate entries.
"""

import argparse
import gzip
import io
import json
import unicodedata
from pathlib import Path
from urllib.parse import quote


ALLOWED_DIALECTS = {"Munster", "Connacht", "Ulster"}


def extract_rows(source: Path) -> set[tuple[str, str, str, str, str]]:
    rows = set()
    with source.open(encoding="utf-8") as stream:
        for line in stream:
            entry = json.loads(line)
            word = unicodedata.normalize("NFC", entry.get("word", ""))
            if (
                not word
                or len(word) > 256
                or not any(character.isalpha() for character in word)
                or any(character.isspace() for character in word)
            ):
                continue

            url = f"https://en.wiktionary.org/wiki/{quote(word, safe='')}#Irish"
            for sound in entry.get("sounds", []):
                raw_ipa = sound.get("ipa", "")
                dialects = sound.get("tags", [])
                if not (
                    raw_ipa.startswith("/")
                    and raw_ipa.endswith("/")
                    and raw_ipa.count("/") == 2
                ):
                    continue
                if any(character in raw_ipa for character in "~()[]?\t\r\n"):
                    continue
                if sound.get("note") or any(
                    dialect not in ALLOWED_DIALECTS for dialect in dialects
                ):
                    continue

                ipa = raw_ipa[1:-1]
                dialect_text = ",".join(sorted(dialects))
                if not ipa or len(ipa) > 512 or len(dialect_text) > 128:
                    continue
                rows.add((word.lower(), word, ipa, dialect_text, url))
    return rows


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("source", type=Path, help="Downloaded Kaikki Irish JSONL")
    parser.add_argument("destination", type=Path, help="Gzipped TSV output")
    arguments = parser.parse_args()

    rows = extract_rows(arguments.source)
    content = io.StringIO()
    for row in sorted(rows):
        content.write("\t".join(row) + "\n")
    arguments.destination.parent.mkdir(parents=True, exist_ok=True)
    arguments.destination.write_bytes(
        gzip.compress(content.getvalue().encode("utf-8"), compresslevel=9, mtime=0)
    )
    print(f"{len(rows)} pronunciations for {len({row[0] for row in rows})} words")


if __name__ == "__main__":
    main()
