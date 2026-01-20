package com.tobi.mesystem;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.tobi.mesystem.commands.MEStatusCommand;
import com.tobi.mesystem.config.MEConfig;
import com.tobi.mesystem.util.BlockPos;
import com.tobi.mesystem.util.NetworkManager;

/**
 * HytaleAE2 - Applied Energistics 2-style ME (Matter/Energy) System for Hytale
 * 
 * Main plugin class following HelloPlugin standards:
 * https://github.com/noel-lang/hytale-example-plugin
 * 
 * Features:
 * - Digital storage system (inspired by Applied Energistics)
 * - Network-based item management
 * - Channel system (8/32 channels)
 * - Multi-block structures (cables, terminals, controllers)
 * 
 * @author Anoxy1
 * @version 0.1.0
 * @since 0.1.0
 * @see NetworkManager
 * @see MENetwork
 * @see MENode
 */
public class MEPlugin extends JavaPlugin {

    private static MEPlugin instance;
    private final NetworkManager networkManager;
    private MEConfig config;
    private ScheduledExecutorService threadPool;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;

    /**
     * Constructs the MEPlugin instance.
     * 
     * Called by Hytale during plugin initialization.
     * Sets up the singleton instance and initializes core managers.
     * 
     * @param init the plugin initialization context from Hytale
     */
    public MEPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        
        // Initialize early to ensure availability
        getLogger().at(Level.INFO).log("===================================================");
        getLogger().at(Level.INFO).log("       HytaleAE2 Constructor - Initializing       ");
        getLogger().at(Level.INFO).log("===================================================");
        
        this.networkManager = new NetworkManager();
    }

    /**
     * Plugin setup phase.
     * 
     * Called by Hytale after construction. This is where we:
     * 1. Load configuration
     * 2. Initialize subsystems
     * 3. Register commands and events
     * 4. Start background tasks
     * 
     * Follows HelloPlugin pattern of centralized initialization.
     */
    @Override
    protected void setup() {
        super.setup();
        
        getLogger().at(Level.INFO).log("=================================================");
        getLogger().at(Level.INFO).log("       HytaleAE2 - Setup Phase Started          ");
        getLogger().at(Level.INFO).log("=================================================");
        
        try {
            // Load configuration first (other systems may need it)
            loadConfiguration();
            getLogger().at(Level.INFO).log("[OK] Configuration loaded");
            
            // Thread Pool initialisieren
            initializeThreadPool();
            getLogger().at(Level.INFO).log("[OK] Thread Pool initialized (%d threads)", config.getThreadPoolSize());
            
            // NetworkManager starten
            networkManager.start();
            getLogger().at(Level.INFO).log("[OK] NetworkManager started");
            
            // BlockState Codecs initialisieren (lädt statische Blöcke)
            initializeBlockStateCodecs();
            getLogger().at(Level.INFO).log("[OK] BlockState Codecs initialized");

            // Commands registrieren
            registerCommands();
            getLogger().at(Level.INFO).log("[OK] Commands registered");
            
            // Event-Listener registrieren
            registerEventListeners();
            getLogger().at(Level.INFO).log("[OK] Event listeners registered");
            
            // Wartungs-Tasks planen
            scheduleMaintenanceTasks();
            getLogger().at(Level.INFO).log("[OK] Maintenance tasks scheduled");
            
            getLogger().at(Level.INFO).log("=================================================");
            getLogger().at(Level.INFO).log("      HytaleAE2 Setup Complete - Ready!        ");
            getLogger().at(Level.INFO).log("      Max Channels: %d | Search Radius: %d    ", 
                config.getMaxChannels(), config.getSearchRadius());
            getLogger().at(Level.INFO).log("=================================================");
        } catch (Exception e) {
            getLogger().at(Level.SEVERE).withCause(e).log("[ERROR] Failed to setup plugin - disabling");
            throw new RuntimeException("Plugin setup failed", e);
        }
    }

    /**
     * Loads plugin configuration from file.
     * Creates default configuration if not exists.
     * 
     * @throws RuntimeException if configuration fails to load
     */
    private void loadConfiguration() {
        try {
            this.config = new MEConfig(getLogger(), getDataDirectory());
            if (!config.load()) {
                getLogger().at(Level.WARNING).log("Failed to load config, using defaults");
            }
            
            // Log important settings
            if (config.isDebugMode()) {
                getLogger().at(Level.INFO).log("Debug mode enabled - verbose logging active");
            }
        } catch (Exception e) {
            getLogger().at(Level.SEVERE).withCause(e).log("Failed to initialize configuration");
            throw new RuntimeException("Configuration initialization failed", e);
        }
    }

    /**
     * Initialisiert Thread Pool für async Operationen
     * Nutzt konfigurierte Thread-Pool-Größe aus config.
     */
    private void initializeThreadPool() {
        final int poolSize = config != null ? config.getThreadPoolSize() : 4;
        this.threadPool = Executors.newScheduledThreadPool(poolSize, r -> {
            Thread thread = new Thread(r);
            thread.setName("MEPlugin-Worker-" + thread.threadId());
            thread.setDaemon(true);
            thread.setUncaughtExceptionHandler((t, e) ->
                getLogger().at(Level.SEVERE).withCause(e).log("Uncaught exception in thread %s", t.getName())
            );
            return thread;
        });
    }

    /**
     * Plant regelmäßige Wartungs-Tasks basierend auf Hytale Task-Scheduling Pattern
     */
    private void scheduleMaintenanceTasks() {
        if (threadPool == null || threadPool.isShutdown()) {
            getLogger().at(Level.WARNING).log("Thread Pool nicht verfügbar - Wartungs-Tasks übersprungen");
            return;
        }

        // Netzwerk-Wartung alle 5 Minuten
        // Initial Delay: 1 Minute (gibt Plugin Zeit zum Stabilisieren)
        threadPool.scheduleAtFixedRate(() -> {
            try {
                getLogger().at(Level.FINE).log("Netzwerk-Wartung gestartet");
                networkManager.cleanupInactiveNetworks();
                networkManager.optimizeChannels();
                String debugInfo = networkManager.getDebugInfo();
                getLogger().at(Level.INFO).log("Wartung abgeschlossen: %s", debugInfo);
            } catch (Exception e) {
                getLogger().at(Level.SEVERE).withCause(e).log("Fehler bei Netzwerk-Wartung");
            }
        }, 1, 5, TimeUnit.MINUTES);
        
        getLogger().at(Level.INFO).log("Task Scheduler: Netzwerk-Wartung alle 5 Minuten");
    }

    /**
     * Registriert Event-Listener bei Hytale
     */
    private void registerEventListeners() {
        try {
            com.hypixel.hytale.event.EventRegistry eventRegistry = getEventRegistry();
            
            if (eventRegistry == null) {
                getLogger().at(Level.WARNING).log("EventRegistry nicht verfügbar");
                return;
            }
            
            // PlaceBlock Event Handler - Based on Hytale Best Practices
            // Loggt detailliert zur Diagnose von Platzierungs-Problemen
            eventRegistry.register(
                com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent.class,
                event -> {
                    try {
                        com.hypixel.hytale.server.core.inventory.ItemStack itemStack = event.getItemInHand();
                        if (itemStack == null) {
                            getLogger().at(Level.FINE).log("PlaceBlockEvent: ItemStack ist null - ignoriert");
                            return;
                        }
                        
                        String itemId = itemStack.getItemId();
                        if (itemId == null) {
                            getLogger().at(Level.FINE).log("PlaceBlockEvent: Item-ID ist null - ignoriert");
                            return;
                        }

                        // Normalize: strip namespace and lowercase for matching
                        String normalized = itemId.contains(":")
                            ? itemId.substring(itemId.lastIndexOf(":") + 1)
                            : itemId;
                        normalized = normalized.toLowerCase();
                        
                        com.hypixel.hytale.math.vector.Vector3i targetBlock = event.getTargetBlock();
                        if (targetBlock == null) {
                            getLogger().at(Level.FINE).log("PlaceBlockEvent: TargetBlock ist null für item=%s", itemId);
                            return;
                        }
                        
                        // Try to extract world via reflection to pass to block handlers
                        Object worldObj = null;
                        try {
                            worldObj = event.getClass().getMethod("getWorld").invoke(event);
                        } catch (Exception e) {
                            getLogger().at(Level.FINE).log("World-Extraktion via Reflection fehlgeschlagen: %s", e.getMessage());
                        }

                        BlockPos pos = new BlockPos(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());

                        getLogger().at(Level.INFO).log("PlaceBlockEvent: item=%s normalized=%s pos=%s", itemId, normalized, pos);
                        
                        // Route basierend auf Item-ID
                        if (normalized.equals("me_cable")) {
                            getLogger().at(Level.INFO).log("Platziere ME Cable bei %s", pos);
                            com.tobi.mesystem.blocks.MECableBlock.onPlaced(pos, worldObj);
                            getLogger().at(Level.FINE).log("ME Cable platziert");
                        } else if (normalized.equals("me_terminal")) {
                            getLogger().at(Level.INFO).log("Platziere ME Terminal bei %s", pos);
                            com.tobi.mesystem.blocks.METerminalBlock.onPlaced(pos, worldObj);
                            getLogger().at(Level.FINE).log("ME Terminal platziert");
                        } else if (normalized.equals("me_controller")) {
                            getLogger().at(Level.INFO).log("Platziere ME Controller bei %s", pos);
                            com.tobi.mesystem.blocks.MEControllerBlock.onPlaced(pos, worldObj);
                            getLogger().at(Level.FINE).log("ME Controller platziert");
                        } else {
                            getLogger().at(Level.FINE).log("Nicht-ME-Item platziert: item=%s normalized=%s", itemId, normalized);
                        }
                    } catch (Exception e) {
                        getLogger().at(Level.SEVERE).withCause(e).log("Fehler in PlaceBlockEvent Handler");
                    }
                }
            );
            
            // BreakBlock Event Handler - Based on Hytale Best Practices
            eventRegistry.register(
                com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent.class,
                event -> {
                    try {
                        com.hypixel.hytale.math.vector.Vector3i targetBlock = event.getTargetBlock();
                        if (targetBlock == null) {
                            getLogger().at(Level.FINE).log("BreakBlockEvent: TargetBlock ist null");
                            return;
                        }
                        
                        // Extract world via reflection
                        Object worldObj = null;
                        try {
                            worldObj = event.getClass().getMethod("getWorld").invoke(event);
                        } catch (Exception e) {
                            getLogger().at(Level.FINE).log("World-Extraktion fehlgeschlagen: %s", e.getMessage());
                        }

                        BlockPos pos = new BlockPos(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
                        
                        // Prüfe ob es ein ME Block ist
                        com.tobi.mesystem.core.MENode node = networkManager.getNode(null, pos);
                        if (node != null) {
                            getLogger().at(Level.INFO).log("Entferne ME Block (%s) bei %s", node.getDeviceType(), pos);
                            
                            switch (node.getDeviceType()) {
                                case CABLE:
                                    com.tobi.mesystem.blocks.MECableBlock.onBroken(pos, worldObj);
                                    getLogger().at(Level.FINE).log("ME Cable entfernt");
                                    break;
                                case TERMINAL:
                                    com.tobi.mesystem.blocks.METerminalBlock.onBroken(pos, worldObj);
                                    getLogger().at(Level.FINE).log("ME Terminal entfernt");
                                    break;
                                case CONTROLLER:
                                    com.tobi.mesystem.blocks.MEControllerBlock.onBroken(pos, worldObj);
                                    getLogger().at(Level.FINE).log("ME Controller entfernt");
                                    break;
                            }
                        } else {
                            getLogger().at(Level.FINE).log("Nicht-ME-Block abgebaut bei %s", pos);
                        }
                    } catch (Exception e) {
                        getLogger().at(Level.SEVERE).withCause(e).log("Fehler in BreakBlockEvent Handler");
                    }
                }
            );
            
            // UseBlock Event Handler - For Terminal GUI interaction
            eventRegistry.register(
                com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent.Pre.class,
                event -> {
                    try {
                        com.hypixel.hytale.math.vector.Vector3i targetBlock = event.getTargetBlock();
                        if (targetBlock == null) {
                            getLogger().at(Level.FINE).log("UseBlockEvent: TargetBlock ist null");
                            return;
                        }
                        
                        // Extract world and player via reflection
                        Object worldObj = null;
                        Object playerObj = null;
                        try {
                            worldObj = event.getClass().getMethod("getWorld").invoke(event);
                        } catch (Exception e) {
                            getLogger().at(Level.FINE).log("World-Extraktion fehlgeschlagen: %s", e.getMessage());
                        }
                        
                        try {
                            // Try common player accessor names
                            try {
                                playerObj = event.getClass().getMethod("getPlayerRef").invoke(event);
                            } catch (NoSuchMethodException nsme) {
                                playerObj = event.getClass().getMethod("getPlayer").invoke(event);
                            }
                        } catch (Exception e) {
                            getLogger().at(Level.FINE).log("Player-Extraktion fehlgeschlagen: %s", e.getMessage());
                        }

                        BlockPos pos = new BlockPos(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
                        
                        // Prüfe ob es ein ME Terminal ist
                        com.tobi.mesystem.core.MENode node = networkManager.getNode(null, pos);
                        if (node != null && node.getDeviceType() == com.tobi.mesystem.core.MEDeviceType.TERMINAL) {
                            getLogger().at(Level.INFO).log("ME Terminal Rechtsklick bei %s", pos);
                            com.tobi.mesystem.blocks.METerminalBlock.onRightClick(pos, worldObj, playerObj);
                            getLogger().at(Level.FINE).log("[OK] Terminal GUI Handler aufgerufen");
                        }
                    } catch (Exception e) {
                        getLogger().at(Level.SEVERE).withCause(e).log("[FEHLER] Fehler in UseBlockEvent Handler");
                    }
                }
            );
            
        } catch (Exception e) {
            getLogger().at(Level.SEVERE).withCause(e).log("Fehler beim Registrieren der Event-Listener");
        }
    }

    /**
     * Initialisiert BlockState Codecs durch Laden der Klassen.
     * Dies stellt sicher, dass die statischen CODEC-Blöcke ausgeführt werden,
     * bevor Hytale versucht, die BlockStates zu erstellen.
     */
    private void initializeBlockStateCodecs() {
        try {
            // Lade Klassen um statische Initialisierung zu triggern
            Class.forName("com.tobi.mesystem.blocks.state.MECableBlockState");
            Class.forName("com.tobi.mesystem.blocks.state.METerminalBlockState");
            Class.forName("com.tobi.mesystem.blocks.state.MEControllerBlockState");
            
            getLogger().at(Level.FINE).log("BlockState Codec Klassen geladen");
        } catch (ClassNotFoundException e) {
            getLogger().at(Level.SEVERE).withCause(e).log("Fehler beim Laden der BlockState Klassen");
        }
    }

    /**
     * Registriert Commands bei Hytale
     */
    private void registerCommands() {
        try {
            this.getCommandRegistry().registerCommand(
                new MEStatusCommand("aestatus", "ME System status and management", false, networkManager)
            );
            this.getCommandRegistry().registerCommand(
                new com.tobi.mesystem.commands.MEPlaceCommand("place", "Debug: Force-place ME blocks", false)
            );
            this.getCommandRegistry().registerCommand(
                new com.tobi.mesystem.commands.MEDebugCommand("medebug", "ME System debug tools", false)
            );
        } catch (Exception e) {
            getLogger().at(Level.WARNING).withCause(e).log("Fehler beim Registrieren der Commands");
        }
    }

    @Override
    protected void start() {
        super.start();
        getLogger().at(Level.INFO).log("HytaleAE2 - Start abgeschlossen");
    }

    @Override
    protected void shutdown() {
        getLogger().at(Level.INFO).log("HytaleAE2 - Shutdown gestartet");
        
        // Stoppe Thread Pool
        if (threadPool != null) {
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        // NetworkManager herunterfahren
        networkManager.shutdown();
        
        getLogger().at(Level.INFO).log("HytaleAE2 - Shutdown abgeschlossen");
    }

    // === Getters ===
    
    /**
     * Gets the singleton instance of this plugin.
     * 
     * @return the plugin instance
     * @throws IllegalStateException if plugin not yet initialized
     */
    public static MEPlugin getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MEPlugin not yet initialized");
        }
        return instance;
    }

    /**
     * Gets the network manager instance.
     * 
     * @return the network manager
     */
    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    /**
     * Gets the configuration instance.
     * 
     * @return the configuration
     */
    public MEConfig getConfig() {
        return config;
    }

    /**
     * Gets the plugin logger.
     * 
     * @return the Hytale logger instance
     */
    public HytaleLogger getPluginLogger() {
        return getLogger();
    }

    /**
     * Gets the thread pool for async operations.
     * 
     * @return the scheduled executor service
     */
    public ScheduledExecutorService getThreadPool() {
        return threadPool;
    }
}
