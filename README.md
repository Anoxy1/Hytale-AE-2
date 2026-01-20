# HytaleAE2

Applied Energistics 2 für Hytale – ME Storage System Plugin.

Kurzer Überblick, klare Anleitung, saubere Struktur und verlässlicher Build.

## Für AI-Agents (Einstieg)
- Schnell orientieren: [docs/AGENT_ONBOARDING.md#ai-agent-start](docs/AGENT_ONBOARDING.md#ai-agent-start)
- Wichtigste Docs: [docs/QUICK_START.md](docs/QUICK_START.md), [docs/INDEX.md](docs/INDEX.md), [docs/PROJECT_RULES.md](docs/PROJECT_RULES.md)
- Build-Befehl: `./gradlew build` (JAR in `build/libs/`)

## Schnellstart

```bash
# Build
.\gradlew build

# Deploy (Windows)
.\deploy.bat

# Deploy (Linux/macOS)
./deploy.sh
```

## Projektstruktur

- `src/main/java/com/tobi/mesystem`: Kernlogik (`core`, `blocks`, `commands`, `util`)
- `src/main/resources`: Manifest, Assets (`Common`), Server-JSONs (`Item`, `Recipes`, `Interactions`, `RootInteractions`, `Languages`)
- `docs`: Dokumentation, Leitfäden, Referenzen; Einstieg: [docs/INDEX.md](docs/INDEX.md)
- `libs`: Referenzinhalte (ChestTerminal extrahiert, Assets)
- `deploy.bat` / `deploy.sh`: One-Click Deployment

## Dokumentation

- Einstieg: [docs/QUICK_START.md](docs/QUICK_START.md)
- Entwicklung: [docs/DEVELOPMENT_GUIDE.md](docs/DEVELOPMENT_GUIDE.md)
- API/Referenzen: [docs/HYTALE_PLUGIN_REFERENCE.md](docs/HYTALE_PLUGIN_REFERENCE.md), [docs/API_REFERENCE.md](docs/API_REFERENCE.md)
- Voller Index: [docs/INDEX.md](docs/INDEX.md)

## Build & Entwicklung

```bash
# Build
.\gradlew build

# Projektinfo
.\gradlew info
```

## Contributing

- Branching: `feature/*`, `fix/*` (gegen `main`)
- Commits: Conventional Commits (z. B. `feat: add import bus`)
- PRs: kleine, fokussierte Änderungen mit kurzer Beschreibung
- Details: [docs/CONTRIBUTING.md](docs/CONTRIBUTING.md)

## Status

- Version: 0.2.0
- Letzte Aktualisierung: 20. Januar 2026
- Fokus: Terminal GUI, Storage Cells

## Lizenz

Private Development
