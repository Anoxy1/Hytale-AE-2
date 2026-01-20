# Hytale Plugin Development - Vollständiger Leitfaden

**Basierend auf:** Analyse von HyPipes, ChestTerminal & offizieller Dokumentation  
**Letzte Aktualisierung:** 20. Januar 2026  
**Status:** Production-Ready Knowledge Base

---

## 📚 Inhaltsverzeichnis

1. [Ordnerstruktur](#ordnerstruktur)
2. [Manifest.json](#manifestjson)
3. [Block JSON Format](#block-json-format)
4. [Asset-Struktur](#asset-struktur)
5. [Interactions System](#interactions-system)
6. [Crafting-Rezepte](#crafting-rezepte)
7. [Java Plugin Architektur](#java-plugin-architektur)
8. [Best Practices](#best-practices)

---

## Ordnerstruktur

### ✅ OFFIZIELLER STANDARD (Empfohlen)

```
src/main/resources/
├── manifest.json              # Plugin-Metadaten
├── Common/                    # Assets (PFLICHT bei IncludesAssetPack: true)
│   ├── BlockTextures/         # Block-Texturen
│   │   ├── Me_Cable.png
│   │   ├── Me_Controller.png
│   │   └── Me_Terminal.png
│   ├── Blocks/                # .blockymodel Dateien (optional)
│   └── Icons/
│       └── ItemsGenerated/    # Inventar-Icons
│           ├── Me_Cable.png
│           ├── Me_Controller.png
│           └── Me_Terminal.png
└── Server/
    ├── Item/
    │   ├── Items/             # Block/Item Definitionen
    │   │   ├── Me_Cable.json
    │   │   ├── Me_Controller.json
    │   │   └── Me_Terminal.json
    │   ├── Recipes/           # Crafting-Rezepte
    │   │   ├── Me_Cable_Recipe.json
    │   │   └── ...
    │   ├── Interactions/Block/   # Interaction-Details
    │   │   └── ME_Terminal_Open.json
    │   └── RootInteractions/Block/  # Root Interactions
    │       └── me_terminal_open.json
    └── Languages/
        └── en-US/
            └── server.lang    # Übersetzungen
```

### ❌ ALTE STRUKTUR (Nicht Standard)
```
resources/
├── BlockTextures/    # ❌ Direkt im Root (funktioniert, aber nicht Standard)
├── Icons/            # ❌ Sollte in Common/ sein
└── Server/
```

**Warum Common/?** 
- Alle funktionierenden Plugins (HyPipes, ChestTerminal) nutzen `Common/`
- Offizielle Dokumentation empfiehlt `Common/`
- Trennung zwischen Client (Common) und Server Assets

---

## Manifest.json

### Vollständiges Beispiel

```json
{
  "Group": "YourName",
  "Name": "PluginName",
  "Version": "1.0.0",
  "Main": "com.yourname.plugin.MainClass",
  "Description": "Plugin description",
  "Authors": [
    {
      "Name": "Your Name",
      "Email": "optional@email.com",
      "Url": "https://optional-website.com"
    }
  ],
  "Website": "https://optional-website.com",
  "ServerVersion": "*",
  "Dependencies": {},
  "OptionalDependencies": {},
  "LoadBefore": {},
  "DisabledByDefault": false,
  "IncludesAssetPack": true,    // KRITISCH für Assets!
  "SubPlugins": []
}
```

### Wichtige Properties

| Property | Erforderlich | Beschreibung |
|----------|--------------|--------------|
| `Group` | ✅ Ja | Autor/Organisation |
| `Name` | ✅ Ja | Plugin-Name |
| `Version` | ✅ Ja | Semantische Versionierung |
| `Main` | ✅ Ja | Vollständiger Klassenname der Hauptklasse |
| `IncludesAssetPack` | ⚠️ Wenn Assets | **true** wenn Plugin Assets enthält |
| `ServerVersion` | Nein | `"*"` für alle Versionen |

---

## Block JSON Format

### Minimales Beispiel

```json
{
  "PlayerAnimationsId": "Block",
  "Categories": ["Furniture.Furniture"],
  "Set": "",
  "Icon": "Common/Icons/ItemsGenerated/My_Block.png",
  
  "BlockType": {
    "BlockSoundSetId": "Stone",
    "DrawType": "Cube",
    "Material": "Solid",
    "State": {
      "Definitions": {
        "Id": "my_block"
      }
    }
  },
  
  "TranslationProperties": {
    "Name": "items.my_block.name",
    "Description": "items.my_block.description"
  }
}
```

### Vollständiges Beispiel (mit allen Features)

```json
{
  "PlayerAnimationsId": "Block",
  "Categories": ["Furniture.Furniture"],
  "Set": "",
  "Icon": "Common/Icons/ItemsGenerated/Me_Terminal.png",
  "IconProperties": {
    "Scale": 0.55,
    "Rotation": [20, 45, 20],
    "Translation": [0, -10]
  },

  "BlockType": {
    "components": {
      "hytale:block_entity": {}  // Macht Block zu BlockEntity
    },
    "Supporting": {
      "Up": [{"FaceType": "Full"}],
      "Down": [{"FaceType": "Full"}],
      "North": [{"FaceType": "Full"}],
      "South": [{"FaceType": "Full"}],
      "East": [{"FaceType": "Full"}],
      "West": [{"FaceType": "Full"}]
    },
    "Support": {
      "Down": [{"FaceType": "Full"}]
    },
    "BlockSoundSetId": "Stone",
    "DrawType": "Cube",
    "Flags": {
      "IsUsable": true
    },
    "Interactions": {
      "Use": "me_terminal_open",
      "Primary": "Break_Container"
    },
    "InteractionHint": "server.interactionHints.open",
    "HitboxType": "Full",
    "Gathering": {
      "Soft": {}
    },
    "Material": "Solid",
    "ParticleColor": "#3070ff",
    "State": {
      "Definitions": {
        "Id": "me_terminal"
      }
    }
  },

  "TranslationProperties": {
    "Name": "items.me_terminal.name",
    "Description": "items.me_terminal.description"
  }
}
```

> **⚠️ WICHTIG:** Items und Recipes sind **separate Assets**!
> - Das `"Recipe"` Feld gibt es **nicht** auf Item-Ebene
> - Die Verknüpfung erfolgt automatisch über die Dateinamen/IDs in den Recipe-Dateien
> - Recipe-Dateien referenzieren Items über `PrimaryOutput.ItemId`

```json
```

### Wichtige Properties erklärt

#### IconProperties
```json
"IconProperties": {
  "Scale": 0.55,              // Größe im Inventar (0.0 - 1.0)
  "Rotation": [20, 45, 20],   // [X, Y, Z] Rotation in Grad
  "Translation": [0, -10]     // [X, Y] Verschiebung in Pixeln
}
```
→ Maps to: `com.hypixel.hytale.server.core.asset.type.item.config.AssetIconProperties`

#### DrawType Optionen

| DrawType | Verwendung | Benötigt |
|----------|------------|----------|
| `"Cube"` | Einfacher Würfel | Nur Texturen |
| `"Model"` | 3D-Modell aus Blockbench | `CustomModel` + `CustomModelTexture` |

**Bei Model:**
```json
"DrawType": "Model",
"CustomModel": "Blocks/MyModel.blockymodel",
"CustomModelTexture": [{
  "Texture": "Common/BlockTextures/my_texture.png"
}],
"CustomModelScale": 1.0,
"Opacity": "Transparent"  // oder "Semitransparent"
```

#### State-Struktur (Zwei Varianten)

**Variante 1: Mit Definitions (HyPipes, HytaleAE2)**
```json
"State": {
  "Definitions": {
    "Id": "my_block"
  }
}
```

**Variante 2: Direkt (ChestTerminal)**
```json
"State": {
  "Id": "ChestTerminal_Block",
  "Capacity": 27
}
```

**Beide funktionieren!** Variante 1 ist flexibler für Multi-State Blöcke.

#### Components

```json
"components": {
  "hytale:block_entity": {}  // Macht Block zu BlockEntity (für Inventar/Daten)
}
```

---

## Asset-Struktur

### Datei-Naming Konventionen

**✅ RICHTIG:**
- `Me_Cable.png` (PascalCase mit Unterstrichen)
- `Me_Controller.png`
- `ChestTerminal_Block.png`

**❌ FALSCH:**
- `me_cable.png` (lowercase)
- `MeCable.png` (ohne Unterstriche)
- `me-cable.png` (Bindestriche)

### Asset IDs

**✅ RICHTIG:**
- `me_terminal`
- `pipe_data`
- `ChestTerminal_Block`

**❌ FALSCH:**
- `hytaleae2:me_terminal` (Namespace nicht erlaubt!)
- `me-terminal` (Bindestriche nicht erlaubt)
- `me.terminal` (Punkte nicht erlaubt)

**Regel:** Nur alphanumerisch + Unterstriche, global eindeutig.

### Textur-Anforderungen

| Typ | Empfohlene Größe | Format |
|-----|------------------|--------|
| Block-Texturen | 16x16 bis 64x64 | PNG |
| Icons | 16x16 oder 32x32 | PNG |
| Mod-Logo | 512x512 | PNG |

---

## Interactions System

### RootInteraction (Einstiegspunkt)

**Pfad:** `Server/Item/RootInteractions/Block/me_terminal_open.json`

```json
{
  "Type": "Block",
  "Id": "me_terminal_open",
  "TranslationKey": "interactions.me_terminal.open",
  "Target": "ME_Terminal_Open"
}
```

### Interaction (Detail-Konfiguration)

**Pfad:** `Server/Item/Interactions/Block/ME_Terminal_Open.json`

```json
{
  "Type": "OpenContainer",
  "ContainerType": "Generic9x3",
  "TranslationKey": "container.me_terminal.title"
}
```

### Interaction Types

| Type | Beschreibung |
|------|--------------|
| `OpenContainer` | Öffnet Container/GUI |
| `Custom` | Eigene Java-Implementierung |

### Verwendung in Block JSON

```json
"Interactions": {
  "Use": "me_terminal_open",      // Rechtsklick
  "Primary": "Break_Container"     // Linksklick/Abbauen
}
```

**Standard-Interactions:**
- `Open_Container` - Vanilla Container
- `Break_Container` - Abbau mit Inventory-Drop

---

## Crafting-Rezepte

### Basis-Struktur

**Pfad:** `Server/Item/Recipes/Me_Cable_Recipe.json`

> **⚠️ WICHTIG - Recipe Format seit Hytale Update:**
> - Verwende `PrimaryOutput: { ItemId, Quantity }` statt `OutputItemId` + `OutputQuantity`
> - Items und Recipes sind **separate Assets** - kein `Recipe` Feld in Item-JSON!
> - Die Verknüpfung erfolgt automatisch über die Item-ID im `PrimaryOutput`

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

### Verknüpfung zwischen Items und Recipes

**Items werden NICHT im Item-JSON referenziert:**
```json
// ❌ FALSCH - Recipe Feld existiert nicht!
{
  "TranslationProperties": { ... },
  "Recipe": "Me_Cable_Recipe"  // <- Führt zu Parse-Error!
}

// ✅ RICHTIG - Keine Recipe-Referenz in Item-JSON
{
  "TranslationProperties": { ... }
}
```

**Verknüpfung erfolgt automatisch:**
- Recipe-Datei definiert `PrimaryOutput.ItemId: "Me_Cable"`
- Hytale verknüpft automatisch mit Item `Me_Cable.json`
- Dateinamen müssen übereinstimmen!

### Vanilla Zutaten (Beispiele)

- `Ingredient_Bar_Iron`
- `Ingredient_Bar_Copper`
- `Ingredient_Dust_Redstone`
- `Ingredient_Glass`
- `Wood_Trunk` (ResourceTypeId!)

### Bench Types

| Type | Id | Categories |
|------|-----|-----------|
| Crafting | `Workbench` | `Workbench_Tinkering` |
| Crafting | `Fieldcraft` | ... |

---

## Java Plugin Architektur

### Plugin-Klassen-Hierarchie

```
PluginBase (abstract)
  ↓
JavaPlugin (extends PluginBase)
  ↓
YourPlugin (extends JavaPlugin)
```

### Minimale Plugin-Klasse

```java
package com.yourname.plugin;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import javax.annotation.Nonnull;

public class MyPlugin extends JavaPlugin {
    
    public MyPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }
    
    @Override
    protected void setup() {
        getLogger().info("MyPlugin is setting up!");
        
        // Hier: Commands & Events registrieren
    }
    
    @Override
    protected void start() {
        getLogger().info("MyPlugin started!");
    }
    
    @Override
    protected void shutdown() {
        getLogger().info("MyPlugin shutting down...");
    }
}
```

### Plugin-Lebenszyklus

1. **Constructor** - Plugin wird initialisiert
2. **`setup()`** - Commands & Events registrieren (parallel mit anderen Plugins)
3. **`start()`** - Plugin startet (nach allen setup()s)
4. **`shutdown()`** - Plugin wird beendet

### Event-Registrierung

```java
@Override
protected void setup() {
    // Event-Handler registrieren
    getEventRegistry().register(PlayerJoinEvent.class, event -> {
        event.getPlayer().sendMessage(
            Message.raw("Welcome!")
        );
    });
}
```

### Command-Registrierung

```java
@Override
protected void setup() {
    getCommandRegistry().registerCommand(new MyCommand());
}

private class MyCommand extends CommandBase {
    public MyCommand() {
        super("mycommand", "mycommand.description");
    }
    
    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        context.sendMessage(Message.raw("Hello!"));
    }
}
```

---

## Best Practices

### 1. Asset-Organisation

✅ **DO:**
- Verwende `Common/` für alle Assets
- PascalCase mit Unterstrichen für Dateinamen
- Konsistente Namensgebung zwischen JSON und Assets

❌ **DON'T:**
- Assets direkt im Root-Ordner
- Namespace-Präfixe in Asset-IDs (`plugin:asset_id`)
- Bindestriche oder Punkte in Namen

### 2. Block-Definitionen

✅ **DO:**
- Immer `Icon` Property setzen
- `IconProperties` für bessere Inventar-Darstellung hinzufügen
- `Interactions` für nutzbare Blöcke definieren
- Rezepte für craftbare Items hinzufügen

❌ **DON'T:**
- Interactions ohne entsprechende RootInteraction-Dateien
- Custom Interactions ohne Java-Implementierung
- Asset-Pfade ohne `Common/`-Präfix

### 3. Performance

✅ **DO:**
- BlockEntities nur wenn nötig (`hytale:block_entity` component)
- Effiziente Event-Handler (keine schweren Operationen)
- Logger für Debugging verwenden

❌ **DON'T:**
- Synchrone I/O in Event-Handlern
- Exzessives Logging in Produktions-Code
- BlockEntities für statische Blöcke

### 4. Fehlerbehandlung

✅ **DO:**
```java
@Override
protected void setup() {
    try {
        // Setup code
        getLogger().info("Setup successful");
    } catch (Exception e) {
        getLogger().error("Setup failed", e);
    }
}
```

❌ **DON'T:**
- Exceptions verschlucken
- Fehler ohne Logging
- Plugin-Crash ohne Cleanup

### 5. Manifest-Konfiguration

✅ **DO:**
- `IncludesAssetPack: true` wenn Assets vorhanden
- Semantische Versionierung (`1.0.0`, `1.1.0`, etc.)
- Korrekte Dependencies angeben

❌ **DON'T:**
- `IncludesAssetPack: false` bei vorhandenen Assets
- Version "1" oder "latest"
- Fehlende obligatorische Felder

---

## Debugging & Troubleshooting

### Häufige Probleme

#### Problem: Lila-schwarzes Schachbrett (Missing Texture)

**Ursachen:**
- Textur-Dateien fehlen oder falsch benannt
- Falsche Pfade im JSON (fehlendes `Common/`)
- Textur-Dateien zu klein (< 100 Bytes = Korrupt)

**Lösung:**
```bash
# Dateigrößen prüfen
dir /s *.png

# Pfade im JSON prüfen
"Icon": "Common/Icons/ItemsGenerated/Block.png"
```

#### Problem: Block nicht platzierbar

**Ursachen:**
- Fehlende `Support` Definition
- Ungültige `Interactions` Referenz
- Asset-Validierung fehlgeschlagen

**Lösung:**
```json
"Support": {
  "Down": [{"FaceType": "Full"}]
}
```

#### Problem: Asset Key Format Warnung

**Log:** `Asset key 'me_cable' for file '/Server/Item/Items/me_cable.json' has incorrect format! Expected: 'Me_Cable'`

**Lösung:** Dateinamen muss PascalCase sein: `Me_Cable.json`

#### Problem: BlockSoundSet nicht gefunden

**Log:** `Unknown block sound set 'Block_Metal' for block 'me_terminal', using empty`

**Lösung:** Verwende gültige Sound-Sets:
- `Stone`
- `Wood`
- `Clay_Pot_Small`
- Nicht: `Block_Metal` (existiert nicht)

---

## Referenzen

### Offizielle Dokumentation
- [Hytale Modding Documentation (GitBook)](https://britakee-studios.gitbook.io/)
- [HytaleModding.dev](https://hytalemodding.dev/)
- [Hytale-Docs.com](https://hytale-docs.com/)

### Funktionierende Beispiele
- **HyPipes** v1.0.5 - Network-System, Custom Models
- **ChestTerminal** v2.0.8 - Container, Interactions
- **Kaupenjoe YouTube** - Plugin Tutorials

### Wichtige API-Klassen
- `com.hypixel.hytale.server.core.plugin.JavaPlugin`
- `com.hypixel.hytale.server.core.plugin.JavaPluginInit`
- `com.hypixel.hytale.server.core.asset.type.item.config.AssetIconProperties`
- `com.hypixel.hytale.server.core.command.system.basecommands.CommandBase`
- `com.hypixel.hytale.server.core.Message`

---

**Letztes Update:** 20. Januar 2026  
**Basierend auf:** HyPipes v1.0.5, ChestTerminal v2.0.8, Offizielle Docs  
**Status:** Production-Ready
