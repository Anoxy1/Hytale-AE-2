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
import com.tobi.mesystem.util.BlockPos;
import com.tobi.mesystem.util.NetworkManager;

/**
 * HytaleAE2 - Applied Energistics 2 für Hytale
 *
 * Basierend auf dem offiziellen HelloPlugin Beispiel:
 * https://github.com/noel-lang/hytale-example-plugin
 */
public class MEPlugin extends JavaPlugin {

    private static MEPlugin instance;
    private final NetworkManager networkManager;
    private ScheduledExecutorService threadPool;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;

    public MEPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        
        // Log sofort im Konstruktor um zu sehen ob er überhaupt aufgerufen wird
        System.out.println("===================================================");
        System.out.println("MEPlugin Constructor called!");
        System.out.println("===================================================");
        
        this.networkManager = new NetworkManager();
    }

    @Override
    protected void setup() {
        super.setup();
        
        getLogger().at(Level.INFO).log("=================================================");
        getLogger().at(Level.INFO).log("       HytaleAE2 - Setup gestartet              ");
        getLogger().at(Level.INFO).log("=================================================");
        
        // Thread Pool initialisieren
        initializeThreadPool();
        getLogger().at(Level.INFO).log("[OK] Thread Pool initialisiert");
        
        // NetworkManager starten
        networkManager.start();
        getLogger().at(Level.INFO).log("[OK] NetworkManager gestartet");
        
        // BlockState Codecs initialisieren (lädt statische Blöcke)
        initializeBlockStateCodecs();
        getLogger().at(Level.INFO).log("[OK] BlockState Codecs initialisiert");

        // Commands registrieren
        registerCommands();
        getLogger().at(Level.INFO).log("[OK] Commands registriert");
        
        // Event-Listener registrieren
        registerEventListeners();
        getLogger().at(Level.INFO).log("[OK] Event-Listener registriert");
        
        // Wartungs-Tasks planen
        scheduleMaintenanceTasks();
        getLogger().at(Level.INFO).log("[OK] Wartungs-Tasks geplant");
        
        getLogger().at(Level.INFO).log("=================================================");
        getLogger().at(Level.INFO).log("      HytaleAE2 erfolgreich gestartet!          ");
        getLogger().at(Level.INFO).log("=================================================");
    }

    /**
     * Initialisiert Thread Pool für async Operationen
     */
    private void initializeThreadPool() {
        this.threadPool = Executors.newScheduledThreadPool(4, r -> {
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
    
    public static MEPlugin getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MEPlugin not yet initialized");
        }
        return instance;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public HytaleLogger getPluginLogger() {
        return getLogger();
    }

    public ScheduledExecutorService getThreadPool() {
        return threadPool;
    }
}
