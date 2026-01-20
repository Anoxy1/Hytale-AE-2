package com.tobi.mesystem.blocks;

import java.util.UUID;

import com.tobi.mesystem.MEPlugin;
import com.tobi.mesystem.core.MEDeviceType;
import com.tobi.mesystem.core.MENetwork;
import com.tobi.mesystem.core.MENode;
import com.tobi.mesystem.util.BlockPos;
import com.tobi.mesystem.util.Direction;

/**
 * ME Cable Block - Basis-Kabel für ME Networks
 */
public class MECableBlock {
    
    public MECableBlock() {
    }
    
    // ==================== STATIC HYTALE EVENT WRAPPERS ====================
    
    /**
     * Statischer Wrapper für Hytale PlaceBlockEvent
     * @param position BlockPos des platzierten Blocks
     * @param world World-Objekt (Object, da Hytale-API nicht kompilierbar)
     */
    public static void onPlaced(BlockPos position, Object world) {
        try {
            if (world == null) return;
            
            // Extrahiere World-UUID über Reflection wenn nötig
            UUID worldId = extractWorldId(world);
            new MECableBlock().onPlaced(worldId, position);
            
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().error("Fehler beim Platzieren von ME Cable", e);
        }
    }
    
    /**
     * Statischer Wrapper für Hytale BreakBlockEvent
     */
    public static void onBroken(BlockPos position, Object world) {
        try {
            if (world == null) return;
            
            UUID worldId = extractWorldId(world);
            new MECableBlock().onBroken(worldId, position);
            
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().error("Fehler beim Zerstören von ME Cable", e);
        }
    }
    
    /**
     * Statischer Wrapper für Hytale UseBlockEvent
     */
    public static void onRightClick(BlockPos position, Object world) {
        try {
            if (world == null) return;
            
            UUID worldId = extractWorldId(world);
            new MECableBlock().onRightClick(worldId, position);
            
        } catch (Exception e) {
            MEPlugin.getInstance().getPluginLogger().error("Fehler beim Interagieren mit ME Cable", e);
        }
    }
    
    /**
     * Hilfsmethode: Extrahiere World-UUID aus World-Objekt
     */
    private static UUID extractWorldId(Object world) {
        try {
            // Versuche getWorldId() zu rufen
            return (UUID) world.getClass().getMethod("getWorldId").invoke(world);
        } catch (ReflectiveOperationException e) {
            // Fallback: Nutze UUID.randomUUID() als Default
            MEPlugin.getInstance().getPluginLogger().warn("Konnte World-UUID nicht extrahieren", e);
            return UUID.randomUUID();
        }
    }
    
    // ==================== INSTANCE METHODS ====================
    
    public void onPlaced(UUID worldId, BlockPos position) {
        
        // ME Node erstellen
        MENode node = new MENode(worldId, position, MEDeviceType.CABLE);
        
        // Network finden oder erstellen
        findOrCreateNetwork(worldId, position, node);
        
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
            network.removeNode(position);
            
            MEPlugin.getInstance().getPluginLogger().info(
                "ME Cable entfernt bei " + position + " | Network: " + network.size() + " Nodes"
            );
            
            // Network neu aufbauen (split check)
            checkNetworkSplit(worldId, position);
        }
        
        // Node aus Manager entfernen
        MEPlugin.getInstance().getNetworkManager().removeNode(worldId, position);
    }
    
    public void onRightClick(UUID worldId, BlockPos position) {
        MENode node = MEPlugin.getInstance().getNetworkManager().getNode(worldId, position);
        
        if (node != null && node.getNetwork() != null) {
            // TODO: display info
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
                    mergeNetworks(node.getNetwork(), neighborNode.getNetwork());
                }
            }
        }
    }
    
    /**
     * Merged zwei Networks
     */
    private void mergeNetworks(MENetwork network1, MENetwork network2) {
        if (network1 == network2) return;
        
        // Kleineres Network in größeres mergen
        MENetwork smaller = network1.size() < network2.size() ? network1 : network2;
        MENetwork larger = network1.size() < network2.size() ? network2 : network1;
        
        for (MENode node : smaller.getNodes()) {
            larger.addNode(node);
        }
        
        MEPlugin.getInstance().getPluginLogger().info(
            "Networks gemerged: " + smaller.size() + " + " + larger.size() + " = " + larger.size()
        );
    }
    
    /**
     * Prüft ob Network gesplittet wurde
     */
    private void checkNetworkSplit(UUID worldId, BlockPos removedPosition) {
        // TODO: Implementiere Netwk
    }}