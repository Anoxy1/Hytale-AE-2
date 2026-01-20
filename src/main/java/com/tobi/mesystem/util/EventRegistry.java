package com.tobi.mesystem.util;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tobi.mesystem.events.HytaleBlockEventListenerStub;

/**
 * EventRegistry - Registriert Event-Listener bei Hytale Nutzt Reflection um
 * Event-Handler zu registrieren
 */
public class EventRegistry {

    private static final Logger logger = LogManager.getLogger(EventRegistry.class);
    private final Object pluginManager;
    private final Object plugin;

    public EventRegistry(Object pluginManager, Object plugin) {
        this.pluginManager = pluginManager;
        this.plugin = plugin;
    }

    /**
     * Registriert alle Event-Listener
     *
     * Hytale auto-discovers @EventHandler methods in the listener object. We
     * register the listener object once, and Hytale's reflection scans for all
     * @EventHandler decorated methods.
     */
    public void registerAllListeners() {
        logger.info("=== ME System Event-Registrierung ===");

        if (pluginManager == null || plugin == null) {
            logger.warn("PluginManager oder Plugin ist null - Event-Registrierung übersprungen");
            return;
        }

        try {
            HytaleBlockEventListenerStub listener = new HytaleBlockEventListenerStub();

            // Register listener object once - Hytale auto-discovers @EventHandler methods
            registerListenerObject(listener);

            logger.info("✓ Alle Event-Listener registriert (Hytale auto-discovery aktiv)");

        } catch (Exception e) {
            logger.error("Fehler beim Registrieren der Event-Listener", e);
        }
    }

    /**
     * Registriert den Listener-Objekt einmalig
     *
     * Hytale's PluginManager scans for @EventHandler methods on the listener.
     * Method signature: pluginManager.registerListener(JavaPlugin plugin,
     * Object listener)
     */
    private void registerListenerObject(HytaleBlockEventListenerStub listener) throws Exception {
        if (pluginManager == null || plugin == null) {
            logger.debug("PluginManager oder Plugin null");
            return;
        }

        try {
            // Call: pluginManager.registerListener(this, listener);
            // The pluginManager will scan listener for @EventHandler methods

            Method registerMethod = pluginManager.getClass().getMethod(
                    "registerListener",
                    Object.class, // plugin instance (JavaPlugin)
                    Object.class // listener instance (contains @EventHandler methods)
            );

            registerMethod.invoke(pluginManager, plugin, listener);
            logger.debug("  ✓ Event-Listener-Objekt registriert (auto-discovery aktiv)");

        } catch (NoSuchMethodException e) {
            logger.warn("PluginManager.registerListener() nicht gefunden - versuche alternatives API");
            tryAlternativeRegistration(listener);
        }
    }

    /**
     * Fallback: Versuche alternatives Event-Registrierungs-API wenn
     * registerListener nicht verfügbar
     */
    private void tryAlternativeRegistration(HytaleBlockEventListenerStub listener) throws Exception {
        try {
            // Try alternate method names
            Method registerMethod = pluginManager.getClass().getMethod(
                    "register",
                    Object.class, // plugin
                    Object.class // listener
            );

            registerMethod.invoke(pluginManager, plugin, listener);
            logger.debug("  ✓ Event-Listener über alternatives API registriert");

        } catch (NoSuchMethodException e2) {
            logger.warn("Kein Standard-Registrierungs-API in PluginManager gefunden");
            logger.warn("@EventHandler Annotationen müssen manuell verarbeitet werden oder Hytale auto-discovers sie");
        }
    }
}
