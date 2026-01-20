package com.tobi.mesystem.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import com.hypixel.hytale.logger.HytaleLogger;
import com.tobi.mesystem.MEPlugin;
import com.tobi.mesystem.core.MENetwork;
import com.tobi.mesystem.core.MENode;
import com.tobi.mesystem.util.BlockPos;

/**
 * Persistiert ME-Netzwerk-Daten zu Disk
 * Speichert als JSON-ähnliches Format (simple key-value)
 */
public class NetworkPersistence {
    
    private final HytaleLogger logger;
    private final Path dataFolder;
    
    public NetworkPersistence(Path dataFolder) {
        this.logger = MEPlugin.getInstance().getPluginLogger();
        this.dataFolder = dataFolder;
        
        try {
            Files.createDirectories(dataFolder);
        } catch (IOException e) {
            logger.at(Level.SEVERE).withCause(e).log("Konnte Data-Folder nicht erstellen");
        }
    }
    
    /**
     * Speichert Netzwerk-Daten für eine Welt
     */
    public void saveWorldNetworks(UUID worldId, Map<BlockPos, MENode> nodes, 
                                   Map<UUID, MENetwork> networks) {
        if (nodes == null || networks == null) return;
        
        Path worldFile = dataFolder.resolve(worldId.toString() + ".dat");
        
        try (BufferedWriter writer = Files.newBufferedWriter(worldFile)) {
            // Header
            writer.write("# HytaleAE2 Network Data v1.0\n");
            writer.write("WorldID=" + worldId + "\n");
            writer.write("Timestamp=" + System.currentTimeMillis() + "\n");
            writer.write("\n");
            
            // Nodes
            writer.write("# Nodes\n");
            writer.write("NodeCount=" + nodes.size() + "\n");
            for (Map.Entry<BlockPos, MENode> entry : nodes.entrySet()) {
                BlockPos pos = entry.getKey();
                MENode node = entry.getValue();
                
                writer.write(String.format("Node=%d,%d,%d|%s\n",
                    pos.getX(), pos.getY(), pos.getZ(),
                    node.getDeviceType().toString()
                ));
            }
            writer.write("\n");
            
            // Networks
            writer.write("# Networks\n");
            writer.write("NetworkCount=" + networks.size() + "\n");
            for (Map.Entry<UUID, MENetwork> entry : networks.entrySet()) {
                UUID networkId = entry.getKey();
                MENetwork network = entry.getValue();
                
                writer.write("Network=" + networkId + "\n");
                writer.write("MaxChannels=" + network.getMaxChannels() + "\n");
                writer.write("HasController=" + network.hasController() + "\n");
                
                // Items
                Map<String, Long> items = network.getAllItems();
                writer.write("ItemCount=" + items.size() + "\n");
                for (Map.Entry<String, Long> item : items.entrySet()) {
                    writer.write(String.format("Item=%s|%d\n", 
                        item.getKey(), item.getValue()));
                }
                writer.write("\n");
            }
            
            logger.at(Level.INFO).log("Gespeichert: %d Nodes, %d Networks für Welt %s",
                nodes.size(), networks.size(), worldId);
                
        } catch (IOException e) {
            logger.at(Level.SEVERE).withCause(e).log("Fehler beim Speichern von Welt %s", worldId);
        }
    }
    
    /**
     * Lädt Netzwerk-Daten für eine Welt
     * @return Map von NetworkID -> gespeicherte Items
     */
    public Map<UUID, Map<String, Long>> loadWorldNetworks(UUID worldId) {
        Path worldFile = dataFolder.resolve(worldId.toString() + ".dat");
        
        if (!Files.exists(worldFile)) {
            logger.at(Level.FINE).log("Keine gespeicherten Daten für Welt %s", worldId);
            return new HashMap<>();
        }
        
        Map<UUID, Map<String, Long>> result = new HashMap<>();
        
        try (BufferedReader reader = Files.newBufferedReader(worldFile)) {
            String line;
            UUID currentNetworkId = null;
            Map<String, Long> currentItems = null;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip comments und empty lines
                if (line.isEmpty() || line.startsWith("#")) continue;
                
                if (line.startsWith("Network=")) {
                    // Neues Network
                    currentNetworkId = UUID.fromString(line.substring(8));
                    currentItems = new HashMap<>();
                    result.put(currentNetworkId, currentItems);
                    
                } else if (line.startsWith("Item=") && currentItems != null) {
                    // Item-Daten
                    String[] parts = line.substring(5).split("\\|");
                    if (parts.length == 2) {
                        String itemId = parts[0];
                        long quantity = Long.parseLong(parts[1]);
                        currentItems.put(itemId, quantity);
                    }
                }
            }
            
            logger.at(Level.INFO).log("Geladen: %d Networks für Welt %s",
                result.size(), worldId);
                
        } catch (IOException e) {
            logger.at(Level.SEVERE).withCause(e).log("Fehler beim Laden von Welt %s", worldId);
        }
        
        return result;
    }
    
    /**
     * Löscht gespeicherte Daten für eine Welt
     */
    public void deleteWorldData(UUID worldId) {
        Path worldFile = dataFolder.resolve(worldId.toString() + ".dat");
        
        try {
            Files.deleteIfExists(worldFile);
            logger.at(Level.FINE).log("Gelöscht: Daten für Welt %s", worldId);
        } catch (IOException e) {
            logger.at(Level.WARNING).withCause(e).log("Konnte Daten nicht löschen für Welt %s", worldId);
        }
    }
}
