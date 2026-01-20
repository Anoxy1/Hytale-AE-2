# 🚀 HytaleAE2 - Entwicklungsplan (Start)

**Datum:** 20. Januar 2026  
**Status:** Dekompilierung abgeschlossen, Start Phase 1

---

## ✅ Was wir jetzt wissen

### HyPipes - Network System (PERFEKTE BASIS!)

**PipeNetwork.java** (203 Zeilen)
```java
// Graph-basiertes Netzwerk
private final Map<BlockPos, PipeNode> nodes = new HashMap<>();
private final Set<BlockPos> inputNodes = new HashSet<>();
private final Set<BlockPos> outputNodes = new HashSet<>();

// BFS Pathfinding
public List<BlockPos> findPathToSpecificOutput(BlockPos start, BlockPos target)

// Priority + Distribution
public List<BlockPos> getOrderedOutputCandidates(BlockPos start, DistributionStrategy strategy)

// Merging Networks
public void merge(PipeNetwork other)

// Tick System
public void tick()
```

**PipeNode.java** (127 Zeilen)
```java
// Node Properties
private final UUID worldId;
private final BlockPos position;
private final Set<Direction> connections;
private PipeNetwork network;

// Mode System
private PipeMode mode; // INPUT, OUTPUT

// Priority System
private int priority;

// Distribution Strategies
private DistributionStrategy distributionStrategy; // ROUND_ROBIN, NEAREST, FARTHEST

// Transfer Control
public boolean canTransfer()
public void onTransfer()
```

### ChestTerminal - GUI & Storage System

**UnifiedTerminalGui.java** (333 Zeilen)
```java
// Hytale GUI System
extends InteractiveCustomUIPage<TerminalGuiData>

// Storage
private HashMap<String, Integer> nearbyItems; // ItemId -> Amount
private final ItemContainer terminalInventory;

// Search
private String searchQuery = "";
private boolean isSearchMode = false;

// UI Builder Pattern
public void build(UICommandBuilder cmd, UIEventBuilder evt)

// Event Handling
public void handleDataEvent(TerminalGuiData data)
```

---

## 🎯 Phase 1: Minimal Viable Product (MVP)

**Ziel:** Funktionierendes ME-Netzwerk mit Basic Storage

### Woche 1-2: Foundation

#### 1. Projekt-Setup
```bash
HytaleAE2/
├── src/main/java/com/yourname/mesystem/
│   ├── MEPlugin.java
│   ├── core/
│   │   ├── MENetwork.java (extends PipeNetwork Konzept)
│   │   ├── MENode.java (extends PipeNode Konzept)
│   │   ├── MEChannel.java (NEU - Channel System)
│   │   └── MEDeviceType.java (CABLE, TERMINAL, DRIVE, etc.)
│   ├── blocks/
│   │   ├── MECableBlock.java
│   │   └── METerminalBlock.java
│   └── gui/
│       └── METerminalGui.java (basiert auf UnifiedTerminalGui)
├── libs/
│   ├── ChestTerminal-2.0.8.jar
│   └── HyPipes-1.0.5-SNAPSHOT.jar
└── build.gradle
```

#### 2. MENetwork Implementierung
**Basis:** PipeNetwork.java  
**Erweiterungen:**
- Channel-System (32 mit Controller, 8 ohne)
- Digital Storage (HashMap<String, Long> itemStorage)
- Device Registry (Map<BlockPos, MEDeviceType>)

```java
public class MENetwork {
    // Von PipeNetwork übernommen
    private final Map<BlockPos, MENode> nodes = new HashMap<>();
    
    // NEU: Digital Storage
    private final Map<String, Long> itemStorage = new HashMap<>();
    
    // NEU: Channel System
    private int maxChannels = 8; // 32 mit Controller
    private final Map<BlockPos, Integer> channelAllocation = new HashMap<>();
    
    // NEU: Device Tracking
    private final Map<BlockPos, MEDeviceType> devices = new HashMap<>();
    
    // Store Item (digital, wie AE2)
    public boolean storeItem(String itemId, long amount)
    
    // Extract Item
    public long extractItem(String itemId, long amount)
    
    // Get Total Storage
    public Map<String, Long> getAllItems()
}
```

#### 3. MECable Block
**Einfachster Block - Startet das Netzwerk**

```java
public class MECableBlock {
    // Basis-Kabel, verbindet Devices
    // 1 Channel Durchsatz
    // Verwendet PipeNode.connections Logic
}
```

#### 4. METerminal Block + GUI
**Basis:** UnifiedTerminalGui.java  
**Features:**
- Item-Liste anzeigen (aus MENetwork.itemStorage)
- Items extrahieren (ins Player Inventory)
- Simple Search

```java
public class METerminalGui extends InteractiveCustomUIPage<METerminalData> {
    private final MENetwork network;
    
    @Override
    public void build(UICommandBuilder cmd, UIEventBuilder evt) {
        // UI laden
        cmd.append("Pages/ME_Terminal.ui");
        
        // Items anzeigen (aus network.getAllItems())
        Map<String, Long> items = network.getAllItems();
        buildItemList(items);
        
        // Click Events
        evt.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#ItemSlot",
            EventData.of("extractItem", itemId + ":" + amount),
            false
        );
    }
}
```

---

## 📋 Konkrete TODOs (Start JETZT)

### ✅ Schritt 1: Projekt erstellen (30 min)
```bash
cd C:\Users\tobia\Documents\Claude\HytaleAE2

# 1. Gradle Projekt
mkdir -p src/main/java/com/yourname/mesystem
mkdir -p src/main/resources

# 2. build.gradle erstellen
# (siehe build.gradle Datei)

# 3. JARs nach libs kopieren
copy "Ausgangs Mods\ChestTerminal-2.0.8.jar" libs\
copy "Ausgangs Mods\HyPipes-1.0.5-SNAPSHOT.jar" libs\
```

### ✅ Schritt 2: MENetwork.java schreiben (2h)
**Kopiere Konzepte aus PipeNetwork, füge Digital Storage hinzu**

Siehe `src/main/java/com/yourname/mesystem/core/MENetwork.java`

### ✅ Schritt 3: MECable Block (1h)
**Einfachster Block, nutzt bestehende Hytale Block APIs**

### ✅ Schritt 4: Test im Spiel (1h)
```java
// Platziere 3 Cables
// Prüfe: Bilden sie ein Netzwerk?
// Teste: network.addNode() funktioniert?
```

---

## 🔧 Siehe build.gradle Datei

Die Datei ist bereits erstellt: `build.gradle`

---

## 📅 Zeitplan

### ✅ HEUTE (Tag 1) - Foundation
- [x] Plugins dekompiliert
- [ ] Projekt-Setup (30 min)
- [ ] MENetwork.java (2h)
- [ ] MENode.java (1h)
- [ ] build.gradle + Compile Test (30 min)

**Ziel:** Kompiliert, bereit für erste Blocks

### Morgen (Tag 2) - First Block
- [ ] MECable Block implementieren
- [ ] Block-Registrierung (manifest.json)
- [ ] Erster In-Game Test
- [ ] Network-Formation testen

**Ziel:** Cables platzierbar, Netzwerk bildet sich

### Tag 3-7 - Terminal
- [ ] METerminal Block
- [ ] Terminal GUI (basierend auf UnifiedTerminalGui)
- [ ] Item Storage testen
- [ ] Item Extraction testen

**Ziel:** Items ins Netzwerk speichern + extrahieren

---

## 🎓 Was wir aus Dekompilierung gelernt haben

### 1. Hytale Plugin-Struktur
```java
public class MEPlugin extends JavaPlugin {
    @Override
    protected void setup() {
        // Phase 1: Registrierung
        getBlockStateRegistry().registerBlockState(...)
        getCodecRegistry().register(...)
    }
    
    @Override
    protected void start() {
        // Phase 2: Initialisierung
        networkManager = new MENetworkManager();
    }
}
```

### 2. Codec-System (statt NBT!)
```java
public static final Codec<MEData> CODEC = BuilderCodec.create(
    builder -> builder
        .field("items", Codec.MAP, MEData::getItems, MEData::setItems)
        .field("energy", Codec.LONG, MEData::getEnergy, MEData::setEnergy)
        .build(MEData::new)
);
```

### 3. Event-System
```java
// Block Click
public class MEInteraction implements Interaction {
    @Override
    public InteractionResponse interact(InteractionContext ctx) {
        Player player = ctx.getPlayer();
        
        // GUI öffnen
        METerminalGui gui = new METerminalGui(...);
        player.getPageManager().addPage(gui);
        
        return InteractionResponse.success();
    }
}
```

---

## 💡 Entwicklungs-Tipps

### 1. Start Klein
- Erst nur Cables + Network
- Dann Terminal + Storage
- Crafting kommt später

### 2. Teste Früh & Oft
```java
// Unit Tests für MENetwork
@Test
public void testStorageAndExtraction() {
    MENetwork net = new MENetwork();
    net.storeItem("diamond", 10);
    
    long extracted = net.extractItem("diamond", 5);
    assertEquals(5, extracted);
    assertEquals(5, net.getAllItems().get("diamond"));
}
```

### 3. Logging
```java
private static final Logger LOGGER = LogManager.getLogger();

LOGGER.info("Network formed: {} nodes", network.size());
LOGGER.debug("Stored {} x{}", itemId, amount);
```

### 4. Performance
```java
// Cache häufige Berechnungen
private Map<String, Long> cachedItems;
private long lastUpdate;

public Map<String, Long> getAllItems() {
    long now = System.currentTimeMillis();
    if (now - lastUpdate > 1000) { // 1 Sekunde Cache
        cachedItems = calculateItems();
        lastUpdate = now;
    }
    return cachedItems;
}
```

---

## 🆘 Wenn du hängenbleibst

### Problem: "Compile Error - Hytale API nicht gefunden"
**Lösung:** Hytale Maven Repository URL checken, Version anpassen

### Problem: "GUI wird nicht angezeigt"
**Lösung:** 
1. `.ui` Datei vorhanden?
2. CustomPageLifetime richtig gesetzt?
3. Event Bindings korrekt?

### Problem: "Network formt sich nicht"
**Lösung:**
1. Log checken: "Network formed?"
2. addNode() wird aufgerufen?
3. BlockPos korrekt?

---

## 🎯 Definition of Done für Phase 1

- [ ] ME Cable platzierbar
- [ ] ME Terminal platzierbar
- [ ] Terminal öffnet GUI
- [ ] Items werden digital gespeichert (HashMap)
- [ ] Items können extrahiert werden
- [ ] Terminal zeigt gespeicherte Items
- [ ] Basic Search funktioniert

**Wenn das läuft:** 60% von AE2 fertig!

---

## 📊 Nächste Phasen (Überblick)

### Phase 2: Storage Cells (Monat 2)
- ME Drive
- Storage Cells (1k, 4k, 16k, 64k)
- Item Type Limits
- Cell Priority

### Phase 3: Import/Export (Monat 3)
- Import Bus
- Export Bus
- Filtered I/O
- Speed Upgrades

### Phase 4: Auto-Crafting (Monat 4-6)
- Pattern Encoding
- Molecular Assembler
- Crafting CPU
- Recursive Crafting

**Aber erst Phase 1 fertigstellen!**

---

**Bereit zum Starten? Lass uns mit Schritt 1 (Projekt-Setup) beginnen!**
