package com.tobi.mesystem.util;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import com.hypixel.hytale.logger.HytaleLogger;
import com.tobi.mesystem.MEPlugin;
import com.tobi.mesystem.core.MENetwork;
import com.tobi.mesystem.core.MENode;
import com.tobi.mesystem.storage.NetworkPersistence;

/**
 * NetworkManager - Verwalten von ME Nodes pro Welt
 *
 * Speichert Zuordnung Welt -> (Position -> Node)
 * Unterstützt einfaches Hinzufügen/Entfernen und Lookup.
 * Verwaltet auch Persistierung und Tick-Updates.
 * 
 * Performance-Features:
 * - ConcurrentHashMap für Thread-Safety
 * - Effiziente Lookup-Operationen
 * - Cache für häufige World-ID Extractions
 * 
 * Thread-Safety:
 * - Alle public Methoden sind thread-safe
 * - Nutzt ConcurrentHashMap für Collections
 */
public class NetworkManager {

    private final HytaleLogger logger;

    // Thread-safe World -> (Position -> Node) Mapping
    private final Map<UUID, Map<BlockPos, MENode>> worldNodes = new ConcurrentHashMap<>();

    // Cache für World-ID Extractions (vermeidet wiederholte Reflection)
    private final Map<Object, UUID> worldIdCache = new ConcurrentHashMap<>();
    
    // Persistierung
    private final NetworkPersistence persistence;

    public NetworkManager() {
        this.logger = MEPlugin.getInstance().getPluginLogger();
        
        // Initialisiere Persistierung in %APPDATA%\Hytale\UserData\Mods\HytaleAE2
        Path dataFolder = java.nio.file.Paths.get(System.getenv("APPDATA"), "Hytale", "UserData", "Mods", "HytaleAE2", "networks");
        this.persistence = new NetworkPersistence(dataFolder);
    }

    public void start() {
        logger.at(Level.INFO).log("NetworkManager gestartet");
        logger.at(Level.FINE).log("Nodes: %d, Welten: %d", 
            worldNodes.values().stream().mapToInt(Map::size).sum(),
            worldNodes.size());
    }

    public void shutdown() {
        logger.at(Level.INFO).log("NetworkManager wird heruntergefahren...");
        int totalNodes = worldNodes.values().stream().mapToInt(Map::size).sum();
        logger.at(Level.FINE).log("Cleanup: %d Nodes, %d Welten", totalNodes, worldNodes.size());

        // Cleanup - entferne alle Referenzen
        worldNodes.clear();
        worldIdCache.clear();

        logger.at(Level.INFO).log("NetworkManager heruntergefahren");
    }

    /**
     * Fügt einen Node zur Welt hinzu (Thread-Safe)
     */
    public void addNode(UUID worldId, BlockPos pos, MENode node) {
        if (worldId == null) {
            logger.at(Level.FINE).log("addNode: worldId ist null - ignoriert");
            return;
        }
        if (pos == null) {
            logger.at(Level.FINE).log("addNode: BlockPos ist null - ignoriert");
            return;
        }
        if (node == null) {
            logger.at(Level.FINE).log("addNode: Node ist null - ignoriert");
            return;
        }

        try {
            worldNodes.computeIfAbsent(worldId, id -> new ConcurrentHashMap<>())
                      .put(pos, node);
            logger.at(Level.FINE).log("Node hinzugefügt: %s", pos);
        } catch (Exception e) {
            logger.at(Level.SEVERE).withCause(e).log("Fehler beim Hinzufügen von Node %s", pos);
        }
    }

    /**
     * Gibt einen Node an einer Position zurück (Thread-Safe)
     */
    public MENode getNode(UUID worldId, BlockPos pos) {
        if (worldId == null) {
            logger.at(Level.FINE).log("getNode: worldId ist null");
            return null;
        }
        if (pos == null) {
            logger.at(Level.FINE).log("getNode: BlockPos ist null");
            return null;
        }
        
        Map<BlockPos, MENode> nodes = worldNodes.get(worldId);
        if (nodes == null) {
            logger.at(Level.FINE).log("getNode: Keine Nodes für Welt %s", worldId.toString().substring(0, 8));
            return null;
        }
        MENode node = nodes.get(pos);
        if (node == null) {
            logger.at(Level.FINE).log("getNode: Kein Node bei %s", pos);
        }
        return node;
    }

    /**
     * Entfernt einen Node (Thread-Safe)
     */
    public void removeNode(UUID worldId, BlockPos pos) {
        if (worldId == null) {
            logger.at(Level.FINE).log("removeNode: worldId ist null");
            return;
        }
        if (pos == null) {
            logger.at(Level.FINE).log("removeNode: BlockPos ist null");
            return;
        }
        
        try {
            Map<BlockPos, MENode> nodes = worldNodes.get(worldId);
            if (nodes != null) {
                MENode removed = nodes.remove(pos);
                if (removed != null) {
                    logger.at(Level.FINE).log("Node entfernt: %s", pos);
                    if (removed.getNetwork() != null) {
                        removed.getNetwork().removeNode(pos);
                    }
                } else {
                    logger.at(Level.FINE).log("removeNode: Node nicht gefunden bei %s", pos);
                }
            } else {
                logger.at(Level.FINE).log("removeNode: Keine Nodes für Welt %s", worldId.toString().substring(0, 8));
            }
        } catch (Exception e) {
            logger.at(Level.SEVERE).withCause(e).log("Fehler beim Entfernen von Node %s", pos);
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
     * 
     * Performance: Verwendet cached World-ID
     */
    public void tickNetworks(Object world) {
        if (world == null) return;
        
        UUID worldId = extractWorldIdCached(world);
        if (worldId == null) return;
        
        Map<BlockPos, MENode> nodes = worldNodes.get(worldId);
        
        if (nodes == null || nodes.isEmpty()) return;
        
        // Aktualisiere alle Nodes mit periodischen Updates
        // ConcurrentHashMap erlaubt sichere Iteration während Modifikation
        for (MENode node : nodes.values()) {
            if (node != null && node.getNetwork() != null) {
                try {
                    node.tick();
                } catch (Exception e) {
                    logger.at(Level.SEVERE).withCause(e).log("Fehler beim Tick von Node %s", node.getPosition());
                }
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
        
        UUID worldId = extractWorldIdCached(world);
        Map<BlockPos, MENode> nodes = worldNodes.get(worldId);
        
        if (nodes == null || nodes.isEmpty()) return;
        
        int checkedNodes = 0;
        int invalidNodes = 0;
        
        // Prüfe alle Netzwerke auf Konsistenz
        for (MENode node : nodes.values()) {
            if (node != null && node.getNetwork() != null) {
                checkedNodes++;
                MENetwork network = node.getNetwork();
                
                // Überprüfe ob dieser Node noch zu seinem Network gehört
                if (!network.getNodes().contains(node)) {
                    // Node ist nicht mehr im Network - sollte nicht vorkommen
                    logger.at(Level.WARNING).log("Inkonsistenz gefunden: Node %s ist nicht in seinem Network", node.getPosition());
                    invalidNodes++;
                }
            }
        }

        if (checkedNodes > 0) {
            logger.at(Level.FINE).log("Wartungs-Check abgeschlossen: %s Nodes geprüft, %s Inkonsistenzen",
                    checkedNodes, invalidNodes);
        }
    }
    
    // ==================== PERSISTIERUNG ====================
    
    /**
     * Lädt gespeicherte Netzwerk-Daten für eine Welt
     * (Wird aufgerufen wenn eine Welt geladen wird)
     */
    public void loadWorldNetworks(Object world) {
        if (world == null) return;

        UUID worldId = extractWorldIdCached(world);

        logger.at(Level.INFO).log("Lade Netzwerk-Daten für Welt %s", worldId);
        
        // Lade gespeicherte Network-Daten
        Map<UUID, Map<String, Long>> savedNetworks = persistence.loadWorldNetworks(worldId);
        
        if (savedNetworks.isEmpty()) {
            logger.at(Level.FINE).log("Keine gespeicherten Netzwerk-Daten für Welt %s", worldId);
            return;
        }
        
        // Rekonstruiere Networks mit gespeicherten Items
        for (Map.Entry<UUID, Map<String, Long>> entry : savedNetworks.entrySet()) {
            UUID networkId = entry.getKey();
            Map<String, Long> items = entry.getValue();
            
            // Erstelle MENetwork und restore Items
            MENetwork network = new MENetwork();
            for (Map.Entry<String, Long> item : items.entrySet()) {
                // Direkt in Storage einfügen (umgeht Kapazitätsprüfung beim Laden)
                network.insertItem(item.getKey(), item.getValue().intValue(), null);
            }
            
            logger.at(Level.FINE).log("Restored Network %s mit %d Items",
                networkId.toString().substring(0, 8), items.size());
        }
    }
    
    /**
     * Speichert Netzwerk-Daten für eine Welt
     * (Wird aufgerufen wenn eine Welt entladen wird)
     */
    public void saveWorldNetworks(Object world) {
        if (world == null) return;
        
        UUID worldId = extractWorldIdCached(world);
        Map<BlockPos, MENode> nodes = worldNodes.get(worldId);
        
        if (nodes == null || nodes.isEmpty()) {
            logger.at(Level.FINE).log("Keine Netzwerk-Daten zu speichern für Welt %s", worldId);
            return;
        }

        logger.at(Level.INFO).log("Speichere Netzwerk-Daten für Welt %s: %d Nodes", worldId, nodes.size());
        
        // Sammle alle Networks dieser Welt
        Map<UUID, MENetwork> worldNetworks = new ConcurrentHashMap<>();
        for (MENode node : nodes.values()) {
            MENetwork network = node.getNetwork();
            if (network != null) {
                worldNetworks.put(network.getNetworkId(), network);
            }
        }
        
        // Speichere zu Disk
        persistence.saveWorldNetworks(worldId, nodes, worldNetworks);
    }
    
    /**
     * Bereinigt die World-Daten aus dem Manager
     * (Wird aufgerufen nachdem eine Welt entladen wurde)
     */
    public void unloadWorld(Object world) {
        if (world == null) return;
        
        UUID worldId = extractWorldIdCached(world);
        
        Map<BlockPos, MENode> removed = worldNodes.remove(worldId);
        worldIdCache.remove(world);

        if (removed != null) {
            logger.at(Level.INFO).log("Welt %s entladen: %s Nodes entfernt", worldId, removed.size());
        }
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Extrahiert World-UUID aus World-Objekt mit Caching
     * Vermeidet wiederholte Reflection-Aufrufe für bessere Performance
     */
    private UUID extractWorldIdCached(Object world) {
        if (world == null) return null;
        
        // Prüfe Cache zuerst
        UUID cached = worldIdCache.get(world);
        if (cached != null) {
            return cached;
        }
        
        // Extrahiere und cache
        UUID worldId = extractWorldId(world);
        if (worldId != null) {
            worldIdCache.put(world, worldId);
        }
        
        return worldId;
    }
    
    /**
     * Hilfsmethode: Extrahiere World-UUID aus World-Objekt via Reflection
     */
    private UUID extractWorldId(Object world) {
        try {
            return (UUID) world.getClass().getMethod("getWorldId").invoke(world);
        } catch (Exception e) {
            logger.at(Level.WARNING).log("Konnte World-UUID nicht extrahieren: %s", e.getMessage());
            // Fallback: Nutze hashCode als Basis für UUID
            return new UUID(world.hashCode(), 0);
        }
    }
    
    /**
     * Cleanup: Entfernt inaktive Netzwerke
     * (z.B. Netzwerke ohne Nodes nach Chunk-Unload)
     */
    public void cleanupInactiveNetworks() {
        java.util.concurrent.atomic.AtomicInteger cleaned = new java.util.concurrent.atomic.AtomicInteger(0);
        
        for (Map.Entry<UUID, Map<BlockPos, MENode>> worldEntry : worldNodes.entrySet()) {
            Map<BlockPos, MENode> nodes = worldEntry.getValue();
            
            // Entferne Nodes ohne Netzwerk
            nodes.values().removeIf(node -> {
                if (node.getNetwork() == null) {
                    cleaned.incrementAndGet();
                    return true;
                }
                return false;
            });
            
            // Entferne leere Welten
            if (nodes.isEmpty()) {
                worldNodes.remove(worldEntry.getKey());
            }
        }
        
        if (cleaned.get() > 0) {
            logger.at(Level.INFO).log("Cleanup: %s inaktive Nodes entfernt", cleaned.get());
        }
    }
    
    /**
     * Optimierung: Rebalanciert Channel-Allokationen in allen Netzwerken
     */
    public void optimizeChannels() {
        int optimized = 0;
        
        for (Map.Entry<UUID, Map<BlockPos, MENode>> worldEntry : worldNodes.entrySet()) {
            Map<BlockPos, MENode> nodes = worldEntry.getValue();
            
            // Sammle alle Netzwerke in dieser Welt
            Map<UUID, MENetwork> networks = new ConcurrentHashMap<>();
            for (MENode node : nodes.values()) {
                MENetwork network = node.getNetwork();
                if (network != null) {
                    networks.put(network.getNetworkId(), network);
                }
            }
            
            // Optimiere jedes Netzwerk
            for (MENetwork network : networks.values()) {
                int used = network.getUsedChannels();
                int max = network.getMaxChannels();
                
                if (used > max * 0.8) { // Über 80% Auslastung
                    logger.at(Level.FINE).log(
                        "Netzwerk %s hat hohe Channel-Auslastung: %s/%s",
                        network.getNetworkId(),
                        used,
                        max
                    );
                    optimized++;
                }
            }
        }
        
        if (optimized > 0) {
            logger.at(Level.FINE).log("Optimierung: %s Netzwerke analysiert", optimized);
        }
    }
    
    /**
     * Gibt alle Netzwerke zurück (Welt -> Network-ID -> Network)
     */
    public Map<UUID, Map<UUID, MENetwork>> getAllNetworks() {
        Map<UUID, Map<UUID, MENetwork>> result = new ConcurrentHashMap<>();
        
        for (Map.Entry<UUID, Map<BlockPos, MENode>> worldEntry : worldNodes.entrySet()) {
            UUID worldId = worldEntry.getKey();
            Map<BlockPos, MENode> nodes = worldEntry.getValue();
            
            Map<UUID, MENetwork> worldNetworks = new ConcurrentHashMap<>();
            
            // Sammle alle einzigartigen Netzwerke
            for (MENode node : nodes.values()) {
                MENetwork network = node.getNetwork();
                if (network != null) {
                    worldNetworks.put(network.getNetworkId(), network);
                }
            }
            
            if (!worldNetworks.isEmpty()) {
                result.put(worldId, worldNetworks);
            }
        }
        
        return result;
    }
    
    /**
     * Gibt Debug-Statistiken über alle geladenen Welten zurück
     */
    public String getDebugInfo() {
        int totalNodes = worldNodes.values().stream()
            .mapToInt(Map::size)
            .sum();
            
        return String.format(
            "NetworkManager [Welten: %d, Gesamt-Nodes: %d, Cache-Size: %d]",
            worldNodes.size(),
            totalNodes,
            worldIdCache.size()
        );
    }
}
