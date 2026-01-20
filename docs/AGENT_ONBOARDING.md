# Agent Onboarding & Project Overview (AI-focused)

Purpose: Make AI agents produktiv und sicher.  
Last Updated: 2026-01-21  
Status: Foundation Complete (v0.1.0)

---

## AI Agent Start (zentrales Format)

### Safeguards (einmal lesen)
- Scope: nur `src/main/java` und `src/main/resources`; niemals `build/`, `libs/`, generierte Dateien.
- Output: ASCII-only in Code, Logs, JSON-Beschreibungen; keine Emoji/Sonderzeichen.
- Git: Feature-Branch von `main`, keine Force-Pushes, PR gegen `main`.
- Gate: `./gradlew clean build` vor Commits; Build-Fehler zuerst beheben.
- Zitieren: Pfade/Zeilen verlinken, kurz begründen.

### Jump Table (Kategorien → Deep Links)
| Kategorie | Was | Direktlink |
| --- | --- | --- |
| Build & Deploy | Befehle, Troubleshooting | [QUICK_START.md](QUICK_START.md) |
| Code & Patterns | HelloPlugin-Struktur, Beispiele, Anti-Patterns | [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md#helloplugin-standard-structure) |
| JSON & Assets | Item/Block/NPC/Loot/Prefabs-Schemas | [JSON_DATA_ASSETS.md](JSON_DATA_ASSETS.md) |
| API & Server | BlockState/Codec, manifest, Szenarien | [API_REFERENCE.md](API_REFERENCE.md) · [TECHNICAL_SOURCES.md](TECHNICAL_SOURCES.md) |
| Governance | Regeln, Commits, Releases | [PROJECT_RULES.md](PROJECT_RULES.md) · [CONTRIBUTING.md](CONTRIBUTING.md) |
| Navigation | Alles finden | [INDEX.md](INDEX.md) |

### 10-Minuten-Start
1) Repo klonen und Build: `git clone https://github.com/Anoxy1/Hytale-AE-2.git && cd Hytale-AE-2 && ./gradlew build`
2) Kontext laden: [README.md](../README.md) → [QUICK_START.md](QUICK_START.md) → [INDEX.md](INDEX.md)
3) Umsetzung: Code nach [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md) Section 8; JSON nach [JSON_DATA_ASSETS.md](JSON_DATA_ASSETS.md)
4) Änderungen vorschlagen: kurz, ASCII, mit Pfad/Zeilen-Link

### Arbeitsablauf für AI-Agenten (Schrittfolge)
1. Ziele klären: Aufgabe/Issue identifizieren und betroffene Kategorie aus der Jump Table wählen.
2. Governance laden: [PROJECT_RULES.md](PROJECT_RULES.md) und Commit/Branch-Vorgaben prüfen.
3. Kontext holen: passenden Leitfaden öffnen (Quick Start für Build/Deploy, Best Practices für Code, JSON Assets für Schemas, API/Technical Sources für tiefe Technik).
4. Fakten extrahieren: relevante Abschnitte lesen, Pfade/IDs/Kommandos notieren; ASCII/Logging-Regeln beachten.
5. Lösung skizzieren: Änderungen mit Pfad+Zeilenangabe planen, notwendige Befehle/JSON-Felder auflisten.
6. Validierung: `./gradlew clean build` einplanen (und ggf. Tests/Deploy-Schritte aus QUICK_START übernehmen).
7. Antwortformat: kurz, mit Links auf Pfade/Zeilen, klare nächste Schritte.

### Kompakte Doc-Digests (AI-taugliche Zusammenfassung)
| Doc | Kerninhalt | Nutze wenn |
| --- | --- | --- |
| README.md (root) | Projektkurzfassung, Schnellstart-Link | Überblick in 30s |
| QUICK_START.md | Build/Deploy, Hardware, In-Game-Test | Du bauen/deployen willst |
| DEVELOPMENT_GUIDE.md | Architektur, Roadmap, Core/GUI/Storage-Phasen | Feature planen/ändern |
| PLUGIN_BEST_PRACTICES.md | HelloPlugin-Pattern, Commands, Logging ASCII, Anti-Patterns | Code schreiben/reviewen |
| API_REFERENCE.md | BlockState/Codec, manifest, Events, GUI | API-Signaturen/Registrierung prüfen |
| JSON_DATA_ASSETS.md | Item/Block/NPC/Loot/World Schemas & Templates | JSON Assets anlegen/anpassen |
| TECHNICAL_SOURCES.md | Server/World config, Szenarien, Access-Control | Server-Tuning/Konfig |
| PROJECT_RULES.md | Regeln, Git/Build/ASCII, Verbote | Vor jeder Änderung |
| INDEX.md | Navigationsknoten | Themen suchen |

### Kommandos & Pfade (Index)
- Build: `./gradlew clean build` → Artefakt `build/libs/hytale-ae2-*.jar`
- Deploy (Windows Early Plugins): `%APPDATA%\Roaming\Hytale\UserData\earlyplugins/`
- Deploy (macOS/Linux): `~/Library/Application Support/Hytale/UserData/Mods/`
- Repo: `https://github.com/Anoxy1/Hytale-AE-2`
- Code-Pfad: `src/main/java/com/tobi/mesystem/`
- Resources: `src/main/resources/`

---

## Minimal Context (merken)

| Aspect | Detail |
| --- | --- |
| Project | HytaleAE2 – Early-game ME (Matter/Energy) System für Hytale |
| Stack | Java 25 LTS, Gradle 9.3.0, Hytale Server 2026.01.17 |
| Repo | https://github.com/Anoxy1/Hytale-AE-2 (branch: main) |
| Build Artifact | Shadow JAR in `build/libs/` nach `./gradlew build` |
| Deploy | `%APPDATA%\Roaming\Hytale\UserData\earlyplugins/` |
| Version | 0.1.0 (Core fertig; GUI/Storage pending) |
| Standards | Conventional commits, Code-Review, ASCII-only Output |

---

## Codebase Skeleton (Orientierung)

```
HytaleAE2/
├── src/main/java/com/tobi/mesystem/
│   ├── core/                MENode, MEBlockBase, MEControllerBlock
│   ├── utils/               ContainerUtils (~300 lines), MEDebugCommand
│   └── blocks/              MECableBlock, METerminalBlock, MEStorageBlock (placeholder)
├── src/main/resources/      manifest.json, Assets, Localization
├── build.gradle             Gradle + Shadow plugin
├── docs/                    Dokumentation
└── .github/workflows/ci.yml CI
```

---

Need help? Starte mit [INDEX.md](INDEX.md) oder eröffne ein Issue im Repo.

Last Review: 2026-01-21 (v0.1.0)
