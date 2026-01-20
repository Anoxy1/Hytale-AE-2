# Hytale Plugin Development - Best Practices Guide

**Quelle:** Community Documentation, CurseForge Plugins, Stack Overflow, GitBook Docs (Januar 2026)

---

## 🏗️ Architecture & Design Patterns

### 1. Singleton Pattern (für Plugin Instance)

```java
public class MEPlugin extends JavaPlugin {
    private static MEPlugin instance;
    
    public MEPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;  // Set singleton in constructor
    }
    
    public static MEPlugin getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Plugin not initialized");
        }
        return instance;
    }
}
```

**Vorteile:**
- ✅ Einfacher Zugriff von überall im Plugin
- ✅ Standard-Pattern für Hytale/Minecraft Plugins
- ✅ Keine statischen Imports nötig

**Nachteile:**
- ⚠️ Tight coupling (schwerer zu testen)
- ⚠️ Global state kann zu Race Conditions führen

### 2. Service-Storage Pattern (für Datenmanagement)

```java
// Storage Layer - Handles persistence
public interface PlayerStorage {
    PlayerData load(UUID playerId);
    void save(UUID playerId, PlayerData data);
}

// Service Layer - Business logic
public class PlayerService {
    private final PlayerStorage storage;
    private final Map<UUID, PlayerData> cache;
    
    public PlayerService(PlayerStorage storage) {
        this.storage = storage;
        this.cache = new ConcurrentHashMap<>();
    }
    
    public PlayerData getPlayer(UUID playerId) {
        return cache.computeIfAbsent(playerId, storage::load);
    }
}
```

**Vorteile:**
- ✅ Separation of Concerns
- ✅ Austauschbare Storage-Implementierungen
- ✅ Testbar durch Dependency Injection

### 3. Entity Component System (ECS)

Hytale nutzt **ECS + Data-Oriented Design** für Optimierung:

```java
// Component (nur Daten)
public class HealthComponent {
    private int current;
    private int max;
    // Getters/Setters
}

// System (Logik)
public class HealthSystem {
    public void update(Entity entity, HealthComponent health) {
        if (health.getCurrent() <= 0) {
            entity.kill();
        }
    }
}
```

**Warum ECS?**
- ✅ Cache-friendly (bessere Performance)
- ✅ Skaliert gut mit vielen Entities
- ✅ Flexibel: Komponenten zur Laufzeit hinzufügen/entfernen

---

## ⚡ Performance & Optimization

### 1. Thread Pool Management

```java
public class MEPlugin extends JavaPlugin {
    private ScheduledExecutorService threadPool;
    
    @Override
    protected void setup() {
        // Named threads für debugging
        this.threadPool = Executors.newScheduledThreadPool(4, r -> {
            Thread thread = new Thread(r);
            thread.setName("MEPlugin-Worker-" + thread.threadId());
            thread.setDaemon(true);  // Don't block shutdown
            return thread;
        });
    }
    
    @Override
    protected void shutdown() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
    }
}
```

### 2. Cache Management mit TTL

```java
public class CacheCleanupTask implements Runnable {
    private final Map<UUID, CachedData> cache;
    private final int maxAgeMinutes;
    
    @Override
    public void run() {
        long now = System.currentTimeMillis();
        cache.entrySet().removeIf(entry -> {
            long age = now - entry.getValue().getTimestamp();
            return age > TimeUnit.MINUTES.toMillis(maxAgeMinutes);
        });
    }
}

// Schedule in setup()
threadPool.scheduleAtFixedRate(
    new CacheCleanupTask(cache, 30),
    5, 5, TimeUnit.MINUTES
);
```

### 3. Entity/AI Optimization

**Häufige Performance-Probleme:**
- ❌ Zu viele Entities in einem Chunk
- ❌ Komplexe AI-Pathfinding ohne Limits
- ❌ Event-Handler mit schweren Operations

**Lösungen:**
```java
// Limit entity processing
if (entities.size() > MAX_ENTITIES_PER_TICK) {
    // Process only subset per tick
    processInBatches(entities, BATCH_SIZE);
}

// Async heavy operations
CompletableFuture.runAsync(() -> {
    // Heavy calculation
    calculateNetworkPaths();
}, threadPool).exceptionally(ex -> {
    logger.error("Async task failed", ex);
    return null;
});
```

### 4. Tick/TPS Optimization

```java
// Avoid doing work every tick
private int tickCounter = 0;

public void onTick() {
    tickCounter++;
    
    // Run heavy tasks only every 20 ticks (1 second)
    if (tickCounter % 20 == 0) {
        updateNetworkConnections();
    }
    
    // Run very heavy tasks every 5 seconds
    if (tickCounter % 100 == 0) {
        saveAllNetworks();
    }
}
```

---

## 🛡️ Error Handling & Security

### 1. Try-Catch Best Practices

```java
@Override
protected void setup() {
    try {
        // Critical initialization
        initializeNetworkManager();
        registerBlockStates();
        initialized = true;
        
    } catch (Exception e) {
        // Log specific error
        logger.error("Failed to initialize ME System", e);
        initialized = false;
        
        // Don't rethrow - allow Hytale to continue
        // (other plugins shouldn't fail because of us)
    }
}
```

**Regeln:**
- ✅ Catch `Throwable` in setup() um Plugin-Loading zu verhindern
- ✅ Immer loggen mit context
- ✅ Set flags (initialized) für safe guards
- ❌ NIEMALS silent failures (empty catch blocks)
- ❌ Nicht Exception-Typ verstecken (catch (Exception) wenn möglich spezifisch)

### 2. Safe Guards Pattern

```java
public void onPlayerInteract(Player player) {
    // Guard: Plugin not initialized
    if (!MEPlugin.isInitialized()) {
        logger.warn("Plugin not ready, ignoring interaction");
        return;
    }
    
    // Guard: Invalid player
    if (player == null || !player.isOnline()) {
        logger.debug("Invalid player state");
        return;
    }
    
    // Guard: Permission check
    if (!player.hasPermission("mesystem.use")) {
        player.sendMessage("No permission");
        return;
    }
    
    // Main logic
    handleInteraction(player);
}
```

### 3. Input Validation

```java
public void setChannels(int channels) {
    // Validate input
    if (channels < 0) {
        throw new IllegalArgumentException("Channels cannot be negative: " + channels);
    }
    if (channels > MAX_CHANNELS) {
        logger.warn("Channels {} exceeds max {}, clamping", channels, MAX_CHANNELS);
        channels = MAX_CHANNELS;
    }
    
    this.channels = channels;
}
```

---

## 📝 Logging Best Practices

### 1. Strukturiertes Logging

```java
// Bad
logger.info("Network created");

// Good
logger.info("Network created: id={}, controller={}, cables={}", 
    network.getId(), 
    controller.getPos(), 
    network.getCableCount()
);
```

### 2. Log Levels richtig verwenden

```java
// TRACE - Very detailed debugging
logger.trace("Checking connection at {}", pos);

// DEBUG - Development debugging
logger.debug("Added node {} to network {}", nodeId, networkId);

// INFO - Important events
logger.info("ME System initialized successfully");

// WARN - Probleme die handled werden können
logger.warn("Failed to load network {}, creating new", networkId);

// ERROR - Kritische Fehler
logger.error("Failed to save network data", exception);
```

### 3. Performance-Aware Logging

```java
// Bad - String concatenation happens even if DEBUG disabled
logger.debug("Network state: " + network.toString());

// Good - Lazy evaluation
logger.debug("Network state: {}", network);

// Best - Conditional expensive operations
if (logger.isDebugEnabled()) {
    logger.debug("Network details: {}", network.getDetailedInfo());
}
```

---

## 🔧 Configuration Management

### 1. Config Pattern

```java
public class PluginConfig {
    private final File configFile;
    private boolean debugMode;
    private int maxNetworks;
    private int autoSaveInterval;
    
    public PluginConfig(File dataFolder) {
        this.configFile = new File(dataFolder, "config.json");
        load();
    }
    
    public void load() {
        if (!configFile.exists()) {
            saveDefaults();
            return;
        }
        
        try {
            // Parse JSON config
            // ...
        } catch (IOException e) {
            logger.error("Failed to load config, using defaults", e);
            saveDefaults();
        }
    }
    
    private void saveDefaults() {
        debugMode = false;
        maxNetworks = 100;
        autoSaveInterval = 300; // 5 minutes
        save();
    }
}
```

---

## 🧪 Testing & Debugging

### 1. Debug Commands

```java
@Override
protected void setup() {
    if (config.isDebugMode()) {
        getCommandRegistry().registerCommand(new DebugCommand());
    }
}

class DebugCommand extends CommandBase {
    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // /medebug networks - List all networks
        // /medebug nodes - List all nodes
        // /medebug gc - Force garbage collection
        
        context.sendMessage(Message.raw(
            "Networks: " + networkManager.getNetworkCount()
        ));
    }
}
```

### 2. Profiling Helper

```java
public class Profiler {
    private final Map<String, Long> timings = new HashMap<>();
    
    public void start(String section) {
        timings.put(section, System.nanoTime());
    }
    
    public void end(String section) {
        long duration = System.nanoTime() - timings.get(section);
        logger.debug("{} took {}ms", section, duration / 1_000_000.0);
    }
}

// Usage
profiler.start("network-update");
updateNetwork();
profiler.end("network-update");
```

---

## 📦 Dependency Management

### 1. Build.gradle Best Practices

```gradle
dependencies {
    // compileOnly für Hytale API (provided at runtime)
    compileOnly files('libs/HytaleServer.jar')
    
    // implementation für Libraries die embedded werden
    implementation 'com.google.code.gson:gson:2.10.1'
    
    // testImplementation für Test-Dependencies
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
}

// Shadow JAR config
shadowJar {
    // Relocate dependencies um Konflikte zu vermeiden
    relocate 'com.google.gson', 'com.tobi.mesystem.libs.gson'
    
    // Minimize JAR size
    minimize()
}
```

---

## 🚀 Deployment Checklist

### Vor dem Release:

- [ ] **Logging:** Alle DEBUG logs disabled oder conditional
- [ ] **Error Handling:** Keine unhandled exceptions
- [ ] **Performance:** Profiling durchgeführt, keine lag spikes
- [ ] **Thread Safety:** Concurrent access geprüft
- [ ] **Memory Leaks:** Alle resources werden in shutdown() freigegeben
- [ ] **Config:** Default config funktioniert out-of-the-box
- [ ] **Documentation:** README.md mit Installation & Config
- [ ] **Testing:** Auf Server mit mehreren Spielern getestet
- [ ] **Compatibility:** Mit anderen beliebten Plugins getestet
- [ ] **Version:** manifest.json Version updated

---

## 📚 Ressourcen

### Offizielle Dokumentation
- **Hytale Docs:** https://hytale-docs.com/docs/modding/plugins/overview
- **Britakee Studios GitBook:** https://britakee-studios.gitbook.io/hytale-modding-documentation

### Tutorials & Guides
- **Kaupenjoe YouTube:** https://www.youtube.com/@Kaupenjoe
  - Plugin Tutorial #1: https://youtu.be/qKI_6gFmnzA (16. Jan 2026)
  - Singleton Pattern: https://youtu.be/MqFKj4sPnEk (6. Jan 2026)
  - Entity Component System: https://youtu.be/uHBtiPzwXQk (12. Jan 2026)

### Templates
- **Kaupenjoe Template:** https://github.com/Kaupenjoe/Hytale-Example-Plugin
- **Britakee Template:** https://github.com/realBritakee/hytale-template-plugin

### Community
- **Discord:** https://discord.gg/hytale
- **HytaleHub:** https://hytalehub.com
- **CurseForge:** https://www.curseforge.com/hytale

---

## ⚠️ Common Pitfalls

### 1. ❌ Static Utility Classes statt Services
```java
// Bad - Hard to test, global state
public static class NetworkUtil {
    public static void update() { }
}

// Good - Testable, injectable
public class NetworkManager {
    public void update() { }
}
```

### 2. ❌ Blocking Operations im Main Thread
```java
// Bad - Freezes server
public void onPlayerJoin(Player player) {
    loadPlayerDataFromDatabase(player);  // Blocking I/O!
}

// Good - Async
public void onPlayerJoin(Player player) {
    CompletableFuture.supplyAsync(() -> 
        loadPlayerDataFromDatabase(player), threadPool
    ).thenAccept(data -> 
        applyPlayerData(player, data)
    );
}
```

### 3. ❌ Memory Leaks durch Event Handlers
```java
// Bad - Handler never removed
getEventRegistry().register(PlayerJoinEvent.class, this::onJoin);

// Good - Track and cleanup
private EventHandler<PlayerJoinEvent> joinHandler;

@Override
protected void setup() {
    joinHandler = getEventRegistry().register(PlayerJoinEvent.class, this::onJoin);
}

@Override
protected void shutdown() {
    if (joinHandler != null) {
        getEventRegistry().unregister(joinHandler);
    }
}
```

---

**Version:** 1.0.0  
**Last Updated:** Januar 20, 2026  
**Hytale Version:** Early Access (Januar 2026)
