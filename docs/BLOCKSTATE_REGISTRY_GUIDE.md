# BlockStateRegistry Implementation Guide

## Overview
This document explains how HytaleAE2 uses Hytale's BlockStateRegistry API to register custom blocks.

## Key Concepts

### 1. Hybrid Registration System
Hytale uses a **JSON + Java hybrid system** for block registration:
- **JSON files** define visual properties, physics, and placement rules
- **Java classes** define runtime behavior and state management
- **BlockStateRegistry** connects the two

### 2. Required Components

#### A. JSON Block Definition
Location: `src/main/resources/Server/Item/Items/<BlockName>.json`

```json
{
  "Id": "HytaleAE2:ME_Controller",
  "State": {
    "Definitions": {
      "Id": "ME_Controller"
    }
  }
}
```

**CRITICAL**: `State.Definitions.Id` must match the registration ID in Java!

#### B. Java BlockState Class
Location: `src/main/java/com/tobi/mesystem/blocks/<BlockName>BlockState.java`

```java
public class MEControllerBlockState extends BlockState {
    public static final BuilderCodec<MEControllerBlockState> CODEC = 
        BuilderCodec.of(MEControllerBlockState::new)
            .withField("active", Codec.BOOL, state -> state.active)
            .build();
    
    private final boolean active;
    
    public MEControllerBlockState(boolean active) {
        this.active = active;
    }
}
```

#### C. Registration in MEPlugin
```java
private void registerBlockStates() {
    BlockStateRegistry registry = getBlockStateRegistry();
    
    registry.registerBlockState(
        MEControllerBlockState.class,
        "ME_Controller",  // Must match JSON State.Definitions.Id
        MEControllerBlockState.CODEC
    );
}
```

## Current Implementation Status

### ✅ Completed
- JSON block definitions for all 3 blocks (Controller, Terminal, Cable)
- Stubbed BlockState classes (require HytaleServer.jar for full implementation)
- Registration method in MEPlugin with commented-out code
- Asset pack structure with `IncludesAssetPack: true` in manifest.json

### ⏳ Pending (Requires HytaleServer.jar)
- Actual Codec implementations
- BlockState class implementations extending Hytale's BlockState
- Uncomment registration code in MEPlugin.registerBlockStates()

## Block Definitions

### ME Controller
- **ID**: `HytaleAE2:ME_Controller`
- **States**: `active` (boolean) - glowing when network is active
- **Purpose**: Network formation hub

### ME Terminal
- **ID**: `HytaleAE2:ME_Terminal`
- **States**: ItemContainerStateData for inventory
- **Interaction**: Opens GUI for network access
- **Purpose**: Player interface to network storage

### ME Cable
- **ID**: `HytaleAE2:ME_Cable`
- **States**: 6 connection types (straight, corner, T, cross, 4-way, 5-way)
- **Purpose**: Network data transmission

## Asset Pack Structure

```
src/main/resources/
├── manifest.json (IncludesAssetPack: true)
├── Server/
│   ├── Item/
│   │   └── Items/
│   │       ├── MEController.json
│   │       ├── METerminal.json
│   │       └── MECable.json
│   └── Languages/
│       └── en-US/
│           └── items.lang
└── Client/ (TODO)
    ├── Models/
    │   ├── MEController.blockymodel
    │   ├── METerminal.blockymodel
    │   └── MECable_*.blockymodel (6 variants)
    └── Textures/
        ├── MEController.png
        ├── METerminal.png
        └── MECable.png
```

## Next Steps

1. **Obtain HytaleServer.jar**
   - Add to `libs/` directory
   - Update `build.gradle` dependencies

2. **Implement Codecs**
   - Import `com.hypixel.hytale.server.core.codec.BuilderCodec`
   - Create proper serialization logic for each BlockState

3. **Extend BlockState**
   - Import `com.hypixel.hytale.server.core.block.BlockState`
   - Implement state change methods

4. **Uncomment Registration**
   - Enable code in MEPlugin.registerBlockStates()
   - Test block placement in Creative Mode

5. **Add Models & Textures**
   - Create .blockymodel files (Hytale's voxel format)
   - Add PNG textures for each block
   - Test visual appearance in-game

## References

### Analyzed Working Mods
- **ChestTerminal**: Example of inventory blocks using ItemContainerStateData
- **HyPipes**: Example of dynamic states (60+ pipe connection combinations)

### Hytale API
- BlockStateRegistry: Main registration API
- BlockState: Base class for all block behaviors
- BuilderCodec: Serialization framework for states
- ItemContainerStateData: Inventory support for blocks
- Interaction: Player interaction handling

## Common Pitfalls

1. **ID Mismatch**: JSON `State.Definitions.Id` MUST match Java registration ID
2. **Missing IncludesAssetPack**: Set to `true` in manifest.json or JSON files won't load
3. **Wrong Base Class**: Must extend `BlockState` from Hytale API, not custom classes
4. **Codec Errors**: BuilderCodec fields must match constructor parameters exactly
5. **Asset Pack Path**: Must follow exact structure: `Server/Item/Items/` for blocks

## Testing Checklist

- [ ] Mod loads without errors
- [ ] Blocks appear in Creative Mode inventory
- [ ] Blocks can be placed in world
- [ ] Block models render correctly
- [ ] Block states change dynamically (active/inactive, cable connections)
- [ ] Terminal opens GUI on right-click
- [ ] Network forms when Controller + Terminal + Cables connected
- [ ] Breaking blocks drops correct items

## Build Commands

```powershell
# Clean build
.\gradlew.bat clean build

# Build only
.\gradlew.bat build

# Deploy to Mods folder
Copy-Item "build\libs\HytaleAE2-1.0.0.jar" "C:\Users\tobia\AppData\Roaming\Hytale\Mods\"
```

## Logging

The mod logs block registration status:
```
→ Registriere BlockStates...
BlockState registration stubbed - requires HytaleServer.jar
✓ BlockState-Registry aktiviert
```

When HytaleServer.jar is available, logs will show:
```
→ Registriere BlockStates...
  ✓ ME Controller BlockState registered
  ✓ ME Terminal BlockState registered
  ✓ ME Cable BlockState registered
  ✓ Terminal Interaction registered
✓ BlockState-Registry aktiviert
```

---

**Last Updated**: January 20, 2026  
**Status**: JSON definitions complete, Java implementation pending HytaleServer.jar
