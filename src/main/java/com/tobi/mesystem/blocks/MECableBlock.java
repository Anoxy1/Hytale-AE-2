package com.tobi.mesystem.blocks;

import java.util.UUID;
import java.util.logging.Level;

import com.tobi.mesystem.MEPlugin;
import com.tobi.mesystem.core.MEDeviceType;
import com.tobi.mesystem.core.MENetwork;
import com.tobi.mesystem.core.MENode;
import com.tobi.mesystem.util.BlockPos;

/**
 * ME Cable Block - Basis-Kabel für ME Networks
 * 
 * Features:
 * - Automatic 6-directional connection to neighbors
 * - Network merging when cables connect
 * - Visual connection state (future: texture rotation)
 * 
 * Simplified mit MEBlockBase - gemeinsame Logik in Basisklasse
 */
public class MECableBlock extends MEBlockBase {
    
    @Override
    protected MEDeviceType getDeviceType() {
        return MEDeviceType.CABLE;
    }
    
    @Override
    protected void onPlacedExtra(UUID worldId, BlockPos position, MENode node, MENetwork network) {
        // Log successful connections
        int connectionCount = node.getConnections().size();
        logger.at(Level.INFO).log(
            "Cable at %s connected to %d neighbors | Network: %d nodes, %d/%d channels",
            position, connectionCount, network.size(), 
            network.getUsedChannels(), network.getMaxChannels()
        );
        
        // Future: Update cable texture based on connection directions
        // updateCableTexture(worldId, position, node.getConnections());
    }
    
    @Override
    protected void onBrokenExtra(UUID worldId, BlockPos position, MENode node, MENetwork network) {
        // When cable breaks, network may split
        logger.at(Level.INFO).log(
            "Cable removed at %s | Network may split",
            position
        );
        
        // Disconnect from neighbors
        for (com.tobi.mesystem.util.Direction dir : node.getConnections()) {
            BlockPos neighborPos = position.offset(dir);
            MENode neighborNode = getNetworkManager().getNode(worldId, neighborPos);
            if (neighborNode != null) {
                neighborNode.removeConnection(dir.getOpposite());
            }
        }
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
