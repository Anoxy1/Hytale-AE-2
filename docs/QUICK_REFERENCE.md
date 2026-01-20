# Hytale Plugin Development - Quick Reference

**Schnellreferenz für die wichtigsten Konzepte**

---

## 📁 Ordnerstruktur (Standard)

```
src/main/resources/
├── manifest.json              - Plugin Konfiguration
├── Common/                    - Assets (REQUIRED wenn IncludesAssetPack: true)
│   ├── BlockTextures/         - Block-Texturen (16x16px PNG)
│   └── Icons/ItemsGenerated/  - Inventar-Icons (32x32px PNG)
└── Server/
    ├── Item/
    │   ├── Items/             - Block JSON Definitionen
    │   ├── Recipes/           - Crafting-Rezepte
    │   ├── RootInteractions/  - Interaction Entry Points
    │   └── Interactions/      - Interaction Details
    └── Languages/
        └── en-US/             - Localization
```

**WICHTIG:** Assets MÜSSEN in `Common/` liegen, nicht im Root!

---

## 📦 manifest.json

```json
{
  "Id": "your_plugin_id",
  "Name": "Your Plugin Name",
  "Version": "1.0.0",
  "MinimumHytaleVersion": "1.0.0",
  "Authors": ["YourName"],
  "EntryPoint": "com.example.YourPlugin",
  "IncludesAssetPack": true
}
```

**Validation Rules:**
- ✅ Id: Alphanumeric + Underscores only (no namespaces)
- ✅ EntryPoint: Voller Class-Name
- ✅ IncludesAssetPack: true → Common/ Ordner ERFORDERLICH

---

## 🧱 Block JSON (Minimal)

```json
{
  "Id": "Me_Cable",
  "PrefabId": "Me_Cable",
  "StackSize": 64,
  "Icon": "Common/Icons/ItemsGenerated/Me_Cable.png",
  "IconProperties": {
    "Scale": [0.55, 0.55, 0.55],
    "Rotation": [45, -30, 0],
    "Translation": [0, 0, 0]
  },
  "BlockTextures": {
    "Top": "Common/BlockTextures/Me_Cable.png",
    "Bottom": "Common/BlockTextures/Me_Cable.png",
    "North": "Common/BlockTextures/Me_Cable.png",
    "East": "Common/BlockTextures/Me_Cable.png",
    "South": "Common/BlockTextures/Me_Cable.png",
    "West": "Common/BlockTextures/Me_Cable.png"
  },
  "DrawType": "Cube",
  "BlockSoundSetId": "Stone",
  "State": {
    "Definitions": {
      "Id": "me_cable"
    }
  }`n}
```

**Key Points:**
- `IconProperties` → Inventory rendering
- `DrawType`: "Cube" (einfach) oder "Model" (custom)
- `BlockSoundSetId`: Stone, Wood, Gravel, Sand (vanilla)
- `State.Definitions.Id`: Eindeutiger State-Identifier

---

## 🎨 IconProperties API

```json
"IconProperties": {
  "Scale": [0.55, 0.55, 0.55],      // Größe (Standard: [1, 1, 1])
  "Rotation": [45, -30, 0],         // X, Y, Z Rotation in Grad
  "Translation": [0, 0, 0]          // Position Offset
}
```

**Typische Werte:**
- Scale: 0.4 - 0.6 für Blöcke (zu groß = beschnitten)
- Rotation: [45, -30, 0] für isometrische Ansicht

---

## 🔧 Interactions (Nutzbare Blöcke)

### 1. Block JSON
```json
{
  "Id": "Me_Terminal",
  "IsUsable": true,
  "Interactions": {
    "Use": "me_terminal_open"
  }
}
```

### 2. RootInteractions/Block/me_terminal_open.json
```json
{
  "Type": "Block",
  "Target": "ME_Terminal_Open"
}
```

### 3. Interactions/Block/ME_Terminal_Open.json
```json
{
  "Type": "OpenContainer",
  "ContainerType": "Generic9x3"
}
```

**Container Types:**
- `Generic9x1` bis `Generic9x6`
- `PlayerInventory`
- `Workbench`

---

## 🍴 Crafting Recipe

> **⚠️ Items und Recipes sind separate Assets!**
> - Kein `"Recipe"` Feld in Item-JSON
> - Verknüpfung erfolgt automatisch über `PrimaryOutput.ItemId`

```json
{
  "TimeSeconds": 1,
  "Input": [
    {
      "ItemId": "Ingredient_Bar_Iron",
      "Quantity": 1
    },
    {
      "ItemId": "Ingredient_Bar_Copper",
      "Quantity": 2
    }
  ],
  "PrimaryOutput": {
    "ItemId": "Me_Cable",
    "Quantity": 8
  },
  "BenchRequirement": [
    {
      "Type": "Crafting",
      "Id": "Workbench",
      "Categories": ["Workbench_Tinkering"]
    }
  ]
}
```

**Recipe Types:**
- `Workbench` - Crafting Table
- `Furnace` - Smelting
- `Anvil` - Smithing

**Categories:**
- `Workbench_Building`
- `Workbench_Tinkering`
- `Workbench_Cooking`

---

## ☕ Java Plugin

```java
package com.example;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.PluginLogger;

public class MyPlugin extends JavaPlugin {
    
    private static MyPlugin instance;
    private PluginLogger logger;
    
    @Override
    public void onLoad() {
        instance = this;
        logger = getPluginLogger();
        logger.info("Plugin loading...");
    }
    
    @Override
    public void onEnable() {
        logger.info("Plugin enabled!");
    }
    
    @Override
    public void onDisable() {
        logger.info("Plugin disabled!");
    }
    
    public static MyPlugin getInstance() {
        return instance;
    }
}
```

**Lifecycle:**
1. `onLoad()` - Initialisierung (Config, Core Systems)
2. `onEnable()` - Start (Events, Commands, Block Registration)
3. `onDisable()` - Cleanup (Save Data, Unregister)

---

## 🎯 Event Handler

```java
@EventHandler
public void onBlockInteract(BlockInteractEvent event) {
    Player player = event.getPlayer();
    BlockState block = event.getBlockState();
    
    if (block.getDefinition().getId().equals("me_terminal")) {
        // Custom logic hier
        player.sendMessage("Terminal opened!");
        event.setCancelled(true);
    }
}
```

**Wichtige Events:**
- `BlockInteractEvent` - Block-Interaktion
- `BlockPlaceEvent` - Block platziert
- `BlockBreakEvent` - Block abgebaut
- `PlayerJoinEvent` - Spieler joined

---

## 🐛 Troubleshooting

| Problem | Ursache | Lösung |
|---------|---------|--------|
| **Purple-Black Checkerboard** | Fehlende/falsche Texture | Prüfe `BlockTextures/` Pfade + PNG-Dateien |
| **Block nicht platzierbar** | Ungültiges JSON | Validiere Block JSON Schema |
| **"Asset not found"** | Assets nicht in `Common/` | Verschiebe zu `Common/BlockTextures/` |
| **"Incorrect format" Warning** | Falsche Naming | PascalCase: `Me_Cable.json` |
| **BlockSoundSetId ungültig** | Custom Sound existiert nicht | Verwende: Stone, Wood, Gravel, Sand |
| **Interaction funktioniert nicht** | Fehlende RootInteraction | Erstelle beide JSON-Dateien |

---

## 📋 Build & Deploy

```bash
# Build
.\gradlew clean build

# Single Player Deploy
copy build\libs\*.jar %APPDATA%\Hytale\UserData\Mods\

# Server Deploy
copy build\libs\*.jar [ServerPath]\plugins\

# Logs prüfen
type %APPDATA%\Hytale\UserData\Logs\*_client.log | findstr /i "pluginname"
```

---

## 📚 Weitere Ressourcen

- **[HYTALE_PLUGIN_COMPLETE_GUIDE.md](HYTALE_PLUGIN_COMPLETE_GUIDE.md)** - Vollständiger Leitfaden
- **[API_REFERENCE.md](API_REFERENCE.md)** - API-Dokumentation
- **[PROJECT_STATUS.md](PROJECT_STATUS.md)** - HytaleAE2 Status

---

**Letzte Aktualisierung:** 20. Januar 2026  
**Basiert auf:** HyPipes v1.0.5, ChestTerminal v2.0.8, Offizielle Hytale Docs
