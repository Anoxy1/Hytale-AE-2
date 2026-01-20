# Hytale Plugin Development - Best Practices Guide

**Quelle:** Community Documentation, CurseForge Plugins, Stack Overflow, GitBook Docs (Januar 2026)  
**REFERENCE**: [HelloPlugin (official)](https://github.com/noel-lang/hytale-example-plugin) - Structure, command patterns, lifecycle  
**CRITICAL**: See [docs/PROJECT_RULES.md](PROJECT_RULES.md#critical-no-emoji-or-unicode-in-codestrings) for emoji/unicode ban

## AI-Übersicht (was, wann, wohin)
- Wann lesen? Vor oder während Java-Code-Änderungen.
- Was drin? HelloPlugin-Struktur, Command-Pattern, Logging-Standards, Anti-Patterns, Performance-Hinweise.
- Direkt springen: [HelloPlugin-Pattern](#hello-plugin-standard-structure-must-follow) · [Command-Beispiel](#2-command-pattern-from-helloplugin) · [Logging/ASCII](#critical-no-emoji-unicode-or-complex-formatting-in-code) · [Anti-Patterns](#-common-pitfalls-anti-patterns-to-avoid)
- AI-Hub: [AGENT_ONBOARDING.md#ai-agent-start](AGENT_ONBOARDING.md#ai-agent-start)

### Digest (maschinenfreundlich)
| Zweck | Abschnitte | Nutze wenn |
| --- | --- | --- |
| HelloPlugin-Struktur & Lifecycle | HelloPlugin Standard Structure | Du neue Klassen/EntryPoints aufsetzt |
| Commands | Command Pattern | Du Befehle/Args implementierst |
| Logging & ASCII | CRITICAL: No Emoji... | Du Logausgaben anpasst |
| Anti-Patterns | Common Pitfalls | Du Code reviewst oder refaktorierst |
| Performance | Performance/Optimization (unten) | Du Laufzeit/Threads optimierst |


## CRITICAL: No Emoji, Unicode, or Complex Formatting in Code

**This is NOT optional.** See [docs/PROJECT_RULES.md](PROJECT_RULES.md) for full context on why.

```java
// WRONG (DO NOT USE)
System.out.println("✓ Container found at: " + pos);
logger.info("━━━ Starting ━━━");
String msg = "🚀 Plugin loaded!";

// CORRECT (USE THIS)
System.out.println("[OK] Container found at: " + pos);
logger.info("========== Starting ==========");
String msg = "Plugin loaded successfully.";
```

**Reason**: Windows console chaos, CI/CD logs garbled, remote SSH sessions fail. ASCII only, always.

---

## HelloPlugin Standard Structure (MUST Follow)

All HytaleAE2 code follows the official [HelloPlugin](https://github.com/noel-lang/hytale-example-plugin) pattern. **Do not deviate.**

### 1. Main Plugin Class

```java
import com.hypixel.hytale.server.HytaleServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HytaleAE2 extends JavaPlugin {
    private static final Logger logger = LoggerFactory.getLogger(HytaleAE2.class);
    private static HytaleAE2 instance;
    
    public HytaleAE2() {
        instance = this;
    }
    
    @Override
    public void onPluginEnable() {
        logger.info("[OK] HytaleAE2 initializing...");
        
        // Register commands (HelloPlugin pattern)
        getCommandRegistry().registerCommand(new MEDebugCommand());
        
        // Register event listeners if needed
        // getEventRegistry().registerEventListener(new MyListener());
        
        logger.info("[OK] HytaleAE2 ready");
    }
    
    @Override
    public void onPluginDisable() {
        logger.info("[OK] HytaleAE2 shutting down");
    }
    
    public static HytaleAE2 getInstance() {
        return instance;
    }
}
```

### 2. Command Pattern (from HelloPlugin)

```java
import com.hypixel.hytale.server.command.AbstractPlayerCommand;
import com.hypixel.hytale.server.player.Player;

public class MEDebugCommand extends AbstractPlayerCommand {
    private static final Logger logger = LoggerFactory.getLogger(MEDebugCommand.class);
    
    @Override
    public String getName() {
        return "medebug";
    }
    
    @Override
    public void execute(Player player, String[] args) {
        // ASCII-safe output only
        StringBuilder sb = new StringBuilder();
        sb.append("=== MENode Debug ===\n");
        sb.append("Status: [OK]\n");
        sb.append("Nodes: ").append(/* count */).append("\n");
        
        player.sendMessage(sb.toString());
        logger.info("[OK] Debug command executed for player: {}", player.getName());
    }
}
```

**Key points**:
- Extend `AbstractPlayerCommand` (HelloPlugin standard)
- Use `player.sendMessage()` for output (ASCII-safe)
- Use `logger` (SLF4J) for system logs
- No emoji, no Unicode formatting

### 3. Manifest (HelloPlugin compliant)

```json
{
  "id": "hytale-ae2",
  "name": "Hytale AE2",
  "version": "0.1.0",
  "author": "Anoxy1",
  "description": "Matter/Energy infrastructure plugin",
  "entrypoint": "com.tobi.HytaleAE2"
}
```

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

### 1. Strukturiertes Logging (SLF4J)

```java
// WRONG: Emoji, string concatenation
logger.info("✓ Network created: " + network.getId());

// CORRECT: ASCII-safe, parameterized logging
logger.info("Network created: id={}, controller={}, cables={}", 
    network.getId(), 
    controller.getPos(), 
    network.getCableCount()
);

// WRONG: Direct System.out in production code
System.out.println("Network initialized!");

// CORRECT: Use logger
logger.info("Network initialized successfully");
```

**Why parameterized logging?**
- Lazy evaluation (better performance if log level is disabled)
- Prevents accidental formatting issues
- Works across all environments (Windows, Linux, CI/CD, SSH)

### 2. Log Levels richtig verwenden

```java
// TRACE - Very detailed debugging (development only)
logger.trace("Checking connection at {}", pos);

// DEBUG - Development debugging  
logger.debug("Added node {} to network {}", nodeId, networkId);

// INFO - Important events
logger.info("[OK] ME System initialized successfully");

// WARN - Problems that are handled
logger.warn("[WARN] Failed to load network {}, creating new", networkId);

// ERROR - Critical failures
logger.error("[ERROR] Failed to save network data", exception);
```

**Anti-patterns**:
```java
// WRONG: Using INFO for debug info
logger.info("Loop iteration: " + i);  // Spam!

// WRONG: No context in error
logger.error("Error occurred");  // Useless!

// CORRECT
if (logger.isDebugEnabled()) {
    logger.debug("Loop iteration: {}", i);
}
logger.error("Failed to save network {} to disk", networkId, exception);
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

## 8. Konkrete Java Plugin-Implementierungen

Praktische, Copy-Paste-ready Beispiele für häufige Plugin-Tasks.

### 8.1 Minimal Main-Plugin-Klasse

```java
package com.deinname.meinplugin;

import com.hypixel.hytale.logger.HytaleLogger;

public class MainPlugin {
    // Statischer Logger fuer die ganze Klasse
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    // Diese Methode wird aufgerufen, wenn das Plugin geladen wird
    public void setup() {
        LOGGER.atInfo().log("MeinPlugin wurde geladen!");
        
        // Hier registrierst du Commands und Events
        getCommandRegistry().registerCommand(new PingCommand());
    }
}
```

**[CRITICAL] Manifest Entry**: Der Main-Eintrag in manifest.json muss exakt auf diese Klasse zeigen (z.B. `com.deinname.meinplugin.MainPlugin`). Falscher Eintrag = Plugin lädt nicht!

### 8.2 Einfacher Command: PingCommand

```java
package com.deinname.meinplugin.commands;

import com.hypixel.hytale.command.CommandBase;
import com.hypixel.hytale.command.CommandContext;
import com.hypixel.hytale.message.Message;
import javax.annotation.Nonnull;

public class PingCommand extends CommandBase {
    
    public PingCommand() {
        // Parameter 1: Command-Name
        // Parameter 2: Translations-Key (kann auch leer sein)
        super("ping", "meinplugin.commands.ping.desc");
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // Nachricht an den Spieler senden
        context.sendMessage(Message.raw("Pong!"));
        
        // Mit Logging
        HytaleLogger logger = HytaleLogger.forEnclosingClass();
        logger.atInfo().log("Spieler %s hat /ping ausgefuehrt", 
            context.senderUUID());
    }
}
```

**Spieler-Erlebnis**: Tippt `/ping` im Chat → Server antwortet mit `Pong!`

### 8.3 Command mit Argumenten: EchoCommand

```java
package com.deinname.meinplugin.commands;

import com.hypixel.hytale.command.CommandBase;
import com.hypixel.hytale.command.CommandContext;
import com.hypixel.hytale.command.argument.ArgTypes;
import com.hypixel.hytale.command.argument.DefaultArg;
import com.hypixel.hytale.command.argument.RequiredArg;
import com.hypixel.hytale.message.Message;
import javax.annotation.Nonnull;

public class EchoCommand extends CommandBase {
    
    // Erforderlicher Argument: Text
    private final RequiredArg<String> textArg = 
        this.withRequiredArg("text", "meinplugin.commands.echo.text", ArgTypes.STRING);
    
    // Optionaler Argument mit Default-Wert: Anzahl (Standard = 1)
    private final DefaultArg<Integer> timesArg = 
        this.withDefaultArg("times", "meinplugin.commands.echo.times", ArgTypes.INTEGER, 1, "1");

    public EchoCommand() {
        super("echo", "meinplugin.commands.echo.desc");
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        String text = textArg.get(context);
        int times = timesArg.get(context);
        
        for (int i = 0; i < times; i++) {
            context.sendMessage(Message.raw(text));
        }
    }
}
```

**Nutzungs-Beispiele:**
- `/echo "Hallo"` → Hallo (einmal)
- `/echo "Hallo" 3` → Hallo, Hallo, Hallo (3x)

### 8.4 Logging mit HytaleLogger

```java
import com.hypixel.hytale.logger.HytaleLogger;

public class MyPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    
    public void someMethod() {
        // Info-Level - fuer wichtige Lifecycle Events
        LOGGER.atInfo().log("Plugin ist aktiv");
        
        // Mit Variablen (printf-Style)
        LOGGER.atInfo().log("Spieler %s verbunden mit UUID %s", 
            "Bob", "uuid-123");
        
        // Warn-Level - fuer potenzielle Probleme
        LOGGER.atWarning().log("Warnung: Feature ist experimentell!");
        
        // Severe-Level - fuer Fehler
        LOGGER.atSevere().log("Fehler: Plugin konnte nicht initialisiert werden");
        
        // Mit Exception Stack Trace
        try {
            // Code...
        } catch (Exception e) {
            LOGGER.atSevere().withCause(e).log("Ein Fehler ist aufgetreten");
        }
    }
}
```

**Log-Speicherort**: `{Hytale-Ordner}/UserData/Saves/{World}/logs`

### 8.5 Command nur fuer Spieler (nicht Konsole)

```java
public class PlayerOnlyCommand extends CommandBase {
    
    public PlayerOnlyCommand() {
        super("playercmd", "meinplugin.commands.playeronly.desc");
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // Pruefe, ob der Befehl von einem Spieler kommt
        if (!context.isPlayer()) {
            context.sendMessage(Message.raw("[ERROR] Nur Spieler koennen diesen Befehl nutzen!"));
            return;
        }
        
        // Ab hier ist es SICHER, dass es ein Spieler ist
        Player player = context.senderAs(Player.class);
        player.sendMessage(Message.raw("[OK] Hallo Spieler!"));
    }
}
```

### 8.6 Manifest-Beispiel (src/main/resources/manifest.json)

```json
{
  "Group": "de.deinname",
  "Name": "MeinPlugin",
  "Version": "1.0.0",
  "Description": "Mein erstes Hytale-Plugin",
  "Authors": [
    {
      "Name": "Dein Name"
    }
  ],
  "Main": "com.deinname.meinplugin.MainPlugin",
  "IncludesAssetPack": false,
  "SubPlugins": []
}
```

**[CRITICAL] Main-Feld**: Muss exakt auf deine Haupt-Klasse zeigen. Falsche Angabe = Plugin lädt nicht!

### 8.7 Commands in setup() Registrieren

```java
public class MainPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public void setup() {
        LOGGER.atInfo().log("MainPlugin wird initialisiert...");
        
        // Commands registrieren
        getCommandRegistry().registerCommand(new PingCommand());
        getCommandRegistry().registerCommand(new EchoCommand());
        getCommandRegistry().registerCommand(new PlayerOnlyCommand());
        
        LOGGER.atInfo().log("Alle Commands sind registriert!");
    }
}
```

### 8.8 Schnell-Checkliste fuer erstes Plugin

```
Schritt 1: [ ] Template aus GitHub clonen
           [ ] Projekt: https://github.com/Kaupenjoe/Hytale-Example-Plugin

Schritt 2: [ ] Build-Datei anpassen
           [ ] settings.gradle: Projektnamen anpassen
           [ ] manifest.json: Group, Name, Main anpassen

Schritt 3: [ ] Plugin-Code schreiben
           [ ] Neue Klasse erstellen (z.B. PingCommand extends CommandBase)
           [ ] Command-Logik implementieren
           [ ] In setup() mit getCommandRegistry().registerCommand() registrieren

Schritt 4: [ ] Kompilieren & Testen
           [ ] Mit gradle build kompilieren
           [ ] JAR aus build/libs/ in {Hytale}/Mods/ kopieren
           [ ] Server starten
           [ ] Im Spiel /ping testen

Fortgeschrittene Features: Event-Listener, Custom-Komponenten, Async-Befehle bauen auf diesen Grundmustern auf.
```

---

## 9. ⚠️ Common Pitfalls

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

## ⚠️ Common Pitfalls (Anti-Patterns to Avoid)

### 1. Emoji/Unicode in Code (THE #1 PROBLEM)

**History**: This project spent 2 weeks debugging garbled console output. Root cause: emoji and box-drawing in strings.

```java
// WRONG (DO NOT USE EVER)
logger.info("✓ Container found");
System.out.println("┌─ Status ─┐");
String banner = "🚀 Initializing...";
CommandOutput output = "━━━━━━━━━━━━━━━━";

// What you see in output:
// [INFO] ? Container found
// ┌─ Status ─┐   (or replaced with ???)
// ?? Initializing...
// ????????   (or other garbled chars)

// CORRECT (USE THIS)
logger.info("[OK] Container found");
System.out.println("=== Status ===");
String banner = "Initializing...";
CommandOutput output = "================";

// What you see in output:
// [INFO] [OK] Container found
// === Status ===
// Initializing...
// ================   (clean, consistent)
```

**Why it breaks**:
- Windows console defaults to code page 850/437 (not UTF-8)
- CI/CD logs often strip Unicode
- SSH remote sessions may not support UTF-8
- Hytale server logs may be collected in non-UTF-8 environment

**Solution**: ASCII ONLY in all code. No exceptions. No "but I used UTF-8 encoding". Still no.

### 2. String Concatenation in Logging

```java
// WRONG: Performance penalty + harder to read
logger.info("Player " + player.getName() + " joined with level " + level);

// CORRECT: Lazy evaluation + clear parameters
logger.info("Player {} joined with level {}", player.getName(), level);
```

### 3. Mixing ERROR and WARNING

```java
// WRONG: No distinction
logger.error("Container not found at " + pos);  // This is a WARN, not ERROR

// CORRECT: Proper severity
logger.warn("[WARN] Container not found at {}", pos);

// ERROR is for unrecoverable failures:
logger.error("[ERROR] Failed to save ME network to database", exception);
```

### 4. Logging without Context

```java
// WRONG: Impossible to debug
logger.error("Operation failed");

// CORRECT: Include all relevant data
logger.error("[ERROR] Failed to extract {} items from container at {} in world {}", 
    itemCount, containerPos, world.getName(), exception);
```

### 5. Blocking Operations in Async Contexts

```java
// WRONG: Blocks event thread
public void onTick() {
    ContainerData data = loadFromDatabase();  // BLOCKING!
    updateUI(data);
}

// CORRECT: Use thread pool
public void onTick() {
    threadPool.submit(() -> {
        ContainerData data = loadFromDatabase();
        updateUI(data);  // Safe to call back to main thread
    });
}
```

### 6. Direct System.out in Libraries

```java
// WRONG: Pollutes stdout, uncontrollable
public class ContainerUtils {
    public void debugContainers() {
        System.out.println("Found " + containers.size() + " containers");  // Bad!
    }
}

// CORRECT: Use logger, let caller configure levels
public class ContainerUtils {
    private static final Logger logger = LoggerFactory.getLogger(ContainerUtils.class);
    
    public void debugContainers() {
        logger.debug("Found {} containers", containers.size());  // Good!
    }
}
```

---

**Version:** 1.2.0  
**Last Updated:** Januar 21, 2026  
**Hytale Version:** Early Access (Januar 2026)  
**Critical Updates**: v1.2 – Added concrete Java plugin implementation examples (8 sections), copy-paste-ready code, quick start checklist  
**Critical Updates**: v1.1 – Added HelloPlugin patterns, emoji/unicode ban with anti-patterns, logging best practices, common pitfalls
