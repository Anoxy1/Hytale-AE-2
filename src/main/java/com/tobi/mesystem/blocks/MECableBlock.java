package com.tobi.mesystem.blocks;

import java.util.UUID;
import java.util.logging.Level;

import com.tobi.mesystem.MEPlugin;
import com.tobi.mesystem.core.MEDeviceType;
import com.tobi.mesystem.util.BlockPos;

/**
 * ME Cable Block - Basis-Kabel für ME Networks
 * 
 * Simplified mit MEBlockBase - gemeinsame Logik in Basisklasse
 */
public class MECableBlock extends MEBlockBase {
    
    @Override
    protected MEDeviceType getDeviceType() {
        return MEDeviceType.CABLE;
    }
    
    // ==================== STATIC HYTALE EVENT WRAPPERS ====================
    
    /**
     * Statischer Wrapper für Hytale PlaceBlockEvent - Based on Hytale Best Practices
     */
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
            MEPlugin.getInstance().getPluginLogger().at(Level.FINE).log("onPlaced: MECableBlock bei %s", position);
            new MECableBlock().onPlaced(worldId, position, world);
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().at(Level.SEVERE).withCause(e).log("Fehler beim Platzieren von ME Cable");
        }
    }
    
    /**
     * Statischer Wrapper für Hytale BreakBlockEvent - Based on Hytale Best Practices
     */
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
            MEPlugin.getInstance().getPluginLogger().at(Level.FINE).log("onBroken: MECableBlock bei %s", position);
            new MECableBlock().onBroken(worldId, position);
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().at(Level.SEVERE).withCause(e).log("Fehler beim Zerstören von ME Cable");
        }
    }
    
    /**
     * Statischer Wrapper für Hytale UseBlockEvent - Based on Hytale Best Practices
     */
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
            MEPlugin.getInstance().getPluginLogger().at(Level.FINE).log("onRightClick: MECableBlock bei %s", position);
            new MECableBlock().onRightClick(worldId, position);
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().at(Level.SEVERE).withCause(e).log("Fehler beim Interagieren mit ME Cable");
        }
    }
}
