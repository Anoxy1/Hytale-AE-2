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
 * Provides diagnostic and testing commands for the ME system.
 * 
 * <p><b>Usage:</b>
 * <ul>
 *   <li>/medebug network - Show network info at player location</li>
 *   <li>/medebug clear - Clear all networks (admin only)</li>
 *   <li>/medebug help - Show command help</li>
 * </ul>
 * 
 * <p><b>Permissions:</b>
 * <ul>
 *   <li>hytaleae2.command.debug - Access to /medebug command</li>
 *   <li>hytaleae2.command.debug.clear - Access to clear subcommand</li>
 * </ul>
 * 
 * @author Anoxy1
 * @version 0.1.0
 * @since 0.1.0
 * @see AbstractPlayerCommand
 * @see NetworkManager
 */
public class MEDebugCommand extends AbstractPlayerCommand {
    
    private static final String PERMISSION_BASE = "hytaleae2.command.debug";
    private static final String PERMISSION_CLEAR = "hytaleae2.command.debug.clear";
    
    private final NetworkManager networkManager;

    /**
     * Constructs the ME Debug command.
     * 
     * @param name the command name
     * @param description the command description
     * @param requiresConfirmation whether command requires confirmation
     */
    public MEDebugCommand(@Nonnull String name, @Nonnull String description, boolean requiresConfirmation) {
        super(name, description, requiresConfirmation);
        this.networkManager = MEPlugin.getInstance().getNetworkManager();
    }

    /**
     * Executes the command.
     * 
     * Follows HelloPlugin pattern with permission checks and argument parsing.
     * 
     * @param context the command context
     * @param store the entity store
     * @param ref the entity reference
     * @param playerRef the player executing the command
     * @param world the world where command is executed
     */
    @Override
    protected void execute(
            @Nonnull CommandContext context,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
    ) {
        // Permission check (based on Britakee Studios Best Practices)
        if (!hasPermission(playerRef, PERMISSION_BASE)) {
            sendMessage(playerRef, "[ERROR] No permission for this command");
            return;
        }
        
        // Get command arguments
        // Note: CommandContext doesn't expose args directly in current API
        // For now, show network info by default
        // TODO: Parse args when API provides access
        
        handleNetworkInfo(playerRef, world);
    }
    
    /**
     * Checks if player has permission.
     * 
     * @param playerRef the player to check
     * @param permission the permission node
     * @return true if player has permission or is OP
     */
    private boolean hasPermission(PlayerRef playerRef, String permission) {
        try {
            // Try to check permission via reflection
            Boolean hasPerm = (Boolean) playerRef.getClass()
                .getMethod("hasPermission", String.class)
                .invoke(playerRef, permission);
            return hasPerm != null && hasPerm;
        } catch (Exception e) {
            // Fallback: Check if player is OP
            try {
                Boolean isOp = (Boolean) playerRef.getClass()
                    .getMethod("isOp")
                    .invoke(playerRef);
                return isOp != null && isOp;
            } catch (Exception e2) {
                // If both fail, allow by default (dev mode)
                MEPlugin.getInstance().getPluginLogger().at(Level.FINE)
                    .log("Permission check failed, allowing command: %s", e2.getMessage());
                return true;
            }
        }
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
