# HytaleAE2 - Finale Projektstruktur

## 📁 Source Code Übersicht

### Core (3 Dateien)
```
src/main/java/com/tobi/mesystem/
├── MEPlugin.java                 # Haupt-Plugin-Klasse (266 Zeilen)
│   ├── extends JavaPlugin
│   ├── Setup: ThreadPool, NetworkManager, Commands, Events
│   └── Lifecycle: setup() → start() → shutdown()
```

### Blocks (3 Block-Typen + Base)
```
blocks/
├── MEBlockBase.java              # Abstrakte Basis-Klasse für alle ME Blocks
├── MECableBlock.java             # Netzwerk-Kabel (Verbindungen)
├── MEControllerBlock.java        # Netzwerk-Controller (Power)
└── METerminalBlock.java          # Storage Terminal (GUI)
```

### Block States (3 Dateien)
```
blocks/state/
├── MECableBlockState.java        # BlockState für Cable
├── MEControllerBlockState.java   # BlockState für Controller
└── METerminalBlockState.java     # BlockState für Terminal
```

### Commands (2 Dateien)
```
commands/
├── MECommand.java                # Base Command (veraltet, TODO: entfernen)
└── MEStatusCommand.java          # /aestatus Command (AbstractPlayerCommand)
    └── Zeigt: Network Count, Items Stored
```

### Core Logic (3 Dateien)
```
core/
├── MEDeviceType.java             # Enum: CABLE, TERMINAL, CONTROLLER
├── MENetwork.java                # Network-Instanz mit Channel-Management
└── MENode.java                   # Network-Node (ein Block im Netzwerk)
```

### Utilities (3 Dateien)
```
util/
├── BlockPos.java                 # 3D Position (x,y,z) + Utility-Methoden
├── Direction.java                # Enum: NORTH, SOUTH, EAST, WEST, UP, DOWN
└── NetworkManager.java           # Multi-Dimensional Network Management
    ├── Map<UUID, Map<UUID, MENetwork>> (Dimension → Network ID → Network)
    ├── Node-Tracking und Network-Formation
    └── Maintenance: cleanupInactiveNetworks(), optimizeChannels()
```

## 📦 Asset Pack Structure

```
src/main/resources/
├── manifest.json                 # Plugin Manifest (Group, Name, Main)
├── Common/
│   ├── Blocks/                   # (leer - auto-load via JSON)
│   ├── BlockTextures/            # PNG Texturen
│   │   ├── Me_Cable.png
│   │   ├── Me_Controller.png
│   │   ├── Me_Controller_Active.png
│   │   └── Me_Terminal.png
│   └── Icons/ItemsGenerated/     # (leer)
├── Localization/
│   ├── de-DE/items.lang          # Deutsche Übersetzungen
│   └── en-US/items.lang          # Englische Übersetzungen
└── Server/Item/
    ├── Interactions/Block/       # (leer)
    ├── Items/                    # Block-Definitionen (JSON)
    │   ├── Me_Cable.json
    │   ├── Me_Controller.json
    │   └── Me_Terminal.json
    ├── Recipes/                  # Crafting-Rezepte (JSON)
    │   ├── Me_Cable_Recipe.json
    │   ├── Me_Controller_Recipe.json
    │   └── Me_Terminal_Recipe.json
    └── RootInteractions/Block/   # (leer)
```

## 🔧 Build System

```
HytaleAE2/
├── build.gradle                  # Gradle Build-Konfiguration
│   ├── shadowJar Plugin
│   ├── Dependencies: HytaleServer.jar
│   └── Target: Java 25
├── settings.gradle               # Projekt-Name
├── gradle.properties             # Version: 0.1.0-SNAPSHOT
└── libs/                         # HytaleServer.jar (nicht im Git)
```

## 📚 Dokumentation

```
docs/
├── INDEX.md                      # Haupt-Inhaltsverzeichnis
├── QUICK_START.md                # Getting Started Guide
├── DEVELOPMENT_GUIDE.md          # Development Best Practices
├── API_REFERENCE.md              # API Documentation
├── HYTALE_PLUGIN_REFERENCE.md    # ✨ NEU: HelloPlugin Reference
├── REFACTORING_COMPLETE.md       # ✨ NEU: Refactoring Changelog
└── TESTING_GUIDE.md              # Testing Instructions
```

## 🎯 Code-Statistik

| Kategorie | Dateien | Zeilen (ca.) |
|-----------|---------|--------------|
| Plugin Core | 1 | 266 |
| Blocks | 7 | ~800 |
| Commands | 2 | ~150 |
| Core Logic | 3 | ~600 |
| Utilities | 3 | ~400 |
| **Total** | **16** | **~2216** |

## 🔥 Entfernte Dateien (Refactoring)

~~`util/EventRegistry.java`~~ - Ersetzt durch `getEventRegistry()`  
~~`util/CommandRegistry.java`~~ - Ersetzt durch `getCommandRegistry()`  
~~`util/BlockRegistry.java`~~ - Ersetzt durch JSON auto-loading  
~~`events/HytaleBlockEventListenerStub.java`~~ - Ersetzt durch Lambda-Handlers  
~~`events/EventHandler.java`~~ - Annotation nicht mehr benötigt

**-5 Dateien, -800 Zeilen Code** 🎉

## 🏗️ Architecture Pattern

```
MEPlugin (Singleton)
    ├── NetworkManager (Multi-Dimension)
    │   └── Map<UUID, Map<UUID, MENetwork>>
    │       └── MENetwork
    │           └── Set<MENode>
    ├── ThreadPool (ScheduledExecutorService)
    │   └── Maintenance Tasks (5min interval)
    ├── EventRegistry
    │   ├── PlaceBlockEvent → Create Node
    │   ├── BreakBlockEvent → Remove Node
    │   └── UseBlockEvent → Open Terminal
    └── CommandRegistry
        └── MEStatusCommand → Display Stats
```

## 🚀 Deployment

**Build:**
```bash
gradlew.bat shadowJar
```

**Output:**
```
build/libs/HytaleAE2-0.1.0-SNAPSHOT.jar
```

**Installation:**
```
%APPDATA%\Hytale\UserData\Mods\HytaleAE2-0.1.0-SNAPSHOT.jar
```

**Activation:**
1. Start Hytale
2. Create/Load World
3. Right-click World → Mods
4. Enable "HytaleAE2"
5. Create World

## ✅ Status

- ✅ Kompiliert erfolgreich
- ✅ API-Compliance nach HelloPlugin-Standard
- ✅ Keine Reflection-Calls mehr
- ✅ Event-System auf native API umgestellt
- ✅ Command-System auf AbstractPlayerCommand umgestellt
- ⏳ In-Game Testing ausstehend

## 🔗 Quick Links

- [HelloPlugin Example](https://github.com/noel-lang/hytale-example-plugin)
- [Refactoring Details](./REFACTORING_COMPLETE.md)
- [API Reference](./HYTALE_PLUGIN_REFERENCE.md)
- [Build Guide](./QUICK_START.md)
