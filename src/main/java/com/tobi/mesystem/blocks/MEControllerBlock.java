package com.tobi.mesystem.blocks;

import java.util.UUID;

import com.tobi.mesystem.MEPlugin;
import com.tobi.mesystem.core.MEDeviceType;
import com.tobi.mesystem.core.MENetwork;
import com.tobi.mesystem.core.MENode;
import com.tobi.mesystem.util.BlockPos;
import com.tobi.mesystem.util.Direction;

/**
 * ME Controller Block - Network Controller
 */
public class MEControllerBlock {
    
    public MEControllerBlock() {
    }
    
    // ==================== STATIC HYTALE EVENT WRAPPERS ====================
    
    /**
     * Statischer Wrapper für Hytale PlaceBlockEvent
     */
    public static void onPlaced(BlockPos position, Object world) {
        try {
            if (world == null) return;
            
            UUID worldId = extractWorldId(world);
            new MEControllerBlock().onPlaced(worldId, position);
            
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().error("Fehler beim Platzieren von ME Controller", e);
        }
    }

    /**
     * Statischer Wrapper für Hytale BreakBlockEvent
     */
    public static void onBroken(BlockPos position, Object world) {
        try {
            if (world == null) return;
            
            UUID worldId = extractWorldId(world);
            new MEControllerBlock().onBroken(worldId, position);
            
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().error("Fehler beim Zerstören von ME Controller", e);
        }
    }

    /**
     * Statischer Wrapper für Hytale UseBlockEvent
     */
    public static void onRightClick(BlockPos position, Object world) {
        try {
            if (world == null) return;
            
            UUID worldId = extractWorldId(world);
            new MEControllerBlock().onRightClick(worldId, position);
            
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().error("Fehler beim Interagieren mit ME Controller", e);
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
        
        // ME Node erstellen
        MENode node = new MENode(worldId, position, MEDeviceType.CONTROLLER);
        
        // Network finden oder erstellen
        MENetwork network = findOrCreateNetwork(worldId, position, node);
        
        // Prüfe ob Network bereits einen Controller hat
        if (network.hasController()) {
            // TODO: Block wieder entfernen oder nicht platzieren
            return;
        }
        
        // Controller zum Network hinzufügen
        network.addController(position);
        
        // Zu benachbarten Nodes verbinden
        connectToNeighbors(worldId, position, node);
        
        // Im NetworkManager registrieren
        MEPlugin.getInstance().getNetworkManager().addNode(worldId, position, node);
    }
    
    public void onBroken(UUID worldId, BlockPos position) {
        
        // Node aus Network entfernen
        MENode node = MEPlugin.getInstance().getNetworkManager().getNode(worldId, position);
        if (node != null && node.getNetwork() != null) {
            MENetwork network = node.getNetwork();
            
            // Controller entfernen (setzt Channels zurück auf 8)
            network.removeController();
            network.removeNode(position);
        }
        
        // Node aus Manager entfernen
        MEPlugin.getInstance().getNetworkManager().removeNode(worldId, position);
    }
    
    public void onRightClick(UUID worldId, BlockPos position) {
        MENode node = MEPlugin.getInstance().getNetworkManager().getNode(worldId, position);
        
        if (node != null && node.getNetwork() != null) {
            // TODO: display controller info
        }
    }
    
    /**
     * Findet existierendes Network oder erstellt neues
     */
    private MENetwork findOrCreateNetwork(UUID worldId, BlockPos position, MENode node) {
        
        // Prüfe benachbarte Blocks auf existierende Networks
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = position.offset(dir);
            MENode neighborNode = MEPlugin.getInstance().getNetworkManager().getNode(worldId, neighborPos);
            
            if (neighborNode != null && neighborNode.getNetwork() != null) {
                // Zu existierendem Network hinzufügen
                MENetwork network = neighborNode.getNetwork();
                network.addNode(node);
                return network;
            }
        }
        
        // Kein existierendes Network gefunden - neues erstellen
        MENetwork newNetwork = new MENetwork();
        newNetwork.addNode(node);
        return newNetwork;
    }
    
    /**
     * Verbindet zu allen benachbarten ME Devices
     */
    private void connectToNeighbors(UUID worldId, BlockPos position, MENode node) {
        
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = position.offset(dir);
            MENode neighborNode = MEPlugin.getInstance().getNetworkManager().getNode(worldId, neighborPos);
            
            if (neighborNode != null) {
                // Connection hinzufügen
                node.addConnection(dir);
                neighborNode.addConnection(dir.getOpposite());
                
                // Networks mergen, falls unterschiedlich
                if (node.getNetwork() != neighborNode.getNetwork()) {
                    MENetwork network1 = node.getNetwork();
                    MENetwork network2 = neighborNode.getNetwork();
                    
                    // Prüfe ob beide Networks Controller haben
                    if (network1.hasController() && network2.hasController()) {
                        MEPlugin.getInstance().getPluginLogger().warn(
                            "Warnung: Versuch zwei Networks mit Controllern zu mergen!"
                        );
                        // TODO: Merge verhindern oder einen Controller entfernen
                        return;
                    }
                    
                    mergeNetworks(network1, network2);
                }
            }
        }
    }
    
    /**
     * Merged zwei Networks
     */
    private void mergeNetworks(MENetwork network1, MENetwork network2) {
        if (network1 == network2) return;
        MENetwork smaller = network1.size() < network2.size() ? network1 : network2;
        MENetwork larger = network1.size() < network2.size() ? network2 : network1;
        for (MENode node : smaller.getNodes()) {
            larger.addNode(node);
        }
    }
}
