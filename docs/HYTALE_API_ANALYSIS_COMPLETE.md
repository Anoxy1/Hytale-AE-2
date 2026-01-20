# Hytale Server API - Complete Analysis (Decompiled from JAR)

**Date**: January 20, 2026  
**Source**: Decompiled from HytaleServer.jar, ChestTerminal-2.0.8.jar, HyPipes-1.0.5-SNAPSHOT.jar

---

## 1. BlockState - Core Class Structure

### Class Declaration
```java
public abstract class com.hypixel.hytale.server.core.universe.world.meta.BlockState 
    implements com.hypixel.hytale.component.Component<com.hypixel.hytale.server.core.universe.world.storage.ChunkStore>
```

**Key Points:**
- ✅ **CLASS, not interface** - Must extend `BlockState`
- ✅ **ABSTRACT** - Requires concrete implementation
- ❌ **NOT DEPRECATED** - No deprecation markers found
- Implements `Component<ChunkStore>` interface

### Static Fields (Important for Codec)
```java
public static final com.hypixel.hytale.codec.lookup.CodecMapCodec<BlockState> CODEC;
public static final com.hypixel.hytale.codec.builder.BuilderCodec<BlockState> BASE_CODEC;
public static final com.hypixel.hytale.codec.KeyedCodec<String> TYPE_STRUCTURE;
```

**Critical Discovery**: BlockState has a `BASE_CODEC` that must be used as parent!

---

## 2. Codec System - Complete API

### Available Codec Types (from Codec interface)
```java
public interface com.hypixel.hytale.codec.Codec<T> {
    // Simple Types
    public static final StringCodec STRING;
    public static final BooleanCodec BOOLEAN;
    public static final DoubleCodec DOUBLE;
    public static final FloatCodec FLOAT;
    public static final ByteCodec BYTE;
    public static final ShortCodec SHORT;
    public static final IntegerCodec INTEGER;
    public static final LongCodec LONG;
    
    // Arrays
    public static final Codec<byte[]> BYTE_ARRAY;
    public static final DoubleArrayCodec DOUBLE_ARRAY;
    public static final FloatArrayCodec FLOAT_ARRAY;
    public static final IntArrayCodec INT_ARRAY;
    public static final LongArrayCodec LONG_ARRAY;
    public static final ArrayCodec<String> STRING_ARRAY;
    
    // Other Types
    public static final BsonDocumentCodec BSON_DOCUMENT;
    public static final FunctionCodec<String, Path> PATH;
    public static final FunctionCodec<String, Instant> INSTANT;
    public static final UUIDBinaryCodec UUID_BINARY;
    public static final FunctionCodec<String, UUID> UUID_STRING;
}
```

### BuilderCodec - EXACT Method Signatures
```java
public class com.hypixel.hytale.codec.builder.BuilderCodec<T> {
    // Create builder for concrete class
    public static <T> BuilderCodec.Builder<T> builder(
        Class<T>, 
        Supplier<T>, 
        BuilderCodec<? super T> parentCodec
    );
    
    // Create builder for abstract class  
    public static <T> BuilderCodec.Builder<T> abstractBuilder(
        Class<T>,
        BuilderCodec<? super T> parentCodec
    );
}
```

**Critical Pattern**: Use `abstractBuilder()` for abstract classes, `builder()` for concrete classes!

---

## 3. EXACT Working Example - ChestTerminal

### Class Declaration
```java
public class com.chestterminal.ChestTerminalBlockState 
    extends com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState 
    implements com.hypixel.hytale.server.core.universe.world.chunk.state.TickableBlockState
```

**Key Points:**
- Extends `ItemContainerState` (which extends `BlockState`)
- Implements `TickableBlockState` interface for tick() method
- Uses `extends`, NOT `implements`

### EXACT Codec Construction (Decompiled)
```java
static {
    CODEC = BuilderCodec.builder(
            ChestTerminalBlockState.class,                    // Class type
            ChestTerminalBlockState::new,                     // Constructor reference
            BlockState.BASE_CODEC                             // Parent codec!
        )
        .append(
            new KeyedCodec<>("Marker", WorldMapManager.MarkerReference.CODEC),
            ChestTerminalBlockState::setMarker,
            ChestTerminalBlockState::getMarker
        )
        .add()
        .append(
            new KeyedCodec<>("ItemContainer", SimpleItemContainer.CODEC),
            ChestTerminalBlockState::setItemContainer,
            ChestTerminalBlockState::getItemContainer
        )
        .add()
        .build();
}
```

**Pattern Analysis:**
1. ✅ `BuilderCodec.builder()` - NOT `BuilderCodec.of()`
2. ✅ Pass `BlockState.BASE_CODEC` as third parameter
3. ✅ Use `new KeyedCodec<>("FieldName", FIELD_CODEC)`
4. ✅ Use `.append()` then `.add()` for each field
5. ✅ End with `.build()`

### Full Import Statements (Reconstructed)
```java
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import com.hypixel.hytale.server.core.universe.world.chunk.state.TickableBlockState;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
```

---

## 4. ItemContainerBlockState - Interface

### EXACT Definition
```java
public interface com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerBlockState {
    public abstract ItemContainer getItemContainer();
}
```

**Key Points:**
- ✅ **INTERFACE, not class**
- Single method: `getItemContainer()`
- ChestTerminal uses `ItemContainerState` (class) instead, which implements this interface

---

## 5. BlockStateRegistry - Registration API

### EXACT Method Signatures
```java
public class com.hypixel.hytale.server.core.universe.world.meta.BlockStateRegistry {
    // Simple registration
    public <T extends BlockState> BlockStateRegistration registerBlockState(
        Class<T> blockStateClass,
        String id,
        Codec<T> codec
    );
    
    // Registration with StateData
    public <T extends BlockState, D extends StateData> BlockStateRegistration registerBlockState(
        Class<T> blockStateClass,
        String id,
        Codec<T> codec,
        Class<D> stateDataClass,
        Codec<D> stateDataCodec
    );
}
```

**Usage Pattern:**
```java
registry.registerBlockState(
    MEControllerBlockState.class,
    "hytaleae2:me_controller",
    MEControllerBlockState.CODEC
);
```

---

## 6. Dynamic State Pattern - HyPipes Example

### Component-Based Approach
```java
public class com.example.plugin.PipeComponent 
    implements Component<EntityStore> {
    
    public int pipeState;  // Bitmask for connections
    
    public static final BuilderCodec<PipeComponent> CODEC;
    
    static {
        CODEC = BuilderCodec.builder(
                PipeComponent.class,
                PipeComponent::new
            )
            .append(
                new KeyedCodec<>("PipeState", Codec.INTEGER),
                (pipe, state) -> pipe.pipeState = state,
                pipe -> pipe.pipeState
            )
            .add()
            .build();
    }
}
```

**Key Pattern:**
- Uses `int` field as bitmask for 6 directions
- Each bit represents a connection direction
- Stored as Component, not BlockState subclass

---

## 7. Complete Code Template for Your Project

### MEControllerBlockState.java
```java
package com.tobi.mesystem.blocks.state;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;

public class MEControllerBlockState extends ItemContainerState {
    
    // CODEC must be public static final
    public static final Codec<MEControllerBlockState> CODEC;
    
    // Fields
    private boolean active;
    private int energy;
    
    // Default constructor required
    public MEControllerBlockState() {
        super();
        this.active = false;
        this.energy = 0;
    }
    
    // Getters/Setters
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = energy; }
    
    // Static initializer - MUST be at end of class
    static {
        CODEC = BuilderCodec.builder(
                MEControllerBlockState.class,
                MEControllerBlockState::new,
                BlockState.BASE_CODEC  // Critical: use parent codec
            )
            .append(
                new KeyedCodec<>("Active", Codec.BOOLEAN),
                MEControllerBlockState::setActive,
                MEControllerBlockState::isActive
            )
            .add()
            .append(
                new KeyedCodec<>("Energy", Codec.INTEGER),
                MEControllerBlockState::setEnergy,
                MEControllerBlockState::getEnergy
            )
            .add()
            .append(
                new KeyedCodec<>("ItemContainer", SimpleItemContainer.CODEC),
                (state, container) -> state.itemContainer = container,
                state -> state.itemContainer
            )
            .add()
            .build();
    }
}
```

### Registration in Plugin
```java
@Override
public void onInitialize(PluginInitializer initializer) {
    BlockStateRegistry registry = BlockStateModule.get().getRegistry();
    
    registry.registerBlockState(
        MEControllerBlockState.class,
        "hytaleae2:me_controller",
        MEControllerBlockState.CODEC
    );
    
    registry.registerBlockState(
        MECableBlockState.class,
        "hytaleae2:me_cable",
        MECableBlockState.CODEC
    );
}
```

---

## 8. Critical Corrections to Previous Code

### ❌ WRONG (Old Pattern)
```java
// DON'T USE BuilderCodec.of()
public static final Codec<MEControllerBlockState> CODEC = BuilderCodec.of(
    MEControllerBlockState.class,
    MEControllerBlockState::new
);
```

### ✅ CORRECT (Actual API)
```java
// Use BuilderCodec.builder() with BASE_CODEC
public static final Codec<MEControllerBlockState> CODEC;

static {
    CODEC = BuilderCodec.builder(
            MEControllerBlockState.class,
            MEControllerBlockState::new,
            BlockState.BASE_CODEC
        )
        .append(new KeyedCodec<>("Field", Codec.TYPE), setter, getter)
        .add()
        .build();
}
```

### Field Registration Pattern
```java
// Each field needs THREE parts:
.append(
    new KeyedCodec<>("FieldName", FieldCodec),  // 1. Key + codec
    (obj, val) -> obj.field = val,              // 2. Setter (BiConsumer)
    obj -> obj.field                            // 3. Getter (Function)
)
.add()  // Close the field
```

---

## 9. Verification Checklist

Before implementing your BlockStates:

- [ ] Extend `BlockState` or `ItemContainerState`, NOT implement
- [ ] Use `BuilderCodec.builder()` NOT `BuilderCodec.of()`
- [ ] Pass `BlockState.BASE_CODEC` as third parameter
- [ ] Use `new KeyedCodec<>("Name", Codec.TYPE)` for each field
- [ ] Use `.append().add()` pattern for each field
- [ ] Put CODEC in static initializer block
- [ ] Use method references or lambdas for getters/setters
- [ ] End with `.build()`
- [ ] Register in plugin's `onInitialize()`

---

## 10. Additional Notes

### ItemContainerState
ChestTerminal extends `ItemContainerState` which is a class that:
- Extends `BlockState`
- Implements `ItemContainerBlockState` interface
- Provides `itemContainer` field and `getItemContainer()` method
- Handles inventory persistence automatically

### TickableBlockState
If your block needs per-tick updates:
```java
public class MEControllerBlockState extends ItemContainerState 
    implements TickableBlockState {
    
    @Override
    public void tick(
        float deltaTime,
        int entityIndex,
        ArchetypeChunk<ChunkStore> chunk,
        Store<ChunkStore> store,
        CommandBuffer<ChunkStore> buffer
    ) {
        // Per-tick logic here
    }
}
```

### Dynamic Visual States (like cables)
For visual-only states (connections), consider:
1. Component-based approach (like HyPipes)
2. Store bitmask as int field
3. Update BlockType rotation/variant instead of BlockState
4. BlockState for data, BlockType for visuals

---

## Summary

The Hytale BlockState API uses:
- **Inheritance**: Extend `BlockState` or `ItemContainerState`
- **Builder Pattern**: `BuilderCodec.builder()` with parent codec
- **Keyed Fields**: `KeyedCodec` with name and codec type
- **Method References**: Getters/setters as lambda or method ref
- **Registry**: Register class + ID + codec

This matches modern Java patterns (builder, functional) and BSON serialization.
