package com.tobi.mesystem.blocks;

import java.util.UUID;
import java.util.logging.Level;

import com.tobi.mesystem.MEPlugin;
import com.tobi.mesystem.core.MEDeviceType;
import com.tobi.mesystem.core.MENetwork;
import com.tobi.mesystem.core.MENode;
import com.tobi.mesystem.util.BlockPos;

/**
 * ME Terminal Block - Zugriff auf Netzwerk Storage
 * 
 * Simplified mit MEBlockBase - gemeinsame Logik in Basisklasse
 */
public class METerminalBlock extends MEBlockBase {

    @Override
    protected MEDeviceType getDeviceType() {
        return MEDeviceType.TERMINAL;
    }
    
    @Override
    protected void onRightClickExtra(UUID worldId, BlockPos position, MENode node, MENetwork network) {
        // GUI wird implementiert sobald Hytale GUI-API verfügbar ist
        // Aktuell: Zeige Network-Status im Chat
        logger.at(Level.INFO).log(
            "Terminal angeklickt bei %s | Network: %s Items, %s/%s Channels, Storage: %s/%s",
            position,
            network.getItemTypeCount(),
            network.getUsedChannels(),
            network.getMaxChannels(),
            network.getStoredItemCount(),
            network.getTotalStorageCapacity()
        );
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
            MEPlugin.getInstance().getPluginLogger().at(Level.FINE).log("onPlaced: METerminalBlock bei %s", position);
            new METerminalBlock().onPlaced(worldId, position, world);
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().at(Level.SEVERE).withCause(e).log("Fehler beim Platzieren von ME Terminal");
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
            MEPlugin.getInstance().getPluginLogger().at(Level.FINE).log("onBroken: METerminalBlock bei %s", position);
            new METerminalBlock().onBroken(worldId, position);
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().at(Level.SEVERE).withCause(e).log("Fehler beim Zerstören von ME Terminal");
        }
    }

    public static boolean onRightClick(BlockPos position, Object world, Object player) {
        try {
            if (position == null) {
                MEPlugin.getInstance().getPluginLogger().at(Level.WARNING).log("onRightClick: BlockPos ist null");
                return false;
            }
            if (world == null) {
                MEPlugin.getInstance().getPluginLogger().at(Level.WARNING).log("onRightClick: World ist null");
                return false;
            }
            if (player == null) {
                MEPlugin.getInstance().getPluginLogger().at(Level.WARNING).log("onRightClick: Player ist null");
                return false;
            }
            
            UUID worldId = extractWorldId(world);
            MEPlugin.getInstance().getPluginLogger().at(Level.FINE).log("onRightClick: METerminalBlock bei %s", position);
            new METerminalBlock().onRightClick(worldId, position);
            return true; // Event wurde verarbeitet
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().at(Level.SEVERE).withCause(e).log("Fehler beim Interagieren mit ME Terminal");
            return false;
        }
    }
}
