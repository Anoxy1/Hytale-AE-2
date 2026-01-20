package com.tobi.mesystem.core;

import com.tobi.mesystem.util.BlockPos;
import com.tobi.mesystem.util.Direction;

import java.util.*;

/**
 * ME Network Node
 * 
 * Basiert auf HyPipes PipeNode.java (dekompiliert)
 * Erweitert um ME-spezifische Features
 */
public class MENode {
    
    private final UUID worldId;
    private final BlockPos position;
    private final Set<Direction> connections;
    private MENetwork network;
    private final MEDeviceType deviceType;
    private int priority = 0;
    private boolean active = true;
    private int ticksSinceLastActivity = 0;
    
    public MENode(UUID worldId, BlockPos position, MEDeviceType deviceType) {
        this.worldId = worldId;
        this.position = position;
        this.deviceType = deviceType;
        this.connections = EnumSet.noneOf(Direction.class);
    }
    
    public UUID getWorldId() {
        return worldId;
    }
    
    public BlockPos getPosition() {
        return position;
    }
    
    public MEDeviceType getDeviceType() {
        return deviceType;
    }
    
    public Set<Direction> getConnections() {
        return Collections.unmodifiableSet(connections);
    }
    
    public MENetwork getNetwork() {
        return network;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setNetwork(MENetwork network) {
        this.network = network;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public void addConnection(Direction direction) {
        connections.add(direction);
    }
    
    public void removeConnection(Direction direction) {
        connections.remove(direction);
    }
    
    public boolean hasConnection(Direction direction) {
        return connections.contains(direction);
    }
    
    public BlockPos[] getConnectedNeighbors() {
        return connections.stream()
            .map(position::offset)
            .toArray(BlockPos[]::new);
    }
    
    public int getConnectionCount() {
        return connections.size();
    }
    
    public void tick() {
        ticksSinceLastActivity++;
        
        if (!active) {
            return;
        }
        
        switch (deviceType) {
            case IMPORT_BUS -> tickImportBus();
            case EXPORT_BUS -> tickExportBus();
            case INTERFACE -> tickInterface();
            default -> {}
        }
    }
    
    private void tickImportBus() {
        // TODO: Implementiere Import Logic
    }
    
    private void tickExportBus() {
        // TODO: Implementiere Export Logic
    }
    
    private void tickInterface() {
        // TODO: Implementiere Interface Logic
    }
    
    public void onActivity() {
        ticksSinceLastActivity = 0;
    }
    
    public int getTicksSinceLastActivity() {
        return ticksSinceLastActivity;
    }
    
    public boolean needsChannel() {
        return deviceType.getChannelUsage() > 0;
    }
    
    public int getChannelUsage() {
        return deviceType.getChannelUsage();
    }
    
    public boolean hasChannel() {
        if (network == null) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return String.format(
            "MENode{pos=%s, type=%s, connections=%d, network=%s, active=%s}",
            position,
            deviceType,
            connections.size(),
            network != null ? "Yes" : "No",
            active
        );
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MENode other)) return false;
        return worldId.equals(other.worldId) && position.equals(other.position);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(worldId, position);
    }
}
