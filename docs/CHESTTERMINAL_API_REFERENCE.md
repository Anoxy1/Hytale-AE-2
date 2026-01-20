# ChestTerminal API Reference

**Source:** Decompiled from ChestTerminal-2.0.8.jar  
**Date:** January 20, 2026

## Wichtige Klassen & Methoden

### 1. UnifiedTerminalGui

**Package:** `com.chestterminal.gui`

**Extends:** `InteractiveCustomUIPage<TerminalGuiData>`

**Constructor:**
```java
public UnifiedTerminalGui(
    PlayerRef playerRef,
    CustomPageLifetime lifetime,
    ItemContainer terminalInventory,
    Vector3i blockPosition,
    HashMap<String, Integer> nearbyItems
)
```

**Key Methods:**
- `build()` - Baut die GUI
- `handleDataEvent(Ref, Store, TerminalGuiData)` - Verarbeitet UI Events
- `buildStorageView()` - Baut die Storage-Ansicht
- `buildSearchList()` - Baut die Such-Liste
- `extractItems(World, Ref, Store, int distance, String itemId, int quantity)` - Extrahiert Items aus Containern
- `notifyNearbyItems()` - Benachrichtigt über Items in der Nähe

**Fields:**
```java
private String searchQuery;
private HashMap<String, Integer> nearbyItems;
private final Map<String, Item> visibleItems;
private boolean isSearchMode;
private final ItemContainer terminalInventory;
private final Vector3i blockPosition;
```

---

### 2. ChestTerminalBlockState

**Package:** `com.chestterminal`

**Extends:** `ItemContainerState`  
**Implements:** `TickableBlockState`

**Key Methods:**
```java
public void tick(
    float deltaTime,
    int tickCount,
    ArchetypeChunk<ChunkStore> chunk,
    Store<ChunkStore> store,
    CommandBuffer<ChunkStore> commandBuffer
)

private List<Short> distributeItemsToNearbyChests(
    World world,
    ItemContainer container,
    Vector3i position
)

private int tryAddToChest(
    ItemContainer chestContainer,
    ItemStack itemStack,
    int x, int y, int z
)
```

**Purpose:**
- Verwaltet Terminal-BlockState
- Verteilt Items zu nahegelegenen Chests
- Tick-basierte Updates

---

### 3. InventoryUtils

**Package:** `com.chestterminal.util`

**Static Utility Methods:**

```java
public static HashMap<String, Integer> collectNearbyItems(
    World world,
    Ref<EntityStore> entityRef,
    Store<EntityStore> store,
    int radius
)
```
**Zweck:** Sammelt alle Items aus Containern in einem bestimmten Radius

```java
public static boolean isBlockInteractable(
    Ref<EntityStore> entityRef,
    World world,
    int x, int y, int z
)
```
**Zweck:** Prüft ob Block interagierbar ist

---

## Verwendung in HytaleAE2

### Container-Suche implementieren:

Basierend auf ChestTerminal können wir:

1. **World-Referenz nutzen** wie ChestTerminal es macht:
   ```java
   // In MENode - World als Parameter speichern
   private volatile Object world;
   
   public void setWorld(Object world) {
       this.world = world;
   }
   ```

2. **Container finden via World API**:
   ```java
   // ChestTerminal nutzt:
   // world.getBlockEntity(x, y, z) oder ähnliche Methode
   private Object findContainerAt(BlockPos pos) {
       if (world == null) return null;
       
       try {
           // Reflection-basierter Zugriff
           Object blockEntity = world.getClass()
               .getMethod("getBlockEntity", int.class, int.class, int.class)
               .invoke(world, pos.getX(), pos.getY(), pos.getZ());
           
           if (blockEntity instanceof ItemContainer) {
               return blockEntity;
           }
       } catch (Exception e) {
           // Fallback
       }
       
       return null;
   }
   ```

3. **Items sammeln aus Radius** (wie InventoryUtils.collectNearbyItems):
   ```java
   public Map<String, Integer> collectNearbyItems(int radius) {
       Map<String, Integer> items = new HashMap<>();
       
       // Durchsuche alle Blöcke in Radius
       for (int dx = -radius; dx <= radius; dx++) {
           for (int dy = -radius; dy <= radius; dy++) {
               for (int dz = -radius; dz <= radius; dz++) {
                   BlockPos neighborPos = position.offset(dx, dy, dz);
                   Object container = findContainerAt(neighborPos);
                   
                   if (container instanceof ItemContainer) {
                       ItemContainer itemContainer = (ItemContainer) container;
                       // Sammle Items...
                   }
               }
           }
       }
       
       return items;
   }
   ```

---

## GUI Implementation Pattern

ChestTerminal nutzt `InteractiveCustomUIPage<T>` für GUIs:

```java
public class METerminalGui extends InteractiveCustomUIPage<METerminalGuiData> {
    
    private final ItemContainer terminalInventory;
    private final BlockPos position;
    private final MENetwork network;
    
    public METerminalGui(PlayerRef player, MENetwork network, BlockPos pos) {
        super(player, CustomPageLifetime.UNTIL_CLOSED);
        this.network = network;
        this.position = pos;
        this.terminalInventory = new SimpleItemContainer((short) 54); // 6 rows
    }
    
    @Override
    public void build(
        Ref<EntityStore> ref,
        UICommandBuilder commands,
        UIEventBuilder events,
        Store<EntityStore> store
    ) {
        // Build UI hier
        // Nutze commands.setXXX() um UI-Elemente zu setzen
        // Nutze events.onXXX() um Event-Handler zu registrieren
    }
    
    @Override
    public void handleDataEvent(
        Ref<EntityStore> ref,
        Store<EntityStore> store,
        METerminalGuiData data
    ) {
        // Handle UI Events (Clicks, etc.)
    }
}
```

---

## Key Takeaways

1. **Container-Zugriff**: Nutze `world.getBlockEntity(x,y,z)` via Reflection
2. **GUI Framework**: Extends `InteractiveCustomUIPage<T>`
3. **Item-Sammlung**: Iteriere über Radius und prüfe jede Position
4. **ItemContainer**: Standard Hytale API für Container-Zugriff
5. **Tick-Updates**: Implementiere `TickableBlockState` für periodische Updates

---

## Nächste Schritte für HytaleAE2

1. ✅ World-Referenz in MENode hinzugefügt
2. ✅ findContainerAt() implementiert mit Reflection
3. ⏳ METerminalGui Klasse erstellen (basierend auf UnifiedTerminalGui Pattern)
4. ⏳ UI-File erstellen (`resources/Pages/ME_Terminal.ui`)
5. ⏳ collectNearbyItems() für Import/Export Bus Radius-Suche

