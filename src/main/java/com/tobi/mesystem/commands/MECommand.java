package com.tobi.mesystem.commands;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import com.hypixel.hytale.logger.HytaleLogger;
import com.tobi.mesystem.MEPlugin;
import com.tobi.mesystem.core.MENetwork;
import com.tobi.mesystem.util.NetworkManager;

/**
 * ME System Command Handler
 *
 * Handles /me commands:
 * - /me status - Show network statistics
 * - /me networks - List all networks
 * - /me help - Show command help
 */
public class MECommand {

    private final HytaleLogger logger;
    private final NetworkManager networkManager;

    public MECommand() {
        this.logger = MEPlugin.getInstance().getPluginLogger();
        this.networkManager = MEPlugin.getInstance().getNetworkManager();
    }

    /**
     * Haupteinstiegspunkt für /me Commands - Based on Hytale Best Practices
     */
    public boolean onCommand(Object sender, String[] args) {
        try {
            if (sender == null) {
                logger.at(Level.WARNING).log("/me command: sender ist null");
                return false;
            }
            
            logger.at(Level.FINE).log("/me command ausgeführt von %s", sender.getClass().getSimpleName());
            
            // Keine Argumente -> help
            if (args == null || args.length == 0) {
                logger.at(Level.FINE).log("Keine Argumente - zeige Help");
                return showHelp(sender);
            }

            String subCommand = args[0].toLowerCase();
            logger.at(Level.FINE).log("Sub-Command: %s", subCommand);

            switch (subCommand) {
                case "status":
                    logger.at(Level.FINE).log("status command");
                    return handleStatus(sender);

                case "networks":
                    logger.at(Level.FINE).log("networks command");
                    return handleNetworks(sender);

                case "help":
                    logger.at(Level.FINE).log("help command");
                    return showHelp(sender);

                default:
                    logger.at(Level.FINE).log("Unbekannter Sub-Command: %s", subCommand);
                    sendMessage(sender, "§cUnbekannter Befehl: " + subCommand);
                    sendMessage(sender, "§7Nutze '/me help' für Hilfe");
                    return false;
            }

        } catch (Exception e) {
            logger.at(Level.SEVERE).withCause(e).log("Fehler beim Verarbeiten von /me command");
            try {
                sendMessage(sender, "§cEin Fehler ist aufgetreten beim Ausführen des Befehls");
            } catch (Exception sendError) {
                logger.at(Level.SEVERE).withCause(sendError).log("Konnte Fehler-Nachricht nicht senden");
            }
            return false;
        }
    }

    /**
     * /me status - Zeigt Netzwerk-Statistiken
     */
    private boolean handleStatus(Object sender) {
        String debugInfo = networkManager.getDebugInfo();

        sendMessage(sender, "§6=== ME System Status ===");
        sendMessage(sender, "§7" + debugInfo);
        sendMessage(sender, "§aSystem läuft normal");

        return true;
    }

    /**
     * /me networks - Listet alle Netzwerke auf
     */
    private boolean handleNetworks(Object sender) {
        sendMessage(sender, "§6=== ME Netzwerke ===");

        Map<UUID, Map<UUID, MENetwork>> allNetworks = networkManager.getAllNetworks();
        
        if (allNetworks.isEmpty()) {
            sendMessage(sender, "§7Keine Netzwerke gefunden");
            return true;
        }
        
        int totalNetworks = 0;
        for (Map.Entry<UUID, Map<UUID, MENetwork>> worldEntry : allNetworks.entrySet()) {
            UUID worldId = worldEntry.getKey();
            Map<UUID, MENetwork> worldNetworks = worldEntry.getValue();
            
            if (!worldNetworks.isEmpty()) {
                sendMessage(sender, String.format("§eWelt: §7%s", worldId.toString().substring(0, 8)));
                
                for (Map.Entry<UUID, MENetwork> netEntry : worldNetworks.entrySet()) {
                    MENetwork network = netEntry.getValue();
                    totalNetworks++;
                    
                    sendMessage(sender, String.format(
                        "  §7Network #%d: §f%s Nodes, %s/%s Channels, %s Items",
                        totalNetworks,
                        network.getNodeCount(),
                        network.getUsedChannels(),
                        network.getMaxChannels(),
                        network.getStoredItemCount()
                    ));
                }
            }
        }
        
        sendMessage(sender, String.format("§6Total: §f%s Netzwerke", totalNetworks));
        sendMessage(sender, "§7Nutze §e/me status§7 für Details");

        return true;
    }

    /**
     * /me help - Zeigt verfügbare Befehle
     */
    private boolean showHelp(Object sender) {
        sendMessage(sender, "§6=== ME System Befehle ===");
        sendMessage(sender, "§e/me status§7 - Zeige Netzwerk-Status");
        sendMessage(sender, "§e/me networks§7 - Liste alle Netzwerke");
        sendMessage(sender, "§e/me help§7 - Zeige diese Hilfe");

        return true;
    }

    /**
     * Sendet eine Nachricht an einen Command-Sender via Reflection
     */
    private void sendMessage(Object sender, String message) {
        try {
            // sender.sendMessage(message)
            sender.getClass()
                .getMethod("sendMessage", String.class)
                .invoke(sender, message);
        } catch (Exception e) {
            logger.at(Level.WARNING).log("Konnte Nachricht nicht senden: %s", e.getMessage());
        }
    }
}
