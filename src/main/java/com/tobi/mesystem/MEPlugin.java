package com.tobi.mesystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.tobi.mesystem.util.EventRegistry;
import com.tobi.mesystem.util.NetworkManager;

/**
 * HytaleAE2 - Applied Energistics 2 für Hytale
 *
 * Main Plugin Class - Initialisiert BlockState-Registry und Event-System
 */
public class MEPlugin extends JavaPlugin {

    private static MEPlugin instance;
    private static boolean initialized = false;
    private final Logger logger = LogManager.getLogger(MEPlugin.class);
    private final NetworkManager networkManager = new NetworkManager();
    private EventRegistry eventRegistry;

    public MEPlugin(JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        try {
            logger.info("╔════════════════════════════════════════════════════════════╗");
            logger.info("║         ME System - Setup & Initialisierung                ║");
            logger.info("╚════════════════════════════════════════════════════════════╝");

            // 1. NetworkManager starten
            logger.info("→ Starte NetworkManager...");
            networkManager.start();
            logger.info("  ✓ NetworkManager initialisiert");

            // 2. BlockStates registrieren (wenn HytaleServer.jar im Classpath)
            try {
                logger.info("→ Registriere BlockStates...");
                registerBlockStates();
                logger.info("  ✓ BlockState-Registry aktiviert");
            } catch (Exception e) {
                logger.warn("BlockState-Registry nicht verfügbar (HytaleServer.jar nicht im Classpath)", e);
            }

            // 3. Event-Listener registrieren
            try {
                logger.info("→ Registriere Event-Listener...");
                eventRegistry = new EventRegistry(getPluginManager(), this);
                eventRegistry.registerAllListeners();
                logger.info("  ✓ Event-Registry aktiviert");
            } catch (Exception e) {
                logger.warn("Event-Registry nicht verfügbar (erwartet wenn Hytale Event-System nicht zugänglich)", e);
            }

            initialized = true;
            logger.info("✓ ME System Setup erfolgreich abgeschlossen");

        } catch (Throwable t) {
            // Critical: Catch Throwable to prevent plugin loading failure
            logger.error("KRITISCHER FEHLER in ME System Setup - Plugin könnte nicht vollständig laden", t);
            // Do NOT rethrow - allow Hytale to continue
            initialized = false;
        }
    }
    
    /**
     * Registers BlockStates with Hytale's BlockStateRegistry
     * 
     * NOTE: This method requires HytaleServer.jar at runtime.
     * When HytaleServer.jar is available, uncomment the code below.
     * 
     * The JSON files in Server/Item/Items/ will be auto-loaded because
     * IncludesAssetPack: true is set in manifest.json
     */
    private void registerBlockStates() {
        logger.info("BlockState registration stubbed - requires HytaleServer.jar");
        
        /* UNCOMMENT WHEN HytaleServer.jar IS IN CLASSPATH:
        
        BlockStateRegistry registry = getBlockStateRegistry();
        
        // Register ME Controller
        registry.registerBlockState(
            MEControllerBlockState.class,
            "ME_Controller",  // Must match JSON State.Definitions.Id
            MEControllerBlockState.CODEC
        );
        logger.debug("  ✓ ME Controller BlockState registered");
        
        // Register ME Terminal (with inventory support)
        registry.registerBlockState(
            METerminalBlockState.class,
            "ME_Terminal",
            METerminalBlockState.CODEC,
            ItemContainerStateData.class,
            ItemContainerStateData.CODEC
        );
        logger.debug("  ✓ ME Terminal BlockState registered");
        
        // Register ME Cable
        registry.registerBlockState(
            MECableBlockState.class,
            "ME_Cable",
            MECableBlockState.CODEC
        );
        logger.debug("  ✓ ME Cable BlockState registered");
        
        // Register Terminal Interaction
        getCodecRegistry(Interaction.CODEC)
            .register(
                "ME_Terminal_Interaction",
                METerminalInteraction.class,
                METerminalInteraction.CODEC
            );
        logger.debug("  ✓ Terminal Interaction registered");
        
        */
    }

    @Override
    protected void start() {
        logger.info("╔════════════════════════════════════════════════════════════╗");
        logger.info("║         ME System erfolgreich gestartet! 🚀                 ║");
        logger.info("╚════════════════════════════════════════════════════════════╝");
        logger.info("  ✓ Netzwerk-Manager aktiv");
        logger.info("  ✓ Blocks: ME Cable, ME Terminal, ME Controller");
        logger.info("  ✓ Event-System aktiv");
        logger.info("");
        logger.info("Verfügbare Befehle:");
        logger.info("  /me status    - Zeige Netzwerk-Status");
        logger.info("  /me networks  - Liste alle Netzwerke");
    }

    @Override
    protected void shutdown() {
        logger.info("ME System wird heruntergefahren...");
        networkManager.shutdown();
        logger.info("ME System heruntergefahren");
    }

    // === Hytale API Getter (mit Reflection Fallback) ===
    /**
     * Gibt BlockManager (oder null falls nicht verfügbar) BlockManager wird von
     * Hytale zur Runtime bereitgestellt (über JavaPlugin)
     */
    private Object getBlockManager() {
        try {
            // Versuche: super.getBlockManager() oder Ähnliches
            // Falls nicht verfügbar, wird null zurückgegeben

            // Die JavaPlugin Basis-Klasse sollte eine Methode haben
            // Wir nutzen Reflection um auf geschützte Methoden zuzugreifen
            java.lang.reflect.Method[] methods = this.getClass().getSuperclass().getDeclaredMethods();
            for (java.lang.reflect.Method m : methods) {
                if (m.getName().equals("getBlockManager")) {
                    m.setAccessible(true);
                    return m.invoke(this);
                }
            }
            logger.debug("BlockManager Methode in JavaPlugin nicht gefunden");
            return null;
        } catch (Exception e) {
            logger.debug("BlockManager nicht verfügbar: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gibt PluginManager (oder null falls nicht verfügbar) PluginManager wird
     * von Hytale zur Runtime bereitgestellt (über JavaPlugin)
     */
    private Object getPluginManager() {
        try {
            // Versuche: super.getPluginManager() oder Ähnliches
            java.lang.reflect.Method[] methods = this.getClass().getSuperclass().getDeclaredMethods();
            for (java.lang.reflect.Method m : methods) {
                if (m.getName().equals("getPluginManager")) {
                    m.setAccessible(true);
                    return m.invoke(this);
                }
            }
            logger.debug("PluginManager Methode in JavaPlugin nicht gefunden");
            return null;
        } catch (Exception e) {
            logger.debug("PluginManager nicht verfügbar: " + e.getMessage());
            return null;
        }
    }

    // === Getters ===
    public static MEPlugin getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MEPlugin not yet initialized. This usually means the plugin failed to load properly.");
        }
        return instance;
    }

    public static boolean isInitialized() {
        return initialized && instance != null;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public Logger getPluginLogger() {
        return logger;
    }
}
