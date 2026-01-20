# HytaleServer.jar API Analysis - Complete Findings

**Analysis Date**: January 20, 2026  
**Hytale Version**: Early Access (Released January 13, 2026)  
**Source**: HytaleServer.jar decompilation via ChestTerminal & HyPipes mods

---

## Table of Contents

1. [Block System](#block-system)
2. [State Management](#state-management)
3. [Codec System](#codec-system)
4. [Interaction System](#interaction-system)
5. [Inventory System](#inventory-system)
6. [Code Examples](#code-examples)
7. [Common Patterns](#common-patterns)

---

## Block System

### BlockStateRegistry (NOT BlockManager!)

**Critical Finding**: Hytale does **NOT** have a `BlockManager` class. The correct API is `BlockStateRegistry`.

#### Location
```
com.hypixel.hytale.server.core.block.BlockStateRegistry
```

#### Registration Methods

##### Standard Block Registration
```java
public <T extends BlockState> void registerBlockState(
    Class<T> blockStateClass,
    String stateId,
    Codec<T> codec
)
```

**Parameters**:
- `blockStateClass`: Your BlockState class (e.g., `MEControllerBlockState.class`)
- `stateId`: Unique identifier matching JSON `State.Definitions.Id`
- `codec`: BuilderCodec for serialization

**Example**:
```java
BlockStateRegistry registry = getBlockStateRegistry();
registry.registerBlockState(
    MEControllerBlockState.class,
    "ME_Controller",
    MEControllerBlockState.CODEC
);
```

##### Block with Inventory Registration
```java
public <T extends BlockState, D extends StateData> void registerBlockState(
    Class<T> blockStateClass,
    String stateId,
    Codec<T> codec,
    Class<D> stateDataClass,
    Codec<D> stateDataCodec
)
```

**Additional Parameters**:
- `stateDataClass`: Data container class (e.g., `ItemContainerStateData.class`)
- `stateDataCodec`: Codec for the data container

**Example** (from ChestTerminal):
```java
registry.registerBlockState(
    ChestTerminalBlockState.class,
    "ChestTerminal",
    ChestTerminalBlockState.CODEC,
    ItemContainerStateData.class,
    ItemContainerStateData.CODEC
);
```

---

## State Management

### BlockState Base Class

#### Location
```
com.hypixel.hytale.server.core.block.BlockState
```

#### Purpose
All custom block states must extend this class.

#### Key Characteristics
1. **Immutable**: BlockState objects are immutable
2. **State Changes**: Return new instances when changing state
3. **Serializable**: Must provide a BuilderCodec

#### Basic Implementation
```java
import com.hypixel.hytale.server.core.block.BlockState;
import com.hypixel.hytale.server.core.codec.BuilderCodec;
import com.hypixel.hytale.server.core.codec.Codec;

public class MEControllerBlockState extends BlockState {
    public static final BuilderCodec<MEControllerBlockState> CODEC = 
        BuilderCodec.of(MEControllerBlockState::new)
            .withField("active", Codec.BOOL, state -> state.active)
            .build();
    
    private final boolean active;
    
    public MEControllerBlockState(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }
    
    // Immutable state change
    public MEControllerBlockState setActive(boolean active) {
        return new MEControllerBlockState(active);
    }
}
```

---

## Codec System

### BuilderCodec

#### Location
```
com.hypixel.hytale.server.core.codec.BuilderCodec
```

#### Purpose
Serialization/deserialization framework for BlockStates.

#### Pattern
```java
public static final BuilderCodec<YourBlockState> CODEC = 
    BuilderCodec.of(YourBlockState::new)
        .withField("fieldName", Codec.TYPE, state -> state.fieldName)
        .withField("otherField", Codec.TYPE, state -> state.otherField)
        .build();
```

#### Available Codec Types

| Type | Java Type | Description |
|------|-----------|-------------|
| `Codec.BOOL` | `boolean` | Boolean values |
| `Codec.INT` | `int` | Integer values |
| `Codec.LONG` | `long` | Long values |
| `Codec.FLOAT` | `float` | Float values |
| `Codec.DOUBLE` | `double` | Double values |
| `Codec.STRING` | `String` | String values |

#### Constructor Order Requirements
**CRITICAL**: `withField()` order must match constructor parameter order!

**Correct**:
```java
public MECableBlockState(int connections, String owner) { ... }

BuilderCodec.of(MECableBlockState::new)
    .withField("connections", Codec.INT, s -> s.connections)  // 1st parameter
    .withField("owner", Codec.STRING, s -> s.owner)           // 2nd parameter
    .build();
```

**Incorrect** (order mismatch):
```java
BuilderCodec.of(MECableBlockState::new)
    .withField("owner", Codec.STRING, s -> s.owner)           // Wrong!
    .withField("connections", Codec.INT, s -> s.connections)  // Wrong!
    .build();
```

---

## Interaction System

### Interaction Interface

#### Location
```
com.hypixel.hytale.server.core.interaction.Interaction
```

#### Purpose
Handle player interactions with blocks (right-click, etc.).

#### Registration Pattern (from ChestTerminal)
```java
// Register block interaction
getCodecRegistry(Interaction.CODEC)
    .register(
        "ChestTerminal_Interaction",
        ChestTerminalInteraction.class,
        ChestTerminalInteraction.CODEC
    );
```

#### Implementation Example
```java
public class METerminalInteraction implements Interaction {
    public static final Codec<METerminalInteraction> CODEC = 
        BuilderCodec.of(METerminalInteraction::new).build();
    
    @Override
    public void onInteract(Player player, BlockPos pos, BlockState state) {
        // Open GUI for player
        METerminalGUI gui = new METerminalGUI(player, pos);
        gui.open();
    }
}
```

---

## Inventory System

### ItemContainerStateData

#### Location
```
com.hypixel.hytale.server.core.block.data.ItemContainerStateData
```

#### Purpose
Provides inventory storage for blocks (like chests, terminals).

#### Usage Pattern (from ChestTerminal)

##### 1. Register with StateData
```java
registry.registerBlockState(
    METerminalBlockState.class,
    "ME_Terminal",
    METerminalBlockState.CODEC,
    ItemContainerStateData.class,
    ItemContainerStateData.CODEC
);
```

##### 2. Simple BlockState Implementation
```java
public class METerminalBlockState extends BlockState {
    public static final BuilderCodec<METerminalBlockState> CODEC = 
        BuilderCodec.of(METerminalBlockState::new).build();
    
    public METerminalBlockState() {
        // No fields needed - inventory handled by ItemContainerStateData
    }
}
```

##### 3. Access Inventory at Runtime
```java
ItemContainerStateData inventory = getStateData(pos, ItemContainerStateData.class);
ItemStack[] items = inventory.getItems();
```

---

## Code Examples

### Example 1: Simple Block (Controller)

**BlockState Class**:
```java
package com.tobi.mesystem.blocks.state;

import com.hypixel.hytale.server.core.block.BlockState;
import com.hypixel.hytale.server.core.codec.BuilderCodec;
import com.hypixel.hytale.server.core.codec.Codec;

public class MEControllerBlockState extends BlockState {
    public static final BuilderCodec<MEControllerBlockState> CODEC = 
        BuilderCodec.of(MEControllerBlockState::new)
            .withField("active", Codec.BOOL, state -> state.active)
            .build();
    
    private final boolean active;
    
    public MEControllerBlockState(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public MEControllerBlockState setActive(boolean active) {
        return new MEControllerBlockState(active);
    }
}
```

**JSON Definition** (`Server/Item/Items/MEController.json`):
```json
{
  "Id": "HytaleAE2:ME_Controller",
  "State": {
    "Definitions": {
      "Id": "ME_Controller"
    }
  },
  "Physics": {
    "IsPlaceable": true,
    "IsSolid": true,
    "IsReplaceable": false
  },
  "Display": {
    "Name": "ME Controller",
    "Description": "Forms and powers ME Networks"
  }
}
```

**Registration** (in MEPlugin):
```java
BlockStateRegistry registry = getBlockStateRegistry();
registry.registerBlockState(
    MEControllerBlockState.class,
    "ME_Controller",
    MEControllerBlockState.CODEC
);
```

---

### Example 2: Block with Inventory (Terminal)

**BlockState Class**:
```java
package com.tobi.mesystem.blocks.state;

import com.hypixel.hytale.server.core.block.BlockState;
import com.hypixel.hytale.server.core.codec.BuilderCodec;

public class METerminalBlockState extends BlockState {
    public static final BuilderCodec<METerminalBlockState> CODEC = 
        BuilderCodec.of(METerminalBlockState::new).build();
    
    public METerminalBlockState() {
        // Inventory handled by ItemContainerStateData
    }
}
```

**JSON Definition** (`Server/Item/Items/METerminal.json`):
```json
{
  "Id": "HytaleAE2:ME_Terminal",
  "State": {
    "Definitions": {
      "Id": "ME_Terminal"
    }
  },
  "Physics": {
    "IsPlaceable": true,
    "IsSolid": true,
    "IsUsable": true
  },
  "Display": {
    "Name": "ME Terminal",
    "Description": "Access network storage"
  }
}
```

**Registration**:
```java
registry.registerBlockState(
    METerminalBlockState.class,
    "ME_Terminal",
    METerminalBlockState.CODEC,
    ItemContainerStateData.class,
    ItemContainerStateData.CODEC
);
```

**Interaction**:
```java
public class METerminalInteraction implements Interaction {
    public static final Codec<METerminalInteraction> CODEC = 
        BuilderCodec.of(METerminalInteraction::new).build();
    
    @Override
    public void onInteract(Player player, BlockPos pos, BlockState state) {
        METerminalGUI gui = new METerminalGUI(player, pos);
        gui.open();
    }
}

// Register interaction
getCodecRegistry(Interaction.CODEC)
    .register(
        "ME_Terminal_Interaction",
        METerminalInteraction.class,
        METerminalInteraction.CODEC
    );
```

---

### Example 3: Dynamic States (Cable)

**BlockState Class**:
```java
package com.tobi.mesystem.blocks.state;

import com.hypixel.hytale.server.core.block.BlockState;
import com.hypixel.hytale.server.core.codec.BuilderCodec;
import com.hypixel.hytale.server.core.codec.Codec;

public class MECableBlockState extends BlockState {
    public static final BuilderCodec<MECableBlockState> CODEC = 
        BuilderCodec.of(MECableBlockState::new)
            .withField("connections", Codec.INT, state -> state.connections)
            .build();
    
    private final int connections; // Bitmask: 0-63 for 6 directions
    
    public MECableBlockState(int connections) {
        this.connections = connections;
    }
    
    public int getConnections() {
        return connections;
    }
    
    public MECableBlockState setConnections(int connections) {
        return new MECableBlockState(connections);
    }
    
    // Helper methods
    public boolean isConnected(Direction dir) {
        return (connections & (1 << dir.ordinal())) != 0;
    }
    
    public MECableBlockState setConnected(Direction dir, boolean connected) {
        int mask = 1 << dir.ordinal();
        int newConnections = connected 
            ? (connections | mask) 
            : (connections & ~mask);
        return new MECableBlockState(newConnections);
    }
}
```

**JSON Definition** (Multiple state variants):
```json
{
  "Id": "HytaleAE2:ME_Cable",
  "State": {
    "Definitions": {
      "Id": "ME_Cable"
    },
    "Variants": [
      {
        "Connections": 0,
        "Model": "Client/Models/MECable_Straight.blockymodel"
      },
      {
        "Connections": 1,
        "Model": "Client/Models/MECable_Corner.blockymodel"
      }
    ]
  }
}
```

---

## Common Patterns

### Pattern 1: Getting BlockStateRegistry
```java
private BlockStateRegistry getBlockStateRegistry() {
    try {
        // JavaPlugin provides this method at runtime
        return (BlockStateRegistry) getClass()
            .getMethod("getBlockStateRegistry")
            .invoke(this);
    } catch (Exception e) {
        throw new RuntimeException("Failed to get BlockStateRegistry", e);
    }
}
```

### Pattern 2: State Update with Neighbor Check
From HyPipes - update cable connections when neighbors change:
```java
@EventHandler
public void onBlockPlace(BlockPlaceEvent event) {
    BlockPos pos = event.getPosition();
    
    // Check all 6 neighbors
    for (Direction dir : Direction.values()) {
        BlockPos neighborPos = pos.offset(dir);
        BlockState neighborState = getBlockState(neighborPos);
        
        if (neighborState instanceof MECableBlockState) {
            // Update neighbor cable connection
            MECableBlockState cable = (MECableBlockState) neighborState;
            MECableBlockState updated = cable.setConnected(dir.getOpposite(), true);
            setBlockState(neighborPos, updated);
        }
    }
}
```

### Pattern 3: Checking Block Type at Position
```java
public boolean isNetworkBlock(BlockPos pos) {
    BlockState state = getBlockState(pos);
    return state instanceof MEControllerBlockState
        || state instanceof METerminalBlockState
        || state instanceof MECableBlockState;
}
```

### Pattern 4: Asset Pack Auto-Loading
**manifest.json**:
```json
{
  "Group": "com.tobi",
  "Name": "HytaleAE2",
  "Version": "0.1.0-SNAPSHOT",
  "Authors": [
    {
      "Name": "Tobi",
      "Email": "",
      "Url": ""
    }
  ],
  "IncludesAssetPack": true
}
```

**Directory Structure**:
```
src/main/resources/
├── manifest.json
├── Server/
│   ├── Item/
│   │   └── Items/
│   │       ├── MEController.json
│   │       ├── METerminal.json
│   │       └── MECable.json
│   └── Languages/
│       └── en-US/
│           └── items.lang
└── Client/
    ├── Models/
    │   ├── MEController.blockymodel
    │   └── ...
    └── Textures/
        ├── MEController.png
        └── ...
```

When `IncludesAssetPack: true`, Hytale automatically loads all JSON files and assets from the JAR.

---

## Real-World Examples from Analyzed Mods

### ChestTerminal v2.0.8

**Purpose**: Storage block with inventory GUI

**Key Implementations**:
1. **BlockState**: Simple, no fields (inventory via ItemContainerStateData)
2. **Registration**: Includes ItemContainerStateData.class and CODEC
3. **Interaction**: Opens GUI with inventory grid
4. **JSON**: IsUsable: true flag for right-click interaction

**Learned Pattern**: Inventory blocks don't need inventory fields in BlockState - use ItemContainerStateData instead.

---

### HyPipes v1.0.5

**Purpose**: Dynamic pipe network with 60+ visual states

**Key Implementations**:
1. **BlockState**: Single int field for connection bitmask (0-63)
2. **Neighbor Updates**: EventHandler updates connections on block place/break
3. **Visual States**: 60+ .blockymodel files for all connection combinations
4. **JSON Variants**: Maps connection values to model files

**Learned Pattern**: Dynamic visual states via integer bitmask + multiple model variants.

---

## Build.gradle Integration

### Adding HytaleServer.jar

**Correct Dependency** (compileOnly, NOT implementation):
```gradle
dependencies {
    // Hytale provides these at runtime
    compileOnly files('libs/com/hypixel/hytale/server/core/plugin/HytaleServer.jar')
    
    // Other dependencies
    implementation 'org.apache.logging.log4j:log4j-core:2.20.0'
    implementation 'org.apache.logging.log4j:log4j-api:2.20.0'
}
```

**Why compileOnly?**
- Hytale's classloader provides these classes at runtime
- Including them in JAR causes ClassLoader conflicts
- Prevents version mismatches

---

## Critical Rules

### ✅ DO

1. **Use BlockStateRegistry** (NOT BlockManager)
2. **Match JSON State.Definitions.Id** to Java registration string exactly
3. **Make BlockStates immutable** - return new instances on state change
4. **Order withField() calls** to match constructor parameters
5. **Use compileOnly** for HytaleServer.jar in build.gradle
6. **Set IncludesAssetPack: true** in manifest.json
7. **Use ItemContainerStateData** for inventory blocks

### ❌ DON'T

1. **Don't look for BlockManager** - it doesn't exist
2. **Don't use implementation** for HytaleServer.jar - causes runtime errors
3. **Don't make BlockStates mutable** - violates Hytale's state system
4. **Don't mismatch JSON IDs** - blocks won't load
5. **Don't add inventory fields** to BlockState when using ItemContainerStateData
6. **Don't forget IncludesAssetPack** - JSON files won't load

---

## Troubleshooting

### Problem: "Cannot find symbol: BlockStateRegistry"
**Solution**: Add HytaleServer.jar to libs/ and update build.gradle with compileOnly

### Problem: "NoClassDefFoundError: BlockState at runtime"
**Solution**: Change `implementation` to `compileOnly` in build.gradle

### Problem: "Block doesn't appear in Creative inventory"
**Solution**: Check JSON file is in `Server/Item/Items/` and IncludesAssetPack is true

### Problem: "BlockState registration fails"
**Solution**: Verify State.Definitions.Id in JSON matches Java registration string

### Problem: "Codec deserialization error"
**Solution**: Ensure withField() order matches constructor parameter order

### Problem: "Inventory doesn't persist"
**Solution**: Register with ItemContainerStateData.class and CODEC as 4th/5th parameters

---

## Package Structure Reference

```
com.hypixel.hytale.server.core
├── plugin
│   ├── JavaPlugin (base class)
│   └── JavaPluginInit (constructor parameter)
├── block
│   ├── BlockState (extend this)
│   ├── BlockStateRegistry (registration API)
│   └── data
│       └── ItemContainerStateData (inventory support)
├── codec
│   ├── Codec (serialization types)
│   └── BuilderCodec (builder for BlockState codecs)
└── interaction
    └── Interaction (player interaction handling)
```

---

## Version Information

**Hytale Early Access**: January 13, 2026  
**Analysis Date**: January 20, 2026  
**API Stability**: Early Access - subject to change  
**Documentation Source**: Decompiled ChestTerminal v2.0.8 & HyPipes v1.0.5

---

## Further Reading

- [BLOCKSTATE_REGISTRY_GUIDE.md](BLOCKSTATE_REGISTRY_GUIDE.md) - Implementation guide for HytaleAE2
- [NEXT_STEPS_HYTALESERVER.md](NEXT_STEPS_HYTALESERVER.md) - Step-by-step integration
- [HYTALE_MANIFEST_FORMAT.md](HYTALE_MANIFEST_FORMAT.md) - Correct manifest.json format

---

**Last Updated**: January 20, 2026  
**Status**: Complete API analysis from HytaleServer.jar decompilation
