package com.tobi.mesystem.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.hypixel.hytale.logger.HytaleLogger;
import com.tobi.mesystem.MEPlugin;
import com.tobi.mesystem.config.MEConfig;

/**
 * Container & Inventory Utilities
 * 
 * Basiert auf ChestTerminal's InventoryUtils Pattern
 * 
 * Features:
 * - Container-Suche in Radius
 * - Item-Sammlung aus mehreren Containern
 * - Container-Validierung
 */
public class ContainerUtils {
    
    private static final HytaleLogger logger = MEPlugin.getInstance().getPluginLogger();
    
    /**
     * Sammelt alle Items aus Containern im konfigurierten Standardradius.
     * Nutzt den searchRadius-Wert aus der Config.
     * 
     * @param world World-Objekt
     * @param center Zentral-Position
     * @return Map von ItemID -> Anzahl
     */
    public static Map<String, Long> collectNearbyItems(Object world, BlockPos center) {
        return collectNearbyItems(world, center, 0); // 0 triggers config value
    }
    
    /**
     * Sammelt alle Items aus Containern in einem Radius
     * 
     * Basiert auf: ChestTerminal's collectNearbyItems()
     * 
     * @param world World-Objekt
     * @param center Zentral-Position
     * @param radius Suchradius in Blöcken (nutzt Config-Wert wenn <= 0)
     * @return Map von ItemID -> Anzahl
     */
    public static Map<String, Long> collectNearbyItems(Object world, BlockPos center, int radius) {
        // Use config value if no radius specified
        if (radius <= 0) {
            MEConfig config = MEPlugin.getInstance().getConfig();
            radius = config != null ? config.getSearchRadius() : 16;
        }
        Map<String, Long> items = new HashMap<>();
        
        if (world == null || center == null) {
            return items;
        }
        
        try {
            // Durchsuche alle Positionen im Radius
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        BlockPos pos = center.offset(dx, dy, dz);
                        
                        // Versuche Container an dieser Position zu finden
                        Object container = findContainerAt(world, pos);
                        
                        if (container != null) {
                            addItemsFromContainer(container, items);
                        }
                    }
                }
            }
            
            logger.at(Level.FINE).log(
                "Collected %d item types from radius %d around %s",
                items.size(), radius, center
            );
            
        } catch (Exception e) {
            logger.at(Level.WARNING).log(
                "Fehler beim Sammeln von Items: %s",
                e.getMessage()
            );
        }
        
        return items;
    }
    
    /**
     * Findet Container an einer bestimmten Position
     * 
     * @param world World-Objekt
     * @param pos Position zum Prüfen
     * @return ItemContainer oder null
     */
    private static Object findContainerAt(Object world, BlockPos pos) {
        if (world == null || pos == null) {
            return null;
        }
        
        try {
            // Versuche BlockEntity an dieser Position zu bekommen
            // ChestTerminal nutzt: world.getBlockEntity(x, y, z)
            Object blockEntity = world.getClass()
                .getMethod("getBlockEntity", int.class, int.class, int.class)
                .invoke(world, pos.getX(), pos.getY(), pos.getZ());
            
            if (blockEntity == null) {
                return null;
            }
            
            // Prüfe ob es ein ItemContainer ist
            if (blockEntity instanceof com.hypixel.hytale.server.core.inventory.container.ItemContainer) {
                return blockEntity;
            }
            
            // Versuche ItemContainer via getContainer() zu holen
            try {
                Object container = blockEntity.getClass()
                    .getMethod("getContainer")
                    .invoke(blockEntity);
                    
                if (container instanceof com.hypixel.hytale.server.core.inventory.container.ItemContainer) {
                    return container;
                }
            } catch (NoSuchMethodException ignored) {
                // Keine getContainer() Methode
            }
            
            // Versuche als ItemContainerState (wie ChestTerminalBlockState)
            try {
                Object containerState = blockEntity.getClass()
                    .getMethod("getItemContainer")
                    .invoke(blockEntity);
                    
                if (containerState instanceof com.hypixel.hytale.server.core.inventory.container.ItemContainer) {
                    return containerState;
                }
            } catch (NoSuchMethodException ignored) {
                // Keine getItemContainer() Methode
            }
            
        } catch (Exception e) {
            logger.at(Level.FINE).log(
                "Container-Suche bei %s fehlgeschlagen: %s",
                pos, e.getMessage()
            );
        }
        
        return null;
    }
    
    /**
     * Fügt Items aus einem Container zur Map hinzu
     * 
     * @param container ItemContainer
     * @param items Map zum Hinzufügen
     */
    private static void addItemsFromContainer(Object container, Map<String, Long> items) {
        try {
            var itemContainer = (com.hypixel.hytale.server.core.inventory.container.ItemContainer) container;
            
            // Durchlaufe alle Slots
            short capacity = itemContainer.getCapacity();
            for (short slot = 0; slot < capacity; slot++) {
                var itemStack = itemContainer.getItemStack(slot);
                
                if (itemStack != null && !itemStack.isEmpty()) {
                    String itemId = itemStack.getItem().getId();
                    int quantity = itemStack.getQuantity();
                    
                    // Addiere zu Map
                    items.merge(itemId, (long) quantity, Long::sum);
                }
            }
            
        } catch (Exception e) {
            logger.at(Level.FINE).log(
                "Fehler beim Lesen von Container: %s",
                e.getMessage()
            );
        }
    }
    
    /**
     * Prüft ob ein Block interagierbar ist (Container, etc.)
     * 
     * Basiert auf: ChestTerminal's isBlockInteractable()
     * 
     * @param world World-Objekt
     * @param pos Position zum Prüfen
     * @return true wenn interagierbar
     */
    public static boolean isBlockInteractable(Object world, BlockPos pos) {
        if (world == null || pos == null) {
            return false;
        }
        
        try {
            Object blockEntity = world.getClass()
                .getMethod("getBlockEntity", int.class, int.class, int.class)
                .invoke(world, pos.getX(), pos.getY(), pos.getZ());
            
            return blockEntity != null;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Findet alle Container in einem Radius
     * 
     * @param world World-Objekt  
     * @param center Zentral-Position
     * @param radius Suchradius
     * @return Liste von Container-Positionen
     */
    public static List<BlockPos> findNearbyContainers(Object world, BlockPos center, int radius) {
        List<BlockPos> containers = new ArrayList<>();
        
        if (world == null || center == null) {
            return containers;
        }
        
        try {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        BlockPos pos = center.offset(dx, dy, dz);
                        
                        if (findContainerAt(world, pos) != null) {
                            containers.add(pos);
                        }
                    }
                }
            }
            
            logger.at(Level.FINE).log(
                "Found %d containers in radius %d around %s",
                containers.size(), radius, center
            );
            
        } catch (Exception e) {
            logger.at(Level.WARNING).log(
                "Fehler beim Suchen von Containern: %s",
                e.getMessage()
            );
        }
        
        return containers;
    }
    
    /**
     * Extrahiert Items aus einem Container
     * 
     * @param container ItemContainer
     * @param itemId Item-ID zum Extrahieren
     * @param amount Anzahl zum Extrahieren
     * @return Tatsächlich extrahierte Anzahl
     */
    public static int extractFromContainer(Object container, String itemId, int amount) {
        if (container == null || itemId == null || amount <= 0) {
            return 0;
        }
        
        try {
            var itemContainer = (com.hypixel.hytale.server.core.inventory.container.ItemContainer) container;
            int extracted = 0;
            
            // Durchsuche alle Slots
            short capacity = itemContainer.getCapacity();
            for (short slot = 0; slot < capacity && extracted < amount; slot++) {
                var itemStack = itemContainer.getItemStack(slot);
                
                if (itemStack != null && !itemStack.isEmpty()) {
                    if (itemStack.getItem().getId().equals(itemId)) {
                        int toExtract = Math.min(itemStack.getQuantity(), amount - extracted);
                        
                        // Entferne Items
                        var toRemove = itemStack.withQuantity(toExtract);
                        var transaction = itemContainer.removeItemStack(toRemove);
                        
                        if (transaction.succeeded()) {
                            extracted += toExtract;
                        }
                    }
                }
            }
            
            return extracted;
            
        } catch (Exception e) {
            logger.at(Level.WARNING).log(
                "Fehler beim Extrahieren: %s",
                e.getMessage()
            );
            return 0;
        }
    }
}
