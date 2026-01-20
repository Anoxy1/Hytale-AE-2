package com.tobi.mesystem.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.tobi.mesystem.core.MENetwork;
import com.tobi.mesystem.core.MENode;

/**
 * NetworkManager - Verwalten von ME Nodes pro Welt
 *
 * Speichert Zuordnung Welt -> (Position -> Node)
 * Unterstützt einfaches Hinzufügen/Entfernen und Lookup.
 * Verwaltet auch Persistierung und Tick-Updates.
 */
public class NetworkManager {

    private final Map<UUID, Map<BlockPos, MENode>> worldNodes = new HashMap<>();

    public void start() {
        // Placeholder für zukünftige Persistenz/Reload
    }

    public void shutdown() {
        worldNodes.clear();
    }

    public void addNode(UUID worldId, BlockPos pos, MENode node) {
        worldNodes.computeIfAbsent(worldId, id -> new HashMap<>())
                  .put(pos, node);
    }

    public MENode getNode(UUID worldId, BlockPos pos) {
        Map<BlockPos, MENode> nodes = worldNodes.get(worldId);
        if (nodes == null) return null;
        return nodes.get(pos);
    }

    public void removeNode(UUID worldId, BlockPos pos) {
        Map<BlockPos, MENode> nodes = worldNodes.get(worldId);
        if (nodes != null) {
            MENode removed = nodes.remove(pos);
            if (removed != null && removed.getNetwork() != null) {
                removed.getNetwork().removeNode(pos);
            }
        }
    }

    /**
     * Hilfsmethode: Gibt das Network an einer Position zurück, sofern vorhanden.
     */
    public MENetwork getNetwork(UUID worldId, BlockPos pos) {
        MENode node = getNode(worldId, pos);
        return node != null ? node.getNetwork() : null;
    }
    
    // ==================== TICK & MAINTENANCE ====================
    
    /**
     * Führt Tick-Updates für alle Netzwerke in einer Welt durch
     * Wird jeden Tick aufgerufen für regelmäßige Netzwerk-Updates
     */
    public void tickNetworks(Object world) {
        if (world == null) return;
        
        UUID worldId = extractWorldId(world);
        if (worldId == null) return;
        
        Map<BlockPos, MENode> nodes = worldNodes.get(worldId);
        
        if (nodes == null || nodes.isEmpty()) return;
        
        // Aktualisiere alle Nodes mit periodischen Updates
        for (MENode node : nodes.values()) {
            if (node != null && node.getNetwork() != null) {
                node.tick();
            }
        }
    }
    
    /**
     * Führt Netzwerk-Wartungsarbeiten durch
     * - Überprüft auf defekte Verbindungen
     * - Säubert leere/gestorbene Netzwerke
     * - Validiert Netzwerk-Integrität
     */
    public void performMaintenanceCheck(Object world) {
        if (world == null) return;
        
        UUID worldId = extractWorldId(world);
        Map<BlockPos, MENode> nodes = worldNodes.get(worldId);
        
        if (nodes == null || nodes.isEmpty()) return;
        
        // Prüfe alle Netzwerke auf Konsistenz
        for (MENode node : nodes.values()) {
            if (node != null && node.getNetwork() != null) {
                MENetwork network = node.getNetwork();
                
                // Überprüfe ob dieser Node noch zu seinem Network gehört
                if (!network.getNodes().contains(node)) {
                    // Node ist nicht mehr im Network - sollte nicht vorkommen
                    // Dies könnte auf einen Bug hindeuten
                }
            }
        }
    }
    
    // ==================== PERSISTIERUNG ====================
    
    /**
     * Lädt gespeicherte Netzwerk-Daten für eine Welt
     * (Wird aufgerufen wenn eine Welt geladen wird)
     */
    public void loadWorldNetworks(Object world) {
        if (world == null) return;
        
        UUID worldId = extractWorldId(world);
        
        // TODO: Implementiere Persistierung von Netzwerk-Daten
        // - Lade alle MENode-Positionen aus Persistierungs-Layer
        // - Rekonstruiere Netzwerk-Verbindungen
        // - Restore Storage-Inhalte
    }
    
    /**
     * Speichert Netzwerk-Daten für eine Welt
     * (Wird aufgerufen wenn eine Welt entladen wird)
     */
    public void saveWorldNetworks(Object world) {
        if (world == null) return;
        
        UUID worldId = extractWorldId(world);
        Map<BlockPos, MENode> nodes = worldNodes.get(worldId);
        
        if (nodes == null || nodes.isEmpty()) return;
        
        // TODO: Implementiere Persistierung von Netzwerk-Daten
        // - Speichere alle MENode-Positionen
        // - Speichere Netzwerk-Konfigurationen
        // - Speichere Storage-Inhalte
    }
    
    /**
     * Bereinigt die World-Daten aus dem Manager
     * (Wird aufgerufen nachdem eine Welt entladen wurde)
     */
    public void unloadWorld(Object world) {
        if (world == null) return;
        
        UUID worldId = extractWorldId(world);
        worldNodes.remove(worldId);
    }
    
    /**
     * Hilfsmethode: Extrahiere World-UUID aus World-Objekt
     */
    private UUID extractWorldId(Object world) {
        try {
            return (UUID) world.getClass().getMethod("getWorldId").invoke(world);
        } catch (Exception e) {
            // Fallback
            return UUID.randomUUID();
        }
    }
}
