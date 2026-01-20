# Release Process for HytaleAE2

**Standardisiertes Workflow für Releases und GitHub Integration**

---

## Overview

Dieser Prozess stellt sicher, dass jeder Release:
- Versioniert ist (MAJOR.MINOR.PATCH)
- In `docs/CHANGELOG.md` dokumentiert ist
- Als GitHub Release verfügbar ist
- Mit Git-Tags trackt wird
- Build-Artefakte enthält

---

## Step-by-Step Release Workflow

### 1. Vorbereitung

**Stelle sicher, dass alles committed ist:**
```bash
git status  # Nichts "modified"
```

### 2. Version aktualisieren

**Datei:** `gradle.properties`

```properties
# Update this:
version=0.1.0  →  0.1.1 (patch) oder 0.2.0 (minor) oder 1.0.0 (major)
```

**Versioning Guide:**
- `PATCH` (0.1.0 → 0.1.1): Bug fixes, kleine Verbesserungen
- `MINOR` (0.1.0 → 0.2.0): Neue Features, backward-compatible
- `MAJOR` (0.1.0 → 1.0.0): Breaking changes, Refactoring

### 3. CHANGELOG aktualisieren

**Datei:** `docs/CHANGELOG.md`

Beispiel:
```markdown
## [0.2.0] - 2026-01-25

### Added
- New ContainerUtils for inventory search
- HelloPlugin compliance patterns
- Project governance documentation

### Fixed
- Unicode/emoji garbling in console logs
- Broken doc links after consolidation

### Changed
- Consolidated 22 docs files to 7 core files
- Enhanced logging with SLF4J

### Security
- N/A
```

### 4. README aktualisieren (falls nötig)

**Datei:** `README.md`

Falls Version-Badge dort:
```markdown
[![Version](https://img.shields.io/badge/version-0.2.0-blue)](releases/tag/v0.2.0)
```

### 5. Build testen

```bash
gradlew clean build

# Erwartet: BUILD SUCCESSFUL
# Artefakt: build/libs/hytale-ae2-0.2.0.jar
```

### 6. Commit mit Release-Tag

```bash
# Commit
git add gradle.properties docs/CHANGELOG.md README.md
git commit -m "release: v0.2.0"

# Tag erstellen
git tag -a v0.2.0 -m "Release v0.2.0

- New ContainerUtils for inventory search
- HelloPlugin compliance patterns
- Unicode/emoji fixes"

# Push mit Tags
git push origin main --tags
```

### 7. GitHub Release erstellen

**UI:** https://github.com/Anoxy1/Hytale-AE-2/releases/new

```
Tag: v0.2.0
Title: Release v0.2.0 - Inventory Search & Docs Consolidation

Description (aus CHANGELOG):
[copy-paste vom CHANGELOG entry]

Artifacts:
- Upload build/libs/hytale-ae2-0.2.0.jar
```

### 8. (Future) Automatisieren mit GitHub Actions

**Datei:** `.github/workflows/release.yml` (zu erstellen)

```yaml
name: Auto Release

on:
  push:
    tags:
      - 'v*'

jobs:
  release:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '25'
      - run: ./gradlew build
      - uses: softprops/action-gh-release@v1
        with:
          files: build/libs/*.jar
```

---

## Files to Update on Each Release

| File | Update | Example |
|------|--------|---------|
| `gradle.properties` | version | `0.1.0` → `0.1.1` |
| `docs/CHANGELOG.md` | new section | `## [0.1.1] - 2026-01-25` |
| `README.md` | version badge (optional) | `badge-version-0.1.1` |
| `docs/INDEX.md` | link to release (optional) | Add to "Latest" section |

---

## Naming Conventions

### Commit Messages (Conventional Commits)

```
release: v0.1.0           # Release commit
feat: add X               # New feature
fix: correct Y            # Bug fix
docs: update Z            # Documentation
chore: update deps        # Maintenance
```

### Git Tags

```
v0.1.0                    # Standard release
v0.1.0-alpha1             # Alpha
v0.1.0-beta1              # Beta
v0.1.0-rc1                # Release candidate
```

### Version Branches (if needed)

```
release/0.1.0             # Release branch for hotfixes
```

---

## Release Checklist

```bash
[ ] 1. git status prüfen (nichts modified)
[ ] 2. gradle.properties version aktualisiert
[ ] 3. docs/CHANGELOG.md updated mit Release-Notes
[ ] 4. README.md version badge updated (falls vorhanden)
[ ] 5. ./gradlew clean build (BUILD SUCCESSFUL)
[ ] 6. git add + git commit "release: vX.Y.Z"
[ ] 7. git tag -a vX.Y.Z -m "..."
[ ] 8. git push origin main --tags
[ ] 9. GitHub Release UI: vX.Y.Z mit CHANGELOG-Text
[ ] 10. Artefakt hochladen: build/libs/*.jar
[ ] 11. GitHub Release als "Latest" markieren
```

---

## Emergency: Hotfix Release

Falls kritischer Bug nach Release:

```bash
# 1. Bug fixen im Code
git checkout main
# ... fix code ...

# 2. Patch-Version bump
gradle.properties: 0.1.0 → 0.1.1

# 3. CHANGELOG: neuen Abschnitt mit [HOTFIX] Label
docs/CHANGELOG.md: "## [0.1.1] - 2026-01-25 [HOTFIX]"

# 4. Build & Release wie oben
./gradlew clean build
git add ... && git commit -m "hotfix: vX.Y.Z"
git tag -a vX.Y.Z-hotfix1 -m "Hotfix: [description]"
git push origin main --tags
```

---

## Versioning Strategy

**Semantic Versioning (semver)**: MAJOR.MINOR.PATCH

| Level | Increment When | Example |
|-------|---|---------|
| MAJOR | Breaking API changes | 0.1.0 → 1.0.0 |
| MINOR | New features (backward-compatible) | 0.1.0 → 0.2.0 |
| PATCH | Bug fixes only | 0.1.0 → 0.1.1 |

**Timeline** (planned):
- `0.1.x`: Foundation phase (current)
- `0.2.x`: Storage Cells
- `0.3.x`: Terminal GUI
- `1.0.0`: Stable Release

---

## Reference

- [Semantic Versioning](https://semver.org/)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Keep a Changelog](https://keepachangelog.com/)
- [GitHub Releases](https://docs.github.com/en/repositories/releasing-projects-on-github/about-releases)
