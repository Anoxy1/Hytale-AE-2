package com.tobi.mesystem.blocks;

import java.util.UUID;
import java.util.logging.Level;

import com.hypixel.hytale.logger.HytaleLogger;
import com.tobi.mesystem.MEPlugin;
import com.tobi.mesystem.core.MEDeviceType;
import com.tobi.mesystem.core.MENetwork;
import com.tobi.mesystem.core.MENode;
import com.tobi.mesystem.util.BlockPos;
import com.tobi.mesystem.util.Direction;
import com.tobi.mesystem.util.NetworkManager;

/**
 * Basis-Klasse für alle ME Blocks
 * 
 * Gemeinsame Funktionalität:
 * - Network Finding/Creation
 * - Neighbor Connection
 * - Network Merging
 * - World-ID Extraction
 * - Error Handling
 * 
 * Best Practices:
 * - DRY (Don't Repeat Yourself)
 * - Defensive Programming
 * - Proper Error Logging
 */
public abstract class MEBlockBase {
    
    protected final HytaleLogger logger;
    
    protected MEBlockBase() {
        this.logger = MEPlugin.getInstance().getPluginLogger();
    }
    
    // === Abstract Methods (müssen von Subklassen implementiert werden) ===
    
    /**
     * Gibt den Device-Type dieses Blocks zurück
     */
    protected abstract MEDeviceType getDeviceType();
    
    /**
     * Wird aufgerufen nach dem Platzieren (optional override)
     */
    protected void onPlacedExtra(UUID worldId, BlockPos position, MENode node, MENetwork network) {
        // Optional override in Subklassen
    }
    
    /**
     * Wird aufgerufen vor dem Entfernen (optional override)
     */
    protected void onBrokenExtra(UUID worldId, BlockPos position, MENode node, MENetwork network) {
        // Optional override in Subklassen
    }
    
    /**
     * Wird aufgerufen bei Right-Click (optional override)
     */
    protected void onRightClickExtra(UUID worldId, BlockPos position, MENode node, MENetwork network) {
        // Optional override in Subklassen
    }
    
    // === Common Implementation ===
    
    /**
     * Standard-Implementierung für Block-Platzierung - Based on Hytale Best Practices
     */
    public void onPlaced(UUID worldId, BlockPos position) {
        onPlaced(worldId, position, null);
    }
    
    /**
     * Erweiterte onPlaced mit World-Referenz
     */
    public void onPlaced(UUID worldId, BlockPos position, Object world) {
        try {
            if (worldId == null || position == null) {
                logger.at(Level.WARNING).log("onPlaced: worldId oder position ist null");
                return;
            }
            
            logger.at(Level.FINE).log("onPlaced: Starte für %s bei %s", getDeviceType(), position);
            
            // 1. Node erstellen
            MENode node = new MENode(worldId, position, getDeviceType());
            
            // 1.5. World-Referenz setzen (für Container-Zugriff)
            if (world != null) {
                node.setWorld(world);
            }
            
            // 2. Network finden oder erstellen
            MENetwork network = findOrCreateNetwork(worldId, position, node);
            
            // 3. Device registrieren (allociert Channels)
            network.registerDevice(position, getDeviceType());
            
            // 4. Zu Nachbarn verbinden
            connectToNeighbors(worldId, position, node);
            
            // 5. Im NetworkManager registrieren
            getNetworkManager().addNode(worldId, position, node);
            
            // 6. Extra Logic (Subklassen)
            onPlacedExtra(worldId, position, node, network);

            logger.at(Level.INFO).log("%s platziert bei %s | Network: %s Nodes, %s/%s Channels",
                    getDeviceType(), position, network.size(),
                    network.getUsedChannels(), network.getMaxChannels());

        } catch (Exception e) {
            logger.at(Level.SEVERE).withCause(e).log("Fehler beim Platzieren von %s bei %s", getDeviceType(), position);
        }
    }
    
    /**
     * Standard-Implementierung für Block-Entfernung - Based on Hytale Best Practices
     */
    public void onBroken(UUID worldId, BlockPos position) {
        try {
            if (worldId == null || position == null) {
                logger.at(Level.WARNING).log("onBroken: worldId oder position ist null");
                return;
            }
            
            logger.at(Level.FINE).log("onBroken: Starte für %s bei %s", getDeviceType(), position);
            
            // 1. Node holen
            MENode node = getNetworkManager().getNode(worldId, position);
            if (node == null) {
                logger.at(Level.WARNING).log("Node nicht gefunden bei %s", position);
                return;
            }
            
            MENetwork network = node.getNetwork();
            
            // 2. Extra Logic vor Entfernung (Subklassen)
            if (network != null) {
                onBrokenExtra(worldId, position, node, network);
                
                // 3. Device unregistrieren
                network.unregisterDevice(position);
                
                // 4. Node aus Network entfernen
                network.removeNode(position);
            } else {
                logger.at(Level.FINE).log("onBroken: Kein Network gefunden für Node bei %s", position);
            }
            
            // 5. Node aus Manager entfernen
            getNetworkManager().removeNode(worldId, position);

            logger.at(Level.INFO).log("%s entfernt bei %s", getDeviceType(), position);

        } catch (Exception e) {
            logger.at(Level.SEVERE).withCause(e).log("Fehler beim Entfernen von %s bei %s", getDeviceType(), position);
        }
    }
    
    /**
     * Standard-Implementierung für Right-Click - Based on Hytale Best Practices
     */
    public void onRightClick(UUID worldId, BlockPos position) {
        try {
            if (worldId == null || position == null) {
                logger.at(Level.FINE).log("onRightClick: worldId oder position ist null");
                return;
            }
            
            logger.at(Level.FINE).log("onRightClick: Starte für %s bei %s", getDeviceType(), position);
            
            MENode node = getNetworkManager().getNode(worldId, position);
            if (node == null) {
                logger.at(Level.FINE).log("👆 Kein Node gefunden bei %s", position);
                return;
            }
            
            if (node.getNetwork() == null) {
                logger.at(Level.FINE).log("👆 Kein Network gefunden bei %s", position);
                return;
            }

            logger.at(Level.FINE).log("onRightClick verarbeitet");
            
            // Extra Logic (Subklassen)
            onRightClickExtra(worldId, position, node, node.getNetwork());

        } catch (Exception e) {
            logger.at(Level.SEVERE).withCause(e).log("Fehler beim Interagieren mit %s", getDeviceType());
        }
    }
    
    // === Protected Helper Methods ===
    
    /**
     * Findet existierendes Network oder erstellt neues
     */
    protected MENetwork findOrCreateNetwork(UUID worldId, BlockPos position, MENode node) {
        // Prüfe benachbarte Blocks auf existierende Networks
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = position.offset(dir);
            MENode neighborNode = getNetworkManager().getNode(worldId, neighborPos);
            
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
    protected void connectToNeighbors(UUID worldId, BlockPos position, MENode node) {
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = position.offset(dir);
            MENode neighborNode = getNetworkManager().getNode(worldId, neighborPos);
            
            if (neighborNode != null) {
                // Connection hinzufügen
                node.addConnection(dir);
                neighborNode.addConnection(dir.getOpposite());
                
                // Networks mergen, falls unterschiedlich
                if (node.getNetwork() != null && 
                    neighborNode.getNetwork() != null &&
                    node.getNetwork() != neighborNode.getNetwork()) {
                    node.getNetwork().merge(neighborNode.getNetwork());
                }
            }
        }
    }
    
    /**
     * Extrahiert World-UUID aus World-Objekt
     */
    protected static UUID extractWorldId(Object world) {
        try {
            return (UUID) world.getClass().getMethod("getWorldId").invoke(world);
        } catch (ReflectiveOperationException e) {
            MEPlugin.getInstance().getPluginLogger().at(Level.WARNING).log("Konnte World-UUID nicht extrahieren: %s", e.getMessage());
            return new UUID(world.hashCode(), 0); // Fallback: nutze hashCode
        }
    }
    
    /**
     * Gibt den NetworkManager zurück
     */
    protected NetworkManager getNetworkManager() {
        return MEPlugin.getInstance().getNetworkManager();
    }
}
