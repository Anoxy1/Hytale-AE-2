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
            logger.at(Level.WARNING).log("Network hat bereits einen Controller! Platzierung bei %s wird blockiert", position);
            // Entferne den Node wieder aus dem Netzwerk
            network.removeNode(position);
            MEPlugin.getInstance().getNetworkManager().removeNode(worldId, position);
            logger.at(Level.INFO).log("Doppel-Controller bei %s blockiert und entfernt", position);
            return;
        }

        // Controller zum Network hinzufügen (erhöht Channels auf 32)
        network.addController(position);
        logger.at(Level.INFO).log("Controller platziert bei %s | Channels: %s", position, network.getMaxChannels());
    }
    
    @Override
    protected void onBrokenExtra(UUID worldId, BlockPos position, MENode node, MENetwork network) {
        // Controller entfernen (reduziert Channels auf 8)
        network.removeController();
        logger.at(Level.INFO).log("Controller entfernt bei %s | Channels reduziert auf %s", position, network.getMaxChannels());
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
