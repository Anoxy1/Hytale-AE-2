# Hytale JSON Data Assets - Schemas & Templates

**Source:** Hytale Early Access v2026.01.17-4b0f30090  
**Last Updated:** Januar 2026  
**Purpose:** Complete JSON schema reference for Items, Blocks, NPCs, Loot Tables, Prefabs, World Generation  
**CRITICAL**: All paths relative to `src/main/resources/`

## AI-Übersicht (was, wann, wohin)
- Wann lesen? Wenn du Items/Blocks/NPC/Loot/Prefabs/World-Configs anlegst oder prüfst.
- Was drin? Vollständige Schemas, Templates und Validierungs-Hinweise.
- Direkt springen: [Items](#item-templates) · [Blocks](#block-templates) · [NPC](#npc-templates) · [Loot](#loot-tables) · [World/Server](#server--world-configuration) · [Best Practices](#validation--best-practices)
- AI-Hub: [AGENT_ONBOARDING.md#ai-agent-start](AGENT_ONBOARDING.md#ai-agent-start)


## 📚 Table of Contents

### Validation-Checkliste (schnell)
- Schema-Vollständigkeit: `Id`, `TranslationProperties`, `Icon`, categories/slots passend zum Item-Typ.
- Pfade prüfen: Icons/Models/Textures referenzieren existierende Assets unter `src/main/resources/Common/...` oder `Server/...`.
- IDs konsistent: `State.Definitions.Id` passt zu Java-Registrierung (wenn BlockState), Namespaces sauber (`mesystem:*`).
- In-Game-Test: Asset im Spiel laden, Log auf Fehler prüfen.
- ASCII/Encoding: Keine Sonderzeichen in Strings/Beschreibungen.

1. [JSON Schema Fundamentals](#json-schema-fundamentals)
2. [Item Templates](#item-templates)
3. [Block Templates](#block-templates)
4. [NPC Templates](#npc-templates)
5. [Loot Tables](#loot-tables)
6. [Prefabs & World Generation](#prefabs--world-generation)
7. [Server & World Configuration](#server--world-configuration)
8. [Validation & Best Practices](#validation--best-practices)

---

## JSON Schema Fundamentals

### Validation Tools

**Official HytaleDocs Validator:**
- Validates plugin manifest.json syntax
- Checks JSON syntax for Blocks, Items, NPCs (structure varies by version)
- ⚠️ Limited schema validation - **ALWAYS TEST IN-GAME**

**Community Tools:**
- HytaleTools.org: Full JSON parsing, formatting, minification
- VS Code: Install "JSON Schema Store" for autocomplete

### Schema Best Practices

```json
{
  "$schema": "https://hytale.example.com/schemas/item-v1.0.json",
  "description": "Complete item template with all optional fields",
  "type": "object",
  "required": ["Id", "TranslationProperties", "Icon"],
  "properties": {
    "Id": {
      "type": "string",
      "pattern": "^[a-zA-Z0-9_]+$",
      "description": "Globally unique alphanumeric identifier with underscores"
    }
  }
}
```

---

## Item Templates

### Basic Item Structure

**File:** `Server/Item/Items/my_item.json`

```json
{
  "Id": "my_custom_item",
  "TranslationProperties": {
    "Name": "items.my_custom_item.name",
    "Description": "items.my_custom_item.description"
  },
  "Icon": "Icons/ItemsGenerated/my_custom_item.png",
  "Categories": ["Crafting", "Materials"],
  "ItemLevel": 1,
  "MaxStackSize": 64,
  "Quality": "Common"
}
```

### Complete Item with Rendering

```json
{
  "Id": "iron_sword",
  "TranslationProperties": {
    "Name": "items.iron_sword.name",
    "Description": "items.iron_sword.description"
  },
  "Icon": "Icons/ItemsGenerated/sword_iron.png",
  "Categories": ["Combat", "Weapons"],
  "ItemLevel": 5,
  "MaxStackSize": 1,
  "Quality": "Uncommon",
  
  "Model": "Resources/sword_iron/model.blockymodel",
  "Texture": "Resources/sword_iron/model_texture.png",
  "Scale": 1.0,
  "Animation": "sword_idle",
  "DroppedItemAnimation": "sword_dropped",
  
  "Light": {
    "Color": [1.0, 1.0, 1.0],
    "Intensity": 0.5
  }
}
```

### Item with Tool Configuration

```json
{
  "Id": "stone_pickaxe",
  "TranslationProperties": {
    "Name": "items.stone_pickaxe.name",
    "Description": "items.stone_pickaxe.description"
  },
  "Icon": "Icons/ItemsGenerated/pickaxe_stone.png",
  "MaxStackSize": 1,
  "Quality": "Common",
  "Set": "Stone_Tools",
  
  "Model": "Resources/stone_pickaxe/model.blockymodel",
  "Texture": "Resources/stone_pickaxe/texture.png",
  "UsePlayerAnimations": false,
  
  "ToolConfiguration": {
    "Specs": [
      {
        "GatherType": "Rocks",
        "Power": 3,
        "Quality": 2
      },
      {
        "GatherType": "Ore",
        "Power": 2,
        "Quality": 1
      }
    ],
    "Speed": 1.2,
    "DurabilityLossBlockTypes": [
      "hytale:stone",
      "hytale:cobblestone",
      "hytale:ore_*"
    ],
    "MaxDurability": 200
  },
  
  "DroppedItemConfig": {
    "PhysicsValues": {
      "ItemMass": 2.0,
      "FrictionCoefficient": 0.6,
      "ApplyGravity": true
    },
    "PickupRadius": 1.75,
    "ttl": 300.0,
    "ParticleSystemId": "tool_sparkles",
    "ParticleColor": "#8B7355",
    "ShowItemParticles": true
  }
}
```

### Item with Weapon Configuration

```json
{
  "Id": "iron_sword",
  "MaxStackSize": 1,
  "Quality": "Uncommon",
  
  "WeaponConfiguration": {
    "AttackSpeed": 1.6,
    "Damage": 7.0,
    "Knockback": 0.5,
    "RenderDualWielded": false,
    "StatModifiers": {
      "Strength": 2.0,
      "AttackSpeed": 0.1
    }
  }
}
```

### Item with Armor Configuration

```json
{
  "Id": "iron_chestplate",
  "MaxStackSize": 1,
  "Quality": "Uncommon",
  
  "ArmorConfiguration": {
    "EquipmentSlot": "Chest",
    "BaseDamageResistance": 0.15,
    "FireResistance": 0.05,
    "ColdResistance": 0.05,
    "StatModifiers": {
      "Health": 5.0,
      "Defense": 0.2
    }
  }
}
```

### Item with Interactions

```json
{
  "Id": "healing_potion",
  "MaxStackSize": 16,
  "Quality": "Common",
  
  "Interactions": {
    "Type": "Serial",
    "Steps": [
      {
        "Type": "Charging",
        "Duration": 0.5,
        "Next": "drink_effect"
      },
      {
        "Type": "Simple",
        "Id": "drink_effect",
        "CustomInteractionId": "healing_potion_drink"
      }
    ]
  }
}
```

---

## Block Templates

### Basic Block Structure

**File:** `Server/Item/Items/my_block.json`

```json
{
  "Id": "my_custom_block",
  "BlockGroup": "Decoration",
  "BlockMaterial": "Solid",
  "RenderingType": "Cube",
  "TransparencyLevel": "Solid",
  
  "BlockTextures": [
    {
      "AllFacesTexture": "BlockTextures/my_block.png",
      "VariantWeight": 1.0
    }
  ]
}
```

### Block with Per-Face Textures and Advanced Properties

```json
{
  "Id": "oak_logs",
  "TranslationProperties": {
    "Name": "server.oak_logs.name"
  },
  "MaxStack": 64,
  "Icon": "Icons/ItemsGenerated/oak_logs.png",
  "Categories": ["Blocks", "Wood"],
  "Set": "Wood_Oak",
  
  "BlockType": {
    "Material": "Solid",
    "DrawType": "Cube",
    "Group": "Wood",
    "Flags": {
      "CanRot": true,
      "CanVariate": true
    },
    
    "Gathering": {
      "Breaking": {
        "GatherType": "Axe",
        "ItemId": "oak_logs"
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
  }
}
```

### Block with Custom Model

```json
{
  "Id": "custom_tree_log",
  "BlockGroup": "Wood",
  "BlockMaterial": "Solid",
  "RenderingType": "Model",
  "TransparencyLevel": "Solid",
  
  "CustomModel": "Resources/custom_tree/log_model.blockymodel",
  "CustomModelScale": 1.0,
  "CustomModelAnimation": "tree_wind_animation"
}
```

### Transparent/Translucent Block

```json
{
  "Id": "glass_pane",
  "BlockGroup": "Glass",
  "BlockMaterial": "Solid",
  "RenderingType": "Cube",
  "TransparencyLevel": "Transparent",
  
  "BlockTextures": [
    {
      "AllFacesTexture": "BlockTextures/glass_pane.png"
    }
  ]
}
```

### Non-Solid Block (e.g., Water, Air)

```json
{
  "Id": "water",
  "BlockGroup": "@Tech",
  "BlockMaterial": "Empty",
  "RenderingType": "Empty",
  "TransparencyLevel": "Transparent",
  
  "CustomModel": "Resources/water/water_model.blockymodel",
  "CustomModelAnimation": "water_flow_animation"
}
```

---

## NPC Templates

### Basic NPC Structure

**File:** `Server/NPC/npcs/my_npc.json`

```json
{
  "Id": "blacksmith_npc",
  "TranslationProperties": {
    "Name": "npcs.blacksmith_npc.name"
  },
  "Model": "Resources/blacksmith/model.blockymodel",
  "Texture": "Resources/blacksmith/model_texture.png",
  
  "Behaviors": [
    {
      "Type": "Idle",
      "Animation": "blacksmith_idle",
      "Duration": 5.0
    },
    {
      "Type": "Wander",
      "Speed": 0.5,
      "MaxDistance": 10.0
    }
  ],
  
  "DialogueReferences": [
    "npcs/blacksmith_dialogue.json"
  ],
  
  "SpawnConditions": {
    "BiomeTags": ["Village"],
    "TimeOfDay": "Day",
    "MinPlayersNearby": 1,
    "MaxPlayersNearby": 10
  },
  
  "AIParameters": {
    "AggressionLevel": 0.0,
    "PerceptionRadius": 20.0,
    "ReactionTime": 0.5
  }
}
```

### NPC with Custom Interactions

```json
{
  "Id": "merchant_npc",
  "Behaviors": [
    {
      "Type": "TradeWithPlayer",
      "TradeTable": "npcs/merchant_trades.json"
    }
  ],
  "DialogueReferences": [
    "npcs/merchant_dialogue_greeting.json",
    "npcs/merchant_dialogue_trade.json",
    "npcs/merchant_dialogue_goodbye.json"
  ],
  "Interactions": {
    "Type": "Trade",
    "CustomInteractionId": "merchant_trade"
  }
}
```

---

## Loot Tables

### Basic Loot Table

**File:** `Server/Item/LootTables/my_loot.json`

```json
{
  "pools": [
    {
      "rolls": 2,
      "bonusRolls": 1,
      "entries": [
        {
          "itemId": "hytale:stone",
          "weight": 10,
          "minCount": 1,
          "maxCount": 3
        },
        {
          "itemId": "hytale:coal_ore",
          "weight": 3,
          "minCount": 1,
          "maxCount": 1
        },
        {
          "itemId": "hytale:rare_gem",
          "weight": 1,
          "minCount": 1,
          "maxCount": 1
        }
      ]
    }
  ]
}
```

### Loot Table with Conditions

```json
{
  "pools": [
    {
      "rolls": 1,
      "conditions": [
        {
          "condition": "luck",
          "value": 0.1
        }
      ],
      "entries": [
        {
          "itemId": "hytale:legendary_sword",
          "weight": 1,
          "minCount": 1,
          "maxCount": 1
        }
      ]
    }
  ],
  "functions": [
    {
      "function": "enchant_randomly"
    }
  ]
}
```

### Loot Table with LootGroup (Mod Pattern)

```json
{
  "pools": [
    {
      "rolls": 1,
      "entries": [
        {
          "itemId": "hytale:stone",
          "weight": 5,
          "minCount": 1,
          "maxCount": 2
        },
        {
          "itemId": "hytale:coal",
          "weight": 3,
          "minCount": 1,
          "maxCount": 1
        }
      ]
    },
    {
      "rolls": 1,
      "entries": [
        {
          "itemId": "custom_mod:magic_dust",
          "weight": 2,
          "minCount": 1,
          "maxCount": 3
        }
      ],
      "enabled": true,
      "chance": 0.35
    }
  ]
}
```

---

## Prefabs & World Generation

### Basic Prefab (YAML Format)

**File:** `Server/Prefabs/simple_house.yaml`

```yaml
Id: simple_village_house
Name: Simple House
Description: A basic wooden house for villages

Size: [7, 5, 7]
Origin: [3, 0, 3]

Palette:
  W: "hytale:oak_planks"
  S: "hytale:stone_bricks"
  G: "hytale:glass_pane"
  D: "hytale:oak_door"
  .: "hytale:air"

Structure:
  # Layer 0 (Ground)
  - "SSSSSSS"
  - "SSSSSSS"
  - "SSSSSSS"
  - "SSSSSSS"
  - "SSSSSSS"
  
  # Layer 1 (Walls)
  - "WWWWWWW"
  - "W.....W"
  - "W.....W"
  - "W.....W"
  - "WWDWWWW"
  
  # Layer 2 (Roof)
  - "GGGGGGG"
  - "G.....G"
  - "G.....G"
  - "G.....G"
  - "GGGGGGG"

PlacementRules:
  RequiresSolidGround: true
  MinGroundLevel: 60
  MaxGroundLevel: 100
  AllowedBiomes:
    - "village_plains"
    - "village_forest"
  MaxRotations: 4
  RandomVariant: true

EntitySpawns:
  - Type: "hytale:blacksmith"
    Position: [3, 2, 3]
    Facing: "north"
  
  - Type: "hytale:merchant"
    Position: [2, 2, 2]
    Facing: "east"
```

### Prefab in JSON (Alternative)

```json
{
  "Id": "simple_well",
  "Size": [3, 5, 3],
  "Origin": [1, 0, 1],
  
  "Palette": {
    "S": "hytale:stone_bricks",
    "W": "hytale:water",
    ".": "hytale:air"
  },
  
  "Structure": [
    ["SSS", "SSS", "SSS"],
    ["S.S", "S.S", "S.S"],
    ["S.S", "S.S", "S.S"],
    ["S.S", "SWS", "S.S"],
    ["SSS", "SSS", "SSS"]
  ],
  
  "PlacementRules": {
    "RequiresSolidGround": true,
    "MinGroundLevel": 50,
    "MaxGroundLevel": 150,
    "AllowedBiomes": ["*"]
  }
}
```

---

## Server & World Configuration

### Global Server Configuration (Complete with Port & Defaults)

**File:** `config.json` (root)

```json
{
  "ServerName": "My Awesome Server",
  "MOTD": "Welcome to Hytale!",
  "Password": null,
  "MaxPlayers": 50,
  "MaxViewRadius": 32,
  "LocalCompressionEnabled": true,
  "Port": 25565,
  
  "Defaults": {
    "World": "default",
    "GameMode": "ADVENTURE"
  }
}
```

### World-Specific Configuration (Complete with WorldGen & ClientEffects)

**File:** `universe/worlds/my_world/config.json`

```json
{
  "WorldName": "Adventure World",
  "Seed": 12345678901234567,
  
  "WorldGen": {
    "Type": "Hytale",
    "Template": "WorldGenTemplates/Orbis_Default.json",
    "Options": {
      "TerrainScale": 1.0,
      "BiomeSize": 256
    }
  },
  
  "IsPvpEnabled": false,
  "IsFallDamageEnabled": true,
  "IsGameTimePaused": false,
  "IsSpawningNPC": true,
  "Gamemode": "ADVENTURE",
  
  "ClientEffects": {
    "BloomEnabled": true,
    "FogEnabled": true,
    "FogDistance": 256,
    "RenderDistance": 32,
    "AmbientLight": 1.0
  },
  
  "GameRules": {
    "MobSpawning": true,
    "KeepInventory": false,
    "DaylightCycle": true
  }
}
```

### Plugin-Specific Configuration

**File:** `plugins/my_plugin/config.json`

```json
{
  "PluginName": "My Custom Plugin",
  "Version": "1.0.0",
  "Enabled": true,
  
  "LuckIncreaseChance": 0.40,
  "MaxPlayers": 100,
  "ServerMessage": "Welcome to the custom server!",
  "EnableFeature": true,
  "BannedWords": [
    "inappropriate_word_1",
    "inappropriate_word_2"
  ],
  "DropRateMultiplier": 1.5,
  "DebugMode": false
}
```

---

## Advanced Modding Concepts

### Asset Inheritance & Composition

**Pattern 1: Extending Base Items**

```json
{
  "Id": "iron_sword_enhanced",
  "ExtendsFrom": "iron_sword",
  "Overrides": {
    "Quality": "Rare",
    "ItemLevel": 10,
    "WeaponConfiguration": {
      "Damage": 10.0,
      "AttackSpeed": 1.8
    }
  }
}
```

**Pattern 2: Modular Behavior (via Plugins)**

Custom Interactions registered in Java:

```java
// In Java Plugin
InteractionRegistry.register("healing_potion_drink", new HealingPotionInteraction());
```

Then referenced in JSON:

```json
{
  "Interactions": {
    "Type": "Simple",
    "CustomInteractionId": "healing_potion_drink"
  }
}
```

### Codec System (Java ↔ JSON Serialization)

**Hytale uses Codecs for type-safe JSON mapping:**

```java
public class MEBlockState extends BlockState {
    public static final BuilderCodec<MEBlockState> CODEC = 
        BuilderCodec.builder(MEBlockState.class, MEBlockState::new)
            .withField("active", Codec.BOOLEAN, s -> s.active)
            .withField("channels", Codec.INT, s -> s.channels)
            .build();
}
```

**Serializes to JSON:**

```json
{
  "active": true,
  "channels": 8
}
```

### Dynamic Asset Manipulation at Runtime

**Plugins can modify assets via Asset Registry:**

```java
// Access asset at runtime
ItemAsset sword = ItemRegistry.get("iron_sword");
sword.damage += 5.0;  // Modify via code

// Or register new assets dynamically
ItemAsset customSword = new ItemAsset("custom_sword");
ItemRegistry.register(customSword);
```

---

## Version Compatibility & Migration

### Schema Changes Between Versions

**Hytale Early Access (v2026.01.17):**
- ItemLevel (integer), Quality (enum: Common/Uncommon/Rare/Epic/Legendary)
- Interactions support Charging, Condition, Serial types
- BlockType format with Gathering, BlockSoundSetId, BlockBreakingDecalId

**Expected Changes in Future Updates:**
- Visual Scripting nodes for NPC behaviors
- New interaction types (trigger zones, conditional logic)
- Enhanced particle system support
- Extended AI parameters

### Migration Checklist

When updating Hytale version:

1. ✅ Re-export all Blockbench models (ensure format compatibility)
2. ✅ Validate all JSON files with latest schema
3. ✅ Test items, blocks, NPCs in-game
4. ✅ Check asset paths (may change in updates)
5. ✅ Review plugin Java code for API changes

---

## Validation & Best Practices

### Pre-Deployment Checklist

- ✅ All JSON syntax valid (use online JSON validator)
- ✅ All required fields present
- ✅ IDs follow naming convention: `snake_case_alphanumeric`
- ✅ File paths relative to `src/main/resources/`
- ✅ Asset files referenced actually exist
- ✅ No emoji or special characters in IDs
- ✅ Model/Texture paths point to correct Blockbench exports
- ✅ Loot table weights sum to reasonable probability
- ✅ NPC dialogue files exist and are valid

### Common Errors & Fixes

| Error | Cause | Solution |
|-------|-------|----------|
| `Invalid JSON syntax` | Trailing commas, unquoted keys | Use JSON linter (VS Code) |
| `File not found` | Incorrect asset path | Check relative path from `resources/` |
| `Unknown ItemId` | Item ID not in registry | Verify ID in Item/Items/*.json |
| `Block renders as missing texture` | Wrong texture path or not in PNG format | Check path and convert to PNG |
| `NPC not spawning` | BiomeTags or SpawnConditions mismatch | Verify biome name and conditions |
| `Loot table empty` | All pools disabled or conditions too strict | Check `enabled: true` and conditions |

### Example: Complete Item with All Features

```json
{
  "Id": "enchanted_sword",
  "TranslationProperties": {
    "Name": "items.enchanted_sword.name",
    "Description": "items.enchanted_sword.description"
  },
  "Icon": "Icons/ItemsGenerated/sword_enchanted.png",
  "Categories": ["Combat", "Weapons", "Legendary"],
  "ItemLevel": 20,
  "MaxStackSize": 1,
  "Quality": "Legendary",
  
  "Model": "Resources/enchanted_sword/model.blockymodel",
  "Texture": "Resources/enchanted_sword/model_texture.png",
  "Scale": 1.2,
  "Animation": "sword_enchanted_idle",
  
  "Light": {
    "Color": [0.2, 0.8, 1.0],
    "Intensity": 1.0
  },
  
  "WeaponConfiguration": {
    "AttackSpeed": 1.8,
    "Damage": 15.0,
    "Knockback": 1.0,
    "RenderDualWielded": false,
    "StatModifiers": {
      "Strength": 5.0,
      "AttackSpeed": 0.3
    },
    "SpecialEffects": [
      {
        "Type": "OnHit",
        "Effect": "frost_explosion",
        "Chance": 0.25
      }
    ]
  },
  
  "DroppedItemConfiguration": {
    "Physics": {
      "Mass": 2.0,
      "Friction": 0.5,
      "Gravity": 9.81
    },
    "PickupRadius": 2.0,
    "LifeTime": 600,
    "ParticleEffect": "sword_sparkles"
  }
}
```

---

**Last Updated:** Januar 2026  
**Source:** Official Hytale Early Access Documentation + Community Research  
**Status:** Complete JSON Schema Reference ✅

