# Technische Quellen & Detaillierte Referenzen

> Umfassende technische Quellen für Hytale Plugin-Entwicklung, basierend auf offiziellen Dokumentationen und erweiterten Referenzen.

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

## 2. Daten-Struktur Referenzen

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

## 6. IDE & Tool-Support

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

## 7. JSON Validierung & Tools

### Online-Validator
- **HytaleTools.org**: Comprehensive JSON Schema Validator
- **HytaleDocs JSON Validator**: Community-betriebener Validator
- **hytale-item-schema**: GitHub-gehostetes Item-Schema mit Beispielen

### Community-Editoren
- **Asset Editor Guide**: Step-by-step JSON Creation
- **Modding Wiki**: Umfassende JSON-Richtlinien
- **CurseForge**: Distribution & Versionierung

## 8. Versionierungs-Strategie

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

## 9. Debugging & Testing

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

---

**Letzte Aktualisierung**: v2026.01 Schema (Hytale Server v2026.01.17-4b0f30090)  
**Status**: Offizielle Quellen validiert, Community-Ressourcen integriert  
**Beiträge**: britakee-studios, Slikey (Technical Director), Blockbench Team, Hytale Community
