# Contributing

Danke fürs Mithelfen! Bitte beachte:

- Branches: `feature/*`, `fix/*`, `docs/*` gegen `main`
- Commits: Conventional Commits (z. B. `feat: add import bus`)
- PRs: klein und fokussiert, mit kurzem Kontext und Testhinweisen
- Code-Stil: Java 25, UTF-8, LF; maximal 120 Zeichen pro Zeile
- Builds: `gradlew build` muss lokal erfolgreich sein

## Commit-Konventionen

- `feat:` neue Funktion
- `fix:` Bugfix
- `docs:` Dokumentation
- `chore:` Wartung (Build, Tools, CI)
- `refactor:` Code-Verbesserungen ohne Feature-Änderung

## Review-Checkliste

- Läuft der Build lokal?
- Sind Assets/JSON valide?
- Sind Logs frei von Unicode/Emoji?
- Sind Änderungen minimal und fokussiert?
