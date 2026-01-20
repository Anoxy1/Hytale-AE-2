# Dokumentation - HytaleAE2

Alle Projektdokumentation an einem Ort.

---

## Einstieg

**Neu hier? Starte hier:**

1. **[JETZT_MACHEN.md](JETZT_MACHEN.md)** - Die nächsten konkreten Schritte
2. **[PROJECT_STATUS.md](PROJECT_STATUS.md)** - Was ist fertig? Was fehlt?
3. **[START_HIER.md](START_HIER.md)** - Detaillierte Schritt-für-Schritt Anleitung

---

## Entwicklung

**Für die Implementierung:**

- **[ENTWICKLUNGSPLAN.md](ENTWICKLUNGSPLAN.md)** - Vollständiger 9-Monats-Plan
  - Phase 1: Foundation & MVP (Woche 1-2)
  - Phase 2: Storage Cells (Monat 2)
  - Phase 3: Import/Export (Monat 3)
  - Phase 4: Auto-Crafting (Monat 4-6)

---

## Setup & Konfiguration

**Technische Details:**

- **[SETUP.md](SETUP.md)** - Setup-Anleitung
- **[DATEIEN_INFO.md](DATEIEN_INFO.md)** - Datei-Übersicht

---

## Dokumenten-Übersicht

| Dokument | Zweck | Wann lesen? |
|----------|-------|-------------|
| **JETZT_MACHEN.md** | Konkrete nächste Schritte | Jetzt sofort |
| **PROJECT_STATUS.md** | Aktueller Status | Für Überblick |
| **ENTWICKLUNGSPLAN.md** | Langfristige Planung | Vor Implementierung |
| **START_HIER.md** | Detaillierte Anleitung | Setup-Phase |
| **SETUP.md** | Setup-Details | Bei Problemen |
| **DATEIEN_INFO.md** | Datei-Referenz | Zur Orientierung |

---

## Wichtigste Erkenntnisse

### Aus ChestTerminal
- InteractiveCustomUIPage Pattern für GUIs
- Digital Storage mit HashMap<String, Integer>
- Event-basierte Interaktionen

### Aus HyPipes
- Graph-basiertes Network (Map<BlockPos, PipeNode>)
- BFS Pathfinding für Routing
- Priority + Distribution Strategies

### Hytale Modding
- Codec-System statt NBT
- JavaPlugin mit setup() & start()
- BlockState Registry für Blocks

---

## Code-Beispiele

### Digital Storage
```java
MENetwork network = new MENetwork();
network.storeItem("minecraft:diamond", 64);
long diamonds = network.getStoredAmount("minecraft:diamond");
```

### Channel System
```java
network.allocateChannel(pos, 1);
network.setController(controllerPos); // 8 -> 32 channels
```

### Node Management
```java
MENode node = new MENode(worldId, pos, MEDeviceType.CABLE);
network.addNode(node);
node.addConnection(Direction.NORTH);
```

---

## Schnelle Navigation

- **Zurück zum Projekt:** [../README.md](../README.md)
- **Quellcode:** [../src/main/java/com/tobi/mesystem/](../src/main/java/com/tobi/mesystem/)
- **Build:** [../build.gradle](../build.gradle)
