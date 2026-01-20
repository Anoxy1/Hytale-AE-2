package com.tobi.mesystem.commands;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.tobi.mesystem.MEPlugin;
import com.tobi.mesystem.core.MENetwork;
import com.tobi.mesystem.util.NetworkManager;

/**
 * ME Debug Command - Testing & Development Tools
 * 
 * Usage:
 *   /medebug additem <itemId> <amount> - Add item to nearest network
 *   /medebug network - Show network info at player location
 *   /medebug clear - Clear all networks
 */
public class MEDebugCommand extends AbstractPlayerCommand {
    
    private final NetworkManager networkManager;

    public MEDebugCommand(@Nonnull String name, @Nonnull String description, boolean requiresConfirmation) {
        super(name, description, requiresConfirmation);
        this.networkManager = MEPlugin.getInstance().getNetworkManager();
    }

    @Override
    protected void execute(
            @Nonnull CommandContext context,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
    ) {
        // CommandContext hat keine getArguments() - verwende context direkt
        // Für jetzt: Zeige nur Network-Info
        handleNetworkInfo(playerRef, world);
    }
    
    /**
     * /medebug additem <itemId> <amount>
     * Fügt Items zum nächsten Netzwerk hinzu
     */
    private void handleAddItem(String[] args, PlayerRef playerRef, World world) {
        if (args.length < 3) {
            sendMessage(playerRef, "§cUsage: /medebug additem <itemId> <amount>");
            return;
        }
        
        String itemId = args[1];
        int amount;
        
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sendMessage(playerRef, "§cUngültige Anzahl: " + args[2]);
            return;
        }
        
        // Finde nächstes Netzwerk
        MENetwork network = findNearestNetwork(world);
        
        if (network == null) {
            sendMessage(playerRef, "§cKein ME-Netzwerk in der Nähe gefunden!");
            return;
        }
        
        // Füge Items hinzu
        int stored = network.insertItem(itemId, amount, null);
        
        sendMessage(playerRef, String.format(
            "§a[OK] %d x %s zum Netzwerk hinzugefügt",
            stored, itemId
        ));
        sendMessage(playerRef, String.format(
            "§7Network: %d Items, %d/%d Channels",
            network.getStoredItemCount(),
            network.getUsedChannels(),
            network.getMaxChannels()
        ));
    }
    
    /**
     * /medebug network
     * Zeigt Netzwerk-Info an Spieler-Position
     */
    private void handleNetworkInfo(PlayerRef playerRef, World world) {
        Map<UUID, Map<UUID, MENetwork>> allNetworks = networkManager.getAllNetworks();
        
        if (allNetworks.isEmpty()) {
            sendMessage(playerRef, "§7Keine Netzwerke gefunden");
            return;
        }
        
        sendMessage(playerRef, "§6=== ME Debug Info ===");
        
        int totalNetworks = 0;
        int totalNodes = 0;
        long totalItems = 0;
        
        for (Map.Entry<UUID, Map<UUID, MENetwork>> worldEntry : allNetworks.entrySet()) {
            for (MENetwork network : worldEntry.getValue().values()) {
                totalNetworks++;
                totalNodes += network.getNodeCount();
                totalItems += network.getStoredItemCount();
                
                sendMessage(playerRef, String.format(
                    "§eNetwork #%d: §f%d Nodes, %d/%d Channels, %d Items",
                    totalNetworks,
                    network.getNodeCount(),
                    network.getUsedChannels(),
                    network.getMaxChannels(),
                    network.getStoredItemCount()
                ));
                
                // Zeige Items
                Map<String, Long> items = network.getAllItems();
                if (!items.isEmpty()) {
                    sendMessage(playerRef, "§7Items:");
                    for (Map.Entry<String, Long> item : items.entrySet()) {
                        sendMessage(playerRef, String.format(
                            "  §f%d x %s",
                            item.getValue(),
                            item.getKey()
                        ));
                    }
                }
            }
        }
        
        sendMessage(playerRef, String.format(
            "§6Total: §f%d Networks, %d Nodes, %d Items",
            totalNetworks, totalNodes, totalItems
        ));
    }
    
    /**
     * /medebug clear
     * Löscht alle Netzwerke (für Testing)
     */
    private void handleClear(PlayerRef playerRef, World world) {
        networkManager.cleanupInactiveNetworks();
        sendMessage(playerRef, "§a[OK] Netzwerke bereinigt");
    }
    
    /**
     * Zeigt Command-Hilfe
     */
    private void showHelp(PlayerRef playerRef) {
        sendMessage(playerRef, "§6=== ME Debug Commands ===");
        sendMessage(playerRef, "§e/medebug additem <itemId> <amount>§7 - Add item to network");
        sendMessage(playerRef, "§e/medebug network§7 - Show network info");
        sendMessage(playerRef, "§e/medebug clear§7 - Clear all networks");
    }
    
    /**
     * Findet das nächste Netzwerk in der Welt
     */
    private MENetwork findNearestNetwork(World world) {
        try {
            UUID worldId = (UUID) world.getClass().getMethod("getWorldId").invoke(world);
            Map<UUID, Map<UUID, MENetwork>> allNetworks = networkManager.getAllNetworks();
            
            Map<UUID, MENetwork> worldNetworks = allNetworks.get(worldId);
            if (worldNetworks != null && !worldNetworks.isEmpty()) {
                return worldNetworks.values().iterator().next();
            }
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().at(Level.WARNING)
                .log("Fehler beim Finden von Network: %s", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Sendet Nachricht an Spieler
     */
    private void sendMessage(PlayerRef playerRef, String message) {
        try {
            playerRef.getClass()
                .getMethod("sendMessage", Message.class)
                .invoke(playerRef, Message.raw(message));
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().at(Level.WARNING)
                .log("Konnte Nachricht nicht senden: %s", e.getMessage());
        }
    }
}
