package com.tobi.mesystem.blocks;

import java.util.UUID;

import com.tobi.mesystem.MEPlugin;
import com.tobi.mesystem.core.MEDeviceType;
import com.tobi.mesystem.core.MENetwork;
import com.tobi.mesystem.core.MENode;
import com.tobi.mesystem.util.BlockPos;
import com.tobi.mesystem.util.Direction;

/**
 * ME Terminal Block - Zugriff auf Netzwerk Storage
 */
public class METerminalBlock {

    public METerminalBlock() {
    }

    // ==================== STATIC HYTALE EVENT WRAPPERS ====================
    
    /**
     * Statischer Wrapper für Hytale PlaceBlockEvent
     */
    public static void onPlaced(BlockPos position, Object world) {
        try {
            if (world == null) return;
            
            UUID worldId = extractWorldId(world);
            new METerminalBlock().onPlaced(worldId, position);
            
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().error("Fehler beim Platzieren von ME Terminal", e);
        }
    }

    /**
     * Statischer Wrapper für Hytale BreakBlockEvent
     */
    public static void onBroken(BlockPos position, Object world) {
        try {
            if (world == null) return;
            
            UUID worldId = extractWorldId(world);
            new METerminalBlock().onBroken(worldId, position);
            
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().error("Fehler beim Zerstören von ME Terminal", e);
        }
    }

    /**
     * Statischer Wrapper für Hytale UseBlockEvent
     * Öffnet das Terminal-GUI
     * 
     * @return true wenn das Event verarbeitet wurde (Block-Interaction canceln)
     */
    public static boolean onRightClick(BlockPos position, Object world, Object player) {
        try {
            if (world == null || player == null) return false;
            
            UUID worldId = extractWorldId(world);
            METerminalBlock terminal = new METerminalBlock();
            terminal.onRightClick(worldId, position);
            
            // TODO: GUI-Öffnung implementieren wenn Terminal-GUI ready ist
            // player.openGui(new METerminalGui(position, network));
            
            return true; // Event wurde verarbeitet
            
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().error("Fehler beim Interagieren mit ME Terminal", e);
            return false;
        }
    }
    
    /**
     * Hilfsmethode: Extrahiere World-UUID aus World-Objekt
     */
    private static UUID extractWorldId(Object world) {
        try {
            return (UUID) world.getClass().getMethod("getWorldId").invoke(world);
        } catch (ReflectiveOperationException e) {
            MEPlugin.getInstance().getPluginLogger().warn("Konnte World-UUID nicht extrahieren", e);
            return UUID.randomUUID();
        }
    }

    // ==================== INSTANCE METHODS ====================
    
    public void onPlaced(UUID worldId, BlockPos position) {

        MENode node = new MENode(worldId, position, MEDeviceType.TERMINAL);
        MENetwork network = findOrCreateNetwork(worldId, position, node);

        // Device registrieren (reserviert Channels nach Bedarf)
        network.registerDevice(position, MEDeviceType.TERMINAL);

        // Nachbarn verbinden
        connectToNeighbors(worldId, position, node);

        // Manager registrieren
        MEPlugin.getInstance().getNetworkManager().addNode(worldId, position, node);
    }

    public void onBroken(UUID worldId, BlockPos position) {
        MENode node = MEPlugin.getInstance().getNetworkManager().getNode(worldId, position);
        if (node != null && node.getNetwork() != null) {
            MENetwork network = node.getNetwork();
            network.unregisterDevice(position);
            network.removeNode(position);
        }
        MEPlugin.getInstance().getNetworkManager().removeNode(worldId, position);
    }

    public void onRightClick(UUID worldId, BlockPos position) {
        MENode node = MEPlugin.getInstance().getNetworkManager().getNode(worldId, position);
        if (node != null && node.getNetwork() != null) {
            // TODO: display terminal info
        }
    }

    private MENetwork findOrCreateNetwork(UUID worldId, BlockPos position, MENode node) {
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = position.offset(dir);
            MENode neighborNode = MEPlugin.getInstance().getNetworkManager().getNode(worldId, neighborPos);
            if (neighborNode != null && neighborNode.getNetwork() != null) {
                MENetwork network = neighborNode.getNetwork();
                network.addNode(node);
                return network;
            }
        }
        MENetwork newNetwork = new MENetwork();
        newNetwork.addNode(node);
        return newNetwork;
    }

    private void connectToNeighbors(UUID worldId, BlockPos position, MENode node) {
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = position.offset(dir);
            MENode neighborNode = MEPlugin.getInstance().getNetworkManager().getNode(worldId, neighborPos);
            if (neighborNode != null) {
                node.addConnection(dir);
                neighborNode.addConnection(dir.getOpposite());
                if (node.getNetwork() != neighborNode.getNetwork()) {
                    node.getNetwork().merge(neighborNode.getNetwork());
                }
            }
        }
    }
}
