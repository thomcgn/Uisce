# Aussprachedaten

`backend/src/main/resources/lexicon/irish-wiktionary-2026-09-02.tsv.gz` enthält einen gefilterten Auszug irischer IPA-Transkriptionen aus dem englischen Wiktionary. Extraktion: [Kaikki.org, Irish dictionary](https://kaikki.org/dictionary/Irish/index.html), Wiktionary-Dump vom 2. September 2026. Jede Zeile enthält einen Link zum jeweiligen Wiktionary-Eintrag.

Die Quelldaten stehen unter [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/) und alternativ GFDL. Dieser abgeleitete Datenauszug wird unter CC BY-SA 4.0 bereitgestellt. Namensnennung: Wiktionary contributors und Tatu Ylonen / Kaikki.org. Änderungen: Es wurden nur einzelne Wörter mit eindeutig abgegrenzter phonemischer IPA-Notation und ohne freie Anmerkungen übernommen; Dialektangaben sind erhalten.

Die Transkriptionen sind quellgebunden, aber nicht von diesem Projekt fachlich einzeln geprüft. Der Status in der Datenbank lautet deshalb `SOURCED` und darf nicht als unabhängige linguistische Verifikation dargestellt werden. Der übrige Quellcode ist von dieser Datenlizenz getrennt.

Der Eingabe-Datensatz hatte SHA-256 `dd87208b0ff81f13aae228761e32a54957d864a4d24a7daa85492b02d7435d69`; der erzeugte Datenauszug hat SHA-256 `ea73d7b0dafb3ce0081257937c88e5b7a720b163d2dec624ff233f4b3f96f4bd`. Der Filter kann mit `python scripts/build_irish_lexicon.py <download.jsonl> <output.tsv.gz>` erneut ausgeführt werden.
