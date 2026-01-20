# HytaleAE2

**Applied Energistics 2 für Hytale - ME Storage System Plugin**

Ein vollständiges ME (Matter Energy) Storage-System für Hytale, inspiriert von Applied Energistics 2 aus Minecraft.

---

## 🚀 Schnellstart

```bash
# 1. Build
.\gradlew build

# 2. Installation (Single Player)
copy build\libs\HytaleAE2-0.2.0.jar "%APPDATA%\Hytale\UserData\Mods\"

# Oder mit Deployment-Script:
.\deploy.bat
```

## ✅ Aktueller Status (Januar 2026)

### Fertiggestellt

- ✅ **Common/ Asset-Struktur** - Standard-konforme Ordnerstruktur
- ✅ **Block-Definitionen** - ME Cable, Controller, Terminal mit IconProperties
- ✅ **Interactions System** - Terminal mit Container-Öffnung
- ✅ **Crafting-Rezepte** - Alle Blöcke craftbar an Workbench
- ✅ **MENetwork Core** - Digitales Storage-System + Channel Management
- ✅ **MENode System** - Network Nodes mit Connection Logic
- ✅ **Hytale Manifest** - IncludesAssetPack: true
- ✅ **Plugin lädt erfolgreich** - Single Player & Server kompatibel
- ✅ **Code Refactoring** - Nach [HelloPlugin](https://github.com/noel-lang/hytale-example-plugin) Standards
- ✅ **API Compliance** - Native Hytale APIs ohne Reflection
- ✅ **Event System** - Lambda-basierte Event-Handler
- ✅ **Command System** - AbstractPlayerCommand Pattern
- ✅ **Build Optimization** - Gradle 0.2.0 mit Checkstyle & Info-Tasks

### In Arbeit

- ⏳ **Terminal GUI** - Requires Java Event Handler
- ⏳ **Storage Cells** - 1k, 4k, 16k, 64k Cells
- ⏳ **Block Entity Data** - Persistent storage

**Status:** ✅ Code Refactored & Optimized - Production-Ready nach Hytale Best Practices

---

## 📚 Dokumentation

### Haupt-Dokumentation

- 📖 **[docs/HYTALE_PLUGIN_COMPLETE_GUIDE.md](docs/HYTALE_PLUGIN_COMPLETE_GUIDE.md)** - Vollständiger Leitfaden
  - Ordnerstruktur, Manifest, Block JSON Format
  - Asset-Naming, Interactions, Rezepte
  - Java Plugin Architektur, Best Practices
- 📘 **[docs/RESOURCES.md](docs/RESOURCES.md)** - Komplette Ressourcenliste (16 Einträge)
  - Offizielle Dokumentation, Setup-Guides, Community-Hubs
  - Tools, Bibliotheken, Alternative APIs
- 📄 **[docs/RESOURCES_SUMMARY.md](docs/RESOURCES_SUMMARY.md)** - Detaillierte Ressourcen-Analyse
  - Durchgesuchte Inhalte, Best Practices
  - Setup-Workflow, Troubleshooting-Guide

### Refactoring & API Reference (Januar 2026)

- 🔧 **[docs/REFACTORING_COMPLETE.md](docs/REFACTORING_COMPLETE.md)** - Code Refactoring Changelog
  - Entfernte Custom Wrapper-Klassen (EventRegistry, CommandRegistry)
  - MEPlugin vereinfacht nach HelloPlugin-Vorbild
  - Native API-Verwendung statt Reflection
- 📘 **[docs/HYTALE_PLUGIN_REFERENCE.md](docs/HYTALE_PLUGIN_REFERENCE.md)** - Hytale API Reference
  - HelloPlugin Example & Video Tutorial
  - Command Registration Pattern
  - Event System Best Practices
- 📊 **[docs/PROJECT_STRUCTURE.md](docs/PROJECT_STRUCTURE.md)** - Finale Projektstruktur
  - 16 Java-Dateien, ~2216 Zeilen Code
  - Block System, Commands, Core Logic, Utilities

### Weitere Dokumentation

- **[docs/SETUP.md](docs/SETUP.md)** - Development Environment Setup
- **[docs/API_REFERENCE.md](docs/API_REFERENCE.md)** - Hytale API Dokumentation
- **[docs/PROJECT_STATUS.md](docs/PROJECT_STATUS.md)** - Implementierungs-Status
- **[PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md)** - Best Practices & Patterns

📑 Vollständiger Index: **[docs/INDEX.md](docs/INDEX.md)**

---

## 🗂️ Projekt-Struktur

```
HytaleAE2/
├── src/main/
│   ├── java/com/tobi/mesystem/
│   │   ├── core/          - MENetwork, MENode, MEDeviceType
│   │   ├── blocks/        - Block Implementations
│   │   ├── events/        - Event Handlers
│   │   ├── gui/           - GUI System (TODO)
│   │   └── util/          - BlockPos, Direction
│   └── resources/
│       ├── manifest.json
│       ├── Common/        - Assets (Standard-Struktur!)
│       │   ├── BlockTextures/
│       │   └── Icons/ItemsGenerated/
│       └── Server/
│           ├── Item/
│           │   ├── Items/           - Block Definitionen
│           │   ├── Recipes/         - Crafting Rezepte
│           │   ├── Interactions/    - Interaction Details
│           │   └── RootInteractions/ - Root Interactions
│           └── Languages/en-US/
├── libs/              - ChestTerminal + HyPipes Reference JARs
├── docs/              - Dokumentation
├── deploy.bat         - Windows Deployment Script
├── deploy.sh          - Linux/macOS Deployment Script
└── build.gradle       - Gradle Configuration (v0.2.0)
```

### Wichtige Änderungen

- ✅ **Common/** statt Root-Assets (Standard-konform)
- ✅ **IconProperties** für bessere Inventar-Darstellung
- ✅ **Interactions System** für nutzbare Blöcke
- ✅ **Crafting-Rezepte** für alle Items
- ✅ **Deployment Scripts** für One-Click Deployment
- ✅ **Build Optimization** mit Checkstyle & Tasks

---

## 🎮 Features

### Phase 1: Basis-Blöcke ✅

- [x] ME Cable - Netzwerk-Verbindung
- [x] ME Controller - Netzwerk-Controller
- [x] ME Terminal - Storage-Interface
- [x] Crafting-Rezepte
- [x] Interactions

### Phase 2: Storage (In Arbeit)

- [ ] ME Drive - Storage-Gehäuse
- [ ] Storage Cells (1k, 4k, 16k, 64k)
- [ ] Item-Speicherung
- [ ] Terminal GUI

### Phase 3: Import/Export (Geplant)

- [ ] Import Bus
- [ ] Export Bus
- [ ] Filtered I/O

### Phase 4: Auto-Crafting (Geplant)

- [ ] Pattern System
- [ ] Molecular Assembler
- [ ] Crafting CPU

---

## 🔧 Entwicklung

### Build & Deployment

```bash
# Standard Build
.\gradlew build

# Schneller Build ohne Tests
.\gradlew quickBuild

# Build mit Deployment (Windows)
.\deploy.bat

# Build mit Deployment (Linux/macOS)
./deploy.sh

# Projekt-Info anzeigen
.\gradlew info
```

### Debugging

Siehe [docs/HYTALE_PLUGIN_COMPLETE_GUIDE.md](docs/HYTALE_PLUGIN_COMPLETE_GUIDE.md#debugging--troubleshooting) für:

- Häufige Probleme (Missing Textures, Block nicht platzierbar, etc.)
- Log-Analyse
- Asset-Validierung

---

## 📊 Projekt-Basis

Dieses Projekt basiert auf detaillierter Analyse von:

- **HyPipes** (v1.0.5) - Network Graph-System, Custom Models
- **ChestTerminal** (v2.0.8) - GUI & Container-Interaktionen
- **Offizielle Hytale Docs** - GitBook, HytaleModding.dev

Alle Erkenntnisse dokumentiert in [HYTALE_PLUGIN_COMPLETE_GUIDE.md](docs/HYTALE_PLUGIN_COMPLETE_GUIDE.md)

---

## 📄 Lizenz

MIT License - Siehe LICENSE Datei

---

## 🤝 Contributing

Contributions willkommen! Bitte:

1. Fork das Repository
2. Feature-Branch erstellen (`git checkout -b feature/AmazingFeature`)
3. Änderungen committen (`git commit -m 'Add AmazingFeature'`)
4. Branch pushen (`git push origin feature/AmazingFeature`)
5. Pull Request öffnen

---

## Technische Details

**Digital Storage:** Items werden als `Map<String, Long>` gespeichert (wie AE2 "Energy")

**Channel System:** 8 Channels ohne Controller, 32 mit Controller

**Node Graph:** Basiert auf HyPipes' BFS Pathfinding

---

**Version:** 0.2.0  
**Letzte Aktualisierung:** 20. Januar 2026  
**Status:** Foundation Complete & Optimized ✅

## Lizenz

Private Development

## Autor

Tobi - 2026
