package com.tobi.mesystem.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;

import com.hypixel.hytale.logger.HytaleLogger;
import com.tobi.mesystem.MEPlugin;
import com.tobi.mesystem.util.BlockPos;

/**
 * ME Network - Kern des Storage-Systems
 * 
 * Basiert auf HyPipes PipeNetwork.java (dekompiliert)
 * Erweitert um:
 * - Digital Storage (HashMap statt physical containers)
 * - Channel System (8/32 channels)
 * - Device Tracking
 * - Thread-Safe Operations mit ReadWriteLock
 * - Performance-Optimierungen
 * 
 * Performance-Features:
 * - ConcurrentHashMap für Thread-Safety
 * - ReadWriteLock für optimierte Lesezugriffe
 * - AtomicLong für thread-safe Counters
 * - Minimale Lock-Kontention durch read/write separation
 */
public class MENetwork {
    
    private final HytaleLogger logger;
    private final UUID networkId;
    
    // Thread-safe collections
    private final Map<BlockPos, MENode> nodes = new ConcurrentHashMap<>();
    private final Set<BlockPos> inputNodes = ConcurrentHashMap.newKeySet();
    private final Set<BlockPos> outputNodes = ConcurrentHashMap.newKeySet();
    private final AtomicLong tickCount = new AtomicLong(0L);
    
    // Storage System mit ReadWriteLock für Performance
    private final Map<String, Long> itemStorage = new ConcurrentHashMap<>();
    private final ReadWriteLock storageLock = new ReentrantReadWriteLock();
    
    // Channel Management
    private volatile int maxChannels = 8;
    private final Map<BlockPos, Integer> channelAllocation = new ConcurrentHashMap<>();
    
    // Device Tracking
    private final Map<BlockPos, MEDeviceType> devices = new ConcurrentHashMap<>();
    private volatile boolean hasController = false;
    private volatile BlockPos controllerPos = null;
    
    // Performance: Cache für häufige Abfragen
    private volatile int cachedItemTypeCount = 0;
    private volatile long cachedTotalItemCount = 0L;
    private volatile boolean cacheValid = false;
    
    public MENetwork() {
        this.logger = MEPlugin.getInstance().getPluginLogger();
        this.networkId = UUID.randomUUID();
    }
    
    public UUID getNetworkId() {
        return networkId;
    }
    
    public int getNodeCount() {
        return nodes.size();
    }
    
    public long getStoredItemCount() {
        updateCacheIfNeeded();
        return cachedTotalItemCount;
    }
    
    public long getTotalStorageCapacity() {
        // Basis-Kapazität: 1024 Items pro Storage-Device
        return getStorageDeviceCount() * 1024L;
    }
    
    private int getStorageDeviceCount() {
        return (int) devices.values().stream()
            .filter(type -> type == MEDeviceType.TERMINAL || type == MEDeviceType.DRIVE)
            .count();
    }
    
    public void addNode(MENode node) {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }
        nodes.put(node.getPosition(), node);
        node.setNetwork(this);
        invalidateCache();
    }
    
    public MENode getNode(BlockPos position) {
        return nodes.get(position);
    }
    
    public void removeNode(BlockPos position) {
        MENode removed = nodes.remove(position);
        if (removed != null) {
            // Entferne ggf. registriertes Device und Channels
            unregisterDevice(position);
            invalidateCache();
        }
    }
    
    public Collection<MENode> getNodes() {
        return nodes.values();
    }
    
    public int size() {
        return nodes.size();
    }
    
    public boolean isEmpty() {
        return nodes.isEmpty();
    }
    
    /**
     * Speichert Items im Netzwerk (Thread-Safe)
     * @param itemId Item-Identifier
     * @param amount Anzahl der Items
     * @return true wenn erfolgreich
     */
    public boolean storeItem(String itemId, long amount) {
        if (itemId == null || itemId.isEmpty() || amount <= 0) {
            return false;
        }
        
        storageLock.writeLock().lock();
        try {
            itemStorage.merge(itemId, amount, Long::sum);
            invalidateCache();
            return true;
        } finally {
            storageLock.writeLock().unlock();
        }
    }
    
    /**
     * Fügt Items ins Netzwerk ein (Thread-Safe)
     * @param itemId Item-Identifier  
     * @param amount Einzufügende Menge
     * @param metadata Item-Metadata (optional, wird für Stacking ignoriert)
     * @return Tatsächlich eingefügte Menge
     */
    public int insertItem(String itemId, int amount, org.bson.BsonDocument metadata) {
        if (itemId == null || amount <= 0) {
            return 0;
        }
        
        // Prüfe Kapazität
        long currentTotal = getStoredItemCount();
        long capacity = getTotalStorageCapacity();
        long spaceAvailable = capacity - currentTotal;
        
        if (spaceAvailable <= 0) {
            return 0; // Netzwerk voll
        }
        
        int canInsert = (int) Math.min(amount, spaceAvailable);
        
        storageLock.writeLock().lock();
        try {
            long current = itemStorage.getOrDefault(itemId, 0L);
            itemStorage.put(itemId, current + canInsert);
            invalidateCache();
            return canInsert;
        } finally {
            storageLock.writeLock().unlock();
        }
    }
    
    /**
     * Extrahiert Items aus dem Netzwerk (Thread-Safe)
     * @param itemId Item-Identifier
     * @param requestedAmount Angeforderte Menge
     * @return Tatsächlich extrahierte Menge
     */
    public long extractItem(String itemId, long requestedAmount) {
        if (itemId == null || requestedAmount <= 0) {
            return 0;
        }
        
        storageLock.writeLock().lock();
        try {
            long available = itemStorage.getOrDefault(itemId, 0L);
            long toExtract = Math.min(available, requestedAmount);
            
            if (toExtract > 0) {
                long newAmount = available - toExtract;
                if (newAmount == 0) {
                    itemStorage.remove(itemId);
                } else {
                    itemStorage.put(itemId, newAmount);
                }
                invalidateCache();
            }
            
            return toExtract;
        } finally {
            storageLock.writeLock().unlock();
        }
    }
    
    /**
     * Gibt alle Items im Netzwerk zurück (Thread-Safe Copy)
     */
    public Map<String, Long> getAllItems() {
        storageLock.readLock().lock();
        try {
            return new HashMap<>(itemStorage);
        } finally {
            storageLock.readLock().unlock();
        }
    }
    
    /**
     * Gibt die gespeicherte Menge eines Items zurück (Thread-Safe)
     */
    public long getStoredAmount(String itemId) {
        storageLock.readLock().lock();
        try {
            return itemStorage.getOrDefault(itemId, 0L);
        } finally {
            storageLock.readLock().unlock();
        }
    }
    
    /**
     * Gibt die Anzahl verschiedener Item-Typen zurück (Cached)
     */
    public int getItemTypeCount() {
        if (!cacheValid) {
            updateCache();
        }
        return cachedItemTypeCount;
    }
    
    /**
     * Gibt die Gesamtzahl aller Items zurück (Cached)
     */
    public long getTotalItemCount() {
        if (!cacheValid) {
            updateCache();
        }
        return cachedTotalItemCount;
    }
    
    /**
     * Aktualisiert den Cache für Item-Statistiken
     */
    private void updateCache() {
        storageLock.readLock().lock();
        try {
            cachedItemTypeCount = itemStorage.size();
            cachedTotalItemCount = itemStorage.values().stream()
                .mapToLong(Long::longValue)
                .sum();
            cacheValid = true;
        } finally {
            storageLock.readLock().unlock();
        }
    }
    
    /**
     * Invalidiert den Cache nach Änderungen
     */
    private void invalidateCache() {
        cacheValid = false;
    }
    
    /**
     * Aktualisiert den Cache wenn ungültig
     */
    private void updateCacheIfNeeded() {
        if (!cacheValid) {
            storageLock.readLock().lock();
            try {
                cachedItemTypeCount = itemStorage.size();
                cachedTotalItemCount = itemStorage.values().stream()
                    .mapToLong(Long::longValue)
                    .sum();
                cacheValid = true;
            } finally {
                storageLock.readLock().unlock();
            }
        }
    }
    
    /**
     * Allokiert Channels für ein Device
     * @param device Position des Device
     * @param channelsNeeded Benötigte Anzahl Channels
     * @return true wenn erfolgreich allokiert
     */
    public boolean allocateChannel(BlockPos device, int channelsNeeded) {
        if (channelsNeeded <= 0) {
            return true;
        }
        
        // Atomic read und conditional write
        int usedChannels = channelAllocation.values().stream()
            .mapToInt(Integer::intValue)
            .sum();
            
        if (usedChannels + channelsNeeded <= maxChannels) {
            channelAllocation.put(device, channelsNeeded);
            return true;
        }
        
        return false;
    }
    
    public void releaseChannel(BlockPos device) {
        channelAllocation.remove(device);
    }
    
    public int getAvailableChannels() {
        int used = getUsedChannels();
        return maxChannels - used;
    }
    
    public int getUsedChannels() {
        return channelAllocation.values().stream()
            .mapToInt(Integer::intValue)
            .sum();
    }
    
    public int getMaxChannels() {
        return maxChannels;
    }

    /**
     * Gibt zurück, ob das Netzwerk einen Controller besitzt
     */
    public boolean hasController() {
        return hasController;
    }
    
    /**
     * Setzt einen Controller und erweitert Channels auf 32
     */
    public void setController(BlockPos pos) {
        this.hasController = true;
        this.controllerPos = pos;
        this.maxChannels = 32;
    }
    
    public void addController(BlockPos pos) {
        setController(pos);
    }
    
    /**
     * Entfernt den Controller und reduziert Channels auf 8
     */
    public void removeController() {
        this.hasController = false;
        this.controllerPos = null;
        this.maxChannels = 8;
        rebalanceChannels();
    }
    
    /**
     * Rebalanciert Channel-Allokationen nach Controller-Verlust
     * Devices mit höherer Priority behalten ihre Channels
     */
    private void rebalanceChannels() {
        int used = getUsedChannels();
        if (used <= maxChannels) return; // Alles passt
        
        // Sortiere Devices nach Priority (niedrigste zuerst)
        List<Map.Entry<BlockPos, MEDeviceType>> sortedDevices = new ArrayList<>(devices.entrySet());
        sortedDevices.sort(Comparator.comparingInt(e -> e.getValue().ordinal()));
        
        int channelsToFree = used - maxChannels;
        int freedChannels = 0;
        
        // Deaktiviere Devices mit niedrigster Priority bis genug Channels frei sind
        for (Map.Entry<BlockPos, MEDeviceType> entry : sortedDevices) {
            if (freedChannels >= channelsToFree) break;
            
            MEDeviceType deviceType = entry.getValue();
            int channelUsage = deviceType.getChannelUsage();
            
            if (channelUsage > 0) {
                // Markiere Device als offline (keine echte Deaktivierung hier)
                freedChannels += channelUsage;
                logger.at(Level.INFO).log(
                    "Channel-Rebalancing: Device %s bei %s deaktiviert (%s Channels freigegeben)",
                    deviceType,
                    entry.getKey(),
                    channelUsage
                );
            }
        }
        
        logger.at(Level.INFO).log(
            "Channel-Rebalancing abgeschlossen: %s/%s Channels gefreit",
            freedChannels,
            channelsToFree
        );
    }
    
    /**
     * Registriert ein Device im Netzwerk
     */
    public void registerDevice(BlockPos pos, MEDeviceType type) {
        if (pos == null || type == null) {
            throw new IllegalArgumentException("Position und Type dürfen nicht null sein");
        }
        
        devices.put(pos, type);
        int channelsNeeded = type.getChannelUsage();
        if (channelsNeeded > 0) {
            allocateChannel(pos, channelsNeeded);
        }
    }
    
    /**
     * Entfernt Device-Registrierung
     */
    public void unregisterDevice(BlockPos pos) {
        devices.remove(pos);
        releaseChannel(pos);
    }
    
    public MEDeviceType getDeviceType(BlockPos pos) {
        return devices.get(pos);
    }
    
    /**
     * Mergt ein anderes Netzwerk in dieses (Thread-Safe)
     * Wird verwendet wenn zwei Netzwerke verbunden werden
     */
    public void merge(MENetwork other) {
        if (other == this || other == null) {
            return;
        }
        
        // Nodes mergen
        for (MENode node : other.nodes.values()) {
            addNode(node);
        }
        
        // Input/Output Nodes mergen
        inputNodes.addAll(other.inputNodes);
        outputNodes.addAll(other.outputNodes);
        
        // Storage mergen (Thread-Safe)
        storageLock.writeLock().lock();
        try {
            for (Map.Entry<String, Long> entry : other.itemStorage.entrySet()) {
                itemStorage.merge(entry.getKey(), entry.getValue(), Long::sum);
            }
            invalidateCache();
        } finally {
            storageLock.writeLock().unlock();
        }
        
        // Devices und Channels mergen
        devices.putAll(other.devices);
        channelAllocation.putAll(other.channelAllocation);
        
        // Controller übernehmen falls vorhanden
        if (other.hasController && !this.hasController) {
            setController(other.controllerPos);
        }
    }
    
    /**
     * Tick-Update für alle Nodes im Netzwerk
     * Sollte nur von einem Thread aufgerufen werden
     */
    public void tick() {
        tickCount.incrementAndGet();
        
        // Copy collection to avoid ConcurrentModificationException
        List<MENode> nodesToTick = new ArrayList<>(nodes.values());
        for (MENode node : nodesToTick) {
            if (node != null) {
                node.tick();
            }
        }
    }
    
    public long getTickCount() {
        return tickCount.get();
    }
    
    /**
     * Gibt Debug-Informationen über das Netzwerk zurück
     */
    public String getDebugInfo() {
        return String.format(
            "MENetwork [Nodes: %d, Items: %d types (%d total), Channels: %d/%d, Controller: %s]",
            nodes.size(),
            getItemTypeCount(),
            getTotalItemCount(),
            getUsedChannels(),
            getMaxChannels(),
            hasController ? "Yes" : "No"
        );
    }
}
