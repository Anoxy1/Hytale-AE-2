# HytaleAE2

**Applied Energistics 2 für Hytale**

Ein vollständiges ME (Matter Energy) Storage-System für Hytale, inspiriert von Applied Energistics 2 aus Minecraft.

---

## Schnellstart

```bash
# 1. Build
.\gradlew build

# 2. Single Player: Plugin-JAR → Hytale Mods/
copy build\libs\HytaleAE2-0.1.0-SNAPSHOT.jar C:\Users\<username>\AppData\Roaming\Hytale\UserData\Mods\

# 3. Dedicated Server: Plugin-JAR → Hytale plugins/
copy build\libs\HytaleAE2-0.1.0-SNAPSHOT.jar [Hytale-Server]\plugins\
```

## Was ist fertig?

- ✅ **MENetwork** - Digitales Storage-System + Channel Management
- ✅ **MENode** - Network Nodes mit Connection Logic
- ✅ **MEDeviceType** - Alle Device-Typen definiert
- ✅ **MEPlugin** - Robust initialization mit Throwable catching
- ✅ **EventHandler** - Polyfill annotation für compile-time support
- ✅ **Hytale manifest.json** - Korrektes Format (Januar 2026)
- ✅ **Mod lädt erfolgreich** - Single Player & Server kompatibel
- ✅ **BlockStateRegistry Integration** - JSON definitions + Java stubs
- ✅ **Asset Pack Structure** - Embedded asset pack mit IncludesAssetPack: true
- ⏳ **Block Implementation** - Requires HytaleServer.jar for Codecs

**Status:** ✅ Plugin Loading Successful - BlockStateRegistry Ready (Pending HytaleServer.jar)

---

## Dokumentation

Alle Dokumentation ist in `docs/`:

- **[JETZT_MACHEN.md](docs/JETZT_MACHEN.md)** - Konkrete nächste Schritte
- **[PROJECT_STATUS.md](docs/PROJECT_STATUS.md)** - Aktueller Projekt-Status
- **[BLOCKSTATE_REGISTRY_GUIDE.md](docs/BLOCKSTATE_REGISTRY_GUIDE.md)** - BlockStateRegistry Implementation
- **[HYTALE_MANIFEST_FORMAT.md](docs/HYTALE_MANIFEST_FORMAT.md)** - Correct manifest.json format
- **[ENTWICKLUNGSPLAN.md](docs/ENTWICKLUNGSPLAN.md)** - 9-Monats Entwicklungsplan
- **[START_HIER.md](docs/START_HIER.md)** - Detaillierte Anleitung
- **[SETUP.md](docs/SETUP.md)** - Setup-Details

---

## Projekt-Struktur

```
HytaleAE2/
├── src/main/java/com/tobi/mesystem/
│   ├── core/          - MENetwork, MENode, MEDeviceType
│   ├── blocks/        - Block-Implementierungen (TODO)
│   ├── gui/           - GUI-System (TODO)
│   └── util/          - BlockPos, Direction
├── libs/              - ChestTerminal + HyPipes JARs
├── docs/              - Dokumentation
└── build.gradle       - Gradle Build
```

---

## Features (Geplant)

### Phase 1 (Woche 1-2)
- ME Cable
- ME Terminal + GUI
- Digital Storage

### Phase 2 (Monat 2)
- ME Drive
- Storage Cells (1k, 4k, 16k, 64k)

### Phase 3 (Monat 3)
- Import/Export Bus
- Filtered I/O

### Phase 4 (Monat 4-6)
- Auto-Crafting
- Pattern System
- Molecular Assembler

---

## Basiert auf

- **HyPipes** (v1.0.5) - Network Graph-System
- **ChestTerminal** (v2.0.8) - GUI & Storage-Konzept

Beide Plugins wurden dekompiliert und analysiert, um die beste Basis für AE2 in Hytale zu finden.

---

## Technische Details

**Digital Storage:** Items werden als `Map<String, Long>` gespeichert (wie AE2 "Energy")

**Channel System:** 8 Channels ohne Controller, 32 mit Controller

**Node Graph:** Basiert auf HyPipes' BFS Pathfinding

---

## Version

**0.1.0-SNAPSHOT** - Foundation Phase

## Lizenz

Private Development

## Autor

Tobi - 2026
