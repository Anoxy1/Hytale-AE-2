package com.tobi.mesystem.core;

import com.tobi.mesystem.util.BlockPos;

import java.util.*;

/**
 * ME Network - Kern des Storage-Systems
 * 
 * Basiert auf HyPipes PipeNetwork.java (dekompiliert)
 * Erweitert um:
 * - Digital Storage (HashMap statt physical containers)
 * - Channel System (8/32 channels)
 * - Device Tracking
 */
public class MENetwork {
    
    private final Map<BlockPos, MENode> nodes = new HashMap<>();
    private final Set<BlockPos> inputNodes = new HashSet<>();
    private final Set<BlockPos> outputNodes = new HashSet<>();
    private long tickCount = 0L;
    
    private final Map<String, Long> itemStorage = new HashMap<>();
    private int maxChannels = 8;
    private final Map<BlockPos, Integer> channelAllocation = new HashMap<>();
    private final Map<BlockPos, MEDeviceType> devices = new HashMap<>();
    private boolean hasController = false;
    private BlockPos controllerPos = null;
    
    public void addNode(MENode node) {
        nodes.put(node.getPosition(), node);
        node.setNetwork(this);
    }
    
    public MENode getNode(BlockPos position) {
        return nodes.get(position);
    }
    
    public void removeNode(BlockPos position) {
        MENode removed = nodes.remove(position);
        if (removed != null) {
            // Entferne ggf. registriertes Device und Channels
            unregisterDevice(position);
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
    
    public boolean storeItem(String itemId, long amount) {
        if (itemId == null || amount <= 0) {
            return false;
        }
        itemStorage.merge(itemId, amount, Long::sum);
        return true;
    }
    
    public long extractItem(String itemId, long requestedAmount) {
        if (itemId == null || requestedAmount <= 0) {
            return 0;
        }
        
        long available = itemStorage.getOrDefault(itemId, 0L);
        long toExtract = Math.min(available, requestedAmount);
        
        if (toExtract > 0) {
            itemStorage.merge(itemId, -toExtract, Long::sum);
            if (itemStorage.get(itemId) == 0) {
                itemStorage.remove(itemId);
            }
        }
        
        return toExtract;
    }
    
    public Map<String, Long> getAllItems() {
        return new HashMap<>(itemStorage);
    }
    
    public long getStoredAmount(String itemId) {
        return itemStorage.getOrDefault(itemId, 0L);
    }
    
    public int getItemTypeCount() {
        return itemStorage.size();
    }
    
    public long getTotalItemCount() {
        return itemStorage.values().stream()
            .mapToLong(Long::longValue)
            .sum();
    }
    
    public boolean allocateChannel(BlockPos device, int channelsNeeded) {
        if (channelsNeeded <= 0) {
            return true;
        }
        
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
        int used = channelAllocation.values().stream()
            .mapToInt(Integer::intValue)
            .sum();
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
    
    public void setController(BlockPos pos) {
        this.hasController = true;
        this.controllerPos = pos;
        this.maxChannels = 32;
    }
    
    public void addController(BlockPos pos) {
        setController(pos);
    }
    
    public void removeController() {
        this.hasController = false;
        this.controllerPos = null;
        this.maxChannels = 8;
        rebalanceChannels();
    }
    
    private void rebalanceChannels() {
        // TODO: Implementiere Priority-basiertes Rebalancing
    }
    
    public void registerDevice(BlockPos pos, MEDeviceType type) {
        devices.put(pos, type);
        int channelsNeeded = type.getChannelUsage();
        if (channelsNeeded > 0) {
            allocateChannel(pos, channelsNeeded);
        }
    }
    
    public void unregisterDevice(BlockPos pos) {
        devices.remove(pos);
        releaseChannel(pos);
    }
    
    public MEDeviceType getDeviceType(BlockPos pos) {
        return devices.get(pos);
    }
    
    public void merge(MENetwork other) {
        if (other == this) {
            return;
        }
        
        for (MENode node : other.nodes.values()) {
            addNode(node);
        }
        
        inputNodes.addAll(other.inputNodes);
        outputNodes.addAll(other.outputNodes);
        
        for (Map.Entry<String, Long> entry : other.itemStorage.entrySet()) {
            itemStorage.merge(entry.getKey(), entry.getValue(), Long::sum);
        }
        
        devices.putAll(other.devices);
        channelAllocation.putAll(other.channelAllocation);
        
        if (other.hasController && !this.hasController) {
            setController(other.controllerPos);
        }
    }
    
    public void tick() {
        tickCount++;
        for (MENode node : new ArrayList<>(nodes.values())) {
            node.tick();
        }
    }
    
    public long getTickCount() {
        return tickCount;
    }
    
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
