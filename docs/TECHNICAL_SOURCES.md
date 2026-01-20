# Technische Quellen & Detaillierte Referenzen

> Umfassende technische Quellen für Hytale Plugin-Entwicklung, basierend auf offiziellen Dokumentationen und erweiterten Referenzen.

## AI-Übersicht (was, wann, wohin)
- Wann lesen? Bei Server-/World-Config, Szenarien, Performance-Tuning, Access-Control.
- Was drin? Server config, World config, Szenario-Profile, Access Control, Plugin-spezifische Configs.
- Direkt springen: [Server config](#21-haupt-server-konfiguration-configjson) · [World config](#22-weltspezifische-konfiguration-universeworldsworld_nameconfigjson) · [Szenarien](#szenarien-konfigurationen) · [Access Control](#23-access-control-files) · [Plugin configs](#24-plugin-spezifische-konfiguration)
- AI-Hub: [AGENT_ONBOARDING.md#ai-agent-start](AGENT_ONBOARDING.md#ai-agent-start)

### Digest (maschinenfreundlich)
| Zweck | Abschnitte | Nutze wenn |
| --- | --- | --- |
| Server-Basis | 2.1 Haupt-Server-Konfiguration | Du config.json anpassen willst |
| Welt-Config | 2.2 Weltspezifische Konfiguration | Du world-spezifische Settings änderst |
| Szenarien | Szenarien-Konfigurationen | Du Kreativ/PvP/Performance-Profile brauchst |
| Access Control | 2.3 Access Control Files | Du permissions/whitelist/bans checkst |
| Plugin-Config | 2.4 Plugin-spezifische Konfiguration | Du pluginbezogene Keys pflegen willst |

## 1. Offizielle Primärquellen

### Hytale Server & Runtime
- **Version**: Hytale Server v2026.01.17-4b0f30090
- **Offizielle Seite**: support.hytale.com – Hytale Server Manual
- **API Level**: 2026 Q1 Schema
- **Protokoll**: QUIC über UDP (nicht TCP)
- **Standard-Port**: 25565
- **Java-Version**: Java 25 LTS (Temurin)

### Getting Started with Plugins
- **Quelle**: britakee-studios GitBook
- **URL**: https://britakee-studios.gitbook.io/hytale-plugin-development/
- **Abdeckung**: 
  - Plugin-Struktur & Manifest-Format
  - Basis-Event-Handler
  - Block- und Item-Registrierung
  - Container- und Inventar-Systeme
  - Netzwerk-Synchronisation

### Offizielle Beispiel-Implementierung
- **Repository**: noel-lang/hytale-example-plugin
- **Name**: HelloPlugin
- **Abdeckung**:
  - Minimale Plugin-Struktur
  - Basic Block/Item Registrierung
  - Simple Event Handler
  - Gradle-Build-Setup
  - Manifest.json Beispiel

### Modding-Strategie & Philosophie
- **Quelle**: Hytale News Archive
- **Autor**: Technical Director Slikey
- **Themen**:
  - Plugin-API Roadmap
  - Modding-Community Guidelines
  - Performance-Expectations
  - Ökosystem-Strategie

## 2. Erweiterte JSON-Konfigurationsdateien

Hytale nutzt JSON als primäres Dateiformat für Konfiguration von Spielinhalten und Servereinstellungen. Diese Dateien ermöglichen einen datengesteuerten Ansatz zur Erstellung und Modifikation von Inhalten – ein Großteil der Spielmechaniken ist nicht hartcodiert, sondern über JSON definierbar.

### 2.1 Haupt-Server-Konfiguration (config.json)

Die zentrale Servereinstellungs-Datei mit kritischen Leistungsparametern:

```json
{
  "ServerName": "My Hytale Server",
  "MOTD": "Welcome to the custom server!",
  "Password": "",                    // Leerer String = öffentlicher Server
  "MaxPlayers": 100,
  "MaxViewRadius": 12,               // Empfohlen: 12-16 Chunks
  "LocalCompressionEnabled": true,   // Bandbreitenoptimierung
  "Port": 25565,
  "Version": 1,
  "Defaults": {
    "World": "default",
    "GameMode": "ADVENTURE"          // oder "CREATIVE"
  },
  "ConnectionTimeouts": {},
  "RateLimit": {},                   // DDoS-Schutz
  "PlayerStorage": {
    "Type": "Hytale"
  },
  "DisplayTmpTagsInStrings": false,
  "LogLevels": {
    "Default": "INFO",
    "Network": "WARN"
  }
}
```

**Kritische Parameter für Performance:**
- **MaxPlayers**: Direkter Einfluss auf Serverlast. Höhere Werte benötigen mehr CPU/RAM
- **MaxViewRadius**: Chunk-Radius um Spieler herum. Verdopplung = 4x CPU/Bandbreite
- **LocalCompressionEnabled**: true reduziert Netzwerknutzung; wichtig für öffentliche Server
- **LogLevels**: DEBUG/TRACE für Debugging, aber erzeugt viel Datenvolumen

⚠️ **Wichtig**: Änderungen nur bei gestopptem Server vornehmen. Backup vor Änderungen erstellen!

### 2.2 Weltspezifische Konfiguration (universe/worlds/<world_name>/config.json)

Jede Welt hat eigene config.json für granulare Anpassung:

```json
{
  "UUID": "auto-generated",
  "DisplayName": "Default World",
  "Seed": 1768666912427,
  "WorldGen": {
    "Type": "Hytale",              // oder "Flat", "Empty"
    "Name": "Default",
    "Path": "WorldGenTemplates/Orbis_Default.json"
  },
  "ChunkStorage": {
    "Type": "Hytale"               // oder "IndexedStorage", "Empty"
  },
  "ChunkConfig": {
    "PregenerateRegion": { "Min": [0, 0], "Max": [100, 100] },
    "KeepLoadedRegion": { "Min": [0, 0], "Max": [50, 50] }
  },
  "GameRules": {
    "IsPvpEnabled": false,
    "IsFallDamageEnabled": true,
    "IsGameTimePaused": false,
    "GameTime": "0001-01-01T05:30:00Z",  // ISO 8601 Format
    "GameplayConfig": "Default",
    "DaytimeDurationSeconds": 600,
    "NighttimeDurationSeconds": 600
  },
  "ClientEffects": {
    "SunIntensity": 1.0,
    "BloomIntensity": 0.5,
    "SunAngleDegrees": 45.0,
    "ForcedWeather": "Clear"       // oder "Rain", "Snow"
  },
  "NPCs": {
    "IsSpawningNPC": true,
    "IsAllNPCFrozen": false
  },
  "System & Saving": {
    "IsTicking": true,
    "IsBlockTicking": true,
    "IsSavingPlayers": true,
    "IsUnloadingChunks": true,
    "SaveNewChunks": true,
    "DeleteOnUniverseStart": false,
    "DeleteOnRemove": false,
    "ResourceStorage": { "Type": "Hytale" }
  },
  "RequiredPlugins": {
    "myplugin.id": ">=1.0.0 <2.0.0"
  },
  "Plugin": {
    "myplugin.config.key": "value"
  }
}
```

**Szenarien-Konfigurationen:**
- **Kreativwelt (safe building)**: IsPvpEnabled=false, IsFallDamageEnabled=false, IsGameTimePaused=true
- **Survival-Arena (fast paced)**: IsPvpEnabled=true, IsSpawningNPC=true, DaytimeDurationSeconds=120
- **Performance (server optimization)**: IsUnloadingChunks=true, kleinere KeepLoadedRegion

### 2.3 Access Control Files

- **permissions.json**: Rolle-basierte Zugriffskontrolle für Admin/Mod/Builder
- **bans.json**: Automatisch verwaltet – nicht manuell bearbeiten
- **whitelist.json**: Definiert erlaubte Spieler (privaten Server)

### 2.4 Plugin-spezifische Konfiguration

Plugins können eigene config.json mit Codec-System-Serialisierung haben:

```json
{
  "LuckIncreaseChance": 0.40,
  "MaxPlayersAllowed": 100,
  "ServerWelcomeMessage": "Welcome to the custom server!",
  "EnableCustomFeature": true,
  "BannedChatWords": ["badword1", "badword2"],
  "DropRateMultiplier": 1.5,
  "DebugModeActive": false,
  "CustomItemRecipeEnabled": {
    "IronGolemSpawner": true,
    "MagicWand": false
  }
}
```

## 3. Daten-Struktur Referenzen

### Item-Konfiguration
**Kernattribute:**
```
Id: string (unique identifier)
TranslationProperties: { key, namespace }
Icon: { assetId, offset, scale }
Categories: string[]
ItemLevel: number (1-200)
MaxStackSize: number
Quality: "Common" | "Uncommon" | "Rare" | "Epic" | "Legendary"
Set: string (optional, for item sets)
```

**Rendering:**
```
Model: { modelId, scale, rotation }
Texture: { assetId, scale }
Animation: { animationId, duration, loop }
DroppedItemAnimation: { type, speed }
UsePlayerAnimations: boolean
Particles: { systemId, scale, color }
FirstPersonParticles: { systemId, scale, color }
Trails: { trailId, color, opacity }
Light: { red, green, blue, brightness }
```

**Interaktionen:**
```
InteractionTypes:
  - Serial (Schritte)
  - Simple (Einmal-Aktion)
  - Custom (Java-registriert)
  - Charging (mit Dauer)
  - Condition (Crouching, etc.)
```

**Tool-Konfiguration:**
```
ToolConfiguration:
  Specs: [
    { GatherType: "Rocks|Ore|Wood|...", Power, Quality },
    ...
  ]
  Speed: multiplier (float)
  MaxDurability: number
  DurabilityLossBlockTypes: { blockId: loss }
  DroppedItemConfig:
    PhysicsValues: { ItemMass, FrictionCoefficient, ApplyGravity }
    PickupRadius: float
    ttl: float (seconds)
    ParticleSystemId: string
    ParticleColor: hex string
```

**Waffen-Konfiguration:**
```
WeaponConfiguration:
  StatModifiers: { Damage, AttackSpeed }
  RenderDualWielded: boolean
  Knockback: float
  CooldownTime: float
```

**Rüstungs-Konfiguration:**
```
ArmorConfiguration:
  EquipmentSlot: "Head|Chest|Legs|Feet|Hands"
  BaseDamageResistance: float (0.0-1.0)
  DamageResistance:
    FireResistance: float
    ColdResistance: float
    [... type-specific ...]
  CosmeticsToHide: string[]
```

### Block-Konfiguration
**Kernattribute:**
```
Id: string
TranslationProperties: { key, namespace }
MaxStack: number
Icon: { assetId, ... }
Categories: string[]
PlayerAnimationsId: string (optional)
Set: string (optional)
```

**BlockType Struktur:**
```
BlockType:
  Material: "Solid" | "Empty" | "Liquid"
  DrawType: "Cube" | "Model" | "CubeWithModel"
  Group: string (für Block-Gruppen)
  Flags:
    CanRotate: boolean
    CanVariate: boolean
  Gathering:
    Breaking:
      GatherType: "Rock|Ore|Wood|..."
      ItemId: string
      HarvestLevel: number
  BlockParticleSetId: string
  Textures:
    - Face: "TopFace|BottomFace|NorthFace|SouthFace|EastFace|WestFace"
      TextureId: string
      VariantWeight: float (für Varianten)
  ParticleColor: hex string
  BlockSoundSetId: string
  BlockBreakingDecalId: string
```

**Ressourcentypen (für Gathering):**
- Rock, Ore, Wood, Sand, Dirt, Grass, Ice, Water, Lava, etc.

### NPC-Konfiguration
```
Id: string
Name: string (localized)
Behaviors:
  - WanderBehavior: { range, speed, pauseDuration }
  - TradeBehavior: { items, prices, cooldown }
  - [weitere...]
DialogueReferences: string[]
SpawnConditions:
  BiomeTags: string[]
  TimeOfDay: "Day" | "Night" | "Any"
  MinPlayersNearby: number
  MaxPlayersNearby: number
AIParameters:
  AggressionLevel: float (0.0-1.0)
  PerceptionRadius: float
  ReactionTime: float
EntitySpawns: []
```

### Loot-Tabellen
```
pools: [
  {
    rolls: number (oder RandomRange)
    bonusRolls: number (oder condition-based)
    entries: [
      {
        type: "item" | "loot_table" | "dynamic"
        name: string (itemId oder table reference)
        weight: number
        quality: number (optional)
        functions: [...]
      }
    ]
    conditions: [
      {
        condition: string
        parameters: {...}
      }
    ]
  }
]
functions: [
  {
    function: "set_count" | "set_nbt" | "enchant_randomly" | ...
    parameters: {...}
  }
]
```

### Prefab-Konfiguration
**Struktur (YAML oder JSON):**
```
Id: string
Size: [x, y, z]
Origin: [x, y, z]
Palette:
  "a": "oak_logs"
  "b": "oak_leaves"
  "c": "dirt"
  ".": "air"
Structure:
  - Layer 0: "aaa\naaa\naaa"
  - Layer 1: "bbb\nbbb\nbbb"
  - Layer 2: "ccc\nccc\nccc"
PlacementRules:
  RequiresSolidGround: boolean
  MinGroundLevel: number
  MaxGroundLevel: number
  AllowedBiomes: string[]
  MaxRotations: number (0, 90, 180, 270)
  RandomVariant: boolean
EntitySpawns:
  - Position: [x, y, z]
    EntityId: string
    SpawnCondition: string
```

### Server-Konfiguration
**Global Config (config.json):**
```
ServerName: string
MOTD: string
Password: string (optional)
MaxPlayers: number
MaxViewDistance: number
LocalCompressionEnabled: boolean
Port: 25565
Defaults:
  World: string
  GameMode: "Survival" | "Creative"
```

**World Config (universe/worlds/<name>/config.json):**
```
Seed: number (long)
WorldGen:
  Type: string
  Template: string
IsPvpEnabled: boolean
IsFallDamageEnabled: boolean
IsGameTimePaused: boolean
IsSpawningNPC: boolean
Gamemode: "Survival" | "Creative"
ClientEffects:
  BloomEnabled: boolean
  FogEnabled: boolean
  FogDistance: number
  RenderDistance: number
  AmbientLight: number (0.0-1.0)
GameRules:
  MobSpawning: boolean
  KeepInventory: boolean
  DaylightCycle: boolean
```

**Plugin Config Pattern:**
```
LuckIncreaseChance: number (0.0-1.0)
MaxPlayersAllowed: number
ServerWelcomeMessage: string
EnableCustomFeature: boolean
BannedChatWords: string[]
DropRateMultiplier: float (>= 1.0)
DebugModeActive: boolean
```

## 3. Erweiterte Konzepte

### Asset-Vererbung
```json
{
  "Id": "iron_sword_enhanced",
  "ExtendsFrom": "iron_sword",
  "Overrides": {
    "Quality": "Rare",
    "ItemLevel": 10,
    "WeaponConfiguration": {
      "Damage": 10.0,
      "AttackSpeed": 1.2
    }
  }
}
```

### Codec-System (Java ↔ JSON)
```java
public class MEBlockState extends BlockState {
    public boolean active;
    public int channels;
    
    public static final BuilderCodec<MEBlockState> CODEC = 
        BuilderCodec.builder(MEBlockState.class, MEBlockState::new)
            .withField("active", Codec.BOOLEAN, s -> s.active)
            .withField("channels", Codec.INT, s -> s.channels)
            .withField("meta", Codec.STRING, s -> s.metadata)
            .build();
}

// Verwendung:
MEBlockState state = new MEBlockState();
JsonElement json = CODEC.encode(state);
MEBlockState decoded = CODEC.decode(json);
```

### Dynamische Asset-Manipulation zur Laufzeit
```java
// Item-Registry zur Laufzeit modifizieren
ItemRegistry.ItemBuilder builder = ItemRegistry.get("stone_pickaxe")
    .modify()
    .setQuality("Rare")
    .setItemLevel(15)
    .addToDamageMultiplier(1.5);

ItemRegistry.register(builder.build());
```

### Komposition über Referenzen
```json
{
  "Id": "full_armor_set",
  "ComposedOf": [
    { "slot": "Head", "itemId": "diamond_helmet" },
    { "slot": "Chest", "itemId": "diamond_chestplate" },
    { "slot": "Legs", "itemId": "diamond_leggings" },
    { "slot": "Feet", "itemId": "diamond_boots" }
  ],
  "SetBonus": {
    "Damage": 2.0,
    "DefenseMultiplier": 1.3
  }
}
```

### Version-Kompatibilität
```json
{
  "Id": "item_with_schema_evolution",
  "SchemaVersion": "2026.01",
  "MigrationPath": {
    "2025.04": {
      "OldField": "NewFieldMapping",
      "LegacyQuality": "Quality"
    }
  },
  "DeprecatedFields": [
    { "name": "OldRenderingMode", "removedIn": "2026.02" }
  ]
}
```

## 4. Performance & Optimierung

### Server-Konfiguration nach Spielerzahl
| Players | RAM | CPU | NVMe | View Distance |
|---------|-----|-----|------|---------------|
| 1-20    | 4-6 GB | Single-core i5 | Optional | 16-24 chunks |
| 20-50   | 8-12 GB | Dual-core i7 | Empfohlen | 12-20 chunks |
| 50+     | 16+ GB | Quad+ i7/Ryzen | Erforderlich | 8-16 chunks |

### Kritische Performance-Faktoren
- **View Distance**: Verdopplung = 4x RAM/Netzwerk-Belastung
- **CPU**: Single-core Performance priorisiert (nicht Multi-core)
- **Speicher**: NVMe für Chunk-Loading (SSD mindestens)
- **Netzwerk**: QUIC mit Datagram-Unterstützung für Priorisierung
- **Memory Caching**: AOT Compilation für JIT-Optimierung
- **Backups**: Inkrementelle Sentry-Integration

## 5. Netzwerk-Architektur

### QUIC über UDP
- **Port**: 25565 (standard)
- **Datagram-Größe**: Max. 1200 Bytes
- **Kompression**: LocalCompressionEnabled Flag
- **Firewall-Konfiguration**: 
  ```bash
  netsh advfirewall firewall add rule name="Hytale Server" dir=in action=allow protocol=UDP localport=25565
  ```

### Multi-Server Architektur
- **Player Referrals**: Server A → Server B für Load-Balancing
- **Connection Redirects**: Nahtlos mit kryptographischer Signierung
- **Fallback Chains**: Sekundäre/Tertiäre Server falls Primär ausfällt
- **Session Persistence**: Cross-Server Inventory/Stats

## 11. IDE & Tool-Support

### JetBrains Marketplace Plugins
- **Hytale Development Tools**: Syntax-Highlighting, Refactoring-Support
- **Hytale .ui Support**: UI-File Editing mit Previews
- **Kompatibilität**: IntelliJ IDEA, PyCharm, WebStorm, DataGrip

### Blockbench Best Practices
- **Modell-Format**: Cubes & Quads nur
- **Textur-Größe**: 32px Multiples (64px für Characters, 32px für Props)
- **Farb-Grenzen**: Vermeiden Sie reines Weiß (#FFFFFF) oder Schwarz (#000000)
- **Export**: Hytale-spezifisches Format mit Animations-Support
- **Partner-Status**: Blockbench ist offizieller Hytale 3D Modeling Partner

## 12. JSON Validierung & Tools

### Online-Validator
- **HytaleTools.org**: Comprehensive JSON Schema Validator
- **HytaleDocs JSON Validator**: Community-betriebener Validator
- **hytale-item-schema**: GitHub-gehostetes Item-Schema mit Beispielen

### Community-Editoren
- **Asset Editor Guide**: Step-by-step JSON Creation
- **Modding Wiki**: Umfassende JSON-Richtlinien
- **CurseForge**: Distribution & Versionierung

## 13. Versionierungs-Strategie

### Semantic Versioning für Plugins
```
MAJOR.MINOR.PATCH[-PRERELEASE]
v1.2.3-alpha.1
```

### Changelog Standards
- Breaking Changes am Anfang dokumentieren
- Feature Additions mit Beispiel-Verwendung
- Bug Fixes mit Referenzen
- Performance Improvements mit Metriken

### Deployment Checklist
- [ ] JSON Schema gegen offizielle Validator prüfen
- [ ] Plugin-Manifest auf neuste API-Level aktualisiert
- [ ] Assets für Target-Server-Version kompatibel
- [ ] Performance-Tests auf Zielserver durchgeführt
- [ ] Backup vor Deployment erstellt
- [ ] Changelog mit Version aktualisiert
- [ ] GitHub Release mit Artifact erstellt

## 9. Erweiterte JSON-Konfiguration - Deep Dive

Hytale nutzt JSON als primäres Dateiformat für die Konfiguration einer Vielzahl von Spielinhalten und Servereinstellungen. Dies ermöglicht einen datengesteuerten Ansatz – ein Großteil der Spielmechaniken ist nicht hartcodiert, sondern über leicht zugängliche und modifizierbare JSON-Strukturen definierbar.

### 9.1 Haupt-Server-Konfiguration (config.json) - Erweiterte Parameter

**Kritische Performance-Parameter:**

```json
{
  "ServerName": "My Hytale Server",
  "MOTD": "Welcome!",
  "Password": "",                    // Leerer String = öffentlich
  "MaxPlayers": 100,                 // 1-20: 4-6GB RAM, 20-50: 8-12GB, 50+: 16+GB
  "MaxViewRadius": 12,               // Empfohlen: 12-16 Chunks
  "LocalCompressionEnabled": true,   // Reduziert Bandbreite um ~40%
  "Port": 25565,
  "Version": 1,
  "Defaults": {
    "World": "default",
    "GameMode": "ADVENTURE"
  },
  "ConnectionTimeouts": {
    "DefaultTimeout": 30000           // Millisekunden bis Rauswurf
  },
  "RateLimit": {
    "PacketsPerSecond": 1000          // DDoS-Schutz
  },
  "PlayerStorage": {
    "Type": "Hytale"
  },
  "LogLevels": {
    "Default": "INFO",
    "Network": "WARN"
  }
}
```

**Performance-Auswirkungen:**
- MaxPlayers verdoppeln = +CPU, +RAM, +Bandbreite
- MaxViewRadius verdoppeln = 4x Speicher, 4x CPU, 4x Netzwerk
- LocalCompressionEnabled=true = ~40% Bandbreiteneinsparung

**⚠️ Kritisch**: Änderungen NUR bei gestopptem Server! Backup vor Änderungen!

### 9.2 Weltspezifische Konfiguration - Erweiterte GameRules

```json
{
  "UUID": "auto-generated",
  "DisplayName": "Default World",
  "Seed": 1768666912427,
  "WorldGen": {
    "Type": "Hytale",                 // oder "Flat", "Empty"
    "Name": "Default",
    "Path": "WorldGenTemplates/Orbis_Default.json"
  },
  "ChunkStorage": {
    "Type": "Hytale"
  },
  "ChunkConfig": {
    "PregenerateRegion": { "Min": [0, 0], "Max": [100, 100] },
    "KeepLoadedRegion": { "Min": [-50, -50], "Max": [50, 50] }
  },
  "GameRules": {
    "IsPvpEnabled": false,
    "IsFallDamageEnabled": true,
    "IsGameTimePaused": false,
    "GameTime": "0001-01-01T05:30:00Z",  // ISO 8601
    "GameplayConfig": "Default",
    "DaytimeDurationSeconds": 600,
    "NighttimeDurationSeconds": 600,
    "DeathDropItemsOnGround": true,
    "KeepInventoryOnDeath": false
  },
  "ClientEffects": {
    "SunIntensity": 1.0,               // 0.0-1.0
    "BloomIntensity": 0.5,             // Licht-Halo
    "SunAngleDegrees": 45.0,           // Schattenwinkel
    "FogEnabled": true,
    "FogDistance": 128.0,
    "RenderDistance": 16,
    "AmbientLight": 0.8                // 0.0-1.0
  },
  "NPCs": {
    "IsSpawningNPC": true,
    "IsAllNPCFrozen": false,
    "IsSpawnMarkersEnabled": true,
    "IsObjectiveMarkersEnabled": true
  },
  "System & Saving": {
    "IsTicking": true,
    "IsBlockTicking": true,
    "IsSavingPlayers": true,
    "IsSavingChunks": true,
    "IsUnloadingChunks": true,
    "SaveNewChunks": true,
    "DeleteOnUniverseStart": false,
    "ResourceStorage": { "Type": "Hytale" }
  },
  "RequiredPlugins": {
    "myplugin.id": ">=1.0.0 <2.0.0"
  }
}
```

**Szenarien-Konfigurationen:**

**Kreativ-Welt (Safe Building):**
```json
{
  "GameRules": {
    "IsPvpEnabled": false,
    "IsFallDamageEnabled": false,
    "IsGameTimePaused": true,
    "GameTime": "0001-01-01T12:00:00Z"  // Mittags
  },
  "ClientEffects": {
    "ForcedWeather": "Clear"
  }
}
```

**Survival-Arena (Fast-Paced PvP):**
```json
{
  "GameRules": {
    "IsPvpEnabled": true,
    "IsGameTimePaused": false,
    "DaytimeDurationSeconds": 120,
    "NighttimeDurationSeconds": 60,
    "IsSpawningNPC": true
  }
}
```

**Performance-Optimiert (Viele Spieler):**
```json
{
  "ChunkConfig": {
    "KeepLoadedRegion": { "Min": [-25, -25], "Max": [25, 25] }
  },
  "GameRules": {
    "DaytimeDurationSeconds": 1200,   // Längere Zyklen = weniger Ticks
    "IsUnloadingChunks": true
  }
}
```

### 9.3 Item-Konfiguration - Vollständige Spezifikation

**Tool-Item mit vollständiger Spec:**
```json
{
  "Id": "stone_pickaxe",
  "TranslationProperties": {
    "Name": "items.stone_pickaxe.name",
    "Description": "items.stone_pickaxe.description"
  },
  "Icon": "Icons/ItemsGenerated/stone_pickaxe.png",
  "ItemLevel": 5,
  "MaxStack": 1,
  "QualityId": "Common",
  "Set": "Tool_Stone",
  "ToolConfiguration": {
    "Specs": [
      {
        "GatherType": "Rocks",
        "Power": 3,
        "Quality": 2,
        "IsIncorrect": false,
        "HitSoundLayer": "pick_hit_rock"
      },
      {
        "GatherType": "Ore",
        "Power": 2,
        "Quality": 1,
        "IsIncorrect": false
      }
    ],
    "Speed": 1.2,
    "MaxDurability": 250,
    "DurabilityLossBlockTypes": [
      { "BlockType": "Rocks", "DurabilityLossOnHit": 1 },
      { "BlockType": "Ore", "DurabilityLossOnHit": 2 }
    ]
  },
  "DroppedItemConfiguration": {
    "PhysicsValues": {
      "ItemMass": 2.0,
      "FrictionCoefficient": 0.6,
      "ApplyGravity": true
    },
    "PickupRadius": 1.75,
    "ttl": 300.0,
    "ParticleSystemId": "tool_sparkles",
    "ParticleColor": "#8B7355"
  }
}
```

**Weapon-Item mit Stats:**
```json
{
  "Id": "iron_sword",
  "WeaponConfiguration": {
    "StatModifiers": {
      "Damage": {
        "Amount": 7.0,
        "CalculationType": "Additive"
      },
      "AttackSpeed": {
        "Amount": 1.2,
        "CalculationType": "Multiplicative"
      }
    },
    "RenderDualWielded": false,
    "Knockback": 1.5
  }
}
```

**Armor-Item mit Widerständen:**
```json
{
  "Id": "diamond_chestplate",
  "ArmorConfiguration": {
    "EquipmentSlot": "Chest",
    "BaseDamageResistance": 0.3,         // 30% Basis
    "DamageResistance": {
      "FireResistance": 0.6,
      "ColdResistance": 0.4,
      "PoisonResistance": 0.2
    },
    "StatModifiers": {
      "Health": {
        "Amount": 20,
        "CalculationType": "Additive"
      }
    }
  }
}
```

### 9.4 Block-Konfiguration - Texture Mapping & Rendering

```json
{
  "Id": "oak_logs",
  "BlockType": {
    "Material": "Solid",
    "DrawType": "Cube",
    "Group": "Wood",
    "Flags": {
      "CanRotate": true,
      "CanVariate": true
    },
    "Gathering": {
      "Breaking": {
        "GatherType": "Wood",
        "ItemId": "oak_logs",
        "HarvestLevel": 0
      }
    },
    "BlockParticleSetId": "Wood",
    "Textures": [
      {
        "TopFace": "BlockTextures/oak_logs_top.png",
        "BottomFace": "BlockTextures/oak_logs_bottom.png",
        "NorthFace": "BlockTextures/oak_logs_side.png",
        "SouthFace": "BlockTextures/oak_logs_side.png",
        "EastFace": "BlockTextures/oak_logs_side.png",
        "WestFace": "BlockTextures/oak_logs_side.png",
        "VariantWeight": 1.0
      }
    ],
    "ParticleColor": "#8B4513",
    "BlockSoundSetId": "Wood",
    "BlockBreakingDecalId": "Breaking_Decals_Wood"
  },
  "ResourceTypes": ["Wood", "OakWood"]
}
```

### 9.5 Loot-Tabellen - Fortgeschrittene Pools

```json
{
  "pools": [
    {
      "rolls": 2,
      "bonusRolls": {
        "min": 0,
        "max": 1
      },
      "entries": [
        {
          "type": "item",
          "name": "stone_cobble",
          "weight": 10,
          "functions": [
            {
              "function": "set_count",
              "count": { "min": 1, "max": 2 }
            },
            {
              "function": "set_damage",
              "damage": { "min": 0.0, "max": 0.5 }
            }
          ]
        },
        {
          "type": "loot_table",
          "name": "loot/bonus_drops",
          "weight": 2,
          "conditions": [
            {
              "condition": "killed_by_player"
            }
          ]
        }
      ]
    }
  ]
}
```

### 9.6 Spawn-Regeln - Zeit & Biom-Filter

```json
{
  "minecraft:spawn_rules": {
    "description": {
      "identifier": "ghost",
      "population_control": "monster"
    },
    "conditions": [
      { "minecraft:brightness_filter": { "min": 0, "max": 7 } },
      { "minecraft:difficulty_filter": { "min": "easy" } },
      { "minecraft:weight": { "default": 80 } },
      { "minecraft:herd": { "min_size": 1, "max_size": 3 } },
      {
        "minecraft:biome_filter": {
          "test": "has_biome_tag",
          "value": "haunted"
        }
      },
      {
        "minecraft:time_filter": {
          "min": 18000,                 // 18000 Ticks = Sonnenuntergang
          "max": 6000                   // 6000 Ticks = Sonnenaufgang
        }
      }
    ]
  }
}
```

### 9.7 Plugin-Konfiguration - Codec System

```json
{
  "LuckIncreaseChance": 0.40,
  "MaxPlayersAllowed": 100,
  "ServerWelcomeMessage": "Welcome!",
  "EnableCustomFeature": true,
  "BannedChatWords": ["badword1", "badword2"],
  "DropRateMultiplier": 1.5,
  "DebugModeActive": false,
  "CustomRecipes": {
    "IronGolemSpawner": {
      "Enabled": true,
      "CooldownSeconds": 30,
      "RequiresPermission": "plugin.spawn.golem"
    },
    "MagicWand": {
      "Enabled": false,
      "ManaCost": 50,
      "CooldownSeconds": 5
    }
  }
}
```

### 9.8 Best Practices für JSON in Hytale

**Syntax & Validierung:**
- Nutze Online-JSON-Validator (JSONLint, HytaleTools.org)
- Achte auf Kommas, Klammern, Datentypen
- format_version Feld nutzen für Versionierbarkeit

**Dynamische Updates:**
- JSON-Schemata ändern sich bei Updates
- Immer Kompatibilität prüfen
- Migration Paths in Version-Kompatibilität dokumentieren

**Manuelle Bearbeitung:**
- ✅ Einfache Konfigurationsdateien: Manuell ok
- ❌ Komplexe Data Assets: Asset Editor bevorzugt
- ⚠️ Bei laufendem Server ändern: Kann überschrieben werden!

**Dokumentation:**
- Jedes Plugin sollte Konfigurationsdokumentation bereitstellen
- JSON-Schemata für IDE-Integration verwenden
- Kommentare nutzen (z.B. $comment Felder)

**Fehlerbehandlung:**
- Server-Logs im logs/ Ordner prüfen
- Fehler zeigen genaue Zeile & Feld
- Inkrementelles Testing: Eine Änderung pro Test

**Sicherheit:**
- JSON-Dateien selbst sind nicht malware-anfällig
- Aber: Von vertrauenswürdigen Quellen laden
- Immer auf isoliertem Server testen
- Backups vor Änderungen!

---

## 10. Debugging & Testing

## 10. Debugging & Testing

### MEDebugCommand Integration
```
/me debug command [args]
```

### Logging-Level
- DEBUG: Detaillierte Tracing-Ausgaben
- INFO: Wichtige Lifecycle-Events
- WARN: Potenzielle Probleme
- ERROR: Behandelbare Fehler
- FATAL: Nicht-behandelbare Fehler

### Common Issues & Lösungen
| Problem | Ursache | Lösung |
|---------|--------|--------|
| Plugin lädt nicht | Manifest-Format ungültig | Gegen britakee GitBook validieren |
| Items erscheinen nicht | JSON Schema falsch | HytaleTools.org Validator nutzen |
| Performance-Einbußen | View Distance zu hoch | Empfehlung vs. Spielerzahl prüfen |
| Netzwerk-Timeouts | TCP statt QUIC | Port 25565 UDP konfigurieren |
| Assets nicht synced | Codec-Serialisierung fehlgeschlagen | BuilderCodec Feld-Mapping prüfen |
| Config wird ignoriert | Server läuft noch während Edit | Server stoppen vor Änderungen |
| Loot-Tabellen funktionieren nicht | Pool-Syntax fehlerhaft | rolls/entries/conditions validieren |
| NPCs spawnen nicht | Biome-Tags falsch | Biom-Filter gegen aktuelle Welt prüfen |

---

**Letzte Aktualisierung**: v2026.01 Schema (Hytale Server v2026.01.17-4b0f30090)  
**Status**: Offizielle Quellen validiert, Community-Ressourcen integriert, Erweiterte JSON-Konfiguration dokumentiert  
**Beiträge**: britakee-studios, Slikey (Technical Director), Blockbench Team, Hytale Community
