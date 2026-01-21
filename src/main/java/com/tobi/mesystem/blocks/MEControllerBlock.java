package com.tobi.mesystem.blocks;

import java.util.UUID;
import java.util.logging.Level;

import com.tobi.mesystem.MEPlugin;
import com.tobi.mesystem.core.MEDeviceType;
import com.tobi.mesystem.core.MENetwork;
import com.tobi.mesystem.core.MENode;
import com.tobi.mesystem.util.BlockPos;

/**
 * ME Controller Block - Network Controller
 * 
 * Besonderheiten:
 * - Erhöht Channel-Limit auf 32
 * - Pro Network nur ein Controller erlaubt
 * - Entfernung reduziert Channels auf 8
 * 
 * Simplified mit MEBlockBase - gemeinsame Logik in Basisklasse
 */
public class MEControllerBlock extends MEBlockBase {
    
    @Override
    protected MEDeviceType getDeviceType() {
        return MEDeviceType.CONTROLLER;
    }
    
    @Override
    protected void onPlacedExtra(UUID worldId, BlockPos position, MENode node, MENetwork network) {
        // Prüfe ob Network bereits einen Controller hat
        if (network.hasController()) {
            logger.at(Level.WARNING).log(
                "[BLOCKED] Network already has a controller! Placement at %s denied",
                position
            );
            logger.at(Level.WARNING).log(
                "[INFO] Only 1 controller per network allowed. Remove existing controller first."
            );
            
            // Entferne den Node wieder aus dem Netzwerk
            network.removeNode(position);
            MEPlugin.getInstance().getNetworkManager().removeNode(worldId, position);
            logger.at(Level.INFO).log("Duplicate controller at %s removed", position);
            return;
        }

        // Controller zum Network hinzufügen (erhöht Channels auf 32)
        network.addController(position);
        
        logger.at(Level.INFO).log("========================================");
        logger.at(Level.INFO).log("[CONTROLLER ACTIVATED]");
        logger.at(Level.INFO).log("Position: %s", position);
        logger.at(Level.INFO).log("Channel Upgrade: 8 -> %d", network.getMaxChannels());
        logger.at(Level.INFO).log("Network Size: %d nodes", network.size());
        logger.at(Level.INFO).log("Used Channels: %d/%d", network.getUsedChannels(), network.getMaxChannels());
        logger.at(Level.INFO).log("Available Channels: %d", network.getMaxChannels() - network.getUsedChannels());
        logger.at(Level.INFO).log("========================================");
        
        // Notify all connected devices about channel upgrade
        notifyChannelUpgrade(network);
    }
    
    @Override
    protected void onBrokenExtra(UUID worldId, BlockPos position, MENode node, MENetwork network) {
        // Controller entfernen (reduziert Channels auf 8)
        network.removeController();
        
        logger.at(Level.WARNING).log("========================================");
        logger.at(Level.WARNING).log("[CONTROLLER REMOVED]");
        logger.at(Level.WARNING).log("Position: %s", position);
        logger.at(Level.WARNING).log("Channel Downgrade: 32 -> %d", network.getMaxChannels());
        logger.at(Level.WARNING).log("Network Size: %d nodes", network.size());
        logger.at(Level.WARNING).log("Used Channels: %d/%d", network.getUsedChannels(), network.getMaxChannels());
        
        if (network.getUsedChannels() > network.getMaxChannels()) {
            logger.at(Level.WARNING).log(
                "[WARNING] Channel overflow! %d channels in use, only %d available",
                network.getUsedChannels(), network.getMaxChannels()
            );
            logger.at(Level.WARNING).log("Some devices may stop working until controller is replaced.");
        }
        
        logger.at(Level.WARNING).log("========================================");
        
        // Notify all connected devices about channel downgrade
        notifyChannelDowngrade(network);
    }
    
    /**
     * Notifies network about channel upgrade.
     * Future: Reactivate offline devices that were waiting for channels.
     */
    private void notifyChannelUpgrade(MENetwork network) {
        logger.at(Level.FINE).log(
            "Notifying %d nodes about channel upgrade",
            network.size()
        );
        // Future: Iterate through nodes and reactivate waiting devices
    }
    
    /**
     * Notifies network about channel downgrade.
     * Future: Deactivate devices if channels exceed limit.
     */
    private void notifyChannelDowngrade(MENetwork network) {
        logger.at(Level.FINE).log(
            "Notifying %d nodes about channel downgrade",
            network.size()
        );
        // Future: Prioritize devices and disable lowest priority if needed
    }
    
    @Override
    protected void onRightClickExtra(UUID worldId, BlockPos position, MENode node, MENetwork network) {
        logger.at(Level.FINE).log("Controller Status: %s Nodes, %s/%s Channels, %s Item-Typen",
            network.size(), network.getUsedChannels(),
            network.getMaxChannels(), network.getItemTypeCount());
    }

    // ==================== STATIC HYTALE EVENT WRAPPERS ====================
    
    public static void onPlaced(BlockPos position, Object world) {
        try {
            if (position == null) {
                MEPlugin.getInstance().getPluginLogger().at(Level.WARNING).log("onPlaced: BlockPos ist null");
                return;
            }
            if (world == null) {
                MEPlugin.getInstance().getPluginLogger().at(Level.WARNING).log("onPlaced: World ist null");
                return;
            }
            
            UUID worldId = extractWorldId(world);
            MEPlugin.getInstance().getPluginLogger().at(Level.FINE).log("onPlaced: MEControllerBlock bei %s", position);
            new MEControllerBlock().onPlaced(worldId, position, world);
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().at(Level.SEVERE).withCause(e).log("Fehler beim Platzieren von ME Controller");
        }
    }

    public static void onBroken(BlockPos position, Object world) {
        try {
            if (position == null) {
                MEPlugin.getInstance().getPluginLogger().at(Level.WARNING).log("onBroken: BlockPos ist null");
                return;
            }
            if (world == null) {
                MEPlugin.getInstance().getPluginLogger().at(Level.WARNING).log("onBroken: World ist null");
                return;
            }
            
            UUID worldId = extractWorldId(world);
            MEPlugin.getInstance().getPluginLogger().at(Level.FINE).log("onBroken: MEControllerBlock bei %s", position);
            new MEControllerBlock().onBroken(worldId, position);
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().at(Level.SEVERE).withCause(e).log("Fehler beim Zerstören von ME Controller");
        }
    }

    public static void onRightClick(BlockPos position, Object world) {
        try {
            if (position == null) {
                MEPlugin.getInstance().getPluginLogger().at(Level.WARNING).log("onRightClick: BlockPos ist null");
                return;
            }
            if (world == null) {
                MEPlugin.getInstance().getPluginLogger().at(Level.WARNING).log("onRightClick: World ist null");
                return;
            }
            
            UUID worldId = extractWorldId(world);
            MEPlugin.getInstance().getPluginLogger().at(Level.FINE).log("onRightClick: MEControllerBlock bei %s", position);
            new MEControllerBlock().onRightClick(worldId, position);
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().at(Level.SEVERE).withCause(e).log("Fehler beim Interagieren mit ME Controller");
        }
    }
}
