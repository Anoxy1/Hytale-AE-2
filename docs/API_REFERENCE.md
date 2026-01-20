# HytaleAE2 - API Reference

**Source:** Decompiled from HytaleServer.jar, Community Plugins  
**Last Updated:** Januar 2026  
**Version:** Hytale Server API (Latest)

---

## 📚 Table of Contents

1. [BlockState System](#blockstate-system)
2. [Codec System](#codec-system)
3. [BlockStateRegistry](#blockstateregistry)
4. [Event System](#event-system)
5. [GUI System](#gui-system)
6. [Plugin Lifecycle](#plugin-lifecycle)

---

## BlockState System

### Core Class Structure

```java
public abstract class com.hypixel.hytale.server.core.universe.world.meta.BlockState 
    implements com.hypixel.hytale.component.Component<ChunkStore>
```

**Key Points:**
- ✅ **ABSTRACT CLASS** (not interface) - Must extend `BlockState`
- ✅ Required for all custom blocks
- ❌ Cannot instantiate directly - needs concrete implementation
- Implements `Component<ChunkStore>` for world integration

### Static Fields (Codec Integration)
```java
public static final CodecMapCodec<BlockState> CODEC;
public static final BuilderCodec<BlockState> BASE_CODEC;
public static final KeyedCodec<String> TYPE_STRUCTURE;
```

**Critical:** Use `BASE_CODEC` as parent when creating custom BlockState codecs!

### Example: ME Controller BlockState
```java
public class MEControllerBlockState extends BlockState {
    public static final BuilderCodec<MEControllerBlockState> CODEC = 
        BuilderCodec.builder(
            MEControllerBlockState.class,
            MEControllerBlockState::new,
            BlockState.BASE_CODEC  // Parent codec
        )
        .withField("active", Codec.BOOLEAN, state -> state.active)
        .build();
    
    private final boolean active;
    
    public MEControllerBlockState(boolean active) {
        this.active = active;
    }
    
    // Getters, methods...
}
```

---

## Codec System

### Available Codec Types

```java
// Primitive Types
Codec.STRING           // String
Codec.BOOLEAN          // boolean
Codec.INTEGER          // int
Codec.LONG             // long
Codec.FLOAT            // float
Codec.DOUBLE           // double
Codec.BYTE             // byte
Codec.SHORT            // short

// Array Types
Codec.BYTE_ARRAY       // byte[]
Codec.INT_ARRAY        // int[]
Codec.LONG_ARRAY       // long[]
Codec.FLOAT_ARRAY      // float[]
Codec.DOUBLE_ARRAY     // double[]
Codec.STRING_ARRAY     // String[]

// Special Types
Codec.BSON_DOCUMENT    // BsonDocument
Codec.UUID_BINARY      // UUID (binary)
Codec.UUID_STRING      // UUID (string)
Codec.PATH             // Path
Codec.INSTANT          // Instant
```

### BuilderCodec API

#### For Concrete Classes
```java
public static <T> BuilderCodec.Builder<T> builder(
    Class<T> clazz,
    Supplier<T> constructor,
    BuilderCodec<? super T> parentCodec
)
```

**Example:**
```java
BuilderCodec<MECableBlockState> CODEC = 
    BuilderCodec.builder(
        MECableBlockState.class,
        MECableBlockState::new,
        BlockState.BASE_CODEC
    )
    .withField("connections", Codec.INT, state -> state.connections)
    .build();
```

#### For Abstract Classes
```java
public static <T> BuilderCodec.Builder<T> abstractBuilder(
    Class<T> clazz,
    BuilderCodec<? super T> parentCodec
)
```

**Example:**
```java
BuilderCodec<CustomItemState> CODEC = 
    BuilderCodec.abstractBuilder(
        CustomItemState.class,
        ItemState.BASE_CODEC
    )
    .build();
```

### Complex Codec Patterns

#### Map Codec (from ChestTerminal)
```java
public static final Codec<Map<String, Integer>> ITEM_MAP_CODEC = 
    Codec.unboundedMap(Codec.STRING, Codec.INTEGER);
```

#### Nested Builder (from ChestTerminal)
```java
.withField("items", 
    Codec.unboundedMap(Codec.STRING, Codec.INTEGER),
    state -> state.items
)
.withField("itemData",
    BsonDocumentCodec.INSTANCE,
    state -> state.itemData
)
```

---

## BlockStateRegistry

### Registration Flow

```java
// 1. Get Registry Service
BlockStateRegistry registry = getServices().getService(BlockStateRegistry.class);

// 2. Register BlockState
registry.registerBlockState(
    MEControllerBlockState.class,
    "ME_Controller",  // Must match JSON State.Definitions.Id
    MEControllerBlockState.CODEC
);
```

### JSON Block Definition Structure

**Location:** `src/main/resources/Server/Item/Items/<BlockName>.json`

```json
{
  "Id": "namespace:block_id",
  "Icon": "namespace:items/icon_path",
  "Name": "Display Name",
  "State": {
    "Definitions": {
      "Id": "BlockState_ID"
    }
  }
}
```

**Critical Rules:**
- `State.Definitions.Id` must match registration ID in Java
- `Id` field is the full item identifier
- Asset pack must be included via `IncludesAssetPack: true` in manifest.json

### Complete Registration Example

**1. JSON Definition** (`Server/Item/Items/MEController.json`)
```json
{
  "Id": "HytaleAE2:ME_Controller",
  "Icon": "HytaleAE2:items/me_controller",
  "Name": "ME Controller",
  "State": {
    "Definitions": {
      "Id": "ME_Controller"
    }
  }
}
```

**2. BlockState Class**
```java
public class MEControllerBlockState extends BlockState {
    public static final BuilderCodec<MEControllerBlockState> CODEC = 
        BuilderCodec.builder(
            MEControllerBlockState.class,
            MEControllerBlockState::new,
            BlockState.BASE_CODEC
        )
        .withField("active", Codec.BOOLEAN, state -> state.active)
        .build();
    
    private final boolean active;
    
    public MEControllerBlockState(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }
}
```

**3. Registration in Plugin**
```java
@Override
protected void setup() {
    BlockStateRegistry registry = getServices().getService(BlockStateRegistry.class);
    
    registry.registerBlockState(
        MEControllerBlockState.class,
        "ME_Controller",
        MEControllerBlockState.CODEC
    );
    
    getLogger().info("Registered ME Controller block");
}
```

---

## Event System

### Event Mapping (Hytale API)

```java
// Block Events
@EventHandler
public void onBlockPlace(BlockPlaceEvent event) {
    BlockPos pos = event.getPosition();
    BlockState state = event.getBlockState();
    Player player = event.getPlayer();
}

@EventHandler
public void onBlockBreak(BlockBreakEvent event) {
    BlockPos pos = event.getPosition();
    BlockState state = event.getBlockState();
}

@EventHandler
public void onBlockInteract(BlockInteractEvent event) {
    BlockPos pos = event.getPosition();
    Player player = event.getPlayer();
    // Open GUI, toggle state, etc.
}
```

### Tickable Blocks
```java
public class METerminalBlockState extends BlockState 
    implements TickableBlockState {
    
    @Override
    public void tick() {
        // Called every server tick
        // Update network, process imports/exports
    }
}
```

### Event Registration Pattern
```java
@Override
protected void start() {
    getServer().getEventBus().register(this);
}
```

---

## GUI System

### InteractiveCustomUIPage (from ChestTerminal)

```java
public class METerminalGUI extends InteractiveCustomUIPage {
    
    public METerminalGUI(Player player, MENetwork network) {
        super("ME Terminal", 54); // 54 slots (6 rows)
        this.player = player;
        this.network = network;
        initializeUI();
    }
    
    private void initializeUI() {
        // Add slots, buttons, etc.
        addButton(0, "Search", this::onSearchClick);
        addItemDisplay(10, 44, network.getStoredItems());
    }
    
    @Override
    public void onSlotClick(int slot, ClickType clickType) {
        // Handle item extraction/insertion
        if (clickType == ClickType.LEFT_CLICK) {
            network.extractItem(itemId, 1);
        }
    }
    
    @Override
    public void onClose() {
        // Cleanup
    }
}
```

### Opening GUIs on Block Interaction
```java
@EventHandler
public void onBlockInteract(BlockInteractEvent event) {
    if (event.getBlockState() instanceof METerminalBlockState) {
        Player player = event.getPlayer();
        MENetwork network = getNetworkAt(event.getPosition());
        
        METerminalGUI gui = new METerminalGUI(player, network);
        player.openGUI(gui);
        
        event.setCancelled(true);
    }
}
```

---

## Plugin Lifecycle

### JavaPlugin Class Structure

```java
public class MEPlugin extends JavaPlugin {
    private static MEPlugin instance;
    
    public MEPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }
    
    @Override
    protected void setup() {
        // Register blocks, items, events
        // Called during plugin initialization
        getLogger().info("Setting up ME System...");
        registerBlockStates();
    }
    
    @Override
    protected void start() {
        // Start services, tick tasks
        // Called when plugin is fully loaded
        getLogger().info("ME System started!");
        getServer().getEventBus().register(this);
    }
    
    public static MEPlugin getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Plugin not initialized");
        }
        return instance;
    }
}
```

### Manifest.json Requirements

**Location:** `src/main/resources/manifest.json`

```json
{
  "Id": "HytaleAE2",
  "Version": "0.1.0-SNAPSHOT",
  "Name": "Applied Energistics 2 for Hytale",
  "Description": "ME Storage System",
  "Author": "YourName",
  "LoadOn": "server",
  "IncludesAssetPack": true,
  "JavaPlugin": "com.tobi.mesystem.MEPlugin"
}
```

**Critical Fields:**
- `JavaPlugin` - Fully qualified class name
- `LoadOn: "server"` - Required for server plugins
- `IncludesAssetPack: true` - Required for embedded assets

---

## 🔍 Real-World Examples

### ChestTerminal (Digital Storage)
```java
public class ChestTerminalBlockState extends ItemContainerState 
    implements TickableBlockState {
    
    private final Map<String, Integer> items = new HashMap<>();
    
    public void storeItem(String itemId, int amount) {
        items.merge(itemId, amount, Integer::sum);
    }
    
    public int extractItem(String itemId, int amount) {
        int available = items.getOrDefault(itemId, 0);
        int toExtract = Math.min(available, amount);
        items.put(itemId, available - toExtract);
        return toExtract;
    }
    
    @Override
    public void tick() {
        // Periodic updates
    }
}
```

### HyPipes (Network System)
```java
public class PipeNetwork {
    private final Map<BlockPos, PipeNode> nodes = new HashMap<>();
    
    public void addNode(BlockPos pos, PipeNode node) {
        nodes.put(pos, node);
        node.setNetwork(this);
    }
    
    public List<BlockPos> findPath(BlockPos start, BlockPos target) {
        // BFS pathfinding implementation
        Queue<BlockPos> queue = new LinkedList<>();
        Map<BlockPos, BlockPos> parent = new HashMap<>();
        // ... BFS logic
        return reconstructPath(parent, target);
    }
}
```

---

## 🚧 Known Limitations & Workarounds

### Missing HytaleServer.jar
**Problem:** Cannot compile without HytaleServer.jar dependencies  
**Workaround:** Use interface-based stubs until official API is available

### Codec Documentation
**Problem:** Limited official documentation  
**Solution:** Refer to decompiled ChestTerminal/HyPipes examples

### Event System Changes
**Problem:** API may change between versions  
**Solution:** Always check manifest.json for correct API version

---

## 📚 Related Documentation

- [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) - Development roadmap
- [PROJECT_STATUS.md](PROJECT_STATUS.md) - Current implementation status
- [HYTALE_MANIFEST_FORMAT.md](HYTALE_MANIFEST_FORMAT.md) - Manifest configuration
- [../PLUGIN_BEST_PRACTICES.md](../PLUGIN_BEST_PRACTICES.md) - Best practices

---

**Last Updated:** Januar 2026  
**Status:** Complete API Documentation ✅
